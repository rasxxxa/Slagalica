package com.example.slagalica.HelperClasses;

import java.util.ArrayList;
import java.util.List;

public class Association {

    private String mainSolution;
    private List<Column> columnsList;

    public Association() {
        mainSolution = "";
        columnsList = new ArrayList<>();
    }

    public String getMainSolution() {
        return mainSolution;
    }

    public void setMainSolution(String mainSolution) {
        this.mainSolution = mainSolution;
    }

    public List<Column> getColumnsList() {
        return columnsList;
    }

    public void addColumn(Column column) {
        this.columnsList.add(column);
    }
}
