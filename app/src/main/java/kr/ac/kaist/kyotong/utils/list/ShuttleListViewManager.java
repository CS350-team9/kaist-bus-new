package kr.ac.kaist.kyotong.utils.list;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import kr.ac.kaist.kyotong.R;
import kr.ac.kaist.kyotong.model.ShuttleModel;

/**
 * Created by joonyoung.yi on 16. 4. 28..
 */
public class ShuttleListViewManager extends ArrayAdapter<ShuttleModel> {
    private static final String TAG = "ShuttleListViewManager";

    /**
     *
     */
    private ViewHolder viewHolder = null;
    private Activity mActivity;
    public ArrayList<ShuttleModel> shuttleModels;
    private int textViewResourceId;

    /**
     * @param activity
     * @param textViewResourceId
     * @param shuttleModels
     */
    public ShuttleListViewManager(Activity activity,
                                  int textViewResourceId,
                                  ArrayList<ShuttleModel> shuttleModels) {
        super(activity, textViewResourceId, shuttleModels);

        this.textViewResourceId = textViewResourceId;
        this.shuttleModels = shuttleModels;
        this.mActivity = activity;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public int getCount() {
        return shuttleModels.size();
    }

    @Override
    public ShuttleModel getItem(int position) {
        return shuttleModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //UI Initiailizing : View Holder
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater()
                    .inflate(textViewResourceId, null);

            viewHolder = new ViewHolder();

            //Find View By ID
            viewHolder.mNameTv = (TextView) convertView.findViewById(R.id.name_tv);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Data Import and export
        final ShuttleModel shuttleModel = this.getItem(position);
        viewHolder.mNameTv.setText(shuttleModel.title);

        return convertView;
    }

    private class ViewHolder {
        TextView mNameTv;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
