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

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
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
      int r = 0;
      int g = 0;
      int b = 0;
      final Map<AvailabilityStatus, Color> expectedColors = new HashMap<>();
      expectedColors.put(AvailabilityStatus.Available, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.AvailableIdle, expectedColors.get(AvailabilityStatus.Available));
      expectedColors.put(AvailabilityStatus.Away, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.BeRightBack, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.Busy, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.BusyIdle, expectedColors.get(AvailabilityStatus.Busy));
      expectedColors.put(AvailabilityStatus.DoNotDisturb, Color.rgb(r++, g++, b++));

      ((ColorPicker) availableColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.Available));
      ((ColorPicker) awayColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.Away));
      ((ColorPicker) beRightBack.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.BeRightBack));
      ((ColorPicker) busyColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.Busy));
      ((ColorPicker) doNotDisturbColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.DoNotDisturb));

      // ACT
      settingsView.saveButton();

      // ASSERT
      assertThat(AvailabilityStatus.Available.getColor(), is(expectedColors.get(AvailabilityStatus.Available)));
      assertThat(AvailabilityStatus.AvailableIdle.getColor(), is(expectedColors.get(AvailabilityStatus.AvailableIdle)));
      assertThat(AvailabilityStatus.Away.getColor(), is(expectedColors.get(AvailabilityStatus.Away)));
      assertThat(AvailabilityStatus.BeRightBack.getColor(), is(expectedColors.get(AvailabilityStatus.BeRightBack)));
      assertThat(AvailabilityStatus.Busy.getColor(), is(expectedColors.get(AvailabilityStatus.Busy)));
      assertThat(AvailabilityStatus.BusyIdle.getColor(), is(expectedColors.get(AvailabilityStatus.BusyIdle)));
      assertThat(AvailabilityStatus.DoNotDisturb.getColor(), is(expectedColors.get(AvailabilityStatus.DoNotDisturb)));
   }

   @Test
   public void shouldSaveAvailabilityStatusColorsInPropertyFileWhenSaveProperty()
         throws IOException, IllegalAccessException {
      // ARRANGE
      int r = 0;
      int g = 0;
      int b = 0;
      final Map<AvailabilityStatus, Color> expectedColors = new HashMap<>();
      expectedColors.put(AvailabilityStatus.Available, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.AvailableIdle, expectedColors.get(AvailabilityStatus.Available));
      expectedColors.put(AvailabilityStatus.Away, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.BeRightBack, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.Busy, Color.rgb(r++, g++, b++));
      expectedColors.put(AvailabilityStatus.BusyIdle, expectedColors.get(AvailabilityStatus.Busy));
      expectedColors.put(AvailabilityStatus.DoNotDisturb, Color.rgb(r++, g++, b++));

      ((ColorPicker) availableColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.Available));
      ((ColorPicker) awayColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.Away));
      ((ColorPicker) beRightBack.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.BeRightBack));
      ((ColorPicker) busyColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.Busy));
      ((ColorPicker) doNotDisturbColor.get(settingsView)).setValue(expectedColors.get(AvailabilityStatus.DoNotDisturb));

      // ACT
      settingsView.saveButton();

      //ASSERT
      final Properties properties = new Properties();
      FileReader fr = new FileReader(Settings.filePath);
      properties.load(fr);

      assertThat(properties.getProperty(AvailabilityStatus.Available.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.Available).toString()));
      assertThat(properties.getProperty(AvailabilityStatus.AvailableIdle.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.AvailableIdle).toString()));
      assertThat(properties.getProperty(AvailabilityStatus.Away.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.Away).toString()));
      assertThat(properties.getProperty(AvailabilityStatus.Busy.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.Busy).toString()));
      assertThat(properties.getProperty(AvailabilityStatus.BusyIdle.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.BusyIdle).toString()));
      assertThat(properties.getProperty(AvailabilityStatus.BeRightBack.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.BeRightBack).toString()));
      assertThat(properties.getProperty(AvailabilityStatus.DoNotDisturb.getPropertyKey()),
            is(expectedColors.get(AvailabilityStatus.DoNotDisturb).toString()));
   }
}
