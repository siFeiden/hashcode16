package models;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class OrderTest {

    @Test
    public void testAddProduct() throws Exception {
        Product p1 = new Product(300, 1);
        Product p2 = new Product(400, 2);

        Order testOrder = new Order(0, 0, 0);
        testOrder.addProduct(p1);
        testOrder.addProduct(p2);
        testOrder.addProduct(p1);

        Assert.assertEquals(2, testOrder.size());

        for ( final OrderItem item : testOrder ) {
            if ( item.product.equals(p1) ) {
                Assert.assertEquals(2, item.amount);
            }
            if ( item.product.equals(p2) ) {
                Assert.assertEquals(1, item.amount);
            }
        }
    }

    @Test
    public void testTotalWeight() throws Exception {
        Product p1 = new Product(300, 1);
        Product p2 = new Product(400, 2);

        Order testOrder = new Order(0, 0, 0);
        testOrder.addProduct(p1);
        testOrder.addProduct(p2);
        testOrder.addProduct(p1);

        Assert.assertEquals(300 + 400 + 300, testOrder.totalWeight());
    }

    @Test
    public void testSplitForMaxTotalWeight() throws Exception {
        Product p1 = new Product(300, 1);
        Product p2 = new Product(400, 2);

        Order testOrder = new Order(0, 0, 0);
        testOrder.addProduct(p1);
        testOrder.addProduct(p2);
        testOrder.addProduct(p1);

        final List<Order> noChange = testOrder.splitForMaxTotalWeight(1000);
        Assert.assertEquals(1, noChange.size());
        Assert.assertEquals(1000, noChange.get(0).totalWeight());

        final List<Order> splitOrders = testOrder.splitForMaxTotalWeight(700);
        for ( Order splitOrder : splitOrders ) {
            Assert.assertTrue(splitOrder.totalWeight() <= 700);
        }
    }
}