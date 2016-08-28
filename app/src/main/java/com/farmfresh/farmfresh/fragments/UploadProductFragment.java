package com.farmfresh.farmfresh.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.activities.MainActivity;
import com.farmfresh.farmfresh.databinding.FragmentUploadProductBinding;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class UploadProductFragment extends Fragment {
    Product product;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    private FragmentUploadProductBinding binding;

    private TextView tvName;
    private TextView tvDescription;
    private TextView tvPrice;
    List<ImageView> imageViews = new ArrayList<>(Constants.MAX_PRODUCT_IMAGES);

    public static UploadProductFragment newInstance(Product product) {
        UploadProductFragment fragment = new UploadProductFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.PRODUCT_KEY, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        this.currentUser = auth.getCurrentUser();
        this.product = getArguments().getParcelable(Constants.PRODUCT_KEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_product, container, false);
        productInfoSetup();
        productImagesSetup();
        toolBarSetup();
        //set current user as product's seller
        this.product.setSellerId(currentUser.getUid());
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        saveProductInFireBase();
    }

    private void productInfoSetup() {
        tvName = binding.tvNameValue;
        tvName.setText(this.product.getName());
        tvDescription = binding.tvDescriptionValue;
        tvDescription.setText(this.product.getDescription());
        tvPrice = binding.tvPriceValue;
        tvPrice.setText(this.product.getPrice());
    }

    private void productImagesSetup() {

        imageViews.add(binding.ivProductImage1);
        imageViews.add(binding.ivProductImage2);
        imageViews.add(binding.ivProductImage3);
        imageViews.add(binding.ivProductImage4);
        final ArrayList<String> imageUrls = product.getImageUrls();
        for(int i = 0; i < imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            final Uri uri = Uri.parse(imageUrl);
            try {
                final ParcelFileDescriptor input = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                Bitmap bitmap = Helper
                        .decodeSampledBitmapFromFileDescriptor(input.getFileDescriptor(),
                                Constants.PRODUCT_IMAGE_WIDTH,
                                Constants.PRODUCT_IMAGE_HEIGHT);
                imageViews.get(i).setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void toolBarSetup() {
        Button cancelButton = (Button)getActivity().findViewById(R.id.btnToolBar);
        cancelButton.setText("Cancel");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to home activity
                Intent homeActivityIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(homeActivityIntent);
            }
        });
    }

    /**
     * Save product info in the products node
     * @return product id
     */
    private String saveProductInfo() {
        final DatabaseReference productsRef = database.getReference()
                .child(Constants.NODE_PRODUCTS);
        final String newProductKey = productsRef.push().getKey();
        productsRef.child(newProductKey);
        //clear product dummy urls
        product.getImageUrls().clear();
        productsRef.setValue(product);
        return newProductKey;
    }

    private ArrayList<String> saveProductImages(String productKey) {
        final StorageReference imagesRef = storage.getReference().child(productKey).child("images");
        for(int i = 0; i < imageViews.size(); i++) {
        }
        return null;
    }

    private void updateProductWithImageUrls(List<String> imageUrls) {

    }

    private void saveProductInFireBase() {
        String productId = saveProductInfo();
        final ArrayList<String> imageUrls = saveProductImages(productId);
        updateProductWithImageUrls(imageUrls);
    }
}
