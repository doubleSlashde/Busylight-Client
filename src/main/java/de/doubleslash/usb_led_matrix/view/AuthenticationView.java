package de.doubleslash.usb_led_matrix.view;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import de.doubleslash.usb_led_matrix.CommandLineOptions;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.doubleslash.usb_led_matrix.graph.Graph;
import de.doubleslash.usb_led_matrix.json_objects.DeviceCodeAnswer;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;

public class AuthenticationView {
   private static final Logger LOG = LoggerFactory.getLogger(AuthenticationView.class);

   private Scene scene;

   @FXML
   private TextField clientIDTextField;

   @FXML
   private Button copyButton;

   @FXML
   private Hyperlink hyperWebsite;

   @FXML
   private Label labelCode;

   @FXML
   private Label code;

   @FXML
   private Label link;

   @FXML
   Label pollingMessage;

   @FXML
   private Button setTeamsIDButton;

   @FXML
   private TextField tenantIDTextField;

   @FXML
   private Label header_Link_Webseite;

   private final SimpleStringProperty deviceCode = new SimpleStringProperty();

   public void customInitialize() {
      link.setVisible(false);
      hyperWebsite.setVisible(false);
      code.setVisible(false);
      labelCode.setVisible(false);
      copyButton.setVisible(false);
      pollingMessage.setVisible(false);
      header_Link_Webseite.setVisible(false);

      if (CommandLineOptions.getColorMode().equals("light")) {
         CommandLineOptions.Light(scene);
      } else if (CommandLineOptions.getColorMode().equals("dark")) {
         CommandLineOptions.Dark(scene);
      }
      final String fileName = "refreshToken.txt";
      try {
         final InputStream input = new FileInputStream(new File(fileName));
         final Properties properties = new Properties();
         properties.load(input);
         final String tenantID = properties.getProperty("tenant_id");
         final String clientID = properties.getProperty("client_id");
         if (tenantID != null && clientID != null) {
            tenantIDTextField.setText(tenantID);
            clientIDTextField.setText(clientID);
            setTeamsIDButton.fire();
         }
      } catch (IOException e) {
         LOG.debug("Could not read dile'" + fileName + "': ", e);
      }
   }

   public SimpleStringProperty getDeviceCodeProperty() {
      return deviceCode;
   }

   private void initializeHyperLink(final String verificationUrl) {
      hyperWebsite.setOnAction(ex -> {
         try {
            Desktop.getDesktop().browse(URI.create(verificationUrl));
         } catch (final IOException e) {
            LOG.error("There was an error when opening url.", e);
         }
      });
   }

   private void initializeCopyButton() {
      copyButton.setOnAction(actionEvent -> {
         final ClipboardContent clipboardContent = new ClipboardContent();
         clipboardContent.putString(labelCode.getText());

         final Clipboard clipboard = Clipboard.getSystemClipboard();
         clipboard.setContent(clipboardContent);
      });
   }

   @FXML
   void toggleLightMode(final MouseEvent event) {
      CommandLineOptions.Light(scene);
   }

   @FXML
   void toggleDarkMode(final MouseEvent event) {
      CommandLineOptions.Dark(scene);
   }

   public void setScene(final Scene scene) {
      this.scene = scene;
   }

   @FXML
   void setTeamsID(ActionEvent event) {
      final FileWriter fw;
      try {
         fw = new FileWriter("refreshToken.txt");
         final Properties p = new Properties();
         p.setProperty("tenant_id", tenantIDTextField.getText());
         p.setProperty("client_id", clientIDTextField.getText());
         p.store(fw, "Sample comments");
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      link.setVisible(true);
      hyperWebsite.setVisible(true);
      code.setVisible(true);
      labelCode.setVisible(true);
      copyButton.setVisible(true);
      pollingMessage.setVisible(true);
      header_Link_Webseite.setVisible(true);

      link.setDisable(false);
      hyperWebsite.setDisable(false);
      code.setDisable(false);
      labelCode.setDisable(false);
      copyButton.setDisable(false);
      pollingMessage.setDisable(false);
      header_Link_Webseite.setDisable(false);

      setWebsiteCode();
   }

   public void setWebsiteCode() {
      final Graph graph = new Graph();

      try {
         final DeviceCodeAnswer answer = graph.deviceCodeFlow();
         final String deviceCode = answer.getDevice_code();

         final String userCode = answer.getUser_code();

         final String verificationUrl = answer.getVerification_uri();
         hyperWebsite.setText(verificationUrl);
         labelCode.setText(userCode);

         initializeHyperLink(verificationUrl);
         initializeCopyButton();

         this.deviceCode.set(deviceCode);
      } catch (IOException | InterruptedException e) {
         LOG.error("Could not retrieve device code: " , e);
      }
   }
}
