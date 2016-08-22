package com.farmfresh.farmfresh.activities;

import android.Manifest;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.action.AddLocationLayer;
import com.farmfresh.farmfresh.action.AddMarkerOnLongClick;
import com.farmfresh.farmfresh.action.AddToMap;
import com.farmfresh.farmfresh.action.LogLocation;
import com.farmfresh.farmfresh.action.MoveToLocationFirstTime;
import com.farmfresh.farmfresh.action.TrackLocation;
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.fragments.ListItemsBottomSheetFragment;
import com.farmfresh.farmfresh.fragments.LoginFragment;
import com.farmfresh.farmfresh.fragments.ProfileFragment;
import com.farmfresh.farmfresh.fragments.SellingFragment;
import com.farmfresh.farmfresh.helper.OnActivity;
import com.farmfresh.farmfresh.helper.OnClient;
import com.farmfresh.farmfresh.helper.OnMap;
import com.farmfresh.farmfresh.helper.OnPermission;
import com.farmfresh.farmfresh.helper.PlaceManager;
import com.farmfresh.farmfresh.models.Product;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseUser;
import com.google.maps.android.ui.IconGenerator;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements FireBaseAuthentication.LoginListener,
        GoogleApiClient.OnConnectionFailedListener{

    public final static String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawer;
    private NavigationView mNvView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;
    private FireBaseAuthentication mFireBaseAuthentication;
    private GoogleAuthentication mGoogleAuthentication;
    private de.hdodenhof.circleimageview.CircleImageView mProfileImage;
    private TextView mUserDisplayName;
    private TextView mUserEmail;

    private PlaceManager manager;
    private AddMarkerOnLongClick click;
    private AddLocationLayer layer;
    private MoveToLocationFirstTime move;
    private TrackLocation track;
    private SupportMapFragment supportMapFragment;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.layout_main);
        mNvView = (NavigationView) findViewById(R.id.nvView);
        mNvView.setItemIconTintList(null);
        updateNavigationMenuItems();

        View headerLayout = mNvView.getHeaderView(0);
        mProfileImage = (de.hdodenhof.circleimageview.CircleImageView)headerLayout
                        .findViewById(R.id.ivProfileImage);
        mUserDisplayName = (TextView)headerLayout.findViewById(R.id.tvDisplayName);
        mUserEmail = (TextView)headerLayout.findViewById(R.id.tvEmail);
        //setup toolbar as action bar
        setSupportActionBar(mToolbar);

        //setup drawer content
        setupDrawerContent();

        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

        //facebook SDK initialization
        FacebookSdk.sdkInitialize(getApplicationContext());
        //Facebook app event registration
        AppEventsLogger.activateApp(getApplication());

        //load the home fragment as default
        selectDrawerItem(mNvView.getMenu().findItem(R.id.menuHome));

        mFireBaseAuthentication = new FireBaseAuthentication(this);
        mGoogleAuthentication = new GoogleAuthentication(mFireBaseAuthentication, this, this);

        AddToMap adder = new AddToMap(getIconGenerator());

        ArrayList<PlaceManager.Place> placeList = new ArrayList<PlaceManager.Place>();

        placeList.add(new PlaceManager.Place("Orange",new LatLng(65.9677,-18.5343)));
        placeList.add(new PlaceManager.Place("Mango",new LatLng(65.9657,-18.5323)));
        placeList.add(new PlaceManager.Place("Apple",new LatLng(65.9647,-18.5313)));
        placeList.add(new PlaceManager.Place("Banana",new LatLng(65.9637,-18.5303)));
        placeList.add(new PlaceManager.Place("Kiwi",new LatLng(65.9627,-18.5293)));

        manager = new PlaceManager(adder,placeList);
        click = new AddMarkerOnLongClick(this, manager);

        layer = new AddLocationLayer();
        move = new MoveToLocationFirstTime(savedInstanceState);
        track = new TrackLocation(getLocationRequest(), new LogLocation());

        new OnActivity.Builder(this, manager, track).build();

        /*FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment != null) {
            getMapAsync(fragment, new OnMap(manager, click, layer, move, track));
        }*/

        mNvView.getMenu().getItem(0).setChecked(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        supportMapFragment = new SupportMapFragment();
        if (supportMapFragment != null) {
            getMapAsync(supportMapFragment, new OnMap(manager, click, layer, move, track));
        }
        fragmentManager.beginTransaction().replace(R.id.flContent, supportMapFragment).commit();
        setTitle("Home");

        GoogleApiClient mGoogleClient = getGoogleApiClient();
        addConnectionCallbacks(mGoogleClient, new OnClient(mGoogleClient, move, track));

        int requestCode = 1001;
        String permission = Manifest.permission.ACCESS_FINE_LOCATION;
        OnPermission.Request location = new OnPermission.Request(requestCode, permission, layer, move, track);
        OnPermission onPermission = new OnPermission.Builder(this).build();
        onPermission.beginRequest(location);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        selectDrawerItem(mNvView.getMenu().findItem(R.id.menuHome));
        //TODO: go to state before login
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "google api client connection failed.....");
    }

    public void logout() {
        mFireBaseAuthentication.signOut();
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
                fragment = LoginFragment.newInstance(mGoogleAuthentication);
                tag = LoginFragment.TAG;
                break;
            case R.id.menuHome:
                title = "Home";
                fragment = supportMapFragment;
                tag = SupportMapFragment.class.getSimpleName();
                break;
            case R.id.menuProfile:
                title = "Profile";
                fragment = new ProfileFragment();
                tag = ProfileFragment.class.getSimpleName();
                break;
            case R.id.menuSelling:
                title = "Selling";
                fragment = new SellingFragment();
                tag = SellingFragment.class.getSimpleName();
                break;
            case R.id.menuLogout:
                logout();
                return;
        }

        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null ){
            fragmentTransaction
                    .replace(R.id.flContent, fragment,tag);
            fragmentTransaction.commit();
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
        if(mCurrentUser != null) {
            mNvView.getMenu().clear();
            mNvView.inflateMenu(R.menu.drawer_logged_in_view);
        }else {
            mNvView.getMenu().clear();
            mNvView.inflateMenu(R.menu.drawer_logout_view);
        }
    }

    private void updateNavigationHeader() {
        if(mCurrentUser != null) {
            this.mProfileImage.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(mCurrentUser.getPhotoUrl())
                    .placeholder(R.drawable.user_profile_placeholder)
                    .into(mProfileImage);
            this.mUserDisplayName.setVisibility(View.VISIBLE);
            this.mUserEmail.setVisibility(View.VISIBLE);
            this.mUserDisplayName.setText(mCurrentUser.getDisplayName());
            this.mUserEmail.setText(mCurrentUser.getEmail());

        }else {
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
        return new GoogleApiClient.Builder(this).addApi(LocationServices.API).build();
    }

    private void getMapAsync(SupportMapFragment fragment, OnMapReadyCallback callback) {
        fragment.getMapAsync(callback);
    }

    private void addConnectionCallbacks(GoogleApiClient client, GoogleApiClient.ConnectionCallbacks callbacks) {
        client.registerConnectionCallbacks(callbacks);
    }
}
