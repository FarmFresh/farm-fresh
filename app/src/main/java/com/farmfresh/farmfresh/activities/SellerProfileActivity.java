package com.farmfresh.farmfresh.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.SellerProductsAdapter;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

public class SellerProfileActivity extends AppCompatActivity {
    ArrayList<Product> products;
    SellerProductsAdapter adapter;

    private String productKey;
    private FirebaseDatabase database;
    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_profile);

        this.productKey = getIntent().getStringExtra(Constants.PRODUCT_KEY);
        database = FirebaseDatabase.getInstance();
        productsRef = database.getReference().child(Constants.NODE_PRODUCTS);

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) findViewById(R.id.rvProducts);

        products = new ArrayList<Product>();
        // Create adapter passing in the sample user data
        adapter = new SellerProductsAdapter(this, products);
        // Attach the adapter to the recyclerview to populate items
        rvProducts.setAdapter(adapter);
        // Set layout manager to position the items
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!

        User user = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
        String userId = (String) Parcels.unwrap(getIntent().getParcelableExtra("userId"));

        // from ProductDetailActivity
        if (user != null) {
            getSellerListing(user);
            displaySellerInfo(user);
        }
        // from MainActivity
        else{
            getSellerInfo(userId);
        }

    }

    // Get seller information who is associated with this product
    void getSellerInfo(String id){

        database.getReference().child("users").child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        user.toString();

                        displaySellerInfo(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    void displaySellerInfo(User user){
        TextView tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvUserName.setText(user.getDisplayName());

        // get image from facebook profile
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);

        Picasso.with(SellerProfileActivity.this)
                .load(user.getProfileImageUrl())
                .into(ivProfileImage);
    }


    // Get list of products from product ids
    void getSellerListing(User user) {

        Query queryRef = productsRef.orderByChild("sellerId")
                .startAt("pMfeFjNSp6eIXsoao3JkbkjoC9b2")
                .endAt("pMfeFjNSp6eIXsoao3JkbkjoC9b2");

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Product product = snapshot.getValue(Product.class);
                products.add(product);
                adapter.notifyItemInserted(products.size()-1);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                int i = products.indexOf(product);
                products.remove(i);
                adapter.notifyItemRemoved(i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }
        });
    }

    void getImageUrl(Product product){
        products.add(product);
        adapter.notifyItemInserted(products.size()-1);
    }
}