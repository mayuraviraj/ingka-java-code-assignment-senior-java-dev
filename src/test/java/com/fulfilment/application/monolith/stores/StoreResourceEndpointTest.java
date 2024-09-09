package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class StoreResourceEndpointTest {

    private static final String PATH = "store";

    @Test
    public void testGetAllStores() {
        // Create new stores
        given()
                .body("{ \"name\": \"NEW_STORE_1\", \"quantityProductsInStock\": 50 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_STORE_1"));

        given()
                .body("{ \"name\": \"NEW_STORE_2\", \"quantityProductsInStock\": 30 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_STORE_2"));

        // List all stores
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(containsString("NEW_STORE_1"), containsString("NEW_STORE_2"));
    }

    @Test
    public void testGetSingleStore() {
        // Create a store
        Integer id = given()
                .body("{ \"name\": \"SINGLE_STORE\", \"quantityProductsInStock\": 20 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Get the single store
        given()
                .when()
                .get(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", is("SINGLE_STORE"))
                .body("quantityProductsInStock", is(20));
    }

    @Test
    public void testCreateStore() {
        given()
                .body("{ \"name\": \"CREATED_STORE\", \"quantityProductsInStock\": 15 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("CREATED_STORE"));
    }

    @Test
    public void testUpdateStore() {
        // Create a store
        Integer id = given()
                .body("{ \"name\": \"STORE_TO_UPDATE\", \"quantityProductsInStock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Update the store
        given()
                .body("{ \"name\": \"UPDATED_STORE\", \"quantityProductsInStock\": 25 }")
                .contentType("application/json")
                .when()
                .put(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", is("UPDATED_STORE"))
                .body("quantityProductsInStock", is(25));
    }

    @Test
    public void testPatchStore() {
        // Create a store
        Integer id = given()
                .body("{ \"name\": \"STORE_TO_PATCH\", \"quantityProductsInStock\": 5 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Patch the store
        given()
                .body("{ \"name\": \"PATCHED_STORE\", \"quantityProductsInStock\": 15 }")
                .contentType("application/json")
                .when()
                .patch(PATH + "/" + id)
                .then()
                .statusCode(200)
                .body("name", is("PATCHED_STORE"))
                .body("quantityProductsInStock", is(15));
    }

    @Test
    public void testDeleteStore() {
        // Create a store
        Integer id = given()
                .body("{ \"name\": \"STORE_TO_DELETE\", \"quantityProductsInStock\": 40 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Delete the store
        given()
                .when()
                .delete(PATH + "/" + id)
                .then()
                .statusCode(204);

        // Verify store is deleted
        given()
                .when()
                .get(PATH + "/" + id)
                .then()
                .statusCode(404);
    }

    @Test
    public void testCreateStoreWithInvalidId() {
        given()
                .body("{ \"id\": 1, \"name\": \"INVALID_STORE\", \"quantityProductsInStock\": 20 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(422)
                .body("error", containsString("Id was invalidly set on request."));
    }

    @Test
    public void testUpdateStoreWithMissingName() {
        // Create a store
        Integer id = given()
                .body("{ \"name\": \"STORE_TO_UPDATE\", \"quantityProductsInStock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        // Try updating without a name
        given()
                .body("{ \"quantityProductsInStock\": 20 }")
                .contentType("application/json")
                .when()
                .put(PATH + "/" + id)
                .then()
                .statusCode(422)
                .body("error", containsString("Store Name was not set on request."));
    }

    @Test
    public void testHandleWebApplicationException() {
        // Attempt to retrieve a non-existing store to trigger 404 error
        given()
                .when()
                .get(PATH + "/9999")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("exceptionType", is(WebApplicationException.class.getName()))
                .body("code", is(404))
                .body("error", containsString("Store with id of 9999 does not exist."));
    }

    @Test
    public void testHandleInvalidIdOnCreate() {
        // Attempt to create a store with an ID set to trigger 422 error
        given()
                .body("{ \"id\": 1, \"name\": \"INVALID_STORE\", \"quantityProductsInStock\": 20 }")
                .contentType(ContentType.JSON)
                .when()
                .post(PATH)
                .then()
                .statusCode(422)
                .contentType(ContentType.JSON)
                .body("exceptionType", is(WebApplicationException.class.getName()))
                .body("code", is(422))
                .body("error", containsString("Id was invalidly set on request."));
    }

}
