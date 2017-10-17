package org.rapidpm.vaadin.develop;


//import static org.rapidpm.frp.vaadin.addon.component.testing.Infrastructure.undertow;

import java.util.Optional;

import io.undertow.Undertow;

/**
 *
 */
public class CalcComponentDevelop {

  public static final String CONTEXT_PATH = "/";

  public static void start() {
    main(new String[0]);
  }

  public static void shutdown() {
    undertowOptional.ifPresent(Undertow::stop);
  }

  private static Optional<Undertow> undertowOptional;

  public static void main(String[] args) {
    Class<ComponentDevelopmentTestServlet> testServletClass = ComponentDevelopmentTestServlet.class;
    ClassLoader classLoader = CalcComponentDevelop.class.getClassLoader();
//    undertowOptional = undertow(classLoader,testServletClass);
  }

}
