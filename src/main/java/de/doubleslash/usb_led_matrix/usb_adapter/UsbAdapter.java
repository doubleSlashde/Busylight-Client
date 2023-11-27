package de.doubleslash.usb_led_matrix.usb_adapter;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.doubleslash.usb_led_matrix.CommandLineOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

public class UsbAdapter implements SerialPortEventListener {
   byte numberLEDS = (byte) CommandLineOptions.getNumberOfLeds();
   private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

   private final SimpleBooleanProperty connected = new SimpleBooleanProperty();
   private final SimpleStringProperty microcontrollerVersion = new SimpleStringProperty();

   private final List<Runnable> actionsQueue = new ArrayList<>();

   private final SimpleBooleanProperty ready = new SimpleBooleanProperty(false);

   private String portName;

   private SerialPort serialPort;

   private byte expectedAnswer;

   private int stillConnectedCounter = 0;

   private Color pixelColor = Color.BLACK;

   private byte pixelBrightness;

   public boolean connectionBoolean = false;

   public UsbAdapter() {
      this(new SerialPort(null));
   }

   public UsbAdapter(final SerialPort serialPort) {
      this.serialPort = serialPort;
      ready.addListener((observable, oldValue, newValue) -> {
         if (Boolean.FALSE.equals(oldValue) && Boolean.TRUE.equals(newValue)) {
            final Thread thread = new Thread(this::executeNextAction);
            thread.setName("UsbAdapterActionQueue Thread");
            thread.start();
         }
      });
      ready.setValue(true);
   }

   @Override
   public void serialEvent(final SerialPortEvent serialPortEvent) {
      if (serialPortEvent.isRXCHAR()) {
         LOG.debug("Data is available.");
         try {
            handleIncomingData();
         } catch (final SerialPortException e) {
            LOG.error("Could not handle incoming data.", e);
         }
      }
   }

   public void connect() {
      try {
         LOG.debug("Connecting to device at port '{}'.", portName);
         closePort();
         createSerialPort();
         LOG.trace("Write byte '{}' to port '{}' expecting '{}' as answer.", ControlCodes.REQUEST_CONNECTION, portName,
               ControlCodes.ANSWER_CONNECTION);
         expectedAnswer = ControlCodes.ANSWER_CONNECTION;
         serialPort.writeByte(ControlCodes.REQUEST_CONNECTION);
      } catch (final SerialPortException e) {
         LOG.error("Could not connect to device.", e);
      }
   }

   public void updatePixel(final Color color) {
      addAction(() -> {
         try {
            ready.setValue(false);
            pixelColor = color;
            expectedAnswer = ControlCodes.READY_FOR_IMAGE_CHANGE;
            serialPort.writeByte(ControlCodes.REQUEST_IMAGE_CHANGE);
         } catch (final SerialPortException e) {
            LOG.error("Could not update pixel.", e);
            ready.setValue(true);
         }
      });
   }

   public void updatePixel(final byte brightness) {
      addAction(() -> {
         try {
            ready.setValue(false);
            pixelBrightness = brightness;
            expectedAnswer = ControlCodes.READY_FOR_IMAGE_CHANGE;
            serialPort.writeByte(ControlCodes.REQUEST_IMAGE_CHANGE);
         } catch (final SerialPortException e) {
            LOG.error("Could not update pixel.", e);
            ready.setValue(true);
         }
      });
   }

   public void updatePixel(final Color color, final byte brightness) {
      addAction(() -> {
         ready.setValue(false);
         pixelColor = color;
         pixelBrightness = brightness;
         expectedAnswer = ControlCodes.READY_FOR_IMAGE_CHANGE;
         try {
            serialPort.writeByte(ControlCodes.REQUEST_IMAGE_CHANGE);
         } catch (final SerialPortException e) {
            LOG.error("Could not update pixel.", e);
            ready.setValue(true);
         }
      });
   }

   public void updateConnectionStatus() {
      addAction(() -> {
         try {
            ready.setValue(false);
            expectedAnswer = ControlCodes.ANSWER_CONNECTION_CHECK;
            serialPort.writeByte(ControlCodes.CONNECTION_CHECK);
         } catch (final SerialPortException e) {
            LOG.debug("Could not update Connection Status.", e);
            ready.setValue(true);
         }
      });
   }

   public void requestVersion() throws SerialPortException {
      addAction(() -> {
         ready.setValue(false);

         expectedAnswer = ControlCodes.ANSWER_VERSION;
         try {
            serialPort.writeByte(ControlCodes.REQUEST_VERSION);
         } catch (final SerialPortException e) {
            LOG.error("Could not update Version Status.", e);
            ready.setValue(true);
         }
      });
   }

   public void closePort() {
      if (serialPort == null || !serialPort.isOpened()) {
         return;
      }

      LOG.debug("Closing port '{}'.", serialPort.getPortName());
      try {
         serialPort.closePort();
      } catch (final SerialPortException e) {
         LOG.info("failed to close Port '{}':\n{}", serialPort.getPortName(), e);
      }
   }

   private void addAction(final Runnable action) {
      actionsQueue.add(action);
   }

   public final SimpleBooleanProperty connectedProperty() {
      return this.connected;
   }

   public final SimpleStringProperty versionProperty() {
      return this.microcontrollerVersion;
   }

   public void connectionCheck() {
      stillConnectedCounter = stillConnectedCounter + 1;
      if (stillConnectedCounter > 2) {
         Platform.runLater(() -> connected.setValue(false));
         LOG.error("Lost connection to device.");
         stillConnectedCounter = 0;
      }
   }

   public void setPortName(final String portName) {
      this.portName = portName;
   }

   public static List<String> getSerialPortNames() {
      return Arrays.asList(SerialPortList.getPortNames());
   }

   private void executeNextAction() {
      waitForActions();
      LOG.debug("Start execution of action.");
      final Runnable action = actionsQueue.get(0);
      action.run();
      actionsQueue.remove(action);
   }

   private void waitForActions() {
      while (actionsQueue.isEmpty()) {
         try {
            Thread.sleep(500);
         } catch (final InterruptedException e) {
            LOG.error("Could not sleep thread.", e);
            Thread.currentThread().interrupt();
         }
      }
   }

   private void createSerialPort() {
      try {
         LOG.debug("Creating new serialPort.");
         serialPort = new SerialPort(portName);
         serialPort.openPort();
         serialPort.setParams(115200, 8, 1, 0);
         serialPort.readBytes();
         serialPort.addEventListener(this);
      } catch (final SerialPortException e) {
         LOG.warn("Could not open Port '{}':\n{}", portName, e);
      }
   }

   private byte getColorValueAsByte(final double colorValue) {
      return (byte) (colorValue * 255);
   }

   private void handleImageTransmission(final byte red, final byte green, final byte blue, final byte brightness,
         final byte numbersOfLEDs) throws SerialPortException {
      final int numberOfIndexes = numbersOfLEDs * 3 + 2;
      final byte[] data = new byte[numberOfIndexes];
      data[0] = brightness;
      data[1] = numbersOfLEDs;

      for (int i = 2; i < numberOfIndexes; i++) {
         data[i++] = red;
         data[i++] = green;
         data[i] = blue;
      }
      serialPort.writeBytes(data);
   }

   private void handleIncomingData() throws SerialPortException {
      final byte input = this.serialPort.readBytes(1)[0];
      switch (input) {
      case ControlCodes.ANSWER_CONNECTION_CHECK:
         stillConnectedCounter = 0;
         ready.set(true);
         break;
      case ControlCodes.ANSWER_CONNECTION:
         LOG.info("Connection request was answered.");
         handleAnswerConnection();
         connectionBoolean = true;
         ready.setValue(true);
         break;
      case ControlCodes.READY_FOR_IMAGE_CHANGE:
         LOG.info("Device is ready for image change.");
         handleReadyForImageChange();
         break;
      case ControlCodes.SUCCESSFULLY_TRANSMITTED_IMAGE:
         LOG.info("Successfully transmitted image.");
         ready.setValue(true);
         stillConnectedCounter = 0;
         break;
      case ControlCodes.ANSWER_VERSION:
         handleAnswerVersion();
         ready.set(true);
         break;
      case ControlCodes.REQUEST_CONNECTION:
      case ControlCodes.REQUEST_IMAGE_CHANGE:
         LOG.warn("Got unexpected control code: '{}'", input);
         break;
      default:
         LOG.warn("Got unknown control code: '{}'", input);
         break;
      }
   }

   private void handleAnswerVersion() throws SerialPortException {
      if (expectedAnswer != ControlCodes.ANSWER_VERSION) {
         LOG.warn("Was not expecting 'ANSWER_VERSION'.");
      }

      final List<Byte> versionByte = new ArrayList<>();
      byte input;
      do {
         input = this.serialPort.readBytes(1)[0];
         versionByte.add(input);
      } while (input != 0);
      final byte[] bytes = new byte[versionByte.size()];
      for (int i = 0; i < versionByte.size(); i++) {
         bytes[i] = versionByte.get(i);
      }

      Platform.runLater(() -> microcontrollerVersion.set(new String(bytes)));
   }

   private void handleAnswerConnection() {
      if (expectedAnswer != ControlCodes.ANSWER_CONNECTION) {
         LOG.warn("Was not expecting 'ANSWER_CONNECTION'.");
      }
      LOG.info("Connected!");
      Platform.runLater(() -> connected.setValue(true));
   }

   private void handleReadyForImageChange() {
      if (expectedAnswer != ControlCodes.READY_FOR_IMAGE_CHANGE) {
         LOG.warn("Was not expecting 'READY_FOR_IMAGE_CHANGE'.");
      }

      LOG.debug("Update to color '{}'.", pixelColor);
      final byte red = getColorValueAsByte(pixelColor.getRed());
      final byte green = getColorValueAsByte(pixelColor.getGreen());
      final byte blue = getColorValueAsByte(pixelColor.getBlue());
      final byte brightness = pixelBrightness;
      try {
         handleImageTransmission(red, green, blue, brightness, numberLEDS);
      } catch (final SerialPortException e) {
         LOG.error("Could not transmit image.", e);
      }
   }
}
