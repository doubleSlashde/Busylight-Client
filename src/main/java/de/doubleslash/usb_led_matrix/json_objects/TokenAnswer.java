package de.doubleslash.usb_led_matrix.json_objects;

public class TokenAnswer {
   private final String access_token;
   private final String refresh_token;

   public TokenAnswer(final String access_token, final String refresh_token) {
      this.access_token = access_token;
      this.refresh_token = refresh_token;
   }

   public String getAccess_token() {
      return access_token;
   }

   public String getRefresh_token() {
      return refresh_token;
   }
}
