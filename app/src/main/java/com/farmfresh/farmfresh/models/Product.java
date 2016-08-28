package com.farmfresh.farmfresh.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    private Bitmap icon;
    private String address;

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

    public Bitmap getIcon() {
        return icon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
