package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.graph.Graph;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class SettingsView {

   @FXML
   private ColorPicker AwayColor;

   @FXML
   private ColorPicker availableColor;

   @FXML
   private ColorPicker beRightBack;

   @FXML
   private ColorPicker busyColor;

   @FXML
   private ColorPicker doNotDisturbColor;




   @FXML
   private Label saveLable;
   Graph graph;

   @FXML
   public void initialize() throws IOException {
      loadProperty();
   }

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
         p.setProperty("beRightBack", String.valueOf(AwayColor.getValue()));
         p.store(fw, "Sample comments");
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
      loadProperty();

   }

   public void loadProperty() {
      final String fileName = "TeamsColor.properties";
      try {
         final InputStream input = new FileInputStream(new File(fileName));
         final Properties properties = new Properties();
         properties.load(input);
         final Color availableStatusColor = Color.valueOf(properties.getProperty("available"));
         final Color awayStatusColor = Color.valueOf(properties.getProperty("away"));
          final Color beRightBackStatusColor = Color.valueOf(properties.getProperty("beRightBack"));
         final Color busyStatusColor = Color.valueOf(properties.getProperty("busy"));
         final Color doNotDisturbStatusColor = Color.valueOf(properties.getProperty("doNotDisturb"));

         availableColor.setValue(availableStatusColor);
         busyColor.setValue(busyStatusColor);
         doNotDisturbColor.setValue(doNotDisturbStatusColor);
         beRightBack.setValue(beRightBackStatusColor);
         AwayColor.setValue(awayStatusColor);

      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);

      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

}
