package com.example;


import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import jakarta.inject.Inject;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller("/lab")
public class HelloController {

    @Inject
    private LabClient labClient;
    @Inject
    private Repository repo;

    @Get
    @Produces(MediaType.TEXT_PLAIN)
    public String index() {
        return "Hello World";
    }

    @Get("/import")
    @Produces(MediaType.TEXT_PLAIN)
    public void population() {
        Publisher<List> result = labClient.fetchPopulationData();
        result.subscribe(new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(2);

            }

            @Override
            public void onNext(List list) {
                list.stream().forEach(val -> {
                    Map<String, String> map = (Map) val;
                    String rank = map.get("Rank");
                    BigDecimal population = new BigDecimal(map.get("population"));
                    String country = map.get("country");
                    String ratio = map.get("World");
                    Country c = new Country();
                    c.setPopulation(population);
                    c.setName(country);
                    c.setRank(rank);
                    c.setRank(ratio);
                    repo.saveData(c);
                });

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Get("/countries")
    public Flux<Country> reaDynamo() {

        return repo.readDynamo();

    }


    @Get("/countriesh/{population}")
    public Flux<Country> getCountriesByPopulation(String population) {

        return repo.getCountriesByPopulation(population);

    }


    @Get("/countries/{name}")
    public Mono getCountry(String name) {
        return repo.getByCountry(name);

    }


    @Get("/eraseCountries")
    public void eraseCountries() {
         repo.eraseDynamo();

    }

}