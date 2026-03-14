package uk.ac.ucl.servlets;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import uk.ac.ucl.model.Model;
import uk.ac.ucl.model.ModelFactory;

import java.io.IOException;
import java.util.Map;

@WebServlet("/operations")
public class OperationsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Model model = ModelFactory.getModel();

            int totalPatients = model.getTotalPatients();
            int oldestRow = model.getOldestRowIndex();

            String oldestName = "N/A";
            String oldestBirthdate = "N/A";
            String oldestCity = "N/A";
            int sameCityCount = 0;

            if (oldestRow >= 0) {
                oldestName = model.getPatientDisplayName(oldestRow);
                oldestBirthdate = model.getData().getValue("BIRTHDATE", oldestRow);
                oldestCity = model.getData().getValue("CITY", oldestRow);
                sameCityCount = model.countPatientsInCity(oldestCity);
            }

            Map<String, Integer> topStates = model.getTopDistributionByColumn("STATE", 5);
            Map<String, Integer> genderDistribution = model.getTopDistributionByColumn("GENDER", 10);

            request.setAttribute("totalPatients", totalPatients);
            request.setAttribute("oldestName", oldestName);
            request.setAttribute("oldestBirthdate", oldestBirthdate);
            request.setAttribute("oldestCity", oldestCity);
            request.setAttribute("sameCityCount", sameCityCount);
            request.setAttribute("topStates", topStates);
            request.setAttribute("genderDistribution", genderDistribution);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Unable to compute operations: " + e.getMessage());
        }

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/operations.jsp");
        dispatch.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
