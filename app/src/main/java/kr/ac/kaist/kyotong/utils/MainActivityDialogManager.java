package kr.ac.kaist.kyotong.utils;

import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.ui.ActivityBase;
import kr.ac.kaist.kyotong.ui.MainActivity;


/**
 * Created by joonyoung.yi on 16. 4. 6..
 */
public class MainActivityDialogManager {

    private static final String TAG = "MainActivityDialogManager";

    private MainActivity mainActivity;

    private MainActivityDialogAdManager mainActivityDialogAdManager;

    private View mCustomView = null;
    private MaterialDialog dialog = null;

    public interface OnFinishListener {
        void onFinish();
    }

    private OnFinishListener onFinishListener = null;

    public MainActivityDialogManager(MainActivity mainActivity,
                                     OnFinishListener onFinishListener) {
        this.mainActivity = mainActivity;
        this.onFinishListener = onFinishListener;

        this.mCustomView = createCustomView(mainActivity);
        this.mainActivityDialogAdManager = new MainActivityDialogAdManager(mainActivity,
                mCustomView.findViewById(R.id.ad_view),
                1);
    }

    private View createCustomView(MainActivity mainActivity) {

        View mCustomView = mainActivity.getLayoutInflater().inflate(R.layout.main_activity_dialog, null);

        mCustomView.findViewById(R.id.yes_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (MainActivityDialogManager.this.onFinishListener != null) {
                    MainActivityDialogManager.this.onFinishListener.onFinish();
                }
            }
        });

        mCustomView.findViewById(R.id.no_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return mCustomView;
    }

    public void show() {

        MaterialDialog.Builder builder = new MaterialDialog.Builder(mainActivity);
        builder.customView(mCustomView, false);
        dialog = builder.show();

        ActivityBase.logScreen(mainActivity.getApplication(), TAG);

        mainActivityDialogAdManager.show();
    }
}
