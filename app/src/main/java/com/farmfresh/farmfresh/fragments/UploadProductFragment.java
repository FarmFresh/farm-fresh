package com.farmfresh.farmfresh.fragments;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.activities.MainActivity;
import com.farmfresh.farmfresh.databinding.FragmentUploadProductBinding;
import com.farmfresh.farmfresh.fragments.ui.models.ImageViewWithProgressBar;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    List<String> imageUrls = new ArrayList<>();
    private FragmentUploadProductBinding binding;
    private DatabaseReference productsRef;
    private TextView tvName;
    private TextView tvDescription;
    private TextView tvPrice;
    List<ImageView> imageViews = new ArrayList<>(Constants.MAX_PRODUCT_IMAGES);
    List<String> imageUrls = new ArrayList<>();

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
        this.currentUser = auth.getCurrentUser();
        this.product = getArguments().getParcelable(Constants.PRODUCT_KEY);
        for (int i = 0; i < product.getImageUrls().size(); i++) {
            imageUrls.add(product.getImageUrls().get(i));
        }
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
        final ArrayList<String> imageUrls = product.getImageUrls();
        for (int i = 0; i < imageUrls.size(); i++) {
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
    private String saveProductInfo() {
        final String newProductKey = productsRef.push().getKey();
        productsRef.child(newProductKey);
        //clear product dummy urls
        product.getImageUrls().clear();
        productsRef.child(newProductKey)
                .setValue(product)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
        return newProductKey;
    }

    private void saveProductImages(String productKey) {
        final StorageReference imagesRef = storage.getReference()
                .child("products")
                .child(productKey).child("images");
        for (int i = 0; i < imageUrls.size(); i++) {
            uploadImage(imageUrls.get(i), imagesRef, productKey, i + 1,
                    imageViewWithProgressBars.get(i));
        }
    }

    private void saveProductInFireBase() {
        String productId = saveProductInfo();
        saveProductImages(productId);
    }

    private void uploadImage(String imageUrl, StorageReference imagesRef, final String productKey, int count,
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
                    final DatabaseReference imageUrlsRef = productsRef.child(productKey).child("imageUrls");
                    final String imageUrlKey = imageUrlsRef.push().getKey();
                    imageUrlsRef.child(imageUrlKey).setValue(downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //TODO: handle image upload failure.
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageViewWithProgressBar.getProgressBar().setVisibility(View.GONE);
                    imageViewWithProgressBar.getImageView().setBorderWidth(40);
                    imageViewWithProgressBar.getImageView().setBorderColor(ContextCompat.getColor(getContext(),
                            R.color.green));
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
