package kr.ac.kaist.kyotong.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 * Created by yearnning on 15. 1. 22..
 */
public class EmailManager {

    private Context mContext;
    private String mEmail;
    private String mTitle;
    private String mContent;

    public EmailManager(Context context, String email, String title, String content) {
        mContext = context;
        mEmail = email;
        mTitle = title;
        mContent = content;
    }

    public void startIntent() {

        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + mEmail));
        intent.putExtra(Intent.EXTRA_SUBJECT, mTitle);
        intent.putExtra(Intent.EXTRA_TEXT, mContent);
        mContext.startActivity(Intent.createChooser(intent, "메일 보내기"));
    }

    private boolean installed(String uri) {
        PackageManager pm = mContext.getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
