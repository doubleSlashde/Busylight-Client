package de.doubleslash.usb_led_matrix.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Toggle;
import javafx.scene.paint.Color;

public class Model {
   private final ObservableList<String> serialPorts = FXCollections.observableArrayList();

   private final SimpleObjectProperty<Color> color = new SimpleObjectProperty<>(Color.BLACK);
   private final SimpleObjectProperty<Byte> brightness = new SimpleObjectProperty<>((byte) 127);
   private final SimpleObjectProperty<String> microcontrollerVersion = new SimpleObjectProperty<>();
   private final SimpleBooleanProperty connected = new SimpleBooleanProperty();
   private final SimpleBooleanProperty checkConnection = new SimpleBooleanProperty(true);
   private final SimpleStringProperty portName = new SimpleStringProperty("");
   private final SimpleStringProperty deviceCode = new SimpleStringProperty("");

   private final SimpleObjectProperty<String> presence = new SimpleObjectProperty<>();

   private final SimpleObjectProperty<Toggle> selectedToggle = new SimpleObjectProperty<>();

   private boolean loggedIntoTeams;

   public void setLoggedIntoTeams(final boolean loggedIntoTeams) {
      this.loggedIntoTeams = loggedIntoTeams;
   }

   public ObservableList<String> getSerialPorts() {
      return serialPorts;
   }

   public final SimpleObjectProperty<Color> colorProperty() {
      return this.color;
   }

   public final void setColor(final Color color) {
      this.colorProperty().set(color);
   }

   public final SimpleObjectProperty<Byte> brightnessProperty() {
      return this.brightness;
   }

   public final void setBrightnessFromPercentage(final double brightness) {
      this.brightnessProperty().set((byte) Math.round(brightness * 2.55));
   }

   public final Byte getBrightness() {
      return this.brightness.getValue();
   }

   public final SimpleBooleanProperty checkConnectionProperty() {
      return this.checkConnection;
   }

   public final SimpleObjectProperty<String> versionProperty() {
      return this.microcontrollerVersion;
   }

   public final void setVersion(final String version) {
      this.versionProperty().set(version);
   }

   public final void setCheckConnection(final boolean checkCon) {
      this.checkConnectionProperty().set(checkCon);
   }

   public final SimpleBooleanProperty connectedProperty() {
      return this.connected;
   }

   public final void setConnected(final boolean connected) {
      this.connectedProperty().set(connected);
   }

   public final SimpleStringProperty portNameProperty() {
      return this.portName;
   }

   public final SimpleStringProperty deviceCodeProperty() {
      return this.deviceCode;
   }

   public final String getDeviceCode() {
      return this.deviceCodeProperty().get();
   }

   public final void setDeviceCode(final String devCode) {
      this.deviceCodeProperty().set(devCode);
   }

   public final String getPortName() {
      return this.portNameProperty().get();
   }

   public final void setPortName(final String portName) {
      this.portNameProperty().set(portName);
   }

   public final SimpleObjectProperty<String> presenceProperty() {
      return this.presence;
   }

   public final void setPresence(final String presence) {
      this.presenceProperty().set(presence);
   }

   public final SimpleObjectProperty<Toggle> selectedToggleProperty() {
      return this.selectedToggle;
   }

   public final Toggle getSelectedToggle() {
      return this.selectedToggleProperty().get();
   }

   public final void setSelectedToggle(final Toggle selectedToggle) {
      this.selectedToggleProperty().set(selectedToggle);
   }

   public boolean isLoggedIntoTeams() {
      return loggedIntoTeams;
   }
}
