package yenon.screencap.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;

/**
 * Created by yenon on 11/21/16.
 */
public class SelectingTilePane extends TilePane {

    SimpleIntegerProperty selectedNode = new SimpleIntegerProperty();

    public void addMonitoredNode(Node monitoredNode, Runnable onClick) {
        final int pos = super.getChildren().size();
        monitoredNode.setOnMouseClicked(mouseEvent -> {
            Node node = SelectingTilePane.super.getChildren().get(selectedNode.get());
            node.setStyle("");
            selectedNode.setValue(pos);
            monitoredNode.setStyle("-fx-background-color: #0000FF77; -fx-background-radius: 4;");
            onClick.run();
        });
        super.getChildren().add(monitoredNode);
    }

    public void addNode(Node node) {
        super.getChildren().add(node);
    }

    public void removeNode(Node remove) {
        super.getChildren().remove(remove);
    }

    @Override
    public ObservableList<Node> getChildren() {
        return super.getChildrenUnmodifiable();
    }
}
