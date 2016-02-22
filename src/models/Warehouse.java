package models;

import java.util.*;

public class Warehouse implements Location {
    private final int row;
    private final int col;
    private final int id;

    private final Map<Product, Integer> products;

    private final Queue<Order> orders;

    public Warehouse(int row, int col, int id) {
        this.row = row;
        this.col = col;
        this.id = id;

        this.products = new HashMap<>();
        this.orders = new LinkedList<>();
    }

    public void addProduct(Product product, int amount) {
        products.merge(product, amount, (o, v) -> o + v);
    }

    public Order takeStockedProducts(Order order) { // TODO find better name
        final Order remainingProducts = order.copyWithoutProducts();
        final Order takenProducts = order.copyWithoutProducts();

        for ( final OrderItem item : order ) {
            final int stock = products.getOrDefault(item.product, 0);
            final int demand = item.amount;

            if ( demand < stock ) {
                takenProducts.addProduct(item.product, demand);
            } else { // demand >= stock
                takenProducts.addProduct(item.product, stock);
                remainingProducts.addProduct(item.product, demand - stock);
            }
        }

        this.addOrder(takenProducts);
        return remainingProducts;
    }

    private void addOrder(Order order) {
        orders.offer(order);
        for ( final OrderItem item : order ) {
            final int demand = item.amount;
            products.compute(item.product, (p, stock) -> stock - demand);
        }
    }

    public boolean hasNextOrder() {
        return !orders.isEmpty();
    }

    public Order popNextOrder() {
        return orders.poll();
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return col;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        Warehouse warehouse = (Warehouse) o;
        return id == warehouse.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
