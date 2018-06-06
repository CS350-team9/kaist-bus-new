package kr.ac.kaist.kyotong.utils;

import java.util.Calendar;

/**
 * 날짜 및 시간 관련 유틸리티 클래스
 */
public class DateUtils {
    private static final String TAG = DateUtils.class.getName();

    /**
     * 현재 (시스템) 시각을 초 단위로 환산하여 돌려준다.
     *
     * @return 유닉스 시각 (초 단위)
     */
    public static long getCurrentAbsoluteSeconds() {
        return toAbsoluteSeconds(Calendar.getInstance());
    }

    /**
     * 주어진 객체의 시각을 유닉스 시각 (Unix time) 기준 초 단위로 환산하여 돌려준다.
     *
     * @param time 시각
     * @return 초 단위의 유닉스 시각
     */
    public static long toAbsoluteSeconds(Calendar time) {
        return time.getTimeInMillis() / 1000;
    }

    /**
     * 두 값의 날짜만을 비교한다. 시간은 비교하지 않는다.
     *
     * <br>left < right이면 음수를 돌려준다.
     * <br>left > right이면 양수를 돌려준다.
     * <br>두 값의 날짜가 같으면 0을 돌려준다.
     *
     * @param left 비교할 시각
     * @param right 비교할 시각
     * @return 비교 결과
     */
    public static int compareDays(Calendar left, Calendar right) {
        if (left.get(Calendar.YEAR) == right.get(Calendar.YEAR)
                && left.get(Calendar.MONTH) == right.get(Calendar.MONTH)
                && left.get(Calendar.DATE) == right.get(Calendar.DATE))
            return 0;
        else
            return left.compareTo(right);
    }

    /**
     * 현재 (시스템) 시각이 오전 4시 이전인지 확인한다.
     *
     * @return
     */
    public static boolean beforeFourAM() {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        return hour < 4;
    }

    /**
     * 주어진 날짜가 공휴일인지 확인한다. (하드코딩되어 있음)
     *
     * @param date
     * @return 공휴일일 경우 true, 평일이면 false
     */
    public static boolean isHoliday(Calendar date) {
        final int year = date.get(Calendar.YEAR);
        final int month = date.get(Calendar.MONTH) + 1;
        final int day = date.get(Calendar.DATE);
        final int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);

        //TODO 외부에서 공휴일 정보를 불러오는 방법을 알아보기

        //토요일, 일요일
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
            return true;

        //법정 공휴일 중 날짜가 고정된 경우
        if ((month == 1 && day == 1)            //신정
                || (month == 3 && day == 1)     //3.1절
                || (month == 5 && day == 5)     //어린이날
                || (month == 6 && day == 6)     //현충일
                || (month == 8 && day == 15)    //광복절
                || (month == 10 && day == 3)    //개천절
                || (month == 10 && day == 9)    //한글날
                || (month == 12 && day == 25)   //기독탄신일(성탄절)
                ) {
            return true;
        }

        //법정공휴일 중 날짜가 바뀌는 경우
        int[][] nonFixedHolidays = {
                //설
                {2018, 2, 15}, {2018, 2, 16}, {2018, 2, 17},
                {2019, 2,  4}, {2019, 2,  5}, {2019, 2,  6},
                {2020, 1, 24}, {2020, 1, 25}, {2020, 1, 26},

                //부처님오신날(석가탄신일)
                {2018, 5, 22},
                {2019, 5, 12},
                {2019, 4, 30},

                //추석
                {2018, 9, 23}, {2018, 9, 24}, {2018, 9, 25},
                {2019, 9, 12}, {2019, 9, 13}, {2019, 9, 14},
                {2019, 9, 30}, {2019, 10, 1}, {2019, 10, 2},
        };

        for (int[] holiday : nonFixedHolidays) {
            if (year == holiday[0] && month == holiday[1] && day == holiday[2])
                return true;
        }

        return false;
    }
}

