package com.woozoom.view;

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
 * �Զ�����Ƶĵ�ͼ����
 */
public class CustomPointOverlay extends Overlay {

    private final Point mMapCoordsProjected = new Point();
    private final Point mMapCoordsTranslated = new Point();
    protected Paint mCirclePaint = new Paint();
    private GeoPoint mGeoPoint;
    private float mRadius;

    public CustomPointOverlay(GeoPoint mGeoPoint,float radius) {
        this.mGeoPoint=mGeoPoint;
        this.mRadius=radius;
    }



    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

        //��γ�����굽��Ļ�����ת��
        mapView.getProjection().toProjectedPixels(mGeoPoint.getLatitude(), mGeoPoint.getLongitude(), mMapCoordsProjected);
        Projection pj = mapView.getProjection();
        pj.toPixelsFromProjected(mMapCoordsProjected, mMapCoordsTranslated);

        final float radius = mRadius/(float) TileSystem.GroundResolution(mGeoPoint.getLatitude(),
                mapView.getZoomLevel());
//       final float radius = 10L;
        mCirclePaint.setColor(Color.RED);
        mCirclePaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mMapCoordsTranslated.x, mMapCoordsTranslated.y, radius, mCirclePaint);
    }
}
