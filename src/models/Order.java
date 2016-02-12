package models;

import java.util.*;

public class Order implements Location, Iterable<Map.Entry<Product, Integer>> {
    private final int row;
    private final int col;
    private final int id;

    private final Map<Product, Integer> products;

    public Order(int row, int col, int id) {
        this.row = row;
        this.col = col;
        this.id = id;

        this.products = new HashMap<>();
    }

    public void addProduct(Product product) {
        products.merge(product, 1, (o, v) -> o + v);
    }

    public int totalWeight() {
        int weight = 0;
        for (Map.Entry<Product, Integer> item : products.entrySet()) {
            weight += item.getKey().getWeight() * item.getValue();
        }

        return weight;
    }

    public List<Order> splitForMaxTotalWeight(int maxWeight) {
        final List<Order> subOrders = new ArrayList<>();

        Order partial = new Order(this.row, this.col, this.id);
        for ( Map.Entry<Product, Integer> item : products.entrySet() ) {
            final int productWeight = item.getKey().getWeight();
            int productCount = item.getValue();

            while ( productCount > 0 ) {
                if ( productWeight <= maxWeight - partial.totalWeight() ) {
                    partial.addProduct(item.getKey());
                    productCount--;
                } else {
                    subOrders.add(partial);
                    partial = new Order(this.row, this.col, this.id);
                }
            }
        }
        subOrders.add(partial);

        return subOrders;
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

    public int size() {
        return products.size();
    }

    @Override
    public Iterator<Map.Entry<Product, Integer>> iterator() {
        return products.entrySet().iterator();
    }
}
