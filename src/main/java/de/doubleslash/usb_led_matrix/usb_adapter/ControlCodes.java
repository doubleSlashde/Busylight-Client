package de.doubleslash.usb_led_matrix.usb_adapter;

public class ControlCodes {
   public static final byte REQUEST_CONNECTION = 0x00;
   public static final byte ANSWER_CONNECTION = 0x01;
   public static final byte REQUEST_IMAGE_CHANGE = 0x02;
   public static final byte READY_FOR_IMAGE_CHANGE = 0x03;
   public static final byte SUCCESSFULLY_TRANSMITTED_IMAGE = 0x04;
   public static final byte CONNECTION_CHECK = 0x05;
   public static final byte ANSWER_CONNECTION_CHECK = 0x06;
   public static final byte REQUEST_VERSION = 0x07;
   public static final byte ANSWER_VERSION = 0x08;

   private ControlCodes() {
   }
}
