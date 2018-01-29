package com.woozoom.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;

/**
 * 自定义绘制的地图坐标
 */
public class CustomPointOverlay extends Overlay {

    private final Point mMapCoordsProjected = new Point();
    private final Point mMapCoordsTranslated = new Point();
    protected Paint mCirclePaint = new Paint();
    GeoPoint mGeoPoint;
    Bitmap  mBitmap;
    float mR;



    public CustomPointOverlay(GeoPoint geoPoint,Bitmap bitmap,float r ) {
        mBitmap=bitmap;
        mR=r;
        mGeoPoint=geoPoint;
    }


    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

        //经纬度坐标到屏幕坐标的转换
        mapView.getProjection().toProjectedPixels(mGeoPoint.getLatitude(), mGeoPoint.getLongitude(), mMapCoordsProjected);
        Projection pj = mapView.getProjection();
        pj.toPixelsFromProjected(mMapCoordsProjected, mMapCoordsTranslated);

           final float radius = mR
                    / (float) TileSystem.GroundResolution(mGeoPoint.getLatitude(),
                    mapView.getZoomLevel());

        mCirclePaint.setColor(Color.BLUE);
        mCirclePaint.setStyle(Paint.Style.FILL);

        canvas.drawBitmap(mBitmap, mMapCoordsTranslated.x, mMapCoordsTranslated.y,mCirclePaint);
        canvas.drawCircle(mMapCoordsTranslated.x, mMapCoordsTranslated.y, radius, mCirclePaint);
    }
}
