package dev.org.rapidpm.vaadin;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Component;
import org.rapidpm.vaadin.server.ui.JumpstartUIComponentFactory;

import javax.inject.Inject;

/**
 *
 */
public class DevMyUIComponentFactory implements JumpstartUIComponentFactory {

  private @Inject DevComponent component;

  @Override
  public Component createComponentToSetAsContent(VaadinRequest vaadinRequest) {
    return component;
  }
}
