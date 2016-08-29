package com.farmfresh.farmfresh.google.api;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by pbabu on 8/28/16.
 */
public class FetchAddressFromLocationIntentService extends IntentService {

    public static final String TAG = FetchAddressFromLocationIntentService.class.getSimpleName();
    private ResultReceiver resultReceiver;
    private Geocoder geocoder;
    private Location location;

    public FetchAddressFromLocationIntentService() {
        super("Fetch address from location");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage;
        geocoder = new Geocoder(this, Locale.getDefault());
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        if(location == null) {
            errorMessage = getString(R.string.no_location_provided);
            deliverResultToReceiver(Constants.FETCH_ADDRESS_FAILURE_RESULT, errorMessage, location);
            return;
        }
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage + ". " + e);
        } catch (IllegalArgumentException e){
            errorMessage = getString(R.string.invalid_lat_long_used);
            Log.e(TAG, errorMessage + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), e);
        }
        if(addresses == null || addresses.isEmpty()) {
            errorMessage = getString(R.string.no_address_found);
            deliverResultToReceiver(Constants.FETCH_ADDRESS_FAILURE_RESULT, errorMessage, location);
        }else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<>();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            Log.i(TAG, getString(R.string.no_address_found));
            deliverResultToReceiver(Constants.FETCH_ADDRESS_SUCCESS_RESULT,
                    TextUtils.join(",",
                            addressFragments), location);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, Location location) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_ADDRESS_DATA_KEY, message);
        bundle.putParcelable(Constants.LOCATION_DATA_EXTRA, location);
        resultReceiver.send(resultCode, bundle);
    }
}
