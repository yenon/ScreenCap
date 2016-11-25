package yenon.screencap.draw;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import yenon.screencap.drag.DragHandler;
import yenon.screencap.drag.DragListener;
import yenon.screencap.tools.ToolView;
import yenon.screencap.ui.BackgroundImageView;
import yenon.screencap.ui.ColorPane;
import yenon.screencap.ui.SelectingTilePane;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by yenon on 11/19/16.
 */
public class DrawHandler {

    private enum DragPos{TOP,BOTTOM,LEFT,RIGHT}

    private ToolView currentTool,moveTool;

    private WritableImage screenshotImage;
    private Canvas fullCanvas,managedCanvas;
    private SelectingTilePane flowPaneTools;
    private ArrayList<DrawAction> drawActions = new ArrayList<>();
    private SimpleIntegerProperty drawActionDepth = new SimpleIntegerProperty(0);
    private ColorPane colorPane;

    private EnumSet<DragPos> getDragPos(double x,double y){
        //x=x-managedCanvas.getLayoutX();
        //y=y-managedCanvas.getLayoutY();
        EnumSet<DragPos> dragPos = EnumSet.noneOf(DragPos.class);

        int DRAG_MARGIN = 8;
        if(y>managedCanvas.getHeight()- DRAG_MARGIN) {
            dragPos.add(DragPos.BOTTOM);
        }
        if(y< DRAG_MARGIN){
            dragPos.add(DragPos.TOP);
        }
        if(x>managedCanvas.getWidth()- DRAG_MARGIN){
            dragPos.add(DragPos.RIGHT);
        }
        if(x< DRAG_MARGIN){
            dragPos.add(DragPos.LEFT);
        }
        return dragPos;
    }

    public DrawHandler(AnchorPane mainPane, Canvas fullCanvas, Canvas managedCanvas, WritableImage screenshotImage, SelectingTilePane flowPaneTools){
        colorPane = new ColorPane(mainPane);

        this.fullCanvas = fullCanvas;
        this.managedCanvas = managedCanvas;
        this.screenshotImage=screenshotImage;
        this.flowPaneTools = flowPaneTools;
        //region Background dragging
        DragHandler fullDragHandler = new DragHandler();
        fullDragHandler.attachTo(fullCanvas);
        fullDragHandler.addDragListener(new DragListener() {
            @Override
            public void onDragUpdate(MouseEvent start, MouseEvent current) {
                makeSelection(start, current);
                redrawCanvas();
            }

            @Override
            public void onDragFinished(MouseEvent start, MouseEvent end) {
                makeSelection(start, end);
                redrawCanvas();
            }
        });
        //endregion
        //region Foreground dragging
        DragHandler dragHandler = new DragHandler();
        dragHandler.attachTo(managedCanvas);
        dragHandler.addDragListener(new DragListener() {

            EnumSet<DragPos> dragPos = null;
            Point2D.Double lastPos;

            private void updateArea(double x,double y){
                if(dragPos.isEmpty()){
                    managedCanvas.setLayoutX(managedCanvas.getLayoutX()+x);
                    managedCanvas.setLayoutY(managedCanvas.getLayoutY()+y);
                }else {
                    if (dragPos.contains(DragPos.TOP)) {
                        managedCanvas.setLayoutY(managedCanvas.getLayoutY() + y);
                        managedCanvas.setHeight(managedCanvas.getHeight() - y);
                    }
                    if (dragPos.contains(DragPos.BOTTOM)) {
                        managedCanvas.setHeight(managedCanvas.getHeight() + y);
                    }
                    if (dragPos.contains(DragPos.LEFT)) {
                        managedCanvas.setLayoutX(managedCanvas.getLayoutX() + x);
                        managedCanvas.setWidth(managedCanvas.getWidth() - x);
                    }
                    if (dragPos.contains(DragPos.RIGHT)) {
                        managedCanvas.setWidth(managedCanvas.getWidth() + x);
                    }
                }
                if (managedCanvas.getLayoutX() < 0) {
                    managedCanvas.setLayoutX(0);
                }
                if (managedCanvas.getLayoutY() < 0) {
                    managedCanvas.setLayoutY(0);
                }
                if(managedCanvas.getWidth()>fullCanvas.getWidth()){
                    managedCanvas.setWidth(fullCanvas.getWidth());
                }
                if(managedCanvas.getHeight()>fullCanvas.getHeight()){
                    managedCanvas.setHeight(fullCanvas.getHeight());
                }
                if (managedCanvas.getLayoutX()+managedCanvas.getWidth()>fullCanvas.getWidth()){
                    managedCanvas.setLayoutX(fullCanvas.getWidth()-managedCanvas.getWidth());
                }
                if (managedCanvas.getLayoutY()+managedCanvas.getHeight()>fullCanvas.getHeight()){
                    managedCanvas.setLayoutY(fullCanvas.getHeight()-managedCanvas.getHeight());
                }

                redrawCanvas();
            }

            @Override
            public void onDragUpdate(MouseEvent start, MouseEvent current) {
                if(currentTool==moveTool) {
                    if (dragPos == null) {
                        dragPos = getDragPos(start.getSceneX() - managedCanvas.getLayoutX(), start.getSceneY() - managedCanvas.getLayoutY());
                        lastPos = new Point.Double(start.getSceneX(), start.getSceneY());
                    }
                    updateArea(current.getSceneX() - lastPos.getX(), current.getSceneY() - lastPos.getY());
                    lastPos.setLocation(current.getSceneX(), current.getSceneY());
                }else{
                    redrawCanvas();
                    currentTool.getDrawAction(start, current,colorPane.getColor(),colorPane.getRadius()).draw(managedCanvas.getGraphicsContext2D());
                }
            }

            @Override
            public void onDragFinished(MouseEvent start, MouseEvent end) {
                if(currentTool==moveTool) {
                    updateArea(end.getSceneX() - lastPos.getX(), end.getSceneY() - lastPos.getY());
                    dragPos = null;
                }else {
                    while (drawActions.size()>drawActionDepth.get()){
                        drawActions.remove(drawActions.size()-1);
                    }
                    DrawAction action = currentTool.getDrawAction(start, end,colorPane.getColor(),colorPane.getRadius());
                    drawActions.add(action);
                    drawActionDepth.set(drawActions.size());
                    redrawCanvas();
                }
            }
        });
        //endregion
        //region Color Picker
        flowPaneTools.addNode(colorPane);
        //endregion
        //region Move Tool
        moveTool = new ToolView("Move the selection","/images/move.png") {
            @Override
            public DrawAction getDrawAction(MouseEvent start, MouseEvent end,Color color,double radius) {
                return new DrawAction(color,radius,(canvas) -> {});
            }
        };
        moveTool.setPickOnBounds(true);
        flowPaneTools.addMonitoredNode(moveTool, () -> currentTool=moveTool);
        currentTool=moveTool;
        //endregion
        //region MouseAnimation
        managedCanvas.setOnMouseMoved(mouseEvent -> {
            if(currentTool!=moveTool) {
                managedCanvas.setCursor(Cursor.CROSSHAIR);
                return;
            }
            EnumSet<DragPos> dragPos = getDragPos(mouseEvent.getX(),mouseEvent.getY());
            if(dragPos.isEmpty()){
                managedCanvas.setCursor(Cursor.MOVE);
                return;
            }
            if(dragPos.contains(DragPos.TOP)){
                if(dragPos.contains(DragPos.LEFT)){
                    managedCanvas.setCursor(Cursor.NW_RESIZE);
                    return;
                }
                if(dragPos.contains(DragPos.RIGHT)){
                    managedCanvas.setCursor(Cursor.NE_RESIZE);
                    return;
                }
                managedCanvas.setCursor(Cursor.N_RESIZE);
                return;
            }
            if(dragPos.contains(DragPos.BOTTOM)){
                if(dragPos.contains(DragPos.LEFT)){
                    managedCanvas.setCursor(Cursor.SW_RESIZE);
                    return;
                }
                if(dragPos.contains(DragPos.RIGHT)){
                    managedCanvas.setCursor(Cursor.SE_RESIZE);
                    return;
                }
                managedCanvas.setCursor(Cursor.S_RESIZE);
                return;
            }
            if(dragPos.contains(DragPos.LEFT)){
                managedCanvas.setCursor(Cursor.W_RESIZE);
            }else {
                managedCanvas.setCursor(Cursor.E_RESIZE);
            }
        });
        //endregion
        //region Tool setup
        for(ToolView view: ToolView.ALL_VIEWS){
            flowPaneTools.addMonitoredNode(view, () -> {
                managedCanvas.setCursor(Cursor.DEFAULT);
                currentTool = view;
            });
        }

        if(flowPaneTools.getChildren().size()%2==1){
            flowPaneTools.addNode(new Pane());
        }
        //endregion
        //region Undo
        BackgroundImageView undoImageView = new BackgroundImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/undo.png")));
        undoImageView.disableProperty().setValue(true);
        undoImageView.setOnMouseClicked(event -> {
            drawActionDepth.set(drawActionDepth.getValue()-1);
            redrawCanvas();
        });
        drawActionDepth.addListener((observable, oldValue, newValue) -> undoImageView.setDisable(newValue.intValue() <= 0)
        );
        Tooltip.install(undoImageView,new Tooltip("Undo last change"));
        flowPaneTools.addNode(undoImageView);
        //endregion
        //region Redo
        BackgroundImageView redoImageView = new BackgroundImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/redo.png")));
        redoImageView.setOnMouseClicked(event -> {
            drawActionDepth.set(drawActionDepth.getValue()+1);
            redrawCanvas();
        });
        drawActionDepth.addListener((observable, oldValue, newValue) -> redoImageView.setDisable(newValue.intValue()>=drawActions.size()));
        Tooltip.install(redoImageView,new Tooltip("Redo last change"));
        flowPaneTools.addNode(redoImageView);
        drawActionDepth.set(0);
        //endregion
    }

    public Image getCurrentImage(){
        redrawCanvas();
        return managedCanvas.snapshot(null,null);
    }

    public void redrawCanvas(){
        if(screenshotImage==null){
            return;
        }
        if(managedCanvas.getLayoutX() + managedCanvas.getWidth() + 10 + flowPaneTools.getWidth() < fullCanvas.getWidth()){
            flowPaneTools.setLayoutX(managedCanvas.getLayoutX() + managedCanvas.getWidth() + 10);
        }else {
            flowPaneTools.setLayoutX(managedCanvas.getLayoutX() + managedCanvas.getWidth() - flowPaneTools.getWidth() - 10);
        }
        if(managedCanvas.getLayoutY() + flowPaneTools.getHeight() + 10 < fullCanvas.getHeight()){
            flowPaneTools.setLayoutY(managedCanvas.getLayoutY() + 10);
        }else {
            flowPaneTools.setLayoutY(managedCanvas.getLayoutY() + 10 - flowPaneTools.getHeight());
        }
        GraphicsContext context = managedCanvas.getGraphicsContext2D();
        context.drawImage(screenshotImage,
                managedCanvas.getLayoutX(),managedCanvas.getLayoutY(),
                managedCanvas.getWidth(),managedCanvas.getHeight(),
                0,0,managedCanvas.getWidth(),managedCanvas.getHeight());

        int i=0;
        while (i<drawActionDepth.get()&&i<drawActions.size()){
            drawActions.get(i).draw(context);
            i++;
        }
    }

    private void makeSelection(MouseEvent p1,MouseEvent p2){
        if(!flowPaneTools.isVisible()){
            flowPaneTools.setVisible(true);
        }
        if(p1.getX()<p2.getX()){
            managedCanvas.setLayoutX(p1.getX());
            managedCanvas.setWidth(p2.getX()-p1.getX()+1);
        }else {
            managedCanvas.setLayoutX(p2.getX());
            managedCanvas.setWidth(p1.getX()-p2.getX()+1);
        }
        if(p1.getY()<p2.getY()){
            managedCanvas.setLayoutY(p1.getY());
            managedCanvas.setHeight(p2.getY()-p1.getY()+1);
        }else {
            managedCanvas.setLayoutY(p2.getY());
            managedCanvas.setHeight(p1.getY()-p2.getY()+1);
        }
    }
}