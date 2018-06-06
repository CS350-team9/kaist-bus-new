package kr.ac.kaist.kyotong.ui;

import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * 버스 시간표 목록에서, 서로 다른 날짜의 버스 사간을 구분하는 구분자를 나타내는 클래스
 */
public class BusTimeListDaySeparator extends BusTimeListItem {
    /** 이 구분자가 표시할 기준 날짜 */
    private Calendar date;
    private ArrayList<BusTimeListItem> relatedListItems;

    public BusTimeListDaySeparator(Calendar date) {
        this.date = (Calendar) date.clone();
        this.date.set(Calendar.HOUR_OF_DAY, 0);
        this.date.set(Calendar.MINUTE, 0);
        this.date.set(Calendar.SECOND, 0);
        this.date.set(Calendar.MILLISECOND, 0);
        relatedListItems = new ArrayList<>();
    }

    @Override
    public void updateListItemView(
            Calendar now,
            TextView headerTextView,
            View contentView,
            TextView timeTextView,
            TextView remainingTimeTextView
    ) {
        contentView.setVisibility(View.GONE);
        headerTextView.setVisibility(View.VISIBLE);
        headerTextView.setText(getHeaderStr(now));

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
    private String getHeaderStr(Calendar now) {
        now = (Calendar) now.clone();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        long dayOffset = (date.getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60 * 60 * 24);

        final String dayOffsetStr;
        if (dayOffset == -1)
            dayOffsetStr = "어제";
        else if (dayOffset == 0)
            dayOffsetStr = "오늘";
        else if (dayOffset == 1)
            dayOffsetStr = "내일";
        else if (dayOffset == 2)
            dayOffsetStr = "모레";
        else if (dayOffset < 0)
            dayOffsetStr = String.format("%d일 전", dayOffset);
        else
            dayOffsetStr = String.format("%d일 후", dayOffset);

        final int month = date.get(Calendar.MONTH) + 1;
        final int day = date.get(Calendar.DATE);
        final String dayOfWeekStr = date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        return String.format("%s(%d월 %d일 %s)", dayOffsetStr, month, day, dayOfWeekStr);
    }

    @Override
    public boolean hasExpired(Calendar now) {
        //참조하는 다른 목록 객체 중 만료된 것들을 제거한다
        for (int i = 0; i < relatedListItems.size(); ++i) {
            if (relatedListItems.get(i).hasExpired(now)) {
                //이미 만료된 객체는 더 이상 참조할 필요가 없다
                relatedListItems.remove(i);
                --i;
            }
        }

        // 구분자 자신의 날짜가 아직 만료되지 않았을 경우
        if (DateUtils.compareDays(date, now) >= 0)
            return false;

        return relatedListItems.size() == 0;
    }

    /**
     * 이 구분자가 관리하는 버스 시간표 목록 객체에 다른 BusTimeListItem을 추가한다.
     */
    public void addRelatedListItem(BusTimeListItem listItem) {
        relatedListItems.add(listItem);
    }

    /**
     * 이 구분자가 관리하는 다른 목록 객체가 없으면 true, 있으면 false를 돌려준다.
     *
     * @return 관리하는 목록 객체의 존재 여부
     */
    public boolean hasNoRelatedItem() {
        return relatedListItems.isEmpty();
    }

    /**
     * 이 구분자가 표시하는 날짜의 사본을 돌려준다. (시, 분, 초는 0이 아닐 수 있음)
     *
     * @return 날짜를 가리키는 객체
     */
    public Calendar getTime() {
        return (Calendar) date.clone();
    }
}
