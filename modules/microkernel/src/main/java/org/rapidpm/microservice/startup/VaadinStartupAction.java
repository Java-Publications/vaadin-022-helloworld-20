package org.rapidpm.microservice.startup;

import org.rapidpm.microservice.Main;
import org.rapidpm.microservice.MainUndertow;

import java.util.Optional;

/**
 *
 */
public class VaadinStartupAction implements Main.MainStartupAction {
  @Override
  public void execute(Optional<String[]> args) {

    System.setProperty(MainUndertow.SHIRO_ACTIVE_PROPERTY, "true");

  }
}
