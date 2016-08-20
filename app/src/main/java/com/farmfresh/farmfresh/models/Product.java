package com.farmfresh.farmfresh.models;

import java.util.ArrayList;

/**
 * Created by lucerne on 8/20/16.
 */
public class Product {
    private long id;
    private String name;
    private String description;
    private String price;
    private ArrayList<String> imageUrls;
    private long sellerId;

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public long getSellerId() {
        return sellerId;
    }

}
