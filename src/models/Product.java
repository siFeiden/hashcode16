package models;

public class Product {
    private final int weight;
    private final int id;

    public Product(int weight, int id) {

        this.weight = weight;
        this.id = id;
    }

    public int getWeight() {
        return weight;
    }

    public int getId() {
        return id;
    }
}
