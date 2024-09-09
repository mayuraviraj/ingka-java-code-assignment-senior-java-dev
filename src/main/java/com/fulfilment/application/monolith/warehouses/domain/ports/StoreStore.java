package com.fulfilment.application.monolith.warehouses.domain.ports;


import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;

public interface StoreStore {
    StoreModel findByName(String name);
}

