package de.doubleslash.usb_led_matrix;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import de.doubleslash.usb_led_matrix.view.ConfigurationView;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class Settings {
   private static final Logger LOG = LoggerFactory.getLogger(ConfigurationView.class);

   public static void loadProperty() throws IOException {
      String filePath = "settings.properties";
      final File file = new File(filePath);
      if (file.exists()) {
         try (final FileReader fr = new FileReader(filePath)) {
            final Properties properties = new Properties();
            properties.load(fr);

            AvailabilityStatus.Available.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.Available.getPropertyKey())));
            AvailabilityStatus.AvailableIdle.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.AvailableIdle.getPropertyKey())));
            AvailabilityStatus.Away.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.Away.getPropertyKey())));
            AvailabilityStatus.BeRightBack.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.BeRightBack.getPropertyKey())));
            AvailabilityStatus.Busy.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.Busy.getPropertyKey())));
            AvailabilityStatus.BusyIdle.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.BusyIdle.getPropertyKey())));
            AvailabilityStatus.DoNotDisturb.setColor(Color.valueOf(properties.getProperty(AvailabilityStatus.DoNotDisturb.getPropertyKey())));

         } catch (FileNotFoundException e) {
            LOG.error("Settings Properties File was not Found",e);
         }
      }
   }
   public static void saveProperty(){

      try (final FileWriter fw = new FileWriter("settings.properties")) {
         final Properties p = new Properties();
         p.setProperty(AvailabilityStatus.Away.getPropertyKey(), String.valueOf(AvailabilityStatus.Away.getColor()));
         p.setProperty(AvailabilityStatus.DoNotDisturb.getPropertyKey(), String.valueOf(AvailabilityStatus.DoNotDisturb.getColor()));
         p.setProperty(AvailabilityStatus.Busy.getPropertyKey(), String.valueOf(AvailabilityStatus.Busy.getColor()));
         p.setProperty(AvailabilityStatus.Available.getPropertyKey(), String.valueOf(AvailabilityStatus.Available.getColor()));
         p.setProperty(AvailabilityStatus.BeRightBack.getPropertyKey(), String.valueOf(AvailabilityStatus.Available.getColor()));
         p.store(fw, "");
      } catch (IOException e) {
         LOG.error("Error while saving settings: ",e);
      }
   }
}
