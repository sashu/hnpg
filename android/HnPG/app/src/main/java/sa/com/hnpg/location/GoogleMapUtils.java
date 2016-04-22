package sa.com.hnpg.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import sa.com.hnpg.bean.HLocation;
import sa.com.hnpg.bean.LocationInfo;
import sa.com.hnpg.bean.MarkerInfo;
import sa.com.hnpg.services.ServiceResponse;

/**
 * Created by Ashu on 3/18/2016.
 */
public class GoogleMapUtils {

    private Context context;
    private GoogleMap googleMap;
    private SupportMapFragment fragment;
    private LocationUtils locationUtils;
    private static float DEFAULT_MARKER_ICON = BitmapDescriptorFactory.HUE_RED;


    public GoogleMapUtils(){}

    public GoogleMapUtils(Context context, SupportMapFragment fragment, LocationUtils locationUtils){
        this.context = context;
        this.fragment = fragment;
        this.locationUtils = locationUtils;
        initilize();
    }

    public void setGoogleMap(GoogleMap googleMap){
        this.googleMap = googleMap;
    }

    public GoogleMap getGoogleMap(){
        return this.googleMap;
    }

    private void initilize() {
        if (googleMap == null) {
            fragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setGoogleMap(googleMap);
                    initiateGoogleMap(locationUtils.getLocation(), 11);
                }
            });

        }
    }

    public void pinLocation(HLocation location,int zoom){
        initiateGoogleMap(location, 11);
    }

    public void initiateGoogleMap(HLocation hlocation,int zoom){
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setRotateGesturesEnabled(true);

        double latitude = hlocation.getLatitude().doubleValue();
        double longitude = hlocation.getLongitude().doubleValue();

        LatLng location = new LatLng(latitude,longitude);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        // create marker
        MarkerOptions marker = new MarkerOptions().position(location);
        // ROSE color icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        marker.title("You are here !!");
        marker.snippet("Scroll on map to find nearby red locators");
        // adding marker
        googleMap.addMarker(marker);

        //addNearbyLocationMarkers(getNearbyLocations());
        new HttpLoadMarkersTask().execute();

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                boolean email = false;
                boolean call = false;
                if (snippet.contains("@")) {
                    email = true;
                } else {
                    call = true;
                }

                if (call) {
                    Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + snippet));
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
                        context.startActivity(i);
                }

                if (email) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setType("plain/text");
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{snippet});
                    intent.putExtra(Intent.EXTRA_SUBJECT, "PG Status Request");
                    intent.putExtra(Intent.EXTRA_TEXT, "Hi " + title + ",");
                    context.startActivity(intent);
                }
            }
        });

//        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//
//            }
//        });

        // check if map is created successfully or not
        if (googleMap == null) {
            Toast.makeText(context,
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private class HttpLoadMarkersTask extends AsyncTask<Void, Void, ServiceResponse> {
        Activity activity = null;

        @Override
        protected ServiceResponse doInBackground(Void... requestBody) {
            try {
                final String url = "http://54.86.143.9:8080/hnpg/service/location/findall.htm";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity greeting = restTemplate.getForEntity(url, ServiceResponse.class);
                return (ServiceResponse)greeting.getBody();
            } catch (Exception e) {
                Toast.makeText(context.getApplicationContext(), "Could not save Location Cuase : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LocationFragment", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ServiceResponse response) {
            if(response == null){
                Toast.makeText(context.getApplicationContext(), "Could not Load Location for Markers", Toast.LENGTH_SHORT).show();
            }else {
                List<MarkerInfo> markers = new ArrayList<MarkerInfo>();
                ArrayList<LinkedHashMap<String,Object>> list = (ArrayList<LinkedHashMap<String,Object>>)response.getData();
                for (LinkedHashMap data:list) {
                    MarkerInfo info = new MarkerInfo();
                    info.setTitle((String) data.get("NAME")+" | "+ (String) data.get("CITY"));
                    LinkedHashMap location  = (LinkedHashMap)data.get("LOCATION");

                    String mobile = (String) data.get("MOBILE");
                    String email = (String) data.get("EMAIL");

                    String snippet = (mobile != null)? mobile!=null ? mobile : "" : email!=null ? email : "";
                    info.setSnippet(snippet);

                    HLocation hloc = new HLocation();
                    if(location!=null) {
                        hloc.setLatitude((Double) location.get("latitude"));
                        hloc.setLongitude((Double) location.get("longitude"));
                    }
                    info.setLatitude(hloc.getLatitude());
                    info.setLongitude((hloc.getLongitude()));

                    markers.add(info);
                }
                addNearbyLocationMarkers(markers);
            }
        }
    }

    public void addNearbyLocationMarkers(List<MarkerInfo> markers){
        for(MarkerInfo markerData:markers ) {
            createMarker(markerData.getLatitude(), markerData.getLongitude(),markerData.getTitle(), markerData.getSnippet(), markerData.getMarkerIconID());
        }
    }

    private void createMarker(double latitude, double longitude, String title, String snippet, float iconResID) {
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(iconResID)));
    }

    public List<MarkerInfo> getNearbyLocations(){
        List<MarkerInfo> markers = new ArrayList<MarkerInfo>();
        markers.add(new MarkerInfo(28.6092504,77.3881294,"Arun Hostels","","8800283747",DEFAULT_MARKER_ICON));
        markers.add(new MarkerInfo(28.6191963,77.3359443,"Shipra PG Makers","","8527489327",DEFAULT_MARKER_ICON));
        markers.add(new MarkerInfo(28.6640918,77.3170616,"Arravalli Hostels","","9958854750",DEFAULT_MARKER_ICON));
        markers.add(new MarkerInfo(28.6613806,77.4317314,"New Students Corner(PG)","","ashu.tushat1@gmail.com",DEFAULT_MARKER_ICON));

        return markers;
    }

}
