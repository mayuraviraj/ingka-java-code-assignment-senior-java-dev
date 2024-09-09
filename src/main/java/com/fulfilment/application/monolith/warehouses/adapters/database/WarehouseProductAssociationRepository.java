package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;
import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseProductAssociation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseProductAssociationStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class WarehouseProductAssociationRepository
        implements WarehouseProductAssociationStore, PanacheRepository<DbWarehouseProductAssociation> {

    @Inject
    private WarehouseRepository warehouseRepository;
    @Inject
    private StoreRepository storeRepository;
    @Inject
    private ProductRepository productRepository;

    @Override
    public long countByStore(String name) {
        return count("store.name", name);
    }


    @Override
    public long countByProduct(String name) {
        return count("product.name", name);
    }

    @Override
    public long countByWarehouse(String businessUnitCode) {
        return count("warehouse.businessUnitCode", businessUnitCode);
    }

    @Override
    @Transactional
    public void create(WarehouseProductAssociation association) {
        DbWarehouseProductAssociation dbWarehouseProductAssociation = new DbWarehouseProductAssociation();

        DbWarehouse dbWarehouse = toDBModel(association.warehouse);
        warehouseRepository.persist(dbWarehouse);
        warehouseRepository.flush();

        Product product = toDBModel(association.product);
        productRepository.persist(product);
        productRepository.flush();

        Store store = toDBModel(association.store);
        storeRepository.persist(store);
        storeRepository.flush();

        dbWarehouseProductAssociation.warehouse = dbWarehouse;
        dbWarehouseProductAssociation.product = product;
        dbWarehouseProductAssociation.store = store;
        persist(dbWarehouseProductAssociation);
    }

    private Product toDBModel(ProductModel productModel) {
        Product product = new Product();
        product.name = productModel.name;
        product.description = productModel.description;
        product.price = productModel.price;
        product.stock = productModel.stock;
        return product;
    }

    private Store toDBModel(StoreModel storeModel) {
        Store store = new Store();
        store.name = storeModel.name;
        store.quantityProductsInStock = storeModel.quantityProductsInStock;
        return store;
    }

    private DbWarehouse toDBModel(Warehouse warehouse) {
        DbWarehouse dbWarehouse = new DbWarehouse();
        dbWarehouse.businessUnitCode = warehouse.businessUnitCode;
        dbWarehouse.location = warehouse.location;
        dbWarehouse.capacity = warehouse.capacity;
        dbWarehouse.stock = warehouse.stock;
        dbWarehouse.createdAt = warehouse.createdAt;
        dbWarehouse.archivedAt = warehouse.archivedAt;
        return dbWarehouse;
    }
}
