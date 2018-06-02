package kr.ac.kaist.kyotong.model;

import android.content.Context;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.utils.prefs.ShuttlePreference;

/**
 * Created by yearnning on 15. 1. 26..
 * 버스 노선도 탭을 나타내는 클래스 (실제 노선 정보가 아님)
 */
public class ShuttleModel {

    public static ShuttleModel newInstance(Context context, int id) {

        /**
         *  KAIST
         */
        if (id == 1) {
            return newInstance(context,
                    1,
                    ShuttleModel.Type.BUS,
                    R.string.tab_kaist_olev,
                    R.string.tab_kaist_olev_explain);
        } else if (id == 2) {
            return ShuttleModel.newInstance(context,
                    2,
                    ShuttleModel.Type.BUS,
                    R.string.tab_kaist_sunhwan,
                    R.string.tab_kaist_sunhwan_explain);
        } else if (id == 3) {
            return ShuttleModel.newInstance(context,
                    3,
                    ShuttleModel.Type.BUS,
                    R.string.tab_kaist_wolpyeong,
                    R.string.tab_kaist_wolpyeong_explain);
        }
        return null;
    }

    private static ShuttleModel newInstance(Context context,
                                            int id,
                                            Type type,
                                            int title,
                                            int explain) {
        ShuttleModel shuttleModel = new ShuttleModel();
        shuttleModel.id = id;
        shuttleModel.type = type;
        shuttleModel.title = title;
        shuttleModel.explain = explain;
        shuttleModel.weight = ShuttlePreference.getWeight(context, id);
        return shuttleModel;
    }


    public enum Type {
        BUS
    }

    public int id = -1;
    public Type type = null;
    /** 버스 노선도의 이름을 나타낸 문자열 리소스 */
    public int title = -1;
    /** 버스 노선도에 대한 설명을 나타낸 문자열 리소스 */
    public int explain = -1;
    public int weight = 0;
    /** 버스 노선도 Fragment의 버스 시간표가 현재 확장되어 있는지를 나타내는 값 */
    public boolean panelExpand = false;
}
