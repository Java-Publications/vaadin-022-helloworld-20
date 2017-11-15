package dev.org.rapidpm.vaadin;

import com.vaadin.ui.*;
import org.rapidpm.ddi.DI;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DevComponent extends Composite {

  private final VerticalLayout  mainLayout         = new VerticalLayout();
  private final Panel           testComponentPanel = new Panel("Component Test Area");
  private final ComboBox<Class> classComboBox      = new ComboBox<>();
  private final Button          refresh            = new Button("refresh");

  public DevComponent() {
    classComboBox.setWidth(100, Unit.PERCENTAGE);

    classComboBox.addSelectionListener(event -> event.getSelectedItem()
                                                     .ifPresent(c -> {
                                                       Component o = (Component) DI.activateDI(c);
                                                       o.setSizeFull();
                                                       testComponentPanel.setContent(o);
                                                     }));

    refresh.setWidth(100, Unit.PERCENTAGE);
    refresh.addClickListener(e -> classComboBox.getSelectedItem().ifPresent(c -> {
      Component o = (Component) DI.activateDI(c);
      o.setSizeFull();
      testComponentPanel.setContent(o);
    }));

    testComponentPanel.setSizeFull();
    mainLayout.addComponents(classComboBox, refresh, testComponentPanel);
  }

  @PostConstruct
  private void postConstruct() {

    final List<Class> classStream = DI.getSubTypesWithoutInterfacesAndGeneratedOf(Composite.class)
                                      .stream()
                                      .filter(aClass -> aClass.getName().contains("org.rapidpm.vaadin"))
                                      .filter(aClass -> !aClass.getName().contains("dev.org.rapidpm.vaadin"))
                                      .collect(Collectors.toList());

    classComboBox.setItems(classStream);


    setCompositionRoot(mainLayout);
//    setSizeFull();
  }
}
