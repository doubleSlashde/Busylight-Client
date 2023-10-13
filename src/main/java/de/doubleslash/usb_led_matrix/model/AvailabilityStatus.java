package de.doubleslash.usb_led_matrix.model;

import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum AvailabilityStatus {
   Available(Color.GREEN),
   AvailableIdle(Color.GREEN),
   Away(Color.ORANGE),
   BeRightBack(Color.PINK),
   Busy(Color.RED),
   BusyIdle(Color.RED),
   DoNotDisturb(Color.DARKRED),
   Offline(Color.BLACK),
   PresenceUnknown(null);

   private Color color;

   private AvailabilityStatus(final Color color) {
 this.color = color;

   }

   public Color getColor() {

      return color;
   }

//   public Color getColorForAvailabilityStatus() {
//      AvailabilityStatus status = null;
//      if (status == AvailabilityStatus.Available) {
//         return Color.PINK; // Ersetzen Sie YOUR_DESIRED_COLOR durch die gewünschte Farbe für Offline
//      } else {
//         return status.getColor();
//      }
//   }



   public void getColorValueFromGUI() {
      final String fileName = "TeamsColor.properties";
      try {
         final InputStream input = new FileInputStream(new File(fileName));
         final Properties properties = new Properties();
         properties.load(input);
         final Color availableColor = Color.valueOf(properties.getProperty("available"));
         final Color awayColor = Color.valueOf(properties.getProperty("away"));
         final Color beRightBackColor = Color.valueOf(properties.getProperty("beRightBack"));
         final Color busyColor = Color.valueOf(properties.getProperty("busy"));
         final Color doNotDisturbColor = Color.valueOf(properties.getProperty("doNotDisturb"));

         System.out.println(availableColor);
         System.out.println(awayColor);
         System.out.println(beRightBackColor);
         System.out.println(busyColor);
         System.out.println(doNotDisturbColor);
      } catch (FileNotFoundException e) {
         throw new RuntimeException(e);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}
