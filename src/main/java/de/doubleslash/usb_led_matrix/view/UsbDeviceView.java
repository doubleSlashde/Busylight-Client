package de.doubleslash.usb_led_matrix.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.doubleslash.usb_led_matrix.model.Model;
import de.doubleslash.usb_led_matrix.usb_adapter.UsbAdapter;
import jssc.SerialPortException;

import java.lang.invoke.MethodHandles;

public class UsbDeviceView {
   private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

   private final Model model;

   private final UsbAdapter usbAdapter;

   public UsbDeviceView(final Model model, final UsbAdapter usbAdapter) {
      this.model = model;
      this.usbAdapter = usbAdapter;
   }

   public void initialize() {
      LOG.debug("Initialize device view.");
      registerPortNameListener();
      registerImageListener();
      registerConnectionListener();
      LOG.debug("Initializing connectedProperty.");
      model.connectedProperty().bind(usbAdapter.connectedProperty());
      model.versionProperty().bind(usbAdapter.versionProperty());
   }

   private void registerConnectionListener() {
      model.checkConnectionProperty().addListener((observable, oldValue, newValue) -> {
         final boolean connectionStatus = newValue;
         if (!connectionStatus) {
            usbAdapter.updateConnectionStatus();
            model.setCheckConnection(true);
         }
      });
   }

   private void registerPortNameListener() {
      LOG.debug("Initializing portNameProperty.");

      model.portNameProperty().addListener((observable, oldValue, newValue) -> {
         LOG.debug("PortName changed from '{}' to '{}'.", oldValue, newValue);
         try {
            handlePortChange(newValue);
         } catch (final SerialPortException e) {
            LOG.error("Could not change serial port (old: '{}', new: '{}').", oldValue, newValue, e);
            model.setConnected(false);
         }
      });
   }

   private void registerImageListener() {
      LOG.debug("Initializing image.");

      model.colorProperty().addListener((observable, oldValue, newValue) -> {
         usbAdapter.updatePixel(newValue);
      });
      model.brightnessProperty().addListener((observable, oldValue, newValue) -> {
         usbAdapter.updatePixel(newValue);
      });
   }

   private void handlePortChange(final String portName) throws SerialPortException {
      LOG.debug("Handling port change.");
      usbAdapter.setPortName(portName);
      usbAdapter.connect();
   }
}
