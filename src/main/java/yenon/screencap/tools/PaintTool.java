package yenon.screencap.tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import yenon.screencap.draw.DrawAction;

import java.awt.*;

/**
 * Created by yenon on 11/19/16.
 */
public class PaintTool extends ToolView {
    public PaintTool() {
        super("Paint ", "/images/paint.png");
    }

    @Override
    public DrawAction getDrawAction(MouseEvent start, MouseEvent end) {
        return new DrawAction(new DrawAction.DrawRunnable() {
            @Override
            public void draw(GraphicsContext canvas, Rectangle selection) {

            }
        });
    }
}
