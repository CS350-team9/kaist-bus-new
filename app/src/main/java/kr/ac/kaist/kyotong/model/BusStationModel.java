package kr.ac.kaist.kyotong.model;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.ac.kaist.kyotong.utils.DateUtils;
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
     * 이 정거장을 지나는 모든 버스의 출발 시간 목록을 생성한다.
     */
    public void addHeader() {

        /**
         *
         */
        int i = 0;
        while (i < departureTimes.size() && departureTimes.get(i).getAbsoluteSecond() <= 24 * 3600) {
            i++;
        }
        departureTimes.add(i, createBusTimeHeader(1));
        i++;

        /**
         *
         */
        while (i < departureTimes.size() && departureTimes.get(i).getAbsoluteSecond() <= 48 * 3600) {
            i++;
        }
        if (i < departureTimes.size()) {
            departureTimes.add(i, createBusTimeHeader(2));
        }
    }

    private BusTimeModel createBusTimeHeader(int date_offset) {

        BusTimeModel busTimeModelHeader = new BusTimeModel();

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, date_offset);
        if (c.get(Calendar.HOUR_OF_DAY) < 4) {
            c.add(Calendar.DATE, -1);
        }

        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        String day_str = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        if (date_offset == 1) {
            busTimeModelHeader.header = String.format("내일(%d월 %d일 %s)", month, day, day_str);
        } else if (date_offset == 2) {
            busTimeModelHeader.header = String.format("모레(%d월 %d일 %s)", month, day, day_str);
        } else {
            Log.e(TAG, "There doesn't exist this date_offset");
        }

        if (DateUtils.isHoliday(date_offset)) {
            busTimeModelHeader.header_textColor = 0xFFF44336;
        }

        return busTimeModelHeader;
    }

    /**
     *
     */
    public void updateHeader() {
        for (int i = 0; i < departureTimes.size(); i++) {
            BusTimeModel busTimeModel = departureTimes.get(i);
            if (busTimeModel.header != null && busTimeModel.header.substring(0, 2).equals("모레")) {
                departureTimes.remove(i);
                departureTimes.add(i, createBusTimeHeader(1));
                break;
            }
        }
    }

}
