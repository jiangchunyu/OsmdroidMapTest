package osmdroidmaptest.osmdroidmaptest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.woozoom.data.WayPoint;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.UNSPECIFIED;

/**
 * @name: MapActivity
 * @author: jiangcy
 * @email: jcy0177@woozoom.net
 * @date: 2018-01-25 11:45
 * @comment:
 */
public class MapActivity extends Activity  implements View.OnClickListener {
    private MapView mMapView;
    private static final String TAG = "MapActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        mMapView=findViewById(R.id.mymapview);
        Button button = findViewById(R.id.button1);
        button.setOnClickListener(this);
        initMap();
    }

    private MapTileProviderArray tileProviderArray;
    public void initMap() {
        mMapView = (MapView) findViewById(R.id.mymapview);
        mMapView.setMultiTouchControls(true);
        mMapView.setPressed(true);
        mGoogleTileSource = new GoogleTileSource();
        mMapView.setTileSource(mGoogleTileSource);
//        mapView.getTileProvider().clearTileCache();
        Configuration.getInstance().setTileDownloadThreads((short) 40);
        Log.d(TAG, "initMap: DownloadThreads  "+Configuration.getInstance().getTileDownloadThreads());
        mMapView.setTilesScaledToDpi(true);//重要
//        mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速(绘制轨迹时需要)
        //定位当前的位置，并设置缩放级别
        mController=mMapView.getController();


        //PathOverlay 路线Overlay
        List<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(41.6748053700, 123.4570324500));
        points.add(new GeoPoint(41.6723055956, 123.4734351099));
        points.add(new GeoPoint(41.6673048423, 123.4724051417));
        points.add(new GeoPoint(41.6656187682, 123.4655171166));
        points.add(new GeoPoint(41.6675872651, 123.4556250174));
        points.add(new GeoPoint(41.6694915981, 123.4483078389));
        points.add(new GeoPoint(41.6727430415, 123.4377314542));
        points.add(new GeoPoint(41.6748053700, 123.4570324500));

        /**
         * 画线
         */
        Polyline line = new Polyline();
        line.setWidth(5);
        line.setColor(0xFF1B7BCD);
        line.setPoints(points);
//        mMapView.getOverlays().add(line);
        Polygon polygon = new Polygon();
        polygon.setStrokeWidth(1);
        polygon.setFillColor(0x8032B5EB);
        polygon.setStrokeColor(Color.BLUE);
        polygon.setPoints(points);
        mMapView.getOverlays().add(polygon);
        mMapView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {

            }

            @Override
            public void onViewDetachedFromWindow(View view) {

            }
        });
        mMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });
        mController.setZoom(15);
        mController.setCenter(points.get(0));
        for (int i = 0; i <points.size() ; i++) {
            GeoPoint geoPoint=points.get(i);
            Marker marker = new Marker(mMapView);
            marker.setIcon(LayoutToDrawable(i));//设置图标
            marker.setPosition(geoPoint);//设置位置
            marker.setAnchor(0.5f, 1f);//设置偏移量
            marker.setOnMarkerClickListener(null);
            mMapView.getOverlays().add(marker);//添加marker到MapView
        }

    }
    private IMapController mController;
    private SimpleRegisterReceiver mRegisterReceiver;
    private MapTileFilesystemProvider fileSystemProvider;
    private MapTileFileArchiveProvider fileArchiveProvider;
    private GoogleTileSource mGoogleTileSource;
    public Drawable LayoutToDrawable(int num ){

        LayoutInflater inflator = getLayoutInflater();
        View viewHelp = inflator.inflate( R.layout.marker, null);
        TextView indexView = viewHelp.findViewById(R.id.indexTextView);
        indexView.setText(""+(num+1));
        Bitmap snapshot = convertViewToBitmap(viewHelp);
        Drawable drawable = (Drawable)new BitmapDrawable(snapshot);

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
        Log.d(TAG, "convertViewToBitmap: drawCircle "+view.getMeasuredWidth()+"  view.getMeasuredHeight() "+view.getMeasuredHeight());
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
    public Bitmap convertViewToBitmap(View view,int width,int height ) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, UNSPECIFIED));
        view.layout(0, 0, width, height);
        Log.d(TAG, "convertViewToBitmap: drawCircle "+width+"  view.getMeasuredHeight() "+width);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                //定位当前的位置，并设置缩放级别
                mMapView.getController().setZoom(19);
                mMapView.getController().setCenter(new WayPoint(41.6748053700, 123.4570324500));
                break;
            default:
                break;
        }
    }
}
