package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.CommandLineOptions;
import de.doubleslash.usb_led_matrix.model.Model;
import de.doubleslash.usb_led_matrix.resources.Resources;
import de.doubleslash.usb_led_matrix.usb_adapter.UsbAdapter;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(ApplicationExtension.class)
@ExtendWith(MockitoExtension.class)
class ConfigurationViewTest {
   private ConfigurationView configurationView;
   private Model model;

   private final File refreshFile = new File("refreshToken.txt");

   @Mock
   private UsbAdapter usbAdapter;

   @Start
   void start(final Stage primaryStage) throws IOException {
      final FileWriter fw = new FileWriter(refreshFile);
      final Properties p = new Properties();
      p.setProperty("refresh_token", "refresh_token");
      p.store(fw, "Sample comments");

      final FXMLLoader fxmlLoader = new FXMLLoader(Resources.CONFIGURATION_VIEW.getResource());
      final Parent root = fxmlLoader.load();
      configurationView = fxmlLoader.getController();
      model = new Model();
      configurationView.setModel(model);
      configurationView.setScene(new Scene(root));
   }

   @BeforeEach
   void setup() {
      lenient().doNothing().when(usbAdapter).connectionCheck();
      configurationView.dataInitialization(usbAdapter);
   }

   @BeforeAll
   static void setupTestFX() {
      System.setProperty("testfx.robot", "glass");
      System.setProperty("glass.platform", "Monocle");
      System.setProperty("monocle.platform", "Headless");
      System.setProperty("testfx.headless", "true");
   }

   @AfterEach
   void tearDown() {
      refreshFile.delete();
   }

   @Test
   void shouldTurnOffWhenCurrentTimeExceedsTimeout() {
      // Given
      final LocalDateTime currentTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0));
      configurationView.setUsbAdapter(usbAdapter);
      CommandLineOptions.setTimeoutInMinutes(60);
      configurationView.timeLastColorChange = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0));

      // When
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);

      // Then
      verify(usbAdapter).updatePixel(Color.BLACK);
   }

   @Test
   void shouldOnlyTurnOffOnceWhenCurrentTimeExceedsTheTimeoutTwice() {
      // Given
      final LocalDateTime currentTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(22, 0));
      configurationView.setUsbAdapter(usbAdapter);
      CommandLineOptions.setTimeoutInMinutes(60);
      configurationView.timeLastColorChange = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0));

      // When
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);

      // Then

      verify(usbAdapter, times(1)).updatePixel(Color.BLACK);

   }

   @Test
   void shouldResetTimeoutWhenColorChanges() {
      // Given
      final LocalDateTime currentTime = LocalDateTime.now();
      final LocalDateTime timeLastColorChange = currentTime.minusHours(2);
      configurationView.setUsbAdapter(usbAdapter);
      CommandLineOptions.setTimeoutInMinutes(60);
      configurationView.timeLastColorChange = timeLastColorChange;
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);
      verify(usbAdapter, times(1)).updatePixel(Color.BLACK);

      // When
      model.setColor(model.colorProperty().getValue().invert());
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);

      // Then
      verify(usbAdapter, times(1)).updatePixel(Color.BLACK);
   }

   @Test
   void shouldTurnOffWhenCurrentTimeEqualsTimeout() {
      // Given
      final LocalDateTime currentTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(21, 0));
      configurationView.setUsbAdapter(usbAdapter);
      CommandLineOptions.setTimeoutInMinutes(60);
      configurationView.timeLastColorChange = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0));
      // When
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);
      // Then
      verify(usbAdapter).updatePixel(Color.BLACK);
   }

   @Test
   void shouldNotTurnOffWhenCurrentTimeDoesNotReachTimeout() {
      // Given
      final LocalDateTime currentTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 30));
      configurationView.setUsbAdapter(usbAdapter);
      CommandLineOptions.setTimeoutInMinutes(60);
      configurationView.timeLastColorChange = LocalDateTime.of(LocalDate.now(), LocalTime.of(20, 0));

      // When
      configurationView.turnOffAutomaticallyIfNeeded(currentTime);

      // Then
      verify(usbAdapter, never()).updatePixel(Color.BLACK);
   }

   @Test
   void shouldSwitchOffWhenTheCurrentTimeExceedsTheTimeoutAndANewDayHasStarted() {
      // Given
      final LocalDateTime timeLastColorChange = LocalDateTime.of(LocalDate.of(2024, 12, 23),
            LocalTime.of(23, 42, 39, 91));
      final LocalDateTime currentDateTimeNextDay = timeLastColorChange.plusMinutes(61);

      configurationView.setUsbAdapter(usbAdapter);
      CommandLineOptions.setTimeoutInMinutes(60);
      configurationView.timeLastColorChange = timeLastColorChange;
      // When
      configurationView.turnOffAutomaticallyIfNeeded(currentDateTimeNextDay);

      // Then
      verify(usbAdapter).updatePixel(Color.BLACK);
   }

   @Test
   void shouldSetPortChoiceBoxValueWhenCommandLineOptionPortIsSet() {
      // Given
      final String expectedPort = "COM7";
      String[] args = { "-p", expectedPort };
      model.getSerialPorts().setAll("COM1", expectedPort, "COM12");
      // When
      CommandLineOptions.handleParameters(args);
      configurationView.initializePortChoiceBox();
      // Then
      assertThat(configurationView.portChoiceBox.getValue()).isEqualTo(expectedPort);
   }

   @Test
   void shouldGetBrightnessValueWhenCommandLineOptionIsSet() {
      String[] args = { "--brightness", "100" };
      CommandLineOptions.handleParameters(args);
      configurationView.dataInitialization(usbAdapter);
      assertThat(model.getBrightness()).isEqualTo((byte) 255);
   }

   @Test
   void shouldSelectManualRadioButtonWhenCommandLineOptionIsSet() {
      String[] args = { "--mode", "manual" };
      CommandLineOptions.handleParameters(args);
      configurationView.dataInitialization(usbAdapter);
      assertThat(configurationView.manualRadioButton.isSelected()).isEqualTo(true);
   }

   @Test
   void shouldSelectTeamsRadioButtonWhenCommandLineOptionIsSet() {
      String[] args = { "--mode", "teams" };
      CommandLineOptions.handleParameters(args);
      configurationView.dataInitialization(usbAdapter);
      assertThat(configurationView.teamsRadioButton.isSelected()).isEqualTo(true);
   }
}
