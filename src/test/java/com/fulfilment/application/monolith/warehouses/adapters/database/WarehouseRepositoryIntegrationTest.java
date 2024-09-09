package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class WarehouseRepositoryIntegrationTest {

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    @TestTransaction
    public void testCreateWarehouse() {
        // Arrange
        Warehouse warehouse = createSampleWarehouse("BU001");

        // Act
        warehouseRepository.create(warehouse);

        // Assert
        Warehouse foundWarehouse = warehouseRepository.findByBusinessUnitCode("BU001");
        assertNotNull(foundWarehouse);
        assertEquals("BU001", foundWarehouse.businessUnitCode);
        assertEquals(1000, foundWarehouse.capacity);
    }

    @Test
    @TestTransaction
    public void testGetAllWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.getAll();

        // Assert
        assertFalse(warehouses.isEmpty());
    }

    @Test
    @TestTransaction
    public void testFindByBusinessUnitCode() {
        // Arrange
        warehouseRepository.create(createSampleWarehouse("BU004"));

        // Act
        Warehouse foundWarehouse = warehouseRepository.findByBusinessUnitCode("BU004");

        // Assert
        assertNotNull(foundWarehouse);
        assertEquals("BU004", foundWarehouse.businessUnitCode);
    }

    @Test
    @TestTransaction
    public void testUpdateWarehouse() {
        // Arrange
        Warehouse warehouse = createSampleWarehouse("BU005");
        warehouseRepository.create(warehouse);

        // Act
        warehouse.capacity = 1500;
        warehouseRepository.update(warehouse);

        // Assert
        Warehouse updatedWarehouse = warehouseRepository.findByBusinessUnitCode("BU005");
        assertEquals(1500, updatedWarehouse.capacity);
    }

    @Test
    @TestTransaction
    public void testRemoveWarehouse() {
        // Arrange
        Warehouse warehouse = createSampleWarehouse("BU006");
        warehouseRepository.create(warehouse);

        // Act
        warehouseRepository.remove(warehouse);

        // Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                warehouseRepository.remove(warehouse));
        assertEquals("Warehouse not found with business unit code: BU006", exception.getMessage());
    }

    @Test
    @TestTransaction
    public void testFindByLongId() {
        Warehouse foundById = warehouseRepository.findByLongId(1L);
        assertNotNull(foundById);
        assertEquals("MWH.001", foundById.businessUnitCode);
    }

    private Warehouse createSampleWarehouse(String businessUnitCode) {
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = businessUnitCode;
        warehouse.location = "Test Location";
        warehouse.capacity = 1000;
        warehouse.stock = 500;
        warehouse.createdAt = LocalDateTime.now();
        warehouse.archivedAt = null;
        return warehouse;
    }
}
