package com.mindspice;

import com.mindspice.logic.Util;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {

    private static Stage stage;

    public void start(Stage stage) throws Exception {

        final int initWidth = 1600;
        final int initHeight = 900;
        final Pane root = new Pane();

        Pane controller = FXMLLoader.load(getClass().getResource("gui/Generator_GUI.fxml"));
        controller.setPrefWidth(initWidth);
        controller.setPrefHeight(initHeight);
        root.getChildren().add(controller);

        Scale scale = new Scale(1, 1, 0, 0);
        scale.xProperty().bind(root.widthProperty().divide(initWidth));
        scale.yProperty().bind(root.heightProperty().divide(initHeight));
        root.getTransforms().add(scale);

        final Scene scene = new Scene(root, initWidth, initHeight);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("NFT Generator");
        Image icon = new Image(getClass().getResourceAsStream("icon.png"));
        stage.getIcons().add(icon);

        stage.show();
        //Listens for and rescales window on resize
        scene.rootProperty().addListener(new ChangeListener<Parent>() {
            @Override
            public void changed(ObservableValue<? extends Parent> arg0, Parent oldValue, Parent newValue) {
                scene.rootProperty().removeListener(this);
                scene.setRoot(root);
                ((Region) newValue).setPrefWidth(initWidth);
                ((Region) newValue).setPrefHeight(initHeight);
                root.getChildren().clear();
                root.getChildren().add(newValue);
                scene.rootProperty().addListener(this);
            }
        });

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if (!Util.confirm("exit")) {
                    event.consume();
                }
            }
        });
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);
    }

}

