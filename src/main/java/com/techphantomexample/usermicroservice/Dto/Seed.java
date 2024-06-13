package com.techphantomexample.usermicroservice.Dto;

import com.fasterxml.jackson.annotation.JsonTypeName;

//@JsonTypeName("seed")
public class Seed extends BaseProduct {
    private String seedType;
    private int germinationTime;
    private String season;

    public Seed(String name, String description, double price, String category, int quantity, String seedType, int germinationTime, String season) {
        super(name, description, price, category, quantity);
        this.seedType = seedType;
        this.germinationTime = germinationTime;
        this.season = season;
    }

    public String getSeedType() {
        return seedType;
    }

    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    public int getGerminationTime() {
        return germinationTime;
    }

    public void setGerminationTime(int germinationTime) {
        this.germinationTime = germinationTime;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }


    public Seed() {
    }

}
