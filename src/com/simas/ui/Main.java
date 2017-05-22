package com.simas.ui;

import com.simas.Scheduler;
import com.simas.processes.Root;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

  @Override
  public void start(Stage primaryStage) throws Exception {
    final Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
    primaryStage.setTitle("MOS");
    primaryStage.setScene(new Scene(root));
    primaryStage.show();

    // After fitting all the content use those measurements as min screen size
    primaryStage.setMinWidth(primaryStage.getWidth());
    primaryStage.setMinHeight(primaryStage.getHeight());
    primaryStage.sizeToScene();
  }

  public static void main(String[] args) {
    // Start ROOT process // Will also start its worker thread
    Scheduler.currentProcess = new Root();

    // Launch UI
    launch(args);
  }

}
