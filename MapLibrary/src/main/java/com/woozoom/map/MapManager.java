package com.woozoom.map;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.tbruyelle.rxpermissions2.RxPermissions;

import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFileArchiveProvider;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * @desc:
 * @author: Jiangcy
 * @datetime: 2018/1/24
 */
public class MapManager {
    private Context mContext;
    private MapView mMapView;
    private RxPermissions mRxPermissions;
    private MapTileProviderArray mMapTileProviderArray;
    private SimpleRegisterReceiver mRegisterReceiver;
    private MapTileFilesystemProvider mTileFilesystemProvider;
    private MapTileFileArchiveProvider fileArchiveProvider;
    private GoogleTileSource mGoogleTileSource;
    //et地图旋转
    private RotationGestureOverlay mRotationGestureOverlay;
    //比例尺
    private ScaleBarOverlay mScaleBarOverlay;
    //指南针方向
    private CompassOverlay mCompassOverlay = null;
    //设置导航图标的位置
    private MyLocationNewOverlay mLocationOverlay;
    public MapManager(@NonNull Activity activity,@NonNull  MapView mapView) {
        mContext = activity;
        mMapView = mapView;
        if(mRxPermissions==null){
            mRxPermissions = new RxPermissions(activity);
        }
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

    private void initMap(){
        mMapView.setDrawingCacheEnabled(true);
        mMapView.setMaxZoomLevel(19);
        mMapView.setMinZoomLevel(3);
        mMapView.getController().setZoom(19);
        //加载谷歌地图，设置地图数据源的形式
//        mMapView.setTileSource(new GoogleTileSource());

        mMapView.setUseDataConnection(true);
        mMapView.setMultiTouchControls(true);// 触控放大缩小
        //是否显示地图数据源
        mMapView.getOverlayManager().getTilesOverlay().setEnabled(true);

        //地图自由旋转
        mRotationGestureOverlay = new RotationGestureOverlay(mMapView);
        mRotationGestureOverlay.setEnabled(false);
        mMapView.getOverlays().add(this.mRotationGestureOverlay);

        //比例尺配置
        final DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mScaleBarOverlay = new ScaleBarOverlay(mMapView);
        mScaleBarOverlay.setCentred(true);
        mScaleBarOverlay.setAlignBottom(true); //底部显示
        mScaleBarOverlay.setScaleBarOffset(dm.widthPixels / 5, 80);
        mMapView.getOverlays().add(this.mScaleBarOverlay);

        //指南针方向
        mCompassOverlay = new CompassOverlay(mContext, new InternalCompassOrientationProvider(mContext),
                mMapView);
        mCompassOverlay.enableCompass();
        mMapView.getOverlays().add(this.mCompassOverlay);

        //设置导航图标
        this.mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(mContext),
                mMapView);
        mMapView.getOverlays().add(this.mLocationOverlay);
        mLocationOverlay.enableMyLocation();  //设置可视
        //指定地图加载提供器
        mRegisterReceiver = new SimpleRegisterReceiver(mContext);

        final TileWriter tileWriter = new TileWriter();
        mTileFilesystemProvider = new MapTileFilesystemProvider(mRegisterReceiver, mGoogleTileSource);

        // Create an archive file modular tile provider
        fileArchiveProvider = new MapTileFileArchiveProvider(mRegisterReceiver, mGoogleTileSource/*, findArchiveFiles()*/);

        // Create a download modular tile provider
        final NetworkAvailabliltyCheck networkAvailabliltyCheck = new NetworkAvailabliltyCheck(mContext);
        final MapTileDownloader downloaderProvider = new MapTileDownloader(mGoogleTileSource, tileWriter, networkAvailabliltyCheck);

        mGoogleTileSource = new GoogleTileSource();
        // Create a custom tile provider array with the custom tile source and  the custom tile providers
//        mMapTileProviderArray = new MapTileProviderArray(mGoogleTileSource, mRegisterReceiver, new MapTileModuleProviderBase[]{mTileFilesystemProvider, fileArchiveProvider, downloaderProvider});
        mMapTileProviderArray = new MapTileProviderArray(mGoogleTileSource, mRegisterReceiver, new MapTileModuleProviderBase[]{downloaderProvider});
        mMapView.getTileProvider().clearTileCache();
//      Log.d("tag", "清理tile缓存........");
        mMapView.setTileProvider(mMapTileProviderArray);
        mMapView.setTileSource(mGoogleTileSource);
//        mMapView.setTilesScaledToDpi(false);//重要
    }
}
