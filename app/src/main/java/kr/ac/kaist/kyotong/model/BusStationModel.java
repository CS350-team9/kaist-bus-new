package kr.ac.kaist.kyotong.model;

import android.location.Location;

import java.util.ArrayList;

import kr.ac.kaist.kyotong.utils.LocationCoordinates;

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
    public static BusStationModel newInstance(String name_full, int degree, Location location) {
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

    public static BusStationModel newInstance(String name_full, int degree, Location location, int img_resource) {
        BusStationModel busStationModel = BusStationModel.newInstance(name_full, degree, location);
        busStationModel.img_resource = img_resource;
        return busStationModel;
    }

    public static Location newLocationInstance(double latitude, double longitude) {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }

    /**
     *
     */
    public Location location = null;
    public String name = "";
    public String name_full = "";
    /** 원형 버스 노선도에서 정거장을 점으로 표시할 위치를 나타내는 각도(degree) */
    public int degree = 0;
    /** 정거장의 사진을 가리키는 그림 리소스 */
    public int img_resource = -1;

    public ArrayList<BusTimeModel> departureTimes = new ArrayList<BusTimeModel>();

    /** 이 버스 정거장에서 다음 정거장까지의 경로를 구성하는 꼭짓점의 좌표 (두 정거장의 좌표는 포함하지 않음) */
    public ArrayList<LocationCoordinates> pointsOnPathToNextStation = new ArrayList<>();

    public void addDepartureTime(BusTimeModel busTimeModel) {
        departureTimes.add(busTimeModel);
    }

    /**
     * 버스 시간표의 내일/모레 도착 항목을 알아보기 좋게 구분자(헤더)를 추가한다.
     */
    public void addHeader() {

        //내일 구분자
        int i = 0;
        while (i < departureTimes.size() && departureTimes.get(i).getAbsoluteSeconds() <= 24 * 3600) {
            i++;
        }
        BusTimeModel divider = new BusTimeModel();
        divider.setDividerDayOffset(1);
        departureTimes.add(i, divider);
        i++;

        //모레 구분자
        while (i < departureTimes.size() && departureTimes.get(i).getAbsoluteSeconds() <= 48 * 3600) {
            i++;
        }
        if (i < departureTimes.size()) {
            divider = new BusTimeModel();
            divider.setDividerDayOffset(2);
            departureTimes.add(i, divider);
        }
    }

    /**
     *
     */
    public void updateHeader() {
        for (int i = 0; i < departureTimes.size(); i++) {
            BusTimeModel busTimeModel = departureTimes.get(i);
            if (busTimeModel.getDividerDayOffset() == 2) {
                busTimeModel.setDividerDayOffset(1);
                break;
            }
        }
    }

}
