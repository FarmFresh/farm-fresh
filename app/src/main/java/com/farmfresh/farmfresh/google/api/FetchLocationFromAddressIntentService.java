package com.farmfresh.farmfresh.google.api;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.util.Log;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.utils.Constants;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by pbabu on 8/28/16.
 */
public class FetchLocationFromAddressIntentService extends IntentService {

    public static final String TAG = FetchLocationFromAddressIntentService.class.getSimpleName();
    private ResultReceiver resultReceiver;
    private Geocoder geocoder;
    private String address;

    public FetchLocationFromAddressIntentService() {
        super("Fetch location from address");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String errorMessage;
        geocoder = new Geocoder(this, Locale.getDefault());
        resultReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        address = intent.getStringExtra(Constants.ADDRESS_DATA_EXTRA);
        if(address == null) {
            errorMessage = getString(R.string.no_address_provided);
            deliverResultToReceiver(Constants.FETCH_ADDRESS_FAILURE_RESULT, errorMessage, null);
            return;
        }
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
        } catch (IOException e) {
            errorMessage = getString(R.string.service_not_available);
            Log.e(TAG, errorMessage + ". " + e);
        } catch (IllegalArgumentException e){
            errorMessage = getString(R.string.invalid_address_used);
            Log.e(TAG, errorMessage + ". " + address, e);
        }
        if(addresses == null || addresses.isEmpty()) {
            errorMessage = getString(R.string.no_address_found);
            deliverResultToReceiver(Constants.FETCH_ADDRESS_FAILURE_RESULT, errorMessage, null);
        }else {
            Address resultAddress = addresses.get(0);
            Location location = new Location("");
            location.setLatitude(resultAddress.getLatitude());
            location.setLongitude(resultAddress.getLongitude());
            Log.i(TAG, getString(R.string.address_found));
            deliverResultToReceiver(Constants.FETCH_ADDRESS_SUCCESS_RESULT, address, location);
        }
    }

    private void deliverResultToReceiver(int resultCode, String message, Location location) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.RESULT_LOCATION_DATA_KEY, location);
        bundle.putString(Constants.ADDRESS_DATA_EXTRA, message);
        resultReceiver.send(resultCode, bundle);
    }
}
