package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.WebApplicationException;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

    private static final Logger LOGGER = Logger.getLogger(WarehouseResourceImpl.class.getName());

    @Inject
    CreateWarehouseUseCase createWarehouseUseCase;
    @Inject
    ReplaceWarehouseUseCase replaceWarehouseUseCase;
    @Inject
    ArchiveWarehouseUseCase archiveWarehouseUseCase;
    @Inject
    private WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> listAllWarehousesUnits() {
        return warehouseRepository.getAll().stream()
                .filter(warehouse -> warehouse.archivedAt == null) // TODO : This is active warehouse list. May be we
                // TODO : need new API to get archived list . This something i would clarify for final solution.
                .map(this::toWarehouseResponse).toList();
    }

    @Override
    public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = toWarehouseModel(data);
        createWarehouseUseCase.create(warehouse);
        LOGGER.debug("Created new warehouse: " + warehouse);
        return data;
    }

    @Override
    public Warehouse getAWarehouseUnitByID(String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null || warehouse.archivedAt != null) {
            // TODO : If archived then consider as its not available. I would verify this.
            throw new WebApplicationException("Warehouse with ID " + id + " not found.", 404);
        }
        return toWarehouseResponse(warehouse);
    }

    // TODO: This is confusing. This API is delete but method name indicate that instead of delete we should
    // TODO: archive this. This means actual warehouse won't be delete but archived adding archivedAt date.
    @Override
    public void archiveAWarehouseUnitByID(String id) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
        if (warehouse == null) {
            throw new WebApplicationException("Warehouse with ID " + id + " not found.", 404);
        }
        LOGGER.debug("Archived warehouse: " + warehouse);
        archiveWarehouseUseCase.archive(warehouse);
    }

    @Override
    public Warehouse replaceTheCurrentActiveWarehouse(
            String businessUnitCode, @NotNull Warehouse data) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse newWarehouse = toWarehouseModel(data);
        newWarehouse.businessUnitCode = businessUnitCode;
        replaceWarehouseUseCase.replace(newWarehouse);
        LOGGER.debug("Replaced warehouse: " + newWarehouse);
        return data;
    }

    private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toWarehouseModel(Warehouse apiModel) {
        com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
        warehouse.businessUnitCode = apiModel.getBusinessUnitCode();
        warehouse.location = apiModel.getLocation();
        warehouse.capacity = apiModel.getCapacity();
        warehouse.stock = apiModel.getStock();
        warehouse.createdAt = LocalDateTime.now();
        return warehouse;
    }


    private Warehouse toWarehouseResponse(
            com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
        var response = new Warehouse();
        response.setBusinessUnitCode(warehouse.businessUnitCode);
        response.setLocation(warehouse.location);
        response.setCapacity(warehouse.capacity);
        response.setStock(warehouse.stock);

        return response;
    }
}
