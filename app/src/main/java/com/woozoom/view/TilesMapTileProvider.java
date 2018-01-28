package com.woozoom.view;

/**
 * @desc:
 * @author: Jiangcy
 * @datetime: 2018/1/25
 */
public class TilesMapTileProvider {

    private static final double EarthRadius = 6378137;
    public static final double MinLatitude = -85.05112878;
    public static final double MaxLatitude = 85.05112878;
    public static final double MinLongitude = -180;
    public static final double MaxLongitude = 180;
    private static double Clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }
    public static double GroundResolution(double latitude, int levelOfDetail) {
        latitude = Clip(latitude, MinLatitude, MaxLatitude);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EarthRadius / MapSize(levelOfDetail);
    }
    public static long MapSize(int levelOfDetail) {
        return (long) 256 << levelOfDetail;
    }
}
