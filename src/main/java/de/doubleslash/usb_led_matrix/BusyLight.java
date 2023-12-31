package de.doubleslash.usb_led_matrix;

public class BusyLight {
   public static void main(final String[] args) {
      final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();
      defaultExceptionHandler.register();

      Settings.loadSettings();
      CommandLineOptions.handleParameters(args);

      App.launchApplication(args);
   }
}
