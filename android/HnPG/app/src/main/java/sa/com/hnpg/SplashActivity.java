package sa.com.hnpg;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import sa.com.hnpg.bean.HLocation;
import sa.com.hnpg.callback.LocationUtilListener;
import sa.com.hnpg.location.LocationUtils;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private LocationUtils locationUtils = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        locationUtils = LocationUtils.getInstance(getApplicationContext(),SplashActivity.this);
        locationUtils.loadPermissions(0,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CALL_PHONE);

        locationUtils.connect(new LocationUtilListener() {
            @Override
            public void onLocationFound(HLocation location) {
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        locationUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }
}
