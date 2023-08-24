package de.doubleslash.usb_led_matrix.json_objects;

public class AvailabilityAnswer {
   private final String availability;

   public AvailabilityAnswer(final String availability) {
      this.availability = availability;
   }

   public String getAvailability() {
      return availability;
   }
}
