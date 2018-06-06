package kr.ac.kaist.kyotong.model;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by yearnning on 14. 12. 20..
 * <br>버스 정거장을 나타내는 클래스
 */
public class BusStationModel {
    public int getDegree() {
        return degree;
    }

    public int getImgResource() {
        return imgResource;
    }

    public ArrayList<BusTimeModel> getVisitTimes() {
        return (ArrayList<BusTimeModel>) visitTimes.clone();
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public ArrayList<LatLng> getPathToNextStation() {
        return pathToNextStation;
    }

    public BusStationModel(String fullName, int degree, LatLng coordinates) {
        this.fullName = fullName;
        if (fullName.contains("(") && fullName.contains(")"))
            name = fullName.substring(0, fullName.indexOf("("));
        else
            name = fullName;

        this.degree = degree;
        this.coordinates = coordinates;
    }

    public BusStationModel(String fullName, int degree, LatLng coordinates, int imgResource) {
        this(fullName, degree, coordinates);
        this.imgResource = imgResource;
    }

    public String getFullName() {
        return fullName;
    }

    public String getName() {
        return name;
    }

    public void addDepartureTime(BusTimeModel busTimeModel) {
        visitTimes.add(busTimeModel);
    }

    public void addNextPointOnPath(LatLng point) {
        pathToNextStation.add(point);
    }

    /**
     * 보관하고 있는 버스 시간표를 정렬한다.
     */
    public void sortBusTimes() {
        Collections.sort(visitTimes);
    }


    private static final String TAG = BusStationModel.class.getName();
    /**
     * 짧은 이름 (지도에 표시)
     */
    private String name = "";
    /**
     * 긴 이름 (목록에 표시)
     */
    private String fullName = "";
    /**
     * 원형 버스 노선도에서 정거장을 점으로 표시할 위치를 나타내는 각도(degree)
     */
    private int degree = 0;
    /**
     * 정거장의 사진을 가리키는 그림 리소스
     */
    private int imgResource = -1;
    /**
     * 이 정거장에 모든 버스가 방문하는 시각
     */
    private ArrayList<BusTimeModel> visitTimes = new ArrayList<>();
    /**
     * 좌표
     */
    private LatLng coordinates = null;
    /**
     * 이 버스 정거장에서 다음 정거장까지의 경로를 구성하는 꼭짓점의 좌표 (두 정거장의 좌표는 포함하지 않음)
     */
    private ArrayList<LatLng> pathToNextStation = new ArrayList<>();
}
