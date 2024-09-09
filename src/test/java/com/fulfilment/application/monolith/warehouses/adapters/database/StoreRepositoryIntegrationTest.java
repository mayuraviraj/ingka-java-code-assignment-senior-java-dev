package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class StoreRepositoryIntegrationTest {

    @Inject
    StoreRepository storeRepository;

    @Test
    @TestTransaction
    public void testFindByName_Success() {
        StoreModel storeModel = storeRepository.findByName("TONSTAD");
        assertEquals("TONSTAD", storeModel.name);
    }

    @Test
    @TestTransaction
    public void testFindByName_NotFound() {
        String name = UUID.randomUUID().toString();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            storeRepository.findByName(name);
        });

        assertEquals("Store not found with name: " + name, exception.getMessage());
    }
}
