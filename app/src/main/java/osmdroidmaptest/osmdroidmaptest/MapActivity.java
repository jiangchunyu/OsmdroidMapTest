package osmdroidmaptest.osmdroidmaptest;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.woozoom.data.WayPoint;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

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

        Polyline line = new Polyline();
        line.setWidth(5);
        line.setColor(0xFF1B7BCD);
        line.setPoints(points);
        mMapView.getOverlays().add(line);
        mController.setCenter(points.get(0));
        Marker marker = new Marker(mMapView);
        marker.setIcon(getResources().getDrawable(R.mipmap.ic_launcher));//设置图标
        marker.setPosition(new WayPoint(41.6748053700, 123.4570324500));//设置位置
        marker.setAnchor(0.5f, 0.5f);//设置偏移量
        marker.setTitle("我是Titile");//设置标题
        marker.setSubDescription("我是SubDescription");//设置说明
//        mMapView.getOverlays().add(marker);//添加marker到MapView
    }
    private IMapController mController;
    private SimpleRegisterReceiver mRegisterReceiver;
    private MapTileFilesystemProvider fileSystemProvider;
    private MapTileFileArchiveProvider fileArchiveProvider;
    private GoogleTileSource mGoogleTileSource;
    /**
     * 添加实线区域
     */
    private Overlay addFullArea(List<GeoPoint> pointList, int color) {
        PathOverlay myPath = new PathOverlay(color, this);
        Paint paint = new Paint();
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(color);
//        paint.setAntiAlias(true);
        paint.setPathEffect(null);

        myPath.setPaint(paint);
        for (GeoPoint geoPoint : pointList) {
            myPath.addPoint(geoPoint);
        }
        mMapView.setLayerType(View.LAYER_TYPE_SOFTWARE, paint);//关闭硬件加速(绘制轨迹时需要)
        mMapView.getOverlays().add(myPath);
        mMapView.invalidate();
        return myPath;
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
