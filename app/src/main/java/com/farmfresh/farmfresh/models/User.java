package com.farmfresh.farmfresh.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by lucerne on 8/20/16.
 */
public class User {
    private String id;
    private String displayName;
    private String email;
    private String profileImageUrl;
    private ArrayList<String> inventory;
    private ArrayList<String> productBought;
    private ArrayList<String> productSold;
    private String userCurrentAddress;
    public static LatLng latLng;

    public void setId(String id) {
        this.id = id;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void setInventory(ArrayList<String> inventory) {
        this.inventory = inventory;
    }

    public void setProductBought(ArrayList<String> productBought) {
        this.productBought = productBought;
    }

    public void setProductSold(ArrayList<String> productSold) {
        this.productSold = productSold;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public ArrayList<String> getInventory() {
        return inventory;
    }

    public ArrayList<String> getProductBought() {
        return productBought;
    }

    public ArrayList<String> getProductSold() {
        return productSold;
    }

    public String getEmail() {
        return email;
    }

    public String getUserCurrentAddress() {
        return userCurrentAddress;
    }

    public void setUserCurrentAddress(String userCurrentAddress) {
        this.userCurrentAddress = userCurrentAddress;
    }
}