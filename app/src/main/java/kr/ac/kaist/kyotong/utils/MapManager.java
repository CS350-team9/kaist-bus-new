package kr.ac.kaist.kyotong.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by yearnning on 15. 1. 28..
 */
public class MapManager {

    private LatLng location = null;

    public MapManager(LatLng location) {
        this.location = location;
    }

    public void showMap(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("geo:0,0?q=" + location.latitude + "," + location.longitude+"?z=16");
        intent.setData(uri);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
