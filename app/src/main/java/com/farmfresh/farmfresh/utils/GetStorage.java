package com.farmfresh.farmfresh.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by lucerne on 8/21/16.
 */
public class GetStorage {

    // Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String databaseUrl = "gs://farm-fresh-76e2e.appspot.com/";

    StorageReference storageRef;
    Bitmap bitmap;

    public Bitmap GetPictures() {

        String url = databaseUrl + "products/product-1/images/orange-1.jpeg";

        storageRef = storage.getReferenceFromUrl(url);

        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                bytes.toString();
                bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
        return bitmap;
    }
}
