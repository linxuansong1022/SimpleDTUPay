package org.acme.simpledtupay;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.acme.simpledtupay.domain.Customer;
import org.acme.simpledtupay.domain.Merchant;
import org.acme.simpledtupay.domain.Payment;
import org.acme.simpledtupay.api.PaymentResource.PaymentRequest;

import java.util.List;

import static io.restassured.RestAssured.given;

public class SimpleDTUPayClient {

    public SimpleDTUPayClient() {
        RestAssured.baseURI = "http://localhost:8080";
        // Force RestAssured to use Jackson 2 for serialization/deserialization
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .defaultObjectMapperType(ObjectMapperType.JACKSON_2));
    }

    public String registerCustomer(Customer customer) {
        return given()
                .contentType("application/json")
                .body(customer)
                .post("/customers")
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    public String registerMerchant(Merchant merchant) {
        return given()
                .contentType("application/json")
                .body(merchant)
                .post("/merchants")
                .then()
                .statusCode(200)
                .extract()
                .asString();
    }

    public Response pay(PaymentRequest request) {
        return given()
                .contentType("application/json")
                .body(request)
                .post("/payments");
    }

    public List<Payment> getPayments() {
        return given()
                .get("/payments")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", Payment.class);
    }
}
