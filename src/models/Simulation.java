package models;

import java.util.*;
import java.util.function.Predicate;

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

    public void sendDrones() {
        Comparator<Drone> sortByIdleTime = Comparator.comparingInt(Drone::getIdleTime);
        PriorityQueue<Drone> drones = new PriorityQueue<>(sortByIdleTime);

        Warehouse warehouse0 = warehouses[0];
        for ( int i = 0; i < dronesCount; i++ ) {
            final Drone drone = new Drone(warehouse0, i);
            drones.add(drone);
        }

        int simulationTime = 0;
        while ( simulationTime <= deadline ) {
            final Drone idle = drones.poll();
            int busyDuration; // flight and load/ delivery time

            if ( idle.shouldDeliver() ) { // drone has an order and should fly to the order's destination
                final Order order = idle.getOrder();

                busyDuration = idle.distance(order) + order.size();

                for ( Map.Entry<Product, Integer> item : order ) {
                    System.out.println(String.format("%d D %d %d %d",
                            idle.getId(), order.getId(), item.getKey().getId(), item.getValue()));
                }

                idle.setLocation(order);
                idle.setOrder(null);
            } else { // drone is at a delivery destination, find next warehouse and load products
                final Warehouse warehouse = findNearestWarehouseWithOrder(idle);
                final Order order = warehouse.popNextOrder();

                busyDuration = idle.distance(order) + order.size();

                for ( Map.Entry<Product, Integer> item : order ) {
                    System.out.println(String.format("%d L %d %d %d",
                            idle.getId(), warehouse.getId(), item.getKey().getId(), item.getValue()));
                }

                idle.setLocation(warehouse);
                idle.setOrder(order);
            }

            simulationTime = idle.getIdleTime();
            idle.pushIdleTime(busyDuration);
            drones.add(idle);
        }
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
