package de.doubleslash.usb_led_matrix.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Co2View {

   @FXML
   private Label co2Value;

   @FXML
   private Label feuchtigkeitValue;

   @FXML
   private Label temperaturValue;

   private Timeline timeline;

   public void initialize() {
      timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> updateValue()));
      timeline.setCycleCount(Timeline.INDEFINITE);
      timeline.play();
      updateValue();
   }

   public void updateValue() {
      String[] urls = { "http://172.19.70.169/temperatur", "http://172.19.70.169/feuchtigkeit",
            "http://172.19.70.169/co2"
      };

      for (String url : urls) {
         try {
            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
               BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
               String response = reader.readLine();
               reader.close();

               if (url.contains("co2")) {
                  co2Value.setText(response);
               } else if (url.contains("feuchtigkeit")) {
                  feuchtigkeitValue.setText(response);
               } else if (url.contains("temperatur")) {
                  temperaturValue.setText(response);
               }
            } else {
               System.out.println("HTTP error response code: " + responseCode + " for URL: " + url);
            }

            connection.disconnect();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }
}
