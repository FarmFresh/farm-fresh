package com.farmfresh.farmfresh.fragments;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pbabu on 8/18/16.
 */
public class ProductImagesFragment extends Fragment {
    private Product product;
    private ImageView[] imageViews = new ImageView[Constants.MAX_PRODUCT_IMAGES];
    private List<Uri> mArrayUri;
    private List<Bitmap> mBitmapsSelected;
    private OnSubmitProductImagesListener listener;

    public interface OnSubmitProductImagesListener {
        void withImages(Product product);
    }

    public static ProductImagesFragment newInstance(Product product) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.PRODUCT_KEY, product);
        ProductImagesFragment fragment = new ProductImagesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.product = getArguments().getParcelable(Constants.PRODUCT_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        final View view = inflater.inflate(R.layout.fragment_product_images, container, false);
        Button btnDone = (Button) view.findViewById(R.id.btnDone);
        imageViews[0] = (ImageView) view.findViewById(R.id.ivProductImage1);
        imageViews[1] = (ImageView) view.findViewById(R.id.ivProductImage2);
        imageViews[2] = (ImageView) view.findViewById(R.id.ivProductImage3);
        imageViews[3] = (ImageView) view.findViewById(R.id.ivProductImage4);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.withImages(product);
            }
        });

        Button cameraButton = (Button) getActivity().findViewById(R.id.btnToolBar);
        cameraButton.setText("Gallery");
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCameraLaunch();
            }
        });

        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if(context instanceof OnSubmitProductImagesListener) {
            this.listener = (OnSubmitProductImagesListener)context;
        }else {
            throw new ClassCastException(context.toString()
                    + " must implement ProductImagesFragment.OnSubmitProductImagesListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.listener = null;
    }

    private void onCameraLaunch() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), Constants.PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_PHOTO_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    mArrayUri = new ArrayList<>();
                    mBitmapsSelected = new ArrayList<>();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                        try {
                            final ParcelFileDescriptor input = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                            Bitmap bitmap = Helper
                                    .decodeSampledBitmapFromFileDescriptor(input.getFileDescriptor(),
                                    Constants.PRODUCT_IMAGE_WIDTH,
                                    Constants.PRODUCT_IMAGE_HEIGHT);
                            mBitmapsSelected.add(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                    int maxImagesCount = (imageViews.length <= mBitmapsSelected.size()) ?
                            imageViews.length : mBitmapsSelected.size();
                    if(maxImagesCount > 0) {
                        //some photos are selected
                        ArrayList<String> imageUris = new ArrayList<>();
                        this.product.setImageUrls(imageUris);
                        for (int i = 0; i < maxImagesCount; i++) {
                            Bitmap bitmap = mBitmapsSelected.get(i);
                            imageViews[i].setImageBitmap(bitmap);
                            imageUris.add(mArrayUri.get(i).toString());
                        }
                    }

                }
            }
        }
    }


}
