package org.rapidpm.vaadin.develop;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinServlet;

/**
 *
 */
@WebServlet(value = "/*")
@VaadinServletConfiguration(productionMode = false, ui = ComponentTestUI.class)
public class ComponentDevelopmentTestServlet extends VaadinServlet {}
