package osmdroidmaptest.osmdroidmaptest;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.woozoom.data.WayPoint;
import com.woozoom.view.CustomPointOverlay;
import com.woozoom.view.CustomPolygon;
import com.woozoom.view.TilesMapTileProvider;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.tileprovider.util.StorageUtils;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.TileSystem;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.simplefastpoint.LabelledGeoPoint;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay;
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions;
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.MeasureSpec.UNSPECIFIED;

/**
 * @name: MapActivity
 * @author: jiangcy
 * @email: jcy0177@woozoom.net
 * @date: 2018-01-25 11:45
 * @comment:
 */
public class MapActivity extends Activity {
    private MapView mMapView;
    private static final String TAG = "MapActivity";
    private boolean normalMap = true;
    private TextView text;
    private ArrayList<GeoPoint> mBounderList = new ArrayList<>();
    private ArrayList<GeoPoint> mRoundObsList = new ArrayList<>();
    private HashMap<Float, List<WayPoint>> mPolygonalObsMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        mMapView = findViewById(R.id.mymapview);
        text = findViewById(R.id.text);
        initMap();
    }

    private MapTileProviderArray tileProviderArray;

    public void initMap() {

        mMapView = (MapView) findViewById(R.id.mymapview);
        mMapView.setMultiTouchControls(true);
//        mMapView.setPressed(true);
        setTileProviderArray(this, new GoogleSatelliteTileSource());
        Configuration.getInstance().setTileDownloadThreads((short) 40);
        text.setText("卫星地图");
        Log.d(TAG, "initMap: DownloadThreads  " + Configuration.getInstance().getTileDownloadThreads());
        mMapView.setTilesScaledToDpi(true);//重要
//        mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速(绘制轨迹时需要)
        //定位当前的位置，并设置缩放级别
        mController = mMapView.getController();
        mController.setZoom(mMapView.getTileProvider().getMinimumZoomLevel());
        Log.d(TAG, "initMap: mMapView.getTileProvider().getMinimumZoomLevel() " + mMapView.getTileProvider().getMinimumZoomLevel());
        //PathOverlay 路线Overlay


        //
        mBounderList.add(new WayPoint(41.7093367905, 123.4431846462));
        mBounderList.add(new WayPoint(41.7108024932, 123.4397728763));
        mBounderList.add(new WayPoint(41.7101937901, 123.4381420932));
        mBounderList.add(new WayPoint(41.7094569313, 123.4377343975));
        mBounderList.add(new WayPoint(41.7073344103, 123.4382922969));
        mBounderList.add(new WayPoint(41.7069098977, 123.4392149768));
        mBounderList.add(new WayPoint(41.7070140237, 123.4421332202));
        /**
         * 画线
         */
        Polyline line = new Polyline();
        line.setWidth(5);
        line.setColor(0xFF1B7BCD);
        line.setPoints(mBounderList);
        line.setOnClickListener(new Polyline.OnClickListener() {
            @Override
            public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                Log.d(TAG, "onClick: polyline " + polyline + "   eventPos " + eventPos.getLatitude() + "," + eventPos.getLongitude());
                return false;
            }
        });
        mMapView.getOverlays().add(line);
        CustomPolygon polygon = new CustomPolygon();
        polygon.setStrokeWidth(1);
        polygon.tag = "地块边界";
        polygon.setFillColor(0x8032B5EB);
        polygon.setStrokeColor(Color.BLUE);
        polygon.setPoints(mBounderList);
        mMapView.getOverlays().add(polygon);
        getCenter(mBounderList);
        ArrayList<GeoPoint> testGeos = new ArrayList<>();
        testGeos.add(new GeoPoint(41.7092727153, 123.4419615589));
        testGeos.add(new GeoPoint(41.7093768374, 123.4403415046));
        testGeos.add(new GeoPoint(41.7086079313, 123.4410603366));
        CustomPolygon pbsPolygon = new CustomPolygon();
        pbsPolygon.tag = "多边形障碍物";
        pbsPolygon.setStrokeWidth(1);
        pbsPolygon.setFillColor(0x98FF404A);
        pbsPolygon.setStrokeColor(0xFFFF404D);
        pbsPolygon.setPoints(testGeos);
        mMapView.getOverlays().add(pbsPolygon);
        for (int i = 0; i < mBounderList.size(); i++) {
            GeoPoint geoPoint = mBounderList.get(i);
            Marker marker = new Marker(mMapView);
            marker.setIcon(LayoutToDrawable(i));//设置图标
            marker.setPosition(geoPoint);//设置位置
            marker.setAnchor(0.5f, 1f);//设置偏移量
            marker.setOnMarkerClickListener(null);
            mMapView.getOverlays().add(marker);//添加marker到MapView
        }

        GeoPoint roundPoint = new GeoPoint(41.7079351309, 123.4406419120);
        double t1 = TileSystem.GroundResolution(roundPoint.getLatitude(),
                mMapView.getZoomLevel());
        double t2 = TilesMapTileProvider.GroundResolution(roundPoint.getLatitude(),
                mMapView.getZoomLevel());
        float mRadius = 5;
//        mMapView.getOverlays().add(new CustomPointOverlay(roundPoint, mRadius));
        testRound();
        mController.setCenter(mBounderList.get(0));
        Marker markerTest = new Marker(mMapView);
        LayoutInflater inflator = getLayoutInflater();
        View viewHelp = inflator.inflate(R.layout.marker_hinder, null);
        Drawable drawable = (Drawable) new BitmapDrawable(convertViewToBitmap(viewHelp, 100, 100));
        markerTest.setIcon(getResources().getDrawable(R.drawable.hinder));//设置图标
        markerTest.setPosition(roundPoint);//设置位置
        // setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        markerTest.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);//设置偏移量
        markerTest.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return true;
            }
        });
        mMapView.getOverlays().add(markerTest);//添加marker到MapView
        mOverlayTest = markerTest;
        //createUav();
    }

    private void getCenter(List<GeoPoint> points) {
        if (points.size() > 0) {
            double maxLng = points.get(0).getLongitude();
            double minLng = points.get(0).getLongitude();
            double maxLat = points.get(0).getLatitude();
            double minLat = points.get(0).getLatitude();

            for (int i = 0; i < points.size(); i++) {
                GeoPoint point = points.get(i);
                if (point.getLongitude() > maxLng) {
                    maxLng = point.getLongitude();
                }
                if (point.getLatitude() > maxLat) {
                    maxLat = point.getLatitude();
                }

                if (point.getLongitude() < minLng) {
                    minLng = point.getLongitude();
                }
                if (point.getLatitude() < minLat) {
                    minLat = point.getLatitude();
                }


            }

            double cenLng = ((maxLng) + (minLng)) / 2;
            double cenLat = ((maxLat) + (minLat)) / 2;
            int zoom = getZoom(maxLng, minLng, maxLat, minLat);
        }
    }

    //根据经纬极值计算绽放级别。
    public int  getZoom(double maxLng,double  minLng,double  maxLat,double  minLat) {

      //  var pointA = new BMap.Point(maxLng, maxLat);  // 创建点坐标A
       // var pointB = new BMap.Point(minLng, minLat);  // 创建点坐标B
        //var distance = map.getDistance(pointA, pointB).toFixed(1);  //获取两点距离,保留小数点后两位
        double distance = getSquaredDistanceToPoint(maxLat,maxLng,minLat,minLng);
        for (var i = 0, zoomLen = zoom.length; i < zoomLen; i++) {
            if (zoom[i] - distance > 0) {
                return 18 - i + 3;//之所以会多3，是因为地图范围常常是比例尺距离的10倍以上。所以级别会增加3。
            }
        }
        ;
    }
    public double getSquaredDistanceToPoint(
            final double pFromX, final double pFromY, final double pToX, final double pToY) {
        final double dX = pFromX - pToX;
        final double dY = pFromY - pToY;
        return dX * dX + dY * dY;
    }
    Overlay mOverlayTest;

    private void createUav() {
        UavCreateUtils uavCreateUtils = new UavCreateUtils();
        ArrayList<WayPoint> bounderList = new ArrayList<>();
        ArrayList<WayPoint> roundObsList = new ArrayList<>();
        HashMap<Float, List<WayPoint>> polygonalObsMap = new HashMap<>();
        WayPoint wayPoint = new WayPoint(41.7083516272, 123.4398801647);
        wayPoint.setRadius(5d);
//        roundObsList.add(wayPoint);
        mMapView.getOverlays().add(new CustomPointOverlay(wayPoint, 5));
        for (int i = 0; i < mBounderList.size(); i++) {
            bounderList.add((WayPoint) mBounderList.get(i));
        }
        Log.e(TAG, "createUav: bounderList " + bounderList);
        Log.e(TAG, "createUav: roundObsList " + roundObsList);
        uavCreateUtils.setOnOutputUavLine(new UavCreateUtils.OnOutputUavLine() {
            @Override
            public void onFailed(int wpCnt) {
                Log.e(TAG, "onFailed: wpCnt " + wpCnt);
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, "onFailed: wpCnt " + e);
            }

            @Override
            public void onOntput(ArrayList<WayPoint> uavList) {
                ArrayList<GeoPoint> uavGeoList = new ArrayList<>();
                for (WayPoint wayPoint :
                        uavList) {
                    uavGeoList.add(wayPoint);
                }
                Log.e(TAG, "onOntput: uavList " + uavList);
                Polyline line = new Polyline();
                line.setWidth(3);
                line.setColor(Color.WHITE);
                line.setPoints(uavGeoList);
                mMapView.getOverlays().add(line);

                Polyline line2 = new Polyline();
                line2.setWidth(5);
                line2.setColor(0xFF1B7BCD);
                line2.setPoints(mBounderList);
                line2.setOnClickListener(new Polyline.OnClickListener() {
                    @Override
                    public boolean onClick(Polyline polyline, MapView mapView, GeoPoint eventPos) {
                        Log.d(TAG, "onClick: polyline " + polyline + "   eventPos " + eventPos.getLatitude() + "," + eventPos.getLongitude());
                        return false;
                    }
                });
//                mMapView.getOverlays().add(line2);

            }
        });
        uavCreateUtils.createUav(0, 0, 5, bounderList, roundObsList, polygonalObsMap);
    }


    private void testRound() {
        // wrap them in a theme
        GeoPoint roundPoint = new GeoPoint(41.6691710349, 123.4462479024);

        List<IGeoPoint> points = new ArrayList<>();
        points.add(roundPoint);
        SimplePointTheme pt = new SimplePointTheme(points, false);
        // create label style
        Paint textStyle = new Paint();
        textStyle.setStyle(Paint.Style.FILL);
        textStyle.setColor(Color.parseColor("#0000ff"));
        textStyle.setTextAlign(Paint.Align.CENTER);
        textStyle.setTextSize(24);

        // set some visual options for the overlay
        // we use here MAXIMUM_OPTIMIZATION algorithm, which works well with >100k points
        SimpleFastPointOverlayOptions opt = SimpleFastPointOverlayOptions.getDefaultStyle()
                .setSymbol(SimpleFastPointOverlayOptions.Shape.CIRCLE)
                .setRadius(10).setIsClickable(true).setCellSize(15).setTextStyle(textStyle);

        // create the overlay with the theme
        final SimpleFastPointOverlay sfpo = new SimpleFastPointOverlay(pt, opt);

        // onClick callback
        sfpo.setOnClickListener(new SimpleFastPointOverlay.OnClickListener() {
            @Override
            public void onClick(SimpleFastPointOverlay.PointAdapter points, Integer point) {
                Toast.makeText(mMapView.getContext()
                        , "You clicked " + ((LabelledGeoPoint) points.get(point)).getLabel()
                        , Toast.LENGTH_SHORT).show();
            }
        });

        // add overlay
        mMapView.getOverlays().add(sfpo);
    }


    private IMapController mController;

    public Drawable LayoutToDrawable(int num) {

        LayoutInflater inflator = getLayoutInflater();
        View viewHelp = inflator.inflate(R.layout.marker, null);
        TextView indexView = viewHelp.findViewById(R.id.indexTextView);
        indexView.setText("" + (num + 1));
        Bitmap snapshot = convertViewToBitmap(viewHelp);
        Drawable drawable = (Drawable) new BitmapDrawable(snapshot);

        return drawable;

    }

    /**
     * 创建View 生成 Bitmap
     *
     * @param view
     * @return
     */
    public Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        Log.d(TAG, "convertViewToBitmap: drawCircle " + view.getMeasuredWidth() + "  view.getMeasuredHeight() " + view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 创建View 生成 Bitmap
     *
     * @param view
     * @return
     */
    public Bitmap convertViewToBitmap(View view, int width, int height) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
        view.layout(0, 0, width, height);
        Log.d(TAG, "convertViewToBitmap: drawCircle " + width + "  view.getMeasuredHeight() " + width);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    public void onClickLocal(View v) {
        //定位当前的位置，并设置缩放级别
        GeoPoint geoPoint = new GeoPoint(41.68600799127257, 123.43166780934118);
        Marker marker = new Marker(mMapView);
        marker.setIcon(getResources().getDrawable(R.drawable.bounder_point));//设置图标
        marker.setPosition(geoPoint);//设置位置
        marker.setAnchor(0.5f, 1f);//设置偏移量
        marker.setOnMarkerClickListener(null);
        mMapView.getOverlays().add(marker);//添加marker到MapView
        mMapView.getController().setZoom(18);
        mMapView.getController().setCenter(geoPoint);
    }

    public void onClickChangeLocal(View v) {
        //定位当前的位置，并设置缩放级别
        GeoPoint geoPoint = new GeoPoint(41.68842522475527, 123.43778637349602);
        Marker marker = new Marker(mMapView);
        marker.setIcon(getResources().getDrawable(R.drawable.bounder_start_point));//设置图标
        marker.setPosition(geoPoint);//设置位置
        marker.setAnchor(0.5f, 1f);//设置偏移量
        marker.setOnMarkerClickListener(null);
        mMapView.getOverlays().add(marker);//添加marker到MapView
        mMapView.getController().setZoom(18);
        mMapView.getController().setCenter(geoPoint);
    }

    public void onClickNarmol(View v) {
        text.setText("普通地图");
        normalMap = true;
        setTileProviderArray(this, new GoogleTileSource());
    }


    public void onClickTest(View view) {
        if (!(mOverlayTest instanceof Marker)) {
            return;
        }
        GeoPoint point = new GeoPoint(41.7083749127, 123.4455674201);
        LayoutInflater inflator = getLayoutInflater();
        View viewHelp = inflator.inflate(R.layout.marker_hinder, null);
        ImageView imageView = viewHelp.findViewById(R.id.image);
        imageView.setImageResource(R.drawable.bounder_start_point);
        Drawable drawable = (Drawable) new BitmapDrawable(convertViewToBitmap(viewHelp, 100, 100));
        Marker markerTest = (Marker) mOverlayTest;
        markerTest.setIcon(getResources().getDrawable(R.drawable.bounder_start_point));//设置图标
        markerTest.setPosition(point);//设置位置
        // setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
        markerTest.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);//设置偏移量
        markerTest.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                return true;
            }
        });
        mMapView.postInvalidate();
    }

    public void onClickSatellite(View v) {
        text.setText("卫星地图");
        normalMap = false;
        setTileProviderArray(this, new GoogleSatelliteTileSource());
    }


    private void setTileProviderArray(Context context, XYTileSource xyTileSource) {
        //指定地图加载提供器
        SimpleRegisterReceiver mRegisterReceiver = new SimpleRegisterReceiver(context);
        Configuration.getInstance().setOsmdroidBasePath(new File(StorageUtils.getStorage().getAbsolutePath(),
                "woozoom"));
        final TileWriter tileWriter = new TileWriter();
        MapTileFilesystemProvider mTileFilesystemProvider = new MapTileFilesystemProvider(mRegisterReceiver, xyTileSource);

        // Create an archive file modular tile provider
        MapTileFileArchiveProvider fileArchiveProvider = new MapTileFileArchiveProvider(mRegisterReceiver, xyTileSource/*, findArchiveFiles()*/);

        // Create a download modular tile provider
        final NetworkAvailabliltyCheck networkAvailabliltyCheck = new NetworkAvailabliltyCheck(context);
        final MapTileDownloader downloaderProvider = new MapTileDownloader(xyTileSource, tileWriter, networkAvailabliltyCheck);
        // Create a custom tile provider array with the custom tile source and  the custom tile providers
        MapTileProviderArray mMapTileProviderArray = new MapTileProviderArray(xyTileSource, mRegisterReceiver, new MapTileModuleProviderBase[]{mTileFilesystemProvider, fileArchiveProvider, downloaderProvider});
//        mMapTileProviderArray = new MapTileProviderArray(xyTileSource, mRegisterReceiver, new MapTileModuleProviderBase[]{downloaderProvider});
        mMapView.getTileProvider().clearTileCache();
//      Log.d("tag", "清理tile缓存........");
        mMapView.setTileProvider(mMapTileProviderArray);
        mMapView.setTileSource(xyTileSource);
    }
}
