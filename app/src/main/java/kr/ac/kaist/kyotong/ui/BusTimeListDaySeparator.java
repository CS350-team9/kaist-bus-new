package kr.ac.kaist.kyotong.ui;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * 버스 시간표 목록에서, 서로 다른 날짜의 버스 사간을 구분하는 구분자를 나타내는 클래스
 */
public class BusTimeListDaySeparator extends BusTimeListItem {
    /** 이 구분자가 표시할 기준 날짜 */
    private Calendar date;

    public BusTimeListDaySeparator(Calendar date) {
        this.date = (Calendar) date.clone();
    }

    public void updateListItemView(
            TextView headerTextView,
            View contentView,
            TextView timeTextView,
            TextView remainingTimeTextView
    ) {
        contentView.setVisibility(View.GONE);
        headerTextView.setVisibility(View.VISIBLE);
        headerTextView.setText(getHeaderStr());

        int headerColor = 0xFF8A8A8A;
        if (DateUtils.isHoliday(date))
            headerColor = 0xFFF44336;
        headerTextView.setTextColor(headerColor);
    }

    /**
     * 구분자에 사용할 헤더 문자열을 생성한다.
     *
     * @return 헤더 문자열
     */
    private String getHeaderStr() {
        Calendar today = Calendar.getInstance();
        final int daysInToday = today.get(Calendar.YEAR) * 365 + today.get(Calendar.DAY_OF_YEAR);
        final int daysInCurrentDate = date.get(Calendar.YEAR) * 365 + date.get(Calendar.DAY_OF_YEAR);
        final int dayOffset = daysInCurrentDate - daysInToday;

        final int month = date.get(Calendar.MONTH) + 1;
        final int day = date.get(Calendar.DATE);
        final String dayOfWeekStr = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        if (dayOffset == 1)
            return String.format("내일(%d월 %d일 %s)", month, day, dayOfWeekStr);
        else if (dayOffset == 2)
            return String.format("모레(%d월 %d일 %s)", month, day, dayOfWeekStr);
        else
            return String.format("%d일 후(%d월 %d일 %s)", dayOffset, month, day, dayOfWeekStr);
    }
}
