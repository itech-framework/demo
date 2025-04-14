package org.itech.framework.javafxapp.demo.utils.components;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.geometry.Bounds;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import org.itech.framework.fx.java_fx.utils.SVGUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class SvgIcon extends StackPane {
    private final SVGPath svgPath = new SVGPath();
    private final DoubleProperty iconSize = new SimpleDoubleProperty(24);
    /*private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.BLACK);*/
    private final StringProperty svgFilePath = new SimpleStringProperty();

    public SvgIcon() {
        initialize();
    }

    private void initialize() {
        svgPath.getStyleClass().add("svg-icon-path");
        getChildren().add(svgPath);
        /*svgPath.fillProperty().bind(color);*/

        // Size bindings
        prefWidthProperty().bind(iconSize);
        prefHeightProperty().bind(iconSize);
        maxWidthProperty().bind(iconSize);
        minWidthProperty().bind(iconSize);
        maxHeightProperty().bind(iconSize);
        minHeightProperty().bind(iconSize);

        // Update when file path changes
        svgFilePath.addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                loadSvgFile(newVal);
            }
        });


    }

    // FXML accessible properties
    public void setIconSize(double size) {
        iconSize.set(size);
        scaleSvg();
    }

    public double getIconSize() {
        return iconSize.get();
    }

    public DoubleProperty iconSizeProperty() {
        return iconSize;
    }

    /*public void setColor(Color color) {
        this.color.set(color);
    }

    public Color getColor() {
        return color.get();
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }*/

    public void setSvgFile(String path) {
        svgFilePath.set(path);
    }

    public String getSvgFile() {
        return svgFilePath.get();
    }

    public StringProperty svgFileProperty() {
        return svgFilePath;
    }

    private void loadSvgFile(String path) {
        try (InputStream svgStream = getClass().getResourceAsStream(path)) {
            if (svgStream == null) {
                throw new IOException("SVG resource not found: " + path);
            }
            String pathData = SVGUtil.extractPathData(svgStream);
            svgPath.setContent(pathData);
            scaleSvg();
        } catch (Exception e) {
            System.err.println("Error loading SVG: " + path);
        }
    }

    private void scaleSvg() {
        Platform.runLater(()->{
            Bounds bounds = svgPath.getBoundsInLocal();
            if (bounds.getWidth() == 0 || bounds.getHeight() == 0) return;

            double scale = Math.min(
                    iconSize.get() / bounds.getWidth(),
                    iconSize.get() / bounds.getHeight()
            );

            svgPath.setScaleX(scale);
            svgPath.setScaleY(scale);
        });
    }
}