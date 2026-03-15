package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;

public class DataFrame {
    private final List<Column> columns;

    public DataFrame(){
        this.columns = new ArrayList<>();
    }

    public void addColumn(Column newColumn){
        if (newColumn == null){
            throw new IllegalArgumentException("Cannot add a null column");
        }
        this.columns.add(newColumn);
    }   

    public List<String> getColumnNames(){
        List<String> fields = new ArrayList<>();
        for (Column col : this.columns){
            fields.add(col.getName());
        }
        return fields;
    }

    public int getRowCount(){
        if (this.columns.isEmpty()){
            return 0;
        }
        return this.columns.get(0).getSize();
    }

    public String getValue(String columnName, int row){
        if (row < 0 || row >= this.getRowCount()) {
            throw new IndexOutOfBoundsException("Row index " + row + " is out of bounds");
        }
        for (Column col : this.columns){
            if (col.getName().equals(columnName)){
                return col.getRowValue(row);
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found");
    }

    public void putValue(String columnName, int row, String newValue){
        if (row < 0 || row >= this.getRowCount()) {
            throw new IndexOutOfBoundsException("Row index " + row + " is out of bounds");
        }
        for (Column col : this.columns){
            if (col.getName().equals(columnName)){
                col.setRowValue(row, newValue);
                return;
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found");
    }

    public void addValue(String columnName, String value){
        for (Column col : this.columns){
            if (col.getName().equals(columnName)){
                col.addRowValue(value);
                return;
            }
        }
        throw new IllegalArgumentException("Column '" + columnName + "' not found");
    }

    public void addRow(List<String> values) {
        if (values == null || values.size() != this.columns.size()) {
            throw new IllegalArgumentException("Row must contain exactly " + this.columns.size() + " values");
        }

        for (int i = 0; i < this.columns.size(); i++) {
            this.columns.get(i).addRowValue(values.get(i));
        }
    }

    public void removeRow(int row) {
        if (row < 0 || row >= this.getRowCount()) {
            throw new IndexOutOfBoundsException("Row index " + row + " is out of bounds");
        }

        for (Column col : this.columns) {
            col.removeRowValue(row);
        }
    }
}