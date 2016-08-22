package com.farmfresh.farmfresh.action;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.PlaceManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class AddMarkerOnLongClick implements OnMap.Listener {

    private final Context mContext;
    private final PlaceManager mPlaceManager;

    public AddMarkerOnLongClick(Context context, PlaceManager manager) {
        mContext = context;
        mPlaceManager = manager;
    }

    @Override
    public void onMap(final GoogleMap map) {
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showAlertDialog(map, latLng);
            }
        });
    }

    private void showAlertDialog(final GoogleMap map, final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("Add Marker");
        builder.setView(R.layout.marker_dialog);

        final AlertDialog dialog = builder.create();

        dialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText titleField = (EditText) dialog.findViewById(R.id.title);
                String title = titleField != null ?
                        titleField.getText().toString() :
                        "Wow!";
                mPlaceManager.addPlace(map, title, latLng);
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        dialog.show();
    }
}
