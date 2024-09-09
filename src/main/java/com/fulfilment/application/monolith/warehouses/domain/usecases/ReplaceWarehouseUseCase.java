package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    @Transactional
    public void replace(Warehouse newWarehouse) {
        // Check if the warehouse with the specified business unit code exists
        Warehouse existingWarehouse = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
        if (existingWarehouse == null) {
            throw new IllegalArgumentException("Warehouse to be replaced does not exist.");
        }

        // Validate location
        Location location = locationResolver.resolveByIdentifier(newWarehouse.location);
        if (location == null) {
            throw new IllegalArgumentException("Invalid location.");
        }

        // Validate capacity accommodation
        if (newWarehouse.capacity < existingWarehouse.stock) {
            throw new IllegalArgumentException("New warehouse capacity cannot accommodate existing stock.");
        }

        // Validate stock matching
        if (!newWarehouse.stock.equals(existingWarehouse.stock)) {
            throw new IllegalArgumentException("Stock of the new warehouse must match the stock of the warehouse being replaced.");
        }

        warehouseStore.update(newWarehouse);
    }
}
