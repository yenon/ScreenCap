package yenon.screencap.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import yenon.screencap.draw.DrawAction;
import yenon.screencap.utils.UI;

import java.awt.*;

/**
 * Created by yenon on 11/24/16.
 */
public class EllipseTool extends ToolView {
    public EllipseTool() {
        super("Draw ellipses", "/images/ellipse.png");
    }

    @Override
    public DrawAction getDrawAction(MouseEvent start, MouseEvent end, Color color, double size) {
        return new DrawAction(color, size, canvas -> {
            canvas.setStroke(color);
            Rectangle selection = UI.getSelectedRectangle(start.getX(), start.getY(), end.getX(), end.getY());
            canvas.strokeOval(selection.x, selection.y, selection.width, selection.height);
        });
    }
}
