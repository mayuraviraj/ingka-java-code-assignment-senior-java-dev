package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;
import com.fulfilment.application.monolith.warehouses.domain.ports.ProductStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductRepository implements ProductStore, PanacheRepository<Product> {
    @Override
    public ProductModel findByName(String name) {
        Product product = this.find("name", name).firstResult();
        if (product == null) {
            throw new IllegalArgumentException("Product not found with name: " + name);
        }
        return product.toProductModel();
    }
}
