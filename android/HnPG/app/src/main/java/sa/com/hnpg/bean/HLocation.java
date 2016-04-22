package sa.com.hnpg.bean;

import java.io.Serializable;

/**
 * Created by Ashu on 2/9/2016.
 */
public class HLocation implements Serializable{

    private Double longitude;
    private Double latitude;

    public HLocation(){}

    public HLocation(Double latitude,Double longitude){
        this.latitude = latitude;
        this.longitude=longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
