package kr.ac.kaist.kyotong.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;

import java.util.Calendar;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.ui.MainActivity;

/**
 * 알람 생성, 관리 등 전반적인 기능을 맡은 클래스
 */
public class Alarms {
    private static String TAG = Alarms.class.getName();

    /**
     * 주어진 버스 시간에 대한 알람을 생성한다.
     *
     * @param busTime
     */
    public static void setAlarm(Context context, BusTimeModel busTime) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                R.id.REQUEST_FIRE_ALARM,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        int wakeUpBeforeTime = 10 * 60 * 1000; //TODO 테스트용으로 10분 전에 발동하게 설정했음

        //minSdkVersion < 19이므로 AlarmManager.setExact()를 쓰기 곤란하다.
        //따라서 Android Support Library에서 제공하는 AlarmManagerCompat를 사용한다.
        AlarmManagerCompat.setExactAndAllowWhileIdle(
                getAlarmManager(context),
                AlarmManager.RTC_WAKEUP,
                busTime.getTime().getTimeInMillis() - wakeUpBeforeTime,
                pendingIntent
        );

        Calendar time = busTime.getTime();
        time.add(Calendar.MILLISECOND, -wakeUpBeforeTime);
        Log.d(TAG, String.format("Created alarm for %02d:%02d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)));
    }

    /**
     * AlarmManager에 대한 핸들을 돌려준다. 실패시 NullPointerException를 발생시킨다.
     *
     * @return AlarmManager
     */
    private static AlarmManager getAlarmManager(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null)
            throw new NullPointerException("Cannot retrieve AlarmManager");
        return alarmManager;
    }
}
