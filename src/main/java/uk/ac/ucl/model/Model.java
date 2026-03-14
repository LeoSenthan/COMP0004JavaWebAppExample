package uk.ac.ucl.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Model {

    private static Model instance;
    private DataFrame data;

    // Private constructor ensures singleton pattern
    private Model() {
        // Load CSV from resources folder
        DataLoader loader = new DataLoader("data/patients1000.csv");
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

    public int getTotalPatients() {
        return data.getRowCount();
    }

    public int getOldestRowIndex() {
        int oldestRow = -1;
        LocalDate oldestBirthdate = null;

        for (int row = 0; row < data.getRowCount(); row++) {
            String birthdateRaw = data.getValue("BIRTHDATE", row);
            if (birthdateRaw == null || birthdateRaw.isBlank()) {
                continue;
            }

            try {
                LocalDate birthdate = LocalDate.parse(birthdateRaw);
                if (oldestBirthdate == null || birthdate.isBefore(oldestBirthdate)) {
                    oldestBirthdate = birthdate;
                    oldestRow = row;
                }
            } catch (DateTimeParseException ignored) {
                // Skip malformed birthdate values.
            }
        }

        return oldestRow;
    }

    public String getPatientDisplayName(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= data.getRowCount()) {
            return "N/A";
        }

        String first = data.getValue("FIRST", rowIndex);
        String last = data.getValue("LAST", rowIndex);
        return (first + " " + last).trim();
    }

    public int countPatientsInCity(String city) {
        if (city == null || city.isBlank()) {
            return 0;
        }

        int count = 0;
        String cityNormalized = city.trim();
        for (int row = 0; row < data.getRowCount(); row++) {
            String rowCity = data.getValue("CITY", row);
            if (rowCity != null && rowCity.equalsIgnoreCase(cityNormalized)) {
                count++;
            }
        }
        return count;
    }

    public Map<String, Integer> getDistributionByColumn(String columnName) {
        Map<String, Integer> counts = new HashMap<>();

        for (int row = 0; row < data.getRowCount(); row++) {
            String value = data.getValue(columnName, row);
            String key = (value == null || value.isBlank()) ? "(blank)" : value;
            counts.put(key, counts.getOrDefault(key, 0) + 1);
        }

        return counts;
    }

    public Map<String, Integer> getTopDistributionByColumn(String columnName, int limit) {
        Map<String, Integer> counts = getDistributionByColumn(columnName);
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort(
            Comparator
                .comparing((Map.Entry<String, Integer> e) -> e.getValue()).reversed()
                .thenComparing(Map.Entry::getKey)
        );

        Map<String, Integer> topCounts = new LinkedHashMap<>();
        int max = Math.max(0, limit);
        for (int i = 0; i < entries.size() && i < max; i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            topCounts.put(entry.getKey(), entry.getValue());
        }
        return topCounts;
    }

    public List<Integer> searchRowIndexes(String keyword) {
        List<Integer> matches = new ArrayList<>();
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase();
        boolean hasSearch = !normalizedKeyword.isEmpty();

        for (int row = 0; row < data.getRowCount(); row++) {
            if (!hasSearch) {
                matches.add(row);
                continue;
            }

            for (String column : data.getColumnNames()) {
                String value = data.getValue(column, row);
                if (value != null && value.toLowerCase().contains(normalizedKeyword)) {
                    matches.add(row);
                    break;
                }
            }
        }

        return matches;
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