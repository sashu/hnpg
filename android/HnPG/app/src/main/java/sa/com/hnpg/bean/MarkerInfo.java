package sa.com.hnpg.bean;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Ashu on 3/18/2016.
 */
public class MarkerInfo {

    private double latitude;
    private double longitude;
    private String title;
    private String subtitle;
    private String snippet;
    private float markerIconID = BitmapDescriptorFactory.HUE_RED;

    public MarkerInfo(){}

    public MarkerInfo(double latitude, double longitude, String title, String subtitle, String snippet, float markerIconID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.subtitle = subtitle;
        this.snippet = snippet;
        this.markerIconID = markerIconID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public float getMarkerIconID() {
        return markerIconID;
    }

    public void setMarkerIconID(float markerIconID) {
        this.markerIconID = markerIconID;
    }
}
