package kr.ac.kaist.kyotong;

import kr.ac.kaist.kyotong.api.BusApi;
import kr.ac.kaist.kyotong.api.BusRouteData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BusApiTest {
    @Test
    public void busApi_CheckStationCount_OLEV() {
        BusApi busApi = new BusApi(R.string.tab_kaist_olev);
        BusRouteData data = busApi.getResult();

        //BusModel.stations의 길이를 외부에서 구할 방법이 없으므로 비정상적인 방법으로 구해야 한다.
        int stationCount = 0;
        try {
            while (data.buses.get(0).getStation(stationCount) != null)
                ++stationCount;
        }
        catch (IndexOutOfBoundsException e) {}

        assertEquals("총 정거장 수와 버스가 방문하는 정거장 수가 같아야 한다.", data.stations.size(), stationCount);
    }

    @Test
    public void busApi_CheckStationCount_Munji() {
        BusApi busApi = new BusApi(R.string.tab_kaist_sunhwan);
        BusRouteData data = busApi.getResult();

        //BusModel.stations의 길이를 외부에서 구할 방법이 없으므로 비정상적인 방법으로 구해야 한다.
        int stationCount = 0;
        try {
            while (data.buses.get(0).getStation(stationCount) != null)
                ++stationCount;
        }
        catch (IndexOutOfBoundsException e) {}

        assertEquals("총 정거장 수와 버스가 방문하는 정거장 수가 같아야 한다.", data.stations.size(), stationCount);
    }

    @Test
    public void busApi_CheckStationCount_Wolpyeong() {
        BusApi busApi = new BusApi(R.string.tab_kaist_wolpyeong);
        BusRouteData data = busApi.getResult();

        //BusModel.stations의 길이를 외부에서 구할 방법이 없으므로 비정상적인 방법으로 구해야 한다.
        int stationCount = 0;
        try {
            while (data.buses.get(0).getStation(stationCount) != null)
                ++stationCount;
        }
        catch (IndexOutOfBoundsException e) {}

        assertEquals("총 정거장 수와 버스가 방문하는 정거장 수가 같아야 한다.", data.stations.size(), stationCount);
    }
}
