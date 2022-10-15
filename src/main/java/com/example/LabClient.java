package com.example;

import io.micronaut.core.async.annotation.SingleResult;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.client.annotation.Client;
import org.reactivestreams.Publisher;

import java.util.List;

import static io.micronaut.http.HttpHeaders.ACCEPT;
import static io.micronaut.http.HttpHeaders.USER_AGENT;

@Client("https://firebasestorage.googleapis.com")
@Header(name = USER_AGENT, value = "Micronaut HTTP Client")
@Header(name = ACCEPT, value = "application/vnd.github.v3+json, application/json")
interface LabClient {
    String path = "/v0/b/micronaut-c179b.appspot.com/o/worldpopulation.json?alt=media&token=9ea51126-8b53-47dd-8b5a-63af14f0e84a";

    @Get(path)
    @SingleResult
    Publisher<List> fetchPopulationData();
}