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
import java.util.Map;

@WebServlet("/analytics")
public class OperationsServlet extends HttpServlet{

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{

        try{
            Model model = Model.getInstance();
            String message = request.getParameter("message");
            String errorMessage = request.getParameter("errorMessage");

            if (message != null && !message.isBlank()){
                request.setAttribute("message", message);
            }
            if (errorMessage != null && !errorMessage.isBlank()){
                request.setAttribute("errorMessage", errorMessage);
            }

            // --- Totals ---
            int totalPatients = model.getTotalPatients();

            // --- Oldest patient ---
            int oldestRow = model.getOldestRowIndex();
            String oldestName = "N/A";
            String oldestBirthdate = "N/A";
            String oldestCity = "N/A";
            int sameCityCount = 0;
            if (oldestRow >= 0){
                oldestName = model.getPatientDisplayName(oldestRow);
                oldestBirthdate = model.getData().getValue("BIRTHDATE", oldestRow);
                oldestCity = model.getData().getValue("CITY", oldestRow);
                sameCityCount = model.countPatientsInCity(oldestCity);
            }

            // --- Youngest patient ---
            int youngestRow = model.getYoungestRowIndex();
            String youngestName = "N/A";
            String youngestBirthdate = "N/A";
            String youngestCity = "N/A";
            if (youngestRow >= 0){
                youngestName = model.getPatientDisplayName(youngestRow);
                youngestBirthdate = model.getData().getValue("BIRTHDATE", youngestRow);
                youngestCity = model.getData().getValue("CITY", youngestRow);
            }

            // --- Field count lookup (form POST or GET with params) ---
            String lookupField = request.getParameter("lookupField");
            String lookupValue = request.getParameter("lookupValue");
            Integer lookupCount = null;
            if (lookupField != null && !lookupField.isBlank()
                    && lookupValue != null && !lookupValue.isBlank()){
                lookupCount = model.countPatientsByFieldValue(lookupField, lookupValue);
            }

            // --- Distributions ---
            Map<String, Integer> raceCounts = model.getRaceDistribution();
            Map<String, Integer> topStates = model.getTopDistributionByColumn("STATE", 5);
            Map<String, Integer> genderDistribution = model.getTopDistributionByColumn("GENDER", 10);
            Map<String, Integer> ageBandDistribution = model.getAgeBandDistribution();
            int maxAgeBandCount = 0;
            for (Integer count : ageBandDistribution.values()){
                if (count != null && count > maxAgeBandCount){
                    maxAgeBandCount = count;
                }
            }

            request.setAttribute("totalPatients", totalPatients);
            request.setAttribute("oldestName", oldestName);
            request.setAttribute("oldestBirthdate", oldestBirthdate);
            request.setAttribute("oldestCity", oldestCity);
            request.setAttribute("sameCityCount", sameCityCount);
            request.setAttribute("youngestName", youngestName);
            request.setAttribute("youngestBirthdate", youngestBirthdate);
            request.setAttribute("youngestCity", youngestCity);
            request.setAttribute("lookupField", lookupField);
            request.setAttribute("lookupValue", lookupValue);
            request.setAttribute("lookupCount", lookupCount);
            request.setAttribute("raceCounts", raceCounts);
            request.setAttribute("topStates", topStates);
            request.setAttribute("genderDistribution", genderDistribution);
            request.setAttribute("ageBandDistribution", ageBandDistribution);
            request.setAttribute("maxAgeBandCount", maxAgeBandCount);

        } catch (Exception e){
            request.setAttribute("errorMessage", "Unable to compute analytics: " + e.getMessage());
        }

        ServletContext context = getServletContext();
        RequestDispatcher dispatch = context.getRequestDispatcher("/operations.jsp");
        dispatch.forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException{
        doGet(request, response);
    }
}

