package kr.ac.kaist.kyotong.model;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Date;

/**
 * 한 정거장에 대한 하나의 버스의 도착 시간을 나타내는 클래스. 주의: Immutable한 클래스이다
 */
public class BusTimeModel implements Cloneable, Comparable<BusTimeModel> {
    private static final String TAG = BusTimeModel.class.getName();

    private Calendar time;

    /**
     * 주어진 시각을 바탕으로 버스 출발/도착 시각을 생성한다.
     *
     * @param date 날짜 참고용 (시, 분, 초는 사용하지 않음). BusTimeModel은 이 객체를 clone()해서 사용한다.
     * @param hours 시
     * @param minutes 분
     * @param seconds 초
     */
    public BusTimeModel(Calendar date, int hours, int minutes, int seconds) {
        time = (Calendar) date.clone();
        time.setLenient(true);
        time.set(Calendar.HOUR_OF_DAY, hours);
        time.set(Calendar.MINUTE, minutes);
        time.set(Calendar.SECOND, seconds);
        time.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 이 객체가 나타내는 유닉스 시각을 초 단위로 환산하여 돌려준다.
     *
     * @return 초로 환산한 시각
     */
    public long getAbsoluteSeconds() {
        return time.getTimeInMillis() / 1000;
    }

    public int getHours() {
        return time.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinutes() {
        return time.get(Calendar.MINUTE);
    }

    public int getSeconds() {
        return time.get(Calendar.SECOND);
    }

    public int getDayOfYear() {
        return time.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 이 객체가 가리키는 시각을 돌려준다.
     *
     * @return 시각 (사본이므로 값을 변경해도 원래 시각에는 영향 없음)
     */
    public Calendar getTime() {
        return (Calendar) time.clone();
    }

    /**
     * 현재 시각에 주어진 시간을 더한 시각을 나타낸 객체를 생성하여 돌려준다. 음수나 범위를 초과한 값도 허용된다.
     *
     * @param hours 시
     * @param minutes 분
     * @param seconds 초
     * @return 주어진 시간 값을 더한 사본
     */
    public BusTimeModel addTime(int hours, int minutes, int seconds) {
        BusTimeModel newTime = clone();
        newTime.time.add(Calendar.HOUR_OF_DAY, hours);
        newTime.time.add(Calendar.MINUTE, minutes);
        newTime.time.add(Calendar.SECOND, seconds);
        return newTime;
    }

    @Override
    public BusTimeModel clone() {
        try {
            return (BusTimeModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int compareTo(BusTimeModel otherBusTime) {
        return time.compareTo(otherBusTime.time);
    }
}
