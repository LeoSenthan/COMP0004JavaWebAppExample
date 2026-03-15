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

@WebServlet("/saveJson")
public class SaveJsonServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String savedPath = Model.getInstance().exportDataAsJson();
            response.sendRedirect("analytics?message="
                + URLEncoder.encode("JSON saved to " + savedPath, StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            response.sendRedirect("analytics?errorMessage="
                + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }
}