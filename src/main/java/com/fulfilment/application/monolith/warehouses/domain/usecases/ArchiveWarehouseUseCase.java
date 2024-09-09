package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

    private final WarehouseStore warehouseStore;

    public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
        this.warehouseStore = warehouseStore;
    }

    @Override
    @Transactional
    public void archive(Warehouse warehouse) {
        Warehouse existingWarehouse = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
        if (existingWarehouse == null) {
            throw new IllegalArgumentException("Warehouse to be archived does not exist.");
        }
        if (existingWarehouse.archivedAt != null) {
            throw new IllegalArgumentException("Warehouse already archived.");
        }

        existingWarehouse.archivedAt = LocalDateTime.now();
        warehouseStore.update(existingWarehouse);
    }
}
