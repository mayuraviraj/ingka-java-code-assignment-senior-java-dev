package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;


    public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    @Override
    @Transactional
    public void create(Warehouse warehouse) {
        // Check if warehouse with the same business unit code already exists
        if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
            throw new IllegalArgumentException("Warehouse with the same business unit code already exists.");
        }

        // Validate location
        Location location = locationResolver.resolveByIdentifier(warehouse.location);
        if (location == null) {
            throw new IllegalArgumentException("Invalid location.");
        }

        // Validate warehouse creation feasibility
        long existingWarehousesCount = warehouseStore.getAll().stream()
                .filter(w -> w.location.equals(warehouse.location))
                .count();

        if (existingWarehousesCount >= location.maxNumberOfWarehouses) {
            throw new IllegalArgumentException("Maximum number of warehouses at this location " + location + "reached.");
        }

        // Validate capacity and stock
        if (warehouse.capacity > location.maxCapacity) {
            throw new IllegalArgumentException("Warehouse capacity exceeds maximum capacity for the location.");
        }

        if (warehouse.stock > warehouse.capacity) {
            throw new IllegalArgumentException("Warehouse stock exceeds its capacity.");
        }

        // if all went well, create the warehouse
        warehouseStore.create(warehouse);
    }
}
