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


    public void writeNewProduct() {
        Product product = new Product();
        product.setId("Product1");
        product.setName("Joan's Home Grown Orange");
        product.setDescription("In Europe and America, surveys show that orange is the colour most associated with amusement, the unconventional, extroverts, warmth, fire, energy, activity, danger, taste and aroma, the autumn season, and Protestantism, as well as having long been the national colour of the Netherlands and the House of Orange.");
        product.setPrice("$5 per dozen");
        product.setSellerId("User1");

        ArrayList<String> images = new ArrayList<String>();
        images.add("gs://farm-fresh-76e2e.appspot.com/products/product-1/images/orange-1.jpeg");
        images.add("gs://farm-fresh-76e2e.appspot.com/products/product-1/images/orange-2.jpeg");
        images.add("gs://farm-fresh-76e2e.appspot.com/products/product-1/images/orange-3.jpeg");
        images.add("gs://farm-fresh-76e2e.appspot.com/products/product-1/images/orange-4.jpeg");

        product.setImageUrls(images);

        rootRef.child("products").child("Product1").setValue(product);
    }

    public void generateNewSeller(){
        User user = new User();
        user.setDisplayName("Joan Dylan");
        user.setEmail("joan@gmail.com");
        user.setProfileImageUrl("gs://farm-fresh-76e2e.appspot.com/users/user-1/images/joandylan.jpeg");

        ArrayList<String> products = new ArrayList<String>();
        products.add("Product1");
        user.setInventory(products);
        user.setProductBought(products);
        user.setProductBought(products);

        rootRef.child("users").child("User1").setValue(user);
    }

}
