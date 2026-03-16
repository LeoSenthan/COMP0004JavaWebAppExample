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
import java.util.Collections;
import java.util.List;

@WebServlet("/runsearch")
public class SearchServlet extends HttpServlet{

  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    doPost(request, response);
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
    String searchString = request.getParameter("searchstring");

    try{
      Model model = Model.getInstance();
      String trimmedSearch = searchString == null ? "" : searchString.trim();

      request.setAttribute("searchString", trimmedSearch);

      if (trimmedSearch.isEmpty()){
        request.setAttribute("errorMessage", "Please enter a search term.");
        request.setAttribute("result", Collections.emptyList());
      } else{
        List<String> searchResult = model.searchFor(trimmedSearch);
        request.setAttribute("result", searchResult);
      }

      forward(request, response, "/searchResult.jsp");
    } catch (RuntimeException e){
      response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      request.setAttribute("errorTitle", "Search unavailable");
      request.setAttribute("errorMessage", "Unable to load patient data. Please try again later.");
      forward(request, response, "/error.jsp");
    }
  }

  private void forward(HttpServletRequest request, HttpServletResponse response, String path)
      throws ServletException, IOException{
    ServletContext context = getServletContext();
    RequestDispatcher dispatch = context.getRequestDispatcher(path);
    dispatch.forward(request, response);
  }
}
