package uk.ac.ucl.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class JSONWriter {

    public void write(DataFrame dataFrame, Path outputPath) {
        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }
            Files.writeString(outputPath, buildJson(dataFrame), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write JSON file to '" + outputPath + "'", e);
        }
    }

    private String buildJson(DataFrame dataFrame) {
        List<String> columnNames = dataFrame.getColumnNames();
        StringBuilder json = new StringBuilder();
        json.append("[\n");

        for (int row = 0; row < dataFrame.getRowCount(); row++) {
            json.append("  {");

            for (int column = 0; column < columnNames.size(); column++) {
                String columnName = columnNames.get(column);
                String value = dataFrame.getValue(columnName, row);

                if (column > 0) {
                    json.append(", ");
                }

                json.append('"')
                    .append(escapeJson(columnName))
                    .append('"')
                    .append(": ")
                    .append('"')
                    .append(escapeJson(value))
                    .append('"');
            }

            json.append("}");
            if (row < dataFrame.getRowCount() - 1) {
                json.append(',');
            }
            json.append("\n");
        }

        json.append("]\n");
        return json.toString();
    }

    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }

        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}