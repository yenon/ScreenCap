package yenon.screencap.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Created by yenon on 11/24/16.
 */
public class ColorPane extends SelectablePane {

    private final ColorPickerController colorPickerController;

    public ColorPane(AnchorPane mainPane) {
        setEffect(new DropShadow(BlurType.GAUSSIAN,Color.color(0,0,0),1,1,1,1));
        setHeight(16);
        setWidth(16);
        setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
        colorPickerController = new ColorPickerController();
        GridPane gridPane = colorPickerController.getGridPane();
        AnchorPane.setBottomAnchor(gridPane, 10d);
        AnchorPane.setRightAnchor(gridPane, 10d);
        mainPane.getChildren().add(gridPane);
        colorPickerController.colorProperty.addListener((observableValue, color1, t1) -> setBackground(new Background(new BackgroundFill(t1, CornerRadii.EMPTY, Insets.EMPTY))));
        this.setOnMouseClicked(mouseEvent -> gridPane.visibleProperty().set(!gridPane.visibleProperty().get()));
    }

    public Color getColor() {
        return colorPickerController.colorProperty().get();
    }

    public double getRadius() {
        return colorPickerController.getSize();
    }

    @Override
    public void onSelect() {
        setEffect(new DropShadow(BlurType.GAUSSIAN,Color.BLUE,1,1,1,1));

    }

    @Override
    public void onDeselect() {
        setEffect(new DropShadow(BlurType.GAUSSIAN,Color.BLACK,1,1,1,1));
    }

    private class ColorPickerController {

        @FXML
        private Slider sliderRed, sliderGreen, sliderBlue, sliderSize;

        @FXML
        private Label labelRed, labelGreen, labelBlue, labelSize;

        private final SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.RED);
        double size = 5;
        private boolean loaded;

        private GridPane gridPane;

        ColorPickerController() {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/ColorPickerDialog.fxml"));
            loader.setController(this);
            try {
                gridPane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        GridPane getGridPane() {
            return gridPane;
        }

        SimpleObjectProperty<Color> colorProperty() {
            return colorProperty;
        }

        private void calculateColor() {
            if (loaded) {
                colorProperty.set(Color.color(sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue()));
            }
        }

        private double getSize() {
            return size;
        }

        @SuppressWarnings("unused")
        @FXML
        private void initialize() {
            sliderRed.valueProperty().addListener((observableValue, number, t1) -> {
                labelRed.setText(Long.toString(Math.round(t1.doubleValue() * 255)));
                calculateColor();
            });
            sliderGreen.valueProperty().addListener((observableValue, number, t1) -> {
                labelGreen.setText(Long.toString(Math.round(t1.doubleValue() * 255)));
                calculateColor();
            });
            sliderBlue.valueProperty().addListener((observableValue, number, t1) -> {
                labelBlue.setText(Long.toString(Math.round(t1.doubleValue() * 255)));
                calculateColor();
            });
            sliderSize.valueProperty().addListener((observableValue, number, t1) -> {
                labelSize.setText(Float.toString(Math.round(t1.doubleValue() * 10) / 10f));
                size = t1.doubleValue();
            });

            sliderRed.valueProperty().set(colorProperty.get().getRed());
            sliderGreen.valueProperty().set(colorProperty.get().getGreen());
            sliderBlue.valueProperty().set(colorProperty.get().getBlue());
            sliderSize.valueProperty().set(size);
            loaded = true;
        }
    }
}
