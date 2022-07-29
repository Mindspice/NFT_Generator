package com.dhickel;


import javafx.application.Application;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class bs extends Application {

    private final ExecutorService exec = Executors.newFixedThreadPool(5, r -> {
        Thread t = new Thread(r);
        t.setDaemon(true);
        return t ;
    });

    private final Random rng = new Random();


    @Override
    public void start(Stage primaryStage) {

        Label pendingTasksLabel = new Label();
        Button button = new Button("Launch tasks");
        TextArea log = new TextArea();

        DoubleProperty progress = new SimpleDoubleProperty(1);

        ProgressBar progressBar = new ProgressBar();
        progressBar.progressProperty().bind(progress);

        IntegerProperty pendingTasks = new SimpleIntegerProperty(0);
        pendingTasksLabel.textProperty().bind(pendingTasks.asString("Pending Tasks: %d"));

        button.disableProperty().bind(pendingTasks.greaterThan(0));

        button.setOnAction(e -> {
            int numTasks = rng.nextInt(5) + 4 ;

            List<Task<Void>> tasks = new ArrayList<>();
            for (int i = 0; i < numTasks; i++) {
                tasks.add(createRandomTask());
            }

            // rebind progress:
            progress.unbind();
            progress.bind( new DoubleBinding() {
                {
                    for (Task<Void> task : tasks) {
                        bind(task.progressProperty());
                    }
                }

                @Override
                public double computeValue() {
                    return tasks.stream().mapToDouble(task -> Math.max(task.getProgress(), 0)).sum() / numTasks;
                }
            });

            log.appendText("Submitting "+numTasks+" tasks\n");

            pendingTasks.set(numTasks);

            // log state of each task:
            tasks.forEach(task ->
                    task.stateProperty().addListener((obs, oldState, newState) -> {
                        log.appendText("\tTask "+newState+"\n");

                        // update pendingTasks if task moves out of running state:
                        if (oldState == Worker.State.RUNNING) {
                            pendingTasks.set(pendingTasks.get() - 1);
                        }
                    }));

            tasks.forEach(exec::execute);
        });

        VBox root = new VBox(10, pendingTasksLabel, progressBar, log, button);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(10));
        VBox.setVgrow(log, Priority.ALWAYS);

        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }

    @Override
    public void stop() {
        exec.shutdownNow() ;
    }

    private Task<Void> createRandomTask() {
        int numSteps = 100 + rng.nextInt(100);
        return new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 1; i <= numSteps; i++) {
                    Thread.sleep(50);
                    updateProgress(i, numSteps);
                }
                return null ;
            }
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}