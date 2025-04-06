package org.itech.framework.javafxapp.demo.controllers.tasks;

import javafx.event.ActionEvent;
import org.itech.framework.fx.core.annotations.reactives.Rx;
import org.itech.framework.fx.java_fx.annotations.FxController;
import org.itech.framework.fx.java_fx.router.Router;
import org.itech.framework.fx.java_fx.router.core.Routable;

@FxController
public class MyTaskViewController implements Routable {

    @Rx
    Router router;

    @Override
    public void onNavigate(Object arguments) {
        Routable.super.onNavigate(arguments);
        System.out.println("arguments: " + arguments);
    }

    @Override
    public void onReturn(Object result) {
        Routable.super.onReturn(result);
        System.out.println("return: " + result);
    }

    public void handleBackBtnClicked(ActionEvent actionEvent) {
        router.back();
    }
}
