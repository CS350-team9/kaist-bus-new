package kr.ac.kaist.kyotong.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


import java.util.ArrayList;

import kr.ac.kaist.kyotong.R;

public class ImageActivity extends ActivityBase {

    private static final String PARAM_IMG_RESOURCE = "param_img_resource";

    public static void startIntent(Activity activity, int img_resource) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra(PARAM_IMG_RESOURCE, img_resource);
        activity.startActivity(intent);
        //activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_activity);

        int img_resource = getIntent().getIntExtra(PARAM_IMG_RESOURCE, -1);

        if (img_resource != -1) {
            ImageView mIv = (ImageView) findViewById(R.id.iv);
            mIv.setImageResource(img_resource);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityRefresh(ArrayList<RefreshCode> refreshCodes) {

    }

}
