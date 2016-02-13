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

    public boolean hasAllProducts(Order order) {
        for (Map.Entry<Product, Integer> item : order) {
            final int stock = products.get(item.getKey());
            final int demand = item.getValue();

            if ( demand > stock ) {
                return false;
            }
        }

        return true;
    }

    public void addOrder(Order order) {
        orders.offer(order);
        for ( Map.Entry<Product, Integer> item : order ) {
            final Integer demand = item.getValue();
            products.compute(item.getKey(), (p, stock) -> stock - demand);
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
