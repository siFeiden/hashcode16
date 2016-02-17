package models;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Simulation {
    private final int rows;
    private final int cols;
    private final int dronesCount;
    private final int deadline;
    private final int maxLoad;
    private final Product[] products;
    private final Warehouse[] warehouses;
    private final Order[] orders;

    private Simulation(int rows, int cols, int dronesCount, int deadline, int maxLoad,
                      Product[] products, Warehouse[] warehouses, Order[] orders) {
        this.rows = rows;
        this.cols = cols;
        this.dronesCount = dronesCount;
        this.deadline = deadline;
        this.maxLoad = maxLoad;
        this.products = products;
        this.warehouses = warehouses;
        this.orders = orders;
    }

    public List<Command> simulate() {
        assignOrdersToWarehouses();
        return sendDrones();
    }


    private void assignOrdersToWarehouses() {
        final List<Order> partialOrders = new ArrayList<>();
        for ( final Order order : orders ) {
            partialOrders.addAll(order.splitForMaxTotalWeight(maxLoad));
        }

        for (final Order order : partialOrders) {
            final Optional<Warehouse> nearestWarehouse = findNearestCapableWarehouse(order);
            if ( nearestWarehouse.isPresent() ) {
                // nearestWarehouse.ifPresent(wh -> wh.addOrder(order));
                nearestWarehouse.get().addOrder(order);
            } else { // found no warehouse with sufficient stock for this order
                // TODO split order and try again
            }
        }
    }

    private List<Command> sendDrones() {
        final Comparator<Drone> sortByIdleTime = Comparator.comparingInt(Drone::getIdleTime);
        final PriorityQueue<Drone> drones = new PriorityQueue<>(sortByIdleTime);

        final Warehouse warehouse0 = warehouses[0];
        for ( int i = 0; i < dronesCount; i++ ) {
            final Drone drone = new Drone(warehouse0, i);
            drones.add(drone);
        }

        final List<Command> commands = new ArrayList<>();
        int simulationTime = 0;

        while ( simulationTime <= deadline ) {
            final Drone idle = drones.poll();

            simulationTime = idle.getIdleTime();
            final List<Command> actionCmds;

            if ( idle.hasOrder() ) { // drone has an order and should fly to the order's destination
                actionCmds = idle.deliver();
            } else { // drone is at a delivery destination, find next warehouse and load products
                final Optional<Warehouse> warehouse = findNearestWarehouseWithOrder(idle);
                if ( warehouse.isPresent() ) {
                    actionCmds = idle.flyToWarehouse(warehouse.get());
                } else { // no more orders to be processed, stop simulating
                    break;
                }
            }

            commands.addAll(actionCmds);
            drones.add(idle);
        }

        final List<Command> remainingCommands = drones.stream()
                .map(Drone::deliver)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        commands.addAll(remainingCommands);

        return commands;
    }

    private Optional<Warehouse> findNearestCapableWarehouse(Order order) {
        return findNearestWarehouseWithPredicate(order, w -> w.hasAllProducts(order));
    }

    private Optional<Warehouse> findNearestWarehouseWithOrder(Location location) {
        return findNearestWarehouseWithPredicate(location, Warehouse::hasNextOrder);
    }

    private Optional<Warehouse> findNearestWarehouseWithPredicate(Location location, Predicate<Warehouse> predicate) {
        int nearestDistance = Integer.MAX_VALUE;
        Warehouse nearestWarehouse = null;

        for ( final Warehouse warehouse : warehouses ) {
            final int currDistance = warehouse.distance(location);
            if ( currDistance < nearestDistance && predicate.test(warehouse) ) {
                nearestDistance = currDistance;
                nearestWarehouse = warehouse;
            }
        }

        return Optional.ofNullable(nearestWarehouse);
    }

    @Override
    public String toString() {
        return "Simulation{" +
                "rows=" + rows +
                ", cols=" + cols +
                ", dronesCount=" + dronesCount +
                ", deadline=" + deadline +
                ", maxLoad=" + maxLoad +
                ", products=" + products.length +
                ", warehouses=" + warehouses.length +
                ", orders=" + orders.length +
                '}';
    }

    public static class Builder {
        private final int rows;
        private final int cols;
        private final int dronesCount;
        private final int deadline;
        private final int maxLoad;
        private Product[] products;
        private Warehouse[] warehouses;
        private Order[] orders;

        public Builder(int rows, int cols, int dronesCount, int deadline, int maxLoad) {
            this.rows = rows;
            this.cols = cols;
            this.dronesCount = dronesCount;
            this.deadline = deadline;
            this.maxLoad = maxLoad;
        }

        public void products(Product[] products) {
            this.products = products;
        }

        public void warehouses(Warehouse[] warehouses) {
            this.warehouses = warehouses;
        }

        public void orders(Order[] orders) {
            this.orders = orders;
        }

        public Simulation buildSimulation() {
            return new Simulation(rows, cols, dronesCount, deadline, maxLoad, products, warehouses, orders);
        }
    }
}
