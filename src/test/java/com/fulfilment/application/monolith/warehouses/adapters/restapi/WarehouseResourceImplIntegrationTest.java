package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTest
public class WarehouseResourceImplIntegrationTest {

    private static final String BASE_PATH = "/warehouse";

    @Test
    public void testSimpleListWarehouses() {
        // List all, should have all 3 products the database has initially:
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .body(containsString("MWH.001"), containsString("MWH.012"), containsString("MWH.023"));
    }

    @Test
    public void testSimpleCheckingArchivingWarehouses() {
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .body(
                        containsString("MWH.001"),
                        containsString("MWH.012"),
                        containsString("MWH.023"),
                        containsString("ZWOLLE-001"),
                        containsString("AMSTERDAM-001"),
                        containsString("TILBURG-001"));

        // Archive the ZWOLLE-001:
        given().when().delete(BASE_PATH + "/MWH.001").then().statusCode(204);

        // List all, ZWOLLE-001 should be missing now:
        given()
                .when()
                .get(BASE_PATH)
                .then()
                .statusCode(200)
                .body(
                        not(containsString("ZWOLLE-001")),
                        containsString("AMSTERDAM-001"),
                        containsString("TILBURG-001"));
    }

    @Test
    @Transactional
    public void testCreateANewWarehouseUnit() {
        Map<String, Object> warehouseData = createWarehouseData("BU102",
                "ZWOLLE-002");

        // Test: create a new warehouse
        given()
                .contentType(ContentType.JSON)
                .body(warehouseData)
                .when().post(BASE_PATH)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("businessUnitCode", is("BU102"))
                .body("location", is("ZWOLLE-002"))
                .body("capacity", is(2));
    }

    @Test
    @Transactional
    public void testGetAWarehouseUnitByID() {
        // Arrange: Add a sample warehouse
        addSampleWarehouse("BU103",
                "AMSTERDAM-001");

        // Test: get warehouse by ID
        given()
                .when().get(BASE_PATH + "/BU103")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("businessUnitCode", is("BU103"));
    }

    @Test
    @Transactional
    public void testArchiveAWarehouseUnitByID() {
        // Arrange: Add a sample warehouse
        addSampleWarehouse("BU104",
                "AMSTERDAM-001");

        // Test: archive warehouse by ID
        given()
                .when().delete(BASE_PATH + "/BU104")
                .then()
                .statusCode(204); // Expecting no content status code

        // Verify: warehouse no longer exists
        given()
                .when().get(BASE_PATH + "/BU104")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON);
    }

    @Test
    @Transactional
    public void testReplaceTheCurrentActiveWarehouse() {
        // Arrange: Add a sample warehouse
        addSampleWarehouse("BU105", "EINDHOVEN-001");

        // Replace data
        Map<String, Object> replacementData = createWarehouseData("BU105",
                "TILBURG-001");
        replacementData.put("capacity", 3);

        // Test: replace warehouse
        given()
                .contentType(ContentType.JSON)
                .body(replacementData)
                .when().post(BASE_PATH + "/BU105/replacement")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("businessUnitCode", is("BU105"))
                .body("capacity", is(3));
    }

    // Helper method to add a sample warehouse
    private void addSampleWarehouse(String businessUnitCode, String location) {
        Map<String, Object> warehouseData = createWarehouseData(businessUnitCode, location);

        given()
                .contentType(ContentType.JSON)
                .body(warehouseData)
                .when().post(BASE_PATH)
                .then()
                .statusCode(200);
    }

    // Helper method to create warehouse data
    private Map<String, Object> createWarehouseData(String businessUnitCode, String location) {
        Map<String, Object> data = new HashMap<>();
        data.put("businessUnitCode", businessUnitCode);
        data.put("location", location);
        data.put("capacity", 2);
        data.put("stock", 2);
        data.put("createdAt", "2024-09-01T10:00:00");
        return data;
    }
}
