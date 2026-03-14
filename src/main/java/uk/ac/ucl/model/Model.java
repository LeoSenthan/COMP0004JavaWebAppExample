package uk.ac.ucl.model;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private static Model instance;
    private DataFrame data;

    // Private constructor ensures singleton pattern
    private Model() {
        // Load CSV from resources folder
        DataLoader loader = new DataLoader("data/patients100.csv");
        data = loader.load();
    }

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        } else if (instance.data == null || instance.data.getRowCount() == 0) {
            // Recover from a previous startup where data failed to load.
            instance = new Model();
        }
        return instance;
    }

    public DataFrame getData() {
        return data;
    }

    // Search across all columns, returns FIRST + LAST names for matches
    public List<String> searchFor(String keyword) {
        List<String> results = new ArrayList<>();

        for (int row = 0; row < data.getRowCount(); row++) {
            boolean match = false;

            for (String column : data.getColumnNames()) {
                String value = data.getValue(column, row);
                if (value != null && value.toLowerCase().contains(keyword.toLowerCase())) {
                    match = true;
                    break;
                }
            }

            if (match) {
                String first = data.getValue("FIRST", row);
                String last = data.getValue("LAST", row);
                results.add(first + " " + last);
            }
        }

        return results;
    }
}