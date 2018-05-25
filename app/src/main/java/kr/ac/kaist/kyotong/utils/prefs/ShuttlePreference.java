package kr.ac.kaist.kyotong.utils.prefs;

import android.content.Context;

/**
 * Created by joonyoung.yi on 16. 4. 28..
 */
public class ShuttlePreference extends PreferenceBase {

    public static int getWeight(Context context, int id) {
        return PreferenceManager.get(context, KEY_SHUTTLE_WEIGHT_BASE + id, 0);
    }

    public static void putWeight(Context context, int shuttle_id, int weight) {
        if (weight > 0) {
            PreferenceManager.put(context, KEY_SHUTTLE_WEIGHT_BASE + shuttle_id, weight);
        }
    }
}
