package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Drone implements Location {
    private final int id;
    private int row;
    private int col;

    private int idleTime;
    private Order order;


    public Drone(Warehouse initialWarehouse, int id) {
        this.row = initialWarehouse.getRow();
        this.col = initialWarehouse.getCol();
        this.id = id;
        this.idleTime = 0;
        this.order = null;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return col;
    }

    private void setLocation(Location location) {
        this.row = location.getRow();
        this.col = location.getCol();
    }

    public int getId() {
        return id;
    }

    public int getIdleTime() {
        return idleTime;
    }

    private void pushIdleTime(int delay) {
        this.idleTime += delay;
    }

    public boolean hasOrder() {
        return this.order != null;
    }

    public List<Command> deliver() {
        if ( order == null ) {
            return new ArrayList<>();
        }

        final int duration = this.distance(order) + order.size();
        this.pushIdleTime(duration);

        final ArrayList<Command> commands = new ArrayList<>();
        for ( Map.Entry<Product, Integer> item : order ) {
            commands.add(CommandFactory.deliver(
                    getId(), order.getId(), item.getKey().getId(), item.getValue()));
        }

        setLocation(order);
        order = null;

        return commands;
    }

    public List<Command> flyToWarehouse(Warehouse warehouse) {
        this.order = warehouse.popNextOrder();

        final int duration = this.distance(order) + order.size();
        this.pushIdleTime(duration);

        final ArrayList<Command> commands = new ArrayList<>();
        for ( Map.Entry<Product, Integer> item : order ) {
            commands.add(CommandFactory.load(
                    getId(), warehouse.getId(), item.getKey().getId(), item.getValue()));
        }

        setLocation(order);

        return commands;
    }
}
