package kr.ac.kaist.kyotong.adapters;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * 버스 시간표 목록에 텍스트 메시지를 표시하는 용도의 클래스
 */
public class BusTimeListText extends BusTimeListItem {
    private String text = null;
    private Calendar expirationDate = null;

    /**
     * 새로운 텍스트 항목을 만든다
     *
     * @param expirationDate 이 텍스트 목록이 만료되는 시각 (null일 경우 절대 만료하지 않음)
     * @param text 목록에 표시할 문자열
     */
    public BusTimeListText(Calendar expirationDate, String text) {
        this.text = text;
        if (expirationDate != null)
            this.expirationDate = (Calendar) expirationDate.clone();
    }

    @Override
    public void updateListItemView(
            Calendar now,
            TextView headerTextView,
            View contentView,
            TextView timeTextView,
            TextView remainingTimeTextView
    ) {
        headerTextView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
        timeTextView.setText(text);
        remainingTimeTextView.setVisibility(View.GONE);
    }

    @Override
    public boolean hasExpired(Calendar now) {
        if (expirationDate == null)
            return false;
        else
            return DateUtils.compareDays(expirationDate, now) < 0;
    }
}
