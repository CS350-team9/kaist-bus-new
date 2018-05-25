package kr.ac.kaist.kyotong.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import kr.ac.kaist.kyotong.R;


/**
 * Created by yearnning on 15. 1. 1..
 */
public class SizeUtils {

    public static int getPanelHeight(Context context) {
        return windowHeight(context)
                - getMainContentHeight(context)
                - getStatusBarHeight(context)
                - getActionBarHeight(context)
                - getAdContainerHeight(context)
                - getTabsHeight(context);
        //+ getNavigationBarHeight(context)
    }

    public static int getMainContentHeight(Context context) {
        return windowWidth(context) * 68 / 100;
    }

    public static int getAdContainerHeight(Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.ad_container_height);
    }

    public static int getTabsHeight(Context context) {
        return context.getResources().getDimensionPixelOffset(R.dimen.tabs_height);
    }

    public static int windowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        return width;
    }


    private static int windowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.y;

        return width;
    }

    private static boolean hasNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        return (!hasMenuKey && !hasBackKey);
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int getNavigationBarHeight(Context context) {

        if (!hasNavigationBar(context)) {
            return 0;
        }

        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private static int getActionBarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize});
        int mActionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return mActionBarSize;
    }

}
