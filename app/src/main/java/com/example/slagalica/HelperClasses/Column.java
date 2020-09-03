package com.example.slagalica.HelperClasses;

import java.util.ArrayList;
import java.util.List;

public class Column {

    private String solution;
    private ArrayList<String> fields;

    public Column()
    {
        solution = "";
        fields = new ArrayList<>();
    }

    // Adding specific field to the column
    public void add(String field)
    {
        fields.add(field);
    }

    // Setting solution
    public void setSolution(String solution)
    {
        this.solution = solution;
    }

    public String getSolution() {
        return solution;
    }

    public ArrayList<String> getFields() {
        return fields;
    }
}
