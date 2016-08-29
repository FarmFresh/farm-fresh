package com.farmfresh.farmfresh.auth;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by pbabu on 8/28/16.
 */
public class AddressResultReceiver extends ResultReceiver {
    private final Context context;

    public AddressResultReceiver(Context context, Handler handler) {
        super(handler);
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        // Show a toast message if an address was found.
        if (resultCode == Constants.FETCH_ADDRESS_SUCCESS_RESULT) {
            String address = resultData.getString(Constants.RESULT_ADDRESS_DATA_KEY);
            Location location = resultData.getParcelable(Constants.LOCATION_DATA_EXTRA);
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            final FirebaseUser currentUser = auth.getCurrentUser();
            if(currentUser != null) {
                DatabaseReference usersRef = FirebaseDatabase.getInstance()
                        .getReference()
                        .child(Constants.NODE_USERS);
                DatabaseReference currentUserRef = usersRef.child(currentUser.getUid());
                //update user location
                //set geolocation for that user.
                GeoFire geoFire = new GeoFire(currentUserRef);
                geoFire.setLocation("location", new GeoLocation(location.getLatitude(), location.getLongitude()));
                final DatabaseReference currentAddressRef = currentUserRef
                        .child("userCurrentAddress");
                currentAddressRef.setValue(address);
            }
        }else {
            Helper.showToast(context,
                    resultData.getString(Constants.RESULT_ADDRESS_DATA_KEY));
        }
    }

}
