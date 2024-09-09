package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;

public interface ProductStore {
    ProductModel findByName(String name);
}
