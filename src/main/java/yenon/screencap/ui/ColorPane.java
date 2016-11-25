package yenon.screencap.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.io.IOException;

/**
 * Created by yenon on 11/24/16.
 */
public class ColorPane extends Pane {

    private ColorPickerController colorPickerController;

    public ColorPane(AnchorPane mainPane){
        setHeight(16);
        setWidth(16);
        setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        colorPickerController = new ColorPickerController();
        GridPane gridPane = colorPickerController.getGridPane();
        AnchorPane.setBottomAnchor(gridPane,10d);
        AnchorPane.setRightAnchor(gridPane,10d);
        mainPane.getChildren().add(gridPane);
        colorPickerController.colorProperty.addListener((observableValue, color1, t1) -> setBackground(new Background(new BackgroundFill(t1, CornerRadii.EMPTY, Insets.EMPTY))));
        this.setOnMouseClicked(mouseEvent -> {
            gridPane.visibleProperty().set(!gridPane.visibleProperty().get());
        });
    }

    public Color getColor(){
        return colorPickerController.colorProperty().get();
    }

    public double getRadius(){
        return colorPickerController.getSize();
    }

    private class ColorPickerController{

        @FXML
        private Slider sliderRed,sliderGreen,sliderBlue,sliderSize;

        @FXML
        private Label labelRed,labelGreen,labelBlue,labelSize;

        private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.RED);
        double size = 5;
        private boolean loaded;

        private GridPane gridPane;

        ColorPickerController(){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/ColorPickerDialog.fxml"));
            loader.setController(this);
            try {
                gridPane=loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public GridPane getGridPane() {
            return gridPane;
        }

        public SimpleObjectProperty<Color> colorProperty(){
            return colorProperty;
        }

        private void calculateColor(){
            if(loaded) {
                colorProperty.set(Color.color(sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue()));
            }
        }

        private double getSize(){
            return size;
        }

        @FXML
        private void initialize(){
            sliderRed.valueProperty().addListener((observableValue, number, t1) -> {
                labelRed.setText(Float.toString(Math.round(t1.doubleValue()*100)/100f));
                calculateColor();
            });
            sliderGreen.valueProperty().addListener((observableValue, number, t1) -> {
                labelGreen.setText(Float.toString(Math.round(t1.doubleValue()*100)/100f));
                calculateColor();
            });
            sliderBlue.valueProperty().addListener((observableValue, number, t1) -> {
                labelBlue.setText(Float.toString(Math.round(t1.doubleValue()*100)/100f));
                calculateColor();
            });
            sliderSize.valueProperty().addListener((observableValue, number, t1) -> {
                labelSize.setText(Float.toString(Math.round(t1.doubleValue()*10)/10f));
                size=t1.doubleValue();
            });

            sliderRed.valueProperty().set(colorProperty.get().getRed());
            sliderGreen.valueProperty().set(colorProperty.get().getGreen());
            sliderBlue.valueProperty().set(colorProperty.get().getBlue());
            sliderSize.valueProperty().set(size);
            loaded=true;
        }
    }
}
