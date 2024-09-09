package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ProductRepositoryIntegrationTest {

    @Inject
    ProductRepository productRepository;

    @Test
    @TestTransaction
    public void testFindByName_Success() {
        Product product = new Product();
        product.name = "TEST_NAME_1";
        product.description = "Description 1";
        productRepository.persist(product);
        // Act: Retrieve the first product by its ID
        ProductModel productModel = productRepository.findByName(product.name);

        // Assert: Verify the retrieved product matches the expected values
        assertEquals("TEST_NAME_1", productModel.name);
    }

    @Test
    @TestTransaction
    public void testFindByName_NotFound() {
        String name = UUID.randomUUID().toString();

        // Act & Assert: Verify that an exception is thrown when a product with a non-existent ID is requested
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productRepository.findByName(name);
        });
        assertEquals("Product not found with name: " + name, exception.getMessage());
    }
}
