package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.exceptions.LocationNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class CreateWarehouseUseCaseIntegrationTest {

    @Inject
    CreateWarehouseUseCase createWarehouseUseCase;

    @Inject
    WarehouseStore warehouseStore;

    @Inject
    LocationResolver locationResolver;

    private Warehouse warehouse;

    @BeforeEach
    void setUp() {
        // Initialize a sample warehouse
        warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU123";
        warehouse.location = "AMSTERDAM-001";
        warehouse.capacity = 100;
        warehouse.stock = 50;
    }


    @Test
    @TestTransaction
    public void testCreateWarehouseSuccess() {
        // Act
        createWarehouseUseCase.create(warehouse);

        // Assert: verify that the warehouse was created and stored in the database
        Warehouse persistedWarehouse = warehouseStore.findByBusinessUnitCode("BU123");
        assertEquals(warehouse.businessUnitCode, persistedWarehouse.businessUnitCode);
        assertEquals(warehouse.location, persistedWarehouse.location);
        assertEquals(warehouse.capacity, persistedWarehouse.capacity);
        assertEquals(warehouse.stock, persistedWarehouse.stock);
    }

    @Test
    @TestTransaction
    public void testCreateWarehouseWithExistingBusinessUnitCode() {
        // Arrange: Persist a warehouse with the same business unit code
        warehouseStore.create(warehouse);

        // Act & Assert: attempt to create another warehouse with the same code
        assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
    }

    @Test
    @TestTransaction
    public void testCreateWarehouseWithInvalidLocation() {
        // Arrange: set an invalid location identifier
        warehouse.location = "INVALID_LOC";

        // Act & Assert: expect an exception due to invalid location
        assertThrows(LocationNotFoundException.class, () -> createWarehouseUseCase.create(warehouse));
    }

    @Test
    @TestTransaction
    public void testCreateWarehouseExceedingMaxWarehouses() {
        // Act: create the first warehouse
        createWarehouseUseCase.create(warehouse);

        // Arrange: attempt to create another warehouse at the same location
        Warehouse anotherWarehouse = new Warehouse();
        anotherWarehouse.businessUnitCode = "BU124";
        anotherWarehouse.location = "AMSTERDAM-001";
        anotherWarehouse.capacity = 200;
        anotherWarehouse.stock = 50;

        // Act & Assert: expect an exception due to exceeding max warehouses
        assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(anotherWarehouse));
    }

    @Test
    @TestTransaction
    public void testCreateWarehouseExceedingMaxCapacity() {

        // Arrange: set warehouse capacity to exceed the location's max capacity
        warehouse.capacity = 150;

        // Act & Assert: expect an exception due to exceeding capacity
        assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
    }

    @Test
    @TestTransaction
    public void testCreateWarehouseWithStockExceedingCapacity() {
        // Arrange: set warehouse stock to exceed its own capacity
        warehouse.capacity = 100;
        warehouse.stock = 150;

        // Act & Assert: expect an exception due to stock exceeding capacity
        assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(warehouse));
    }
}
