package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.Settings;
import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SettingsView {
   private static final Logger LOG = LoggerFactory.getLogger(ConfigurationView.class);

   @FXML
   private ColorPicker awayColor;

   @FXML
   private ColorPicker availableColor;

   @FXML
   private ColorPicker beRightBack;

   @FXML
   private ColorPicker busyColor;

   @FXML
   private ColorPicker doNotDisturbColor;

   @FXML
   public void initialize() throws IOException {
      availableColor.setValue(AvailabilityStatus.Available.getColor());
      busyColor.setValue(AvailabilityStatus.Busy.getColor());
      doNotDisturbColor.setValue(AvailabilityStatus.DoNotDisturb.getColor());
      beRightBack.setValue(AvailabilityStatus.BeRightBack.getColor());
      awayColor.setValue(AvailabilityStatus.Away.getColor());

   }

   @FXML
   void saveButton() {
      AvailabilityStatus.Away.setColor(awayColor.getValue());
      AvailabilityStatus.DoNotDisturb.setColor(doNotDisturbColor.getValue());
      AvailabilityStatus.Busy.setColor(busyColor.getValue());
      AvailabilityStatus.BusyIdle.setColor(busyColor.getValue());
      AvailabilityStatus.Available.setColor(availableColor.getValue());
      AvailabilityStatus.BeRightBack.setColor((beRightBack.getValue()));
      AvailabilityStatus.AvailableIdle.setColor(availableColor.getValue());
      Settings.saveProperty();

   }

}
