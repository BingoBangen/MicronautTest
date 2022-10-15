package com.example;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.notNullValue;

@MicronautTest
class DemoTest {

    @Inject
    EmbeddedApplication<?> application;

    @Inject
    HelloController controller;



    @Test
    void test1(RequestSpecification spec) {
        spec.when().get("/lab/countries").then().statusCode(200).body(notNullValue());
        controller.population();
    }

    @Test
    void test2(RequestSpecification spec) {
        spec.when().get("/lab/countries/Sweden").then().statusCode(200).body(notNullValue());
        controller.population();
    }


}
