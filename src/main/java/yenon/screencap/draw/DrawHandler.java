package yenon.screencap.draw;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import yenon.screencap.drag.DragHandler;
import yenon.screencap.drag.DragListener;
import yenon.screencap.tools.PaintTool;
import yenon.screencap.tools.ToolView;
import yenon.screencap.ui.SelectingTilePane;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * Created by yenon on 11/19/16.
 */
public class DrawHandler implements DragListener {
    private final ToolView[] tools = new ToolView[]{new PaintTool()};
    private ToolView currentTool,moveTool;

    private WritableImage screenshotImage;
    private Canvas fullCanvas,managedCanvas;
    private SelectingTilePane flowPaneTools;
    private ArrayList<DrawAction> drawActions = new ArrayList<>();
    private SimpleIntegerProperty drawActionDepth = new SimpleIntegerProperty();

    public DrawHandler(Canvas fullCanvas,Canvas managedCanvas,WritableImage screenshotImage, SelectingTilePane flowPaneTools){
        this.fullCanvas = fullCanvas;
        this.managedCanvas = managedCanvas;
        this.screenshotImage=screenshotImage;
        this.flowPaneTools = flowPaneTools;

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

        managedCanvas.setCursor(Cursor.MOVE);
        //DragHandler dragHandler = new DragHandler();
        //dragHandler.attachTo(managedCanvas);
        //dragHandler.addDragListener(this);
        managedCanvas.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                managedCanvas.setCursor(Cursor.CROSSHAIR);
            }
        });


        moveTool = new ToolView("Move the selection","/images/move.png") {
            @Override
            public DrawAction getDrawAction(MouseEvent start, MouseEvent end) {
                return new DrawAction(new DrawAction.DrawRunnable() {
                    @Override
                    public void draw(GraphicsContext canvas, Rectangle selection) {

                    }
                });
            }
        };
        flowPaneTools.addMonitoredNode(moveTool, new Runnable() {
            @Override
            public void run() {
                currentTool=moveTool;
            }
        });
        currentTool=moveTool;
        for(ToolView view:tools){
            flowPaneTools.addMonitoredNode(view, new Runnable() {
                @Override
                public void run() {
                    managedCanvas.setCursor(Cursor.DEFAULT);
                    currentTool = view;
                }
            });
        }

        if(flowPaneTools.getChildren().size()%2==1){
            flowPaneTools.addNode(new Pane());
        }

        //region Undo
        ImageView undoImageView = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/undo.png")));
        undoImageView.setPickOnBounds(true);
        undoImageView.setFitWidth(16);
        undoImageView.setFitHeight(16);
        undoImageView.setOnMouseClicked(event -> {
            if(drawActionDepth.get()!=-1){
                drawActionDepth.subtract(1);
                redrawCanvas();
            }
        });
        drawActionDepth.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                undoImageView.disableProperty().setValue(newValue.intValue()==0);
            }
        });
        Tooltip.install(undoImageView,new Tooltip("Undo last change"));
        flowPaneTools.addNode(undoImageView);
        //endregion
        //region Redo
        ImageView redoImageView = new ImageView(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/redo.png")));
        redoImageView.setPickOnBounds(true);
        redoImageView.setFitWidth(16);
        redoImageView.setFitHeight(16);
        redoImageView.setOnMouseClicked(event -> {
            if(drawActionDepth.get()<drawActions.size()){
                drawActionDepth.add(1);
                redrawCanvas();
            }
        });
        drawActionDepth.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                redoImageView.disableProperty().setValue(newValue.intValue()<drawActions.size());
            }
        });
        Tooltip.install(redoImageView,new Tooltip("Redo last change"));
        flowPaneTools.addNode(redoImageView);
        drawActionDepth.setValue(0);
        //endregion
    }

    public void setScreenshotImage(WritableImage screenshotImage){
        this.screenshotImage=screenshotImage;
    }

    public WritableImage getScreenshotImage() {
        return screenshotImage;
    }

    public Image getCurrentImage(){
        redrawCanvas();
        return managedCanvas.snapshot(null,null);
    }

    public void redrawCanvas(){
        if(screenshotImage==null){
            return;
        }
        if(managedCanvas.getLayoutX() + managedCanvas.getWidth() + 2 + flowPaneTools.getWidth() < fullCanvas.getWidth()){
            flowPaneTools.setLayoutX(managedCanvas.getLayoutX() + managedCanvas.getWidth() + 2);
        }else {
            flowPaneTools.setLayoutX(managedCanvas.getLayoutX() + managedCanvas.getWidth() - flowPaneTools.getWidth() - 2);
        }
        if(managedCanvas.getLayoutY() + flowPaneTools.getHeight() < fullCanvas.getHeight()){
            flowPaneTools.setLayoutY(managedCanvas.getLayoutY());
        }else {
            flowPaneTools.setLayoutY(managedCanvas.getLayoutY() - flowPaneTools.getHeight());
        }
        GraphicsContext context = managedCanvas.getGraphicsContext2D();
        context.drawImage(screenshotImage,
                managedCanvas.getLayoutX(),managedCanvas.getLayoutY(),managedCanvas.getWidth(),managedCanvas.getHeight(),
                0,0,managedCanvas.getWidth(),managedCanvas.getHeight());


        for (DrawAction action:drawActions){
            //action.draw(context,selection);
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

    @Override
    public void onDragUpdate(MouseEvent start, MouseEvent current) {
        if(currentTool==moveTool) {
            makeSelection(start, current);
        }else {
            DrawAction action = currentTool.getDrawAction(start, current);
            //action.draw(managedCanvas.getGraphicsContext2D(), selection);
        }

        redrawCanvas();
    }

    @Override
    public void onDragFinished(MouseEvent start, MouseEvent end) {
        if(currentTool==moveTool){
            makeSelection(start, end);
        }else {
            DrawAction action = currentTool.getDrawAction(start, end);
            int i = drawActions.size() - 1;
            while (i > drawActionDepth.get()) {
                drawActions.remove(i);
            }
            drawActions.add(action);
            drawActionDepth.set(drawActions.size() - 1);
        }
        redrawCanvas();
    }
}