package kr.ac.kaist.kyotong.utils;

import java.util.Calendar;

/**
 * Created by yearnning on 15. 1. 6..
 */
public class DateUtils {

    private static final String TAG = "DateUtils";

    public static boolean beforeFourAM() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour < 4;
    }

    public static boolean isHoliday(int date_offset) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, date_offset);

        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) % 7;

        if (day_of_week <= 1) {
            return true;
        }

        if ((month == 1 && day == 1) /* 신정 */
                || (month == 3 && day == 1) /* 3.1절 */
                || (month == 5 && day == 5) /* 어린이 날 */
                || (month == 6 && day == 6) /* 현충일 */
                || (month == 8 && day == 15) /* 광복절 */
                || (month == 10 && day == 3) /* 개천절 */
                || (month == 10 && day == 9) /* 한글날 */
                || (month == 12 && day == 25) /* 기독탄신일 */
                ) {
            return true;
        }

        if ((month == 2 && (day >= 18 && day <= 20)) /* 설날 */
                || (month == 5 && day == 25) /* 석가탄신일 */
                || (month == 9 && (day >= 26 && day <= 28)) /* 추석 */
                ) {
            return true;
        }

        return false;
    }

}
