package kr.ac.kaist.kyotong.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * Created by yearnning on 2014. 9. 12..
 * <br>한 정거장에 대한 하나의 버스의 도착 시간을 나타내는 클래스.
 */
public class BusTimeModel {
    private static final String TAG = BusTimeModel.class.getName();

    private int dayOffset;
    private int hours;
    private int minutes;
    private int seconds;
    private String indicatorString = null;

    public BusTimeModel() {
        dayOffset = 0;
        hours = 0;
        minutes = 0;
        seconds = 0;
    }

    /**
     *
     */
    public void makeTomorrowBusTime() {
        hours += 24;
    }

    /**
     * 이 객체가 나타내는 시간을 초로 환산하여 돌려준다.
     *
     * @return 초로 환산한 시간
     */
    public int getAbsoluteSeconds() {
        return hours * 60 * 60 + minutes * 60 + seconds;
    }

    /**
     * 이 객체에 저장된 시간을 변경한다.
     *
     * @param hours 시
     * @param minutes 분
     * @param seconds 초
     */
    public void setTime(int hours, int minutes, int seconds) {
        int totalSeconds = hours * 3600 + minutes * 60 + seconds;
        this.hours = totalSeconds / 3600;
        this.minutes = (totalSeconds / 60) % 60;
        this.seconds = totalSeconds % 60;
    }

    /**
     * 이 객체에 저장된 시간을 변경한다. 초 단위는 0으로 설정한다.
     * @param hours 시
     * @param minutes 분
     */
    public void setTime(int hours, int minutes) {
        setTime(hours, minutes, 0);
    }

    /**
     * 이 객체를 시간표에서 내일/모레 항목을 나타내기 위한 구분자로 변환한다.
     *
     * @param dayOffset 내일은 1, 모레는 2
     */
    public void setDividerDayOffset(int dayOffset) {
        this.dayOffset = dayOffset;
    }

    /**
     * 이 객체가 구분자일 경우, 내일이면 1, 모레이면 2를 돌려준다. 구분자가 아닐 경우 0을 돌려준다.
     *
     * @return
     */
    public int getDividerDayOffset() { return dayOffset; }

    /**
     * 이 객체가 시간표에서 내일/모레 구분자를 나타내는 객체일 경우 true를,
     * 일반적인 시각을 나타내는 항목일 경우 false를 돌려준다.
     * @return
     */
    public boolean isDivider() {
        return dayOffset != 0;
    }

    /**
     * 이 객체를 indicator로 변환한다. Indicator는 시간표 목록에 간단한 문자열을 나타내는 항목이다.
     *
     * @param text 시간표에 표시할 문자열.
     */
    public void setIndicatorString(String text) { indicatorString = text; }

    /**
     * 이 객체가 indicator일 경우, 시간표 목록에서 표시할 문자열을 돌려준다.
     *
     * @return Indicator가 시간표 목록에 표시할 문자열
     */
    public String getIndicatorString() { return indicatorString; }

    /**
     * 이 객체가 indicator인지 확인한다.
     *
     * @return 이 객체가 indicator일 경우 true, 아니면 false
     */
    public boolean isIndicator() { return indicatorString != null; }

    /**
     * 시간을 HH:MM 형태로 나타낸 문자열을 리턴한다.
     *
     * @return 현재 시각을 나타낸 문자열
     */
    public String getTimeStr() {
        return String.format("%02d:%02d", hours, minutes);
    }

    /**
     * 시간이 내일/모레로 넘어갈 경우 이를 시간표에 표시할 때 사용할 헤더 문자열을 돌려준다.
     *
     * @return 헤더 문자열
     */
    public String getHeaderStr() {
        Calendar c = Calendar.getInstance();
        c.setLenient(true);
        c.set(Calendar.DATE, c.get(Calendar.DATE) + dayOffset);

        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        String dayOfWeekStr = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        if (dayOffset == 1)
            return String.format("내일(%d월 %d일 %s)", month, day, dayOfWeekStr);
        else if (dayOffset == 2)
            return String.format("모레(%d월 %d일 %s)", month, day, dayOfWeekStr);
        else
            return null;
    }

    /**
     * 날짜에 저장된 값이 공휴일/주말인지 확인한다.
     *
     * @return 공휴일 여부
     */
    public boolean isHoliday() {
        return DateUtils.isHoliday(dayOffset);
    }

    /**
     * 현재 시각에 주어진 시간(분 단위)를 더한다. (음수도 허용)
     *
     * @param minutes 더할 시간 (분 단위)
     * @return 현재 시각에 {@code minutes}를 더한 시각
     */
    public void addTime(int hours, int minutes, int seconds) {
        setTime(this.hours + hours, this.minutes + minutes, this.seconds + seconds);
    }

    /**
     * 이 객체가 가리키는 시각을 {@code currentTime}과 비교하여 버스의 상태를 문자열로 돌려준다.
     *
     * <p>이 객체의 시각이 {@code currentTime}보다 과거일 경우 이미 지나친 버스이다.<br>
     * 이 객체의 시각이 {@code currentTime}보다 미래일 경우 아직 도착하지 않은 버스이다.</p>
     *
     * @param currentTime 현재 시각
     * @return 버스의 도착 여부 또는 남은 시간을 나타낸 문자열
     */
    public String getLeftTimeString(BusTimeModel currentTime) {
        int deltaTotalSeconds = getAbsoluteSeconds() - currentTime.getAbsoluteSeconds();

        if (deltaTotalSeconds < -60)
            return " - ";
        else if (deltaTotalSeconds < 0)
            return "곧 도착하거나 지나갔습니다";
        else if (deltaTotalSeconds < 30)
            return "잠시 후 도착합니다";
        else {
            int deltaDays    = deltaTotalSeconds / 86400;
            int deltaHours   = (deltaTotalSeconds / 3600) % 24;
            int deltaMinutes = (deltaTotalSeconds / 60) % 60;
            int deltaSeconds = deltaTotalSeconds % 60;

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

    /**
     * 이 객체가 가리키는 시각이 현재 (시스템) 시각에 비해 이미 늦은 과거(60초 이상 지남)일 경우 true,
     * 아니면 false를 돌려준다.
     *
     * @return
     */
    public boolean isOverdue() {
        int deltaTotalSeconds = getAbsoluteSeconds() - getCurrentTime().getAbsoluteSeconds();
        return deltaTotalSeconds < -60;
    }

    /**
     * 이 객체가 가리키는 시각을 현재 (시스템) 시각과 비교하여 버스의 상태를 문자열로 돌려준다.
     *
     * <p>이 객체의 시각이 현재 시각보다 과거일 경우 이미 지나친 버스이다.<br>
     * 이 객체의 시각이 현재 시각보다 미래일 경우 아직 도착하지 않은 버스이다.</p>
     *
     * @return 버스의 도착 여부 또는 남은 시간을 나타낸 문자열
     */
    public String getLeftTimeString() {
        return getLeftTimeString(getCurrentTime());
    }

    /**
     * 현재 시스템 시간을 돌려준다.
     *
     * @return 현재 시스템 시간
     */
    public static BusTimeModel getCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();

        BusTimeModel busTime = new BusTimeModel();
        busTime.setTime(currentTime.getHours(), currentTime.getMinutes(), currentTime.getSeconds());
        return busTime;
    }
}
