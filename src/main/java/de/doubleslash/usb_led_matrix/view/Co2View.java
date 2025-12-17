package de.doubleslash.usb_led_matrix.view;

import de.doubleslash.usb_led_matrix.usb_adapter.UsbAdapter;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.control.TextField;

public class Co2View {

   @FXML
   private Label co2Value;

   @FXML
   private Label feuchtigkeitValue;

   @FXML
   private Label temperaturValue;

   private Timeline timeline;
   private UsbAdapter usbAdapter;

   @FXML
   private TextField IPEingabe;

   private static final String DEFAULT_BASE_URL = "http://example.com"; // Beispiel-URL als Fallback

   public void initialize() {
      timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> updateValue()));
      timeline.setCycleCount(Timeline.INDEFINITE);
      timeline.play();
   }

   @FXML
   void IPButtonSave() {
      updateValue();
   }

   public void updateValue() {
      String baseUrl = IPEingabe.getText().trim();
      if (baseUrl.isEmpty() || !baseUrl.startsWith("http://") && !baseUrl.startsWith("https://")) {
         baseUrl = DEFAULT_BASE_URL;
      }
      String[] endpoints = { "/temperatur", "/feuchtigkeit", "/co2" };

      for (String endpoint : endpoints) {
         try {
            URL apiUrl = new URL(baseUrl + endpoint);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String response = reader.readLine();
               reader.close();

               if (endpoint.contains("co2")) {
                  co2Value.setText(response);
               } else if (endpoint.contains("feuchtigkeit")) {
                  feuchtigkeitValue.setText(response);
               } else if (endpoint.contains("temperatur")) {
                  temperaturValue.setText(response);
               }
            } else {
               System.out.println("HTTP error response code: " + responseCode + " for URL: " + baseUrl + endpoint);
            }

            connection.disconnect();
         } catch (Exception e) {
            System.err.println("Error occurred while fetching data from URL: " + baseUrl + endpoint);
            e.printStackTrace();
         }
      }
   }
}
