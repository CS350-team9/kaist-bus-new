package kr.ac.kaist.kyotong.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import java.util.ArrayList;

import kr.ac.kaist.kyotong.R;

public class SplashActivity extends ActivityBase {


    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    /*

     */
    int screen_width = -1;
    int screen_height = -1;

    /**
     *
     */
    LoginApiTask mLoginApiTask = null;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        /*
        final LinearLayout mWholeLayout = (LinearLayout) findViewById(R.id.whole_layout);
        ViewTreeObserver vto = mWholeLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                //remove listener to ensure only one call is made.
                mWholeLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                int h = mWholeLayout.getHeight();
                int w = mWholeLayout.getWidth();

                //make use of height and width
                Log.d(TAG, "h -> " + h);
                Log.d(TAG, "w -> " + w);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLoginApiTask = new LoginApiTask();
        mLoginApiTask.execute();
    }

    /**
     *
     */
    public class LoginApiTask extends AsyncTask<Void, Void, Void> {

        /**
         * @param params
         * @return
         */
        @Override
        protected Void doInBackground(Void... params) {


            try {
                Thread.sleep(500);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void param) {
            MainActivity.startActivity(SplashActivity.this);
            finish();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mLoginApiTask = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mLoginApiTask != null) {
            mLoginApiTask.cancel(true);
        }

        super.onDestroy();
    }


    @Override
    protected void onActivityRefresh(ArrayList<RefreshCode> refreshCodes) {

    }


}
