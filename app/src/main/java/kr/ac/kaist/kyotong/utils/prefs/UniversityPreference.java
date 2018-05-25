package kr.ac.kaist.kyotong.utils.prefs;

import android.content.Context;

/**
 * Created by joonyoung.yi on 16. 4. 28..
 */
public class UniversityPreference extends PreferenceBase {

    public static int getUniversity_id(Context context) {
        return PreferenceManager.get(context, KEY_UNIVERSITY_ID, -1);
    }

    public static void putUniversity_id(Context context, int university_id) {
        if (university_id > 0) {
            PreferenceManager.put(context, KEY_UNIVERSITY_ID, university_id);
        }
    }
}
