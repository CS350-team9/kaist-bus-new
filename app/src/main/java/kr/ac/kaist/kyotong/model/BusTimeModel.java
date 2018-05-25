package kr.ac.kaist.kyotong.model;

/**
 * Created by yearnning on 2014. 9. 12..
 */
public class BusTimeModel {

    private static final String TAG = "Ticket";

    /**
     *
     */
    public String left_time_str = null;
    public String time_str = null;
    public String header = null;
    public int header_textColor = 0xFF8A8A8A;
    public String indicator = null;

    /**
     *
     */
    private int hour;
    private int minute;

    /**
     *
     */
    public void makeTomorrowBusTime() {
        hour += 24;
    }

    public int getAbsoluteSecond() {
        return hour * 60 * 60 + minute * 60 ;
    }

    public void setTime(int hour, int minute) {

        this.hour = hour;
        this.minute = minute;

        if (hour >= 24) {
            hour -= 24;
        }

        time_str = String.format("%02d:%02d", hour, minute);
    }

    public void setTime(String time_str) {

        /**
         *
         */
        this.hour = Integer.parseInt(time_str.substring(0, 2));
        this.minute = Integer.parseInt(time_str.substring(3, 5));

        /**
         *
         */
        if (this.minute >= 60) {
            this.minute -= 60;
            this.hour++;
        }

        /**
         *
         */
        int hour = this.hour;
        if (hour >= 24) {
            hour -= 24;
        }
        this.time_str = String.format("%02d:%02d", hour, minute);
    }

    public void updateLeftTimeStr(int current_hour, int current_minute, int current_second) {

        int absoluteSecond_delta = getAbsoluteSecond() - (current_hour * 3600 + current_minute * 60 + current_second);

        if (absoluteSecond_delta < -60) {
            left_time_str = " - ";

        } else if (absoluteSecond_delta < 0) {
            left_time_str = "곧 도착하거나 지나갔습니다";

        } else if (absoluteSecond_delta < 30) {
            left_time_str = "잠시 후 도착합니다";

        } else {

            int date_delta = (absoluteSecond_delta / 86400);
            int hour_delta = (absoluteSecond_delta / 3600) % 24;
            int minute_delta = (absoluteSecond_delta / 60) % 60;
            int second_delta = (absoluteSecond_delta) % 60;

            if (date_delta > 0) {
                left_time_str = String.format("%d일 %02d시간 %02d분 전", date_delta, hour_delta, minute_delta);

            } else if (hour_delta > 0) {
                left_time_str = String.format("%d시간 %02d분 전", hour_delta, minute_delta);

            } else if (minute_delta > 0) {
                left_time_str = String.format("%d분 %02d초 전", minute_delta, second_delta);

            } else {
                left_time_str = String.format("%d초 전", second_delta);

            }
        }
    }
}
