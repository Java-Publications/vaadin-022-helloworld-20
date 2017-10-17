package org.rapidpm.vaadin.develop;

import java.util.Optional;
import java.util.stream.Stream;

import org.rapidpm.vaadin.trainer.modules.mainview.calc.CalcComponent;
import org.rapidpm.vaadin.trainer.modules.mainview.dashboard.DashboardComponent;
import com.vaadin.annotations.Theme;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 *
 */
@Theme("valo") //manual testing
//switch to MyUIComponentFactory
public class ComponentTestUI extends UI {
  @Override
  protected void init(VaadinRequest request) {
    final VerticalLayout mainLayout = new VerticalLayout();
    final Panel testComponentPanel = new Panel("Component Test Area");
    final ComboBox<Class> classComboBox = new ComboBox<>();
    classComboBox.setWidth(100, Unit.PERCENTAGE);
    classComboBox.setItems(Stream.of(CalcComponent.class, DashboardComponent.class));
    classComboBox.addSelectionListener((SingleSelectionListener<Class>) event -> {
      Optional<Class> selectedItem = event.getSelectedItem();
      selectedItem.ifPresent(c -> {
        try {
          Component o = (Component)c.newInstance(); //TODO DI activated
          testComponentPanel.setContent(o);
        } catch (InstantiationException | IllegalAccessException e) {
          e.printStackTrace();
        }

      });
    });
    mainLayout.addComponents(classComboBox, testComponentPanel);
    setContent(mainLayout);
    setSizeFull();
  }
}

