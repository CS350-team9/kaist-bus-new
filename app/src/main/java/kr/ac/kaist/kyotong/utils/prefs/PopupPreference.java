package kr.ac.kaist.kyotong.utils.prefs;

import android.content.Context;

/**
 * Created by joonyoung.yi on 16. 4. 28..
 */
public class PopupPreference extends PreferenceBase {

    public static boolean isFirstOpen(Context context) {
        String first_open = PreferenceManager.get(context, PreferenceBase.KEY_FIRST_OPEN, "true");
        return first_open.equals("true");
    }

    public static void putFirstOpen(Context context) {
        PreferenceManager.put(context, PreferenceBase.KEY_FIRST_OPEN, "false");
    }

}
