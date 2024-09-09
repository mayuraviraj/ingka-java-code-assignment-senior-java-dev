package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseProductAssociation;

public interface WarehouseProductAssociationStore {

    long countByStore(String name);

    long countByProduct(String name);

    long countByWarehouse(String businessUnitCode);

    void create(WarehouseProductAssociation association);
}
