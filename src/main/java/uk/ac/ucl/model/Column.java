package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;

public class Column {
    private final String name;
    private final List<String> rows;
    
    public Column(String columnName){
        this.name = columnName;
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public int getSize(){
        return this.rows.size();
    }

    public String getRowValue(int index){
        return this.rows.get(index);
    }

    public void setRowValue(int index, String newValue){
        this.rows.set(index,newValue);
    }

    public void addRowValue(String newValue){
        this.rows.add(newValue);
    }

    public void removeRowValue(int index) {
        this.rows.remove(index);
    }
}
