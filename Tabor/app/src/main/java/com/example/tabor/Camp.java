package com.example.tabor;

public class Camp {
    private String name;
    private String description;
    private String price;
    private float ratedInfo;
    private final int imageUrl;

    public Camp(String description, int imageUrl, String name, String price, float ratedInfo) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.ratedInfo = ratedInfo;
    }

    public String getDescription() {
        return description;
    }

    public int getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public float getRatedInfo() {
        return ratedInfo;
    }
}