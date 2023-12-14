package de.doubleslash.usb_led_matrix;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Properties;

public class Settings {
   private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
   public static final String filePath = "settings.properties";

   public static void loadSettings() {
      try (final FileReader fr = new FileReader(filePath)) {
         final Properties properties = new Properties();
         properties.load(fr);
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.Available,
               properties.getProperty(AvailabilityStatus.Available.getPropertyKey()));
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.AvailableIdle,
               properties.getProperty(AvailabilityStatus.AvailableIdle.getPropertyKey()));
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.Away,
               properties.getProperty(AvailabilityStatus.Away.getPropertyKey()));
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.BeRightBack,
               properties.getProperty(AvailabilityStatus.BeRightBack.getPropertyKey()));
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.Busy,
               properties.getProperty(AvailabilityStatus.Busy.getPropertyKey()));
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.BusyIdle,
               properties.getProperty(AvailabilityStatus.BusyIdle.getPropertyKey()));
         setColorOfAvailabilityStatusIfPropertyValueIsNotNull(AvailabilityStatus.DoNotDisturb,
               properties.getProperty(AvailabilityStatus.DoNotDisturb.getPropertyKey()));
      } catch (IOException e) {
         LOG.error("Properties could not be loaded from file '{}'", filePath, e);
      }
   }

   public static void saveSettings() {
      try (final FileWriter fw = new FileWriter(filePath)) {
         final Properties p = new Properties();
         p.setProperty(AvailabilityStatus.Available.getPropertyKey(),
               String.valueOf(AvailabilityStatus.Available.getColor()));
         p.setProperty(AvailabilityStatus.Away.getPropertyKey(), String.valueOf(AvailabilityStatus.Away.getColor()));
         p.setProperty(AvailabilityStatus.BeRightBack.getPropertyKey(),
               String.valueOf(AvailabilityStatus.BeRightBack.getColor()));
         p.setProperty(AvailabilityStatus.Busy.getPropertyKey(), String.valueOf(AvailabilityStatus.Busy.getColor()));
         p.setProperty(AvailabilityStatus.DoNotDisturb.getPropertyKey(),
               String.valueOf(AvailabilityStatus.DoNotDisturb.getColor()));
         p.store(fw, "");
      } catch (IOException e) {
         LOG.error("Error while saving settings: ", e);
      }
   }

   private static void setColorOfAvailabilityStatusIfPropertyValueIsNotNull(final AvailabilityStatus availabilityStatus,
         final String propertyValue) {
      if (propertyValue != null) {
         availabilityStatus.setColor(Color.valueOf(propertyValue));
      }
   }
}