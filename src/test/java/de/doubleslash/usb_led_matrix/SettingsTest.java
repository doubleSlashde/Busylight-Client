package de.doubleslash.usb_led_matrix;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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



   @Test
   public void GetColorAvailableTest() {
      AvailabilityStatus status = AvailabilityStatus.Available;
      assertEquals(Color.GREEN, status.getColor());
   }

   @Test
   public void GetColorAwayTest() {
      AvailabilityStatus status = AvailabilityStatus.Away;
      assertEquals(Color.ORANGE, status.getColor());
   }

   @Test
   public void GetColorBeRightBackTest() {
      AvailabilityStatus status = AvailabilityStatus.BeRightBack;
      assertEquals(Color.ORANGE, status.getColor());
   }

   @Test
   public void GetColorDoNotDisturbTest() {
      AvailabilityStatus status = AvailabilityStatus.DoNotDisturb;
      assertEquals(Color.RED, status.getColor());
   }
   @Test
   public void GetColorBusyTest() {
      AvailabilityStatus status = AvailabilityStatus.Busy;
      assertEquals(Color.RED, status.getColor());
   }

   @Test
   public void GetColorOfflineTest() {
      AvailabilityStatus status = AvailabilityStatus.Offline;
      assertEquals(Color.BLACK, status.getColor());
   }

   @ParameterizedTest
   @EnumSource(AvailabilityStatus.class)
   public void setColorTest(AvailabilityStatus status) {
      status.setColor(Color.PINK);
      assertEquals(Color.PINK, status.getColor());
   }


//   @Test
//   public void setColorTest() {
//      AvailabilityStatus status = AvailabilityStatus.Available;
//      status.setColor(Color.BLUE);
//      assertEquals(Color.BLUE, status.getColor());
//   }
//
//   @Test
//   public void setColorAwayTest() {
//      AvailabilityStatus status = AvailabilityStatus.Away;
//      status.setColor(Color.BLUE);
//      assertEquals(Color.BLUE, status.getColor());
//   }
//
//   @Test
//   public void setColorBeRightBackTest() {
//      AvailabilityStatus status = AvailabilityStatus.BeRightBack;
//      status.setColor(Color.BLUE);
//      assertEquals(Color.BLUE, status.getColor());
//   }
//
//   @Test
//   public void setColorBusyTest() {
//      AvailabilityStatus status = AvailabilityStatus.Busy;
//      status.setColor(Color.BLUE);
//      assertEquals(Color.BLUE, status.getColor());
//   }
//   @Test
//   public void setColorDoNotDisturbTest() {
//      AvailabilityStatus status = AvailabilityStatus.DoNotDisturb;
//      status.setColor(Color.BLUE);
//      assertEquals(Color.BLUE, status.getColor());
//   }
//


}
