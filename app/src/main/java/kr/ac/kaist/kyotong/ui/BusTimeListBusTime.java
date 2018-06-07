package kr.ac.kaist.kyotong.ui;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;

import kr.ac.kaist.kyotong.adapters.BusTimeListItem;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * 버스 시간표 목록에 표시된 버스 도착 시간 항목을 나타내는 클래스
 */
public class BusTimeListBusTime extends BusTimeListItem {
    private BusTimeModel busTime;

    public BusTimeListBusTime(BusTimeModel busTime) {
        this.busTime = busTime;
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
        String timeStr = String.format("%02d:%02d", busTime.getHours(), busTime.getMinutes());
        timeTextView.setText(timeStr);
        remainingTimeTextView.setVisibility(View.VISIBLE);
        remainingTimeTextView.setText(getLeftTimeString(now));
    }


    /**
     * 이 객체가 가리키는 버스 도착 시간을 돌려준다.
     *
     * @return 버스 시간 객체
     */
    public BusTimeModel getBusTime() {
        return busTime;
    }

    /**
     * 버스 도착 시간이 현재 시간과 비교하여 60초 이상 지났으면 버스 시간표에서 삭제한다.
     *
     * @return
     */
    @Override
    public boolean hasExpired(Calendar now) {
        long deltaTotalSeconds = busTime.getAbsoluteSeconds() - DateUtils.toAbsoluteSeconds(now);

        return deltaTotalSeconds < -60;
    }

    /**
     * 이 객체가 가리키는 버스 도착 시각을 현재(시스템) 시각과 비교하여 남은 시간을 문자열로 돌려준다.
     *
     * <p>버스 도착 시각이 {@code currentTime}보다 과거일 경우 이미 떠난 버스이다.<br>
     * 버스 도착 시각이 {@code currentTime}보다 미래일 경우 아직 도착하지 않은 버스이다.</p>
     *
     * @param now 현재 시각
     * @return 버스의 도착 여부 또는 남은 시간을 나타낸 문자열
     */
    private String getLeftTimeString(Calendar now) {
        long deltaTotalSeconds = busTime.getAbsoluteSeconds() - DateUtils.toAbsoluteSeconds(now);

        if (deltaTotalSeconds < -60)
            return " - ";
        else if (deltaTotalSeconds < 0)
            return "곧 도착하거나 지나갔습니다";
        else if (deltaTotalSeconds < 30)
            return "잠시 후 도착합니다";
        else {
            long deltaDays    = deltaTotalSeconds / 86400;
            long deltaHours   = (deltaTotalSeconds / 3600) % 24;
            long deltaMinutes = (deltaTotalSeconds / 60) % 60;
            long deltaSeconds = deltaTotalSeconds % 60;

            if (deltaDays > 0)
                return String.format("%d일 %02d시간 %02d분 전", deltaDays, deltaHours, deltaMinutes);
            else if (deltaHours > 0)
                return String.format("%d시간 %02d분 전", deltaHours, deltaMinutes);
            else if (deltaMinutes > 0)
                return String.format("%d분 %02d초 전", deltaMinutes, deltaSeconds);
            else
                return String.format("%d초 전", deltaSeconds);
        }
    }
}
