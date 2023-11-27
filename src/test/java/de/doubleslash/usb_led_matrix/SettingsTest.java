package de.doubleslash.usb_led_matrix;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SettingsTest {
   private static final String filePath = "settings.properties";
   private static final String fileContent =
         // @formatter:off
          "away=0xffa500ff\n"
        + "doNotDisturb=0xff0000ff\n"
        + "busy=0xff00000f\n"
        + "available=0x00ff0fff\n"
        + "beRightBack=0xffa550ff\n";
         // @formatter:on

   @Test
   void shouldLoadSettingsSuccessfullyWhenFileExists() throws IOException {
      createPropertiesFile();

      Settings.loadSettings();

      assertThat(AvailabilityStatus.Available.getColor(), equalTo(Color.web("#00ff0fff")));
      assertThat(AvailabilityStatus.AvailableIdle.getColor(), equalTo(Color.web("#00ff0fff")));
      assertThat(AvailabilityStatus.Away.getColor(), equalTo(Color.web("#ffa500ff")));
      assertThat(AvailabilityStatus.BeRightBack.getColor(), equalTo(Color.web("#ffa550ff")));
      assertThat(AvailabilityStatus.Busy.getColor(), equalTo(Color.web("#ff00000f")));
      assertThat(AvailabilityStatus.BusyIdle.getColor(), equalTo(Color.web("#ff00000f")));
      assertThat(AvailabilityStatus.DoNotDisturb.getColor(), equalTo(Color.web("#ff0000ff")));
   }

   @Test
   void shouldNotThrowNullPointerExceptionWhenSettingsFileExistsButPropertyIsMissing() throws IOException {
      //ARRANGE
      final File file = new File(filePath);
      file.createNewFile();

      //ACT+ASSERT
      assertDoesNotThrow(Settings::loadSettings);
   }

   @Test
   void shouldThrowExceptionWhenFileDoesNotExist() {
      //ARRANGE
      final File file = new File(filePath);
      assertThat(file.exists(), is(false));

      //ACT+ASSERT
      assertDoesNotThrow(Settings::loadSettings);
   }

   private void createPropertiesFile() throws IOException {
      File file = new File(filePath);
      FileWriter fileWriter = new FileWriter(file);
      fileWriter.write(fileContent);
      fileWriter.close();
   }

   @AfterEach
   private void deleteFileIfExists() {
      final File file = new File(filePath);
      if (file.exists()) {
         file.delete();
      }
   }
}
