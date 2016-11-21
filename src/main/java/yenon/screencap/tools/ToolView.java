package yenon.screencap.tools;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import yenon.screencap.draw.DrawAction;

import java.awt.*;

/**
 * Created by yenon on 11/19/16.
 */
public abstract class ToolView extends ImageView{

    public ToolView(String tooltip,String icon){
        this.setImage(new Image(ToolView.class.getResourceAsStream(icon)));
        Tooltip.install(this,new Tooltip(tooltip));
    }

    public abstract DrawAction getDrawAction(MouseEvent start,MouseEvent end);
}