package com.farmfresh.farmfresh.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.farmfresh.farmfresh.R;
import com.farmfresh.farmfresh.fragments.TestFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private NavigationView mNvView;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mDrawer = (DrawerLayout)findViewById(R.id.layout_main);
        mNvView = (NavigationView)findViewById(R.id.nvView);

        //setup toolbar as action bar
        setSupportActionBar(mToolbar);

        //setup drawer content
        setupDrawerContent();

        mDrawerToggle = setupDrawerToggle();
        mDrawer.addDrawerListener(mDrawerToggle);

    }

    private void setupDrawerContent(){
        mNvView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    private void selectDrawerItem(MenuItem item){
        String title;
        switch (item.getItemId()) {
            case R.id.googleLogin:
                title = "Google Login";
                break;
            case R.id.other_1:
                title = "Other-1";
                break;
            case R.id.other_2:
                title = "Other-2";
                break;
            default:
                title = "invalid";
        }
        Fragment testFragment = TestFragment.newInstance(title);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.flContent, testFragment)
                .commit();

        //Highlight the selected item
        item.setChecked(true);
        //Set the toolbar title
        setTitle(title);
        //close the navigation drawer
        mDrawer.closeDrawers();
    }

    private ActionBarDrawerToggle setupDrawerToggle(){
        return  new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.drawer_open, R.string.drawer_close);
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
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
