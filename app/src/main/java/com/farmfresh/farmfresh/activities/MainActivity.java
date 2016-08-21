package com.farmfresh.farmfresh.activities;

import android.content.res.Configuration;
import android.os.Bundle;
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

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.fragments.LoginFragment;
import com.farmfresh.farmfresh.fragments.TestFragment;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements LoginFragment.FireBaseLoginListener {

    private final static String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawer;
    private NavigationView mNvView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.layout_main);
        mNvView = (NavigationView) findViewById(R.id.nvView);
        mNvView.setItemIconTintList(null);

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
    public void onLoginFailure() {

    }

    @Override
    public void onLoginSuccess(FirebaseUser user) {
        mCurrentUser = user;
        Log.d(TAG, String.format("onLoginSuccess:fire base login successful:[id:%s, display name:%s]",
                mCurrentUser.getUid(),
                mCurrentUser.getDisplayName()));
        //TODO: update navigation header
        //TODO: update navigation menu items
        //TODO: go to state before login
    }

    @Override
    public void onLogout() {
        //TODO: update navigation header
        //TODO: update navigation menu items
        //TODO: go to landing page
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
        String title;
        Fragment fragment = null;
        String tag = null;
        switch (item.getItemId()) {
            case R.id.menuLogin:
                title = "Login into your account";
                fragment = new LoginFragment();
                tag = LoginFragment.TAG;
                break;
            case R.id.other:
                title = "Other";
                fragment = TestFragment.newInstance(title);
                tag = TestFragment.TAG;
                break;
            default:
                title = "invalid";
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
}
