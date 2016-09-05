package com.farmfresh.farmfresh.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.adapter.SellerProductsAdapter;
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

/**
 * Created by pbabu on 8/18/16.
 */
public class ProfileFragment extends Fragment {

    ArrayList<Product> products;
    GetFirebase firebase;
    SellerProductsAdapter adapter;

    FirebaseStorage storage;
    StorageReference storageRef;
    final long ONE_MEGABYTE = 1024 * 1024;

    User currentUser;
    Bitmap userProfileImage;
    static String userId;

    public static ProfileFragment newInstance(String id) {
        ProfileFragment fg = new ProfileFragment();
        userId = id;
        Bundle args = new Bundle();
        args.putString("userId", id);
        fg.setArguments(args);
        return fg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get back arguments
        userId = getArguments().getString("userId", "");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        super.onCreate(savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_seller_profile, container, false);

        firebase = new GetFirebase();

        storage = FirebaseStorage.getInstance();

        // Lookup the recyclerview in activity layout
        RecyclerView rvProducts = (RecyclerView) view.findViewById(R.id.rvProducts);

        products = new ArrayList<Product>();
        // Create adapter passing in the sample user data
        adapter = new SellerProductsAdapter(this.getContext(), products);
        // Attach the adapter to the recyclerview to populate items
        rvProducts.setAdapter(adapter);
        // Set layout manager to position the items
        rvProducts.setLayoutManager(new LinearLayoutManager(this.getContext()));
        // That's all!

        // get user and user image from Google login page
//        currentUser = (User) Parcels.unwrap(getIntent().getParcelableExtra("user"));
//        Bitmap bm = (Bitmap) getIntent().getParcelableExtra("ProfileImage");

        userId.toString();
        if (currentUser != null && userProfileImage != null) {
            getSellerListing(currentUser.getInventory());
            displaySellerInfo(view, currentUser, userProfileImage);
        }
        return view;
    }

    void displaySellerInfo(View view, User user, Bitmap bm){
        TextView tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvUserName.setText(user.getDisplayName());

        // get image from facebook profile
        ImageView ivProfileImage = (ImageView) view.findViewById(R.id.ivProfileImage);
        ivProfileImage.setImageBitmap(bm);
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

                        getImageUrl(product);
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
                    Bitmap bm = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                    // resize image to 100x100
                    int height = 100;
                    int width = 100;
                    if (bm.getWidth() > bm.getHeight()){
                        height = 100* bm.getHeight() / bm.getWidth();
                    }
                    else{
                        width = 100* bm.getWidth() / bm.getHeight();
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


