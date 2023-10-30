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

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

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
   void shouldSetStatusColorsToPinkWhenSaveButtonClicked() throws IllegalAccessException {
      // ARRANGE
      ((ColorPicker) awayColor.get(settingsView)).setValue(Color.PINK);
      ((ColorPicker) availableColor.get(settingsView)).setValue(Color.PINK);
      ((ColorPicker) beRightBack.get(settingsView)).setValue(Color.PINK);
      ((ColorPicker) busyColor.get(settingsView)).setValue(Color.PINK);
      ((ColorPicker) doNotDisturbColor.get(settingsView)).setValue(Color.PINK);

      // ACT
      settingsView.saveButton();

      // ASSERT
      assertThat(AvailabilityStatus.Away.getColor(), is(Color.PINK));
      assertThat(AvailabilityStatus.Available.getColor(), is(Color.PINK));
      assertThat(AvailabilityStatus.AvailableIdle.getColor(), is(Color.PINK));
      assertThat(AvailabilityStatus.BeRightBack.getColor(), is(Color.PINK));
      assertThat(AvailabilityStatus.Busy.getColor(), is(Color.PINK));
      assertThat(AvailabilityStatus.BusyIdle.getColor(), is(Color.PINK));
      assertThat(AvailabilityStatus.DoNotDisturb.getColor(), is(Color.PINK));
   }

   @Test
   public void shouldSaveAvailabilityStatusColorsInPropertyFileWhenSaveProperty()
         throws IOException, IllegalAccessException {
      // ARRANGE
      final Color expectedColor = Color.PINK;
      ((ColorPicker) awayColor.get(settingsView)).setValue(expectedColor);
      ((ColorPicker) availableColor.get(settingsView)).setValue(expectedColor);
      ((ColorPicker) beRightBack.get(settingsView)).setValue(expectedColor);
      ((ColorPicker) busyColor.get(settingsView)).setValue(expectedColor);
      ((ColorPicker) doNotDisturbColor.get(settingsView)).setValue(expectedColor);

      // ACT
      settingsView.saveButton();

      //ASSERT
      final Properties properties = new Properties();
      FileReader fr = new FileReader("settings.properties");
      properties.load(fr);

      assertThat(properties.getProperty(AvailabilityStatus.Available.getPropertyKey()), is(expectedColor.toString()));
      assertThat(properties.getProperty(AvailabilityStatus.AvailableIdle.getPropertyKey()),
            is(expectedColor.toString()));
      assertThat(properties.getProperty(AvailabilityStatus.Busy.getPropertyKey()), is(expectedColor.toString()));
      assertThat(properties.getProperty(AvailabilityStatus.BusyIdle.getPropertyKey()), is(expectedColor.toString()));
      assertThat(properties.getProperty(AvailabilityStatus.BeRightBack.getPropertyKey()), is(expectedColor.toString()));
      assertThat(properties.getProperty(AvailabilityStatus.Away.getPropertyKey()), is(expectedColor.toString()));

   }
}
