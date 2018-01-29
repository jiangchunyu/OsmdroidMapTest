package com.woozoom.data;

import android.location.Location;

import org.osmdroid.util.GeoPoint;

/**
 * @name: WayPoint
 * @author: jiangcy
 * @email: jcy0177@woozoom.net
 * @date: 2018-01-24 17:24
 * @comment:
 */
public class WayPoint extends GeoPoint {
    private Double radius = 5.0;
    public short pstate;//航迹点时为state
    public WayPoint(int aLatitudeE6, int aLongitudeE6) {
        super(aLatitudeE6, aLongitudeE6);
    }

    public WayPoint(int aLatitudeE6, int aLongitudeE6, int aAltitude) {
        super(aLatitudeE6, aLongitudeE6, aAltitude);
    }

    public WayPoint(double aLatitude, double aLongitude) {
        super(aLatitude, aLongitude);
    }

    public WayPoint(double aLatitude, double aLongitude, double aAltitude) {
        super(aLatitude, aLongitude, aAltitude);
    }

    public WayPoint(Location aLocation) {
        super(aLocation);
    }

    public WayPoint(GeoPoint aGeopoint) {
        super(aGeopoint);
    }

    public Double getRadius() {
        return radius;
    }

    public void setRadius(Double radius) {
        this.radius = radius;
    }
}
