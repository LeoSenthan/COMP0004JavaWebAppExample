package uk.ac.ucl.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Model {
    private static final Pattern SEARCH_TERM_SPLITTER = Pattern.compile("\\s+");
    private static final List<String> SUMMARY_COLUMNS = List.of(
        "ID", "FIRST", "LAST", "GENDER", "BIRTHDATE", "CITY", "STATE", "ZIP"
    );
    private static final String CITY_COLUMN = "CITY";
    private static final String BIRTHDATE_COLUMN = "BIRTHDATE";
    private static final String DATA_RESOURCE_PATH = "data/patients100000.csv";
    private static final String JSON_RESOURCE_PATH = "data/patients100000.json";
    private static final Path SOURCE_DATA_PATH = Paths.get("src", "main", "resources", DATA_RESOURCE_PATH);
    private static final Path TARGET_DATA_PATH = Paths.get("target", "classes", DATA_RESOURCE_PATH);
    private static final Path SOURCE_JSON_PATH = Paths.get("src", "main", "resources", JSON_RESOURCE_PATH);
    private static final Path TARGET_JSON_PATH = Paths.get("target", "classes", JSON_RESOURCE_PATH);

    private static Model instance;
    private final DataFrame data;

    private Model() {
        DataLoader loader = new DataLoader(DATA_RESOURCE_PATH);
        this.data = loader.load();
    }

    public static synchronized Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    public DataFrame getData() {
        return data;
    }

    public int getTotalPatients() {
        return this.data.getRowCount();
    }

    public int getOldestRowIndex() {
        return getExtremeBirthdateRowIndex(true);
    }

    public int getYoungestRowIndex() {
        return getExtremeBirthdateRowIndex(false);
    }

    public String getPatientDisplayName(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.data.getRowCount()) {
            return "N/A";
        }

        String first = this.data.getValue("FIRST", rowIndex);
        String last = this.data.getValue("LAST", rowIndex);
        return (first + " " + last).trim();
    }

    public int countPatientsInCity(String city) {
        return countPatientsByFieldValue(CITY_COLUMN, city);
    }

    public int countPatientsByFieldValue(String columnName, String value) {
        if (columnName == null || columnName.isBlank() || value == null || value.isBlank()) {
            return 0;
        }
        String trimmed = value.trim();
        int count = 0;
        for (int row = 0; row < this.data.getRowCount(); row++) {
            String rowValue = this.data.getValue(columnName, row);
            if (rowValue != null && rowValue.equalsIgnoreCase(trimmed)) {
                count++;
            }
        }
        return count;
    }

    public Map<String, Integer> getRaceDistribution() {
        return getDistributionByColumn("RACE");
    }

    public Map<String, Integer> getAgeBandDistribution() {
        Map<String, Integer> ageBands = new LinkedHashMap<>();
        ageBands.put("0-17", 0);
        ageBands.put("18-29", 0);
        ageBands.put("30-44", 0);
        ageBands.put("45-59", 0);
        ageBands.put("60-74", 0);
        ageBands.put("75+", 0);
        ageBands.put("Unknown", 0);

        LocalDate today = LocalDate.now();
        for (int row = 0; row < this.data.getRowCount(); row++) {
            LocalDate birthdate = parseBirthdate(row);
            if (birthdate == null || birthdate.isAfter(today)) {
                ageBands.put("Unknown", ageBands.get("Unknown") + 1);
                continue;
            }

            int age = Period.between(birthdate, today).getYears();
            String bucket;
            if (age <= 17) {
                bucket = "0-17";
            } else if (age <= 29) {
                bucket = "18-29";
            } else if (age <= 44) {
                bucket = "30-44";
            } else if (age <= 59) {
                bucket = "45-59";
            } else if (age <= 74) {
                bucket = "60-74";
            } else {
                bucket = "75+";
            }
            ageBands.put(bucket, ageBands.get(bucket) + 1);
        }

        return ageBands;
    }

    public Map<String, Integer> getDistributionByColumn(String columnName) {
        Map<String, Integer> counts = new HashMap<>();

        for (int row = 0; row < this.data.getRowCount(); row++) {
            String value = this.data.getValue(columnName, row);
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
        List<String> searchTerms = getSearchTerms(keyword);
        boolean hasSearch = !searchTerms.isEmpty();

        for (int row = 0; row < this.data.getRowCount(); row++) {
            if (!hasSearch) {
                matches.add(row);
                continue;
            }

            if (rowMatchesTerms(row, searchTerms)) {
                matches.add(row);
            }
        }

        return matches;
    }

    public List<Integer> searchRowIndexes(Map<String, String> searchCriteria) {
        Map<String, String> normalizedCriteria = normalizeSearchCriteria(searchCriteria);
        if (normalizedCriteria.isEmpty()) {
            List<Integer> allRows = new ArrayList<>();
            for (int row = 0; row < this.data.getRowCount(); row++) {
                allRows.add(row);
            }
            return allRows;
        }

        List<Integer> matches = new ArrayList<>();
        for (int row = 0; row < this.data.getRowCount(); row++) {
            if (rowMatchesCriteria(row, normalizedCriteria)) {
                matches.add(row);
            }
        }
        return matches;
    }

    public List<String> searchFor(String keyword) {
        List<String> results = new ArrayList<>();
        List<String> searchTerms = getSearchTerms(keyword);

        if (searchTerms.isEmpty()) {
            return results;
        }

        for (int row = 0; row < this.data.getRowCount(); row++) {
            if (rowMatchesTerms(row, searchTerms)) {
                results.add(getPatientDisplayName(row));
            }
        }

        return results;
    }

    public List<String> getSummaryColumns() {
        return SUMMARY_COLUMNS;
    }

    public List<String> getAllColumns() {
        return this.data.getColumnNames();
    }

    public synchronized int addPatient(Map<String, String> patientValues) {
        List<String> values = buildOrderedRow(patientValues);
        this.data.addRow(values);
        saveData();
        return this.data.getRowCount() - 1;
    }

    public synchronized void updatePatient(int rowIndex, Map<String, String> patientValues) {
        validateRowIndex(rowIndex);
        for (String columnName : this.data.getColumnNames()) {
            this.data.putValue(columnName, rowIndex, normalizeFieldValue(patientValues.get(columnName)));
        }
        saveData();
    }

    public synchronized void deletePatient(int rowIndex) {
        validateRowIndex(rowIndex);
        this.data.removeRow(rowIndex);
        saveData();
    }

    public synchronized String exportDataAsJson() {
        JSONWriter jsonWriter = new JSONWriter();
        jsonWriter.write(this.data, SOURCE_JSON_PATH);
        jsonWriter.write(this.data, TARGET_JSON_PATH);
        return SOURCE_JSON_PATH.toString();
    }

    public Map<String, String> normalizeSearchCriteria(Map<String, String> searchCriteria) {
        if (searchCriteria == null || searchCriteria.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> normalizedCriteria = new LinkedHashMap<>();
        for (String columnName : SUMMARY_COLUMNS) {
            String rawValue = searchCriteria.get(columnName);
            if (rawValue == null) {
                continue;
            }

            String normalizedValue = rawValue.trim().toLowerCase();
            if (!normalizedValue.isEmpty()) {
                normalizedCriteria.put(columnName, normalizedValue);
            }
        }
        return normalizedCriteria;
    }

    public Map<String, String> getRowValues(int rowIndex) {
        return getRowValues(rowIndex, this.data.getColumnNames());
    }

    public Map<String, String> getRowValues(int rowIndex, List<String> columnNames) {
        Map<String, String> values = new LinkedHashMap<>();
        for (String columnName : columnNames) {
            values.put(columnName, this.data.getValue(columnName, rowIndex));
        }
        return values;
    }

    private List<String> getSearchTerms(String keyword) {
        List<String> searchTerms = new ArrayList<>();
        if (keyword == null) {
            return searchTerms;
        }

        String normalizedKeyword = keyword.trim().toLowerCase();
        if (normalizedKeyword.isEmpty()) {
            return searchTerms;
        }

        String[] terms = SEARCH_TERM_SPLITTER.split(normalizedKeyword);
        for (String term : terms) {
            if (!term.isBlank()) {
                searchTerms.add(term);
            }
        }
        return searchTerms;
    }

    private int getExtremeBirthdateRowIndex(boolean oldest) {
        int matchingRow = -1;
        LocalDate matchingBirthdate = null;

        for (int row = 0; row < this.data.getRowCount(); row++) {
            LocalDate birthdate = parseBirthdate(row);
            if (birthdate == null) {
                continue;
            }

            if (matchingBirthdate == null
                || (oldest && birthdate.isBefore(matchingBirthdate))
                || (!oldest && birthdate.isAfter(matchingBirthdate))) {
                matchingBirthdate = birthdate;
                matchingRow = row;
            }
        }

        return matchingRow;
    }

    private LocalDate parseBirthdate(int rowIndex) {
        String birthdateRaw = this.data.getValue(BIRTHDATE_COLUMN, rowIndex);
        if (birthdateRaw == null || birthdateRaw.isBlank()) {
            return null;
        }

        try {
            return LocalDate.parse(birthdateRaw);
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }

    private List<String> buildOrderedRow(Map<String, String> patientValues) {
        List<String> values = new ArrayList<>();
        for (String columnName : this.data.getColumnNames()) {
            values.add(normalizeFieldValue(patientValues.get(columnName)));
        }
        return values;
    }

    private void validateRowIndex(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= this.data.getRowCount()) {
            throw new IllegalArgumentException("Invalid patient row: " + rowIndex);
        }
    }

    private String normalizeFieldValue(String value) {
        if (value == null) {
            return "";
        }

        String trimmedValue = value.trim();
        if (trimmedValue.contains(",") || trimmedValue.contains("\n") || trimmedValue.contains("\r")) {
            throw new IllegalArgumentException("Field values cannot contain commas or line breaks");
        }
        return trimmedValue;
    }

    private void saveData() {
        List<String> lines = new ArrayList<>();
        List<String> columnNames = this.data.getColumnNames();
        lines.add(String.join(",", columnNames));

        for (int row = 0; row < this.data.getRowCount(); row++) {
            List<String> rowValues = new ArrayList<>();
            for (String columnName : columnNames) {
                rowValues.add(this.data.getValue(columnName, row));
            }
            lines.add(String.join(",", rowValues));
        }

        writeDataFile(SOURCE_DATA_PATH, lines);
        writeDataFile(TARGET_DATA_PATH, lines);
    }

    private void writeDataFile(Path path, List<String> lines) {
        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.write(path, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save patient data to '" + path + "'", e);
        }
    }

    private boolean rowMatchesTerms(int rowIndex, List<String> searchTerms) {
        for (String searchTerm : searchTerms) {
            if (!rowContainsTerm(rowIndex, searchTerm)) {
                return false;
            }
        }
        return true;
    }

    private boolean rowMatchesCriteria(int rowIndex, Map<String, String> searchCriteria) {
        for (Map.Entry<String, String> entry : searchCriteria.entrySet()) {
            String value = this.data.getValue(entry.getKey(), rowIndex);
            if (value == null || !value.toLowerCase().contains(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private boolean rowContainsTerm(int rowIndex, String searchTerm) {
        for (String column : this.data.getColumnNames()) {
            String value = this.data.getValue(column, rowIndex);
            if (value != null && value.toLowerCase().contains(searchTerm)) {
                return true;
            }
        }
        return false;
    }
}