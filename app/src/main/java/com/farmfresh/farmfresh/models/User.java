package com.farmfresh.farmfresh.models;

import java.util.ArrayList;

/**
 * Created by lucerne on 8/20/16.
 */
public class User {
    private Long id;
    private String firstName;
    private String profileImageUrl;
    private ArrayList<Long> inventory;
    private ArrayList<Long> productBought;
    private ArrayList<Long> productSold;

    public void setId(Long id) {
        this.id = id;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setInventory(ArrayList<Long> inventory) {
        this.inventory = inventory;
    }

    public void setProductBought(ArrayList<Long> productBought) {
        this.productBought = productBought;
    }

    public void setProductSold(ArrayList<Long> productSold) {
        this.productSold = productSold;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public ArrayList<Long> getInventory() {
        return inventory;
    }

    public ArrayList<Long> getProductBought() {
        return productBought;
    }

    public ArrayList<Long> getProductSold() {
        return productSold;
    }
}

