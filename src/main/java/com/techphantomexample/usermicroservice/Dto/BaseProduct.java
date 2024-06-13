package com.techphantomexample.usermicroservice.Dto;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


//@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "category", visible = true)
//@JsonSubTypes({
//        @JsonSubTypes.Type(value = Plant.class, name = "plant"),
//        @JsonSubTypes.Type(value = Planter.class, name = "planter"),
//        @JsonSubTypes.Type(value = Seed.class, name = "seed")
//})
public class BaseProduct {
    private int id;

    private String name;
    private String description;
    private double price;
    private String category;


    private int quantity;

    @Override
    public String toString() {
        return "BaseProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", quantity=" + quantity +
                '}';
    }

    public BaseProduct() {
    }



    public BaseProduct(String name, String description, double price, String category, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
