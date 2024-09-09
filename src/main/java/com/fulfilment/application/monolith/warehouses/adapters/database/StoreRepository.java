package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import com.fulfilment.application.monolith.warehouses.domain.ports.StoreStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoreRepository implements StoreStore, PanacheRepository<Store> {
    @Override
    public StoreModel findByName(String name) {
        Store store = this.find("name", name).firstResult();
        if (store == null) {
            throw new IllegalArgumentException("Store not found with name: " + name);
        }
        return store.toStoreModel();
    }
}
