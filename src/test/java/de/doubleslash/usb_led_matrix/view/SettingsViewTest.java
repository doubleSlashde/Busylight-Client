package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.Settings;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
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




   @Test
   public void shouldSaveAvailabilityStatusColorsInPropertyFileWhenSaveProperty() throws IOException {
      final Properties properties = new Properties();
      FileReader fr = new FileReader("settings.properties");
//      FileReader fr = new FileReader("src/test/resources/settingsTest.properties");
      properties.load(fr);
     assertEquals(AvailabilityStatus.Away.getColor().toString(), properties.getProperty(AvailabilityStatus.Away.getPropertyKey()));
      assertEquals(AvailabilityStatus.DoNotDisturb.getColor().toString(), properties.getProperty(AvailabilityStatus.DoNotDisturb.getPropertyKey()));
      assertEquals(AvailabilityStatus.Busy.getColor().toString(), properties.getProperty(AvailabilityStatus.Busy.getPropertyKey()));
      assertEquals(AvailabilityStatus.BusyIdle.getColor().toString(), properties.getProperty(AvailabilityStatus.Busy.getPropertyKey()));
      assertEquals(AvailabilityStatus.BeRightBack.getColor().toString(), properties.getProperty(AvailabilityStatus.BeRightBack.getPropertyKey()));
      assertEquals(AvailabilityStatus.Available.getColor().toString(), properties.getProperty(AvailabilityStatus.Available.getPropertyKey()));
      assertEquals(AvailabilityStatus.AvailableIdle.getColor().toString(), properties.getProperty(AvailabilityStatus.Available.getPropertyKey()));
   }

   @Test
   void shouldNotNullIfPropertiesFileDoesNotExist() throws IOException {
      String filePath = "src/test/resources/settingsTestNull.properties";

      final File file = new File(filePath);

      if (file.exists()) {
         file.delete();
      }
      assertNotNull(AvailabilityStatus.Away.getColor());
      assertNotNull(AvailabilityStatus.DoNotDisturb.getColor());
      assertNotNull(AvailabilityStatus.Busy.getColor());
      assertNotNull(AvailabilityStatus.BusyIdle.getColor());
      assertNotNull(AvailabilityStatus.BeRightBack.getColor());
      assertNotNull(AvailabilityStatus.Available.getColor());
      assertNotNull(AvailabilityStatus.AvailableIdle.getColor());
   }


}
