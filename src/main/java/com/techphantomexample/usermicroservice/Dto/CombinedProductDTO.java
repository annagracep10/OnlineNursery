package com.techphantomexample.usermicroservice.Dto;

import java.util.List;

public class CombinedProductDTO {
    private List<PlantDTO> plants;
    private List<PlanterDTO> planters;
    private List<SeedDTO> seeds;

    public CombinedProductDTO() {
    }

    public CombinedProductDTO(List<PlantDTO> plants, List<PlanterDTO> planters, List<SeedDTO> seeds) {
        this.plants = plants;
        this.planters = planters;
        this.seeds = seeds;
    }

    // Getters and setters
    public List<PlantDTO> getPlants() {
        return plants;
    }

    public void setPlants(List<PlantDTO> plants) {
        this.plants = plants;
    }

    public List<PlanterDTO> getPlanters() {
        return planters;
    }

    public void setPlanters(List<PlanterDTO> planters) {
        this.planters = planters;
    }

    public List<SeedDTO> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<SeedDTO> seeds) {
        this.seeds = seeds;
    }
}