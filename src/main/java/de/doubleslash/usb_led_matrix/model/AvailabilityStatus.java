package de.doubleslash.usb_led_matrix.model;

import javafx.scene.paint.Color;

public enum AvailabilityStatus {
   // @formatter:off
   Available(Color.GREEN,"available"),
   AvailableIdle(Color.GREEN, Available.propertyKey),
   Away(Color.ORANGE,"away"),
   BeRightBack(Color.ORANGE,"beRightBack"),
   Busy(Color.RED,"busy"),
   BusyIdle(Color.RED,Busy.propertyKey),
   DoNotDisturb(Color.RED,"doNotDisturb"),
   Offline(Color.BLACK,null),
   PresenceUnknown(null,null);
   // @formatter:on

   private Color color;
   private String propertyKey;

   private AvailabilityStatus(final Color color, final String propertyKey) {
      this.color = color;
      this.propertyKey = propertyKey;
   }

   public void setColor(Color c) {
      this.color = c;
   }

   public String getPropertyKey() {
      return propertyKey;
   }

   public Color getColor() {
      return color;
   }
}
