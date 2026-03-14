package uk.ac.ucl.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataLoader {

    private String filename;
    private DataFrame database;

    public DataLoader(String filepath) {
        this.filename = filepath;
    }

    public DataFrame load() {

        database = new DataFrame();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new IOException("Resource not found: " + filename);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String header = reader.readLine();
            createColumns(header);
            String line;

            while ((line = reader.readLine()) != null) {
                readLine(line);
            }

        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }

        return database;
    }

    private void createColumns(String columnNames) {
        String[] attributes = columnNames.split(",");
        for (String field : attributes) {
            Column col = new Column(field);
            database.addColumn(col);
        }
    }

    private void readLine(String line) {
        String[] vals = line.split(",", -1);
        for (int i = 0; i < vals.length; i++) {
            database.addValue(database.getColumnNames().get(i), vals[i]);
        }
    }
}