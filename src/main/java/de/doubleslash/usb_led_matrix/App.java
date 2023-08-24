package de.doubleslash.usb_led_matrix;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.doubleslash.usb_led_matrix.model.Model;
import de.doubleslash.usb_led_matrix.resources.Resources;
import de.doubleslash.usb_led_matrix.usb_adapter.UsbAdapter;
import de.doubleslash.usb_led_matrix.view.ConfigurationView;
import de.doubleslash.usb_led_matrix.view.UsbDeviceView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
   private static final Logger LOG = LoggerFactory.getLogger(App.class);
   private final UsbAdapter usbAdapter = new UsbAdapter();
   public static String clientVersion;

   public static void launchApplication(final String[] args) {
      launch(args);
   }

   @Override
   public void start(final Stage primaryStage) throws Exception {
      LOG.info("Starting application...");
      Platform.setImplicitExit(false);
      primaryStage.setTitle("Configuration");

      final Properties properties = new Properties();
      properties.load(this.getClass().getResourceAsStream("/busylight.properties"));

      LOG.debug("Version of client is " + properties.getProperty("version"));
      clientVersion = properties.getProperty("version");
      final FXMLLoader fxmlLoader = new FXMLLoader(Resources.CONFIGURATION_VIEW.getResource());
      final Parent root = fxmlLoader.load();

      final Model model = new Model();
      model.getSerialPorts().setAll(UsbAdapter.getSerialPortNames());

      final UsbDeviceView usbDeviceView = new UsbDeviceView(model, usbAdapter);
      usbDeviceView.initialize();
      final Scene scene = new Scene(root);
      final ConfigurationView configurationView = fxmlLoader.getController();
      configurationView.setModel(model);
      configurationView.setScene(scene);
      configurationView.dataInitialization(usbAdapter);

      primaryStage.setScene(scene);
      final Image image = new Image("/images/DsIcon.png");
      primaryStage.getIcons().add(image);
      primaryStage.sizeToScene();
      primaryStage.setMinWidth(560);
      primaryStage.setMinHeight(404);
      primaryStage.setResizable(false);
      primaryStage.show();
      primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
         @Override
         public void handle(final WindowEvent t) {
            hide(primaryStage);
         }
      });
      this.createTrayIcon(primaryStage);
   }

   private void createTrayIcon(final Stage stage) {
      if (SystemTray.isSupported()) {
         final ActionListener showListener = new ActionListener() {
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent e) {
               Platform.runLater(new Runnable() {
                  @Override
                  public void run() {
                     stage.show();
                  }
               });
            }
         };

         final ActionListener exitListener = new ActionListener() {
            @Override
            public void actionPerformed(final java.awt.event.ActionEvent e) {
               stop();
            }
         };

         final PopupMenu popup = new PopupMenu();

         final URL url = BusyLight.class.getResource("/images/trayIcon.png");
         final java.awt.Image image = Toolkit.getDefaultToolkit().getImage(url);

         final TrayIcon trayIcon = new TrayIcon(image);

         final SystemTray tray = SystemTray.getSystemTray();

         // Create a pop-up menu components
         final MenuItem configureItem = new MenuItem("Configure");
         configureItem.addActionListener(showListener);
         final MenuItem exitItem = new MenuItem("Exit");
         exitItem.addActionListener(exitListener);
         // Add components to pop-up menu
         popup.add(configureItem);
         popup.addSeparator();
         popup.add(exitItem);

         trayIcon.setPopupMenu(popup);
         trayIcon.addActionListener(showListener);
         try {
            tray.add(trayIcon);
         } catch (final AWTException e) {
            LOG.warn("TrayIcon could not be added.");
         }
      }
   }

   private void hide(final Stage stage) {
      Platform.runLater(new Runnable() {
         @Override
         public void run() {
            if (SystemTray.isSupported()) {
               stage.hide();
            } else {
               stop();
            }
         }
      });
   }

   @Override
   public void stop() {
      usbAdapter.closePort();
      final int exitCode = 0;
      LOG.info("Application stopped with code '{}'.", exitCode);
      System.exit(exitCode);
   }
}
