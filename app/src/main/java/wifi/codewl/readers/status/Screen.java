package wifi.codewl.readers.status;

import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Screen {
    public static DisplayMetrics dm = new DisplayMetrics();

    public static final float display1 = 0.908f;
    public static final float display2 = 0.974f;


    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


}
