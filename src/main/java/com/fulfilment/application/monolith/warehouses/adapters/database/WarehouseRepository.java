package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

    @Override
    public List<Warehouse> getAll() {
        return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
    }

    @Override
    public void create(Warehouse warehouse) {
        DbWarehouse dbWarehouse = new DbWarehouse();
        dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
        dbWarehouse.location = warehouse.location;
        dbWarehouse.capacity = warehouse.capacity;
        dbWarehouse.stock = warehouse.stock;
        dbWarehouse.createdAt = warehouse.createdAt;
        dbWarehouse.archivedAt = warehouse.archivedAt;

        this.persist(dbWarehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
        DbWarehouse dbWarehouse = findByBusinessUnitCodeInternal(warehouse.businessUnitCode);

        if (dbWarehouse != null) {
            dbWarehouse.location = warehouse.location;
            dbWarehouse.capacity = warehouse.capacity;
            dbWarehouse.stock = warehouse.stock;
            dbWarehouse.archivedAt = warehouse.archivedAt;

            this.persist(dbWarehouse);
        } else {
            throw new IllegalArgumentException("Warehouse not found with business unit code: " + warehouse.businessUnitCode);
        }
    }

    @Override
    public void remove(Warehouse warehouse) {
        DbWarehouse dbWarehouse = findByBusinessUnitCodeInternal(warehouse.businessUnitCode);

        if (dbWarehouse != null) {
            this.delete(dbWarehouse);
        } else {
            throw new IllegalArgumentException("Warehouse not found with business unit code: " + warehouse.businessUnitCode);
        }
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
        DbWarehouse dbWarehouse = findByBusinessUnitCodeInternal(buCode);
        if (dbWarehouse != null) {
            return dbWarehouse.toWarehouse();
        } else {
            return null;
        }
    }

    @Override
    public Warehouse findByLongId(Long warehouseId) {
        Optional<DbWarehouse> dbWarehouse = this.findByIdOptional(warehouseId);

        return dbWarehouse.map(DbWarehouse::toWarehouse)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found with ID: " + warehouseId));
    }

    private DbWarehouse findByBusinessUnitCodeInternal(String buCode) {
        return this.find("businessUnitCode", buCode).firstResult();
    }
}
