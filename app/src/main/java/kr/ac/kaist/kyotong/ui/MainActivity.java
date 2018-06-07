package kr.ac.kaist.kyotong.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.TabLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.ShuttleModel;
import kr.ac.kaist.kyotong.model.UniversityModel;
import kr.ac.kaist.kyotong.utils.EmailManager;
import kr.ac.kaist.kyotong.utils.MainActivityDialogManager;
import kr.ac.kaist.kyotong.utils.SmsManager;
import kr.ac.kaist.kyotong.utils.prefs.PopupPreference;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends ActivityBase {


    private MainActivityDialogManager mainActivityDialogManager;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static final String SAVED_STATE_ACTION_BAR_HIDDEN = "saved_state_action_bar_hidden";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     *
     */
    private enum ActionBarState {
        SHOWEN, HIDDEN, SHOWING, HIDING
    }

    private ActionBarState mActionBarState = ActionBarState.SHOWEN;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    /** 메인 화면의 상단 툴바에 대한 참조 (버스 시간표를 화면 전체로 확장할 때 숨기기 위한 용도) */
    private View mActionbarView;
    private TabLayout tabs;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        /**
         * If User open this app first, show dialog.
         */
//        if (PopupPreference.isFirstOpen(this)) {
//            showFirstOpenDialog();
//        }

        /**
         * Create the adapter that will return a fragment for each of the three
         * primary sections of the activity.
         * This line should be called before setContentView()
         */
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        /**
         *
         */
        mActionbarView = findViewById(R.id.actionbar_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        /**
         *
         */
        setSupportActionBar(mToolbar);
        restoreActionBar();

        update();



        mainActivityDialogManager = new MainActivityDialogManager(this,
                new MainActivityDialogManager.OnFinishListener() {
                    @Override
                    public void onFinish() {
                        //beforeOnDestroy();
                        finish();
                    }
                });

        /**
         *  * // Set up the ViewPager with the sections adapter.
         */
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setCurrentItem(0);
        mViewPager.setPageMargin(0);

        /**
         *
         */
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mSectionsPagerAdapter.shuttleModelArrayList.get(tab.getPosition()).panelExpand) {
                    hideActionBarAndTabs();
                } else {
                    showActionbar();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
        });

    }

    /**
     * 한 정거장의 버스 시간표를 자세히 보기 위해 화면 전체로 확장했을 때, 다른 모든 UI를 숨긴다.
     */
    public void hideActionBarAndTabs() {
        if (mActionBarState == ActionBarState.SHOWEN) {
            mActionBarState = ActionBarState.HIDING;
            final Animation mActionbarHideAnimation = AnimationUtils.loadAnimation(this, R.anim.base_actionbar_hide);
            final Animation mTabsHideAnimation      = AnimationUtils.loadAnimation(this, R.anim.base_tabs_hide);
            mActionbarView.startAnimation(mActionbarHideAnimation);
            tabs.startAnimation(mTabsHideAnimation);
            mActionBarState = ActionBarState.HIDDEN;
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        // Resume the AdView.
        // mAdView.resume();
    }

    public void onPause() {
        // Pause the AdView.
        //   mAdView.pause();

        super.onPause();
    }

    /**
     * 앱을 처음 실행했을 때 주의사항을 표시하는 용도로 보이나, 현재 안 쓰고 있음
     */
    private void showFirstOpenDialog() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.app_name)
                .content("본 어플리케이션의 버스 정보의 경우, 실시간 정보를 제공하지 못합니다. 제공된 시간표나 실측한 시간을 기준으로 현재 버스의 위치를 개략적으로 나타낼 뿐입니다. 따라서, 도로 상황이나 기타 여건에 따라 다소 오차가 발생할 수 있음을 인지하여 주시길 부탁드립니다.")
                .positiveText("예 알겠습니다")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PopupPreference.putFirstOpen(MainActivity.this);
                    }
                });
        builder.show();
    }

    /**
     * 한 정거장의 버스 시간표가 확장되었던 상태에서 원래 크기로 돌아갔을 때, 숨겼던 UI를 다시 보여준다.
     */
    public void showActionbar() {
        if (mActionBarState == ActionBarState.HIDDEN) {
            mActionBarState = ActionBarState.SHOWING;
            final Animation mActionbarShowAnimation = AnimationUtils.loadAnimation(this, R.anim.base_actionbar_show);
            final Animation mTabsShowAnimation      = AnimationUtils.loadAnimation(this, R.anim.base_tabs_show);
            mActionbarView.startAnimation(mActionbarShowAnimation);
            tabs.startAnimation(mTabsShowAnimation);
            mActionBarState = ActionBarState.SHOWEN;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_info) {
            showInfo();
        } else if (id == R.id.action_email) {
            EmailManager em = new EmailManager(MainActivity.this, "joonyoung@bablabs.com", "[셔틀즈] " + mTitle + "편에 할말 있다!", "어떤 의견이 있으신가요? 시원하게 말씀해보세요!");
            em.startIntent();
        } else if (id == R.id.action_sms) {
            SmsManager sm = new SmsManager(MainActivity.this, "01097650885");
            sm.showDialog();
        }
//        } else if (id == R.id.action_alarm){
//
//        }
//        } else if (id == R.id.action_change) {
//            ShuttleActivity.startActivity(this);
//        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 현재 활성화된 버스 노선도에 대한 설명을 팝업 창으로 보여준다.
     */
    private void showInfo() {
        ShuttleModel shuttleModel = mSectionsPagerAdapter.shuttleModelArrayList.get(mViewPager.getCurrentItem());

        if (shuttleModel != null && shuttleModel.title != -1) {

            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
            builder.title(shuttleModel.title)
                    .neutralText("닫기");

            if (shuttleModel.explain != -1) {
                builder.content(shuttleModel.explain);
            } else {
                builder.content("정보가 없습니다");
            }

            builder.show();
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public ArrayList<ShuttleModel> shuttleModelArrayList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            ShuttleModel shuttleModel = shuttleModelArrayList.get(position);

            return BusFragment.newInstance(position, shuttleModelArrayList.get(position).title);

        }

        @Override
        public int getCount() {
            return shuttleModelArrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();

            if (position < shuttleModelArrayList.size()) {
                return getString(shuttleModelArrayList.get(position).title).toUpperCase(l);
            }

            return null;
        }

        /**
         * @param object
         * @return
         */
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }

    /**
     * BusFragment에서 버스 시간표가 화면 전체로 확장되었다는 것을 알려줄 때 호출하는 메서드
     *
     * @param position 현재 활성화된 버스 노선 탭의 번호
     */
    public void notifyPanelExpand(int position) {
        mSectionsPagerAdapter.shuttleModelArrayList.get(position).panelExpand = true;
        hideActionBarAndTabs();
    }

    /**
     * BusFragment에서 버스 시간표가 원래 위치로 되돌아갔다는 것을 알려줄 때 호출하는 메서드
     *
     * @param position 현재 활성화된 버스 노선 탭의 번호
     */
    public void notifyPanelCollpased(int position) {
        mSectionsPagerAdapter.shuttleModelArrayList.get(position).panelExpand = false;
        showActionbar();
    }


    private void update() {

        /**
         *
         */
        UniversityModel universityModel = UniversityModel.newInstance(this, 1);
        mSectionsPagerAdapter.shuttleModelArrayList = universityModel.shuttleModels;
        mSectionsPagerAdapter.notifyDataSetChanged();

        /**
         *
         */
        mTitle = universityModel.name;

        if (mViewPager != null) {
            mViewPager.setCurrentItem(0);
        }

        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    /**
     * Override onBackPressed;
     */
    @Override
    public void onBackPressed() {
        mainActivityDialogManager.show();
    }

    @Override
    protected void onActivityRefresh(ArrayList<RefreshCode> refreshCodes) {

    }


}
