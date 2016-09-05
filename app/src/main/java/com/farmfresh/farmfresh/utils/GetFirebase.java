package com.farmfresh.farmfresh.utils;

import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by lucerne on 8/21/16.
 */
public class GetFirebase {

    // Write a message to the database
    public FirebaseDatabase database = FirebaseDatabase.getInstance();
    public DatabaseReference rootRef = database.getReference();

    public String databaseUrl = "gs://farm-fresh-76e2e.appspot.com/";


    void generateNewSellerItem(int N, String SellerId) {
        for (int i=0; i < N; ++i) {
            Product product = new Product();
            product.setId(SellerId + "Product" + Integer.toString(i));
            product.setName("Joan's Home Grown Orange");
            product.setDescription("In Europe and America, surveys show that orange is the colour most associated with amusement, the unconventional, extroverts, warmth, fire, energy, activity, danger, taste and aroma, the autumn season, and Protestantism, as well as having long been the national colour of the Netherlands and the House of Orange.");
            product.setPrice("$5 per dozen");
            product.setSellerId(SellerId);

            ArrayList<String> images = new ArrayList<String>();
            images.add("products/product-1/images/orange-1.jpeg");
            images.add("products/product-1/images/orange-2.jpeg");
            images.add("products/product-1/images/orange-3.jpeg");
            images.add("products/product-1/images/orange-4.jpeg");


            product.setImageUrls(images);

            rootRef.child("products").child(SellerId + "Product" + Integer.toString(i)).setValue(product);
        }
    }

    void generateNewSeller(int N, String SellerId){
        User user = new User();
        user.setDisplayName("Joan");
        user.setEmail("joan@gmail.com");
        user.setProfileImageUrl("users/user-1/images/joandylan.jpeg");

        ArrayList<String> products = new ArrayList<String>();

        for (int i=0; i < N; ++i) {
            products.add(SellerId + "Product" + Integer.toString(i));
        }
        user.setInventory(products);
        user.setProductBought(products);
        user.setProductBought(products);

        rootRef.child("users").child(SellerId).setValue(user);
    }

}
