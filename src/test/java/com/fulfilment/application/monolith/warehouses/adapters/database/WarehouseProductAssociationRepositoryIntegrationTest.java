package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;
import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseProductAssociation;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@QuarkusTest
public class WarehouseProductAssociationRepositoryIntegrationTest {

    @Inject
    WarehouseProductAssociationRepository repository;

    @BeforeEach
    @TestTransaction
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void testCreateAndCountByStore() {
        // Create a new WarehouseProductAssociation
        WarehouseProductAssociation association = new WarehouseProductAssociation();
        StoreModel store = new StoreModel();
        store.quantityProductsInStock = 1;
        store.name = "Sample Store";
        association.store = store;

        ProductModel product = new ProductModel();
        product.name = "Sample Product";
        product.description = "Sample Description";
        product.stock = 1;
        product.price = BigDecimal.ONE;
        association.product = product;

        Warehouse warehouse = new Warehouse();
        warehouse.capacity = 1;
        warehouse.createdAt = LocalDateTime.now();
        warehouse.location = "TEST";
        warehouse.businessUnitCode = "A";
        warehouse.archivedAt = LocalDateTime.now();
        association.warehouse = warehouse;  // Assume this is the warehouse's ID

        repository.create(association);

        long count = repository.countByStore("Sample Store");
        Assertions.assertEquals(1L, count);
    }
}
