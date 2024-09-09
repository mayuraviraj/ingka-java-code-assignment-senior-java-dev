package com.fulfilment.application.monolith.warehouses.domain.models;

import java.io.Serializable;

public class WarehouseProductAssociation implements Serializable {

    public Warehouse warehouse;

    public ProductModel product;

    public StoreModel store;
}
