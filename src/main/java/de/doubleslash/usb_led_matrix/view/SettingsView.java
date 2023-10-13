package de.doubleslash.usb_led_matrix.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class SettingsView {

   @FXML
   private ColorPicker AwayColor;

   @FXML
   private ColorPicker availableColor;

   @FXML
   private ColorPicker beRightBackColor;

   @FXML
   private ColorPicker busyColor;

   @FXML
   private Region colorIcon;

   @FXML
   private Region colorIcon1;

   @FXML
   private Button darkModeButton;

   @FXML
   private ColorPicker doNotDisturbColor;



   @FXML
   void saveButton(MouseEvent event) {
    final FileWriter fw;
      try {
      fw = new FileWriter("TeamsColor.properties");
      final Properties p = new Properties();
      p.setProperty("away", String.valueOf(AwayColor.getValue()));
      p.setProperty("doNotDisturb", String.valueOf(doNotDisturbColor.getValue()));
      p.setProperty("busy", String.valueOf(busyColor.getValue()));
      p.setProperty("available", String.valueOf(availableColor.getValue()));
      p.setProperty("beRightBackColor", String.valueOf(AwayColor.getValue()));
      p.store(fw, "Sample comments");
   } catch (
   IOException e) {
      throw new RuntimeException(e);
   }
   }

}
