package kr.ac.kaist.kyotong.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.utils.DateUtils;


/**
 * Created by yearnning on 14. 12. 29..
 */
public class BusApi extends ApiBase {

    private final static String TAG = "BusApi";

    private ArrayList<BusStationModel> busStationModels;
    private ArrayList<BusModel> buses = new ArrayList<BusModel>();


    /**
     * Init
     */
    public BusApi(int title_id) {

        /**
         * init busStations
         */
        busStationModels = createStations(title_id);

        /**
         * create Today Buses.
         */
        ArrayList<BusModel> todayBuses = createTodayBuses(title_id, busStationModels);
        addBusTimeInBusStations(busStationModels, todayBuses, true);
        buses.addAll(todayBuses);

        /**
         * create Tomorrow Buses.
         */
        ArrayList<BusModel> tomorrowBuses = createTomorrowBuses(title_id, busStationModels);
        addBusTimeInBusStations(busStationModels, tomorrowBuses, false);

        /**
         * SORT
         */
        sort(buses, busStationModels);

        /**
         * addHeader Info
         */
        addHeaderInBusStations(busStationModels);

    }

    private ArrayList<BusModel> createTodayBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        boolean holiday;
        if (DateUtils.beforeFourAM()) {
            holiday = DateUtils.isHoliday(-1);
            Log.d(TAG, "Today Bus by Yesterday");
        } else {
            holiday = DateUtils.isHoliday(0);
            Log.d(TAG, "Today Bus by Today");
        }

        if (holiday) {
            return createHolidayBuses(title_id, busStationModels);
        } else {
            return createWeekdayBuses(title_id, busStationModels);
        }
    }

    private ArrayList<BusModel> createTomorrowBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        boolean holiday;
        if (DateUtils.beforeFourAM()) {
            holiday = DateUtils.isHoliday(0);
        } else {
            holiday = DateUtils.isHoliday(1);
        }

        if (holiday) {
            return createHolidayBuses(title_id, busStationModels);
        } else {
            return createWeekdayBuses(title_id, busStationModels);
        }
    }

    private void addBusTimeInBusStations(ArrayList<BusStationModel> busStationModels, ArrayList<BusModel> buses, boolean today) {
        if (buses.size() > 0) {
            for (BusModel busModel : buses) {
                for (int i = 0; i < busModel.busDepartureTimes.size(); i++) {
                    if (!today) {
                        busModel.getDepartureTime(i).makeTomorrowBusTime();
                    }
                    busModel.getBusDepartureStation(i).addDepartureTime(busModel.getDepartureTime(i));
                }
            }
        } else {
            for (BusStationModel busStationModel : busStationModels) {
                BusTimeModel busTimeModel = new BusTimeModel();
                busTimeModel.indicator = "주말 및 공휴일은 운행하지 않습니다";
                if (today)
                    busTimeModel.setTime(24, 0);
                else
                    busTimeModel.setTime(48, 0);

                busStationModel.addDepartureTime(busTimeModel);
            }
        }

    }

    private void addHeaderInBusStations(ArrayList<BusStationModel> busStaions) {
        for (BusStationModel busStationModel : busStaions) {
            busStationModel.addHeader();
        }
    }

    /**
     * 특정 버스 노선을 구성하는 모든 정거장의 목록을 생성한다.
     *
     * @param title_id 버스 노선의 이름을 가리키는 문자열 리소스
     * @return 버스 정거장의 목록
     */
    private ArrayList<BusStationModel> createStations(int title_id) {

        ArrayList<BusStationModel> busStationModels = new ArrayList<>();

        switch (title_id) {

            /**
             * KAIST
             */
            case R.string.tab_kaist_olev:
                busStationModels.add(BusStationModel.newInstance("카이마루", 0, BusStationModel.newLocationInstance(36.373428, 127.359221)));
                busStationModels.add(BusStationModel.newInstance("스컴", 30, BusStationModel.newLocationInstance(36.372784, 127.361855)));
                busStationModels.add(BusStationModel.newInstance("창의관", 60, BusStationModel.newLocationInstance(36.370849, 127.362381)));
                busStationModels.add(BusStationModel.newInstance("의과학센터", 110, BusStationModel.newLocationInstance(36.370193, 127.365932)));
                busStationModels.add(BusStationModel.newInstance("파팔라도", 140, BusStationModel.newLocationInstance(36.369545, 127.369612)));
                busStationModels.add(BusStationModel.newInstance("정문", 200, BusStationModel.newLocationInstance(36.366357, 127.363614)));
                busStationModels.add(BusStationModel.newInstance("오리연못", 230, BusStationModel.newLocationInstance(36.367420, 127.362574)));
                busStationModels.add(BusStationModel.newInstance("교육지원동", 270, BusStationModel.newLocationInstance(36.370020, 127.360728)));
                busStationModels.add(BusStationModel.newInstance("아름관(간이)", 310, BusStationModel.newLocationInstance(36.373484, 127.356651)));
                busStationModels.add(BusStationModel.newInstance("카이마루", 360, null));
                break;

            case R.string.tab_kaist_wolpyeong:
                busStationModels.add(BusStationModel.newInstance("강당", 0, null));
                busStationModels.add(BusStationModel.newInstance("본관", 30, null));
                busStationModels.add(BusStationModel.newInstance("오리연못", 60, null));
                busStationModels.add(BusStationModel.newInstance("충남대앞(일미식당)", 97, BusStationModel.newLocationInstance(36.361533, 127.345736), R.drawable.station_kaist_wolpyeong_3));
                busStationModels.add(BusStationModel.newInstance("월평역(1번출구)", 128, BusStationModel.newLocationInstance(36.358109, 127.364356), R.drawable.station_kaist_wolpyeong_4));
                busStationModels.add(BusStationModel.newInstance("갤러리아(대일빌딩)", 189, BusStationModel.newLocationInstance(36.352054, 127.376309), R.drawable.station_kaist_wolpyeong_5));
                busStationModels.add(BusStationModel.newInstance("청사시외(택시승강장)", 232, BusStationModel.newLocationInstance(36.361140, 127.379472), R.drawable.station_kaist_wolpyeong_6));
                busStationModels.add(BusStationModel.newInstance("월평역(3번출구)", 282, BusStationModel.newLocationInstance(36.358587, 127.363199), R.drawable.station_kaist_wolpyeong_7));
                busStationModels.add(BusStationModel.newInstance("오리연못", 300, null));
                busStationModels.add(BusStationModel.newInstance("본관", 330, null));
                busStationModels.add(BusStationModel.newInstance("강당", 360, null));
                break;

            case R.string.tab_kaist_sunhwan:
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(화암방향)", 0, null));
                busStationModels.add(BusStationModel.newInstance("화암기숙사", 90, null));
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(본원방향)", 180, null));
                busStationModels.add(BusStationModel.newInstance("로덴하우스", 225, null));
                busStationModels.add(BusStationModel.newInstance("본원(대덕캠퍼스)", 270, null));
                busStationModels.add(BusStationModel.newInstance("교수아파트", 315, null));
                busStationModels.add(BusStationModel.newInstance("문지", 360, null));
                break;
        }
        return busStationModels;
    }

    /**
     * @param title_id
     * @param busStationModels
     * @return
     */
    private ArrayList<BusModel> createHolidayBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        ArrayList<BusModel> buses = new ArrayList<>();

        if (title_id == R.string.tab_kaist_sunhwan) {

            ArrayList<String> times_str;

            for (int h = 7; h < 26; h += 3) {

                times_str = new ArrayList<>();
                times_str.add(String.format("%02d:50", h));
                times_str.add(String.format("%02d:00", h + 1));
                times_str.add(String.format("%02d:10", h + 1));
                times_str.add(String.format("%02d:14", h + 1));
                times_str.add(String.format("%02d:30", h + 1));
                times_str.add(String.format("%02d:45", h + 1));
                times_str.add(String.format("%02d:50", h + 1));
                buses.add(createBus(times_str, 0));
            }

            for (int h = 9; h < 25; h += 3) {
                times_str = new ArrayList<>();
                times_str.add(String.format("%02d:20", h));
                times_str.add(String.format("%02d:30", h));
                times_str.add(String.format("%02d:40", h));
                times_str.add(String.format("%02d:44", h));
                times_str.add(String.format("%02d:00", h + 1));
                times_str.add(String.format("%02d:15", h + 1));
                times_str.add(String.format("%02d:20", h + 1));
                buses.add(createBus(times_str, 0));
            }
        }

        return buses;
    }

    /**
     * @param title_id
     * @param busStationModels
     * @return
     */
    private ArrayList<BusModel> createWeekdayBuses(int title_id, ArrayList<BusStationModel> busStationModels) {

        ArrayList<BusModel> buses = new ArrayList<>();

        switch (title_id) {

            /**
             * KAIST
             */
            case R.string.tab_kaist_olev:
                for (int h = 8; h <= 17; h++) {
                    int[] ms = {0, 15, 30, 45};
                    for (int m : ms) {
                        if ((h != 8 || m >= 30) && (h != 17 || m == 0) && (h != 12)) {
                            BusModel busModel = new BusModel();
                            addPathInBus(busModel, 0, String.format("%02d:%02d", h, m), 1, String.format("%02d:%02d", h, m + 1));
                            addPathInBus(busModel, 1, String.format("%02d:%02d", h, m + 1), 2, String.format("%02d:%02d", h, m + 2));
                            addPathInBus(busModel, 2, String.format("%02d:%02d", h, m + 2), 3, String.format("%02d:%02d", h, m + 3));
                            addPathInBus(busModel, 3, String.format("%02d:%02d", h, m + 3), 4, String.format("%02d:%02d", h, m + 4));
                            addPathInBus(busModel, 4, String.format("%02d:%02d", h, m + 4), 5, String.format("%02d:%02d", h, m + 6));
                            addPathInBus(busModel, 5, String.format("%02d:%02d", h, m + 6), 6, String.format("%02d:%02d", h, m + 7));
                            addPathInBus(busModel, 6, String.format("%02d:%02d", h, m + 7), 7, String.format("%02d:%02d", h, m + 8));
                            addPathInBus(busModel, 7, String.format("%02d:%02d", h, m + 8), 8, String.format("%02d:%02d", h, m + 9));
                            addPathInBus(busModel, 8, String.format("%02d:%02d", h, m + 9), 9, String.format("%02d:%02d", h, m + 11));
                            buses.add(busModel);
                        }
                    }
                }
                break;

            case R.string.tab_kaist_wolpyeong:
                for (int h = 9; h <= 17; h++) {
                    if (h != 12) {
                        BusModel busModel = new BusModel();
                        addPathInBus(busModel, 0, String.format("%02d:%02d", h, 0), 1, String.format("%02d:%02d", h, 2));
                        addPathInBus(busModel, 1, String.format("%02d:%02d", h, 2), 2, String.format("%02d:%02d", h, 4));
                        addPathInBus(busModel, 2, String.format("%02d:%02d", h, 4), 3, String.format("%02d:%02d", h, 10));
                        addPathInBus(busModel, 3, String.format("%02d:%02d", h, 10), 4, String.format("%02d:%02d", h, 15));
                        addPathInBus(busModel, 4, String.format("%02d:%02d", h, 15), 5, String.format("%02d:%02d", h, 25));
                        addPathInBus(busModel, 5, String.format("%02d:%02d", h, 25), 6, String.format("%02d:%02d", h, 32));
                        addPathInBus(busModel, 6, String.format("%02d:%02d", h, 32), 7, String.format("%02d:%02d", h, 40));
                        addPathInBus(busModel, 7, String.format("%02d:%02d", h, 40), 8, String.format("%02d:%02d", h, 43));
                        addPathInBus(busModel, 8, String.format("%02d:%02d", h, 43), 9, String.format("%02d:%02d", h, 44));
                        addPathInBus(busModel, 9, String.format("%02d:%02d", h, 44), 10, String.format("%02d:%02d", h, 45));
                        buses.add(busModel);
                    }
                }
                break;

            case R.string.tab_kaist_sunhwan:
                ArrayList<String> times_str;
                int[] hs = {7, 8};
                for (int h : hs) {
                    times_str = new ArrayList<>();
                    times_str.add(String.format("%02d:10", h));
                    times_str.add(String.format("%02d:20", h));
                    times_str.add(String.format("%02d:30", h));
                    times_str.add(String.format("%02d:34", h));
                    times_str.add(String.format("%02d:50", h));
                    times_str.add(String.format("%02d:05", h + 1));
                    times_str.add(String.format("%02d:10", h + 1));
                    buses.add(createBus(times_str, 0));
                }
                for (int h = 7; h < 9; h++) {
                    times_str = new ArrayList<String>();
                    times_str.add(String.format("%02d:40", h));
                    times_str.add(String.format("%02d:50", h));
                    times_str.add(String.format("%02d:00", h + 1));
                    times_str.add(String.format("%02d:04", h + 1));
                    times_str.add(String.format("%02d:20", h + 1));
                    times_str.add(String.format("%02d:35", h + 1));
                    times_str.add(String.format("%02d:40", h + 1));
                    buses.add(createBus(times_str, 0));
                }
                for (int h = 9; h < 19; h++) {
                    if ((h < 11) || (h > 15)) {
                        times_str = new ArrayList<String>();
                        times_str.add(String.format("%02d:50", h));
                        times_str.add(String.format("%02d:00", h + 1));
                        times_str.add(String.format("%02d:10", h + 1));
                        times_str.add(String.format("%02d:14", h + 1));
                        times_str.add(String.format("%02d:30", h + 1));
                        times_str.add(String.format("%02d:45", h + 1));
                        times_str.add(String.format("%02d:50", h + 1));
                        buses.add(createBus(times_str, 0));
                    }
                }
                for (int h = 9; h < 27; h++) {
                    if ((h < 11) || (h > 12)) {
                        times_str = new ArrayList<String>();
                        times_str.add(String.format("%02d:20", h));
                        times_str.add(String.format("%02d:30", h));
                        times_str.add(String.format("%02d:40", h));
                        times_str.add(String.format("%02d:44", h));
                        times_str.add(String.format("%02d:00", h + 1));
                        times_str.add(String.format("%02d:15", h + 1));
                        times_str.add(String.format("%02d:20", h + 1));
                        buses.add(createBus(times_str, 0));
                    }
                }
                int[] hs_2 = {11, 27};
                for (int h : hs_2) {
                    times_str = new ArrayList<String>();
                    times_str.add(String.format("%02d:20", h));
                    times_str.add(String.format("%02d:30", h));
                    times_str.add(String.format("%02d:40", h));
                    buses.add(createBus(times_str, 0));
                }

                //
                times_str = new ArrayList<String>();
                times_str.add("11:50");
                times_str.add("12:00");
                times_str.add("12:10");
                times_str.add("12:14");
                times_str.add("12:30");
                buses.add(createBus(times_str, 0));

                //
                int[] hs_3 = {19, 21};
                for (int h : hs_3) {
                    times_str = new ArrayList<String>();
                    times_str.add(String.format("%02d:50", h));
                    times_str.add(String.format("%02d:00", h + 1));
                    times_str.add(String.format("%02d:10", h + 1));
                    buses.add(createBus(times_str, 0));
                }

                //
                times_str = new ArrayList<String>();
                times_str.add("13:00");
                times_str.add("13:15");
                times_str.add("13:20");
                buses.add(createBus(times_str, 4));

                //
                times_str = new ArrayList<String>();
                times_str.add("21:10");
                times_str.add("21:14");
                times_str.add("21:30");
                times_str.add("21:45");
                times_str.add("21:50");
                buses.add(createBus(times_str, 2));
                break;

        }

        return buses;
    }

    private void sort(ArrayList<BusModel> buses, ArrayList<BusStationModel> busStationModels) {
        Collections.sort(buses, new Comparator<BusModel>() {
            @Override
            public int compare(BusModel lhs, BusModel rhs) {
                return lhs.getDepartureTime(0).getAbsoluteSecond() > rhs.getDepartureTime(0).getAbsoluteSecond() ? 1 : -1;
            }
        });

        for (BusStationModel busStationModel : busStationModels) {
            Collections.sort(busStationModel.departureTimes, new Comparator<BusTimeModel>() {
                @Override
                public int compare(BusTimeModel lhs, BusTimeModel rhs) {
                    return lhs.getAbsoluteSecond() - rhs.getAbsoluteSecond();
                }
            });
        }
    }

    private BusModel createBus(ArrayList<String> times_str, int offset) {
        BusModel busModel = new BusModel();
        for (int i = 0; i < (times_str.size() - 1); i++) {
            addPathInBus(busModel, i + offset, times_str.get(i), (i + 1 + offset) % busStationModels.size(), times_str.get(i + 1));
        }
        return busModel;
    }

    /**
     * @param busModel
     * @param departureStationIndex
     * @param departureTime
     * @param arrivalStationIndex
     * @param arrivalTime
     */
    private void addPathInBus(BusModel busModel, int departureStationIndex, String departureTime, int arrivalStationIndex, String arrivalTime) {

        BusTimeModel busDepartureTime = new BusTimeModel();
        busDepartureTime.setTime(departureTime);

        BusTimeModel busArrivalTime = new BusTimeModel();
        busArrivalTime.setTime(arrivalTime);

        busModel.addPath(busStationModels.get(departureStationIndex), busDepartureTime, busStationModels.get(arrivalStationIndex), busArrivalTime);
    }

    /**
     * 결과를 반환합니다.
     */

    public HashMap<String, Object> getResult() {

        HashMap<String, Object> map = new HashMap<String, Object>();

        map.put("busStations", busStationModels);
        map.put("buses", buses);

        return map;
    }
}
