package com.farmfresh.farmfresh.auth;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;

import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/**
 * Created by pbabu on 8/28/16.
 */
public class LocationResultReceiver extends ResultReceiver {
    private final Context context;
    private final Product product;
    private final String productKey;
    public LocationResultReceiver(Context context, String prodcutKey, Product product) {
        super(null);
        this.context = context;
        this.product = product;
        this.productKey = prodcutKey;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // Show a toast message if an address was found.
        if (resultCode == Constants.FETCH_ADDRESS_SUCCESS_RESULT) {
            Location location = resultData.getParcelable(Constants.RESULT_LOCATION_DATA_KEY);
            final DatabaseReference productsRef = FirebaseDatabase.getInstance()
                    .getReference()
                    .child(Constants.NODE_PRODUCTS);
            //update product location
            GeoFire geoFire = new GeoFire(productsRef);
            geoFire.setLocation(this.productKey,
                    new GeoLocation(location.getLatitude(), location.getLongitude()),
                    new GeoFire.CompletionListener() {
                        @Override
                        public void onComplete(String key, DatabaseError error) {
                            Map<String,Object> productMap = LocationResultReceiver.this.product.toMap();
                            productsRef.child(productKey).updateChildren(productMap);
                        }
                    });
        }else {
            Helper.showToast(context,
                    resultData.getString(Constants.ADDRESS_DATA_EXTRA));
        }
    }

}
