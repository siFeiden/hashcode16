package models;

public class Drone implements Location {
    final private int id;
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

    public void setLocation(Location location) {
        this.row = location.getRow();
        this.col = location.getCol();
    }

    public int getId() {
        return id;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void pushIdleTime(int delay) {
        this.idleTime += delay;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public boolean shouldDeliver() {
        return this.order != null;
    }
}
