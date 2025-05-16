package com.example.tabor;

public class Camp {

    private String id;
    private String name;
    private String description;
    private String price;
    private float ratedInfo;
    private int imageUrl;
    private int count;


    public Camp() {
    }

    public Camp(String description, int imageUrl, String name, String price, float ratedInfo, int count) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.ratedInfo = ratedInfo;
        this.count = count;

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
    public int getCount() {return count;}
    public String _getId() {return id;}
    public void setId(String id) {this.id = id;}

}