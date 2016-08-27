package com.farmfresh.farmfresh.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.activities.MainActivity;
import com.farmfresh.farmfresh.databinding.FragmentUploadProductBinding;
import com.farmfresh.farmfresh.fragments.ui.models.ImageViewWithProgressBar;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class UploadProductFragment extends Fragment {
    Product product;
    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseUser currentUser;
    List<ImageViewWithProgressBar> imageViewWithProgressBars = new ArrayList<>();
    private FragmentUploadProductBinding binding;
    private DatabaseReference productsRef;
    private TextView tvName;
    private TextView tvDescription;
    private TextView tvPrice;
    private RelativeLayout rlSuccess;
    private int numberOfImagesSelected = 0;
    List<String> imageUrls = new ArrayList<>();
    private String newProductKey;
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
        productsRef = database.getReference().child(Constants.NODE_PRODUCTS);
        newProductKey = productsRef.push().getKey();
        this.currentUser = auth.getCurrentUser();
        this.product = getArguments().getParcelable(Constants.PRODUCT_KEY);
        for (int i = 0; i < product.getImageUrls().size(); i++) {
            imageUrls.add(product.getImageUrls().get(i));
            numberOfImagesSelected++;
        }
        //clear product dummy image urls
        this.product.getImageUrls().clear();
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
        imageViewWithProgressBars.add(new ImageViewWithProgressBar(binding.ivProductImage1, binding.pbImage1));
        imageViewWithProgressBars.add(new ImageViewWithProgressBar(binding.ivProductImage2, binding.pbImage2));
        imageViewWithProgressBars.add(new ImageViewWithProgressBar(binding.ivProductImage3, binding.pbImage3));
        imageViewWithProgressBars.add(new ImageViewWithProgressBar(binding.ivProductImage4, binding.pbImage4));
        for (int i = 0; i < this.imageUrls.size(); i++) {
            String imageUrl = imageUrls.get(i);
            final Uri uri = Uri.parse(imageUrl);
            try {
                final ParcelFileDescriptor input = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                Bitmap bitmap = Helper
                        .decodeSampledBitmapFromFileDescriptor(input.getFileDescriptor(),
                                Constants.PRODUCT_IMAGE_WIDTH,
                                Constants.PRODUCT_IMAGE_HEIGHT);
                imageViewWithProgressBars.get(i).getImageView().setImageBitmap(bitmap);
                imageViewWithProgressBars.get(i).getProgressBar().setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void toolBarSetup() {
        Button cancelButton = (Button) getActivity().findViewById(R.id.btnToolBar);
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
     *
     * @return product id
     */
    private void saveProduct() {
        productsRef.child(newProductKey);
        productsRef.child(newProductKey)
                .setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(binding.llImageViews,
                                UploadProductFragment.this.product.getName() + " is successfully created" ,
                                Snackbar.LENGTH_INDEFINITE)
                                .setActionTextColor(Color.YELLOW)
                                .setDuration(Snackbar.LENGTH_LONG)
                                .show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }
                        }, 2000);
                    }
                });
    }

    private void saveProductImages() {
        final StorageReference imagesRef = storage.getReference()
                .child("products")
                .child(newProductKey).child("images");
        for (int i = 0; i < imageUrls.size(); i++) {
            uploadImage(imageUrls.get(i), imagesRef, i + 1,
                    imageViewWithProgressBars.get(i));
        }
    }

    private void saveProductInFireBase() {
        //save images and then save the product at the end of them
        saveProductImages();
    }

    private void uploadImage(String imageUrl, StorageReference imagesRef, int count,
                             final ImageViewWithProgressBar imageViewWithProgressBar) {
        final StorageReference imageRef = imagesRef.child("image-" + count);
        final Uri uri = Uri.parse(imageUrl);
        try {
            final InputStream imageStream = getActivity().getContentResolver().openInputStream(uri);
            final UploadTask uploadTask = imageRef.putStream(imageStream);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    imageViewWithProgressBar.getProgressBar().setVisibility(View.GONE);
                    imageViewWithProgressBar.getImageView().setBorderWidth(10);
                    imageViewWithProgressBar.getImageView().setBorderColor(ContextCompat.getColor(getContext(),
                            R.color.green));
                    addImageUrlToProduct(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO: handle image upload failure.
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void addImageUrlToProduct(String imageUrl) {
        synchronized(this.product){
            this.product.getImageUrls().add(imageUrl);
            if(product.getImageUrls().size() == numberOfImagesSelected) {
                saveProduct();
            }
        }

    }
}
