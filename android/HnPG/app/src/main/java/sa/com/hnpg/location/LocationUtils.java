package sa.com.hnpg.location;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import sa.com.hnpg.R;
import sa.com.hnpg.bean.HLocation;
import sa.com.hnpg.callback.LocationUtilListener;

/**
 * Created by Ashu on 2/9/2016.
 */
public class LocationUtils implements
        ConnectionCallbacks, OnConnectionFailedListener,LocationListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private Double latitude;
    private Double longitude;
    LocationUtilListener listener;
    private LocationUtils utils = this;
    private Activity calledFromActivity;
    private static final int REQUEST_FINE_LOCATION=0;
    private static final int REQUEST_COARSE_LOCATION=1;

    private String TAG = "LocationUtils";

    public LocationUtils(){
    }

    public LocationUtils(Context context,Activity calledFromActivity){
        this.context = context;
        this.calledFromActivity = calledFromActivity;
        buildGoogleApiClient(context);
    }

    public GoogleApiClient getGoogleApiClient(){
        return this.mGoogleApiClient;
    }

    public HLocation getLocation(){
        return new HLocation(this.latitude,this.longitude);
    }

    public static LocationUtils getInstance(Context context,Activity calledFromActivity){
        LocationUtils utils = new LocationUtils(context,calledFromActivity);
        return utils;
    }
    protected synchronized void buildGoogleApiClient(Context context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public void connect(LocationUtilListener listener){
        this.listener = listener;
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();

                listener.onLocationFound(getLocation());
            } else {
                LocationRequest mLocationRequest = new LocationRequest();
                mLocationRequest.setInterval(1000);
                mLocationRequest.setFastestInterval(500);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                mLocationRequest.setSmallestDisplacement(100.0f);
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, utils);
                Toast.makeText(context, "No Location Detected. Please enable Location Services on your device.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(context, "Found Location from - "+location.getProvider(), Toast.LENGTH_LONG).show();
    }

    public void loadPermissions(int requestCode,String... permissions) {
        for (String perm : permissions) {
            if (ContextCompat.checkSelfPermission(context, perm) != PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(calledFromActivity, perm)) {
                    ActivityCompat.requestPermissions(calledFromActivity, new String[]{perm},requestCode);
                }
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,"Fine Location Access Granted",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"Fine Location Not Access Granted",Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case REQUEST_COARSE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context,"Fine Location Access Granted",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context,"Fine Location Not Access Granted",Toast.LENGTH_SHORT).show();
                }
                return;
            }

        }
    }
}
