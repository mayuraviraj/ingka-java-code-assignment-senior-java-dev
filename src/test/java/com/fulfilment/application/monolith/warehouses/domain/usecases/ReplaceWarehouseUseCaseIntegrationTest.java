package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ReplaceWarehouseUseCaseIntegrationTest {

    @Inject
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Inject
    WarehouseStore warehouseStore;

    @Inject
    LocationResolver locationResolver;

    private Warehouse existingWarehouse;

    void createWarehouse(String busCode, String location) {
        // Initialize an existing warehouse
        existingWarehouse = new Warehouse();
        existingWarehouse.businessUnitCode = busCode;
        existingWarehouse.location = location;
        existingWarehouse.capacity = 100;
        existingWarehouse.stock = 50;
        warehouseStore.create(existingWarehouse);
    }

    @Test
    @TestTransaction
    public void testReplaceWarehouseSuccess() {
        createWarehouse("BU123",  "AMSTERDAM-001");
        // Arrange: create a new warehouse object to replace the existing one
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "BU123";
        newWarehouse.location = "AMSTERDAM-001";
        newWarehouse.capacity = 150; // New capacity is greater to accommodate existing stock
        newWarehouse.stock = 50;     // Matching existing stock

        // Act
        replaceWarehouseUseCase.replace(newWarehouse);

        // Assert: verify that the warehouse was replaced correctly in the database
        Warehouse persistedWarehouse = warehouseStore.findByBusinessUnitCode("BU123");
        assertEquals(newWarehouse.businessUnitCode, persistedWarehouse.businessUnitCode);
        assertEquals(newWarehouse.location, persistedWarehouse.location);
        assertEquals(newWarehouse.capacity, persistedWarehouse.capacity);
        assertEquals(newWarehouse.stock, persistedWarehouse.stock);
    }

    @Test
    @TestTransaction
    public void testReplaceWarehouseWithNonExistentWarehouse() {
        // Arrange: create a new warehouse with a different business unit code
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "NON_EXISTENT";
        newWarehouse.location = "AMSTERDAM-001";
        newWarehouse.capacity = 150;
        newWarehouse.stock = 50;

        // Act & Assert: attempt to replace a non-existent warehouse
        assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    @TestTransaction
    public void testReplaceWarehouseWithInvalidLocation() {
        createWarehouse("BU1234",  "AMSTERDAM-001");
        // Arrange: set an invalid location identifier
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "BU1234";
        newWarehouse.location = "INVALID_LOC";
        newWarehouse.capacity = 150;
        newWarehouse.stock = 50;

        // Act & Assert: expect an exception due to invalid location
        assertThrows(LocationNotFoundException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    @TestTransaction
    public void testReplaceWarehouseWithInsufficientCapacity() {
        createWarehouse("BU1237",  "AMSTERDAM-001");
        // Arrange: set new warehouse capacity less than existing stock
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "BU1237";
        newWarehouse.location = "AMSTERDAM-001";
        newWarehouse.capacity = 40;  // Less than existing stock of 50
        newWarehouse.stock = 50;

        // Act & Assert: expect an exception due to insufficient capacity
        assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }

    @Test
    @TestTransaction
    public void testReplaceWarehouseWithMismatchedStock() {
        createWarehouse("ADB123",  "AMSTERDAM-001");
        // Arrange: set new warehouse stock different from existing stock
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "ADB123";
        newWarehouse.location = "AMSTERDAM-001";
        newWarehouse.capacity = 150;
        newWarehouse.stock = 60;  // Different from existing stock of 50

        // Act & Assert: expect an exception due to stock mismatch
        assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(newWarehouse));
    }
}
