package org.rapidpm.vaadin.develop;

import org.rapidpm.ddi.DI;
import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.MainUndertow;
import org.rapidpm.vaadin.server.JumpstartServlet;

import java.util.Optional;

/**
 *
 */
public class VaadinDevelopmentStartupAction implements Main.MainStartupAction {
  @Override
  public void execute(Optional<String[]> args) {
    DI.activatePackages("org.rapidpm");
    DI.activatePackages("dev.org.rapidpm");



  }
}
