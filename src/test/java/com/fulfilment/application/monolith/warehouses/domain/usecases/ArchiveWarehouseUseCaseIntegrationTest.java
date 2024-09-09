package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class ArchiveWarehouseUseCaseIntegrationTest {

    @Inject
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @Inject
    WarehouseStore warehouseStore;


    @Transactional
    Warehouse createWarehouse(String busCode) {
        // Initialize an existing warehouse
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = busCode;
        warehouse.location = "AMSTERDAM-001";
        warehouse.capacity = 100;
        warehouse.stock = 50;
        warehouse.archivedAt = null; // Not archived
        warehouseStore.create(warehouse);
        return warehouse;
    }


    @Test
    @TestTransaction
    public void testArchiveWarehouseSuccess() {
        Warehouse warehouse = createWarehouse("ADD001");
        // Act
        archiveWarehouseUseCase.archive(warehouse);

        // Assert: verify that the warehouse is archived correctly
        Warehouse persistedWarehouse = warehouseStore.findByBusinessUnitCode("ADD001");
        assertNotNull(persistedWarehouse.archivedAt, "The warehouse should be archived.");
    }

    @Test
    @TestTransaction
    public void testArchiveNonExistentWarehouse() {
        // Arrange: create a new warehouse object with a non-existent business unit code
        Warehouse nonExistentWarehouse = new Warehouse();
        nonExistentWarehouse.businessUnitCode = "NON_EXISTENT";
        nonExistentWarehouse.location = "AMSTERDAM-001";
        nonExistentWarehouse.capacity = 100;
        nonExistentWarehouse.stock = 50;

        // Act & Assert: attempt to archive a non-existent warehouse
        assertThrows(IllegalArgumentException.class, () -> archiveWarehouseUseCase.archive(nonExistentWarehouse), "Warehouse to be archived does not exist.");
    }

    @Test
    @TestTransaction
    public void testArchiveAlreadyArchivedWarehouse() {
        Warehouse warehouse = createWarehouse("ADD001");
        // Arrange: archive the existing warehouse first
        warehouse.archivedAt = LocalDateTime.now();
        warehouseStore.update(warehouse);

        // Act & Assert: attempt to archive the already archived warehouse
        assertThrows(IllegalArgumentException.class, () -> archiveWarehouseUseCase.archive(warehouse), "Warehouse already archived.");
    }
}
