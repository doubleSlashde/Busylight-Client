package de.doubleslash.usb_led_matrix;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SettingsTest {
   private final String fileName = Settings.filePath;

   @Test
   void shouldNotThrowNullPointerExceptionWhenSettingsFileExistsButPropertyIsMissing() throws IOException {
      //ARRANGE
      final File file = new File(fileName);
      file.createNewFile();

      //ACT+ASSERT
      assertDoesNotThrow(Settings::loadProperty);
   }

   @Test
   void shouldThrowExceptionWhenFileDoesNotExist() {
      //ARRANGE
      final File file = new File(fileName);
      assertThat(file.exists(), is(false));

      //ACT+ASSERT
      assertDoesNotThrow(Settings::loadProperty);
   }

   @AfterEach
   private void deleteFileIfExists() {
      final File file = new File(fileName);
      if (file.exists()) {
         file.delete();
      }
   }
}
