package com.farmfresh.farmfresh.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.ProductsAdapter;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.GetFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SellerProfileActivity extends AppCompatActivity {
    ArrayList<Product> products;
    GetFirebase firebase;
    ProductsAdapter adapter;

    FirebaseStorage storage;
    StorageReference storageRef;
    final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        firebase = new GetFirebase();

        storage = FirebaseStorage.getInstance();

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) findViewById(R.id.rvProducts);

        products = new ArrayList<Product>();
//        getSellerInfo("User1");
//        products = Product.createProductsList(20);
        // Create adapter passing in the sample user data
        adapter = new ProductsAdapter(this, products);
        // Attach the adapter to the recyclerview to populate items
        rvProducts.setAdapter(adapter);
        // Set layout manager to position the items
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

        getSellerInfo("User1");
    }

    // Get list of product by seller ID
    void getSellerInfo(String id){

        firebase.rootRef.child("users").child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        user.toString();

                        getSellerListing(user.getInventory());
                        displaySellerInfo(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    void displaySellerInfo(User user){
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(user.getFirstName());

        // get image from facebook profile
        //        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
    }

    // Get list of products from product ids
    void getSellerListing(ArrayList<String> productIds) {

        for (int i = 0; i < productIds.size(); ++i) {

            firebase.rootRef.child("products").child(productIds.get(i)).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            Product product = dataSnapshot.getValue(Product.class);
                            product.toString();

//                        products.add(product);
//                        adapter.notifyItemInserted(products.size()-1);

                            // doesn't work
//                        getRealImageUrl(product);

                            getImageUrl(product);

                            // update adapter, limit number of item download later
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }
    }

    void getImageUrl(Product product){

        if (product.getImageUrls().size() > 0){
            String gs = firebase.databaseUrl + product.getImageUrls().get(0);
            storageRef = storage.getReferenceFromUrl(gs);

            final Product p = product;

            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    int height = 100;
                    int width = 100;
                    if (bm.getWidth() > bm.getHeight()){
                        height = 100* bm.getWidth() / bm.getHeight();
                    }
                    else{
                        width = 100* bm.getHeight()/bm.getWidth();
                    }

                    Bitmap resized = Bitmap.createScaledBitmap(
                            bm, width, height, true);

                    p.setIcon(resized);
                    products.add(p);
                    adapter.notifyItemInserted(products.size()-1);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }



    }


}