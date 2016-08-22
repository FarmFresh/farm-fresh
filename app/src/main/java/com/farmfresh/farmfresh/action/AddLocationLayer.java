package com.farmfresh.farmfresh.action;

import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.OnPermission;
import com.google.android.gms.maps.GoogleMap;

public class AddLocationLayer implements
        OnMap.Listener,
        OnPermission.Listener {

    private GoogleMap mGoogleMap;
    private OnPermission.Result mPermissionResult;

    private void addLayer(GoogleMap map) {
        map.setMyLocationEnabled(true);
    }

    private void check() {
        if (mGoogleMap != null &&
                mPermissionResult == OnPermission.Result.GRANTED) {
            addLayer(mGoogleMap);
        }
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
