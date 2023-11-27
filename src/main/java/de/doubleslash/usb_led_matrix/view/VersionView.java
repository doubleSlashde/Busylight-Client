package de.doubleslash.usb_led_matrix.view;

import java.io.IOException;

import de.doubleslash.usb_led_matrix.App;
import de.doubleslash.usb_led_matrix.CommandLineOptions;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;

public class VersionView {
   private Scene scene;

   @FXML
   private Label versionClient;

   @FXML
   private Label versionController;

   public void instantiate(final SimpleStringProperty microcontrollerVersionProperty) throws IOException {
      if (CommandLineOptions.getColorMode().equals("light")) {
         CommandLineOptions.Light(scene);
      }
      if (CommandLineOptions.getColorMode().equals("dark")) {
         CommandLineOptions.Dark(scene);
      }
      versionClient.setText(App.clientVersion);
      versionController.textProperty().bind(microcontrollerVersionProperty);
   }

   public void setScene(final Scene scene) {
      this.scene = scene;
   }
}