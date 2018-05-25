package kr.ac.kaist.kyotong.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;

/**
 * Created by yearnning on 15. 1. 28..
 */
public class MapManager {

    private Location location = null;

    public MapManager(Location location) {
        this.location = location;
    }

    public void showMap(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0,0?q=" + location.getLatitude() + "," + location.getLongitude()+"?z=16");
        intent.setData(uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
