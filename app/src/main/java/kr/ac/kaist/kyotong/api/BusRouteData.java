package kr.ac.kaist.kyotong.api;

import java.util.ArrayList;

import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;

/**
 * 한 버스 노선의 모든 정거장과 버스 시간표 데이터를 담은 클래스.
 * <br>BusApi가 생성하여 넘겨주는 용도
 */
public class BusRouteData {
    //단순히 데이터를 주고받는 용도이므로 getter/setter를 사용하지 않음
    public final ArrayList<BusStationModel> stations;
    public final ArrayList<BusModel> buses;

    public BusRouteData(ArrayList<BusStationModel> stations,
                        ArrayList<BusModel> buses)
    {
        this.stations = stations;
        this.buses = buses;
    }
}
