package kr.ac.kaist.kyotong.ui;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import kr.ac.kaist.kyotong.utils.GoogleAnalyticsManager;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;


/**
 * Created by yearnning on 16. 1. 5..
 */
public abstract class ActivityBase extends AppCompatActivity {

    protected static final int REQUEST_CODE_DEFAULT = 1;
    //protected static final int REQUEST_CODE_FOOD_ACTIVITY = 2;
    //protected static final int REQUEST_CODE_HUNGRY_USER_ACTIVITY = 3;

    public static void logScreen(Application application, String screenName) {
        Tracker t = GoogleAnalyticsManager.getInstance(application).getTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.AppViewBuilder().build());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logScreen(getApplication(), getActivityName());
    }

    @Override
    public void onStart() {
        super.onStart();
        GoogleAnalytics.getInstance(getApplication()).reportActivityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        GoogleAnalytics.getInstance(getApplication()).reportActivityStop(this);
    }


    protected String getActivityName() {
        return this.getClass().getSimpleName();
    }

    /**
     *
     */
    protected interface OnActivityResultListener {
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    protected enum RefreshCode {
        STORE("store"), USER("user"), LOGIN("login"), COMMENT("comment"), RATING("rating"),
        PUSH("push");

        private String key;

        RefreshCode(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }

        public static RefreshCode getRefreshCodeByKey(String key) {
            for (RefreshCode refreshCode : values()) {
                if (refreshCode.getKey().equals(key)) {
                    return refreshCode;
                }
            }
            return null;
        }
    }

    private ArrayList<OnActivityResultListener> onActivityResultListenerArrayList;

    protected void addOnActivityResultListener(OnActivityResultListener onActivityResultListener) {
        if (onActivityResultListenerArrayList == null) {
            onActivityResultListenerArrayList = new ArrayList<>();
        }
        onActivityResultListenerArrayList.add(onActivityResultListener);
    }

    /**
     *
     */
    private static final int RESULT_CODE_REFRESH = 101;
    protected static final int RESULT_CODE_OK = 200;

    //
    protected abstract void onActivityRefresh(ArrayList<RefreshCode> refreshCodes);

    //
    private Intent mResultIntent = null;

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    final protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(getActivityName(), "resultCode -> " + resultCode);
        if (resultCode == RESULT_CODE_REFRESH) {

            if (data == null) {
                return;
            }

            Bundle bundle = data.getExtras();
            if (bundle == null) {
                return;
            }

            ArrayList<RefreshCode> refreshCodes = new ArrayList<>();
            for (String key : bundle.keySet()) {
                boolean refresh = bundle.getBoolean(key);
                if (refresh) {
                    RefreshCode refreshCode = RefreshCode.getRefreshCodeByKey(key);
                    Log.d(getActivityName(), "refresh -> " + refreshCode.getKey());
                    setActivityRefresh(refreshCode);
                    if (refreshCode != null)
                        refreshCodes.add(refreshCode);
                }
            }

            if (refreshCodes.size() > 0) {
                onActivityRefresh(refreshCodes);
            }

        } else {
            if (onActivityResultListenerArrayList != null)
                for (OnActivityResultListener onActivityResultListener : onActivityResultListenerArrayList) {
                    if (onActivityResultListener != null) {
                        onActivityResultListener.onActivityResult(requestCode, resultCode, data);
                    }
                }
        }
    }

    protected void setActivityRefresh(RefreshCode refreshCode) {

        if (mResultIntent == null) {
            mResultIntent = new Intent();
        }

        mResultIntent.putExtra(refreshCode.getKey(), true);
        setResult(RESULT_CODE_REFRESH, mResultIntent);
    }

}
