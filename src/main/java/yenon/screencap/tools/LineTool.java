package yenon.screencap.tools;

import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import yenon.screencap.draw.DrawAction;

/**
 * Created by yenon on 11/19/16.
 */
public class LineTool extends ToolView {
    public LineTool() {
        super("Paint ", "/images/line.png");
    }

    @Override
    public DrawAction getDrawAction(MouseEvent start, MouseEvent end, Color color, double radius) {
        return new DrawAction(color,radius, canvas -> canvas.strokeLine(start.getX(),start.getY(),end.getX(),end.getY()));
    }
}
