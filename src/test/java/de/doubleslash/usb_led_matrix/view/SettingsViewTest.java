package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import de.doubleslash.usb_led_matrix.resources.Resources;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.io.IOException;
import java.lang.reflect.Field;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
class SettingsViewTest {
   private SettingsView settingsView;
   private Field awayColor;

   private Field availableColor;

   private Field beRightBack;
   private Field busyColor;

   private Field doNotDisturbColor;

   @Start
   void start(final Stage primaryStage) throws IOException, NoSuchFieldException {

      final FXMLLoader fxmlLoader = new FXMLLoader(Resources.SETTINGS_VIEW.getResource());
      fxmlLoader.load();
      settingsView = fxmlLoader.getController();

      awayColor = settingsView.getClass().getDeclaredField("awayColor");
      awayColor.setAccessible(true);

      availableColor = settingsView.getClass().getDeclaredField("availableColor");
      availableColor.setAccessible(true);

      beRightBack = settingsView.getClass().getDeclaredField("beRightBack");
      beRightBack.setAccessible(true);

      busyColor = settingsView.getClass().getDeclaredField("busyColor");
      busyColor.setAccessible(true);

      doNotDisturbColor = settingsView.getClass().getDeclaredField("doNotDisturbColor");
      doNotDisturbColor.setAccessible(true);

   }

   @Test
   void shouldSetAwayStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      // ARRANGE
      ((ColorPicker) awayColor.get(settingsView)).setValue(Color.PINK);
      // ACT
      settingsView.saveButton();
      // ASSERT
      assertThat(AvailabilityStatus.Away.getColor(), is(Color.PINK));
   }

   @Test
   void shouldSetAvailableColorStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      ((ColorPicker) availableColor.get(settingsView)).setValue(Color.PINK);
      settingsView.saveButton();
      assertThat(AvailabilityStatus.Available.getColor(), is(Color.PINK));
   }

   @Test
   void shouldSetAvailableColorIdleStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      ((ColorPicker) availableColor.get(settingsView)).setValue(Color.PINK);
      settingsView.saveButton();
      assertThat(AvailabilityStatus.AvailableIdle.getColor(), is(Color.PINK));
   }

   @Test
   void shouldSetBeRightBackStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      ((ColorPicker) beRightBack.get(settingsView)).setValue(Color.PINK);
      settingsView.saveButton();
      assertThat(AvailabilityStatus.BeRightBack.getColor(), is(Color.PINK));
   }

   @Test
   void shouldSetBusyColorStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      ((ColorPicker) busyColor.get(settingsView)).setValue(Color.PINK);
      settingsView.saveButton();
      assertThat(AvailabilityStatus.Busy.getColor(), is(Color.PINK));
   }

   @Test
   void shouldSetBusyColorIdleStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      ((ColorPicker) busyColor.get(settingsView)).setValue(Color.PINK);
      settingsView.saveButton();
      assertThat(AvailabilityStatus.BusyIdle.getColor(), is(Color.PINK));
   }

   @Test
   void shouldSetDoNotDisturbColorStatusColorToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      ((ColorPicker) doNotDisturbColor.get(settingsView)).setValue(Color.PINK);
      settingsView.saveButton();
      assertThat(AvailabilityStatus.DoNotDisturb.getColor(), is(Color.PINK));
   }

}