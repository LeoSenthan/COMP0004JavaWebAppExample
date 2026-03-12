package uk.ac.ucl.model;
import java.util.ArrayList;

public class Column {
    private String name;
    private ArrayList<String> rows;
    
    public Column(String column_name){
        this.name = column_name;
        this.rows = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public int getSize(){
        return rows.size();
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
}
