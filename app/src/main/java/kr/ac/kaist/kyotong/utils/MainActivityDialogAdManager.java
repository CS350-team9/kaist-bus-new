package kr.ac.kaist.kyotong.utils;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.ui.MainActivity;


/**
 * Created by joonyoung.yi on 16. 4. 6..
 */
public class MainActivityDialogAdManager {

    private AdMessageModel newAmmInstance(String message, Integer hour, int weight) {
        AdMessageModel adMessageModel = new AdMessageModel();
        adMessageModel.message = message;
        adMessageModel.hour = hour;
        adMessageModel.weight = weight;
        return adMessageModel;
    }

    private class AdMessageModel {
        String message = "";
        Integer hour = null;
        int weight = 0;
    }

    private int campus_id = -1;
    private MainActivity mainActivity;
    private View adContainer;

    private ImageView mNativeAdIv;
    private TextView mMessageView;

    private View mWhyView;


    public MainActivityDialogAdManager(final MainActivity mainActivity, View adContainer, int campus_id) {

        this.campus_id = campus_id;
        this.mainActivity = mainActivity;
        this.adContainer = adContainer;
        this.mMessageView = (TextView) adContainer.findViewById(R.id.message_view);
        this.mNativeAdIv = (ImageView) adContainer.findViewById(R.id.native_ad_iv);
        this.mWhyView = adContainer.findViewById(R.id.why_view);


    }

    public void onDestroy() {

    }

    public void show() {

    }


}
