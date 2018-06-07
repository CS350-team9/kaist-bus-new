package kr.ac.kaist.kyotong.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;

import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.utils.GoogleMapsUtils;

/**
 * 구글 지도 위에 버스 노선과 버스의 위치를 표시하는 View
 * <br>정거장과 버스는 Marker로, 이동 경로는 Polyline으로 표시한다.
 */
public class GoogleMapBusRouteMapView extends MapView {
    private static String TAG = GoogleMapBusRouteMapView.class.getName();
    private GoogleMap googleMap = null;
    private ArrayList<Marker> stationMarkers = new ArrayList<>();
    private ArrayList<Polyline> stationPathPolylines = new ArrayList<>();
    private ArrayList<Marker> busMarkers = new ArrayList<>();
    private OnStationClickListener stationClickListener = null;
    private Marker lastClickedStation = null;

    public GoogleMapBusRouteMapView(Context context) {
        super(context);
    }

    public GoogleMapBusRouteMapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public GoogleMapBusRouteMapView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    public GoogleMapBusRouteMapView(Context context, GoogleMapOptions googleMapOptions) {
        super(context, googleMapOptions);
    }

    public interface OnStationClickListener {
        void onStationClick(int stationIndex);
    }

    /**
     * 정거장 아이콘을 클릭했을 때 이벤트를 수신하는 listener를 설정한다.
     * @param listener 이벤트 수신자
     */
    public void setOnStationClickListener(OnStationClickListener listener) {
        stationClickListener = listener;
    }


    /**
     * 정거장 아이콘 및 이름의 위치를 새로고침한다.
     * @param stations 아이콘으로 나타낼 정거장
     */
    public void updateStations(ArrayList<BusStationModel> stations) {
        if (googleMap == null) {
            Log.w(TAG, "GoogleMap not initialized");
            final ArrayList<BusStationModel> currentStations = (ArrayList<BusStationModel>) stations.clone();
            getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    setGoogleMap(gMap);
                    updateStations(currentStations);
                }
            });
            return;
        }

        //정거장 Marker/Polyline 갯수가 부족하면 추가
        while (stationMarkers.size() < stations.size()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            markerOptions.position(new LatLng(0, 0));       //초기 좌표를 설정하지 않으면 오류 발생
            Marker stationMarker = googleMap.addMarker(markerOptions);
            stationMarker.setTag(stationMarkers.size());    //지금 추가된 Marker의 순서 == 정거장 인덱스
            stationMarkers.add(stationMarker);

            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.color(Color.rgb(10, 53, 115));
            polylineOptions.width(15);
            stationPathPolylines.add(googleMap.addPolyline(polylineOptions));
        }

        //정거장 Marker/Polyline 갯수가 너무 많으면 삭제
        while (stationMarkers.size() > stations.size()) {
            stationMarkers.remove(stationMarkers.size() - 1).remove();
            stationPathPolylines.remove(stationPathPolylines.size() - 1).remove();
        }

        //모든 점이 한 화면에 들어올 수 있는 지도의 경계를 측정한다
        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

        //정거장 Marker와 Polyline의 위치를 업데이트한다
        for (int i = 0; i < stations.size(); ++i) {
            //정거장 Marker의 이름과 좌표를 업데이트한다.
            BusStationModel station = stations.get(i);
            Marker stationMarker = stationMarkers.get(i);

            stationMarker.setTitle(station.getName());

            LatLng stationPos = station.getCoordinates();
            stationMarker.setPosition(stationPos);

            boundsBuilder.include(stationPos);  //정거장의 좌표를 경계선 측성에 사용한다.

            ArrayList<LatLng> polylinePath = (ArrayList<LatLng>) station.getPathToNextStation().clone();

            for (LatLng pointOnPath : polylinePath) //정거장 간 경로의 좌표를 경계선 측정에 사용한다.
                boundsBuilder.include(pointOnPath);

            polylinePath.add(0, stationPos);
            BusStationModel nextStation = stations.get((i + 1) % stations.size());
            polylinePath.add(nextStation.getCoordinates());
            stationPathPolylines.get(i).setPoints(polylinePath);
        }

        //측정한 지도 경계를 바탕으로 카메라 위치를 조정한다.
        int padding = 100;
        final CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), padding);
        googleMap.animateCamera(cu);
    }


    /**
     * 버스 아이콘의 위치를 새로고침한다.
     * @param buses 모든 버스의 목록
     */
    public void updateBuses(ArrayList<BusModel> buses) {
        if (googleMap == null) {
            Log.w(TAG, "GoogleMap not initialized");
            final ArrayList<BusModel> currentBuses = (ArrayList<BusModel>) buses.clone();
            getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    setGoogleMap(gMap);
                    updateBuses(currentBuses);
                }
            });
            return;
        }

        //현재 운행 중인 버스를 추려낸다
        ArrayList<BusModel> activeBuses = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (BusModel bus : buses) {
            if (bus.isActive(now))
                activeBuses.add(bus);
        }


        //버스 Marker 수가 부족하면 추가
        while (busMarkers.size() < activeBuses.size()) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title("Bus");
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("ic_bus", 56, 56)));
            markerOptions.position(new LatLng(0, 0));       //초기 좌표를 설정하지 않으면 오류 발생
            busMarkers.add(googleMap.addMarker(markerOptions));
        }

        //버스 Marker 수가 너무 많으면 삭제
        while (busMarkers.size() < activeBuses.size())
            busMarkers.get(busMarkers.size() - 1).remove();

        //버스 Marker의 위치 업데이트
        for (int i = 0; i < activeBuses.size(); ++i) {
            BusModel bus = activeBuses.get(i);
            BusStationModel prevStation = bus.getPrevStationAt(now);
            ArrayList<LatLng> polylinePath = (ArrayList<LatLng>) prevStation.getPathToNextStation().clone();
            polylinePath.add(0, prevStation.getCoordinates());
            polylinePath.add(bus.getNextStationAt(now).getCoordinates());

            //버스의 예상 좌표를 계산한다
            busMarkers.get(i).setPosition(
                    GoogleMapsUtils.interpolate(polylinePath, bus.getEstimatedProgressBetweenStations(now))
            );
        }
    }


    private Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(
                getResources(),
                getResources().getIdentifier(iconName, "drawable", getContext().getPackageName())
        );
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }


    /**
     * 현재 인스턴스가 주어진 GoogleMap 객체를 참조하게 만들고, Marker 클릭에 대한 이벤트 리스너를 설정한다.
     *
     * @param gMap GoogleMap 객체
     */
    private void setGoogleMap(GoogleMap gMap) {
        if (googleMap == null) {
            googleMap = gMap;
            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Object tag = marker.getTag();
                    if (tag != null) { //버스 정거장 마커라고 가정한다
                        int stationIndex = (Integer) tag;

                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        if (lastClickedStation != null)
                            lastClickedStation.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        lastClickedStation = marker;

                        if (stationClickListener != null)
                            stationClickListener.onStationClick(stationIndex);
                    }

                    return true; //지도의 카메라가 마커로 이동하는 것과 정보창이 열리는 것을 방지함
                }
            });
        }
    }
}
