import models.Order;
import models.Product;
import models.Simulation;
import models.Warehouse;

import java.util.List;

class Parser {
    public Simulation parse(List<String> inputLines) {
        final Simulation.Builder builder = createSimulationWithParams(inputLines.get(0));

        final Product[] products = createProducts(inputLines.get(1), inputLines.get(2));

        final Warehouse[] warehouses = createWarehouses(inputLines.subList(3, inputLines.size()), products);

        List<String> orderLines = inputLines.subList(4 + 2 * warehouses.length, inputLines.size());
        final Order[] orders = createOrders(orderLines, products);

        builder.products(products);
        builder.warehouses(warehouses);
        builder.orders(orders);

        return builder.buildSimulation();
    }

    private Simulation.Builder createSimulationWithParams(String params) {
        String[] paramsSplit = params.split(" ");
        final int rows        = asInt(paramsSplit[0]);
        final int cols        = asInt(paramsSplit[1]);
        final int dronesCount = asInt(paramsSplit[2]);
        final int deadline    = asInt(paramsSplit[3]);
        final int maxLoad     = asInt(paramsSplit[4]);

        return new Simulation.Builder(rows, cols, dronesCount, deadline, maxLoad);
    }

    private Product[] createProducts(String prodCountLine, String prodWeightLine) {
        final int productTypesCount = asInt(prodCountLine);

        final String[] productsWeightSplit = prodWeightLine.split(" ");
        final Product[] products = new Product[productTypesCount];

        for ( int i = 0; i < productsWeightSplit.length; i++ ) {
            products[i] = new Product(asInt(productsWeightSplit[i]), i);
        }

        return products;
    }

    private Warehouse[] createWarehouses(List<String> lines, Product[] products) {
        final int warehouseCount = asInt(lines.get(0));
        final Warehouse[] warehouses = new Warehouse[warehouseCount];

        for ( int i = 0; i < warehouseCount; i++ ) {
            final String[] positionSplit = lines.get(2 * i + 1).split(" "); // position
            final Warehouse warehouse = new Warehouse(asInt(positionSplit[0]), asInt(positionSplit[1]), i);

            final String[] productCountsSplit = lines.get(2 * i + 2).split(" ");
            for ( int j = 0; j < products.length; j++ ) {
                warehouse.addProduct(products[j], asInt(productCountsSplit[j]));
            }

            warehouses[i] = warehouse;
        }

        return warehouses;
    }

    private Order[] createOrders(List<String> lines, Product[] products) {
        final int orderCount = asInt(lines.get(0));
        final Order[] orders = new Order[orderCount];

        for ( int i = 0; i < orderCount; i++ ) {
            final String[] destinationSplit = lines.get(3 * i + 1).split(" "); // position
            final Order order = new Order(asInt(destinationSplit[0]), asInt(destinationSplit[1]), i);

            final int prodCount = asInt(lines.get(3 * i + 2));
            final String[] prodTypesSplit = lines.get(3 * i + 3).split(" ");
            for ( int j = 0; j < prodCount; j++ ) {
                final int productType = asInt(prodTypesSplit[j]);
                order.addProduct(products[productType]);
            }

            orders[i] = order;
        }

        return orders;
    }

    private int asInt(String s) {
        return Integer.parseInt(s);
    }
}
