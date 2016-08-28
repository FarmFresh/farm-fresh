package com.farmfresh.farmfresh.action;

import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;

public class AddToMap {

    private IconGenerator mIconGenerator;

    public AddToMap(IconGenerator generator) {
        mIconGenerator = generator;
    }

    public Marker addTo(GoogleMap map, String title, LatLng latLng, boolean animate) {
        Bitmap bitmap = mIconGenerator.makeIcon(title);
        MarkerOptions opts = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));
        Marker marker = map.addMarker(opts);
        if (animate) {
            animate(map, marker);
        }
        return marker;
    }

    private void animate(GoogleMap map, final Marker marker) {
        final LatLng target = marker.getPosition();
        Projection projection = map.getProjection();
        Point endPoint = projection.toScreenLocation(target);
        Point startPoint = new Point(endPoint.x, 0);
        final LatLng offscreen = projection.fromScreenLocation(startPoint);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator an) {
                float value = (float) an.getAnimatedValue();
                double fraction = Float.valueOf(value).doubleValue();
                LatLng latLng = SphericalUtil.interpolate(offscreen, target, fraction);
                marker.setPosition(latLng);
            }
        });

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(2500);
        animator.start();
    }

}
