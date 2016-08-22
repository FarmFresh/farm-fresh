package com.farmfresh.farmfresh.activities;

import android.Manifest;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.action.AddLocationLayer;
import com.farmfresh.farmfresh.action.AddMarkerOnLongClick;
import com.farmfresh.farmfresh.action.AddToMap;
import com.farmfresh.farmfresh.action.LogLocation;
import com.farmfresh.farmfresh.action.MoveToLocationFirstTime;
import com.farmfresh.farmfresh.action.TrackLocation;
import com.farmfresh.farmfresh.fragments.ListItemsBottomSheetFragment;
import com.farmfresh.farmfresh.helper.OnActivity;
import com.farmfresh.farmfresh.helper.OnClient;
import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.OnPermission;
import com.farmfresh.farmfresh.helper.PlaceManager;
import com.farmfresh.farmfresh.models.Product;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.ui.IconGenerator;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends AppCompatActivity {

    public static final String TAG = MapsActivity.class.getSimpleName();
    private BottomSheetBehavior mBottomSheetBehavior;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.bottom_sheet)
    NestedScrollView bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        /*if (savedInstanceState == null) {
            Toast.makeText(this, "Long click on map to add marker", Toast.LENGTH_LONG).show();
        }*/

        AddToMap adder = new AddToMap(getIconGenerator());

        ArrayList<PlaceManager.Place> placeList = new ArrayList<PlaceManager.Place>();

        placeList.add(new PlaceManager.Place("Orange",new LatLng(65.9667,-18.5333)));
        placeList.add(new PlaceManager.Place("Mango",new LatLng(65.9657,-18.5323)));
        placeList.add(new PlaceManager.Place("Apple",new LatLng(65.9647,-18.5313)));
        placeList.add(new PlaceManager.Place("Banana",new LatLng(65.9637,-18.5303)));
        placeList.add(new PlaceManager.Place("Kiwi",new LatLng(65.9627,-18.5293)));

        PlaceManager manager = new PlaceManager(adder,placeList);
        AddMarkerOnLongClick click = new AddMarkerOnLongClick(this, manager);

        AddLocationLayer layer = new AddLocationLayer();
        MoveToLocationFirstTime move = new MoveToLocationFirstTime(savedInstanceState);
        TrackLocation track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, manager, track).build();

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            getMapAsync(fragment, new OnMap(manager, click, layer, move, track));
        }

        GoogleApiClient client = getGoogleApiClient();
        addConnectionCallbacks(client, new OnClient(client, move, track));

        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, move, track);
        OnPermission onPermission = new OnPermission.Builder(this).build();
        onPermission.beginRequest(location);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MapsActivity.this,"fab clicked",Toast.LENGTH_SHORT).show();
                BottomSheetDialogFragment bottomSheetDialogFragment = new ListItemsBottomSheetFragment();

                Product product = new Product();
                List<Product> list = new ArrayList<Product>();

                product.setName("Home Grown Orange");
                product.setDescription("In Europe and America, surveys show that orange is the colour most associated with amusement, the unconventional, extroverts, warmth, fire, energy, activity, danger, taste and aroma, the autumn season, and Protestantism, as well as having long been the national colour of the Netherlands and the House of Orange.");
                product.setPrice("$5 per dozen");
                list.add(product);

                product = new Product();
                product.setName("Farm Grown Orange");
                product.setDescription("In ancient Egypt artists used an orange mineral pigment called realgar for tomb paintings");
                product.setPrice("$50 per dozen");
                list.add(product);

                product = new Product();
                product.setName("Orange");
                product.setDescription("Home grown california oranges");
                product.setPrice("$40 per dozen");
                list.add(product);

                product = new Product();
                product.setName("Mango");
                product.setDescription("The mango is a juicy stone fruit (drupe) belonging to the genus Mangifera, consisting of numerous tropical fruiting trees, cultivated mostly for edible fruit. The majority of these species are found in nature as wild mangoes");
                product.setPrice("$30 per dozen");
                list.add(product);

                product = new Product();
                product.setName("Banana");
                product.setDescription("The banana is an edible fruit, botanically a berry,[1][2] produced by several kinds of large herbaceous flowering plants in the genus Musa.[3] In some countries, bananas used for cooking may be called plantains.");
                product.setPrice("$20 per dozen");
                list.add(product);

                product = new Product();
                product.setName("Apple");
                product.setDescription("Apple Inc. is an American multinational technology company headquartered in Cupertino, California, that designs, develops, and sells consumer electronics, computer software, and online services.");
                product.setPrice("$10 per dozen");
                list.add(product);

                Bundle bundle = new Bundle();
                bundle.putParcelable("productList", Parcels.wrap(list));

                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            }
        });
    }

    private IconGenerator getIconGenerator() {
        IconGenerator iconGenerator = new IconGenerator(this);
        iconGenerator.setStyle(IconGenerator.STYLE_GREEN);
        iconGenerator.setTextAppearance(R.style.MarkerFont);
        return iconGenerator;
    }

    private LocationRequest getLocationRequest() {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);        // 10 seconds
        request.setFastestInterval(5000);  // 5 seconds
        return request;
    }

    private GoogleApiClient getGoogleApiClient() {
        return new GoogleApiClient.Builder(this).enableAutoManage(this,null).addApi(LocationServices.API).build();
    }

    private void getMapAsync(SupportMapFragment fragment, OnMapReadyCallback callback) {
        fragment.getMapAsync(callback);
    }

    private void addConnectionCallbacks(GoogleApiClient client, GoogleApiClient.ConnectionCallbacks callbacks) {
        client.registerConnectionCallbacks(callbacks);
    }
}
