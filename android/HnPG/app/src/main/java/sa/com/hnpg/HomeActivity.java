package sa.com.hnpg;


import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.SupportMapFragment;

import sa.com.hnpg.bean.HLocation;
import sa.com.hnpg.callback.LocationUtilListener;
import sa.com.hnpg.fragments.AddLocationFragment;
import sa.com.hnpg.fragments.ViewMyPostsFragment;
import sa.com.hnpg.location.GoogleMapUtils;
import sa.com.hnpg.location.LocationUtils;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    HomeFragment.OnFragmentInteractionListener,AddLocationFragment.OnFragmentInteractionListener,
                    ViewMyPostsFragment.OnFragmentInteractionListener {

    LocationUtils locationUtils;
    GoogleMapUtils googleMapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        locationUtils = LocationUtils.getInstance(getApplicationContext(),HomeActivity.this);
        locationUtils.connect(new LocationUtilListener() {
            @Override
            public void onLocationFound(HLocation location) {
                setFragment(R.id.nav_locate_item, null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_add_item) {
            setFragment(R.id.nav_add_item,item);
        } else if (id == R.id.nav_locate_item) {
            setFragment(R.id.nav_locate_item,item);
        } else if (id == R.id.nav_my_posts) {
            setFragment(R.id.nav_my_posts,item);
        } else if (id == R.id.nav_my_saved_locations) {
            setFragment(R.id.nav_my_saved_locations,item);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void setFragment(int id,MenuItem item){
        try {
            Fragment fragment = null;
            Class fragmentClass;
            switch(id) {
                case R.id.nav_add_item:
                    fragment = (Fragment) AddLocationFragment.class.newInstance();
                    break;
                case R.id.nav_my_posts:
                    fragment = (Fragment) ViewMyPostsFragment.class.newInstance();
                    break;
                case R.id.nav_locate_item:
                    fragment = (Fragment) SupportMapFragment.class.newInstance();
                    googleMapUtils = new GoogleMapUtils(getApplicationContext(),(SupportMapFragment)fragment,locationUtils);
                    break;
                default:
                    fragment = (Fragment) SupportMapFragment.class.newInstance();
                    googleMapUtils = new GoogleMapUtils(getApplicationContext(),(SupportMapFragment)fragment,locationUtils);
                    break;
            }

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            if(item!=null){
                setTitle(item.getTitle());
            }else {
                setTitle("Hostel n PG Locator");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
