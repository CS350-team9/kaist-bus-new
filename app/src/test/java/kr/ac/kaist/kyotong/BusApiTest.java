package kr.ac.kaist.kyotong;

import kr.ac.kaist.kyotong.api.BusApi;
import kr.ac.kaist.kyotong.api.BusRouteData;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BusApiTest {
    @Test
    public void busApi_Sample_PrintDebug() {
        assertTrue(true);
        assertFalse(1 == 0);
        BusApi busApi = new BusApi(R.string.tab_kaist_olev);
        BusRouteData data = busApi.getResult();

        assertEquals(62, data.buses.size());
        assertEquals(10, data.stations.size());
    }
}
