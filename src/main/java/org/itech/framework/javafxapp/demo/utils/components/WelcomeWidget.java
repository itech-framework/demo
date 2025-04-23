package org.itech.framework.javafxapp.demo.utils.components;

import io.github.itech_framework.core.annotations.constructor.DefaultConstructor;
import io.github.itech_framework.core.annotations.methods.OnInit;
import io.github.itech_framework.java_fx.annotations.components.FxComponent;
import io.github.itech_framework.java_fx.annotations.components.Root;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;


@FxComponent("/views/components/welcome-widget.fxml")
@Getter
public class WelcomeWidget{
    @FXML
    @Root
    private VBox root;

    @FXML
    private Label welcomeTitle;

    private String title;

    @DefaultConstructor
    public WelcomeWidget(){}

    public WelcomeWidget(String title){
        this.title = title;
    }

    @OnInit
    private void onInit(){
        welcomeTitle.setText(title);
    }
}
