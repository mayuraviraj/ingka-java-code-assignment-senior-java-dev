package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.ProductModel;
import com.fulfilment.application.monolith.warehouses.domain.models.StoreModel;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.WarehouseProductAssociation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ProductStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.StoreStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseProductAssociationStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class CreateProductWarehouseAssociationUseCaseIntegrationTest {

    @Inject
    CreateProductWarehouseAssociationUseCase useCase;

    @InjectMock
    WarehouseProductAssociationStore associationStore;

    @InjectMock
    WarehouseStore warehouseStore;

    @InjectMock
    ProductStore productStore;

    @InjectMock
    StoreStore storeStore;

    @BeforeEach
    public void setUp() {
        // Reset mocks before each test
        Mockito.reset(associationStore, warehouseStore, productStore, storeStore);
    }

    @Test
    @Transactional
    public void testCreateAssociation_Success() {
        // Arrange
        StoreModel store = new StoreModel();
        store.name = "StoreA";
        ProductModel product = new ProductModel();
        product.name = "ProductA";
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU100";

        when(storeStore.findByName("StoreA")).thenReturn(store);
        when(productStore.findByName("ProductA")).thenReturn(product);
        when(warehouseStore.findByBusinessUnitCode("BU100")).thenReturn(warehouse);
        when(associationStore.countByStore("StoreA")).thenReturn(1L);
        when(associationStore.countByProduct("ProductA")).thenReturn(1L);
        when(associationStore.countByWarehouse("BU100")).thenReturn(2L);

        // Act & Assert: No exception should be thrown for a valid case
        assertDoesNotThrow(() -> useCase.createAssociation("StoreA", "ProductA", "BU100"));

        // Verify that the association was created
        verify(associationStore, times(1)).create(any(WarehouseProductAssociation.class));
    }

    @Test
    @Transactional
    public void testCreateAssociation_ExceedStoreLimit() {
        // Arrange
        StoreModel store = new StoreModel();
        store.name = "StoreB";
        ProductModel product = new ProductModel();
        product.name = "ProductB";
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU101";

        when(storeStore.findByName("StoreB")).thenReturn(store);
        when(productStore.findByName("ProductB")).thenReturn(product);
        when(warehouseStore.findByBusinessUnitCode("BU101")).thenReturn(warehouse);
        when(associationStore.countByStore("StoreB")).thenReturn(3L); // Exceeds limit

        // Act & Assert: Expect IllegalArgumentException due to exceeding store limit
        assertThrows(IllegalArgumentException.class,
                () -> useCase.createAssociation("StoreB", "ProductB", "BU101"),
                "Store can only be fulfilled by a maximum of 3 different warehouses.");
    }

    @Test
    @Transactional
    public void testCreateAssociation_ExceedProductLimit() {
        // Arrange
        StoreModel store = new StoreModel();
        store.name = "StoreC";
        ProductModel product = new ProductModel();
        product.name = "ProductC";
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU102";

        when(storeStore.findByName("StoreC")).thenReturn(store);
        when(productStore.findByName("ProductC")).thenReturn(product);
        when(warehouseStore.findByBusinessUnitCode("BU102")).thenReturn(warehouse);
        when(associationStore.countByStore("StoreC")).thenReturn(2L);
        when(associationStore.countByProduct("ProductC")).thenReturn(2L); // Exceeds limit

        // Act & Assert: Expect IllegalArgumentException due to exceeding product limit
        assertThrows(IllegalArgumentException.class,
                () -> useCase.createAssociation("StoreC", "ProductC", "BU102"),
                "ProductModel can only be fulfilled by a maximum of 2 different warehouses per storeModel.");
    }

    @Test
    @Transactional
    public void testCreateAssociation_ExceedWarehouseLimit() {
        // Arrange
        StoreModel store = new StoreModel();
        store.name = "StoreD";
        ProductModel product = new ProductModel();
        product.name = "ProductD";
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = "BU103";

        when(storeStore.findByName("StoreD")).thenReturn(store);
        when(productStore.findByName("ProductD")).thenReturn(product);
        when(warehouseStore.findByBusinessUnitCode("BU103")).thenReturn(warehouse);
        when(associationStore.countByStore("StoreD")).thenReturn(1L);
        when(associationStore.countByProduct("ProductD")).thenReturn(1L);
        when(associationStore.countByWarehouse("BU103")).thenReturn(5L); // Exceeds limit

        // Act & Assert: Expect IllegalArgumentException due to exceeding warehouse limit
        assertThrows(IllegalArgumentException.class,
                () -> useCase.createAssociation("StoreD", "ProductD", "BU103"),
                "Warehouse can only store a maximum of 5 types of products.");
    }

    @Test
    @Transactional
    public void testCreateAssociation_InvalidInputs() {
        // Arrange: null inputs for store, product, and warehouse
        when(storeStore.findByName("InvalidStore")).thenReturn(null);
        when(productStore.findByName("InvalidProduct")).thenReturn(null);
        when(warehouseStore.findByBusinessUnitCode("InvalidBU")).thenReturn(null);

        // Act & Assert: Expect IllegalArgumentException due to missing entities
        assertThrows(IllegalArgumentException.class,
                () -> useCase.createAssociation("InvalidStore", "InvalidProduct", "InvalidBU"),
                "Store, Product, or Warehouse not found.");
    }
}
