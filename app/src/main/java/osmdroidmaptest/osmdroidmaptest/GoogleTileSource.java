package osmdroidmaptest.osmdroidmaptest;

import android.util.Log;

import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.tilesource.XYTileSource;

/**
 * @desc:
 * @author: Jiangcy
 * @datetime: 2018/1/23
 */
public class GoogleTileSource extends XYTileSource {
    //普通地图
    static final String[] base_map_roadurl = new String[]{"http://mt0.google.cn/vt/lyrm=y&gl=CN"};



    String urlXYZ = "&x={$x}&y={$y}&z={$z}";

    public GoogleTileSource() {
        super("Google", 3, 19, 256, "png", base_map_roadurl);
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

