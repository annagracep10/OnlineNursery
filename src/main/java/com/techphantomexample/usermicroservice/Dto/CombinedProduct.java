package com.techphantomexample.usermicroservice.Dto;

import java.util.List;

public class CombinedProduct {
    private List<Plant> plants;
    private List<Planter> planters;
    private List<Seed> seeds;

    public CombinedProduct() {
    }

    public CombinedProduct(List<Plant> plants, List<Planter> planters, List<Seed> seeds) {
        this.plants = plants;
        this.planters = planters;
        this.seeds = seeds;
    }

    // Getters and setters
    public List<Plant> getPlants() {
        return plants;
    }

    public void setPlants(List<Plant> plants) {
        this.plants = plants;
    }

    public List<Planter> getPlanters() {
        return planters;
    }

    public void setPlanters(List<Planter> planters) {
        this.planters = planters;
    }

    public List<Seed> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<Seed> seeds) {
        this.seeds = seeds;
    }
}