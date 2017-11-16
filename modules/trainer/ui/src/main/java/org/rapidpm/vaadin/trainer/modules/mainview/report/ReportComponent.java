package org.rapidpm.vaadin.trainer.modules.mainview.report;

import com.vaadin.ui.Composite;
import com.vaadin.ui.Grid;
import org.rapidpm.frp.model.serial.Quad;
import org.rapidpm.vaadin.ComponentIDGenerator;
import org.rapidpm.vaadin.trainer.api.property.PropertyService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.time.LocalDateTime;

import static org.rapidpm.vaadin.ComponentIDGenerator.gridID;

/**
 *
 */
public class ReportComponent extends Composite {


  public static final String REPORT_STATISTITICS_GRID            = "report.component.statistics.grid";
  public static final String REPORT_STATISTITICS_GRID_ID         = "report.component.statistics.grid.column.forename";
  public static final String REPORT_STATISTITICS_GRID_FORENAME   = "report.component.statistics.grid.column.forename";
  public static final String REPORT_STATISTITICS_GRID_FAMILYNAME = "report.component.statistics.grid.column.familyname";
  public static final String REPORT_STATISTITICS_GRID_LAST_LOGIN = "report.component.statistics.grid.column.lastlogin";

  private @Inject PropertyService propertyService;


  public static class Statistics extends Quad<Long, String, String, LocalDateTime> {
    public Statistics(Long id, String foreName, String familyName, LocalDateTime lastLogin) {
      super(id, foreName, familyName, lastLogin);
    }

    public Long id() {return getT1();}

    public String foreName() {return getT2();}

    public String familyName() {return getT3();}

    public LocalDateTime lastLogin() {return getT4();}
  }

  private final Grid<Statistics> statisticsGrid = new Grid<>();

  public ReportComponent() {
    statisticsGrid.setColumnReorderingAllowed(true);
    statisticsGrid.setSizeFull();

//    statisticsGrid.appendFooterRow();//.

    //statisticsGrid.setSizeFull();


  }

  private String property(String key) { return propertyService.resolve(key); }

  @PostConstruct
  private void postConstruct() {

    statisticsGrid.setId(gridID().apply(ReportComponent.class, REPORT_STATISTITICS_GRID));

    statisticsGrid.addColumn(Statistics::id)
                  .setCaption(property(REPORT_STATISTITICS_GRID_ID));
    statisticsGrid.addColumn(Statistics::foreName)
                  .setCaption(property(REPORT_STATISTITICS_GRID_FORENAME));
    statisticsGrid.addColumn(Statistics::familyName)
                  .setCaption(property(REPORT_STATISTITICS_GRID_FAMILYNAME));
    statisticsGrid.addColumn(Statistics::lastLogin)
                  .setCaption(property(REPORT_STATISTITICS_GRID_LAST_LOGIN));


    setCompositionRoot(statisticsGrid);
  }

}

