package com.farmfresh.farmfresh.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
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
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import java.util.ArrayList;

public class ProductDetailActivity extends AppCompatActivity {
        CarouselView carouselView;
        GetFirebase firebase;
        Product product;
        ArrayList<Bitmap> list_images;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_product_detail);

            firebase = new GetFirebase();

            // write product
            firebase.writeNewProduct();
            firebase.generateNewSeller();

            list_images = new ArrayList<>();

            carouselView = (CarouselView) findViewById(R.id.carouselView);
            carouselView.setImageListener(imageListener);
            // get product by product id
            final String id = "Product1";
            firebase.rootRef.child("products").child(id).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // Get user value
                            product = dataSnapshot.getValue(Product.class);
                            product.toString();

                            displayProductImages(product);
                            displayProductInfo(product);
                            getSellerInfo(product.getSellerId());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("error", id.toString());
                        }
                    });
        }

        ImageListener imageListener = new ImageListener() {
            @Override
            public void setImageForPosition(int position, ImageView imageView) {
                imageView.setImageBitmap(list_images.get(position));
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

            id.toString();

            firebase.rootRef.child("users").child(id).addListenerForSingleValueEvent(
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
//                    Log.d("error", id.toString());
                        }
                    });
        }

        // display seller information
        void displayUserInfo(User user){
            ImageView ivSellerEmail = (ImageView) findViewById(R.id.ivSellerEmail);
            ivSellerEmail.setTag(user);

            TextView tvSellerName = (TextView) findViewById(R.id.tvSellerName);
            tvSellerName.setText(user.getFirstName());

            ImageView ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
            // Get and display seller image

            final String id = product.getSellerId();


        }


        void displayProductImages(Product product) {

            // Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef;
            final long ONE_MEGABYTE = 1024 * 1024;

            int a = product.getImageUrls().size();
            assert(a > -1);

            for (int i=0; i < product.getImageUrls().size(); ++i){
                storageRef = storage.getReferenceFromUrl(product.getImageUrls().get(i));

                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Log.d("DEBUG: early", Integer.toString(list_images.size()));
                        Bitmap bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        bm.toString();
                        int a = list_images.size();

                        int c = list_images.size();
                        Log.d("DEBUG: size", Integer.toString(list_images.size()));
                        // draw this photo
                        list_images.add(0, bm);
//                    list_images.add(bitmap);

                        // reset carousel each time list_images increases
                        carouselView.setPageCount(list_images.size());
//                    carouselView.setImageListener(imageListener);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

        }



        @Override
        protected void onStart(){
            super.onStart();


//        tvProductName = (TextView) findViewById(R.id.tvProductName);

//        conditionRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // populate data from database
////                String text = dataSnapshot.getValue(String.class);
////                tvProductName.setText(text);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

            final ImageView ivSellerEmail = (ImageView) findViewById(R.id.ivSellerEmail);
            ivSellerEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User user = (User) ivSellerEmail.getTag();
                    // Do something here
                    String uriText =
                            "mailto:" + user.getEmail() +
                                    "?subject=" + Uri.encode(user.getFirstName() +
                                    " is interested in your " + product.getName()) +
                                    "&body=" + Uri.encode("I'm " + user.getFirstName() + " ." +
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
    }
