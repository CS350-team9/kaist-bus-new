package kr.ac.kaist.kyotong.model;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by yearnning on 14. 12. 20..
 */
public class BusModel {

    private static final String TAG = "BUS";

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

    private boolean exist(int current_hour, int current_minute, int current_second) {

        int departure_abssec = busDepartureTimes.get(0).getAbsoluteSecond();
        int arrival_abssec = busArrivalTimes.get(busArrivalTimes.size() - 1).getAbsoluteSecond();
        int current_abssec = current_second + current_minute * 60 + current_hour * 3600;

        if (departure_abssec <= current_abssec && current_abssec < arrival_abssec) {
            return true;
        } else {
            return false;
        }
    }

    public int getLocation(int current_hour, int current_minute, int current_second) {

        int current_absoluteSecond = current_hour * 3600 + current_minute * 60 + current_second;

        if (exist(current_hour, current_minute, current_second)) {

            BusTimeModel busDepartureTime;
            BusTimeModel busArrivalTime;
            BusTimeModel busDepartureTime_next;

            for (int i = 0; i < this.size() - 1; i++) {

                /**
                 *
                 */
                busDepartureTime = busDepartureTimes.get(i);
                busArrivalTime = busArrivalTimes.get(i);
                busDepartureTime_next = busDepartureTimes.get(i + 1);

                /**
                 *
                 */
                if ((busDepartureTime.getAbsoluteSecond() <= current_absoluteSecond
                        && current_absoluteSecond < busDepartureTime_next.getAbsoluteSecond())) {

                    if (current_absoluteSecond >= busArrivalTime.getAbsoluteSecond()) {
                        Log.d(TAG, "CASE 1 -> " + busArrivalStations.get(i).degree);
                        return busArrivalStations.get(i).degree;
                    }

                    int left = current_absoluteSecond - busDepartureTime.getAbsoluteSecond();
                    int right = busArrivalTime.getAbsoluteSecond() - current_absoluteSecond;

                    Log.d(TAG, "CASE 2 -> " + Math.round((float) (busDepartureStations.get(i).degree * right
                            + busArrivalStations.get(i).degree * left)
                            / (float) (left + right)));
                    return Math.round((float) (busDepartureStations.get(i).degree * right
                            + busArrivalStations.get(i).degree * left)
                            / (float) (left + right));
                }
            }

            int i = this.size() - 1;
            busDepartureTime = busDepartureTimes.get(i);
            busArrivalTime = busArrivalTimes.get(i);
            if (current_absoluteSecond < busArrivalTime.getAbsoluteSecond()) {

                int left = current_absoluteSecond - busDepartureTime.getAbsoluteSecond();
                int right = busArrivalTime.getAbsoluteSecond() - current_absoluteSecond;

                Log.d(TAG, "CASE 3 -> " + Math.round((float) (busDepartureStations.get(i).degree * right
                        + busArrivalStations.get(i).degree * left)
                        / (float) (left + right)));
                return Math.round((float) (busDepartureStations.get(i).degree * right
                        + busArrivalStations.get(i).degree * left)
                        / (float) (left + right));
            }
        }

        //Log.d(TAG, "CASE 4");
        return -1;
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
