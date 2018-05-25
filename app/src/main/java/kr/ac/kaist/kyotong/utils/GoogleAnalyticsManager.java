package kr.ac.kaist.kyotong.utils;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by yearnning on 15. 10. 21..
 */
public class GoogleAnalyticsManager {

    private static final String TAG = "GoogleAnalyticsManager";

    /**
     *
     */
    private android.app.Application mApplication = null;
    private static final String PROPERTY_ID = "UA-58645401-7";
    private Tracker mTracker = null;

    /**
     *
     */
    private static final Object lock = new Object();
    private static GoogleAnalyticsManager instance;

    public synchronized Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApplication);
            mTracker = analytics.newTracker(PROPERTY_ID);
            mTracker.enableAdvertisingIdCollection(true);
            return mTracker;
        }
        return mTracker;
    }

    /**
     * @return
     */
    public static GoogleAnalyticsManager getInstance(android.app.Application application) {
        synchronized (lock) {
            if (instance == null) {
                instance = new GoogleAnalyticsManager(application);
            }
            return instance;
        }
    }

    /**
     *
     */
    private GoogleAnalyticsManager(android.app.Application application) {
        this.mApplication = application;
    }
}
