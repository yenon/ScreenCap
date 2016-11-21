package yenon.screencap.drag;

import javafx.scene.input.MouseEvent;

/**
 * Created by yenon on 11/19/16.
 */
public interface DragListener {
    void onDragUpdate(MouseEvent start,MouseEvent current);
    void onDragFinished(MouseEvent start,MouseEvent end);
}
