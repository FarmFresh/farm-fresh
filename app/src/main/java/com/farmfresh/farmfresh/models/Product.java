package com.farmfresh.farmfresh.models;

import java.util.ArrayList;

/**
 * Created by lucerne on 8/20/16.
 */
public class Product {
    private String id;
    private String name;
    private String description;
    private String price;
    private ArrayList<String> imageUrls;
    private String sellerId;
    private Double latitude;
    private Double longitude;
    private String g;
    private ArrayList<Double> l;

    public void setId(String id) {
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

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getId() {
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

    public String getSellerId() {
        return sellerId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public ArrayList<Double> getL() {
        return l;
    }

    public void setL(ArrayList<Double> l) {
        this.l = l;
    }
}
