package com.fulfilment.application.monolith.products;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNot.not;

@QuarkusTest
public class ProductEndpointTest {

    private static final String PATH = "product";

    @Test
    public void testGetAllProducts() {
        given()
                .body("{ \"name\": \"NEW_PRODUCT_1\", \"description\": \"A new product\", \"price\": 100.00, \"stock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_PRODUCT_1"));
        given()
                .body("{ \"name\": \"NEW_PRODUCT_2\", \"description\": \"A new product\", \"price\": 100.00, \"stock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_PRODUCT_2"));
        given()
                .body("{ \"name\": \"NEW_PRODUCT_3\", \"description\": \"A new product\", \"price\": 100.00, \"stock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_PRODUCT_3"));
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(containsString("NEW_PRODUCT_1"), containsString("NEW_PRODUCT_2"), containsString("NEW_PRODUCT_3"));
    }

    @Test
    public void testGetSingleProduct() {
        given()
                .when()
                .get(PATH + "/1")
                .then()
                .statusCode(200)
                .body(containsString("TONSTAD"));

        given()
                .when()
                .get(PATH + "/999")
                .then()
                .statusCode(404)
                .body(containsString("Product with id of 999 does not exist."));
    }

    @Test
    public void testCreateProduct() {
        given()
                .body("{ \"name\": \"NEW_PRODUCT\", \"description\": \"A new product\", \"price\": 100.00, \"stock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_PRODUCT"));
        given()
                .body("{ \"id\": 1, \"name\": \"INVALID_PRODUCT\", \"description\": \"An invalid product\", \"price\": 50.00, \"stock\": 5 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(422)
                .body(containsString("Id was invalidly set on request."));
    }

    @Test
    public void testUpdateProduct() {
        given()
                .body("{ \"name\": \"UPDATED_PRODUCT\", \"description\": \"Updated description\", \"price\": 120.00, \"stock\": 20 }")
                .contentType("application/json")
                .when()
                .put(PATH + "/1")
                .then()
                .statusCode(200)
                .body(containsString("UPDATED_PRODUCT"));
        given()
                .body("{ \"name\": \"NON_EXISTENT_PRODUCT\", \"description\": \"Description\", \"price\": 150.00, \"stock\": 30 }")
                .contentType("application/json")
                .when()
                .put(PATH + "/999")
                .then()
                .statusCode(404)
                .body(containsString("Product with id of 999 does not exist."));
    }

    @Test
    public void testDeleteProduct() {
        given()
                .when()
                .delete(PATH + "/1")
                .then()
                .statusCode(204);
        given()
                .when()
                .delete(PATH + "/999")
                .then()
                .statusCode(404)
                .body(containsString("Product with id of 999 does not exist."));
    }

    @Test
    public void testCrudProduct() {
        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(containsString("TONSTAD"), containsString("KALLAX"), containsString("BESTÅ"));
        Response createResponse = given()
                .body("{ \"name\": \"NEW_PRODUCT\", \"description\": \"A new product\", \"price\": 100.00, \"stock\": 10 }")
                .contentType("application/json")
                .when()
                .post(PATH)
                .then()
                .statusCode(201)
                .body(containsString("NEW_PRODUCT"))
                .extract()
                .response();

        String newProductId = createResponse.jsonPath().getString("id");

        given().when().delete(PATH + "/" + newProductId).then().statusCode(204);

        given()
                .when()
                .get(PATH)
                .then()
                .statusCode(200)
                .body(not(containsString("NEW_PRODUCT")), containsString("KALLAX"), containsString("BESTÅ"));
    }

    @Test
    public void testUpdateProductWithMissingName() {
        final Long existingProductId = 1L;

        given()
                .body("{ \"description\": \"Updated description\", \"price\": 120.00, \"stock\": 20 }")
                .contentType("application/json")
                .when()
                .put(PATH + "/" + existingProductId)
                .then()
                .statusCode(422) // Expecting a 422 Unprocessable Entity
                .body("error", equalTo("Product Name was not set on request.")); // Error message validation
    }

    @Test
    public void testErrorMapper() {
        given()
                .when()
                .get("/product/99999") // Assuming this endpoint will throw a WebApplicationException with a 404 status
                .then()
                .statusCode(jakarta.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode())
                .body("exceptionType", equalTo("jakarta.ws.rs.WebApplicationException"))
                .body("code", equalTo(jakarta.ws.rs.core.Response.Status.NOT_FOUND.getStatusCode()))
                .body("error", equalTo("Product with id of 99999 does not exist.")); // Adjust message based on actual exception message
    }
}
