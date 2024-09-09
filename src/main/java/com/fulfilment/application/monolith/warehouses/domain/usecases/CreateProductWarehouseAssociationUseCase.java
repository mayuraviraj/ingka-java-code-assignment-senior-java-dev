package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;
import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseProductAssociation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ProductStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.StoreStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseProductAssociationStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class CreateProductWarehouseAssociationUseCase {

    private final WarehouseProductAssociationStore associationStore;
    private final WarehouseStore warehouseStore;
    private final ProductStore productStore;
    private final StoreStore storeStore;

    public CreateProductWarehouseAssociationUseCase(WarehouseProductAssociationStore associationStore,
                                                    WarehouseStore warehouseStore, ProductStore productStore, StoreStore storeStore) {
        this.associationStore = associationStore;
        this.warehouseStore = warehouseStore;
        this.productStore = productStore;
        this.storeStore = storeStore;
    }

    @Transactional
    public void createAssociation(String storeName, String productName, String businessUnitCode) {
        StoreModel storeModel = storeStore.findByName(storeName);
        ProductModel product = productStore.findByName(productName);
        Warehouse warehouse = warehouseStore.findByBusinessUnitCode(businessUnitCode);

        if (storeModel == null || product == null || warehouse == null) {
            throw new IllegalArgumentException("Store, Product, or Warehouse not found.");
        }

        long associationsCountForStore = associationStore.countByStore(storeName);
        if (associationsCountForStore >= 3) {
            throw new IllegalArgumentException("Store can only be fulfilled by a maximum of 3 different warehouses.");
        }

        long associationsCountForProduct = associationStore.countByProduct(productName);
        if (associationsCountForProduct >= 2) {
            throw new IllegalArgumentException("ProductModel can only be fulfilled by a maximum of 2 different warehouses per storeModel.");
        }

        long associationsCountForWarehouse = associationStore.countByWarehouse(businessUnitCode);
        if (associationsCountForWarehouse >= 5) {
            throw new IllegalArgumentException("Warehouse can only store a maximum of 5 types of products.");
        }

        WarehouseProductAssociation association = new WarehouseProductAssociation();
        association.warehouse = warehouse;
        association.product = product;
        association.store = storeModel;

        associationStore.create(association);
    }
}
