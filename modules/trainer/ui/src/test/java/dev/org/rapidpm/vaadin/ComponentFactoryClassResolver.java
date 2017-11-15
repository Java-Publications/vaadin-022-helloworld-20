package dev.org.rapidpm.vaadin;

import org.rapidpm.ddi.ResponsibleFor;
import org.rapidpm.ddi.implresolver.ClassResolver;
import org.rapidpm.vaadin.server.ui.JumpstartUIComponentFactory;

/**
 *
 */


@ResponsibleFor(JumpstartUIComponentFactory.class)
public class ComponentFactoryClassResolver implements ClassResolver<JumpstartUIComponentFactory>{
  @Override
  public Class<? extends JumpstartUIComponentFactory> resolve(Class<JumpstartUIComponentFactory> interf) {
    return DevMyUIComponentFactory.class;
  }
}
