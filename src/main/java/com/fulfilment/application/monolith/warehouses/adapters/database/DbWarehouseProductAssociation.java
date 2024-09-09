package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "warehouse_product_associations")
public class DbWarehouseProductAssociation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "warehouse_id", nullable = false)
    public DbWarehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    public Product product;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    public Store store;
}
