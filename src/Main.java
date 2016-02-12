//import models.Drone;
import models.Simulation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        if ( args.length < 1 ) {
            System.out.println("Need input file");
            return;
        }

        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(args[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parser p = new Parser();
        Simulation sim = p.parse(lines);
        System.out.println(sim.toString());

        //simulate(sim);
    }


/*
    public static void simulate(Simulation simulation) {
        long timeStep = 0;

        Comparator<Drone> droneComparator = (d1, d2) -> d1.idleTime - d2.idleTime;
        Comparator.comparingInt(Drone::getIdleTime);
        PriorityQueue<Drone> drones = new PriorityQueue<>(droneComparator);

        final Warehouse warehouse0 = simulation.warehouses.get(0);
        for (int i = 0; i < simulation.dronesCount; i++) {
            drones.add(new Drone(i, warehouse0.row, warehouse0.col, 0, warehouse0));
        }


        while ( timeStep <= simulation.deadLine ) {
            Drone idle = drones.poll();

            if ( idle == null ) {
                break;
            }

            if ( idle.order == null ) { // at warehouse
                Order order = idle.warehouse.getNextOrder();

                int loadCount = 0
                for (int i = 0; i < simulation.prodCount; i++) {
                    if ( order.products != null && i < order.products.length && order.products[i] > 0 ) {
                        System.out.println(String.format("%d L %d %d %d", idle.id, idle.warehouse.id, i, order.products[i]));
                        loadCount++;
                    }
                }

                for (int i = 0; i < simulation.prodCount; i++) {
                    if ( order.products != null && i < order.products.length && order.products[i] > 0 ) {
                        System.out.println(String.format("%d D %d %d %d", idle.id, order.id, i, order.products[i]));
                        loadCount++;
                    }
                }

                Warehouse wh = idle.warehouse;
                double dist = Math.ceil(Math.hypot(wh.row - idle.row, wh.col - idle.col));
                dist += Math.ceil(Math.hypot(wh.row - order.row, wh.col - order.col));
                int flightTime = (int) dist + loadCount);
                idle.idleTime += flightTime;
                idle.order = order;
                idle.warehouse = null;
            } else {
                final int idleRow = idle.row;
                final int idleCol = idle.col;

                double dist = Double.MIN_VALUE;
                Warehouse next = null;
                for (Warehouse wh : simulation.warehouses) {
                    double d = Math.hypot(idleRow - wh.row, idleCol - wh.col);
                    if ( d < dist && wh.hasNextOrder() ) {
                        dist = d;
                        next = wh;
                    }
                }
                if ( next != null ) {
                    break;
                }

                idle.warehouse = next;
                idle.order = null;
            }

            drones.add(idle);
        }
    }*/
}
