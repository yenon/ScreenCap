package yenon.screencap.ui;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

/**
 * Created by yenon on 11/23/16.
 */
public class BackgroundImageView extends Pane {

    private final ImageView imageView = new ImageView();

    private void init() {
        this.getChildren().add(imageView);
    }

    public BackgroundImageView() {
        init();
    }

    public BackgroundImageView(Image image) {
        imageView.setImage(image);
        this.setWidth(image.getWidth());
        this.setHeight(image.getHeight());
        init();
    }

    public void setImage(Image image) {
        imageView.setImage(image);
        this.setWidth(image.getWidth());
        this.setHeight(image.getHeight());
    }
}
