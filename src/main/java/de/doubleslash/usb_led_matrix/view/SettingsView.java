package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

public class SettingsView {
   private static final Logger LOG = LoggerFactory.getLogger(ConfigurationView.class);

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

   String filePath = "TeamsColor.properties";

   @FXML
   public void initialize() throws IOException {
      loadProperty();
   }

   @FXML
   void saveButton(MouseEvent event) {
      AvailabilityStatus.Away.setColor(AwayColor.getValue());
      AvailabilityStatus.DoNotDisturb.setColor(doNotDisturbColor.getValue());
      AvailabilityStatus.Busy.setColor(busyColor.getValue());
      AvailabilityStatus.BusyIdle.setColor(busyColor.getValue());

      AvailabilityStatus.Available.setColor(availableColor.getValue());
      AvailabilityStatus.BeRightBack.setColor((beRightBack.getValue()));
      AvailabilityStatus.AvailableIdle.setColor(availableColor.getValue());

      try (final FileWriter fw = new FileWriter("TeamsColor.properties")) {
         final Properties p = new Properties();
         p.setProperty("away", String.valueOf(AwayColor.getValue()));
         p.setProperty("doNotDisturb", String.valueOf(doNotDisturbColor.getValue()));
         p.setProperty("busy", String.valueOf(busyColor.getValue()));
         p.setProperty("busyIdle", String.valueOf(busyColor.getValue()));
         p.setProperty("available", String.valueOf(availableColor.getValue()));
         p.setProperty("availableIdle", String.valueOf(availableColor.getValue()));

         p.setProperty("beRightBack", String.valueOf(beRightBack.getValue()));
         p.store(fw, "");
      } catch (IOException e) {

      }
   }

   public void loadProperty() throws IOException {
      final File file = new File(filePath);
      if (file.exists()) {
         try (final FileReader fr = new FileReader(filePath)) {
            final Properties properties = new Properties();
            properties.load(fr);

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
            LOG.error("File was not Found");
         }
      }
   }
}