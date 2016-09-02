package com.farmfresh.farmfresh.action;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.farmfresh.farmfresh.helper.OnClient;
import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.OnPermission;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MoveToLocationFirstTime implements
        OnMap.Listener,
        OnPermission.Listener,
        OnClient.Listener {

    private final Bundle mSavedInstanceState;

    private GoogleApiClient mClient;
    private GoogleMap mGoogleMap;
    private OnPermission.Result mPermissionResult;

    public MoveToLocationFirstTime(@Nullable Bundle savedInstanceState) {
        mSavedInstanceState = savedInstanceState;
    }

    // TODO C2 Move map to current location
    // Use LocationServices' FusedLocationApi.
    // Get last location.
    // Move map with camera.
    // Use LatLng, CameraUpdateFactory, and getCameraPosition helper method.
    //
    // If you see an error, try changing the annotation to @SuppressWarnings("All").
    // We have already checked that the location permission is granted.
    @SuppressWarnings("MissingPermission")
    private void moveToUserLocation(GoogleApiClient client, GoogleMap map) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(client);
        Log.d("Location==null?",(location==null)+"");
        if(location != null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition(latLng)));
        }
    }

    // TODO C3 Build CameraPosition
    // Use CameraPosition.Builder.
    // Set target, zoom, and tilt
    // Play around with zoom and tilt values for 3d effect.
    private CameraPosition getCameraPosition(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(14).build();
    }

    private void check() {
        if (mSavedInstanceState == null &&
                mClient != null && mClient.isConnected() &&
                mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED) {
            moveToUserLocation(mClient, mGoogleMap);
        }
    }

    @Override
    public void onClient(@Nullable GoogleApiClient client) {
        mClient = client;
        check();
    }

    @Override
    public void onMap(GoogleMap map) {
        mGoogleMap = map;
        check();
    }

    @Override
    public void onResult(int requestCode, OnPermission.Result result) {
        mPermissionResult = result;
        check();
    }

}
