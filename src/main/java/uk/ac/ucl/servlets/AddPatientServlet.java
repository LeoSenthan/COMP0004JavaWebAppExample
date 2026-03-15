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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/add")
public class AddPatientServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Model model = Model.getInstance();
        List<String> allColumns = model.getAllColumns();

        request.setAttribute("allColumns", allColumns);
        request.setAttribute("patientValues", getPatientValues(request, allColumns));

        forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Model model = Model.getInstance();
        List<String> allColumns = model.getAllColumns();
        Map<String, String> patientValues = getPatientValues(request, allColumns);

        try {
            int rowIndex = model.addPatient(patientValues);
            response.sendRedirect("patientList?selectedRow=" + rowIndex + "&message=Patient+added#details");
            return;
        } catch (RuntimeException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("allColumns", allColumns);
            request.setAttribute("patientValues", patientValues);
        }

        forward(request, response);
    }

    private Map<String, String> getPatientValues(HttpServletRequest request, List<String> allColumns) {
        Map<String, String> patientValues = new LinkedHashMap<>();
        for (String columnName : allColumns) {
            String value = request.getParameter(columnName);
            patientValues.put(columnName, value == null ? "" : value);
        }
        return patientValues;
    }

    private void forward(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/addPatient.jsp");
        dispatch.forward(request, response);
    }
}