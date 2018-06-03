package kr.ac.kaist.kyotong.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.utils.SizeUtils;

import kr.ac.kaist.kyotong.ui.CircularBusRouteMapView;

import kr.ac.kaist.kyotong.api.BusApi;
import kr.ac.kaist.kyotong.utils.MapManager;

//TODO Google Map 관련 코드를 Custom View 클래스에 몰아넣기
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

/**
 * 메인 화면의 버스 노선 탭에 대응하는 노선도를 표시하는 Fragment
 */
public class BusFragment extends Fragment implements OnMapReadyCallback {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = "BusFragment";
    private static final String ARG_SECTION_NUMBER = "arg_section_number";
    private static final String ARG_POSITION = "arg_position";

    /**
     *
     */
    private TimerTask mBusTimerTask;
    private BusApiTask mBusApiTask = null;
    private Timer mTimer;

    /**
     *
     */
    private boolean mShowErrorView = true;
    private boolean mShowErrorViewBuffer = false;
    private boolean mUpdateStationRunning = false;

    /**
     *
     */
    private int mPosition = -1;

    /*
    For Performance
     */
    private int mContentWidth = -1;
    private int mContentHeight = -1;

    /*

     */
    ArrayList<BusStationModel> busStationModels = new ArrayList<BusStationModel>();
    ArrayList<BusModel> buses = new ArrayList<BusModel>();
    private int current_hour = -1;
    private int current_minute = -1;
    private int current_second = -1;

    /**
     *
     */
    private View mShadowView;
//    private ImageView mCircleIv;
    private SlidingUpPanelLayout mLayout;
    private ImageButton mStationMapBtn;
    private ImageButton mStationImgBtn;
    private TextView mNameTv;
    private FrameLayout mMainLayout;
//    /** 원형 버스 노선도 위에서 움직이는 버스를 나타내는 아이콘의 View */
//    private View[] mBusView = new View[3];
//    private ArrayList<TextView> mStationTvs = new ArrayList<TextView>();
//    private ArrayList<View> mStationViews = new ArrayList<View>();
    /** Custom View */
    CircularBusRouteMapView circularBusRouteMapView;
    private ListView mLv;
    private LvAdapter mLvAdapter;

    private View mErrorView;
    private TextView mErrorTv;
    private ProgressBar mErrorPb;

    //TODO Google Map 관련 코드
    private MapView mapView = null;


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static BusFragment newInstance(int position, int title_id) {
        BusFragment fragment = new BusFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putInt(ARG_SECTION_NUMBER, title_id);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bus_fragment, container, false);
        View headerView = inflater.inflate(R.layout.base_fragment_lv_header, null, false);
        View footerView = inflater.inflate(R.layout.bus_fragment_lv_footer, null, false);

        /**
         *
         */
        int title_id = getArguments().getInt(ARG_SECTION_NUMBER);
        mPosition = getArguments().getInt(ARG_POSITION);

        /**
         *
         */
        mContentWidth = SizeUtils.windowWidth(getActivity());
        mContentHeight = SizeUtils.getMainContentHeight(getActivity());

        /**
         *
         */
        mLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.sliding_layout);

//        mMainLayout = (FrameLayout) rootView.findViewById(R.id.main_layout);
        circularBusRouteMapView = (CircularBusRouteMapView) rootView.findViewById(R.id.circular_bus_route_view);
//        mCircleIv = (ImageView) rootView.findViewById(R.id.circle_iv);
//        mBusView[0] = rootView.findViewById(R.id.bus_view_0);
//        mBusView[1] = rootView.findViewById(R.id.bus_view_1);
//        mBusView[2] = rootView.findViewById(R.id.bus_view_2);

        mStationMapBtn = (ImageButton) rootView.findViewById(R.id.station_map_btn);
        mStationImgBtn = (ImageButton) rootView.findViewById(R.id.station_img_btn);
        mNameTv = (TextView) rootView.findViewById(R.id.name_tv);
        mShadowView = rootView.findViewById(R.id.shadow_view);
        mLv = (ListView) rootView.findViewById(R.id.lv);

        mErrorView = rootView.findViewById(R.id.error_view);
        mErrorTv = (TextView) mErrorView.findViewById(R.id.error_tv);
        mErrorPb = (ProgressBar) mErrorView.findViewById(R.id.error_pb);

        /**
         *
         */
        mLv.addHeaderView(headerView);
        mLv.addFooterView(footerView);

        /**
         *
         */
//        initBus();

        /**
         *
         */
//        mMainLayout.setLayoutParams(new LinearLayout.LayoutParams(mContentWidth, mContentHeight));

        /**
         *
         */
        //mLayout.setEnableDragViewTouchEvents(true);
        mLayout.setPanelHeight(SizeUtils.getPanelHeight(getActivity()));
        mLayout.setScrollableView(mLv);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                mShadowView.setAlpha(slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel,
                                            SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {

                if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                    ((MainActivity) getActivity()).notifyPanelCollpased(mPosition);
                } else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    ((MainActivity) getActivity()).notifyPanelExpand(mPosition);
                }

            }
        });

        /**
         *
         */
        ArrayList<BusTimeModel> busTimeModels = new ArrayList<>();
        mLvAdapter = new LvAdapter(getActivity(), R.layout.bus_fragment_lv, busTimeModels);
        mLv.setAdapter(mLvAdapter);

        /**
         *
         */
        mBusApiTask = new BusApiTask();
        mBusApiTask.execute(title_id);


        /**
         *
         */
//        rootView.findViewById(R.id.bab_ad_view).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openBab();
//            }
//        });

        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        /**
         *
         */
        return rootView;
    }

    private void openBab() {
        try {
            Intent launchIntent = getActivity().getPackageManager().getLaunchIntentForPackage("kr.ac.kaist.bab");
            startActivity(launchIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=kr.ac.kaist.bab"));
            startActivity(intent);
        }
    }

    /**
     * @param show
     */
    private void showErrorView(final boolean show, String msg) {

        if (show) {
            mLv.setVisibility(View.GONE);
            mErrorView.setVisibility(View.VISIBLE);
            mErrorTv.setText(msg);

            if (msg.equals("")) {
                mErrorPb.setVisibility(View.VISIBLE);
            } else {
                mErrorPb.setVisibility(View.GONE);
            }

        } else {
            mLv.setVisibility(View.VISIBLE);
            mErrorPb.setVisibility(View.VISIBLE);
            mErrorView.setVisibility(View.GONE);
        }
    }


    /**
     *
     */
    public class BusApiTask extends AsyncTask<Integer, HashMap<String, Object>, HashMap<String, Object>> {

        @Override
        protected void onPreExecute() {
            mShowErrorView = true;
            showErrorView(true, "");
        }

        /**
         * @param params
         * @return
         */
        @Override
        protected HashMap<String, Object> doInBackground(Integer... params) {

            BusApi busApi = new BusApi(params[0]);
            return busApi.getResult();
        }

        /**
         * @param maps
         */
        @Override
        protected void onPostExecute(HashMap<String, Object> maps) {

            /**
             *  Setting results
             */
            busStationModels.addAll((ArrayList<BusStationModel>) maps.get("busStations"));
            buses.addAll((ArrayList<BusModel>) maps.get("buses"));

            /**
             *
             */
//            initStations(busStationModels);
            circularBusRouteMapView.setOnStationClickListener(new CircularBusRouteMapView.OnStationClickListener() {
                @Override
                public void onStationClick(int stationIndex) {
                    updateStation(stationIndex);
                }
            });
            circularBusRouteMapView.updateStations(busStationModels);

            updateStation(0);

            mBusTimerTask = new BusTimerTask();
            mTimer = new Timer();
            mTimer.schedule(mBusTimerTask, ( 1000 - Calendar.getInstance().get(Calendar.MILLISECOND)) % 1000, 1000);

            mBusApiTask = null;
        }

        /**
         *
         */
        @Override
        protected void onCancelled() {
            super.onCancelled();
            mBusApiTask = null;
        }
    }

//    /**
//     *
//     */
//    private void initBus() {
//
//        int busViewRadius = (int) (((float) mContentHeight) * 0.02f);
//        int circleRadius = (int) (((float) mContentHeight) * 0.20f);
//
//        FrameLayout.LayoutParams params;
//        params = new FrameLayout.LayoutParams(circleRadius * 2, circleRadius * 2);
//        params.gravity = Gravity.CENTER;
//        mCircleIv.setLayoutParams(params);
//
//        for (int i = 0; i < 3; i++) {
//            mBusView[i].setPivotX(busViewRadius);
//            mBusView[i].setPivotY(busViewRadius + circleRadius);
//            params = new FrameLayout.LayoutParams(busViewRadius * 2, busViewRadius * 2);
//            params.gravity = Gravity.CENTER;
//            params.bottomMargin = circleRadius;
//            mBusView[i].setLayoutParams(params);
//        }
//    }

//    /**
//     * @param busStationModels
//     */
//    private void initStations(ArrayList<BusStationModel> busStationModels) {
//
//        FrameLayout.LayoutParams params;
//        int station_tv_padding = (int) ((((float) mContentHeight) * 0.05f));
//        float station_tv_radius_x = ((float) mContentHeight) * 0.45f;
//        float station_tv_radius_y = ((float) mContentHeight) * 0.37f;
//        int station_radius = (int) (((float) mContentHeight) * 0.06f);
//        float station_view_radius = ((float) mContentHeight) * 0.27f;
//
//        for (int i = 0; i < (busStationModels.size() - 1); i++) {
//
//            /**
//             *
//             */
//            final int index = i;
//            BusStationModel busStationModel = busStationModels.get(i);
//
//            /**
//             *
//             */
//            TextView stationTv = new TextView(getActivity());
//            params = new FrameLayout.LayoutParams(-2, -2);
//            params.gravity = Gravity.CENTER;
//            params.leftMargin = (int) (station_tv_radius_x * Math.sin(2 * Math.PI * ((float) busStationModel.degree) / 360f));
//            params.bottomMargin = (int) (station_tv_radius_y * Math.cos(2 * Math.PI * ((float) busStationModel.degree) / 360f));
//            stationTv.setPadding(station_tv_padding, station_tv_padding, station_tv_padding, station_tv_padding);
//            stationTv.setLayoutParams(params);
//            stationTv.setText(busStationModel.name);
//            stationTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, station_radius);
//            stationTv.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    updateStation(index);
//                }
//            });
//            mMainLayout.addView(stationTv, 2);
//            mStationTvs.add(stationTv);
//
//            /**
//             *
//             */
//            View stationView = new View(getActivity());
//            params = new FrameLayout.LayoutParams(station_radius, station_radius);
//            params.gravity = Gravity.CENTER;
//            params.leftMargin = (int) (station_view_radius * Math.sin(2 * Math.PI * ((float) busStationModel.degree) / 360f));
//            params.bottomMargin = (int) (station_view_radius * Math.cos(2 * Math.PI * ((float) busStationModel.degree) / 360f));
//            stationView.setLayoutParams(params);
//            stationView.setBackgroundResource(R.drawable.bus_fragment_station);
//            stationView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    updateStation(index);
//                }
//            });
//            mMainLayout.addView(stationView, 2);
//            mStationViews.add(stationView);
//
//            /**
//             *
//             */
//            View stationBtn = new View(getActivity());
//            params = new FrameLayout.LayoutParams(station_radius * 5 / 2, station_radius * 5 / 2);
//            params.gravity = Gravity.CENTER;
//            params.leftMargin = (int) (station_view_radius * Math.sin(2 * Math.PI * ((float) busStationModel.degree) / 360f));
//            params.bottomMargin = (int) (station_view_radius * Math.cos(2 * Math.PI * ((float) busStationModel.degree) / 360f));
//            stationBtn.setLayoutParams(params);
//            stationBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    updateStation(index);
//                }
//            });
//            mMainLayout.addView(stationBtn, 2);
//        }
//    }

    /**
     * index에 해당하는 정거장이 클릭되었을 때 UI를 업데이트한다.
     * @param index 클릭한 정거장을 나타내는 숫자 (0부터 시작)
     */
    private void updateStation(int index) {

        mUpdateStationRunning = true;
        mShowErrorView = true;
        showErrorView(true, "");

        /**
         *
         */
        final BusStationModel busStationModel = busStationModels.get(index);

        /**
         *
         */
        mLvAdapter.busTimeModels.clear();
        mLvAdapter.busTimeModels.addAll(busStationModels.get(index).departureTimes);
        mLvAdapter.notifyDataSetChanged();

        for (BusTimeModel busTimeModel : mLvAdapter.busTimeModels) {
            Log.d(TAG, "time_str -> " + busTimeModel.time_str + ", header -> " + busTimeModel.header);
        }
        mNameTv.setText(busStationModel.name_full);

        /**
         *
         */
        if (busStationModel.location != null) {
            mStationMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapManager mm = new MapManager(busStationModel.location);
                    mm.showMap(getActivity());

                }
            });
            mStationMapBtn.setVisibility(View.VISIBLE);
        } else {
            mStationMapBtn.setVisibility(View.GONE);
        }

        /**
         *
         */
        if (busStationModel.img_resource != -1) {
            mStationImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageActivity.startIntent(getActivity(), busStationModel.img_resource);
                }
            });
            mStationImgBtn.setVisibility(View.VISIBLE);
        } else {
            mStationImgBtn.setVisibility(View.GONE);
        }

//        /**
//         *
//         */
//        for (View stationView : mStationViews) {
//            stationView.setBackgroundResource(R.drawable.bus_fragment_station);
//        }
//        mStationViews.get(index).setBackgroundResource(R.drawable.bus_fragment_station_selected);
//
//        for (TextView stationTv : mStationTvs) {
//            stationTv.setTypeface(null, Typeface.NORMAL);
//        }
//        mStationTvs.get(index).setTypeface(null, Typeface.BOLD);

        /**
         *
         */
        mUpdateStationRunning = false;

    }


    /**
     *
     */
    private class BusTimerTask extends TimerTask {

        private boolean show_colon = false;
        Handler handler = new Handler();

        @Override
        public void run() {

            try {
                /**
                 *
                 */
                Calendar c = Calendar.getInstance();

                int absolute_second = c.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000
                        + c.get(Calendar.MINUTE) * 60 * 1000
                        + c.get(Calendar.SECOND) * 1000
                        + c.get(Calendar.MILLISECOND);

                absolute_second /= 1000;

                current_hour = (absolute_second / 3600) % 24;
                if (current_hour < 4) {
                    current_hour += 24;
                }

                current_minute = (absolute_second / 60) % 60;
                current_second = absolute_second % 60;
                show_colon = !show_colon;

                mLvAdapter.updateLeftTimeStrInTickets();

                handler.post(new Runnable() {
                                 public void run() {
                                     circularBusRouteMapView.updateBuses(buses, current_hour, current_minute, current_second);
//                                     setCurrentBusLocations(getCurrentBusLocations());
                                     mLvAdapter.notifyDataSetChanged();
                                     if (mShowErrorView && !mUpdateStationRunning && mBusApiTask == null) {


                                         if (mShowErrorViewBuffer) {
                                             mShowErrorView = false;
                                             showErrorView(false, "");
                                             mShowErrorViewBuffer = false;
                                         } else {
                                             mShowErrorViewBuffer = true;
                                         }

                                     }
                                 }
                             }

                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


//    /**
//     * 현재 운행 중인 버스의 위치를 나타낸 각도의 배열을 돌려준다.
//     * @return 버스의 위치를 나타낸 각도의 배열
//     */
//    private int[] getCurrentBusLocations() {
//
//        int[] locations = {-1, -1, -1};
//        int cur = 0;
//        for (BusModel busModel : buses) {
//            int location = busModel.getLocation(current_hour, current_minute, current_second);
//            if (location != -1) {
//                locations[cur] = location;
//                cur++;
//            }
//        }
//
//        return locations;
//    }

//    /**
//     * 주어진 각도를 바탕으로 각 버스 아이콘의 표시 여부 및 위치를 변경한다.
//     * @param locations 각 버스 아이콘의 각도를 나타낸 배열 (-1일 경우 버스 아이콘 숨김)
//     */
//    private void setCurrentBusLocations(int[] locations) {
//
//        for (int i = 0; i < 3; i++) {
//
//            if (locations[i] == -1) {
//                mBusView[i].setVisibility(View.GONE);
//            } else {
//                mBusView[i].setVisibility(View.VISIBLE);
//                mBusView[i].setRotation(locations[i]);
//            }
//        }
//    }

    /**
     * ListView Apdater Setting
     */
    private class LvAdapter extends ArrayAdapter<BusTimeModel> {
        private static final String TAG = "BusFragment LvAdapter";

        /**
         *
         */
        private ViewHolder viewHolder = null;
        public ArrayList<BusTimeModel> busTimeModels;
        private int textViewResourceId;

        /**
         * @param context
         * @param textViewResourceId
         * @param articles
         */
        public LvAdapter(Activity context, int textViewResourceId,
                         ArrayList<BusTimeModel> articles) {
            super(context, textViewResourceId, articles);

            this.textViewResourceId = textViewResourceId;
            this.busTimeModels = articles;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public int getCount() {
            return busTimeModels.size();
        }

        @Override
        public BusTimeModel getItem(int position) {
            return busTimeModels.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

			/*
             * UI Initiailizing : View Holder
			 */

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(textViewResourceId, null);

                viewHolder = new ViewHolder();

                /**
                 * Find View By ID
                 */
                viewHolder.mHeaderTv = (TextView) convertView.findViewById(R.id.header_tv);

                viewHolder.mContentView = convertView.findViewById(R.id.content_view);
                viewHolder.mTimeTv = (TextView) convertView.findViewById(R.id.time_tv);
                viewHolder.mLeftTv = (TextView) convertView.findViewById(R.id.left_tv);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            final BusTimeModel busTimeModel = this.getItem(position);

			/*
             * Data Import and export
			 */

            if (busTimeModel.header != null) {
                viewHolder.mContentView.setVisibility(View.GONE);
                viewHolder.mHeaderTv.setVisibility(View.VISIBLE);
                viewHolder.mHeaderTv.setText(busTimeModel.header);
                viewHolder.mHeaderTv.setTextColor(busTimeModel.header_textColor);

            } else {
                viewHolder.mContentView.setVisibility(View.VISIBLE);
                viewHolder.mHeaderTv.setVisibility(View.GONE);

                if (busTimeModel.indicator == null) {
                    viewHolder.mTimeTv.setText(busTimeModel.time_str);
                    viewHolder.mLeftTv.setVisibility(View.VISIBLE);
                    viewHolder.mLeftTv.setText(busTimeModel.left_time_str);
                } else {
                    viewHolder.mTimeTv.setText(busTimeModel.indicator);
                    viewHolder.mLeftTv.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

        private class ViewHolder {
            TextView mHeaderTv;

            View mContentView;
            TextView mTimeTv;
            TextView mLeftTv;
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        private void updateLeftTimeStrInTickets() {

            /**
             *
             */
            for (BusTimeModel busTimeModel : busTimeModels) {
                if (busTimeModel.header != null) {
                    continue;
                }
                busTimeModel.updateLeftTimeStr(current_hour, current_minute, current_second);
            }

            if ((busTimeModels.size() > 1) && (busTimeModels.get(0).indicator != null)
                    && (current_hour >= 24)) {
                busTimeModels.remove(0);
                busTimeModels.remove(0);

                for (BusStationModel busStationModel : busStationModels) {
                    busStationModel.updateHeader();
                }
            }

            while ((busTimeModels.size() > 0)
                    && (busTimeModels.get(0).left_time_str != null)
                    && (busTimeModels.get(0).left_time_str.equals(" - "))) {
                busTimeModels.remove(0);
            }

            if ((busTimeModels.size() > 0) && (busTimeModels.get(0).header != null)) {
                BusTimeModel busTimeModel = new BusTimeModel();
                busTimeModel.indicator = "당일 버스 운행은 종료되었습니다";
                busTimeModels.add(0, busTimeModel);
            }
        }
    }

    /**
     *
     */
    @Override
    public void onDestroy() {

        if (mBusApiTask != null) {
            mBusApiTask.cancel(true);
            mBusApiTask = null;
        }

        if (mTimer != null) {
            mTimer.cancel();
        }

        if (mBusTimerTask != null) {
            mBusTimerTask.cancel();
            mBusTimerTask = null;
        }

        mapView.onDestroy();
        super.onDestroy();
    }


    //MapView를 사용하기 위해 필요함
    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    public void onMapReady(GoogleMap googleMap) {

    }
}

