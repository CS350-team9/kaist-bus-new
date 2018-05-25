package kr.ac.kaist.kyotong.model;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * Created by yearnning on 14. 12. 20..
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
    public int degree = 0;
    public int img_resource = -1;

    public ArrayList<BusTimeModel> departureTimes = new ArrayList<BusTimeModel>();

    public void addDepartureTime(BusTimeModel busTimeModel) {
        departureTimes.add(busTimeModel);
    }

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
