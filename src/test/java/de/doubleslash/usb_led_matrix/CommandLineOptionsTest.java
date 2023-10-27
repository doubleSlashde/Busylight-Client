package de.doubleslash.usb_led_matrix;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.paint.Color;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class CommandLineOptionsTest {
   @BeforeEach
   void resetSettings() {
      CommandLineOptions.reset();
   }

   @Test
   void shouldSetBrightnessValueWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--brightness", "60" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getBrightness()).isEqualTo(60);
   }

   @Test
   void shouldSetBrightnessValueWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-b", "60" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getBrightness()).isEqualTo(60);
   }

   @Test
   void shouldSetModeToManualWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--mode", "manual" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getMode()).isEqualTo("manual");
   }

   @Test
   void shouldSetModeToManualWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-m", "manual" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getMode()).isEqualTo("manual");
   }

   @Test
   void shouldSetColorModeToDarkWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--colorMode", "dark" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getColorMode()).isEqualTo("dark");
   }

   @Test
   void shouldSetColorModeToDarkWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-c", "dark" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getColorMode()).isEqualTo("dark");
   }

   @Test
   void shouldSetTimeoutWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--timeout", "30" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getTimeout()).isEqualTo(30);
   }

   @Test
   void shouldSetTimeoutWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-t", "30" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getTimeout()).isEqualTo(30);
   }

   @Test
   void shouldSetComPortWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--port", "COM7" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getCom()).isEqualTo("COM7");
   }

   @Test
   void shouldSetComPortWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-p", "COM7" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getCom()).isEqualTo("COM7");
   }

   @Test
   void shouldSetNumberOfLedsWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--numberOfLeds", "5" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getNumberOfLeds()).isEqualTo(5);
   }

   @Test
   void shouldSetNumberOfLedsWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-nr", "5" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getNumberOfLeds()).isEqualTo(5);
   }

   @Test
   void shouldNotSetColorModeToDarkWhenConfiguredWithInvalidValue() {
      String[] args = { "-c", "pink" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getColorMode()).isNotEqualTo("pink");
   }

   @Test
   void shouldSetComPortAndNumberOfLedsWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-nr", "5", "-p", "COM7" };
      CommandLineOptions.handleParameters(args);
      assertThat(CommandLineOptions.getNumberOfLeds()).isEqualTo(5);
      assertThat(CommandLineOptions.getCom()).isEqualTo("COM7");
   }

   @ParameterizedTest
   @EnumSource(AvailabilityStatus.class)
   public void setColorTest(AvailabilityStatus status) {
      status.setColor(Color.PINK);
      assertEquals(Color.PINK, status.getColor());
   }
}
