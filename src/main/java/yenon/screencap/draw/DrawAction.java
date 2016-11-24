package yenon.screencap.draw;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


/**
 * Created by yenon on 11/19/16.
 */
public class DrawAction {
    public interface DrawRunnable{
        void draw(GraphicsContext canvas);
    }

    private DrawRunnable drawRunnable;

    public DrawAction(Color color,double radius,DrawRunnable drawRunnable){
        this.drawRunnable = drawRunnable;
        this.color=color;
        this.radius=radius;
    }

    private javafx.scene.paint.Color color;
    private double radius;

    public void draw(GraphicsContext canvas){
        canvas.setStroke(color);
        canvas.setFill(color);
        canvas.setLineWidth(radius);
        drawRunnable.draw(canvas);
    }
}
