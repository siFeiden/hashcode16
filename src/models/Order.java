package models;

import java.util.*;
import java.util.function.BiFunction;

public class Order implements Location, Iterable<OrderItem> {
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

    private Order copyWithoutProducts() {
        return new Order(row, col, id);
    }

    public void addProduct(Product product) {
        products.merge(product, 1, (o, v) -> o + v);
    }

    public List<Order> splitForMaxTotalWeight(int maxWeight) { // TODO optimize order splitting (generate fewer orders)
        final List<Order> subOrders = new ArrayList<>();

        Order partial = this.copyWithoutProducts();
        for ( final OrderItem item : this ) {
            final int productWeight = item.product.getWeight();
            int productCount = item.amount;

            while ( productCount > 0 ) {
                if ( productWeight <= maxWeight - partial.totalWeight() ) {
                    partial.addProduct(item.product);
                    productCount--;
                } else {
                    subOrders.add(partial);
                    partial = this.copyWithoutProducts();
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

    public int totalWeight() {
        int weight = 0;
        for ( final OrderItem item : this ) {
            weight += item.product.getWeight() * item.amount;
        }

        return weight;
    }

    @Override
    public Iterator<OrderItem> iterator() {
        return new TransformMapIterator<>(
                products.entrySet().iterator(), OrderItem::new);
    }

    private static class TransformMapIterator<K, V, T> implements Iterator<T> {

        private final Iterator<Map.Entry<K, V>> iterator;
        private final BiFunction<K, V, T> transformer;

        private TransformMapIterator(Iterator<Map.Entry<K, V>> iterator, BiFunction<K, V, T> transformer) {
            Objects.requireNonNull(iterator);
            Objects.requireNonNull(transformer);

            this.iterator = iterator;
            this.transformer = transformer;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            final Map.Entry<K, V> next = iterator.next();
            return transformer.apply(next.getKey(), next.getValue());
        }
    }
}
