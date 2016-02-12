package models;

public class Drone implements Location {
    final private int id;
    private int row;
    private int col;

    private int idleTime;
    private Warehouse warehouse;
    private Order order;


    public Drone(Warehouse initialWarehouse, int id) {
        this.row = initialWarehouse.getRow();
        this.col = initialWarehouse.getCol();
        this.id = id;
        this.idleTime = 0;
        this.warehouse = initialWarehouse;
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

    public void setLocation(int row, int col) {
        this.row = row;
        this.col = col;
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

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        this.order = null;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
        this.warehouse = null;
    }

    public boolean shouldDeliver() {
        return this.warehouse != null;
    }
}
