package org.rapidpm.vaadin.trainer.modules.mainview.report;

import javax.annotation.PostConstruct;

import com.vaadin.ui.Component;
import com.vaadin.ui.Composite;
import com.vaadin.ui.Label;

/**
 *
 */
public class ReportComponent extends Composite {

  @PostConstruct
  private void postConstruct(){
    setCompositionRoot(new Label("Report"));
  }

}

