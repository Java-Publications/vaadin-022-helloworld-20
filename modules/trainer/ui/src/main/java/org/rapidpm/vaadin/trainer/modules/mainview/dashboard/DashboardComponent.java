package org.rapidpm.vaadin.trainer.modules.mainview.dashboard;

import javax.annotation.PostConstruct;

import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

/**
 *
 */
public class DashboardComponent extends Composite {

  @PostConstruct
  private void postConstruct() {
    setCompositionRoot(new Label("DashBoard"));
  }
}