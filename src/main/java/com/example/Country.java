package com.example;

import io.micronaut.core.annotation.Indexed;

import java.math.BigDecimal;

public class Country {


    private String name;
    private String rank;
    private BigDecimal population;
    private double ratio;


    public Country() {
    }

    public Country(String name,  BigDecimal population) {
        this.name = name;

        this.population = population;

    }

    public Country(String name, String rank, BigDecimal population, double ratio) {
        this.name = name;
        this.rank = rank;
        this.population = population;
        this.ratio = ratio;
    }

    public Country(String name, String population) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public BigDecimal getPopulation() {
        return population;
    }

    public void setPopulation(BigDecimal population) {
        this.population = population;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }
}
