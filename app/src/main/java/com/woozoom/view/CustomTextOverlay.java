package com.woozoom.view;

import android.content.Context;
import android.graphics.Canvas;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Overlay;

/**
 * @name: CustomTextIOverlay
 * @author: jiangcy
 * @email: jcy0177@woozoom.net
 * @date: 2018-02-05 17:27
 * @comment:
 */
public class CustomTextOverlay extends Overlay {
    private GeoPoint mFirstPoint;
    private GeoPoint mTwoPoint;
    private MapView mMapView;

    public CustomTextOverlay(Context ctx, MapView mapView) {
        super(ctx);
        mMapView = mapView;
    }

    public CustomTextOverlay(MapView mapView) {
        mMapView = mapView;
    }

    public void setFirstPoint(GeoPoint firstPoint) {
        mFirstPoint = firstPoint;
    }

    public void setTwoPoint(GeoPoint twoPoint) {
        mTwoPoint = twoPoint;
    }

    @Override
    public void draw(Canvas c, MapView osmv, boolean shadow) {
        if (mFirstPoint == null || mTwoPoint == null) {
            return;
        }
        double slope = getSlope(mFirstPoint, mTwoPoint);
        float rotationOnScreen = (float) (mMapView.getMapOrientation() - slope);

    }

    /**
     * 算斜率
     */
    public double getSlope(GeoPoint fromPoint, GeoPoint toPoint) {
        if (toPoint.getLongitude() == fromPoint.getLongitude()) {
            return Double.MAX_VALUE;
        }
        double slope = ((toPoint.getLatitude() - fromPoint.getLatitude()) / (toPoint.getLongitude() - fromPoint.getLongitude()));
        return slope;

    }
}
