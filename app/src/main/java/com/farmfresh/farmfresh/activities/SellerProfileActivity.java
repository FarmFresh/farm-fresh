package com.farmfresh.farmfresh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.SellerProductsAdapter;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.ItemClickSupport;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerProfileActivity extends AppCompatActivity {
    ArrayList<Product> products;
    SellerProductsAdapter adapter;

    private String productKey;
    private FirebaseDatabase database;
    private DatabaseReference productsRef;

    private Product product;

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

//        // from ProductDetailActivity
//        if (user != null) {
//            displaySellerInfo(user);
//        }
//        // from MainActivity
//        else{
//            getSellerInfo(userId);
//        }
//
        getSellerInfo(userId);

        Log.d("DEBUG:", "getSellerListing");
        System.out.println("userId: " + userId);
        getSellerListing(userId);


        ItemClickSupport.addTo(rvProducts).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Product p = products.get(position);
                        // do it
                        Toast.makeText(getApplicationContext(), "some message", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), ProductDetailActivity.class);
                        intent.putExtra(Constants.PRODUCT_KEY, p.getId());
                        startActivity(intent);
                    }
                }
        );
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
                        Log.d("DEBUG: user", user.toString());

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
        CircleImageView ivProfileImage = (CircleImageView) findViewById(R.id.ivProfileImage);

        Picasso.with(SellerProfileActivity.this)
                .load(user.getProfileImageUrl())
                .into(ivProfileImage);
    }


    // Get list of products from product ids
    void getSellerListing(String userId) {

        Log.d("DEBUG: userId", userId);

        Query queryRef = productsRef.orderByChild("sellerId")
                .startAt(userId)
                .endAt(userId);

        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                Product p = snapshot.getValue(Product.class);
                p.setId(snapshot.getKey());
                products.add(p);
                product = p;
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

    void onClickProduct(View view){
        Toast.makeText(getApplicationContext(), "some message", Toast.LENGTH_SHORT).show();
    }


}