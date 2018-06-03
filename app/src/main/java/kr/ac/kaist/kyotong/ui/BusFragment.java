package kr.ac.kaist.kyotong.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.utils.LocationCoordinates;
import kr.ac.kaist.kyotong.utils.SizeUtils;

import kr.ac.kaist.kyotong.ui.CircularBusRouteMapView;

import kr.ac.kaist.kyotong.api.BusApi;
import kr.ac.kaist.kyotong.utils.MapManager;

import android.widget.Switch;
import android.widget.CompoundButton;

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
    private SlidingUpPanelLayout mLayout;
    private ImageButton mStationMapBtn;
    private ImageButton mStationImgBtn;
    private TextView mNameTv;

    /** Custom View */
    CircularBusRouteMapView circularBusRouteMapView;
    /** 원형 다이어그램과 구글 맵 중 하나를 토글하는 스위치 */
    private Switch toggleGoogleMapButton;
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

        circularBusRouteMapView = rootView.findViewById(R.id.circular_bus_route_view);
        toggleGoogleMapButton = rootView.findViewById(R.id.toggle_google_map_switch);

        toggleGoogleMapButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    circularBusRouteMapView.setVisibility(View.GONE);
                    mapView.setVisibility(View.VISIBLE);
                }
                else {
                    circularBusRouteMapView.setVisibility(View.VISIBLE);
                    mapView.setVisibility(View.GONE);
                }
            }
        });

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

        mapView = rootView.findViewById(R.id.mapView);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        BusApi busApi = new BusApi(R.string.tab_kaist_olev);

        ArrayList<BusStationModel> busStationModels = (ArrayList<BusStationModel>) busApi.getResult().get("busStations");

        for (BusStationModel bm : busStationModels) {
            // marker at stations;
            Location loc = bm.location;
            LatLng thisStation = new LatLng(loc.getLatitude(), loc.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(thisStation);
            markerOptions.title(bm.name);
            googleMap.addMarker(markerOptions);

            // polyline at path
            ArrayList<LocationCoordinates> pointsOnPathToNextStation = bm.pointsOnPathToNextStation;
            for (LocationCoordinates path : pointsOnPathToNextStation) {
                Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .add(thisStation, new LatLng(path.latitude, path.longitude))
                    .width(5)
                    .color(Color.WHITE));
            }
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(busStationModels.get(0).location.getLatitude(), busStationModels.get(0).location.getLongitude())));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(13));

    }
}

