package de.doubleslash.usb_led_matrix;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {
   private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

   @Override
   public void uncaughtException(final Thread t, final Throwable e) {
      LOG.error("Uncaught exception on thread '{}'.", t, e);
   }

   public void register() {
      LOG.debug("Registering uncaught exception handler");
      final UncaughtExceptionHandler defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
      if (defaultUncaughtExceptionHandler != null) {
         LOG.warn("Uncaught exception handler was already set ('{}'). Overwritting.", defaultUncaughtExceptionHandler);
      }
      Thread.setDefaultUncaughtExceptionHandler(this);
   }
}
