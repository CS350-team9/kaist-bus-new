package kr.ac.kaist.kyotong.utils;

/**
 * Created by yearnning on 15. 1. 2..
 */
public class ColorUtils {

    private static final int header_r = 0x8B;
    private static final int header_g = 0xC3;
    private static final int header_b = 0x4A;
    private static final int header_default = 0xFF;

    public static int getHeaderColor(float alpha) {
        return getColor(alpha, header_r, header_g, header_b, header_default);
    }

    public static int getHeaderTextColor(float alpha) {
        return getColor(alpha, 0xff, 0xff, 0xff, 0x00);
    }

    private static int getColor(float alpha, float r, float g, float b, float d) {

        return getIntFromColor((int) ((r) * (1f - alpha) + d * (alpha)),
                (int) ((g) * (1f - alpha) + d * (alpha)),
                (int) ((b) * (1f - alpha) + d * (alpha)));
    }

    private static int getIntFromColor(int Red, int Green, int Blue) {
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xFF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }

}
