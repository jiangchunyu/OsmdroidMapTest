package com.woozoom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
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
    public void draw(Canvas canvas, MapView osmv, boolean shadow) {
        if (mFirstPoint == null || mTwoPoint == null) {
            return;
        }
        GeoPoint centerPoint = new GeoPoint(Math.abs((mFirstPoint.getLatitude() + mTwoPoint.getLatitude()) / 2)
                , Math.abs((mFirstPoint.getLongitude() + mTwoPoint.getLongitude()) / 2));
        Point mPositionPixels = new Point();
        final Projection pj = mMapView.getProjection();
        pj.toPixels(centerPoint, mPositionPixels);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30f);
        canvas.drawText("测试", mPositionPixels.x, mPositionPixels.y, paint);
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

    /**
     * @param lat_a 纬度1
     * @param lng_a 经度1
     * @param lat_b 纬度2
     * @param lng_b 经度2
     * @return
     */
    private double getAngle(double lat_a, double lng_a, double lat_b, double lng_b) {

        double y = Math.sin(lng_b - lng_a) * Math.cos(lat_b);
        double x = Math.cos(lat_a) * Math.sin(lat_b) - Math.sin(lat_a) * Math.cos(lat_b) * Math.cos(lng_b - lng_a);
        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        if (brng < 0)
            brng = brng + 360;
        return brng;

    }
}
