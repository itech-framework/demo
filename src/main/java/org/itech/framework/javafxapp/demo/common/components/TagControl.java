package org.itech.framework.javafxapp.demo.common.components;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.*;

import java.util.List;

public class TagControl extends StackPane {
    private static final StyleablePropertyFactory<TagControl> FACTORY =
            new StyleablePropertyFactory<>(StackPane.getClassCssMetaData());

    private final Label label = new Label();
    private final Path tagShape = new Path();


    private final StyleableProperty<Number> cornerRadius =
            FACTORY.createStyleableNumberProperty(this, "-fx-corner-radius", "-corner-radius", s -> s.cornerRadius, 8.0);

    public TagControl() {
        initialize();
    }

    public TagControl(String text) {
        this();
        setText(text);
    }

    private void initialize() {
        getStyleClass().add("tag-control");
        label.getStyleClass().add("tag-label");

        // Bind shape to size changes
        layoutBoundsProperty().addListener((obs, oldVal, newVal) ->
                updateShape(newVal.getWidth(), newVal.getHeight()));

        getChildren().addAll(label);
        setShape(tagShape);
        setPrefSize(100, 30);
    }

    private void updateShape(double width, double height) {
        tagShape.getElements().clear();
        double notchSize = height * 0.3;
        double radius = cornerRadius.getValue().doubleValue();

        tagShape.getElements().addAll(
                new MoveTo(notchSize, 0),
                new LineTo(width - radius, 0),
                new ArcTo(radius, radius, 0, width, radius, false, true),
                new LineTo(width, height - radius),
                new ArcTo(radius, radius, 0, width - radius, height, false, true),
                new LineTo(notchSize, height),
                new LineTo(0, height / 2),
                new ClosePath()
        );

    }

    // CSS metadata
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    // Properties
    public String getText() {
        return label.getText();
    }

    public void setText(String value) {
        label.setText(value);
    }

    public double getCornerRadius() {
        return cornerRadius.getValue().doubleValue();
    }

    public void setCornerRadius(double value) {
        cornerRadius.setValue(value);
    }
}