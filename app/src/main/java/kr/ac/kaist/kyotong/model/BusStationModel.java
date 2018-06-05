package kr.ac.kaist.kyotong.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yearnning on 14. 12. 20..
 * <br>버스 정거장을 나타내는 클래스
 */
public class BusStationModel {
    private static final String TAG = "BusStation";

    /**
     * @param name_full
     * @param degree
     * @return
     */
    public static BusStationModel newInstance(String name_full, int degree, LatLng location) {
        BusStationModel busStationModel = new BusStationModel();
        busStationModel.name_full = name_full;
        if (name_full.contains("(") && name_full.contains(")")) {
            busStationModel.name = name_full.substring(0, name_full.indexOf("("));
        } else {
            busStationModel.name = name_full;
        }
        busStationModel.degree = degree;
        busStationModel.location = location;
        return busStationModel;
    }

    public static BusStationModel newInstance(String name_full, int degree, LatLng location, int img_resource) {
        BusStationModel busStationModel = BusStationModel.newInstance(name_full, degree, location);
        busStationModel.img_resource = img_resource;
        return busStationModel;
    }

    /**
     *
     */
    public LatLng location = null;
    public String name = "";
    public String name_full = "";
    /** 원형 버스 노선도에서 정거장을 점으로 표시할 위치를 나타내는 각도(degree) */
    public int degree = 0;
    /** 정거장의 사진을 가리키는 그림 리소스 */
    public int img_resource = -1;

    public ArrayList<BusTimeModel> departureTimes = new ArrayList<BusTimeModel>();

    /** 이 버스 정거장에서 다음 정거장까지의 경로를 구성하는 꼭짓점의 좌표 (두 정거장의 좌표는 포함하지 않음) */
    public ArrayList<LatLng> pointsOnPathToNextStation = new ArrayList<>();

    public void addDepartureTime(BusTimeModel busTimeModel) {
        departureTimes.add(busTimeModel);
    }

    /**
     * 보관하고 있는 버스 시간표를 정렬한다.
     */
    public void sortBusTimes() {
        Collections.sort(departureTimes);
    }
}
