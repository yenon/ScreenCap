package yenon.screencap.draw;

import javafx.scene.canvas.GraphicsContext;

import java.awt.*;

/**
 * Created by yenon on 11/19/16.
 */
public class DrawAction {
    public interface DrawRunnable{
        void draw(GraphicsContext canvas, Rectangle selection);
    }

    private DrawRunnable drawRunnable;

    public DrawAction(DrawRunnable drawRunnable){

        this.drawRunnable = drawRunnable;
    }

    public void draw(GraphicsContext canvas, Rectangle selection){
        drawRunnable.draw(canvas,selection);
    }
}
