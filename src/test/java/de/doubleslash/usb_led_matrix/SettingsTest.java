package de.doubleslash.usb_led_matrix;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SettingsTest {
   @Test
   void shouldNotThrowNullPointerExceptionWhenSettingsFileExistsButPropertyIsMissing() throws IOException {
      //ARRANGE
      File file =new File("settings.properties");
      file.createNewFile();
      //ACT+ASSERT
      assertDoesNotThrow(Settings::loadProperty);
   }

}