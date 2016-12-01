package yenon.screencap.ui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;

/**
 * Created by yenon on 11/21/16.
 */
public class SelectingTilePane extends TilePane {

    private final SimpleIntegerProperty selectedNode = new SimpleIntegerProperty();

    public void addMonitoredNode(Node monitoredNode, Runnable onClick) {
        final int pos = super.getChildren().size();
        monitoredNode.setOnMouseClicked(mouseEvent -> {
            Node node = SelectingTilePane.super.getChildren().get(selectedNode.get());
            if(node instanceof SelectablePane){
                ((SelectablePane)node).setSelected(false);
            }
            selectedNode.setValue(pos);
            if(monitoredNode instanceof SelectablePane){
                ((SelectablePane)monitoredNode).setSelected(true);
            }
            onClick.run();
        });
        super.getChildren().add(monitoredNode);
    }

    public void setSelectedNode(int newSelectedNode) {
        Node node = SelectingTilePane.super.getChildren().get(selectedNode.get());
        if(node instanceof SelectablePane){
            ((SelectablePane)node).setSelected(false);
        }
        selectedNode.setValue(newSelectedNode);
        Node monitoredNode = super.getChildren().get(newSelectedNode);
        if(monitoredNode instanceof SelectablePane){
            ((SelectablePane)monitoredNode).setSelected(true);
        }
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
