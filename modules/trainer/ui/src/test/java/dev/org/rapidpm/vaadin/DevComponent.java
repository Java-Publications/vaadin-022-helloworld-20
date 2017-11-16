package dev.org.rapidpm.vaadin;

import com.vaadin.ui.*;
import org.rapidpm.ddi.DI;
import org.rapidpm.frp.functions.CheckedFunction;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.System.getProperty;

/**
 *
 */
public class DevComponent extends Composite {

  public static final String SELECTED_CLASS = "selected.class";


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
                                      .filter(aClass -> !aClass.getName().contains(this.getClass().getPackage().getName()))
                                      .collect(Collectors.toList());

    classComboBox.setItems(classStream);


    try {
      final Class<?> aClass = Class.forName(System.getProperty(SELECTED_CLASS));
      classComboBox.setValue(aClass);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }



    // explain in blog
    ((CheckedFunction<String, Class>) Class::forName)
        .apply(getProperty(SELECTED_CLASS))
        .ifPresent(classComboBox::setValue);

//    ((CheckedFunction<String, Class>) Class::forName)
//        .apply(getProperty(SELECTED_CLASS))
//        .ifPresent(classComboBox::setValue);

    setCompositionRoot(mainLayout);
  }
}
