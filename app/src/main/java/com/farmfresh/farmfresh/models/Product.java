package com.farmfresh.farmfresh.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lucerne on 8/20/16.
 */
public class Product implements Parcelable {
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
    private Bitmap icon;
    private String address;
    private String duration;
    private String distance;

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
    public void setIcon(Bitmap icon) {
        this.icon = icon;
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
    public Bitmap getIcon() {
        return icon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.price);
        dest.writeStringList(this.imageUrls);
        dest.writeString(this.sellerId);
        dest.writeParcelable(this.icon, flags);
        dest.writeString(this.address);
    }

    public Product() {
    }

    protected Product(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.price = in.readString();
        this.imageUrls = in.createStringArrayList();
        this.sellerId = in.readString();
        this.icon = in.readParcelable(Bitmap.class.getClassLoader());
        this.address = in.readString();
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public Map<String,Object> toMap() {
        Map<String,Object> map = new HashMap<>();
        map.put("name", name);
        map.put("description", description);
        map.put("imageUrls", imageUrls);
        map.put("sellerId", sellerId);
        map.put("price", price);
        map.put("address", address);
        return map;
    }
}
