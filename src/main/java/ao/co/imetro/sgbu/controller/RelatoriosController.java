package ao.co.imetro.sgbu.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TableView;

public class RelatoriosController {

    @FXML
    private BarChart<?, ?> booksChart;

    @FXML
    private PieChart statusChart;

    @FXML
    private TableView<?> summaryTable;

    @FXML
    public void initialize() {
        // Init charts
    }
}
