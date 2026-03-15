package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import uk.ac.ucl.model.Model;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/patientList")
public class ViewPatientListServlet extends HttpServlet {

    private static final int PAGE_SIZE = 50;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        Model model = Model.getInstance();
        List<String> summaryColumns = model.getSummaryColumns();
        Map<String, String> searchCriteria = getSearchCriteria(request, summaryColumns);
        String searchQueryString = buildSearchQueryString(searchCriteria, summaryColumns);

        setSearchStateAttributes(request, summaryColumns, searchCriteria, searchQueryString);
        setFeedbackAttributes(request);

        try {
            populatePatientListAttributes(request, model, summaryColumns, searchCriteria);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Search failed: " + e.getMessage());
        }

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/patientList.jsp");
        dispatch.forward(request, response);
    }

    private void setSearchStateAttributes(
        HttpServletRequest request,
        List<String> summaryColumns,
        Map<String, String> searchCriteria,
        String searchQueryString
    ) {
        request.setAttribute("summaryColumns", summaryColumns);
        request.setAttribute("searchCriteria", searchCriteria);
        request.setAttribute("searchQueryString", searchQueryString);
    }

    private void setFeedbackAttributes(HttpServletRequest request) {
        String message = request.getParameter("message");
        if (message != null && !message.isBlank()) {
            request.setAttribute("message", message);
        }

        String errorMessage = request.getParameter("errorMessage");
        if (errorMessage != null && !errorMessage.isBlank()) {
            request.setAttribute("errorMessage", errorMessage);
        }
    }

    private void populatePatientListAttributes(
        HttpServletRequest request,
        Model model,
        List<String> summaryColumns,
        Map<String, String> searchCriteria
    ) {
        List<Integer> matchingRows = model.searchRowIndexes(searchCriteria);
        request.setAttribute("matchingRows", matchingRows);

        Integer selectedRow = parseSelectedRow(request.getParameter("selectedRow"), model.getData().getRowCount());
        if (selectedRow != null && matchingRows.contains(selectedRow)) {
            request.setAttribute("selectedRow", selectedRow);
            request.setAttribute("selectedPatient", model.getRowValues(selectedRow));
        }

        int totalRows = matchingRows.size();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalRows / PAGE_SIZE));
        int currentPage = parsePage(request.getParameter("page"), totalPages);
        int startIndex = (currentPage - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalRows);

        request.setAttribute("patientRows", getPageRows(model, matchingRows, summaryColumns, startIndex, endIndex));
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
    }

    private Map<String, String> getSearchCriteria(HttpServletRequest request, List<String> summaryColumns) {
        Map<String, String> searchCriteria = new LinkedHashMap<>();
        for (String columnName : summaryColumns) {
            String value = request.getParameter(columnName);
            if (value != null) {
                searchCriteria.put(columnName, value.trim());
            }
        }
        return searchCriteria;
    }

    private String buildSearchQueryString(Map<String, String> searchCriteria, List<String> summaryColumns) {
        List<String> queryParts = new ArrayList<>();
        for (String columnName : summaryColumns) {
            String value = searchCriteria.get(columnName);
            if (value == null || value.isBlank()) {
                continue;
            }

            queryParts.add(
                URLEncoder.encode(columnName, StandardCharsets.UTF_8)
                    + "="
                    + URLEncoder.encode(value, StandardCharsets.UTF_8)
            );
        }
        return String.join("&", queryParts);
    }

    private List<Map<String, String>> getPageRows(
        Model model,
        List<Integer> matchingRows,
        List<String> summaryColumns,
        int startIndex,
        int endIndex
    ) {
        List<Map<String, String>> rows = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            int rowIndex = matchingRows.get(i);
            Map<String, String> rowValues = new LinkedHashMap<>(model.getRowValues(rowIndex, summaryColumns));
            rowValues.put("__rowIndex", Integer.toString(rowIndex));
            rows.add(rowValues);
        }
        return rows;
    }

    private int parsePage(String rawPage, int totalPages) {
        int page = 1;

        if (rawPage != null) {
            try {
                page = Integer.parseInt(rawPage);
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }

        if (page < 1) {
            return 1;
        }
        if (page > totalPages) {
            return totalPages;
        }
        return page;
    }

    private Integer parseSelectedRow(String rawRow, int totalRows) {
        if (rawRow == null) {
            return null;
        }

        try {
            int row = Integer.parseInt(rawRow);
            if (row < 0 || row >= totalRows) {
                return null;
            }
            return row;
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}