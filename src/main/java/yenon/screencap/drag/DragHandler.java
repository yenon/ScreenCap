package yenon.screencap.drag;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

/**
 * Created by yenon on 11/19/16.
 */
public class DragHandler {
    private final ArrayList<DragListener> dragListeners = new ArrayList<>();
    private final ArrayList<ClickListener> clickListeners = new ArrayList<>();

    private boolean mouseMoved = false;
    private MouseEvent startEvent;

    public void attachTo(Node node) {
        node.setOnMouseDragged(dragHandler);
        node.setOnMousePressed(pressHandler);
        node.setOnMouseReleased(releaseHandler);
    }

    private final EventHandler<MouseEvent> dragHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            mouseMoved = true;
            for (DragListener listener : dragListeners) {
                listener.onDragUpdate(startEvent, event);
            }
        }
    };
    private final EventHandler<MouseEvent> pressHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            startEvent = event;
            mouseMoved = false;
        }
    };
    private final EventHandler<MouseEvent> releaseHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (!mouseMoved) {
                for (ClickListener listener : clickListeners) {
                    listener.onClick(event);
                }
            } else {
                for (DragListener listener : dragListeners) {
                    listener.onDragFinished(startEvent, event);
                }
            }
        }
    };

    public void addDragListener(DragListener listener) {
        dragListeners.add(listener);
    }

    public void addClickListener(ClickListener listener) {
        clickListeners.add(listener);
    }

    public void removeDragListener(DragListener listener) {
        dragListeners.remove(listener);
    }

    public void removeClickListener(ClickListener listener) {
        clickListeners.remove(listener);
    }

    public void clearDragListeners() {
        dragListeners.clear();
    }

    public void clearClickListeners() {
        clickListeners.clear();
    }
}
