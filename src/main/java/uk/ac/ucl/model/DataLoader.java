package uk.ac.ucl.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataLoader {

    private String resourcePath; // path relative to resources folder
    private DataFrame database;
    private String[] attributes;

    public DataLoader(String resourcePath) {
        // Example: "data/patients100.csv"
        this.resourcePath = resourcePath;
    }

    public DataFrame load() {
        database = new DataFrame();

        // Load CSV from classpath
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {

            if (is == null) {
                System.out.println("File not found in resources: " + resourcePath);
                return database; // return empty DataFrame
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String header = reader.readLine();
            if (header != null) {
                createColumns(header);
                System.out.println("Header line: " + header);
            }

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
        attributes = columnNames.split(",");
        for (String field : attributes) {
            Column col = new Column(field);
            database.addColumn(col);
        }
    }

    private void readLine(String line) {
        String[] vals = line.split(",", -1); // keeps empty values
        for (int i = 0; i < vals.length; i++) {
            database.addValue(attributes[i], vals[i]);
        }
    }
}