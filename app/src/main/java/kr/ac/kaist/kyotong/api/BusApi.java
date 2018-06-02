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
import kr.ac.kaist.kyotong.utils.LocationCoordinates;


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
                busStationModels.add(BusStationModel.newInstance("카이마루",     0,   BusStationModel.newLocationInstance(36.373428, 127.359221)));
                busStationModels.add(BusStationModel.newInstance("스컴",         30,  BusStationModel.newLocationInstance(36.372784, 127.361855)));
                busStationModels.add(BusStationModel.newInstance("창의관",       60,  BusStationModel.newLocationInstance(36.370849, 127.362381)));
                busStationModels.add(BusStationModel.newInstance("의과학센터",   110, BusStationModel.newLocationInstance(36.370193, 127.365932)));
                busStationModels.add(BusStationModel.newInstance("파팔라도",     140, BusStationModel.newLocationInstance(36.369545, 127.369612)));
                busStationModels.add(BusStationModel.newInstance("정문",         200, BusStationModel.newLocationInstance(36.366357, 127.363614)));
                busStationModels.add(BusStationModel.newInstance("오리연못",     230, BusStationModel.newLocationInstance(36.367420, 127.362574)));
                busStationModels.add(BusStationModel.newInstance("교육지원동",   270, BusStationModel.newLocationInstance(36.370020, 127.360728)));
                busStationModels.add(BusStationModel.newInstance("아름관(간이)", 310, BusStationModel.newLocationInstance(36.373484, 127.356651)));
                busStationModels.add(BusStationModel.newInstance("카이마루",     360, BusStationModel.newLocationInstance(36.373428, 127.359221)));

                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.373420, 127.359524));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.373476, 127.359904));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.373455, 127.361101));

                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.372502, 127.362224));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.372157, 127.362245));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.371574, 127.361290));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.370939, 127.362218));

                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.370777, 127.362600));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.370660, 127.363142));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.370686, 127.363828));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.370824, 127.364370));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.371153, 127.365159));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.370993, 127.365336));

                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.369898, 127.366212));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.369924, 127.366362));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.370773, 127.367966));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.370094, 127.369055));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.369524, 127.369479));

                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.369375, 127.369488));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.369241, 127.369332));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368887, 127.369128));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368697, 127.368688));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368788, 127.368206));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.369159, 127.367825));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368805, 127.367138));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368604, 127.367289));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368306, 127.367369));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.367991, 127.367310));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.367715, 127.367117));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.366400, 127.364634));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.366391, 127.364001));

                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.366308, 127.363459));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.367137, 127.362751));

                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.368792, 127.361763));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.369854, 127.360889));

                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.370814, 127.359665));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.371258, 127.358758));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.371405, 127.358362));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.371673, 127.357407));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.371845, 127.357074));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.372489, 127.356457));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.372999, 127.356232));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.373413, 127.356221));

                //8 -> 0은 직선 경로여서 중간 점이 필요없음
                break;

            case R.string.tab_kaist_wolpyeong:
                busStationModels.add(BusStationModel.newInstance("강당",                 0,   BusStationModel.newLocationInstance(36.372485, 127.363282)));
                busStationModels.add(BusStationModel.newInstance("본관",                 30,  BusStationModel.newLocationInstance(36.369884, 127.360720)));
                busStationModels.add(BusStationModel.newInstance("오리연못",             60,  BusStationModel.newLocationInstance(36.367198, 127.362556)));
                busStationModels.add(BusStationModel.newInstance("충남대앞(일미식당)",   97,  BusStationModel.newLocationInstance(36.361533, 127.345736), R.drawable.station_kaist_wolpyeong_3));
                busStationModels.add(BusStationModel.newInstance("월평역(1번출구)",      128, BusStationModel.newLocationInstance(36.358109, 127.364356), R.drawable.station_kaist_wolpyeong_4));
                busStationModels.add(BusStationModel.newInstance("갤러리아(대일빌딩)",   189, BusStationModel.newLocationInstance(36.352054, 127.376309), R.drawable.station_kaist_wolpyeong_5));
                busStationModels.add(BusStationModel.newInstance("청사시외(택시승강장)", 232, BusStationModel.newLocationInstance(36.361140, 127.379472), R.drawable.station_kaist_wolpyeong_6));
                busStationModels.add(BusStationModel.newInstance("월평역(3번출구)",      282, BusStationModel.newLocationInstance(36.358587, 127.363199), R.drawable.station_kaist_wolpyeong_7));
                busStationModels.add(BusStationModel.newInstance("오리연못",             300, BusStationModel.newLocationInstance(36.367420, 127.362574)));
                busStationModels.add(BusStationModel.newInstance("본관",                 330, BusStationModel.newLocationInstance(36.369929, 127.360819)));
                busStationModels.add(BusStationModel.newInstance("강당",                 360, BusStationModel.newLocationInstance(36.372404, 127.363313)));

                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.372472, 127.362725));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.371600, 127.361303));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.370727, 127.359640));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.370432, 127.360033));

                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.369290, 127.361239));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.368744, 127.361687));

                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.367011, 127.362769));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.365750, 127.363766));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.365326, 127.363627));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.363745, 127.360398));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.362639, 127.358863));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.361608, 127.356911));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.360433, 127.353241));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.360329, 127.352394));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.360364, 127.351267));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.360545, 127.350194));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.362230, 127.345345));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.362212, 127.344766));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.361996, 127.344669));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.361748, 127.345097));

                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.360507, 127.348465));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.360151, 127.349671));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.359919, 127.350779));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.358744, 127.359437));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.358787, 127.360134));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.358217, 127.363857));

                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.357703, 127.368264));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.357647, 127.374214));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.352723, 127.374267));

                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.351269, 127.378493));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.352358, 127.379190));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.353179, 127.379469));

                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.364643, 127.379597));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.364652, 127.379425));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.358080, 127.379407));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.357898, 127.379192));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.357847, 127.376342));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.357752, 127.375998));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.357804, 127.369550));
                busStationModels.get(6).pointsOnPathToNextStation.add(new LocationCoordinates(36.357890, 127.367974));

                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.359273, 127.357581));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.360310, 127.358890));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.360673, 127.358890));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.361934, 127.357678));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.362757, 127.359232));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.363681, 127.360530));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.365440, 127.364021));
                busStationModels.get(7).pointsOnPathToNextStation.add(new LocationCoordinates(36.365980, 127.363716));

                busStationModels.get(8).pointsOnPathToNextStation.add(new LocationCoordinates(36.368784, 127.361769));

                busStationModels.get(9).pointsOnPathToNextStation.add(new LocationCoordinates(36.370484, 127.360148));
                busStationModels.get(9).pointsOnPathToNextStation.add(new LocationCoordinates(36.370743, 127.359777));
                busStationModels.get(9).pointsOnPathToNextStation.add(new LocationCoordinates(36.371583, 127.361408));
                busStationModels.get(9).pointsOnPathToNextStation.add(new LocationCoordinates(36.372365, 127.362626));
                busStationModels.get(9).pointsOnPathToNextStation.add(new LocationCoordinates(36.372412, 127.362851));

                //9 -> 0은 직선 경로여서 중간 점이 필요없음
                break;

            case R.string.tab_kaist_sunhwan:
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(화암방향)", 0,   BusStationModel.newLocationInstance(36.393770, 127.399925)));
                busStationModels.add(BusStationModel.newInstance("화암기숙사",           90,  BusStationModel.newLocationInstance(36.408435, 127.381788)));
                busStationModels.add(BusStationModel.newInstance("문지캠퍼스(본원방향)", 180, BusStationModel.newLocationInstance(36.393794, 127.400063)));
                busStationModels.add(BusStationModel.newInstance("로덴하우스",           225, BusStationModel.newLocationInstance(36.388011, 127.378545)));
                busStationModels.add(BusStationModel.newInstance("본원(대덕캠퍼스)",     270, BusStationModel.newLocationInstance(36.372485, 127.363282)));
                busStationModels.add(BusStationModel.newInstance("교수아파트",           315, BusStationModel.newLocationInstance(36.388524, 127.379928)));
                busStationModels.add(BusStationModel.newInstance("문지",                 360, BusStationModel.newLocationInstance(36.393770, 127.399925)));

                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.393816, 127.398789));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.393202, 127.398113));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.392529, 127.398135));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.392511, 127.396997));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390788, 127.396997));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390503, 127.397705));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390494, 127.399390));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390079, 127.399443));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390071, 127.398306));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390330, 127.397019));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390926, 127.395560));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.391099, 127.394562));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.391038, 127.393661));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390227, 127.389859));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.389683, 127.387852));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.389674, 127.387466));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.389147, 127.385106));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.388620, 127.380905));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.388612, 127.376484));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.388845, 127.375594));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.389406, 127.374650));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.390002, 127.374188));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.393275, 127.373248));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.394087, 127.373227));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.401256, 127.374461));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.401836, 127.374728));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.404830, 127.376706));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.405417, 127.376835));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.406004, 127.376696));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.406522, 127.376363));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.407135, 127.375612));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.407463, 127.375676));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.409199, 127.379238));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.409281, 127.380018));
                busStationModels.get(0).pointsOnPathToNextStation.add(new LocationCoordinates(36.409083, 127.380844));

                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.408459, 127.381781));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.404076, 127.387871));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.402072, 127.389384));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.401105, 127.390543));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.400198, 127.392184));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.399991, 127.392796));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.399611, 127.394931));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.399611, 127.404533));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.399378, 127.404876));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.390077, 127.404941));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.390106, 127.399537));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.392464, 127.399526));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.392455, 127.401715));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.393198, 127.401747));
                busStationModels.get(1).pointsOnPathToNextStation.add(new LocationCoordinates(36.393759, 127.401039));

                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.393816, 127.398789));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.393202, 127.398113));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.392529, 127.398135));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.392511, 127.396997));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390788, 127.396997));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390503, 127.397705));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390494, 127.399390));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390079, 127.399443));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390071, 127.398306));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390330, 127.397019));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390926, 127.395560));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.391099, 127.394562));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.391038, 127.393661));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.390227, 127.389859));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.389683, 127.387852));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.389674, 127.387466));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.389147, 127.385106));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.388620, 127.380905));
                busStationModels.get(2).pointsOnPathToNextStation.add(new LocationCoordinates(36.388589, 127.378578));

                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.388019, 127.378560));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.378894, 127.378526));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.378127, 127.378657));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.375614, 127.379440));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.374793, 127.379526));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.371958, 127.374235));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.366196, 127.365448));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.365714, 127.364349));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.365791, 127.363931));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.365980, 127.363716));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.367420, 127.362574));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.368784, 127.361769));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.369929, 127.360819));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.370484, 127.360148));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.370743, 127.359777));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.371583, 127.361408));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.372365, 127.362626));
                busStationModels.get(3).pointsOnPathToNextStation.add(new LocationCoordinates(36.372412, 127.362851));

                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.372472, 127.362725));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.371600, 127.361303));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.370727, 127.359640));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.370432, 127.360033));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.369884, 127.360720));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.369290, 127.361239));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.368744, 127.361687));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.367198, 127.362556));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.367011, 127.362769));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.365476, 127.364000));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.366159, 127.365684));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.371839, 127.374193));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.374692, 127.379625));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.375685, 127.379595));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.378677, 127.378651));
                busStationModels.get(4).pointsOnPathToNextStation.add(new LocationCoordinates(36.388524, 127.378705));

                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.388534, 127.380916));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.388448, 127.384167));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.388577, 127.385175));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.389087, 127.386881));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.389596, 127.387986));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.390175, 127.389992));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.390969, 127.393758));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.390952, 127.394820));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.390779, 127.395636));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.390140, 127.397234));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.389985, 127.398361));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.389985, 127.399509));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.392464, 127.399526));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.392455, 127.401715));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.393198, 127.401747));
                busStationModels.get(5).pointsOnPathToNextStation.add(new LocationCoordinates(36.393759, 127.401039));

                //5와 0은 동일한 정거장이므로 입력하지 않음
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
