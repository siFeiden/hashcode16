package models;

public interface Command {

    String format();

    static Command load(int droneId, int warehouseId, int productId, int amount) {
        return () -> String.format("%d L %d %d %d", droneId, warehouseId, productId, amount);
    }

    static Command deliver(int droneId, int orderId, int productId, int amount) {
        return () -> String.format("%d D %d %d %d", droneId, orderId, productId, amount);
    }
}