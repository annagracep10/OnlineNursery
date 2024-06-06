package com.techphantomexample.usermicroservice.Dto;

public class Plant extends BaseProduct{
    private String typeOfPlant;
    private String sunlightRequirements;
    private String wateringFrequency;

    public String getTypeOfPlant() {
        return typeOfPlant;
    }

    public void setTypeOfPlant(String typeOfPlant) {
        this.typeOfPlant = typeOfPlant;
    }

    public String getSunlightRequirements() {
        return sunlightRequirements;
    }

    public void setSunlightRequirements(String sunlightRequirements) {
        this.sunlightRequirements = sunlightRequirements;
    }

    public String getWateringFrequency() {
        return wateringFrequency;
    }

    public void setWateringFrequency(String wateringFrequency) {
        this.wateringFrequency = wateringFrequency;
    }

    public Plant() {
    }

    public Plant(String name, String description, double price, String category, int quantity, String typeOfPlant, String sunlightRequirements, String wateringFrequency) {
        super(name, description, price, category, quantity);
        this.typeOfPlant = typeOfPlant;
        this.sunlightRequirements = sunlightRequirements;
        this.wateringFrequency = wateringFrequency;
    }
}
