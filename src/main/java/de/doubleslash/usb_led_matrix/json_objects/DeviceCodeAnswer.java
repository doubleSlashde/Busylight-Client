package de.doubleslash.usb_led_matrix.json_objects;

public class DeviceCodeAnswer {
   private final String user_code;
   private final String device_code;
   private final String verification_uri;

   public DeviceCodeAnswer(final String user_code, final String device_code, final String verification_uri) {
      this.user_code = user_code;
      this.device_code = device_code;
      this.verification_uri = verification_uri;
   }

   public String getUser_code() {
      return user_code;
   }

   public String getDevice_code() {
      return device_code;
   }

   public String getVerification_uri() {
      return verification_uri;
   }
}
