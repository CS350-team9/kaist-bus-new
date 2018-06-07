package kr.ac.kaist.kyotong.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.adapters.BusTimeListAdapter;
import kr.ac.kaist.kyotong.alarm.Alarms;
import kr.ac.kaist.kyotong.api.BusRouteData;
import kr.ac.kaist.kyotong.model.BusModel;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.utils.SizeUtils;

import kr.ac.kaist.kyotong.api.BusApi;
import kr.ac.kaist.kyotong.utils.MapManager;

import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.Toast;

/**
 * 메인 화면의 버스 노선 탭에 대응하는 노선도를 표시하는 Fragment
 */
public class BusFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String TAG = BusFragment.class.getName();
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
    private ListView mLv;
    private BusTimeListAdapter mLvAdapter;

    private View mErrorView;
    private TextView mErrorTv;
    private ProgressBar mErrorPb;

    //TODO Google Map 관련 코드
    private GoogleMapBusRouteMapView mapView = null;

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

        Log.w("titleChange", Integer.toString(title_id));

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
        mapView = rootView.findViewById(R.id.mapView);
        mapView.setVisibility(View.INVISIBLE);
        mapView.onCreate(savedInstanceState);

        Switch toggleGoogleMapButton = rootView.findViewById(R.id.toggle_google_map_switch);

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

        mLvAdapter = new BusTimeListAdapter(getActivity(), R.layout.bus_fragment_lv);
        mLv.setAdapter(mLvAdapter);
        mLv.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String debugMessage = String.format("List long-clicked: position %d / ID %d", position, id);
                Toast t = Toast.makeText(getActivity(), debugMessage, Toast.LENGTH_SHORT);
                t.show();
                Log.d(TAG, debugMessage);
                BusTimeListBusTime busTimeItem = (BusTimeListBusTime) mLv.getItemAtPosition(position);
                Alarms.setAlarm(getActivity(), busTimeItem.getBusTime());
                return true;
            }
        });

        mLv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String debugMessage = String.format("List clicked: position %d / ID %d", position, id);
                Toast t = Toast.makeText(getActivity(), debugMessage, Toast.LENGTH_SHORT);
                t.show();
                Log.d(TAG, debugMessage);
            }
        });

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
    public class BusApiTask extends AsyncTask<Integer, BusRouteData, BusRouteData> {
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
        protected BusRouteData doInBackground(Integer... params) {
            BusApi busApi = new BusApi(params[0]);
            return busApi.getResult();
        }

        @Override
        protected void onPostExecute(BusRouteData data) {
            busStationModels.addAll(data.stations);
            buses.addAll(data.buses);

            circularBusRouteMapView.setOnStationClickListener(new CircularBusRouteMapView.OnStationClickListener() {
                @Override
                public void onStationClick(int stationIndex) {
                    updateStation(stationIndex);
                    Log.w("onClick", "onClicked");
                }
            });
            circularBusRouteMapView.updateStations(busStationModels);

            mapView.setOnStationClickListener(new GoogleMapBusRouteMapView.OnStationClickListener() {
                @Override
                public void onStationClick(int stationIndex) {
                    updateStation(stationIndex);
                    Log.w("onClick", "onClicked");
                }
            });
            mapView.updateStations(busStationModels);

            updateStation(0);

            mBusTimerTask = new BusTimerTask();
            mTimer = new Timer();
            mTimer.schedule(mBusTimerTask, ( 1000 - Calendar.getInstance().get(Calendar.MILLISECOND)) % 1000, 1000);

            mBusApiTask = null;
        }

        /**
         * Task가 취소되었을 경우 호출됨
         */
        @Override
        protected void onCancelled() {
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

        final BusStationModel station = busStationModels.get(index);
        mLvAdapter.updateListFromStation(station);
        mLvAdapter.notifyDataSetChanged();

        mNameTv.setText(station.getFullName());

        if (station.getCoordinates() != null) {
            mStationMapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MapManager mm = new MapManager(station.getCoordinates());
                    mm.showMap(getActivity());

                }
            });
            mStationMapBtn.setVisibility(View.VISIBLE);
        } else {
            mStationMapBtn.setVisibility(View.GONE);
        }

        if (station.getImgResource() != -1) {
            mStationImgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageActivity.startIntent(getActivity(), station.getImgResource());
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
                show_colon = !show_colon;

                mLvAdapter.updateBusTimeListItems(Calendar.getInstance());

                handler.post(new Runnable() {
                                 public void run() {
                                     circularBusRouteMapView.updateBuses(buses);
                                     mapView.updateBuses(buses);

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
}

