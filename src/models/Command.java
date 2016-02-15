package models;

public final class Command {
    private final int droneId;
    private final String command;
    private final int warehouseId;
    private final int productId;
    private final int amount;

    private Command(int droneId, String command, int warehouseId, int productId, int amount) {
        this.droneId = droneId;
        this.command = command;
        this.warehouseId = warehouseId;
        this.productId = productId;
        this.amount = amount;
    }

    public String format() {
        return String.format("%d %s %d %d %d", droneId, command, warehouseId, productId, amount);
    }


    public static Command load(int droneId, int warehouseId, int productId, int amount) {
        return new Command(droneId, "L", warehouseId, productId, amount);
    }

    public static Command deliver(int droneId, int orderId, int productId, int amount) {
        return new Command(droneId, "D", orderId, productId, amount);
    }
}