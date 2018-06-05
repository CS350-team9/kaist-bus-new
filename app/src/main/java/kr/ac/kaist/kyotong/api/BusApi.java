package kr.ac.kaist.kyotong.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import com.google.android.gms.maps.model.LatLng;

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
        ArrayList<BusModel> todayBuses = createTodayBuses(title_id);
        addBusTimeInBusStations(busStationModels, todayBuses, true);
        buses.addAll(todayBuses);

        /**
         * create Tomorrow Buses.
         */
        ArrayList<BusModel> tomorrowBuses = createTomorrowBuses(title_id);
        addBusTimeInBusStations(busStationModels, tomorrowBuses, false);

        /**
         * SORT
         */
        sort(buses, busStationModels);
    }

    private ArrayList<BusModel> createTodayBuses(int title_id) {
        Calendar today = Calendar.getInstance();
        if (DateUtils.beforeFourAM())
            today.add(Calendar.DATE, -1);

        if (DateUtils.isHoliday(today))
            return createHolidayBuses(title_id);
        else
            return createWeekdayBuses(title_id);
    }

    private ArrayList<BusModel> createTomorrowBuses(int title_id) {
        Calendar tomorrow = Calendar.getInstance();
        if (!DateUtils.beforeFourAM())
            tomorrow.add(Calendar.DATE, 1);

        if (DateUtils.isHoliday(tomorrow))
            return createHolidayBuses(title_id);
        else
            return createWeekdayBuses(title_id);
    }

    private void addBusTimeInBusStations(ArrayList<BusStationModel> busStationModels, ArrayList<BusModel> buses, boolean today) {
        for (BusModel busModel : buses) {
            for (int i = 0; i < busModel.busDepartureTimes.size(); i++) {
                if (!today) {
                    busModel.getDepartureTime(i).makeTomorrowBusTime();
                }
                busModel.getBusDepartureStation(i).addDepartureTime(busModel.getDepartureTime(i));
            }
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

        LatLng pathsBetweenStations_olev[][] = {
                {
                        new LatLng(36.373420, 127.359),
                        new LatLng(36.373476, 127.359),
                        new LatLng(36.373455, 127.361),
                },
                {
                        new LatLng(36.372502, 127.362),
                        new LatLng(36.372157, 127.362),
                        new LatLng(36.371574, 127.361),
                        new LatLng(36.370939, 127.362),
                },
                {
                        new LatLng(36.370777, 127.362),
                        new LatLng(36.370660, 127.363),
                        new LatLng(36.370686, 127.363),
                        new LatLng(36.370824, 127.364),
                        new LatLng(36.371153, 127.365),
                        new LatLng(36.370993, 127.365),
                },
                {
                        new LatLng(36.369898, 127.366),
                        new LatLng(36.369924, 127.366),
                        new LatLng(36.370773, 127.367),
                        new LatLng(36.370094, 127.369),
                        new LatLng(36.369524, 127.369),
                },
                {
                        new LatLng(36.369375, 127.369),
                        new LatLng(36.369241, 127.369),
                        new LatLng(36.368887, 127.369),
                        new LatLng(36.368697, 127.368),
                        new LatLng(36.368788, 127.368),
                        new LatLng(36.369159, 127.367),
                        new LatLng(36.368805, 127.367),
                        new LatLng(36.368604, 127.367),
                        new LatLng(36.368306, 127.367),
                        new LatLng(36.367991, 127.367),
                        new LatLng(36.367715, 127.367),
                        new LatLng(36.366400, 127.364),
                        new LatLng(36.366391, 127.364),
                },
                {
                        new LatLng(36.366308, 127.363),
                        new LatLng(36.367137, 127.362),
                },
                {
                        new LatLng(36.368792, 127.361),
                        new LatLng(36.369854, 127.360),
                },
                {
                        new LatLng(36.370814, 127.359),
                        new LatLng(36.371258, 127.358),
                        new LatLng(36.371405, 127.358),
                        new LatLng(36.371673, 127.357),
                        new LatLng(36.371845, 127.357),
                        new LatLng(36.372489, 127.356),
                        new LatLng(36.372999, 127.356),
                        new LatLng(36.373413, 127.356),
                },
                {
                        //8 -> 0은 직선 경로여서 중간 점이 필요없음
                },
        };

        LatLng pathsBetweenStations_wolpyeong[][] = {
                {
                        new LatLng(36.372472, 127.362),
                        new LatLng(36.371600, 127.361),
                        new LatLng(36.370727, 127.359),
                        new LatLng(36.370432, 127.360),
                },
                {
                        new LatLng(36.369290, 127.361),
                        new LatLng(36.368744, 127.361),
                },
                {
                        new LatLng(36.367011, 127.362),
                        new LatLng(36.365750, 127.363),
                        new LatLng(36.365326, 127.363),
                        new LatLng(36.363745, 127.360),
                        new LatLng(36.362639, 127.358),
                        new LatLng(36.361608, 127.356),
                        new LatLng(36.360433, 127.353),
                        new LatLng(36.360329, 127.352),
                        new LatLng(36.360364, 127.351),
                        new LatLng(36.360545, 127.350),
                        new LatLng(36.362230, 127.345),
                        new LatLng(36.362212, 127.344),
                        new LatLng(36.361996, 127.344),
                        new LatLng(36.361748, 127.345),
                },
                {
                        new LatLng(36.360507, 127.348),
                        new LatLng(36.360151, 127.349),
                        new LatLng(36.359919, 127.350),
                        new LatLng(36.358744, 127.359),
                        new LatLng(36.358787, 127.360),
                        new LatLng(36.358217, 127.363),
                },
                {
                        new LatLng(36.357703, 127.368),
                        new LatLng(36.357647, 127.374),
                        new LatLng(36.352723, 127.374),
                },
                {
                        new LatLng(36.351269, 127.378),
                        new LatLng(36.352358, 127.379),
                        new LatLng(36.353179, 127.379),
                },
                {
                        new LatLng(36.364643, 127.379),
                        new LatLng(36.364652, 127.379),
                        new LatLng(36.358080, 127.379),
                        new LatLng(36.357898, 127.379),
                        new LatLng(36.357847, 127.376),
                        new LatLng(36.357752, 127.375),
                        new LatLng(36.357804, 127.369),
                        new LatLng(36.357890, 127.367),
                },
                {
                        new LatLng(36.359273, 127.357),
                        new LatLng(36.360310, 127.358),
                        new LatLng(36.360673, 127.358),
                        new LatLng(36.361934, 127.357),
                        new LatLng(36.362757, 127.359),
                        new LatLng(36.363681, 127.360),
                        new LatLng(36.365440, 127.364),
                        new LatLng(36.365980, 127.363),
                },
                {
                        new LatLng(36.368784, 127.361),
                },
                {
                        new LatLng(36.370484, 127.360),
                        new LatLng(36.370743, 127.359),
                        new LatLng(36.371583, 127.361),
                        new LatLng(36.372365, 127.362),
                        new LatLng(36.372412, 127.362),
                },
                {
                        //9 -> 0은 직선 경로여서 중간 점이 필요없음
                }
        };

        LatLng pathsBetweenStations_munji[][] = {
                {
                        new LatLng(36.393816, 127.398),
                        new LatLng(36.393202, 127.398),
                        new LatLng(36.392529, 127.398),
                        new LatLng(36.392511, 127.396),
                        new LatLng(36.390788, 127.396),
                        new LatLng(36.390503, 127.397),
                        new LatLng(36.390494, 127.399),
                        new LatLng(36.390079, 127.399),
                        new LatLng(36.390071, 127.398),
                        new LatLng(36.390330, 127.397),
                        new LatLng(36.390926, 127.395),
                        new LatLng(36.391099, 127.394),
                        new LatLng(36.391038, 127.393),
                        new LatLng(36.390227, 127.389),
                        new LatLng(36.389683, 127.387),
                        new LatLng(36.389674, 127.387),
                        new LatLng(36.389147, 127.385),
                        new LatLng(36.388620, 127.380),
                        new LatLng(36.388612, 127.376),
                        new LatLng(36.388845, 127.375),
                        new LatLng(36.389406, 127.374),
                        new LatLng(36.390002, 127.374),
                        new LatLng(36.393275, 127.373),
                        new LatLng(36.394087, 127.373),
                        new LatLng(36.401256, 127.374),
                        new LatLng(36.401836, 127.374),
                        new LatLng(36.404830, 127.376),
                        new LatLng(36.405417, 127.376),
                        new LatLng(36.406004, 127.376),
                        new LatLng(36.406522, 127.376),
                        new LatLng(36.407135, 127.375),
                        new LatLng(36.407463, 127.375),
                        new LatLng(36.409199, 127.379),
                        new LatLng(36.409281, 127.380),
                        new LatLng(36.409083, 127.380),
                },
                {
                        new LatLng(36.408459, 127.381),
                        new LatLng(36.404076, 127.387),
                        new LatLng(36.402072, 127.389),
                        new LatLng(36.401105, 127.390),
                        new LatLng(36.400198, 127.392),
                        new LatLng(36.399991, 127.392),
                        new LatLng(36.399611, 127.394),
                        new LatLng(36.399611, 127.404),
                        new LatLng(36.399378, 127.404),
                        new LatLng(36.390077, 127.404),
                        new LatLng(36.390106, 127.399),
                        new LatLng(36.392464, 127.399),
                        new LatLng(36.392455, 127.401),
                        new LatLng(36.393198, 127.401),
                        new LatLng(36.393759, 127.401),
                },
                {
                        new LatLng(36.393816, 127.398),
                        new LatLng(36.393202, 127.398),
                        new LatLng(36.392529, 127.398),
                        new LatLng(36.392511, 127.396),
                        new LatLng(36.390788, 127.396),
                        new LatLng(36.390503, 127.397),
                        new LatLng(36.390494, 127.399),
                        new LatLng(36.390079, 127.399),
                        new LatLng(36.390071, 127.398),
                        new LatLng(36.390330, 127.397),
                        new LatLng(36.390926, 127.395),
                        new LatLng(36.391099, 127.394),
                        new LatLng(36.391038, 127.393),
                        new LatLng(36.390227, 127.389),
                        new LatLng(36.389683, 127.387),
                        new LatLng(36.389674, 127.387),
                        new LatLng(36.389147, 127.385),
                        new LatLng(36.388620, 127.380),
                        new LatLng(36.388589, 127.378),
                },
                {
                        new LatLng(36.388019, 127.378),
                        new LatLng(36.378894, 127.378),
                        new LatLng(36.378127, 127.378),
                        new LatLng(36.375614, 127.379),
                        new LatLng(36.374793, 127.379),
                        new LatLng(36.371958, 127.374),
                        new LatLng(36.366196, 127.365),
                        new LatLng(36.365714, 127.364),
                        new LatLng(36.365791, 127.363),
                        new LatLng(36.365980, 127.363),
                        new LatLng(36.367420, 127.362),
                        new LatLng(36.368784, 127.361),
                        new LatLng(36.369929, 127.360),
                        new LatLng(36.370484, 127.360),
                        new LatLng(36.370743, 127.359),
                        new LatLng(36.371583, 127.361),
                        new LatLng(36.372365, 127.362),
                        new LatLng(36.372412, 127.362),
                },
                {
                        new LatLng(36.372472, 127.362),
                        new LatLng(36.371600, 127.361),
                        new LatLng(36.370727, 127.359),
                        new LatLng(36.370432, 127.360),
                        new LatLng(36.369884, 127.360),
                        new LatLng(36.369290, 127.361),
                        new LatLng(36.368744, 127.361),
                        new LatLng(36.367198, 127.362),
                        new LatLng(36.367011, 127.362),
                        new LatLng(36.365476, 127.364),
                        new LatLng(36.366159, 127.365),
                        new LatLng(36.371839, 127.374),
                        new LatLng(36.374692, 127.379),
                        new LatLng(36.375685, 127.379),
                        new LatLng(36.378677, 127.378),
                        new LatLng(36.388524, 127.378),
                },
                {
                        new LatLng(36.388534, 127.380),
                        new LatLng(36.388448, 127.384),
                        new LatLng(36.388577, 127.385),
                        new LatLng(36.389087, 127.386),
                        new LatLng(36.389596, 127.387),
                        new LatLng(36.390175, 127.389),
                        new LatLng(36.390969, 127.393),
                        new LatLng(36.390952, 127.394),
                        new LatLng(36.390779, 127.395),
                        new LatLng(36.390140, 127.397),
                        new LatLng(36.389985, 127.398),
                        new LatLng(36.389985, 127.399),
                        new LatLng(36.392464, 127.399),
                        new LatLng(36.392455, 127.401),
                        new LatLng(36.393198, 127.401),
                        new LatLng(36.393759, 127.401),
                },
                {
                        //5와 0은 동일한 정거장이므로 입력하지 않음
                }
        };

        switch (title_id) {

            /**
             * KAIST
             */
            case R.string.tab_kaist_olev:
                busStationModels.add(BusStationModel.newInstance("카이마루", 0, new LatLng(36.373428, 127.359221)));
                busStationModels.add(BusStationModel.newInstance("스컴", 30, new LatLng(36.372784, 127.361855)));
                busStationModels.add(BusStationModel.newInstance("창의관", 60, new LatLng(36.370849, 127.362381)));
                busStationModels.add(BusStationModel.newInstance("의과학센터", 110, new LatLng(36.370193, 127.365932)));
                busStationModels.add(BusStationModel.newInstance("파팔라도", 140, new LatLng(36.369545, 127.369612)));
                busStationModels.add(BusStationModel.newInstance("정문", 200, new LatLng(36.366357, 127.363614)));
                busStationModels.add(BusStationModel.newInstance("오리연못", 230, new LatLng(36.367420, 127.362574)));
                busStationModels.add(BusStationModel.newInstance("교육지원동", 270, new LatLng(36.370020, 127.360728)));
                busStationModels.add(BusStationModel.newInstance("아름관(간이)", 310, new LatLng(36.373484, 127.356651)));
                busStationModels.add(BusStationModel.newInstance("카이마루", 360, new LatLng(36.373428, 127.359221)));

                for (int i = 0; i < pathsBetweenStations_olev.length; ++i) {
                    for (LatLng coords : pathsBetweenStations_olev[i])
                        busStationModels.get(i).pointsOnPathToNextStation.add(coords);
                }

                break;

            case R.string.tab_kaist_wolpyeong:
                busStationModels.add(BusStationModel.newInstance("강당", 0, new LatLng(36.372485, 127.363282)));
                busStationModels.add(BusStationModel.newInstance("본관", 30, new LatLng(36.369884, 127.360720)));
                busStationModels.add(BusStationModel.newInstance("오리연못", 60, new LatLng(36.367198, 127.362556)));
                busStationModels.add(BusStationModel.newInstance("충남대앞(일미식당)", 97, new LatLng(36.361533, 127.345736), R.drawable.station_kaist_wolpyeong_3));
                busStationModels.add(BusStationModel.newInstance("월평역(1번출구)", 128, new LatLng(36.358109, 127.364356), R.drawable.station_kaist_wolpyeong_4));
                busStationModels.add(BusStationModel.newInstance("갤러리아(대일빌딩)", 189, new LatLng(36.352054, 127.376309), R.drawable.station_kaist_wolpyeong_5));
                busStationModels.add(BusStationModel.newInstance("청사시외(택시승강장)", 232, new LatLng(36.361140, 127.379472), R.drawable.station_kaist_wolpyeong_6));
                busStationModels.add(BusStationModel.newInstance("월평역(3번출구)", 282, new LatLng(36.358587, 127.363199), R.drawable.station_kaist_wolpyeong_7));
                busStationModels.add(BusStationModel.newInstance("오리연못", 300, new LatLng(36.367420, 127.362574)));
                busStationModels.add(BusStationModel.newInstance("본관", 330, new LatLng(36.369929, 127.360819)));
                busStationModels.add(BusStationModel.newInstance("강당", 360, new LatLng(36.372404, 127.363313)));

                for (int i = 0; i < pathsBetweenStations_wolpyeong.length; ++i) {
                    for (LatLng coords : pathsBetweenStations_wolpyeong[i])
                        busStationModels.get(i).pointsOnPathToNextStation.add(coords);
                }

                break;

            case R.string.tab_kaist_sunhwan:
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(화암방향)", 0, new LatLng(36.393770, 127.399925)));
                busStationModels.add(BusStationModel.newInstance("화암기숙사", 90, new LatLng(36.408435, 127.381788)));
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(본원방향)", 180, new LatLng(36.393794, 127.400063)));
                busStationModels.add(BusStationModel.newInstance("로덴하우스", 225, new LatLng(36.388011, 127.378545)));
                busStationModels.add(BusStationModel.newInstance("본원(대덕캠퍼스)", 270, new LatLng(36.372485, 127.363282)));
                busStationModels.add(BusStationModel.newInstance("교수아파트", 315, new LatLng(36.388524, 127.379928)));
                busStationModels.add(BusStationModel.newInstance("문지", 360, new LatLng(36.393770, 127.399925)));

                for (int i = 0; i < pathsBetweenStations_munji.length; ++i) {
                    for (LatLng coords : pathsBetweenStations_munji[i])
                        busStationModels.get(i).pointsOnPathToNextStation.add(coords);
                }

                break;
        }
        return busStationModels;
    }

    /**
     * 주어진 노선의 공휴일 버스 일정을 생성한다.
     *
     * @param title_id 버스 노선을 나타내는 문자열의 ID
     * @return 버스 일정
     */
    private ArrayList<BusModel> createHolidayBuses(int title_id) {
        ArrayList<BusModel> buses = new ArrayList<>();

        if (title_id == R.string.tab_kaist_sunhwan) {
            for (int h = 7; h < 26; h += 3)
                buses.add(createBus(h, 50, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));

            for (int h = 9; h < 25; h += 3)
                buses.add(createBus(h, 20, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
        }

        return buses;
    }

    /**
     * 주어진 노선의 평일 버스 일정을 생성한다.
     *
     * @param title_id 버스 노선을 나타내는 문자열의 ID
     * @return 버스 일정
     */
    private ArrayList<BusModel> createWeekdayBuses(int title_id) {
        ArrayList<BusModel> buses = new ArrayList<>();

        switch (title_id) {

            /**
             * KAIST
             */
            case R.string.tab_kaist_olev:
                for (int m = 30; m <= 3 * 60 + 45; m += 15)
                    buses.add(createBus(8, m, new int[]{0, 1, 2, 3, 4, 6, 7, 8, 9, 11}, 0));
                for (int m = 0; m <= 4 * 60; m += 15)
                    buses.add(createBus(13, m, new int[]{0, 1, 2, 3, 4, 6, 7, 8, 9, 11}, 0));
                break;

            case R.string.tab_kaist_wolpyeong:
                for (int h = 9; h <= 17; h++) {
                    if (h != 12) {
                        buses.add(createBus(h, 0, new int[]{0, 2, 4, 10, 15, 25, 32, 40, 43, 44, 45}, 0));
                    }
                }
                break;

            case R.string.tab_kaist_sunhwan:
                buses.add(createBus(7, 10, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(7, 40, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(8, 10, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(8, 40, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(9, 20, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(9, 50, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(10, 20, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(10, 50, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(11, 20, new int[]{0, 10, 20}, 0));
                buses.add(createBus(11, 50, new int[]{0, 10, 20, 24, 30}, 0));

                buses.add(createBus(13, 0, new int[]{0, 15, 20}, 4));

                for (int h = 13; h < 27; h++) {
                    buses.add(createBus(h, 20, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                }

                buses.add(createBus(16, 50, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(17, 50, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(18, 50, new int[]{0, 10, 20, 24, 40, 55, 60}, 0));
                buses.add(createBus(19, 50, new int[]{0, 10, 20}, 0));
                buses.add(createBus(21, 10, new int[]{0, 4, 20, 35, 40}, 2));
                buses.add(createBus(21, 50, new int[]{0, 10, 20}, 0));

                buses.add(createBus(27, 20, new int[]{0, 10, 20}, 0));

                break;

        }

        return buses;
    }

    private void sort(ArrayList<BusModel> buses, ArrayList<BusStationModel> busStationModels) {
        Collections.sort(buses, new Comparator<BusModel>() {
            @Override
            public int compare(BusModel lhs, BusModel rhs) {
                return lhs.getDepartureTime(0).getAbsoluteSeconds() > rhs.getDepartureTime(0).getAbsoluteSeconds() ? 1 : -1;
            }
        });

        for (BusStationModel busStationModel : busStationModels) {
            Collections.sort(busStationModel.departureTimes, new Comparator<BusTimeModel>() {
                @Override
                public int compare(BusTimeModel lhs, BusTimeModel rhs) {
                    return lhs.getAbsoluteSeconds() - rhs.getAbsoluteSeconds();
                }
            });
        }
    }

    private BusModel createBus(int baseHours, int baseMinutes, int deltaMinutes[], int offset) {
        BusModel busModel = new BusModel();
        for (int i = 0; i < deltaMinutes.length - 1; i++) {
            addPathInBus(busModel, i + offset, baseHours, baseMinutes + deltaMinutes[i], (i + 1 + offset) % busStationModels.size(), baseHours, baseMinutes + deltaMinutes[i + 1]);
        }
        return busModel;
    }

    private void addPathInBus(BusModel busModel,
                              int departureStationIndex, int departureTimeHours, int departureTimeMinutes,
                              int arrivalStationIndex, int arrivalTimeHours, int arrivalTimeMinutes) {
        BusTimeModel busDepartureTime = new BusTimeModel();
        busDepartureTime.setTime(departureTimeHours, departureTimeMinutes, 0);

        BusTimeModel busArrivalTime = new BusTimeModel();
        busArrivalTime.setTime(arrivalTimeHours, arrivalTimeMinutes, 0);

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
