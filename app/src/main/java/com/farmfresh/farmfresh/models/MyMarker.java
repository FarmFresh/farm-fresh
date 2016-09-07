package com.farmfresh.farmfresh.models;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by bhaskarjaiswal on 9/6/16.
 */
public class MyMarker {
    private Marker marker;
    private boolean isInRange;

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public boolean isInRange() {
        return isInRange;
    }

    public void setInRange(boolean inRange) {
        isInRange = inRange;
    }
}
