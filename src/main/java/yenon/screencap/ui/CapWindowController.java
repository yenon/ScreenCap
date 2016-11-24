package yenon.screencap.ui;

import javafx.application.HostServices;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import yenon.screencap.draw.DrawHandler;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yenon on 11/19/16.
 */
public class CapWindowController {
    private Stage primaryStage;
    private WritableImage screenshotImage;
    private DrawHandler drawHandler;
    private HostServices hostServices;

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    @FXML
    private Pane canvasPane;
    @FXML
    private Canvas canvasPreview;
    @FXML
    private Canvas canvasSelection;
    @FXML
    private SelectingTilePane flowPaneTools;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;

        try {
            Robot robot = new Robot(MouseInfo.getPointerInfo().getDevice());
            Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
            BufferedImage image = robot.createScreenCapture(new Rectangle(screenDimensions));
            screenshotImage = new WritableImage(screenDimensions.width, screenDimensions.height);
            SwingFXUtils.toFXImage(image, screenshotImage);
            drawHandler = new DrawHandler(canvasPreview,canvasSelection,screenshotImage, flowPaneTools);
            primaryStage.fullScreenProperty().addListener((observableValue, aBoolean, t1) -> {
                if(!t1){
                    System.exit(0);
                }
            });
            Platform.runLater(() -> {
                primaryStage.show();
                canvasPreview.getGraphicsContext2D().drawImage(screenshotImage, 0, 0);
                drawHandler.redrawCanvas();
            });
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        //region UploadTool
        BackgroundImageView toolImageView = new BackgroundImageView(new Image(getClass().getResourceAsStream("/images/upload.png")));
        toolImageView.setDisable(true);
        toolImageView.setPickOnBounds(true);
        toolImageView.setOnMouseClicked(event -> closeWindow());
        Tooltip.install(toolImageView, new Tooltip("Upload to <websitename>"));
        flowPaneTools.addNode(toolImageView);
        //endregion
        //region CloseTool
        toolImageView = new BackgroundImageView(new Image(getClass().getResourceAsStream("/images/close.png")));
        toolImageView.setPickOnBounds(true);
        toolImageView.setOnMouseClicked(event -> closeWindow());
        Tooltip.install(toolImageView, new Tooltip("Close the application"));
        flowPaneTools.addNode(toolImageView);
        //endregion
        //region SaveTool
        toolImageView = new BackgroundImageView(new Image(getClass().getResourceAsStream("/images/save.png")));
        toolImageView.setPickOnBounds(true);
        toolImageView.setOnMouseClicked(event -> {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
            Path image = Paths.get(System.getProperty("user.home"), "ScreenCap", "Screenshots", dateFormat.format(date) + ".png");
            if (!Files.isDirectory(image.getParent())) {
                try {
                    Files.createDirectories(image.getParent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (Files.isDirectory(image.getParent())) {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(drawHandler.getCurrentImage(), null), "png", Files.newOutputStream(image));
                    Toast toast = new Toast("Image saved to\n" + image.toString(), 5000);
                    toast.setOnClickListener(mouseEvent -> {
                        System.out.println("Ya clicked me");
                        try {
                            if (hostServices != null) {
                                System.out.println(image.toUri().toString());
                                hostServices.showDocument(image.toUri().toString());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        toast.close();
                        closeWindow();
                    });
                    toast.setOnFinish(this::closeWindow);
                    toast.show();
                    primaryStage.hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Tooltip.install(toolImageView, new Tooltip("Save to disk"));
        flowPaneTools.addNode(toolImageView);
        //endregion
        //region ClipboardTool
        toolImageView = new BackgroundImageView(new Image(getClass().getResourceAsStream("/images/clipboard.png")));
        toolImageView.setPickOnBounds(true);
        toolImageView.setOnMouseClicked(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(drawHandler.getCurrentImage());
            clipboard.setContent(content);
            Toast toast = new Toast("Image copied to Clipboard.", 5000);
            toast.setOnFinish(this::closeWindow);
            primaryStage.hide();
            toast.show();
        });
        Tooltip.install(toolImageView, new Tooltip("Copy to clipboard"));
        flowPaneTools.addNode(toolImageView);
        //endregion

        InvalidationListener listener = observable -> canvasPreview.getGraphicsContext2D().drawImage(screenshotImage, 0, 0);
        canvasPreview.widthProperty().bind(canvasPane.widthProperty());
        canvasPreview.heightProperty().bind(canvasPane.heightProperty());
        canvasPreview.widthProperty().addListener(listener);
        canvasPreview.heightProperty().addListener(listener);
    }

    @FXML
    private void closeWindow() {
        System.exit(0);
    }
}
