package yenon.screencap.utils;

import java.awt.Rectangle;

/**
 * Created by yenon on 11/24/16.
 */
public class UI {

    public static Rectangle getSelectedRectangle(double x1, double y1, double x2, double y2) {
        return getSelectedRectangle((int) x1, (int) y1, (int) x2, (int) y2);
    }

    private static Rectangle getSelectedRectangle(int x1, int y1, int x2, int y2) {
        Rectangle selection = new Rectangle();
        if (x1 < x2) {
            selection.x = x1;
            selection.width = x2 - x1 + 1;
        } else {
            selection.x = x2;
            selection.width = x1 - x2 + 1;
        }
        if (y1 < y2) {
            selection.y = y1;
            selection.height = y2 - y1 + 1;
        } else {
            selection.y = y2;
            selection.height = y1 - y2 + 1;
        }
        return selection;
    }
}
