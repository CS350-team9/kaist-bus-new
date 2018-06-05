package kr.ac.kaist.kyotong.model;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by yearnning on 14. 12. 20..
 * <br>기점에서 종점까지 운행하는 버스를 나타내는 클래스
 */
public class BusModel {
    private static final String TAG = BusModel.class.getName();

    /** 기점부터 종점까지의 정거장 목록 */
    private ArrayList<BusStationModel> stations = new ArrayList<>();
    /** 기점부터 종점까지 각 정거장에서 출발/도착하는 시각. */
    private ArrayList<BusTimeModel> visitTimes = new ArrayList<>();

    public void addNextStation(BusStationModel nextStation, BusTimeModel visitTime) {
        stations.add(nextStation);
        visitTimes.add(visitTime);
    }

    /**
     * 이 버스가 {@code index+1}번째로 방문하는 정거장을 돌려준다.
     *
     * @param index 정거장을 나타내는 숫자 (0 = 기점, 마지막 = 종점)
     * @return 정거장 객체
     */
    public BusStationModel getStation(int index) { return stations.get(index); }

    /**
     * 이 버스가 {@code index+1}번째로 방문하는 정거장에 도착/출발하는 시간을 돌려준다.
     *
     * @param index 정거장을 나타내는 숫자 (0 = 기점, 마지막 = 종점)
     * @return 시간
     */
    public BusTimeModel getVisitTime(int index) {
        return visitTimes.get(index);
    }

    /**
     * 이 버스가 방문하는 정거장의 수를 돌려준다.
     *
     * @return 정거장 수 (>= 0)
     */
    public int stationCount() { return stations.size(); }

    /**
     * 이 버스가 주어진 시간에 운행 중인지 확인한다.
     *
     * @param time 시간 값
     * @return 버스 운행 여부
     */
    public boolean isActive(BusTimeModel time) {
        long seconds = time.getAbsoluteSeconds();
        long departureSeconds = visitTimes.get(0).getAbsoluteSeconds();
        long arrivalSeconds = visitTimes.get(visitTimes.size() - 1).getAbsoluteSeconds();

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
    public double getEstimatedProgressBetweenStations(Calendar time) {
        int nextStationIndex = getNextStationAt(time);

        if (nextStationIndex == 0 || nextStationIndex == stations.size())
            return -1.0;

        long departingTime = visitTimes.get(nextStationIndex - 1).getAbsoluteSeconds();
        long arrivingTime = visitTimes.get(nextStationIndex).getAbsoluteSeconds();

        long seconds = time.getTimeInMillis() / 1000;
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
    private int getNextStationAt(Calendar time) {
        long seconds = time.getTimeInMillis() / 1000;

        int nextStationIndex = 0;
        while (nextStationIndex < stations.size()) {
            if (seconds < visitTimes.get(nextStationIndex).getAbsoluteSeconds())
                break;
            ++nextStationIndex;
        }
        return nextStationIndex;
    }

    /**
     * 주어진 시간에 버스의 예상 위치를 계산하여 원형 그래프에 표시할 수 있는 각도로 돌려준다.
     *
     * @param time  시간
     * @return 각도 (0 <= value <= 360) / 운행 시간이 아닐 경우 -1
     */
    public int getAngle(Calendar time) {
        int nextStationIndex = getNextStationAt(time);

        if (nextStationIndex == 0 || nextStationIndex == stations.size())
            return -1;

        long departingTime = visitTimes.get(nextStationIndex - 1).getAbsoluteSeconds();
        long arrivingTime = visitTimes.get(nextStationIndex).getAbsoluteSeconds();

        long seconds = time.getTimeInMillis() / 1000;
        double progress = 1.0 * (arrivingTime - seconds) / (arrivingTime - departingTime);

        int departingStationAngle = stations.get(nextStationIndex - 1).degree;
        int arrivingStationAngle = stations.get(nextStationIndex).degree;

        return (int) Math.round(progress * (arrivingStationAngle - departingStationAngle) + departingStationAngle);
    }
}
