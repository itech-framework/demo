package org.itech.framework.javafxapp.demo.controllers.dashboard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.itech.framework.fx.core.annotations.methods.InitMethod;
import org.itech.framework.fx.core.annotations.properties.Property;
import org.itech.framework.fx.core.annotations.reactives.Rx;
import org.itech.framework.fx.core.annotations.storage.DataStorage;
import org.itech.framework.fx.core.utils.DataStorageUtil;
import org.itech.framework.fx.java_fx.annotations.FxController;
import org.itech.framework.fx.java_fx.router.Router;
import org.itech.framework.fx.java_fx.router.core.Routable;
import javafx.scene.input.MouseEvent;

@FxController
public class DashboardController implements Routable {

    @FXML
    public TextField searchInput;
    @FXML
    public Label title;

    @FXML
    private Button darkModeBtn;

    @Property(key = "flexi.app.name")
    private String appName;

    @Rx
    Router router;

    @DataStorage(key = "isDarkMode")
    private boolean isDarkMode;

    @InitMethod
    public void initializeData(){
        title.setText(appName);
        Platform.runLater(()->{
            darkModeBtn.setText(isDarkMode ? "Light Mode" : "Dark Mode");
            darkModeBtn.setOnAction((e) -> {
                isDarkMode = !isDarkMode;
                DataStorageUtil.save("isDarkMode", isDarkMode);
                router.refresh();
            });
        });
    }

    @FXML
    public void handleCardClick(MouseEvent event) {
        AnchorPane clickedCard = (AnchorPane) event.getSource();
        String styleClass = clickedCard.getStyleClass().toString();

        ObjectMapper mapper = new ObjectMapper();

        try {
            System.out.println(mapper.writeValueAsString(router));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (styleClass.contains("today")) {
            Platform.runLater(()->{
                router.to("tasks");
            });
        } else if (styleClass.contains("fav")) {
            System.out.println("Fav Page Redirect.");
        } else if (styleClass.contains("done")) {
            System.out.println("Completed Page Redirect.");
        } else if (styleClass.contains("note")) {
            System.out.println("Note Page redirect");
        }
    }
}
