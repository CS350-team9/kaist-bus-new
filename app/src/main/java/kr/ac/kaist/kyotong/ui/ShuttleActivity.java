package kr.ac.kaist.kyotong.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

import java.util.ArrayList;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.ShuttleModel;
import kr.ac.kaist.kyotong.model.UniversityModel;
import kr.ac.kaist.kyotong.utils.list.ShuttleListViewManager;
import kr.ac.kaist.kyotong.utils.prefs.ShuttlePreference;

public class ShuttleActivity extends ActivityBase {

    private DragSortListView mLv;
    private ShuttleListViewManager shuttleListViewManager;

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, ShuttleActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shuttle_activity);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP
                    | ActionBar.DISPLAY_SHOW_HOME
                    | ActionBar.DISPLAY_SHOW_TITLE);
        }

        mLv = (DragSortListView) findViewById(R.id.lv);

        shuttleListViewManager = new ShuttleListViewManager(this,
                R.layout.shuttle_activity_lv,
                new ArrayList<ShuttleModel>());
        mLv.setAdapter(shuttleListViewManager);
        mLv.setDropListener(new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
                if (from != to) {
                    ShuttleModel item = shuttleListViewManager.getItem(from);
                    shuttleListViewManager.remove(item);
                    shuttleListViewManager.insert(item, to);
                }
            }
        });

        requestRefresh();

        DragSortController controller = new DragSortController(mLv);
        controller.setDragHandleId(R.id.imageView1);
        //controller.setClickRemoveId(R.id.);
        controller.setRemoveEnabled(false);
        controller.setSortEnabled(true);
        controller.setDragInitMode(1);
        //controller.setRemoveMode(removeMode);

        mLv.setFloatViewManager(controller);
        mLv.setOnTouchListener(controller);
        mLv.setDragEnabled(true);
    }

    private void requestRefresh() {

        UniversityModel universityModel = UniversityModel.newInstance(this, 1);
        shuttleListViewManager.addAll(universityModel.shuttleModels);
        shuttleListViewManager.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shuttle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        } else if (id == R.id.action_confirm) {
            confirm();
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirm() {
        for (int position = 0; position < shuttleListViewManager.getCount(); position++) {
            ShuttleModel shuttleModel = shuttleListViewManager.getItem(position);
            ShuttlePreference.putWeight(this, shuttleModel.id, shuttleListViewManager.getCount() - position);
        }
        Toast.makeText(this, "앱을 재시작합니다", Toast.LENGTH_LONG).show();
        SplashActivity.startActivity(this);
    }

    @Override
    protected void onActivityRefresh(ArrayList<RefreshCode> refreshCodes) {

    }


}
