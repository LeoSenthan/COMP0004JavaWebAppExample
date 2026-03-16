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

@WebServlet("/deletePatient")
public class DeletePatientServlet extends HttpServlet{

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        int rowIndex = Integer.parseInt(request.getParameter("rowIndex"));

        try{
            Model.getInstance().deletePatient(rowIndex);
            response.sendRedirect(buildRedirectUrl(request, "Patient deleted", false));
        } catch (RuntimeException e){
            response.sendRedirect(buildRedirectUrl(request, e.getMessage(), true));
        }
    }

    private String buildRedirectUrl(HttpServletRequest request, String message, boolean error){
        StringBuilder url = new StringBuilder("patientList?");
        boolean hasParam = false;

        String page = request.getParameter("page");
        if (page != null && !page.isBlank()){
            url.append("page=").append(URLEncoder.encode(page, StandardCharsets.UTF_8));
            hasParam = true;
        }

        String returnQuery = request.getParameter("returnQuery");
        if (returnQuery != null && !returnQuery.isBlank()){
            if (hasParam){
                url.append('&');
            }
            url.append(returnQuery);
            hasParam = true;
        }

        if (hasParam){
            url.append('&');
        }

        String messageParam = error ? "errorMessage" : "message";
        url.append(messageParam)
            .append('=')
            .append(URLEncoder.encode(message, StandardCharsets.UTF_8));

        return url.toString();
    }
}