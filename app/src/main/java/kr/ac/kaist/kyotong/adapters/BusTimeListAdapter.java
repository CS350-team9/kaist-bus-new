package kr.ac.kaist.kyotong.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.BusStationModel;
import kr.ac.kaist.kyotong.model.BusTimeModel;
import kr.ac.kaist.kyotong.ui.BusTimeListBusTime;
import kr.ac.kaist.kyotong.utils.DateUtils;

/**
 * 버스 시간표를 표시하기 위한 adapter 클래스
 */
public class BusTimeListAdapter extends ArrayAdapter<BusTimeListItem> {
    private static final String TAG = BusTimeListAdapter.class.getName();

    private ArrayList<BusTimeListItem> listItems;
    private int resourceId;

    /**
     * @param context 컨텍스트 (Activity)
     * @param resource XML 리소스 ID
     */
    public BusTimeListAdapter(Activity context, int resource) {
        super(context, resource);
        resourceId = resource;
        listItems = new ArrayList<>();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public BusTimeListItem getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView headerTextView, timeTextView, remainingTimeTextView;
        View contentView;

        //목록을 처음 생성할 때
        if (convertView == null) {
            final LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (layoutInflater == null)
                throw new AssertionError("Cannot retrieve LayoutInflater");
            convertView = layoutInflater.inflate(resourceId, null);

            headerTextView = convertView.findViewById(R.id.header_tv);
            contentView = convertView.findViewById(R.id.content_view);
            timeTextView = convertView.findViewById(R.id.time_tv);
            remainingTimeTextView = convertView.findViewById(R.id.left_tv);

            //findViewById()는 속도가 느리므로 나중에 View를 빠르게 불러올 수 있게 저장한다
            convertView.setTag(R.id.header_tv, headerTextView);
            convertView.setTag(R.id.content_view, contentView);
            convertView.setTag(R.id.time_tv, timeTextView);
            convertView.setTag(R.id.left_tv, remainingTimeTextView);
        } else {
            //위에서 setTag()으로 저장한 View를 불러온다
            headerTextView = (TextView) convertView.getTag(R.id.header_tv);
            contentView = (View) convertView.getTag(R.id.content_view);
            timeTextView = (TextView) convertView.getTag(R.id.time_tv);
            remainingTimeTextView = (TextView) convertView.getTag(R.id.left_tv);
        }

        this.getItem(position).updateListItemView(
                Calendar.getInstance(),
                headerTextView,
                contentView,
                timeTextView,
                remainingTimeTextView
        );

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        return listItems.get(position) instanceof BusTimeListBusTime;
    }

    /**
     * 현재 표시된 버스 시간표 목록을 주어진 정거장의 시간표로 교체한다.
     *
     * <br>실제로 UI를 업데이트하려면 notifyDataSetChanged()를 호출해야 한다.
     *
     * @param station 정거장 객체
     */
    public void updateListFromStation(BusStationModel station) {
        Log.d(TAG, String.format("시간표를 표시할 정거장 : %s", station.getFullName()));

        //기존의 버스 시간표를 지우고 새로운 시간표를 생성한다.
        listItems.clear();

        ArrayList<BusTimeModel> busTimes = station.getVisitTimes();

        //구분자를 생성하기 위한 날짜
        Calendar beginDate = Calendar.getInstance();

        //구분자의 날짜가 버스 시간보다 더 미래일 경우 구분자의 날짜를 버스 시간과 같게 함
        if (busTimes.size() > 0) {
            Calendar firstBusTimeValue = busTimes.get(0).getTime();
            if (DateUtils.compareDays(beginDate, firstBusTimeValue) > 0)
                beginDate = firstBusTimeValue;
        }

        //버스 도착 시간을 날짜별로 묶는다
        TreeMap<Calendar, ArrayList<BusTimeModel>> busTimesByDay = groupBusTimes(busTimes, beginDate);

        //첫 구분자 추가
        for (TreeMap.Entry<Calendar, ArrayList<BusTimeModel>> entry : busTimesByDay.entrySet()) {
            Calendar date = entry.getKey();
            BusTimeListDaySeparator header = new BusTimeListDaySeparator(date);
            listItems.add(header);

            ArrayList<BusTimeModel> busTimesInDay = entry.getValue();
            if (busTimesInDay.isEmpty()) {
                BusTimeListText textItem = new BusTimeListText(date, "주말 및 공휴일은 운행하지 않습니다");
                listItems.add(textItem);

                header.addRelatedListItem(textItem);
            }
            else {
                for (BusTimeModel busTime : busTimesInDay) {
                    BusTimeListBusTime busTimeListItem = new BusTimeListBusTime(busTime);
                    listItems.add(busTimeListItem);

                    //구분자 객체가 버스 시각 목록 객체를 참조함으로서 자신이 언제 삭제되어야 할지 확인할 수 있게 한다
                    header.addRelatedListItem(busTimeListItem);
                }
            }
        }
    }

    /**
     * 버스 시간표 목록에서 만료된 항목을 제거한다.
     *
     * <br>실제로 UI를 업데이트하려면 notifyDataSetChanged()를 호출해야 한다.
     *
     * @param now 각 항목의 만료 여부를 확인할 때 사용할 기준 시각
     */
    public void updateBusTimeListItems(Calendar now) {
        for (int i = 0; i < listItems.size(); ++i) {
            if (listItems.get(i).hasExpired(now)) {
                listItems.remove(i);
                --i;
            }
        }

        for (int i = 0; i < listItems.size(); ++i) {
            BusTimeListItem listItem = listItems.get(i);
            if (listItem instanceof BusTimeListDaySeparator) {
                BusTimeListDaySeparator separator = (BusTimeListDaySeparator) listItem;
                if (separator.hasNoRelatedItem()) {
                    BusTimeListText noMoreBusText = new BusTimeListText(separator.getTime(), "당일 버스 운행은 종료되었습니다");
                    listItems.add(i++, noMoreBusText);
                    separator.addRelatedListItem(noMoreBusText);
                }
            }
        }
    }

    /**
     * 버스 도착 시간 목록을 날짜별로 묶어서 돌려준다.
     * @param busTimes 버스 도착 시간 목록
     * @return (날짜) => (날짜에 해당하는 버스 시간 목록)
     */
    private static TreeMap<Calendar, ArrayList<BusTimeModel>> groupBusTimes(ArrayList<BusTimeModel> busTimes, Calendar beginDate) {
        Calendar date = (Calendar) beginDate.clone();
        TreeMap<Calendar, ArrayList<BusTimeModel>> busTimesByDay = new TreeMap<>();
        ArrayList<BusTimeModel> currentList = new ArrayList<>();
        busTimesByDay.put(date, currentList);

        for (BusTimeModel busTime : busTimes) {
            //새로운 날짜를 시작함
            while (DateUtils.compareDays(date, busTime.getTime()) < 0) {
                date = (Calendar) date.clone();
                date.add(Calendar.DATE, 1);
                currentList = new ArrayList<>();
                busTimesByDay.put(date, currentList);
            }

            currentList.add(busTime);
        }

        return busTimesByDay;
    }
}