package uk.ac.ucl.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class DataLoader {

    private final String filename;

    public DataLoader(String filepath) {
        this.filename = filepath;
    }

    public DataFrame load() {
        DataFrame database = new DataFrame();

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            if (is == null) {
                throw new IOException("Resource not found: " + filename);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String header = reader.readLine();
            if (header == null || header.isBlank()) {
                throw new IOException("CSV header is missing in resource: " + filename);
            }
            createColumns(database, header);
            String line;

            while ((line = reader.readLine()) != null) {
                readLine(database, line);
            }

        } catch (IOException e) {
            throw new IllegalStateException("Failed to load patient data from '" + filename + "'", e);
        }

        return database;
    }

    private void createColumns(DataFrame database, String columnNames) {
        String[] attributes = columnNames.split(",");
        for (String field : attributes) {
            Column col = new Column(field);
            database.addColumn(col);
        }
    }

    private void readLine(DataFrame database, String line) {
        String[] vals = line.split(",", -1);
        for (int i = 0; i < vals.length; i++) {
            String columnName = database.getColumnNames().get(i);
            String cleanedValue = sanitizeValue(columnName, vals[i]);
            database.addValue(columnName, cleanedValue);
        }
    }

    private String sanitizeValue(String columnName, String value) {
        if (columnName == null || value == null) {
            return value;
        }
        if ("FIRST".equals(columnName) || "LAST".equals(columnName)) {
            return value.replaceAll("\\d+$", "");
        }
        return value;
    }
}