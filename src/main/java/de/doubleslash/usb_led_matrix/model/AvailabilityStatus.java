package de.doubleslash.usb_led_matrix.model;

import javafx.scene.paint.Color;

public enum AvailabilityStatus {
   Available(Color.GREEN),
   AvailableIdle(Color.GREEN),
   Away(Color.ORANGE),
   BeRightBack(Color.ORANGE),
   Busy(Color.RED),
   BusyIdle(Color.RED),
   DoNotDisturb(Color.RED),
   Offline(Color.BLACK),
   PresenceUnknown(null);

   private Color color;

   private AvailabilityStatus(final Color color) {
      this.color = color;
   }

   public void setColor(Color c){
      this.color=c;
   }

   public Color getColor() {
      return color;
   }
}
