package osmdroidmaptest.osmdroidmaptest;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.woozoom.data.WayPoint;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private MapView mapView;
    //et地图旋转
    private RotationGestureOverlay mRotationGestureOverlay;
    //比例尺
    private ScaleBarOverlay mScaleBarOverlay;
    //指南针方向
    private CompassOverlay mCompassOverlay = null;
    //设置导航图标的位置
    private MyLocationNewOverlay mLocationOverlay;
    private RxPermissions mRxPermissions;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRxPermissions = new RxPermissions(this);
        initView();
    }

    private void initView() {
        findViewById(R.id.button1).setOnClickListener(this);
        mRxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean permission) throws Exception {
                        if (permission) {
                            initMap();
                        }
                    }
                });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            initMap();
        }
    };

    private MapTileProviderArray tileProviderArray;
    public void initMap() {
        mapView = (MapView) findViewById(R.id.mymapview);
        mapView.setDrawingCacheEnabled(true);
        mapView.setMaxZoomLevel(19);
        mapView.setMinZoomLevel(3);
        //加载谷歌地图，设置地图数据源的形式
        mGoogleTileSource = new GoogleTileSource();
        mapView.setTileSource(mGoogleTileSource);

        mapView.setUseDataConnection(true);
        mapView.setMultiTouchControls(true);// 触控放大缩小
        //是否显示地图数据源
        mapView.getOverlayManager().getTilesOverlay().setEnabled(false);

        //地图自由旋转
        mRotationGestureOverlay = new RotationGestureOverlay(mapView);
        mRotationGestureOverlay.setEnabled(false);
        mapView.getOverlays().add(this.mRotationGestureOverlay);

        //比例尺配置
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setAlignBottom(true); //底部显示
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 5, 80);
        mapView.getOverlays().add(this.mScaleBarOverlay);

        //指南针方向
        mCompassOverlay = new CompassOverlay(this, new InternalCompassOrientationProvider(this),
                mapView);
        mCompassOverlay.enableCompass();
        mapView.getOverlays().add(this.mCompassOverlay);

        //设置导航图标
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this),
                mapView);
        mapView.getOverlays().add(this.mLocationOverlay);
        mLocationOverlay.enableMyLocation();  //设置可视
        //指定地图加载提供器
        mRegisterReceiver = new SimpleRegisterReceiver(getApplicationContext());

        final TileWriter tileWriter = new TileWriter();
        fileSystemProvider = new MapTileFilesystemProvider(mRegisterReceiver, mGoogleTileSource);
        // Create an archive file modular tile provider
        fileArchiveProvider = new MapTileFileArchiveProvider(mRegisterReceiver, mGoogleTileSource/*, findArchiveFiles()*/);

        // Create a download modular tile provider
        final NetworkAvailabliltyCheck networkAvailabliltyCheck = new NetworkAvailabliltyCheck(getApplicationContext());
        final MapTileDownloader downloaderProvider = new MapTileDownloader(mGoogleTileSource, tileWriter, networkAvailabliltyCheck);


        // Create a custom tile provider array with the custom tile source and  the custom tile providers
        tileProviderArray = new MapTileProviderArray(mGoogleTileSource, mRegisterReceiver, new MapTileModuleProviderBase[]{fileSystemProvider, fileArchiveProvider, downloaderProvider});
//      Log.d("tag", "清理tile缓存........");
        mapView.setTileProvider(tileProviderArray);
        mapView.getTileProvider().clearTileCache();
        Configuration.getInstance().setTileDownloadThreads((short) 10);
        Log.d(TAG, "initMap: DownloadThreads  "+Configuration.getInstance().getTileDownloadThreads());
        mapView.setTilesScaledToDpi(false);//重要
        //定位当前的位置，并设置缩放级别
        mapView.getController().setZoom(19);
        mapView.getController().setCenter(new WayPoint(41.6748053700, 123.4570324500));
    }
    private SimpleRegisterReceiver mRegisterReceiver;
    private MapTileFilesystemProvider fileSystemProvider;
    private MapTileFileArchiveProvider fileArchiveProvider;
    private GoogleTileSource mGoogleTileSource;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:
                //定位当前的位置，并设置缩放级别
                mapView.getController().setZoom(19);
                mapView.getController().setCenter(new WayPoint(41.6748053700, 123.4570324500));
                break;
            default:
                break;
        }
    }

    //增加图层的形式
    public void initExtendMap() {
        MapTileProviderBasic mapTileProviderBasic =
                new MapTileProviderBasic(this, new GoogleTileSource());
        TilesOverlay tilesOverlay = new TilesOverlay(mapTileProviderBasic, this);
        mapView.getOverlays().add(tilesOverlay);

    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}