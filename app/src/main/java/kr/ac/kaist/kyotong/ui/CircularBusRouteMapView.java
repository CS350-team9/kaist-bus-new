package kr.ac.kaist.kyotong.ui;

import java.util.ArrayList;
import java.util.Calendar;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Typeface;
import android.util.Log;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.utils.SizeUtils;
import kr.ac.kaist.kyotong.utils.ViewIdGenerator;

/**
 * 원형의 그래프 상에 버스 노선도를 표시하는 View 클래스
 * <br>정거장은 원 바깥쪽에 작은 아이콘으로, 각 버스는 더 작은 아이콘으로 표시한다.
 */
public class CircularBusRouteMapView extends ConstraintLayout {
    private ArrayList<View>     busIcons            = null;
    private ArrayList<View>     stationIcons        = null;
    private ArrayList<TextView> stationNameViews    = null;
    private View                centerCircle        = null;
    private OnStationClickListener stationClickListener = null;

    public CircularBusRouteMapView(Context context) {
        super(context);
        init();
    }

    public CircularBusRouteMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularBusRouteMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.circular_bus_route_map_view, this);

        busIcons            = new ArrayList<>();
        stationIcons        = new ArrayList<>();
        stationNameViews    = new ArrayList<>();
        centerCircle        = findViewById(R.id.circular_bus_route_circle);
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
        if (centerCircle == null) {
            Log.w("updateStations", "missing centerCircle");
            centerCircle = findViewById(R.id.circular_bus_route_circle);
            if (centerCircle == null) {
                Log.w("updateStations", "cannot retrieve centerCircle");
                return;
            }
        }

        View.OnClickListener iconClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stationClickListener == null)
                    return;

                int stationIndex = (Integer) v.getTag(R.id.CIRCULAR_MAP_STATION_INDEX);

                Log.d("Station clicked: ", String.format("%d", stationIndex));

                for (View stationIcon : stationIcons)
                    stationIcon.setBackgroundResource(R.drawable.bus_fragment_station);
                stationIcons.get(stationIndex).setBackgroundResource(R.drawable.bus_fragment_station_selected);

                for (TextView stationNameView : stationNameViews)
                    stationNameView.setTypeface(null, Typeface.NORMAL);
                stationNameViews.get(stationIndex).setTypeface(null, Typeface.BOLD);

                stationClickListener.onStationClick(stationIndex);
            }
        };

        int mainContentHeight = SizeUtils.getMainContentHeight(getContext());
        final int stationIconRadius = (int) (((float) mainContentHeight) * 0.08f);

        //정거장 아이콘 수가 부족하면 추가
        while (stationIcons.size() < stations.size() - 1) {
            View stationIcon = new View(getContext());
            stationIcon.setId(ViewIdGenerator.generateViewId());
            stationIcon.setBackgroundResource(R.drawable.bus_fragment_station);
            stationIcon.setLayoutParams(new FrameLayout.LayoutParams(stationIconRadius, stationIconRadius));
            stationIcon.setTag(R.id.CIRCULAR_MAP_STATION_INDEX, stationIcons.size());           //지금 추가된 아이콘의 순서 == 정거장 인덱스
            stationIcon.setOnClickListener(iconClickListener);
            stationIcons.add(stationIcon);
            addView(stationIcon);
            Log.d("Station tag set: ", String.format("%d => %d", R.id.CIRCULAR_MAP_STATION_INDEX, stationIcons.size() - 1));

            TextView stationNameView = new TextView(getContext());
            stationNameView.setId(ViewIdGenerator.generateViewId());
            stationNameView.setTag(R.id.CIRCULAR_MAP_STATION_INDEX, stationNameViews.size());   //지금 추가된 이름표의 순서 == 정거장 인덱스
            stationNameView.setOnClickListener(iconClickListener);
            stationNameViews.add(stationNameView);
            addView(stationNameView);

            Log.d("Station tag set: ", String.format("%d => %d", R.id.CIRCULAR_MAP_STATION_INDEX, stationNameViews.size() - 1));
        }

        //정거장 아이콘 수가 너무 많으면 삭제
        while (stationIcons.size() > stations.size() - 1) {
            removeView(stationIcons.remove(stationIcons.size() - 1));
            removeView(stationNameViews.remove(stationNameViews.size() - 1));
        }

        //정거장 이름표의 텍스트 설정
        for (int i = 0; i < stations.size() - 1; ++i)
            stationNameViews.get(i).setText(stations.get(i).getName());

        int centerCircleRadius      = centerCircle.getWidth() / 2;
        int stationIconOffset       = (int) (centerCircleRadius * 1.25);
        int stationNameViewOffset   = (int) (centerCircleRadius * 1.65);

        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(this);
        for (int i = 0; i < stations.size() - 1; ++i) {
            float stationAngle = (float) (stations.get(i).getDegree());
            constraints.constrainCircle(stationIcons.get(i).getId(), getId(), stationIconOffset, stationAngle);
            int stationNameViewAdjustment = (int) Math.round(
                    0.015 * mainContentHeight * stations.get(i).getName().length()
                            * Math.abs(Math.sin(Math.toRadians(stationAngle)))
            );
            constraints.constrainCircle(stationNameViews.get(i).getId(), getId(), stationNameViewOffset + stationNameViewAdjustment, stationAngle);
        }
        constraints.applyTo(this);
    }

    /**
     * 버스 아이콘의 위치를 새로고침한다.
     * @param buses 모든 버스의 목록
     */
    public void updateBuses(ArrayList<BusModel> buses) {
        if (centerCircle == null) {
            Log.w("updateBuses", "missing centerCircle");
            centerCircle = findViewById(R.id.circular_bus_route_circle);
            if (centerCircle == null) {
                Log.w("updateBuses", "cannot retrieve centerCircle");
                return;
            }
        }

        //현재 운행 중인 버스를 추려낸다
        ArrayList<BusModel> activeBuses     = new ArrayList<>();
        ArrayList<Float>    activeBusAngles = new ArrayList<>();
        Calendar now = Calendar.getInstance();
        for (BusModel bus : buses) {
            float busAngle = bus.getAngle(now);
            if (busAngle >= 0) {
                activeBuses.add(bus);
                activeBusAngles.add(busAngle);
            }
        }

        while (busIcons.size() < activeBuses.size()) {
            View busIcon = createBusIconView();
            busIcons.add(busIcon);
            addView(busIcon);
        }

        while (busIcons.size() > activeBuses.size()) {
            removeView(busIcons.remove(busIcons.size() - 1));
        }

        int centerCircleRadius  = centerCircle.getWidth() / 2;
        int busIconOffset       = centerCircleRadius;

        ConstraintSet constraints = new ConstraintSet();
        constraints.clone(this);
        for (int i = 0; i < activeBuses.size(); ++i) {
            constraints.constrainCircle(busIcons.get(i).getId(), getId(), busIconOffset, activeBusAngles.get(i));
        }
        constraints.applyTo(this);
    }

    private View createBusIconView() {
        int busIconSize = SizeUtils.dpToPixels(getContext(), 10.0f);

        View busIcon = new View(getContext());
        busIcon.setId(ViewIdGenerator.generateViewId());
        busIcon.setBackgroundResource(R.drawable.bus_fragment_bus);
        busIcon.setLayoutParams(new FrameLayout.LayoutParams(busIconSize, busIconSize));

        return busIcon;
    }
}
