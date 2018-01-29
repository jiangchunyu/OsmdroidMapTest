package com.woozoom.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.OverlayWithIW;

import java.util.ArrayList;
import java.util.List;

/**
 * @name: CustomPolygon
 * @author: jiangcy
 * @email: jcy0177@woozoom.net
 * @date: 2018-01-29 13:22
 * @comment:
 */
public class CustomPolygon extends OverlayWithIW {
    private static final String TAG = "CustomPolygon";
    protected OnClickListener mOnClickListener;
    public String tag = "";

    /**
     * inner class holding one ring: the polygon outline, or a hole inside the polygon
     */
    class LinearRing {
        /**
         * original GeoPoints
         */
        int mOriginalPoints[][]; //as an array, to reduce object creation

        /**
         * Stores points, converted to the map projection.
         */
        ArrayList<Point> mConvertedPoints;

        /**
         * is precomputation of points done or not
         */
        boolean mPrecomputed;

        LinearRing() {
            mOriginalPoints = new int[0][2];
            mConvertedPoints = new ArrayList<Point>(0);
            mPrecomputed = false;
        }

        ArrayList<GeoPoint> getPoints() {
            int size = mOriginalPoints.length;
            ArrayList<GeoPoint> result = new ArrayList<GeoPoint>(size);
            for (int i = 0; i < size; i++) {
                GeoPoint gp = new GeoPoint(mOriginalPoints[i][0], mOriginalPoints[i][1]);
                result.add(gp);
            }
            return result;
        }

        void setPoints(final List<GeoPoint> points) {
            int size = points.size();
            mOriginalPoints = new int[size][2];
            mConvertedPoints = new ArrayList<Point>(size);
            int i = 0;
            for (GeoPoint p : points) {
                mOriginalPoints[i][0] = p.getLatitudeE6();
                mOriginalPoints[i][1] = p.getLongitudeE6();
                mConvertedPoints.add(new Point(p.getLatitudeE6(), p.getLongitudeE6()));
                i++;
            }
            mPrecomputed = false;
        }

        /**
         * Note - highly optimized to handle long paths, proceed with care.
         * Should be fine up to 10K points.
         */
        protected void buildPathPortion(Projection pj) {
            final int size = mConvertedPoints.size();
            if (size < 2) // nothing to paint
                return;

            // precompute new points to the intermediate projection.
            if (!mPrecomputed) {
                for (int i = 0; i < size; i++) {
                    final Point pt = mConvertedPoints.get(i);
                    pj.toProjectedPixels(pt.x, pt.y, pt);
                }
                mPrecomputed = true;
            }

            Point projectedPoint0 = mConvertedPoints.get(0); // points from the points list
            Point projectedPoint1;

            Point screenPoint0 = pj.toPixelsFromProjected(projectedPoint0, mTempPoint1); // points on screen
            Point screenPoint1;

            mPath.moveTo(screenPoint0.x, screenPoint0.y);

            for (int i = 0; i < size; i++) {
                // compute next points
                projectedPoint1 = mConvertedPoints.get(i);
                screenPoint1 = pj.toPixelsFromProjected(projectedPoint1, mTempPoint2);

                if (Math.abs(screenPoint1.x - screenPoint0.x) + Math.abs(screenPoint1.y - screenPoint0.y) <= 1) {
                    // skip this point, too close to previous point
                    continue;
                }

                mPath.lineTo(screenPoint1.x, screenPoint1.y);

                // update starting point to next position
                projectedPoint0 = projectedPoint1;
                screenPoint0.x = screenPoint1.x;
                screenPoint0.y = screenPoint1.y;
            }
            mPath.close();
        }

    }

    private LinearRing mOutline;
    private ArrayList<LinearRing> mHoles;

    /**
     * Paint settings.
     */
    protected Paint mFillPaint;
    protected Paint mOutlinePaint;

    private final Path mPath = new Path(); //Path drawn is kept for click detection

    private final Point mTempPoint1 = new Point();
    private final Point mTempPoint2 = new Point();

    // ===========================================================
    // Constructors
    // ===========================================================

    /**
     * Use {@link #CustomPolygon()} instead
     */
    @Deprecated
    public CustomPolygon(final Context ctx) {
        this();
    }

    public CustomPolygon() {
        super();
        mFillPaint = new Paint();
        mFillPaint.setColor(Color.TRANSPARENT);
        mFillPaint.setStyle(Paint.Style.FILL);
        mOutlinePaint = new Paint();
        mOutlinePaint.setColor(Color.BLACK);
        mOutlinePaint.setStrokeWidth(10.0f);
        mOutlinePaint.setStyle(Paint.Style.STROKE);
        mOutlinePaint.setAntiAlias(true);
        mOutline = new LinearRing();
        mHoles = new ArrayList<LinearRing>(0);
        mPath.setFillType(Path.FillType.EVEN_ODD); //for correct support of holes
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public int getFillColor() {
        return mFillPaint.getColor();
    }

    public int getStrokeColor() {
        return mOutlinePaint.getColor();
    }

    public float getStrokeWidth() {
        return mOutlinePaint.getStrokeWidth();
    }

    /**
     * @return the Paint used for the outline. This allows to set advanced Paint settings.
     */
    public Paint getOutlinePaint() {
        return mOutlinePaint;
    }

    /**
     * @return a copy of the list of polygon's vertices.
     */
    public List<GeoPoint> getPoints() {
        return mOutline.getPoints();
    }

    public boolean isVisible() {
        return isEnabled();
    }

    public void setFillColor(final int fillColor) {
        mFillPaint.setColor(fillColor);
    }

    public void setStrokeColor(final int color) {
        mOutlinePaint.setColor(color);
    }

    public void setStrokeWidth(final float width) {
        mOutlinePaint.setStrokeWidth(width);
    }

    public void setVisible(boolean visible) {
        setEnabled(visible);
    }

    /**
     * This method will take a copy of the points.
     */
    public void setPoints(final List<GeoPoint> points) {
        mOutline.setPoints(points);
    }

    public void setHoles(List<? extends List<GeoPoint>> holes) {
        mHoles = new ArrayList<LinearRing>(holes.size());
        for (List<GeoPoint> sourceHole : holes) {
            LinearRing newHole = new LinearRing();
            newHole.setPoints(sourceHole);
            mHoles.add(newHole);
        }
    }

    public List<ArrayList<GeoPoint>> getHoles() {
        ArrayList<ArrayList<GeoPoint>> result = new ArrayList<ArrayList<GeoPoint>>(mHoles.size());
        for (LinearRing hole : mHoles) {
            result.add(hole.getPoints());
        }
        return result;
    }

    /**
     * Build a list of GeoPoint as a circle.
     *
     * @param center         center of the circle
     * @param radiusInMeters
     * @return the list of GeoPoint
     */
    public static ArrayList<GeoPoint> pointsAsCircle(GeoPoint center, double radiusInMeters) {
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>(360 / 6);
        for (int f = 0; f < 360; f += 6) {
            GeoPoint onCircle = center.destinationPoint(radiusInMeters, f);
            circlePoints.add(onCircle);
        }
        return circlePoints;
    }

    /**
     * Build a list of GeoPoint as a rectangle.
     *
     * @param rectangle defined as a BoundingBox
     * @return the list of 4 GeoPoint
     */
    @Deprecated
    public static ArrayList<IGeoPoint> pointsAsRect(BoundingBoxE6 rectangle) {
        ArrayList<IGeoPoint> points = new ArrayList<IGeoPoint>(4);
        points.add(new GeoPoint(rectangle.getLatNorthE6(), rectangle.getLonWestE6()));
        points.add(new GeoPoint(rectangle.getLatNorthE6(), rectangle.getLonEastE6()));
        points.add(new GeoPoint(rectangle.getLatSouthE6(), rectangle.getLonEastE6()));
        points.add(new GeoPoint(rectangle.getLatSouthE6(), rectangle.getLonWestE6()));
        return points;
    }

    /**
     * Build a list of GeoPoint as a rectangle.
     *
     * @param rectangle defined as a BoundingBox
     * @return the list of 4 GeoPoint
     */
    public static ArrayList<IGeoPoint> pointsAsRect(BoundingBox rectangle) {
        ArrayList<IGeoPoint> points = new ArrayList<IGeoPoint>(4);
        points.add(new GeoPoint(rectangle.getLatNorth(), rectangle.getLonWest()));
        points.add(new GeoPoint(rectangle.getLatNorth(), rectangle.getLonEast()));
        points.add(new GeoPoint(rectangle.getLatSouth(), rectangle.getLonEast()));
        points.add(new GeoPoint(rectangle.getLatSouth(), rectangle.getLonWest()));
        return points;
    }

    /**
     * Build a list of GeoPoint as a rectangle.
     *
     * @param center         of the rectangle
     * @param lengthInMeters on longitude
     * @param widthInMeters  on latitude
     * @return the list of 4 GeoPoint
     */
    public static ArrayList<IGeoPoint> pointsAsRect(GeoPoint center, double lengthInMeters, double widthInMeters) {
        ArrayList<IGeoPoint> points = new ArrayList<IGeoPoint>(4);
        GeoPoint east = center.destinationPoint(lengthInMeters * 0.5, 90.0f);
        GeoPoint south = center.destinationPoint(widthInMeters * 0.5, 180.0f);
        double westLon = center.getLongitude() * 2 - east.getLongitude();
        double northLat = center.getLatitude() * 2 - south.getLatitude();
        points.add(new GeoPoint(south.getLatitude(), east.getLongitude()));
        points.add(new GeoPoint(south.getLatitude(), westLon));
        points.add(new GeoPoint(northLat, westLon));
        points.add(new GeoPoint(northLat, east.getLongitude()));
        return points;
    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

        if (shadow) {
            return;
        }

        final Projection pj = mapView.getProjection();
        mPath.rewind();

        mOutline.buildPathPortion(pj);

        for (LinearRing hole : mHoles) {
            hole.buildPathPortion(pj);
        }

        canvas.drawPath(mPath, mFillPaint);
        canvas.drawPath(mPath, mOutlinePaint);
    }

    /**
     * Important note: this function returns correct results only if the Polygon has been drawn before,
     * and if the MapView positioning has not changed.
     *
     * @param event
     * @return true if the Polygon contains the event position.
     */
    public boolean contains(MotionEvent event) {
        if (mPath.isEmpty())
            return false;
        RectF bounds = new RectF(); //bounds of the Path
        mPath.computeBounds(bounds, true);
        Region region = new Region();
        //Path has been computed in #draw (we assume that if it can be clicked, it has been drawn before).
        region.setPath(mPath, new Region((int) bounds.left, (int) bounds.top,
                (int) (bounds.right), (int) (bounds.bottom)));
        return region.contains((int) event.getX(), (int) event.getY());
    }

    @Override
    public boolean onSingleTapConfirmed(final MotionEvent event, final MapView mapView) {
        boolean tapped = contains(event);

        if (tapped) {
            Projection pj = mapView.getProjection();
            GeoPoint eventPos = (GeoPoint) pj.fromPixels((int) event.getX(), (int) event.getY());
            Log.d(TAG, tag+"  onSingleTapConfirmed: position " + eventPos);
            if (mOnClickListener == null) {
                return onClickDefault(this, mapView, eventPos);
            } else {
                return mOnClickListener.onClick(this, mapView, eventPos);
            }
        } else {
            Log.d(TAG, tag+"  onSingleTapConfirmed: tapped " + tapped);
            return tapped;
        }
    }

    @Override
    public void onDetach(MapView mapView) {
        mOutline = null;
        mOnClickListener = null;
        mHoles.clear();
        onDestroy();
    }

    public void setOnClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public interface OnClickListener {
        abstract boolean onClick(CustomPolygon polygon, MapView mapView, GeoPoint eventPos);
    }

    public void showInfoWindow(GeoPoint position) {
        if (mInfoWindow == null)
            return;
        mInfoWindow.open(this, position, 0, 0);
    }

    protected boolean onClickDefault(CustomPolygon polygon, MapView mapView, GeoPoint eventPos) {
        polygon.showInfoWindow(eventPos);
        return true;
    }
}
