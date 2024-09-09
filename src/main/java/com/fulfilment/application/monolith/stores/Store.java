package com.fulfilment.application.monolith.stores;

import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Cacheable
public class Store extends PanacheEntity {

    @Column(length = 40, unique = true)
    public String name;

    public int quantityProductsInStock;

    public Store() {
    }

    public Store(String name) {
        this.name = name;
    }

    public StoreModel toStoreModel() {
        StoreModel storeModel = new StoreModel();
        storeModel.name = this.name;
        storeModel.quantityProductsInStock = this.quantityProductsInStock;
        return storeModel;
    }
}
