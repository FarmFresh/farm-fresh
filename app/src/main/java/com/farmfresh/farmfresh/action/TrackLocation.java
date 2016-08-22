package com.farmfresh.farmfresh.action;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.farmfresh.farmfresh.activities.MainActivity;
import com.farmfresh.farmfresh.helper.OnActivity;
import com.farmfresh.farmfresh.helper.OnClient;
import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.OnPermission;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class TrackLocation implements
        LocationListener,
        OnActivity.Listener,
        OnClient.Listener,
        OnMap.Listener,
        OnPermission.Listener {

    private final LocationRequest mLocationRequest;
    private final Listener[] mListeners;

    private OnActivity.Status mActivityStatus;
    private GoogleApiClient mClient;
    private GoogleMap mGoogleMap;
    private OnPermission.Result mPermissionResult;
    private boolean mIsTracking;

    public TrackLocation(LocationRequest request, Listener... listeners) {
        mLocationRequest = request;
        mListeners = listeners;
    }

    @SuppressWarnings("MissingPermission")
    private void startLocationUpdates() {
        Log.d(MainActivity.TAG, "Requested location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mClient,mLocationRequest,this);
    }

    private void stopLocationUpdates() {
        Log.d(MainActivity.TAG, "Removed location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mClient,this);
    }

    private void check() {
        if (mActivityStatus == OnActivity.Status.RESUMED &&
                mClient != null && mClient.isConnected() &&
                mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED &&
                !mIsTracking) {
            startLocationUpdates();
            mIsTracking = true;
        }

        if (mActivityStatus == OnActivity.Status.PAUSED &&
                mClient != null && mClient.isConnected() &&
                mIsTracking) {
            stopLocationUpdates();
            mIsTracking = false;
        }

        if (mClient == null) {
            // Disconnected
            mIsTracking = false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        for (Listener listener : mListeners) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            listener.accept(mGoogleMap, latLng);
        }
    }

    @Override
    public void onStatus(OnActivity.Status status) {
        mActivityStatus = status;
        check();
    }

    @Override
    public void onSave(Bundle state) {

    }

    @Override
    public void onRestore(Bundle state) {

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

    public interface Listener {
        void accept(GoogleMap map, LatLng location);
    }

}
