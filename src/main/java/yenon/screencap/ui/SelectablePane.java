package yenon.screencap.ui;

import javafx.scene.layout.Pane;

/**
 * Created by yenon on 11/30/16.
 */
public abstract class SelectablePane extends Pane {
    public void setSelected(boolean value){
        if(value){
            onSelect();
        }else {
            onDeselect();
        }
    }

    public abstract void onSelect();
    public abstract void onDeselect();
}
