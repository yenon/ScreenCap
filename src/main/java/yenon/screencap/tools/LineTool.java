package yenon.screencap.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import yenon.screencap.draw.DrawAction;

/**
 * Created by yenon on 11/19/16.
 */
public class LineTool extends ToolView {
    public LineTool() {
        super("Draw a line", "/images/line.png");
    }

    @Override
    public DrawAction getDrawAction(MouseEvent start, MouseEvent end, Color color, double size) {
        if (!start.isPrimaryButtonDown() || start.isShiftDown()) {
            if (Math.abs(end.getX() - start.getX()) > Math.abs(end.getY() - start.getY())) {
                return new DrawAction(color, size, canvas -> canvas.strokeLine(start.getX(), start.getY(), end.getX(), start.getY()));
            } else {
                return new DrawAction(color, size, canvas -> canvas.strokeLine(start.getX(), start.getY(), start.getX(), end.getY()));
            }
        } else {
            return new DrawAction(color, size, canvas -> canvas.strokeLine(start.getX(), start.getY(), end.getX(), end.getY()));
        }
    }
}
