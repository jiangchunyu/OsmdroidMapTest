package osmdroidmaptest.osmdroidmaptest;

import android.util.Log;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

/**
 * @desc:
 * @author: Jiangcy
 * @datetime: 2018/1/23
 */
public class GoogleSatelliteTileSource extends XYTileSource {
    //影像的叠加层 lyrs=h
    static final String[] baseUrl_GoogleLabel = new String[]{
            "http://mt1.google.cn/vt/imgtp=png32&lyrs=h@210000000&hl=en-US&gl=US&src=app&s=G",
            "http://mt2.google.cn/vt/imgtp=png32&lyrs=h@210000000&hl=en-US&gl=US&src=app&s=G",
            "http://mt3.google.cn/vt/imgtp=png32&lyrs=h@210000000&hl=en-US&gl=US&src=app&s=G"
    };

    //矢量底图 lyrs=m  lyrs=是指瓦片类型 有标注  在国内但有偏移，国外暂无测试
    static final String[] baseUrl_GoogleRoad = new String[]{
            "http://mt1.google.cn/vt/lyrs=m@209712068&hl=en-US&gl=US&src=app&s=G",
            "http://mt2.google.cn/vt/lyrs=m@209712068&hl=en-US&gl=US&src=app&s=G",
            "http://mt3.google.cn/vt/lyrs=m@209712068&hl=en-US&gl=US&src=app&s=G"
    };


    //影像底图 lyrs=y  有标注  在国内但有偏移，国外暂无测试
    static final String[] baseUrl_Google_cn = new String[]{
            "http://mt0.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G",
            "http://mt1.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G",
            "http://mt2.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G",
            "http://mt3.google.cn/vt/lyrs=y@126&hl=zh-CN&gl=cn&src=app&s=G"
    };


    //影像底图 lyrs=y  有标注  在国内但有偏移，国外暂无测试
    static final String[] base_map_satellite = new String[]{"http://mt0.google.cn/vt/lyrs=y&gl=CN"};


    public static final String GOOGLE_MAP_SATELLITE_URL =  "http://mt2.google.cn/vt/lyrs=y&gl=CN&x=%d&y=%d&z=%d";//卫星地图
    public static final String GOOGLE_MAP_ROADURL = "http://mt2.google.cn/vt/lyrm=y&gl=CN&x=%d&y=%d&z=%d";//普通地图
    //影像底图 lyrs=s  没有标注
    static final String[] baseUrl_GoogleSatellite = new String[]{
            "http://mt0.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G",
            "http://mt1.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G",
            "http://mt2.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G",
            "http://mt3.google.cn/vt/lyrs=s@126&hl=en-US&gl=US&src=app&s=G"
    };

    String urlXYZ = "&x={$x}&y={$y}&z={$z}";

    public GoogleSatelliteTileSource() {
        super("GoogleSatellite", 3, 19, 256, "jpg", base_map_satellite);
    }

    @Override
    public String getTileURLString(MapTile aTile) {
        String url = getBaseUrl() + urlXYZ
                .replace("{$x}", aTile.getX() + "")
                .replace("{$y}", aTile.getY() + "")
                .replace("{$z}", aTile.getZoomLevel() + "");
        Log.e("jiangcy", "getTileURLString: url "+url );
        return url;
    }
}

