package de.doubleslash.usb_led_matrix.graph;

import com.google.gson.Gson;
import de.doubleslash.usb_led_matrix.json_objects.AvailabilityAnswer;
import de.doubleslash.usb_led_matrix.json_objects.DeviceCodeAnswer;
import de.doubleslash.usb_led_matrix.json_objects.TokenAnswer;
import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import de.doubleslash.usb_led_matrix.model.Model;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Graph {
   private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
   private final String SCOPE = "https://graph.microsoft.com/User.Read Presence.Read offline_access";

   private final File refreshFile = new File("refreshToken.txt");
   private final Model model = new Model();
   private static final Properties properties = new Properties();
   private final String REFRESH_TOKEN_PROPERTY = "refresh_token";
   private final String TENANT_ID_PROPERTY = "tenant_id";
   private final String CLIENT_ID_PROPERTY = "client_id";

   private static String tokenURI;
   private String accessToken;
   private HttpClient client = HttpClient.newHttpClient();

   public Graph() {
      try {
         final InputStream input = new FileInputStream(refreshFile);
         properties.load(input);
         final String tenantID = properties.getProperty(TENANT_ID_PROPERTY);
         if (tenantID != null) {
            tokenURI = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/token", tenantID);
         }
      } catch (IOException e) {
         LOG.error("Could not load properties from file '{}'", refreshFile.getAbsolutePath(), e);
      }
   }

   public Graph(final HttpClient client) {
      this();
      this.client = client;
   }

   public String pollingForToken(final String deviceCode) throws IOException, InterruptedException {
      if (tokenURI == null)
         return "authorization_pending";

      final String GRANT_TYPE_DEVICE_CODE = "urn:ietf:params:oauth:grant-type:device_code";
      final Map<Object, Object> bodyValue = new HashMap<>();
      bodyValue.put("grant_type", GRANT_TYPE_DEVICE_CODE);
      bodyValue.put("client_id", properties.getProperty(CLIENT_ID_PROPERTY));
      bodyValue.put("device_code", deviceCode);

      final HttpRequest request = HttpRequest.newBuilder()
                                             .POST(ofFormData(bodyValue))
                                             .uri(URI.create(tokenURI))
                                             .setHeader("User-Agent", "Java 11 HttpClient Bot")
                                             .header("Content-Type", "application/x-www-form-urlencoded")
                                             .build();

      final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      return response.body();
   }

   private DeviceCodeAnswer extractCode(final String answerBody) {
      final Gson gsonDeviceCode = new Gson();

      return gsonDeviceCode.fromJson(answerBody, DeviceCodeAnswer.class);
   }

   public void extractAndStoreAccessRefreshToken(final String answerBody) throws IOException {
      final Gson gsonToken = new Gson();
      final TokenAnswer token = gsonToken.fromJson(answerBody, TokenAnswer.class);
      storeToken(token.getRefresh_token());
      accessToken = token.getAccess_token();
   }

   private String extractStatus(final String status) {
      final Gson gsonStatus = new Gson();
      final AvailabilityAnswer presence = gsonStatus.fromJson(status, AvailabilityAnswer.class);
      return presence.getAvailability();
   }

   private void storeToken(final String refreshToken) throws IOException {
      try (OutputStream output = new FileOutputStream(refreshFile)) {
         properties.setProperty(REFRESH_TOKEN_PROPERTY, refreshToken);
         properties.store(output, null);
      }
   }

   public DeviceCodeAnswer deviceCodeFlow() throws IOException, InterruptedException {
      final String uri = String.format("https://login.microsoftonline.com/%s/oauth2/v2.0/devicecode",
            properties.getProperty(TENANT_ID_PROPERTY));
      final Map<Object, Object> bodyValues = new HashMap<>();
      bodyValues.put("client_id", properties.getProperty(CLIENT_ID_PROPERTY));
      bodyValues.put("scope", SCOPE);

      final HttpRequest request = HttpRequest.newBuilder()
                                             .POST(ofFormData(bodyValues))
                                             .uri(URI.create(uri))
                                             .setHeader("User-Agent", "Java 11 HttpClient Bot")
                                             .header("Content-Type", "application/x-www-form-urlencoded")
                                             .build();

      final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      return extractCode(response.body());
   }

   private HttpRequest.BodyPublisher ofFormData(final Map<Object, Object> data) {
      final var builder = new StringBuilder();
      for (final Map.Entry<Object, Object> entry : data.entrySet()) {
         if (builder.length() > 0) {
            builder.append("&");
         }
         builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
         builder.append("=");
         final Object value = entry.getValue();
         builder.append(URLEncoder.encode(value != null ? value.toString() : "", StandardCharsets.UTF_8));
      }

      return HttpRequest.BodyPublishers.ofString(builder.toString());
   }

   private void getNewAccessToken() throws IOException, InterruptedException {
      final InputStream input = new FileInputStream(refreshFile);
      properties.load(input);

      final Map<Object, Object> values = new HashMap<>();

      final String REFRESH_GRANT_TYPE = "refresh_token";
      values.put("grant_type", REFRESH_GRANT_TYPE);
      values.put("client_id", properties.getProperty("client_id"));
      values.put("scope", SCOPE);
      values.put("refresh_token", properties.getProperty(REFRESH_TOKEN_PROPERTY));

      final HttpRequest request = HttpRequest.newBuilder()
                                             .POST(ofFormData(values))
                                             .uri(URI.create(tokenURI))
                                             .setHeader("User-Agent", "Java 11 HttpClient Bot")
                                             .header("Content-Type", "application/x-www-form-urlencoded")
                                             .build();

      final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != HttpURLConnection.HTTP_OK) {
         LOG.error("Could not acquire access token. Response code was not 200. Response was '{}'. Body: '{}'.",
               response, response.body());
         // TODO add handling for this case
      }

      extractAndStoreAccessRefreshToken(response.body());
   }

   public boolean isRefreshTokenAvailable() {
      if (properties.containsKey(REFRESH_TOKEN_PROPERTY)) {
         return true;
      } else {
         return false;
      }
   }

   public String getTeamsStatus() throws IOException, InterruptedException {
      final HttpRequest request = HttpRequest.newBuilder()
                                             .uri(URI.create("https://graph.microsoft.com/v1.0/me/presence"))
                                             .header("Authorization", "Bearer " + accessToken)
                                             .header("Accept", "application/json")
                                             .build();

      final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      if (response.body().contains("InvalidAuthenticationToken")) {
         LOG.info("Access token expired, get new access Token");
         getNewAccessToken();
         final String currentTeamsStatus = getTeamsStatus();
         return currentTeamsStatus;
      }
      return extractStatus(response.body());
   }

   public Color getStatusColorFromTeams() throws IOException, InterruptedException {
      String teamsStatus = getTeamsStatus();
      AvailabilityStatus availabilityStatus = null;

      if (teamsStatus == null) {
         teamsStatus = "Offline";
      }
      model.setPresence(teamsStatus);
      try {
         availabilityStatus = AvailabilityStatus.valueOf(teamsStatus);
         if (availabilityStatus == AvailabilityStatus.PresenceUnknown) {
            throw new IllegalArgumentException("");
         }
      } catch (final IllegalArgumentException e) {
         LOG.warn("Unknown availability '{}'.", teamsStatus, e);
      }
      return availabilityStatus.getColor();
   }
}
