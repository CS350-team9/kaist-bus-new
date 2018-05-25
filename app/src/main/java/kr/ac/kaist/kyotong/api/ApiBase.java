package kr.ac.kaist.kyotong.api;

import android.app.Application;
import android.widget.Toast;


public class ApiBase {
    private final static String TAG = "Api Base";

    /**
     *
     */
    public static final int REQUEST_CODE_SUCCESS = 0;
    public static final int REQUEST_CODE_FAIL = 1;
    public static final int REQUEST_CODE_NOT_PARSED = 2;
    public static final int REQUEST_CODE_NOT_CONNECTED = 3;
    public static final int REQUEST_CODE_UNEXPECTED = 4;
    public static final int REQUEST_CODE_CAN_NOT_FIND_RESULT_OBJECT = 5;

    /**
     *
     */
    public Application application = null;
    public String response = null;

    /**
     *//*
    public static void setString2Prefs(Application application, String key, String value) {

        SharedPreferences prefs = application.getSharedPreferences(Argument.PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefs_editor;

        prefs_editor = prefs.edit();
        prefs_editor.putString(key, value);
        prefs_editor.commit();
    }

    /**
     * @param application
     * @param key
     * @param value_init
     * @return
     *//*
    public static String getStringInPrefs(Application application, String key, String value_init) {
        SharedPreferences prefs = application.getSharedPreferences(Argument.PREFS, Context.MODE_PRIVATE);
        return prefs.getString(key, value_init);
    }

    /**
     * @param application
     * @return
     *//*
    public static String getSessionKeyInPrefs(Application application) {
        SharedPreferences prefs = application.getSharedPreferences(Argument.PREFS, Context.MODE_PRIVATE);

        return prefs.getString(Argument.PREFS_SESSION_KEY, null);
    }

    /**
     * @return
     */
    public int getRequestCode() {



        return ApiBase.REQUEST_CODE_SUCCESS;
    }

    /**
     * @param request_code
     */
    public static void showToastMsg(Application application, int request_code) {

        if (application.getApplicationContext() == null)
            return;

        if (request_code == ApiBase.REQUEST_CODE_NOT_PARSED) {
            Toast.makeText(application.getApplicationContext(), "인터넷 연결이 불안정합니다. 잠시 후, 다시 시도해 주세요.", Toast.LENGTH_LONG).show();

        } else if (request_code == ApiBase.REQUEST_CODE_NOT_CONNECTED) {
            Toast.makeText(application.getApplicationContext(), "인터넷 연결이 불안정합니다. 잠시 후, 다시 시도해 주세요.", Toast.LENGTH_LONG).show();

        } else if (request_code == ApiBase.REQUEST_CODE_CAN_NOT_FIND_RESULT_OBJECT) {
            Toast.makeText(application.getBaseContext(), "서버와의 연결이 불안정합니다. 잠시 후, 다시 시도해 주세요.", Toast.LENGTH_LONG).show();

        } else if (request_code == ApiBase.REQUEST_CODE_UNEXPECTED) {
            Toast.makeText(application.getBaseContext(), "알 수 없는 오류가 발생했습니다. 잠시 후, 다시 시도해 주세요.", Toast.LENGTH_LONG).show();

        }


    }

}
