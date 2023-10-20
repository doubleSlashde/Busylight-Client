package de.doubleslash.usb_led_matrix;

import javafx.scene.Scene;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.doubleslash.usb_led_matrix.model.AvailabilityStatus;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
   private static final Logger LOG = LoggerFactory.getLogger(Settings.class);
   private static final String COM_PORT_OPTION = "port";
   private static final String MODE_OPTION = "mode";
   private static final String BRIGHTNESS_OPTION = "brightness";
   private static final String HELP_OPTION = "help";
   private static final String COLOR_MODE_OPTION = "colorMode";
   private static final String IDLE_TIMEOUT_OPTION = "timeout";
   private static final String LOG4J_OPTION = "log-level";
   private static final String NUMBER_OF_LEDS_OPTION = "numberOfLeds";

   private static final String defaultCom = "";
   private static final String defaultMode = "teams";
   private static final double defaultBrightness = 50.0;
   private static final String defaultColorMode = "dark";
   private static final int defaultTimeoutInMinutes = 120;
   private static final int defaultNumberOfLeds = 10;

   private static String com = defaultCom;
   private static String mode = defaultMode;
   private static double brightness = defaultBrightness;
   private static String colorMode = defaultColorMode;
   private static String cssFile = "/CSS/light_Mode.css";
   private static int timeoutInMinutes = defaultTimeoutInMinutes;
   private static int numberOfLeds = defaultNumberOfLeds;

   public static double getBrightness() {
      return brightness;
   }

   public static String getMode() {
      return mode;
   }

   public static String getCom() {
      return com;
   }

   public static String getColorMode() {
      return colorMode;
   }

   public static void setColorMode(final String inputColorBackgroundMode) {
      colorMode = inputColorBackgroundMode;
   }

   public static int getTimeout() {
      return timeoutInMinutes;
   }

   public static void setTimeoutInMinutes(final int timeoutInMinutes) {
      Settings.timeoutInMinutes = timeoutInMinutes;
   }

   public static int getNumberOfLeds() {
      return numberOfLeds;
   }

   public static void handleParameters(final String[] args) {
      final CommandLineParser parser = new DefaultParser();
      final Options options = createOptions();

      try {
         final CommandLine commandLine = parser.parse(options, args);

         handlePortOption(commandLine);
         handleModeOption(commandLine);
         handleBrightnessOption(commandLine);
         handleHelpOption(options, commandLine);
         handleIdleTimeoutOption(commandLine);
         handleColorModeOption(commandLine);
         handleLog4jOption(commandLine);
         handleNumberOfLeds(commandLine);
      } catch (final ParseException e) {
         LOG.error("Error while processing parameters.", e);
      }
   }

   private static Options createOptions() {
      final Options options = new Options();

      final Option idleTimeoutOption = new Option("t", IDLE_TIMEOUT_OPTION, true,
            "Sets the time when the controller turns off if it's in the same state for too long. Enter the duration in minutes, the default timeout is 120 min = 2 hours.");
      idleTimeoutOption.setArgName("Idle Timeout");
      options.addOption(idleTimeoutOption);

      final Option portOption = new Option("p", COM_PORT_OPTION, true,
            "Sets com port e.g. COM42. The default value is the first port found in the list of serial ports.");
      portOption.setArgName("COM port");
      options.addOption(portOption);

      final Option colorModeOption = new Option("c", COLOR_MODE_OPTION, true,
            "Sets color mode dark or light. The default value is 'dark'.");
      colorModeOption.setArgName("dark | light ");
      options.addOption(colorModeOption);

      final Option modeOption = new Option("m", MODE_OPTION, true,
            "Sets the operation mode. The default value is 'teams'.");
      modeOption.setArgName("manual | teams");
      options.addOption(modeOption);

      final Option brightnessOption = new Option("b", BRIGHTNESS_OPTION, true,
            "Sets brightness between 0% and 100%. When you set brightness to 0, the LEDs are turned off. The default value is '50'.");
      brightnessOption.setArgName("Number in [0,100]");
      options.addOption(brightnessOption);

      final Option helpOption = new Option("h", HELP_OPTION, false, "Shows the help page.");
      options.addOption(helpOption);

      final Option log4jOption = new Option("l", LOG4J_OPTION, true,
            "Sets the log level (fatal; error; warn; info; debug; trace). 'info' is the default value.");
      log4jOption.setArgName("fatal | error | warn | info | debug | trace");

      options.addOption(log4jOption);

      final Option numberOFLedsOption = new Option("nr", NUMBER_OF_LEDS_OPTION, true,
            "Sets the number of Leds. The default value is 10.");
      numberOFLedsOption.setArgName("Number of LEDs");
      options.addOption(numberOFLedsOption);

      return options;
   }

   private static void handleHelpOption(final Options options, final CommandLine commandLine) {
      if (commandLine.hasOption(HELP_OPTION)) {
         final HelpFormatter formatter = new HelpFormatter();
         formatter.printHelp("java -jar busylight_client.jar [OPTIONS]", "Busylight", options, "");
         System.exit(0);
      }
   }

   private static void handlePortOption(final CommandLine commandLine) {
      if (commandLine.hasOption(COM_PORT_OPTION)) {
         final String port = commandLine.getOptionValue(COM_PORT_OPTION);
         final Pattern pattern = Pattern.compile("^com\\d+$", Pattern.CASE_INSENSITIVE);
         final Matcher matcher = pattern.matcher(port);
         if (matcher.find()) {
            com = port.toUpperCase();
         } else {
            LOG.warn("'{}' is not a valid value for the COM port.", port);
         }
      }
   }

   private static void handleModeOption(final CommandLine commandLine) {
      if (commandLine.hasOption(MODE_OPTION)) {
         String modeInput = commandLine.getOptionValue(MODE_OPTION);
         modeInput = modeInput.toLowerCase();
         if (modeInput.equals("manual") || modeInput.equals("teams")) {
            mode = modeInput;
            LOG.debug("The mode is: '{}'", mode);
         } else {
            LOG.warn("'{}' is not a valid mode.", modeInput);
         }
      }
   }

   private static void handleBrightnessOption(final CommandLine commandLine) {
      final String invalidInputMessage = "'{}' is not a valid value for brightness.";
      if (commandLine.hasOption(BRIGHTNESS_OPTION)) {
         final String brightnessValue = commandLine.getOptionValue(BRIGHTNESS_OPTION);
         try {
            final double brightnessInput = Double.parseDouble(brightnessValue);
            if (brightnessInput >= 0 && brightnessInput <= 100) {
               brightness = brightnessInput;
            } else {
               LOG.warn(invalidInputMessage, brightnessValue);
            }
         } catch (final NumberFormatException nfe) {
            LOG.warn(invalidInputMessage, brightnessValue);
         }
      }
   }

   private static void handleIdleTimeoutOption(final CommandLine commandLine) {
      if (commandLine.hasOption(IDLE_TIMEOUT_OPTION)) {
         final String timeoutValueString = commandLine.getOptionValue(IDLE_TIMEOUT_OPTION);
         final int timeoutValue = Integer.parseInt(timeoutValueString);
         timeoutInMinutes = timeoutValue;
      }
   }

   private static void handleColorModeOption(final CommandLine commandLine) {
      if (commandLine.hasOption(COLOR_MODE_OPTION)) {
         String modeInput = commandLine.getOptionValue(COLOR_MODE_OPTION);
         modeInput = modeInput.toLowerCase();
         if (modeInput.equals("dark") || modeInput.equals("light")) {
            colorMode = modeInput;
            LOG.debug("The colorMode is: '{}'", mode);
         } else {
            LOG.warn("'{}' is not a valid mode.", modeInput);
         }
      }
   }

   private static void handleLog4jOption(final CommandLine commandLine) {
      if (commandLine.hasOption(LOG4J_OPTION)) {
         final String log4jOptionValue = commandLine.getOptionValue(LOG4J_OPTION);
         switch (log4jOptionValue.toLowerCase()) {
         case "fatal":
            setLogLevel(Level.FATAL);
            break;
         case "error":
            setLogLevel(Level.ERROR);
            break;
         case "warn":
            setLogLevel(Level.WARN);
            break;
         case "info":
            setLogLevel(Level.INFO);
            break;
         case "debug":
            setLogLevel(Level.DEBUG);
            break;
         case "trace":
            setLogLevel(Level.TRACE);
            break;
         default:
            LOG.warn(
                  "The only valid log levels are 'fatal', 'error', 'warn', 'info' and 'debug', 'trace'. You have entered '{}'.",
                  log4jOptionValue);
            break;
         }
      }
   }

   private static void setLogLevel(final Level logLevel) {
      final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
      final Configuration config = ctx.getConfiguration();
      final LoggerConfig loggerConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
      loggerConfig.setLevel(logLevel);
      ctx.updateLoggers();
   }

   private static void handleNumberOfLeds(final CommandLine commandLine) {
      if (commandLine.hasOption(NUMBER_OF_LEDS_OPTION)) {
         final String numberOfLedsValueString = commandLine.getOptionValue(NUMBER_OF_LEDS_OPTION);
         final int numberOfLedsValue = Integer.parseInt(numberOfLedsValueString);
         numberOfLeds = numberOfLedsValue;
      }
   }

   public static void Dark(final Scene scene) {
      setColorMode("dark");
      if (scene.getStylesheets().contains(cssFile)) {
         scene.getStylesheets().remove(cssFile);
      }
   }

   public static void Light(final Scene scene) {
      scene.getStylesheets().add(cssFile);
      setColorMode("light");
   }

   public static void reset() {
      com = defaultCom;
      mode = defaultMode;
      brightness = defaultBrightness;
      colorMode = defaultColorMode;
      timeoutInMinutes = defaultTimeoutInMinutes;
      numberOfLeds = defaultNumberOfLeds;
   }
}
