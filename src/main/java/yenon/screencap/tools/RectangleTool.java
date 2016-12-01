package yenon.screencap.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import yenon.screencap.draw.DrawAction;
import yenon.screencap.utils.UI;

import java.awt.*;

/**
 * Created by yenon on 11/25/16.
 */
public class RectangleTool extends ToolView {
    public RectangleTool() {
        super("Draw a (round) rectangle\nPress Shift to round", "/images/rectangle.png");
    }

    @Override
    public DrawAction getDrawAction(MouseEvent start, MouseEvent end, Color color, double size) {
        return new DrawAction(color, size, canvas -> {
            Rectangle selection = UI.getSelectedRectangle(start.getX(), start.getY(), end.getX(), end.getY());
            if (start.isShiftDown() || !start.isPrimaryButtonDown()) {
                canvas.strokeRoundRect(selection.x, selection.y, selection.width, selection.height, size * 5, size * 5);
            } else {
                canvas.strokeRoundRect(selection.x, selection.y, selection.width, selection.height, size, size);
            }
        });
    }
}
