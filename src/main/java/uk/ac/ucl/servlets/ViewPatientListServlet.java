package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import uk.ac.ucl.model.DataFrame;
import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/patientList")
public class ViewPatientListServlet extends HttpServlet {

    private static final int PAGE_SIZE = 50;

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Model model = ModelFactory.getModel();
            DataFrame df = model.getData();
            String search = request.getParameter("search");
            List<Integer> matchingRows = model.searchRowIndexes(search);
            Integer selectedRow = parseSelectedRow(request.getParameter("selectedRow"), df.getRowCount());

            request.setAttribute("dataframe", df);
            request.setAttribute("search", search == null ? "" : search.trim());
            request.setAttribute("searchEncoded", URLEncoder.encode(search == null ? "" : search.trim(), StandardCharsets.UTF_8));
            request.setAttribute("matchingRows", matchingRows);

            if (selectedRow != null && matchingRows.contains(selectedRow)) {
                request.setAttribute("selectedRow", selectedRow);
            }

            int totalRows = matchingRows.size();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalRows / PAGE_SIZE));
            int currentPage = parsePage(request.getParameter("page"), totalPages);
            int startIndex = (currentPage - 1) * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, totalRows);

            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("startIndex", startIndex);
            request.setAttribute("endIndex", endIndex);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to load patient data. Please try again later.");
        }

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/patientList.jsp");
        dispatch.forward(request, response);
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