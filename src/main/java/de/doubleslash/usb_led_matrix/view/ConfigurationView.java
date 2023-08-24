package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.Settings;
import de.doubleslash.usb_led_matrix.graph.Graph;
import de.doubleslash.usb_led_matrix.model.Model;
import de.doubleslash.usb_led_matrix.resources.Resources;
import de.doubleslash.usb_led_matrix.usb_adapter.UsbAdapter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jssc.SerialPortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConfigurationView {
   private static final int POLLING_INTERVALL_SECONDS = 10;

   private static final Logger LOG = LoggerFactory.getLogger(ConfigurationView.class);

   LocalTime timeLightsTurnedOffNow = LocalTime.now();

   private final Graph graph = new Graph();

   private final Stage popUpStage = new Stage();

   private final ToggleGroup radioButtonGroup = new ToggleGroup();

   private UsbAdapter usbAdapter;

   private Model model;

   private Scene scene;

   private Thread currentlyRunningThread;

   private Thread healthCheckThread;

   private boolean startAfterAuthentification;

   private boolean timedOut = false;
   final Image image = new Image("/images/DsIcon.png");

   @FXML
   RadioButton manualRadioButton;

   @FXML
   RadioButton teamsRadioButton;

   @FXML
   ChoiceBox<String> portChoiceBox;

   @FXML
   private ColorPicker colorPicker;

   @FXML
   private Button setColorButton;

   @FXML
   private Label connectionStatusLabel;

   @FXML
   private Slider brightnessSlider;

   private final Runnable manualModeConnectionCheckRunnable = () -> {
      LOG.info("Manual mode started.");
      while (true) {
         try {
            Thread.sleep(POLLING_INTERVALL_SECONDS * 1000);
         } catch (final InterruptedException e) {
            LOG.debug("Manual mode was interrupted.", e);
            return;
         }
      }
   };

   private final Runnable teamsPollingRunnable = () -> {
      LOG.info("MS Teams status polling started.");
      while (true) {
         try {
            try {
               model.setColor(graph.getStatusColorFromTeams());
            } catch (final IOException ioe) {
               LOG.error("Microsoft server connection lost.", ioe);
            }
            Thread.sleep(POLLING_INTERVALL_SECONDS * 1000);
         } catch (final InterruptedException ie) {
            LOG.debug("MS Teams status polling was interrupted.", ie);
            return;
         }
      }
   };

   private final Runnable healthCheckRunnable = () -> {
      while (true) {
         try {
            model.setCheckConnection(false);
            usbAdapter.connectionCheck();
            Thread.sleep(POLLING_INTERVALL_SECONDS * 1000);
         } catch (final InterruptedException e) {
            LOG.debug("Connection check was interrupted.", e);
            return;
         }
         turnOffAutomaticallyIfNeeded(LocalTime.now());
      }
   };

   private final Runnable reconnectionCheckRunnable = () -> {
      while (true) {
         try {
            reconnect();
            Thread.sleep(POLLING_INTERVALL_SECONDS * 1000);
         } catch (final InterruptedException e) {
            LOG.debug("Reconnection check was interrupted.", e);
            return;
         }
         turnOffAutomaticallyIfNeeded(LocalTime.now());
      }
   };

   private final Task<Void> deviceCodePollingTask = new Task<>() {
      @Override
      protected Void call() {
         String answerBody = "authorization_pending";
         LOG.info("Start polling for token.");
         updateMessage("Polling for Token...");
         while (answerBody.contains("authorization_pending")) {
            try {
               answerBody = graph.pollingForToken(model.getDeviceCode());
               LOG.debug("Polling for token. Answer was: '{}'.", answerBody);
               Thread.sleep(POLLING_INTERVALL_SECONDS * 500);
            } catch (IOException | InterruptedException e) {
               LOG.error("Could not get answer from server.", e);
            }
         }
         if (answerBody.contains("access_token")) {
            LOG.info("Received token");
            updateMessage("Received token");
            try {
               graph.extractAndStoreAccessRefreshToken(answerBody);
            } catch (final IOException e) {
               LOG.error("Could not get access and refresh token", e);
            }
            LOG.info("Token was successfully saved");
            startNamedThreadWithRunnable(teamsPollingRunnable, "Teams");
         }
         return null;
      }
   };

   @FXML
   private void initialize() {
      LOG.debug("Initialize configuration view.");
      popUpStage.initModality(Modality.WINDOW_MODAL);

      connectionStatusLabel.setTextFill(Color.BLACK);
      teamsRadioButton.setToggleGroup(radioButtonGroup);
      manualRadioButton.setToggleGroup(radioButtonGroup);

      radioButtonGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
         LOG.trace("newValue is '{}'.", newValue);
         model.setSelectedToggle(newValue);

         if (newValue == manualRadioButton) {
            manualMode();
            return;
         }

         LOG.info("Teams mode activated");

         final boolean isRefreshTokenAvailable = graph.isRefreshTokenAvailable();

         model.setLoggedIntoTeams(isRefreshTokenAvailable);
         if (isRefreshTokenAvailable) {
            LOG.info("Refresh token available");
            teamsMode();
         }

         if (!model.isLoggedIntoTeams()) {
            LOG.info("Start Device Code Flow.");
            final Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("You need to log in.");
            final Optional<ButtonType> optionalButtonType = alert.showAndWait();

            if (optionalButtonType.isPresent()) {
               final ButtonType buttonType = optionalButtonType.get();
               if (buttonType.equals(ButtonType.CANCEL)) {
                  Platform.runLater(() -> radioButtonGroup.selectToggle(manualRadioButton));
               } else {
                  final FXMLLoader fxmlLoader = new FXMLLoader(Resources.AUTHENTICATION_VIEW.getResource());
                  try {
                     final Parent root = fxmlLoader.load();
                     final AuthenticationView authFlow = fxmlLoader.getController();
                     final Scene scene = new Scene(root);
                     authFlow.setScene(scene);
                     authFlow.customInitialize();
                     model.deviceCodeProperty().bind(authFlow.getDeviceCodeProperty());

                     deviceCodePollingTask.setOnFailed(
                           (event -> LOG.info("Polling task failed '{}'.", event, event.getSource().getException())));
                     deviceCodePollingTask.setOnSucceeded((event -> {
                        LOG.info("Polling task succeeded.");
                        popUpStage.close();
                     }));

                     new Thread(deviceCodePollingTask).start();
                     authFlow.pollingMessage.textProperty().bind(deviceCodePollingTask.messageProperty());
                     authFlow.pollingMessage.setTextFill(Color.web("#58c443"));

                     popUpStage.setResizable(false);
                     popUpStage.setScene(scene);
                     popUpStage.getIcons().add(image);
                     popUpStage.setTitle("Authentication");
                     popUpStage.showAndWait();
                     startAfterAuthentification = true;
                  } catch (IOException e) {
                     LOG.error("Could not load view '{}'. Exiting.", Resources.AUTHENTICATION_VIEW, e);
                  }
               }
            }
         }

         if (startAfterAuthentification && model.connectedProperty().getValue()) {
            brightnessSlider.setDisable(false);
            connectionStatusLabel.setText("Connected to device.");
            connectionStatusLabel.setTextFill(Color.GREEN);
            model.setColor(Color.BLACK);
            usbAdapter.updatePixel(model.brightnessProperty().getValue());
         }
      });
      initializeSetColorButton();
      initializeBrightness();
   }

   private void manualMode() {
      LOG.info("Manual mode activated");
      if (model.connectedProperty().getValue()) {
         colorPicker.setDisable(false);
         setColorButton.setDisable(false);
         brightnessSlider.setDisable(false);
         model.setColor(colorPicker.getValue());
         model.setBrightnessFromPercentage(brightnessSlider.getValue());
         usbAdapter.updatePixel(model.colorProperty().getValue(), model.brightnessProperty().getValue());
         startNamedThreadWithRunnable(manualModeConnectionCheckRunnable, "Manual");
      }
   }

   private void teamsMode() {
      LOG.info("teams mode activated");
      if (model.connectedProperty().getValue()) {
         startNamedThreadWithRunnable(teamsPollingRunnable, "Teams");
         colorPicker.setDisable(true);
         setColorButton.setDisable(true);
      }
   }

   private void initializeSetColorButton() {
      LOG.debug("Initialize setColorButton.");
      setColorButton.setOnAction(actionEvent -> model.setColor(colorPicker.getValue()));
   }

   void initializePortChoiceBox() {
      LOG.debug("Initialize portChoiceBox.");

      portChoiceBox.valueProperty().addListener((observable, oldValue, newValue) -> {
         LOG.debug("Port changed from '{}' to '{}'", oldValue, newValue);
         if (newValue != null) {
            model.setPortName(newValue);
         }
      });
      selectPortComboBoxItem(Settings.getCom());
   }

   private void selectPortComboBoxItem(final String portName) {
      final List<String> validPortNames = new ArrayList<>(model.getSerialPorts());
      if (validPortNames.contains(portName)) {
         LOG.warn("Changing port to '{}'.", portName);
         portChoiceBox.getSelectionModel().select(portName);
      } else {
         LOG.error("The port name '{}' is invalid.", portName);
      }
   }

   private void initializeBrightness() {
      LOG.debug("Initialize Brightness.");
      brightnessSlider.setOnMouseReleased(new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent event) {
            model.setBrightnessFromPercentage(brightnessSlider.getValue());
            LOG.debug("Brightness changed to '{}'", brightnessSlider.getValue());
         }
      });
   }

   public void setModel(final Model model) {
      this.model = model;
   }

   public void setScene(final Scene scene) {
      this.scene = scene;
   }

   public void dataInitialization(final UsbAdapter usbAdapter) {
      LOG.debug("Initialize data of configuration view.");
      model.colorProperty().addListener((observable, oldValue, newValue) -> {
         timeLightsTurnedOffNow = LocalTime.now();
         timedOut = false;
      });
      this.usbAdapter = usbAdapter;
      if (Settings.getColorMode().equals("light")) {
         Settings.Light(scene);
      } else if (Settings.getColorMode().equals("dark")) {
         Settings.Dark(scene);
      }
      portChoiceBox.setItems(model.getSerialPorts());
      initializePortChoiceBox();
      if (Settings.getMode().equals("manual")) {
         radioButtonGroup.selectToggle(manualRadioButton);
      } else if (Settings.getMode().equals("teams")) {
         radioButtonGroup.selectToggle(teamsRadioButton);
      }
      if (model.getSelectedToggle() == null) {
         LOG.trace("model.getSelectedToggle is '{}'.", model.getSelectedToggle());
         radioButtonGroup.selectToggle(manualRadioButton);
      } else {
         radioButtonGroup.selectToggle(model.getSelectedToggle());
      }
      model.setBrightnessFromPercentage(Settings.getBrightness());
      brightnessSlider.setValue(Settings.getBrightness());
      registerConnectedListener();
      healthCheckThread = new Thread(healthCheckRunnable);
      healthCheckThread.setName("Health");
      healthCheckThread.start();
   }

   private void registerConnectedListener() {
      LOG.debug("Initialize connectedProperty.");
      model.connectedProperty().addListener((observable, oldValue, newValue) -> {
         LOG.debug("connectedProperty changed from '{}' to '{}'.", oldValue, newValue);

         if (newValue == Boolean.TRUE) {
            handleSuccess();
         }
         if (newValue == Boolean.FALSE) {
            handleFailure();
         }
      });
   }

   private void handleSuccess() {
      LOG.debug("Setting connectionStatusLabel to success.");
      if (manualRadioButton.isSelected()) {
         colorPicker.setDisable(false);
         setColorButton.setDisable(false);
      }
      brightnessSlider.setDisable(false);
      connectionStatusLabel.setText("Connected to device.");
      if (manualRadioButton.isSelected()) {
         startNamedThreadWithRunnable(manualModeConnectionCheckRunnable, "Manual");
      } else {
         startNamedThreadWithRunnable(teamsPollingRunnable, "Teams");
      }

      model.setColor(Color.BLACK);
      if (usbAdapter.connectionBoolean == true) {
         setColorButton.fire();
         usbAdapter.connectionBoolean = false;
      }
      usbAdapter.updatePixel(model.colorProperty().getValue(), model.brightnessProperty().getValue());
      connectionStatusLabel.setTextFill(Color.GREEN);
      refreshButton();
   }

   private void handleFailure() {
      LOG.debug("Setting connectionStatusLabel to failure.");
      connectionStatusLabel.setTextFill(Color.RED);
      connectionStatusLabel.setText("Not connected to device.");
      currentlyRunningThread.interrupt();
      usbAdapter.closePort();
      startNamedThreadWithRunnable(reconnectionCheckRunnable, "reconnectionThread");
   }

   @FXML
   void informationImageButton(final MouseEvent event) throws SerialPortException {
      final FXMLLoader fxmlLoader = new FXMLLoader(Resources.INFO_VIEW.getResource());
      try {
         usbAdapter.requestVersion();
         final Parent root = fxmlLoader.load();
         final VersionView versionView = fxmlLoader.getController();
         final Scene scene = new Scene(root);
         versionView.setScene(scene);
         versionView.instantiate(usbAdapter.versionProperty());

         popUpStage.setScene(scene);
         popUpStage.getIcons().add(image);
         popUpStage.setTitle("Info");
         popUpStage.setMinWidth(328);
         popUpStage.setMinHeight(193);
         popUpStage.setMaxHeight(193);
         popUpStage.setMaxWidth(328);
         popUpStage.setResizable(false);
         popUpStage.showAndWait();
      } catch (final IOException e) {
         LOG.error("Could not load view '{}'. Exiting.", Resources.INFO_VIEW, e);
      }
   }

   private void startNamedThreadWithRunnable(final Runnable runnable, final String name) {
      if (currentlyRunningThread != null && currentlyRunningThread.isAlive()) {
         currentlyRunningThread.interrupt();
      }
      currentlyRunningThread = new Thread(runnable);
      currentlyRunningThread.setName(name);
      currentlyRunningThread.start();
   }

   void reconnect() {
      usbAdapter.connect();
      model.setColor(Color.BLACK);
   }

   @FXML
   void refreshButton() {
      final String currentPort = model.getPortName();
      model.getSerialPorts().setAll(UsbAdapter.getSerialPortNames());
      if (model.getSerialPorts().contains(currentPort)) {
         selectPortComboBoxItem(currentPort);
      } else if (model.getSerialPorts().contains(Settings.getCom())) {
         selectPortComboBoxItem(Settings.getCom());
      }
   }

   void turnOffAutomaticallyIfNeeded(final LocalTime currentTime) {
      final LocalTime timeLightsTurnedOff = timeLightsTurnedOffNow.plusMinutes(Settings.getTimeout());
      if (!currentTime.isBefore(timeLightsTurnedOff) && !timedOut) {
         LOG.info("Turn off automatically at '{}'", timeLightsTurnedOff);
         usbAdapter.updatePixel(Color.BLACK);
         timedOut = true;
      }
   }

   void setUsbAdapter(final UsbAdapter usbAdapter) {
      this.usbAdapter = usbAdapter;
   }

   @FXML
   void toggleLightMode(final MouseEvent event) {
      Settings.Light(scene);
   }

   @FXML
   void toggleDarkMode(final MouseEvent event) {
      Settings.Dark(scene);
   }
}
