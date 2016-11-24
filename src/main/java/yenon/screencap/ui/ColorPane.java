package yenon.screencap.ui;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by yenon on 11/24/16.
 */
public class ColorPane extends Pane {

    private Color color = Color.RED;
    private double radius = 5;

    public ColorPane(){
        setHeight(16);
        setWidth(16);
        setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));

        this.setOnMouseClicked(mouseEvent -> {
            ColorPickerDialog dialog = new ColorPickerDialog(color,radius);
            dialog.colorProperty.addListener((observableValue, color1, t1) -> setBackground(new Background(new BackgroundFill(t1, CornerRadii.EMPTY, Insets.EMPTY))));
            Optional<Pair<Color,Double>> pairColorRadius = dialog.showAndWait();
            if(pairColorRadius.isPresent()){
                ColorPane.this.color=pairColorRadius.get().getKey();
                radius=pairColorRadius.get().getValue();
                setBackground(new Background(new BackgroundFill(ColorPane.this.color, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        });
    }

    public Color getColor(){
        return color;
    }

    public double getRadius(){
        return radius;
    }

    private class ColorPickerDialog extends Dialog<Pair<Color,Double>>{

        @FXML
        private Slider sliderRed,sliderGreen,sliderBlue,sliderSize;

        @FXML
        private Label labelRed,labelGreen,labelBlue,labelSize;

        private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();
        private Color oldColor;
        private double radiusOld;
        private boolean loaded;

        ColorPickerDialog(Color current,double radius){
            oldColor =new Color(current.getRed(),current.getGreen(),current.getBlue(),current.getOpacity());
            radiusOld=radius;
            colorProperty.set(current);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/layouts/ColorPickerDialog.fxml"));
            loader.setController(this);
            try {
                getDialogPane().setContent(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }
            getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            setResultConverter(buttonType -> {
                if(buttonType == ButtonType.OK){
                    return new Pair<>(colorProperty.get(), sliderSize.getValue());
                }else {
                    return new Pair<>(oldColor, radiusOld);
                }
            });
        }

        public SimpleObjectProperty<Color> colorProperty(){
            return colorProperty;
        }

        private void calculateColor(){
            if(loaded) {
                colorProperty.set(Color.color(sliderRed.getValue(), sliderGreen.getValue(), sliderBlue.getValue()));
            }
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
                labelSize.setText(Float.toString(Math.round(t1.doubleValue()*100)/100f));
                radius=t1.doubleValue();
            });

            sliderRed.valueProperty().set(colorProperty.get().getRed());
            sliderGreen.valueProperty().set(colorProperty.get().getGreen());
            sliderBlue.valueProperty().set(colorProperty.get().getBlue());
            sliderSize.valueProperty().set(radius);
            loaded=true;
        }
    }
}
