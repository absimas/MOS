package com.simas.ui;

import com.simas.Scheduler;
import com.simas.processes.Process;
import com.simas.real_machine.Channel1;
import com.simas.real_machine.Channel2;
import com.simas.resources.Element;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class MainController implements Initializable {

  @FXML
  private TableView<Process> processTable;
  @FXML
  private ListView<Element> resourceList;
  @FXML
  private TextField input, output;

  /**
   * Dummy list with an invalid element that's represented by an empty string value. Used when no resources are available to a process.
   */
  private static final List<Element> EMPTY_LIST = Collections.singletonList(new Element(null, null) {
    @Override
    public String toString() {
      return "";
    }
  });

  /**
   * Required c-tor
   */
  public MainController() {
    // Re-draw on scheduler events
    Scheduler.setListener(this::draw);

    new Thread(Scheduler::schedule).start();
  }

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Command TableView
    initializeProcessTable();

    // Resource ListView
    initializeResourceList();

    // Input field listener
    initializeIO();
  }

  private void initializeIO() {
    // Input
    // Set buffer every time input field is changed
    input.textProperty().addListener((observable, oldValue, newValue) -> {
      Channel1.setBuffer(newValue);
    });

    // Re-draw on Channel1 events
    Channel1.setListener(this::draw);

    // Output
    // Disable clicking and in/out focusing
    output.setMouseTransparent(false);
    output.setFocusTraversable(false);
    output.addEventFilter(MouseEvent.ANY, Event::consume);
  }

  private void initializeResourceList() {
  }

  private void initializeProcessTable() {
    // Update resource list when process is selected
    processTable.getSelectionModel().selectedItemProperty().addListener(observable -> {
      updateResourceList();
    });

    processTable.getItems().setAll(Process.PROCESSES);
    processTable.sort();
  }

  private void draw() {
    // Execute drawing on the UI thread
    Platform.runLater(() -> {
      updateResourceList();
      updateProcessTable();
      updateIO();
    });
  }

  private void updateResourceList() {
    final Process process = processTable.getSelectionModel().getSelectedItem();
    if (process == null) return;
    if (process.availableResources.isEmpty()) {
      resourceList.getItems().setAll(EMPTY_LIST);
    } else {
      resourceList.getItems().setAll(process.availableResources);
    }
  }

  private void updateProcessTable() {
    processTable.getItems().setAll(Process.PROCESSES);
    processTable.sort();
  }

  private void updateIO() {
    // Input
    input.setText(Channel1.getBuffer());

    // Output
    output.setText(Channel2.getOutput());

    // Remove focus from both fields
    input.getParent().requestFocus();
    output.getParent().requestFocus();
  }

  /**
   * Called when next button is pressed
   */
  @FXML
  public void schedule() {
    Scheduler.resume();
  }

}
