package models;

@FunctionalInterface
public interface Command {

    String format();
}

class CommandFactory {

    public static Command load(int droneId, int warehouseId, int productId, int amount) {
        return () -> String.format("%d L %d %d %d", droneId, warehouseId, productId, amount);
    }

    public static Command deliver(int droneId, int orderId, int productId, int amount) {
        return () -> String.format("%d D %d %d %d", droneId, orderId, productId, amount);
    }

    public static Command wait(int droneId, int duration) {
        return () -> String.format("%d W %d", droneId, duration);
    }

}