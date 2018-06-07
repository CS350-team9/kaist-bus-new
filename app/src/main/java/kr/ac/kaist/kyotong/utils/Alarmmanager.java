package kr.ac.kaist.kyotong.utils;

/**
 * Created by jaylee on 2018. 6. 6..
 */

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;



public class Alarmmanager {

    private Context mContext;
    private Intent intent;
    private final String packageName = "com.phillipsu.clock2";

    public Alarmmanager(Context context) {
        mContext = context;
    }

    public void startIntent() {

        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage("com.philliphsu.clock2.debug");
        mContext.startActivity(intent);
    }
}

