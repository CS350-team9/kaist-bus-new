package kr.ac.kaist.kyotong.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


/**
 * Google Maps API 관련 각종 유틸리티 함수를 모아놓은 클래스
 * <p>
 * TODO: Cohesion level이 낮으므로 나중에 다른 장소로 옮기고 가급적이면 클래스는 삭제할 것
 */
public final class GoogleMapsUtils {
    /**
     * 꺾은선으로 이루어진 경로를 따라 일정한 거리만큼 이동했을 때의 좌표를 구한다.
     *
     * <p>{@code points}에 점이 1개만 있으면 그 점의 좌표를 돌려주고, 비어 있으면 (0.0, 0.0)를 돌려준다.</p>
     * <p>{@code fraction}이 0보다 작으면 첫 번째 꼭짓점을 돌려주고, 1보다 크면 마지막 꼭짓점을 돌려준다.</p>
     *
     * @param points   꺾은선 경로를 구성하는 꼭짓점의 좌표 목록
     * @param fraction 경로를 따라 이동한 거리 / 경로의 전체 길이
     * @return {@code fraction}만큼 이동한 후 도달한 좌표
     */
    public static LatLng interpolate(List<LatLng> points, double fraction) {
        if (points.size() == 0)
            return new LatLng(0, 0);
        else if (points.size() == 1)
            return points.get(0);

        double totalDistance = 0;
//        ArrayList<Double> cumulativeDistances = new ArrayList<Double>(points.size());
        ArrayList<Double> cumulativeDistances = new ArrayList<>(Arrays.asList(new Double[points.size()]));

        cumulativeDistances.set(0, 0.0);

        for (int i = 0; i < points.size() - 1; ++i) {
            totalDistance += SphericalUtil.computeDistanceBetween(points.get(i), points.get(i + 1));
            cumulativeDistances.set(i, totalDistance);
        }

        int pointIndex = Collections.binarySearch(cumulativeDistances, fraction * totalDistance);
        if (pointIndex >= 0)    //정확한 일치
            return points.get(pointIndex);
        else {                  //일치하지 않음
            int endPointIndex = (-pointIndex) - 1;

            if (endPointIndex == 0)                     //fraction < 0.0
                return points.get(0);
            else if (endPointIndex == points.size())    //fraction > 1.0
                return points.get(points.size() - 1);

            int beginPointIndex = endPointIndex - 1;
            LatLng beginPoint = points.get(beginPointIndex), endPoint = points.get(endPointIndex);
            double segmentFraction = fraction - cumulativeDistances.get(beginPointIndex);
            return SphericalUtil.interpolate(beginPoint, endPoint, segmentFraction);
        }
    }
}
