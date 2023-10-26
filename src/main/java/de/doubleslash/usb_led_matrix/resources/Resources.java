package de.doubleslash.usb_led_matrix.resources;

import java.io.InputStream;
import java.net.URL;

public enum Resources {
   CONFIGURATION_VIEW("/layouts/configuration.fxml"),
   AUTHENTICATION_VIEW("/layouts/authentication.fxml"),
   INFO_VIEW("/layouts/version.fxml"),
   SETTINGS_VIEW ("/layouts/settings.fxml");

   private final String location;

   private Resources(final String location) {
      this.location = location;
   }

   public URL getResource() {
      return Resources.class.getResource(location);
   }
}
