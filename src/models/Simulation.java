package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
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
        dispatchOrders();
        return sendDrones();
    }


    public void dispatchOrders() {
        List<Order> partialOrders = new ArrayList<>();
        for (Order order : orders) {
            partialOrders.addAll(order.splitForMaxTotalWeight(maxLoad));
        }

        for (Order order : partialOrders) {
            Warehouse nearestWarehouse = findNearestCapableWarehouse(order);
            if ( nearestWarehouse != null ) {
                nearestWarehouse.addOrder(order);
            } else {
                // TODO split further, try again
            }
        }
    }

    public List<Command> sendDrones() {
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
                final Warehouse warehouse = findNearestWarehouseWithOrder(idle);
                if ( warehouse == null ) {
                    break;
                }

                actionCmds = idle.flyToWarehouse(warehouse);
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

    private Warehouse findNearestCapableWarehouse(Order order) {
        /* int nearestDistance = Integer.MAX_VALUE;
        Warehouse nearestWarehouse = null;

        for (Warehouse warehouse : warehouses) { // TODO improve: sort by distance, then check for capability
            final int currDistance = warehouse.distance(order);
            if ( currDistance < nearestDistance && warehouse.hasAllProducts(order) ) {
                nearestDistance = currDistance;
                nearestWarehouse = warehouse;
            }
        }

        return nearestWarehouse; */

        return findNearestWarehouseWithPredicate(order, w -> w.hasAllProducts(order));
    }

    private Warehouse findNearestWarehouseWithOrder(Location location) {
        return findNearestWarehouseWithPredicate(location, Warehouse::hasNextOrder);
    }

    private Warehouse findNearestWarehouseWithPredicate(Location location, Predicate<Warehouse> predicate) {
        int nearestDistance = Integer.MAX_VALUE;
        Warehouse nearestWarehouse = null;

        for (Warehouse warehouse : warehouses) { // TODO improve: sort by distance, then check for capability
            final int currDistance = warehouse.distance(location);
            if ( currDistance < nearestDistance && predicate.test(warehouse) ) {
                nearestDistance = currDistance;
                nearestWarehouse = warehouse;
            }
        }

        return nearestWarehouse;
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
        private int rows;
        private int cols;
        private int dronesCount;
        private int deadline;
        private int maxLoad;
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
