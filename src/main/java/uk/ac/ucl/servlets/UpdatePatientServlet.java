package uk.ac.ucl.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import uk.ac.ucl.model.Model;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/updatePatient")
public class UpdatePatientServlet extends HttpServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        Model model = Model.getInstance();
        List<String> allColumns = model.getAllColumns();
        int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));
        Map<String, String> patientValues = new LinkedHashMap<>();

        for (String columnName : allColumns){
            String value = request.getParameter(columnName);
            patientValues.put(columnName, value == null ? "" : value);
        }

        try{
            model.updatePatient(rowIndex, patientValues);
            response.sendRedirect(buildRedirectUrl(request, rowIndex, "Patient updated", false));
        } catch (RuntimeException e){
            response.sendRedirect(buildRedirectUrl(request, rowIndex, e.getMessage(), true));
        }
    }

    private String buildRedirectUrl(HttpServletRequest request, int rowIndex, String message, boolean error){
        StringBuilder url = new StringBuilder("patientList?selectedRow=").append(rowIndex);
        appendOptionalQuery(url, request.getParameter("page"), "page");

        String returnQuery = request.getParameter("returnQuery");
        if (returnQuery != null && !returnQuery.isBlank()){
            url.append('&').append(returnQuery);
        }

        String messageParam = error ? "errorMessage" : "message";
        url.append('&')
            .append(messageParam)
            .append('=')
            .append(URLEncoder.encode(message, StandardCharsets.UTF_8));

        return url.append("#details").toString();
    }

    private void appendOptionalQuery(StringBuilder url, String value, String paramName){
        if (value != null && !value.isBlank()){
            url.append('&')
                .append(paramName)
                .append('=')
                .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
        }
    }
}