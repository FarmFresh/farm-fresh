package com.farmfresh.farmfresh.activities;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.action.AddLocationLayer;
import com.farmfresh.farmfresh.action.AddMarkerOnLongClick;
import com.farmfresh.farmfresh.action.AddToMap;
import com.farmfresh.farmfresh.action.MoveToLocationFirstTime;
import com.farmfresh.farmfresh.action.TrackLocation;
import com.farmfresh.farmfresh.auth.AddressResultReceiver;
import com.farmfresh.farmfresh.auth.EmailPasswordAuthentication;
import com.farmfresh.farmfresh.auth.FacebookAuthentication;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.fragments.EmailPasswordSignUpFragment;
import com.farmfresh.farmfresh.fragments.ListItemsFragment;
import com.farmfresh.farmfresh.fragments.LoginFragment;
import com.farmfresh.farmfresh.helper.OnActivity;
import com.farmfresh.farmfresh.helper.OnClient;
import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.OnPermission;
import com.farmfresh.farmfresh.helper.PlaceManager;
import com.farmfresh.farmfresh.models.MyMarker;
import com.farmfresh.farmfresh.models.Product;
import com.farmfresh.farmfresh.models.User;
import com.farmfresh.farmfresh.utils.Constants;
import com.farmfresh.farmfresh.utils.Helper;
import com.farmfresh.farmfresh.utils.Keys;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FireBaseAuthentication.LoginListener,
        GoogleApiClient.OnConnectionFailedListener, TrackLocation.Listener, GeoQueryEventListener, LoginFragment.SignUpListener {

    public final static String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawer;
    private NavigationView mNvView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;
    private FireBaseAuthentication mFireBaseAuthentication;
    private GoogleAuthentication mGoogleAuthentication;
    private FacebookAuthentication mFacebookAuthentication;
    private EmailPasswordAuthentication emailPasswordAuthentication;
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private TextView mUserDisplayName;
    private TextView mUserEmail;

    private PlaceManager manager;
    private AddMarkerOnLongClick click;
    private AddLocationLayer layer;
    private MoveToLocationFirstTime move;
    private TrackLocation track;
    private ReadyDisplayProduct displayProduct;
    private AddToMap adder;
    private MapFragment supportMapFragment;
    private GeoFire geoFire;
    private GeoQuery geoQuery;
    private Circle searchCircle;

    private static final double EPSILON = 0.0001;

    private GoogleApiClient mGoogleClient;
    private DatabaseReference mFirebaseDatabaseReference;
    private BottomSheetBehavior mBottomSheetBehavior;

    final ArrayList<Product> productList = new ArrayList<Product>();
    //    HashMap<String, Product> productMap = new HashMap<String, Product>();
    ArrayList<PlaceManager.Place> placeList = new ArrayList<PlaceManager.Place>();
    HashMap<String, MyMarker> markers = new HashMap<String, MyMarker>();

    ListItemsFragment listItemsFragment = new ListItemsFragment();

    final StringBuffer queryBuf = new StringBuffer();

    @BindView(R.id.bottom_sheet)
    View bottomSheet;

    @BindView(R.id.ivProductImage)
    ImageView ivProductImage;

    @BindView(R.id.tvProductName)
    TextView tvProductName;

    @BindView(R.id.fabCar)
    FloatingActionButton fabCar;

    @BindView(R.id.tvStreetAddress)
    TextView tvStreetAddress;

    @BindView(R.id.tvCityAddress)
    TextView tvCityAddress;

    @BindView(R.id.tvDuration)
    TextView tvDuration;

    @BindView(R.id.tvDescription)
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

//        new CreateSchema();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.layout_main);
        mNvView = (NavigationView) findViewById(R.id.nvView);
        mNvView.setItemIconTintList(null);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        updateNavigationMenuItems();

        View headerLayout = mNvView.getHeaderView(0);
        mProfileImage = (de.hdodenhof.circleimageview.CircleImageView) headerLayout
                .findViewById(R.id.ivProfileImage);
        mUserDisplayName = (TextView) headerLayout.findViewById(R.id.tvDisplayName);
        mUserEmail = (TextView) headerLayout.findViewById(R.id.tvEmail);
        //setup toolbar as action bar
        setSupportActionBar(mToolbar);

        //setup drawer content
        setupDrawerContent();

        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

        //facebook SDK initialization
        FacebookSdk.sdkInitialize(getApplicationContext(), Constants.RC_FACEBOOK_REQUEST_OFFSET);
        //Facebook app event registration
        AppEventsLogger.activateApp(getApplication());

        //load the home fragment as default
        selectDrawerItem(mNvView.getMenu().findItem(R.id.menuHome));

        mFireBaseAuthentication = new FireBaseAuthentication(this, this);
        mGoogleAuthentication = new GoogleAuthentication(mFireBaseAuthentication, this, this);
        mFacebookAuthentication = new FacebookAuthentication(mFireBaseAuthentication, this);
        emailPasswordAuthentication = new EmailPasswordAuthentication(mFireBaseAuthentication, this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        geoFire = new GeoFire(mFirebaseDatabaseReference.child("products"));

        adder = new AddToMap(getIconGenerator());

        manager = new PlaceManager(adder);
        click = new AddMarkerOnLongClick(this, manager);

        layer = new AddLocationLayer();
        move = new MoveToLocationFirstTime(savedInstanceState);
        track = new TrackLocation(getLocationRequest(), this);
        displayProduct = new ReadyDisplayProduct();

        new OnActivity.Builder(this, manager, track).build();

        mNvView.getMenu().getItem(0).setChecked(true);
        android.app.FragmentManager fragmentManager = getFragmentManager();
        supportMapFragment = new MapFragment();
        if (supportMapFragment != null) {
            getMapAsync(supportMapFragment, new OnMap(manager, click, layer, move, track, displayProduct));
        }
        fragmentManager.beginTransaction().add(R.id.flContent, supportMapFragment).commit();
        setTitle("Home");

        mGoogleClient = getGoogleApiClient();
        addConnectionCallbacks(mGoogleClient, new OnClient(mGoogleClient, move, track));

        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, move, track);
        OnPermission onPermission = new OnPermission.Builder(this).build();
        onPermission.beginRequest(location);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(1);
                    ivProductImage.setVisibility(View.GONE);
                    fabCar.setVisibility(View.INVISIBLE);
                    fabCar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0084B4")));
                    fabCar.setImageResource(R.drawable.car_collapsed);
                    tvDescription.setVisibility(View.GONE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    ivProductImage.setVisibility(View.VISIBLE);
                    fabCar.setVisibility(View.VISIBLE);
                    fabCar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
                    fabCar.setImageResource(R.drawable.car_expanded);
                    tvDescription.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialogFragment bottomSheetDialogFragment = new ListItemsBottomSheetFragment();

                Bundle bundle = new Bundle();
                productList.clear();
                for (String key : productMap.keySet()) {
                    if(markers.get(key).isVisible())
                        productList.add(productMap.get(key));
                }
                bundle.putParcelable("productList", Parcels.wrap(productList));

                bottomSheetDialogFragment.setArguments(bundle);
                bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            }
        });*/
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        int searchImgId = android.support.v7.appcompat.R.id.search_button;
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
//        v.setImageResource(searchImgId);

        final int searchEditId = android.support.v7.appcompat.R.id.search_src_text;
        EditText et = (EditText) searchView.findViewById(searchEditId);
        et.setTextColor(Color.BLACK);
        et.setHintTextColor(Color.BLACK);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                /*placeList.clear();

                for (String key : productMap.keySet()) {
                    Product product = productMap.get(key);
                    if (product.getG() != null && query.equalsIgnoreCase(product.getName())) {
                        placeList.add(new PlaceManager.Place(product.getName(), new LatLng(product.getL().get(0), product.getL().get(1))));
                    }
                }

                displayProduct.populateMap(true);*/
                queryBuf.delete(0, queryBuf.length());
                queryBuf.append(query);

                for (String key : markers.keySet()) {
                    MyMarker myMarker = markers.get(key);
                    if (myMarker.isInRange()) {
                        Product product = (Product) myMarker.getMarker().getTag();
                        if (showMarker(product)) {
                            myMarker.getMarker().setVisible(true);
                        } else {
                            myMarker.getMarker().setVisible(false);
                        }
                    }
                }

                android.app.Fragment fragment = getFragmentManager().findFragmentByTag("listItemsFragment");
                Log.d("fragment==null",(fragment==null)+"");

                if(fragment != null){
                    float[] productDistance = new float[1];
                    productList.clear();
                    for (String key : markers.keySet()) {
                        if (markers.get(key).getMarker().isVisible()) {
//                        Product product = productMap.get(key);
                            Product product = (Product) markers.get(key).getMarker().getTag();
                            float dist = getProductDistance(product, productDistance);
                            product.setDistance(String.format("%.2f", dist) + " mi");
                            productList.add(product);
                        }
                    }
                    listItemsFragment.notifyListUpdate(productList);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() == 0) {
                    onQueryTextSubmit("");
                }
                return true;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
//                Toast.makeText(MainActivity.this, "Closed", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

//        searchItem.expandActionView();
        searchView.requestFocus();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {
        mCurrentUser = user;
        Log.d(TAG, String.format("onLoginSuccess:fire base login successful:[id:%s, display name:%s]",
                mCurrentUser.getUid(),
                mCurrentUser.getDisplayName()));
        updateNavigationHeader();
        updateNavigationMenuItems();
        //goto home page after successful login
        getFragmentManager().popBackStack();
        setTitle("Home");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "google api client connection failed.....");
    }

    public void logout() {
        mFireBaseAuthentication.signOut();
        //logout from facebook
        mFacebookAuthentication.logout();
        mCurrentUser = null;
        updateNavigationHeader();
        updateNavigationMenuItems();
        selectDrawerItem(mNvView.getMenu().findItem(R.id.menuHome));
    }

    private void setupDrawerContent() {
        mNvView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        String title = null;
        Fragment fragment = null;
        String tag = null;
        switch (item.getItemId()) {
            case R.id.menuLogin:
                title = "Login into your account";
                fragment = LoginFragment.newInstance(mGoogleAuthentication, mFacebookAuthentication, emailPasswordAuthentication);
                tag = LoginFragment.TAG;
                break;
            case R.id.menuHome:
                title = "Home";
                fragment = supportMapFragment;
                tag = SupportMapFragment.class.getSimpleName();
                break;
            case R.id.menuProfile:
                title = "Profile";
                Intent intent = new Intent(this, SellerProfileActivity.class);
                intent.putExtra("userId", Parcels.wrap(mCurrentUser.getUid()));
                startActivity(intent);
                break;
            case R.id.menuSelling:
                Intent newProductIntent = new Intent(this, NewProductActivity.class);
                startActivity(newProductIntent);
                return;
            case R.id.menuLogout:
                logout();
                return;
        }

        final FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (fragment != null) {
            fragmentTransaction
                    .replace(R.id.flContent, fragment, tag).
                    addToBackStack(tag).commit();
            getSupportFragmentManager().executePendingTransactions();
            //Highlight the selected item
            item.setChecked(true);
            //Set the toolbar title
            setTitle(title);
            //close the navigation drawer
        }
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
    }

    private void updateNavigationMenuItems() {
        if (mCurrentUser != null) {
            mNvView.getMenu().clear();
            mNvView.inflateMenu(R.menu.drawer_logged_in_view);
        } else {
            mNvView.getMenu().clear();
            mNvView.inflateMenu(R.menu.drawer_logout_view);
        }
    }

    private void updateNavigationHeader() {
        if (mCurrentUser != null) {
            this.mProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(mCurrentUser.getPhotoUrl())
                    .placeholder(R.drawable.user_profile_placeholder)
                    .into(mProfileImage);
            this.mUserDisplayName.setVisibility(View.VISIBLE);
            this.mUserEmail.setVisibility(View.VISIBLE);
            this.mUserDisplayName.setText(mCurrentUser.getDisplayName());
            this.mUserEmail.setText(mCurrentUser.getEmail());

        } else {
            this.mUserDisplayName.setVisibility(View.INVISIBLE);
            this.mUserEmail.setVisibility(View.INVISIBLE);
            this.mProfileImage.setVisibility(View.INVISIBLE);
        }
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
        return new GoogleApiClient.Builder(this).enableAutoManage(MainActivity.this, 1, null).addApi(LocationServices.API).build();
    }

    private void getMapAsync(MapFragment fragment, OnMapReadyCallback callback) {
        fragment.getMapAsync(callback);
    }

    private void addConnectionCallbacks(GoogleApiClient client, GoogleApiClient.ConnectionCallbacks callbacks) {
        client.registerConnectionCallbacks(callbacks);
    }

    class ReadyDisplayProduct implements OnMap.Listener, GoogleMap.OnCameraChangeListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener {
        private GoogleMap map;
        private double radius;

        @Override
        public void onMap(GoogleMap map) {
            this.map = map;
            this.map.setOnCameraChangeListener(this);
            this.map.setOnMapClickListener(this);
            this.map.setOnMarkerClickListener(this);
        }

        public void populateMap(boolean clearMap) {
            if (map != null) {
                if (clearMap) {
                    map.clear();
                }

                for (PlaceManager.Place place : placeList) {
                    adder.addTo(map, place.getmTitle(), place.getmLatLng(), true); // try to animate by setting to true.
                }
            }
        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
//            Log.d("camerachange ", cameraPosition.toString());
            // Update the search criteria for this geoQuery and the circle on the map
            LatLng center = cameraPosition.target;
            double radius = zoomLevelToRadius(cameraPosition.zoom);
            this.radius = radius;
            if (geoQuery != null && searchCircle != null) {
                searchCircle.setCenter(center);
                searchCircle.setRadius(radius);
                geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                geoQuery.setRadius(radius / 1000);
            }
        }

        @Override
        public void onMapClick(LatLng latLng) {
            float[] results = new float[1];
            Location.distanceBetween(geoQuery.getCenter().latitude, geoQuery.getCenter().longitude, latLng.latitude, latLng.longitude, results);
            float distance = results[0];
            float[] productDistance = new float[1];

            if (distance < radius) {
                Log.d("New User Location ", latLng + " distance " + distance);
                Bundle bundle = new Bundle();

                productList.clear();
                for (String key : markers.keySet()) {
                    if (markers.get(key).getMarker().isVisible()) {
//                        Product product = productMap.get(key);
                        Product product = (Product) markers.get(key).getMarker().getTag();
                        float dist = getProductDistance(product, productDistance);
                        product.setDistance(String.format("%.2f", dist) + " mi");
                        productList.add(product);
                    }
                }
                bundle.putParcelable("productList", Parcels.wrap(productList));

                mBottomSheetBehavior.setPeekHeight(1);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                Log.d("bottomsheetstate", mBottomSheetBehavior.getState() + "");

                listItemsFragment.setArguments(bundle);
                android.app.FragmentTransaction ft = getFragmentManager().beginTransaction().setCustomAnimations(
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out);
                ft.replace(R.id.flContent, listItemsFragment,"listItemsFragment").addToBackStack("listItemsFragment");
                ft.commit();
            }
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            mBottomSheetBehavior.setPeekHeight(300);
//            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            fabCar.setVisibility(View.VISIBLE);
            Product product = (Product) marker.getTag();

            ivProductImage.setImageResource(0);
            if (product.getImageUrls() != null && product.getImageUrls().size() != 0) {
                Picasso.with(MainActivity.this)
                        .load(product.getImageUrls().get(0))
                        .into(ivProductImage);
            }
            tvDescription.setText(product.getDescription());
            String address = product.getAddress();
            String streetAddress = address.substring(0,address.indexOf(","));
            String cityAddress = address.substring(address.indexOf(",")+1).trim();

            if(product.getName() != null ) {
                tvProductName.setText(product.getName().toUpperCase());
            }else{
                tvProductName.setText("");
            }
            tvStreetAddress.setText(streetAddress);
            tvCityAddress.setText(cityAddress);

            Log.d("Markerclicked", product.getName() + "");

//            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+User.latLng.latitude+","+User.latLng.longitude+"&destinations="+product.getL().get(0)+","+product.getL().get(1)+"&mode=driving&key="+ Keys.GOOGLE_API_KEY;
//            Log.d("volley-url",url);

            String url = Constants.GOOGLE_DISTANCE.replace(Constants.USER_LATITUDE, User.latLng.latitude + "").replace(Constants.USER_LONGITUDE, User.latLng.longitude + "").replace(Constants.DESTINATION_LATITUDE, product.getL().get(0) + "").replace(Constants.DESTINATION_LONGITUDE, product.getL().get(1) + "").replace(Constants.GOOGLE_API_KEY, Keys.GOOGLE_API_KEY);

            updateDistance(url);

            return false;
        }

        private void updateDistance(String url) {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
//                            Log.d("volley",response);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                jsonObject = jsonObject.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                                String duration = jsonObject.getJSONObject("duration").get("text").toString();
                                String distance = jsonObject.getJSONObject("distance").get("text").toString();
//                                Log.d("volley-duration",duration);
                                tvDuration.setText(duration);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("volley-error", error.toString());
                }
            });
            queue.add(stringRequest);
        }
    }

    @Override
    public void accept(GoogleMap map, LatLng latlng) {
//        Log.d(MainActivity.TAG, "Location update " + latlng + " mylocation " + map.getMyLocation() + "");
        AddressResultReceiver resultReceiver = new AddressResultReceiver(this, null);
        User.latLng = latlng;
        //fetch address for location
        Location location = new Location("");
        location.setLatitude(latlng.latitude);
        location.setLongitude(latlng.longitude);
        hasUserLocationChanged(map, latlng);
        Helper.startFetchAddressIntentService(this, resultReceiver, location);
    }

    private void hasUserLocationChanged(GoogleMap map, LatLng latLng) {
        /*Log.d("isnull", (displayProduct.map.getMyLocation() == null) + "");
        Double latitude = null;
        Double longitude = null;

        if (displayProduct.map.getMyLocation() != null) {
            latitude = displayProduct.map.getMyLocation().getLatitude();
            longitude = displayProduct.map.getMyLocation().getLongitude();
            Log.d("location_ll", latitude + " " + longitude);
        }*/

        if (geoQuery == null) {
//            geoQuery= geoFire.queryAtLocation(new GeoLocation(37.401025,-121.924067), 25);
            geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), 1);
            geoQuery.addGeoQueryEventListener(this);

            searchCircle = displayProduct.map.addCircle(new CircleOptions().center(latLng).radius(1000));
            searchCircle.setFillColor(Color.argb(66, 255, 0, 255));
            searchCircle.setStrokeColor(Color.argb(66, 0, 0, 0));
            searchCircle.setCenter(latLng);
            searchCircle.setRadius(1000);

        } /*else if(geoQuery != null && latitude != null && longitude != null){
            float[] results = new float[1];
            Location.distanceBetween(geoQuery.getCenter().latitude, geoQuery.getCenter().longitude, latitude, longitude, results);
            float distance = (float) 0.0;
            distance = results[0];

            if (distance > (float) 500.0) {
                Log.d("New User Location ", latLng + " distance "+distance);
                geoQuery.setCenter(new GeoLocation(latitude, longitude));
                geoQuery.setRadius(1);
                this.searchCircle.setCenter(latLng);
            }
        }*/
        /*
//        When user is on move, set the cicle's center to user's latlng.
//        Also move the camera to user's location
        this.searchCircle.setCenter(latLng);
        map.moveCamera(CameraUpdateFactory.newCameraPosition(getCameraPosition(latLng)));*/
    }

    private CameraPosition getCameraPosition(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(14).build();
    }

    private float getProductDistance(Product product, float[] results) {
        Location.distanceBetween(User.latLng.latitude, User.latLng.longitude, product.getL().get(0), product.getL().get(1), results);
        return (float) (results[0] / 1600.00);
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation to fit circle into view
        return 16384000 / Math.pow(2, zoomLevel);
    }

    private boolean showMarker(Product product) {
        Log.d("Product-key", product.getId());
        if (product.getName() != null && product.getName().length() != 0) {
            if (queryBuf.length() == 0 || product.getName().toUpperCase().indexOf(queryBuf.toString().toUpperCase()) != -1)
                return true;
        }
        return false;
    }

    @Override
    public void onKeyEntered(final String key, GeoLocation location) {
        Log.d("entered", key + " " + location.latitude + " " + location.longitude);
        if (!markers.containsKey(key)) {
            mFirebaseDatabaseReference.child("products").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Product product = dataSnapshot.getValue(Product.class);
                    product.setId(key);
//                    productMap.put(key, product);
                    MyMarker myMarker = new MyMarker();
                    Marker marker = adder.addTo(displayProduct.map, product.getName(), new LatLng(product.getL().get(0), product.getL().get(1)), true);
                    myMarker.setMarker(marker);
                    myMarker.setInRange(true);
                    marker.setTag(product);
                    if (!showMarker(product)) {
                        marker.setVisible(false);
                    }
                    markers.put(key, myMarker);

                    Log.d("key ", key + " " + product.getId() + " " + product.getG() + " " + product.getName() + " " + product.getL().get(0) + " " + product.getL().get(1));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Marker marker = markers.get(key).getMarker();
            Product product = (Product) marker.getTag();
            markers.get(key).setInRange(true);

            if (showMarker(product)) {
                marker.setVisible(true);
            } else {
                marker.setVisible(false);
            }
        }
    }

    @Override
    public void onKeyExited(String key) {
//        Log.d("exited", key);
        MyMarker myMarker = markers.get(key);
        Marker marker = myMarker.getMarker();
        if (marker != null) {
            marker.setVisible(false);
            myMarker.setInRange(false);
        }
    }

    @Override
    public void onKeyMoved(final String key, GeoLocation location) {
        Log.d("keyMoved", key + " " + location.latitude + " " + location.longitude);
        mFirebaseDatabaseReference.child("products").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                product.setId(key);
                Marker marker = markers.get(key).getMarker();
                /*if (marker != null) {
                    marker.remove();
                    markers.remove(key);
                }*/
                marker.setPosition(new LatLng(product.getL().get(0), product.getL().get(1)));
//                marker = adder.addTo(displayProduct.map, product.getName(), new LatLng(product.getL().get(0), product.getL().get(1)), true);
//                markers.put(key, marker);
                marker.setTag(product);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onGeoQueryReady() {
    }

    @Override
    public void onGeoQueryError(DatabaseError error) {
        Log.d("GeoQueryError", error.toString());
    }

    @Override
    public void onSignup() {
        getFragmentManager().beginTransaction()
                .replace(R.id.flContent, EmailPasswordSignUpFragment.newInstance(this))
                .commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFireBaseAuthentication.addAuthListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFireBaseAuthentication.removeAuthListener();
    }
}
