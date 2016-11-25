package yenon.screencap.tools;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import yenon.screencap.draw.DrawAction;
import yenon.screencap.ui.BackgroundImageView;

/**
 * Created by yenon on 11/19/16.
 */
public abstract class ToolView extends BackgroundImageView {

    @SuppressWarnings("StaticInitializerReferencesSubClass")
    public static final ToolView[] ALL_VIEWS = new ToolView[]{new LineTool(), new RectangleTool(), new EllipseTool()};

    public ToolView(String tooltip, String icon) {
        this.setImage(new Image(ToolView.class.getResourceAsStream(icon)));
        Tooltip.install(this, new Tooltip(tooltip));
    }

    public abstract DrawAction getDrawAction(MouseEvent start, MouseEvent end, Color color, double radius);
}