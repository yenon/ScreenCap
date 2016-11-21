package yenon.screencap.ui;

/**
 * Created by yenon on 11/20/16.
 */
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.*;

public final class Toast
{

    private Stage toastStage;
    private StackPane root;
    private long toastDelay;
    private Runnable onFinish;

    public Toast(String toastMsg, int toastDelay){
        this.toastDelay=toastDelay;
        toastStage=new Stage();
        toastStage.setResizable(false);
        toastStage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(toastMsg);
        text.setFont(Font.font("Verdana", 20));
        text.setFill(Color.LIGHTGRAY);
        text.setMouseTransparent(true);

        root = new StackPane(text);
        root.setStyle("-fx-background-color: #101010; -fx-background-radius: 10; -fx-padding: 10px;");
        root.setPickOnBounds(true);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        toastStage.setScene(scene);
    }

    public void show(){
        toastStage.show();
        Rectangle bounds = MouseInfo.getPointerInfo().getDevice().getDefaultConfiguration().getBounds();
        toastStage.setX(bounds.x+bounds.width-10-toastStage.getWidth());
        toastStage.setY(bounds.y+bounds.height-10-toastStage.getHeight());

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey1 = new KeyFrame(Duration.millis(500), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey1);
        fadeInTimeline.setOnFinished((ae) ->
                new Thread(() -> {
                    try {
                        Thread.sleep(toastDelay);
                    }catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Timeline fadeOutTimeline = new Timeline();
                    KeyFrame fadeOutKey1 = new KeyFrame(Duration.millis(2000), new KeyValue (toastStage.getScene().getRoot().opacityProperty(), 0));
                    fadeOutTimeline.getKeyFrames().add(fadeOutKey1);
                    fadeOutTimeline.setOnFinished((aeb) -> {
                        if(onFinish!=null){
                            try {
                                onFinish.run();
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        toastStage.close();
                    });
                    fadeOutTimeline.play();
                }).start());
        fadeInTimeline.play();
    }

    public void setOnClickListener(EventHandler<MouseEvent> eventEventHandler){
        root.setOnMouseClicked(eventEventHandler);
    }

    public void setOnFinish(Runnable onFinish){
        this.onFinish=onFinish;
    }

    public void close(){
        toastStage.close();
    }
}