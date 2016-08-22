package com.farmfresh.farmfresh.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import com.farmfresh.farmfresh.auth.FireBaseAuthentication;
import com.farmfresh.farmfresh.auth.GoogleAuthentication;
import com.farmfresh.farmfresh.fragments.HomeFragment;
import com.farmfresh.farmfresh.fragments.LoginFragment;
import com.farmfresh.farmfresh.fragments.ProfileFragment;
import com.farmfresh.farmfresh.fragments.SellingFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements FireBaseAuthentication.LoginListener,
        GoogleApiClient.OnConnectionFailedListener{

    private final static String TAG = MainActivity.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        startActivity(new Intent(this, MapsActivity.class));

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
                fragment = new HomeFragment();
                tag = HomeFragment.class.getSimpleName();
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
        fragmentTransaction
                .replace(R.id.flContent, fragment,tag);
        fragmentTransaction.commit();
        getSupportFragmentManager().executePendingTransactions();
        //Highlight the selected item
        item.setChecked(true);
        //Set the toolbar title
        setTitle(title);
        //close the navigation drawer
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
}
