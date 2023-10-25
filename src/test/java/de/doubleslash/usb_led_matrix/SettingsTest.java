package de.doubleslash.usb_led_matrix;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javafx.scene.paint.Color;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class SettingsTest {
   @BeforeEach
   void resetSettings() {
      Settings.reset();
   }

   @Test
   void shouldSetBrightnessValueWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--brightness", "60" };
      Settings.handleParameters(args);
      assertThat(Settings.getBrightness()).isEqualTo(60);
   }

   @Test
   void shouldSetBrightnessValueWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-b", "60" };
      Settings.handleParameters(args);
      assertThat(Settings.getBrightness()).isEqualTo(60);
   }

   @Test
   void shouldSetModeToManualWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--mode", "manual" };
      Settings.handleParameters(args);
      assertThat(Settings.getMode()).isEqualTo("manual");
   }

   @Test
   void shouldSetModeToManualWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-m", "manual" };
      Settings.handleParameters(args);
      assertThat(Settings.getMode()).isEqualTo("manual");
   }

   @Test
   void shouldSetColorModeToDarkWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--colorMode", "dark" };
      Settings.handleParameters(args);
      assertThat(Settings.getColorMode()).isEqualTo("dark");
   }

   @Test
   void shouldSetColorModeToDarkWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-c", "dark" };
      Settings.handleParameters(args);
      assertThat(Settings.getColorMode()).isEqualTo("dark");
   }

   @Test
   void shouldSetTimeoutWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--timeout", "30" };
      Settings.handleParameters(args);
      assertThat(Settings.getTimeout()).isEqualTo(30);
   }

   @Test
   void shouldSetTimeoutWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-t", "30" };
      Settings.handleParameters(args);
      assertThat(Settings.getTimeout()).isEqualTo(30);
   }

   @Test
   void shouldSetComPortWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--port", "COM7" };
      Settings.handleParameters(args);
      assertThat(Settings.getCom()).isEqualTo("COM7");
   }

   @Test
   void shouldSetComPortWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-p", "COM7" };
      Settings.handleParameters(args);
      assertThat(Settings.getCom()).isEqualTo("COM7");
   }

   @Test
   void shouldSetNumberOfLedsWhenConfiguredWithLongCommandLineOption() {
      String[] args = { "--numberOfLeds", "5" };
      Settings.handleParameters(args);
      assertThat(Settings.getNumberOfLeds()).isEqualTo(5);
   }

   @Test
   void shouldSetNumberOfLedsWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-nr", "5" };
      Settings.handleParameters(args);
      assertThat(Settings.getNumberOfLeds()).isEqualTo(5);
   }

   @Test
   void shouldNotSetColorModeToDarkWhenConfiguredWithInvalidValue() {
      String[] args = { "-c", "pink" };
      Settings.handleParameters(args);
      assertThat(Settings.getColorMode()).isNotEqualTo("pink");
   }

   @Test
   void shouldSetComPortAndNumberOfLedsWhenConfiguredWithShortCommandLineOption() {
      String[] args = { "-nr", "5", "-p", "COM7" };
      Settings.handleParameters(args);
      assertThat(Settings.getNumberOfLeds()).isEqualTo(5);
      assertThat(Settings.getCom()).isEqualTo("COM7");
   }

   @ParameterizedTest
   @EnumSource(AvailabilityStatus.class)
   public void setColorTest(AvailabilityStatus status) {
      status.setColor(Color.PINK);
      assertEquals(Color.PINK, status.getColor());
   }
}
