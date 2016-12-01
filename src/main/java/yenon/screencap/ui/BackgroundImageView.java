package yenon.screencap.ui;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * Created by yenon on 11/23/16.
 */
public class BackgroundImageView extends SelectablePane {

    private final ImageView imageView = new ImageView();

    private void init() {
        imageView.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.color(0,0,0),1,1,1,1));
        this.getChildren().add(imageView);
    }

    protected BackgroundImageView() {
        init();
    }

    public BackgroundImageView(Image image) {
        imageView.setImage(image);
        this.setWidth(image.getWidth());
        this.setHeight(image.getHeight());
        init();
    }

    protected void setImage(Image image) {
        imageView.setImage(image);
        this.setWidth(image.getWidth());
        this.setHeight(image.getHeight());
    }

    @Override
    public void onSelect() {
        imageView.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLUE,1,1,1,1));
    }

    @Override
    public void onDeselect() {
        imageView.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.BLACK,1,1,1,1));
    }
}
