package kr.ac.kaist.kyotong;

import kr.ac.kaist.kyotong.api.BusApi;
import kr.ac.kaist.kyotong.api.BusRouteData;
import kr.ac.kaist.kyotong.model.BusModel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BusApiTest {
    @Test
    public void busApi_CheckStationCount_OLEV() {
        BusApi busApi = new BusApi(R.string.tab_kaist_olev);
        BusRouteData data = busApi.getResult();

        for (int busIndex = 0; busIndex < data.buses.size(); ++busIndex) {
            BusModel bus = data.buses.get(busIndex);
            assertEquals(
                    String.format("총 정거장 수와 버스 %d이(가) 방문하는 정거장 수가 같아야 한다.", busIndex),
                    data.stations.size(),
                    bus.stationCount()
            );
        }
    }

    @Test
    public void busApi_CheckStationCount_Munji() {
        BusApi busApi = new BusApi(R.string.tab_kaist_sunhwan);
        BusRouteData data = busApi.getResult();

        BusModel bus = data.buses.get(0);
        assertEquals(
                "총 정거장 수와 버스 0이 방문하는 정거장 수가 같아야 한다.",
                data.stations.size(),
                bus.stationCount()
        );
    }

    @Test
    public void busApi_CheckStationCount_Wolpyeong() {
        BusApi busApi = new BusApi(R.string.tab_kaist_wolpyeong);
        BusRouteData data = busApi.getResult();

        for (int busIndex = 0; busIndex < data.buses.size(); ++busIndex) {
            BusModel bus = data.buses.get(busIndex);
            assertEquals(
                    String.format("총 정거장 수와 버스 %d이(가) 방문하는 정거장 수가 같아야 한다.", busIndex),
                    data.stations.size(),
                    bus.stationCount()
            );
        }
    }
}
