package kr.ac.kaist.kyotong.model;

import java.util.ArrayList;

/**
 * Created by yearnning on 14. 12. 20..
 * <br>기점에서 종점까지 운행하는 버스를 나타내는 클래스
 */
public class BusModel {
    private static final String TAG = BusModel.class.getName();

    private ArrayList<BusStationModel> busDepartureStations = new ArrayList<BusStationModel>();
    public ArrayList<BusTimeModel> busDepartureTimes = new ArrayList<BusTimeModel>();
    private ArrayList<BusStationModel> busArrivalStations = new ArrayList<BusStationModel>();
    private ArrayList<BusTimeModel> busArrivalTimes = new ArrayList<BusTimeModel>();

    public void addPath(BusStationModel busDepartureStation, BusTimeModel busDepartureTime, BusStationModel busArrivalStation, BusTimeModel busArrivalTime) {
        busDepartureStations.add(busDepartureStation);
        busDepartureTimes.add(busDepartureTime);

        busArrivalStations.add(busArrivalStation);
        busArrivalTimes.add(busArrivalTime);
    }

    public BusStationModel getBusDepartureStation(int index) {
        return busDepartureStations.get(index);
    }

    /**
     * 이 버스가 주어진 시간에 운행 중인지 확인한다.
     *
     * @param time 시간 값
     * @return 버스 운행 여부
     */
    public boolean isActive(BusTimeModel time) {
        int seconds = time.getAbsoluteSeconds();
        int departureSeconds = busDepartureTimes.get(0).getAbsoluteSeconds();
        int arrivalSeconds = busArrivalTimes.get(busArrivalTimes.size() - 1).getAbsoluteSeconds();

        return departureSeconds <= seconds && seconds < arrivalSeconds;
    }

    /**
     * 이 버스가 주어진 시간에 정거장 A에서 정거장 B로 이동하고 있을 때, A ~ B 사이의 거리를 얼마나
     * 이동했는지 0과 1 사이의 값으로 나타낸다.
     *
     * <p>이 버스가 운행하는 시간대가 아닐 때는 0보다 작은 값을 돌려준다.</p>
     *
     * @param time 시간
     * @return 두 정거장 간의 이동 거리 (0 ~ 1), 운행 중인 버스가 아닐 경우 음수
     */
    public double getEstimatedProgressBetweenStations(BusTimeModel time) {
        int arrivingStationIndex = getLastDepartedStationAt(time);

        if (arrivingStationIndex == 0 || arrivingStationIndex == busArrivalStations.size())
            return -1.0;

        int departingStationIndex = arrivingStationIndex - 1;
        int departingTime = busDepartureTimes.get(departingStationIndex).getAbsoluteSeconds();
        int arrivingTime = busArrivalTimes.get(arrivingStationIndex).getAbsoluteSeconds();

        int seconds = time.getAbsoluteSeconds();
        return 1.0 * (arrivingTime - seconds) / (arrivingTime - departingTime);
    }

    /**
     * 이 버스가 주어진 시간에 운행 중일 때, 도착 예정인 정거장의 index를 돌려준다.
     *
     * <p>만약 {@code time}이 운행 시간 이전이라면 0을 반환하며, 운행 시간 이후라면 정거장의 수와 같은 값을 반환한다.</p>
     *
     * @param time 시간
     * @return 다음 도착 예정인 정거장의 index (0 ... 버스 정거장 수)
     */
    private int getLastDepartedStationAt(BusTimeModel time) {
        int seconds = time.getAbsoluteSeconds();

        int arrivingStationIndex = 0;
        while (arrivingStationIndex < busArrivalTimes.size()) {
            if (seconds < busArrivalTimes.get(arrivingStationIndex).getAbsoluteSeconds())
                break;
            ++arrivingStationIndex;
        }
        return arrivingStationIndex;
    }

    /**
     * 주어진 시간에 버스의 예상 위치를 계산하여 원형 그래프에 표시할 수 있는 각도로 돌려준다.
     *
     * @param time  시간
     * @return 각도 (0 <= value <= 360) / 운행 시간이 아닐 경우 -1
     */
    public int getAngle(BusTimeModel time) {
        int arrivingStationIndex = getLastDepartedStationAt(time);

        if (arrivingStationIndex == 0 || arrivingStationIndex == busArrivalStations.size())
            return -1;

        int departingStationIndex = arrivingStationIndex - 1;

        int departingTime = busDepartureTimes.get(departingStationIndex).getAbsoluteSeconds();
        int arrivingTime = busArrivalTimes.get(arrivingStationIndex).getAbsoluteSeconds();

        int seconds = time.getAbsoluteSeconds();
        double progress = 1.0 * (arrivingTime - seconds) / (arrivingTime - departingTime);

        int departingStationAngle = busDepartureStations.get(departingStationIndex).degree;
        int arrivingStationAngle = busArrivalStations.get(arrivingStationIndex).degree;

        return (int) Math.round(progress * (arrivingStationAngle - departingStationAngle) + departingStationAngle);
    }


    public BusTimeModel getDepartureTime(int index) {
        return busDepartureTimes.get(index);
    }

    public BusStationModel getBusArrivalStation(int index) {
        return busArrivalStations.get(index);
    }

    public BusTimeModel getArrivalTime(int index) {
        return busArrivalTimes.get(index);
    }

    public int size() {
        assert (busArrivalTimes.size() == busDepartureTimes.size());
        assert (busDepartureTimes.size() == busDepartureStations.size());
        assert (busArrivalStations.size() == busDepartureStations.size());
        return busArrivalTimes.size();
    }
}
