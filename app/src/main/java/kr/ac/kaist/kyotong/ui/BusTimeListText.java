package kr.ac.kaist.kyotong.ui;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * 버스 시간표 목록에 텍스트 메시지를 표시하는 용도의 클래스
 */
public class BusTimeListText extends BusTimeListItem {
    private String text = null;
    private long expirationAbsoluteDays;

    /**
     * 새로운 텍스트 항목을 만든다
     *
     * @param expirationDate 이 텍스트 목록이 만료되는 시각 (null일 경우 절대 만료하지 않음)
     * @param text 목록에 표시할 문자열
     */
    public BusTimeListText(Calendar expirationDate, String text) {
        this.text = text;
        if (expirationDate == null)
            expirationAbsoluteDays = -1;
        else
            expirationAbsoluteDays = DateUtils.toAbsoluteDays(expirationDate);
    }

    public void updateListItemView(
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
    public boolean hasExpired() {
        if (expirationAbsoluteDays == -1)
            return false;
        else
            return expirationAbsoluteDays < DateUtils.toAbsoluteDays(Calendar.getInstance());
    }
}
