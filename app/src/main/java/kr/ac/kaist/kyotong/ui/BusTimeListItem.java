package kr.ac.kaist.kyotong.ui;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

/**
 * ListView로 버스 시간표를 표시할 때 리스트 항목을 나타내는 인터페이스.
 */
public abstract class BusTimeListItem {
    /**
     * 이 객체를 화면에 표시하는 View를 업데이트한다.
     *
     * @param now 현재 시각
     * @param headerTextView
     * @param contentView
     * @param timeTextView
     * @param remainingTimeTextView
     */
    public abstract void updateListItemView(
            Calendar now,
            TextView headerTextView,
            View contentView,
            TextView timeTextView,
            TextView remainingTimeTextView
    );

    /**
     * 이 리스트 항목이 만료되어 삭제해야 할 경우 true를, 아니라면 false를 반환한다.
     *
     * @param now 현재 시각
     * @return 만료 여부
     */
    public abstract boolean hasExpired(Calendar now);
}
