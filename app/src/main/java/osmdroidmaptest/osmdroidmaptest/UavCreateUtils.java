package osmdroidmaptest.osmdroidmaptest;

import android.util.Log;

import com.woozoom.data.WayPoint;
import com.woozoom.uavline.AgriPlanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @desc:
 * @author: Jiangcy
 * @datetime: 2018/1/28
 */
public class UavCreateUtils {
    public static String planTag;
    private AgriPlanUtils.UavCreateLitener mUavCreateLitener = new AgriPlanUtils.UavCreateLitener() {
        @Override
        public void onOutput(int wpCnt, double[] uavLat, double[] uavLon, byte[] wpState) {
            if (mOnOutputUavLine == null) {
                return;
            }
            if (wpCnt > 0) {
                try {
                    ArrayList<WayPoint> uavList = new ArrayList<>();
                    for (int i = 0; i < wpCnt; i++) {
                        WayPoint point= new WayPoint(uavLat[i], uavLon[i]);
                        point.pstate=wpState[i];
                        uavList.add(point);
                    }
                    mOnOutputUavLine.onOntput(uavList);
                } catch (Exception e) {
                    mOnOutputUavLine.onError(e);
                }

            } else {
                mOnOutputUavLine.onFailed(wpCnt);
            }
        }
    };
    public void createUav(int startLine, int startPoint, double width,
                          List<WayPoint> bounderList, List<WayPoint> roundObsList,
                          HashMap<Float, List<WayPoint>> polygonalObsMap) {

        Log.d("jiangcy", "createUav: startEdge "+ startLine);
        Log.d("jiangcy", "createUav: startPoint "+ startPoint);
        Log.d("jiangcy", "createUav: width "+ width);
        double[] mapLat;
        double[] mapLon;
        double[] obsLat;
        double[] obsLon;
        double[] obsR;
        double[] pygLat;
        double[] pygLon;
        double[] pygPnt;
        int mapCnt = 0;
        int obsCnt = 0;
        int pygCnt = 0;
        if (bounderList != null) {
            mapCnt = bounderList.size();
            mapLat = new double[mapCnt];
            mapLon = new double[mapCnt];
            for (int i = 0; i < mapCnt; i++) {
                mapLat[i] = bounderList.get(i).getLatitude();
                mapLon[i] = bounderList.get(i).getLongitude();
            }
            Log.d("jiangcy", "createUav: mapLat "+ Arrays.toString(mapLat));
            Log.d("jiangcy", "createUav: mapLon "+ Arrays.toString(mapLon));

        } else {
            return;
        }

        if (roundObsList != null) {
            obsCnt = roundObsList.size();
            obsLat = new double[obsCnt];
            obsLon = new double[obsCnt];
            obsR = new double[obsCnt];
            for (int i = 0; i < obsCnt; i++) {
                obsLat[i] = roundObsList.get(i).getLatitude();
                obsLon[i] = roundObsList.get(i).getLatitude();
                obsR[i] = roundObsList.get(i).getRadius();
            }
            Log.d("jiangcy", "createUav: obsLat "+ Arrays.toString(obsLat));
            Log.d("jiangcy", "createUav: obsLon "+ Arrays.toString(obsLon));
            Log.d("jiangcy", "createUav: obsR   "+ Arrays.toString(obsR));
        } else {
            obsLat = new double[0];
            obsLon = new double[0];
            obsR = new double[0];
        }
//        polygonalObsMap.clear();
        if (polygonalObsMap != null && polygonalObsMap.size() > 0) {
            pygCnt = polygonalObsMap.size();
            Iterator iter = polygonalObsMap.entrySet().iterator();
            ArrayList<WayPoint> allPygPoints = new ArrayList<>();
            ArrayList<Integer> allPygNum = new ArrayList<>();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                List<WayPoint> wayPoints = (List<WayPoint>) entry.getValue();
                allPygPoints.addAll(wayPoints);
                allPygNum.add(wayPoints.size());
            }
            pygLat = new double[allPygPoints.size()];
            pygLon = new double[allPygPoints.size()];
            for (int i = 0; i < allPygPoints.size(); i++) {
                pygLat[i] = allPygPoints.get(i).getLatitude();
                pygLon[i] = allPygPoints.get(i).getLongitude();
            }
            pygPnt = new double[allPygNum.size()];
            for (int i = 0; i < allPygNum.size(); i++) {
                pygPnt[i] = allPygNum.get(i);
            }
            Log.d("jiangcy", "createUav: pygLat "+ Arrays.toString(pygLat));
            Log.d("jiangcy", "createUav: pygLon "+ Arrays.toString(pygLon));
            Log.d("jiangcy", "createUav: pygPnt "+ Arrays.toString(pygPnt));
        } else {
            pygLat = new double[0];
            pygLon = new double[0];
            pygPnt = new double[0];
        }
        AgriPlanUtils agriPlanUtils = new AgriPlanUtils();
        agriPlanUtils.createUav(mUavCreateLitener, width, mapCnt, mapLat, mapLon, obsCnt, obsLat, obsLon, obsR, pygCnt, pygLat, pygLon, pygPnt, startLine, startPoint);

    }

    private OnOutputUavLine mOnOutputUavLine;

    public void setOnOutputUavLine(OnOutputUavLine onOutputUavLine) {
        mOnOutputUavLine = onOutputUavLine;
    }

    public interface OnOutputUavLine {
        void onFailed(int wpCnt);

        void onError(Exception e);

        void onOntput(ArrayList<WayPoint> uavList);
    }
}
