package com.farmfresh.farmfresh.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.GetFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.parceler.Parcels;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
    CarouselView carouselView;
    GetFirebase firebase;
    Product product;
    ArrayList<Bitmap> list_images;
    private String productKey;
    private FirebaseDatabase database;
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        this.productKey = getIntent().getStringExtra(Constants.PRODUCT_KEY);
        database = FirebaseDatabase.getInstance();
        productsRef = database.getReference().child(Constants.NODE_PRODUCTS);
        productsRef.child(this.productKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ProductDetailActivity.this.product = dataSnapshot.getValue(Product.class);
                final ArrayList<String> imageUrls = ProductDetailActivity.this.product.getImageUrls();
                carouselView.setPageCount(imageUrls.size());
                displayProductInfo(product);
                getSellerInfo(product.getSellerId());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        list_images = new ArrayList<>();

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setImageListener(imageListener);
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            final ArrayList<String> imageUrls = ProductDetailActivity.this.product.getImageUrls();
            Picasso.with(ProductDetailActivity.this)
                    .load(imageUrls.get(position))
                    .into(imageView);
        }
    };

    // Display product information, want this part to show up before images
    void displayProductInfo(Product product){
        TextView tvDescription = (TextView) findViewById(R.id.tvDescription);
        TextView tvPrice = (TextView) findViewById(R.id.tvPrice);
        TextView tvProductName = (TextView) findViewById(R.id.tvProductName);

        tvDescription.setText(product.getDescription());
        tvPrice.setText(product.getPrice());
        tvProductName.setText(product.getName());
    }

    // Get seller information who is associated with this product
    void getSellerInfo(String id){

        database.getReference().child("users").child(id).addListenerForSingleValueEvent(
<<<<<<< HEAD
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);
                        user.toString();

                        displayUserInfo(user);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
=======
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Get user value
                    User user = dataSnapshot.getValue(User.class);
                    user.toString();

                    displayUserInfo(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
>>>>>>> e4b52971d57dbdd71a07fc3a4883d08fbfce4e7e
    }

    // display seller information
    void displayUserInfo(User user){
        ImageView ivSellerEmail = (ImageView) findViewById(R.id.ivSellerEmail);
        ivSellerEmail.setTag(user);

        TextView tvSellerName = (TextView) findViewById(R.id.tvSellerName);
        tvSellerName.setText(user.getDisplayName());

        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        ivProfileImage.setTag(user);

        Picasso.with(ProductDetailActivity.this)
                .load(user.getProfileImageUrl())
                .into(ivProfileImage);
    }

    @Override
    protected void onStart(){
        super.onStart();

        final ImageView ivSellerEmail = (ImageView) findViewById(R.id.ivSellerEmail);
        ivSellerEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = (User) ivSellerEmail.getTag();
                // Do something here
                String uriText =
                        "mailto:" + user.getEmail() +
                                "?subject=" + Uri.encode(user.getDisplayName() +
                                " is interested in your " + product.getName()) +
                                "&body=" + Uri.encode("I'm " + user.getDisplayName() + " ." +
                                "I saw your ad in FarmFresh app. " +
                                "I'm interested in your " + product.getName() + " ." +
                                "I would like to know how I can buy from you.");

                Uri uri = Uri.parse(uriText);

                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                sendIntent.setData(uri);
                if (sendIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(sendIntent, "Send email"));
                }
            }
        });
    }

    void onSellerPhone(View view){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:0377778888"));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    void onSellerSMS(View view){
        Uri smsUri = Uri.parse("tel:" + "0377778888");
        Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
        intent.putExtra("address", "0377778888");
        intent.putExtra("sms_body", "");
        intent.setType("vnd.android-dir/mms-sms");//here setType will set the previous data null.
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    void onSellerProfile(View view){
        Intent intent = new Intent(this, SellerProfileActivity.class);
        ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        User u = (User) ivProfileImage.getTag();
        intent.putExtra("user", Parcels.wrap(u));
        startActivity(intent);
    }
}