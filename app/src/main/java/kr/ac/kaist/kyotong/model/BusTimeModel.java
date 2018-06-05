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

    private int hours;
    private int minutes;
    private int seconds;

    public BusTimeModel() {
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
     * 시간을 HH:MM 형태로 나타낸 문자열을 리턴한다.
     *
     * @return 현재 시각을 나타낸 문자열
     */
    public String getTimeStr() {
        return String.format("%02d:%02d", hours, minutes);
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
