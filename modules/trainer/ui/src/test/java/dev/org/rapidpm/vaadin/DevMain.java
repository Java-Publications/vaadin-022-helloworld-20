package dev.org.rapidpm.vaadin;

import org.rapidpm.ddi.DI;
import org.rapidpm.dependencies.core.logger.HasLogger;
import org.rapidpm.dependencies.core.logger.Logger;
import org.rapidpm.dependencies.core.logger.LoggingService;
import org.rapidpm.microservice.Main;

/**
 *
 */
public class DevMain implements HasLogger {
  public static void main(String[] args) {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("dev.org.rapidpm");

    final LoggingService logger = Logger.getLogger(DevMain.class);
    logger.warning("Starting the Vaadin Component Testing App");
    Main.deploy();

  }

}
