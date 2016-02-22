package models;

class OrderItem {
    public final Product product;
    public final int amount;

    public OrderItem(Product product, int amount) {
        this.product = product;
        this.amount = amount;
    }
}
