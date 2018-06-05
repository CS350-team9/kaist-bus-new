package kr.ac.kaist.kyotong.ui;

import android.view.View;
import android.widget.TextView;

/**
 * 버스 시간표 목록에 텍스트 메시지를 표시하는 용도의 클래스
 */
public class BusTimeListText extends BusTimeListItem {
    private String text = null;

    public BusTimeListText(String text) {
        this.text = text;
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
}
