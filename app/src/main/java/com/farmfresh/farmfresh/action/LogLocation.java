package com.farmfresh.farmfresh.action;

import android.util.Log;

import com.farmfresh.farmfresh.activities.MainActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;

import java.util.Random;

public class LogLocation implements TrackLocation.Listener {

    private Random mRandom = new Random();
    private Circle mCircle;

    // TODO E4 Log location updates
    // Use MainActivity.TAG
    // Use the map too and get creative.
    // See CircleLocation for an example.
    @Override
    public void accept(GoogleMap map, LatLng location) {
        int radiusInMeters = 500 + mRandom.nextInt(4500);

        /*CircleOptions opts = new CircleOptions()
                .fillColor(Color.YELLOW)
                .strokeWidth(0)
                .radius(radiusInMeters)
                .center(location);
        mCircle = map.addCircle(opts);*/
        Log.d(MainActivity.TAG,"Location update "+location);
    }
}
