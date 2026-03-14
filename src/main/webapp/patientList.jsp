<%@ page import="uk.ac.ucl.model.DataFrame" %>
<%@ page import="uk.ac.ucl.model.ModelFactory" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <jsp:include page="/meta.jsp"/>
  <title>Patient Data App</title>
  <style>
    table {
      border-collapse: collapse;
      width: 100%;
    }
    th, td {
      border: 1px solid #aaa;
      padding: 8px;
      text-align: left;
    }
    th {
      background-color: #ddd;
    }
  </style>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <h2>Patients:</h2>

  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorMessage != null) {
  %>
      <p style="color: red;"><%= errorMessage %></p>
  <%
    }

    DataFrame df = (DataFrame) request.getAttribute("dataframe");
    if (df == null) {
      try {
        df = ModelFactory.getModel().getData();
      } catch (Exception e) {
        errorMessage = "Error loading data: " + e.getMessage();
      }
    }

    if (df != null && df.getRowCount() > 0) {
      // Only show the important columns
      String[] columnsToShow = {"FIRST", "LAST", "CITY", "STATE", "BIRTHDATE"};
  %>

  <table>
    <tr>
      <% for (String col : columnsToShow) { %>
        <th><%= col %></th>
      <% } %>
    </tr>

    <% for (int i = 0; i < df.getRowCount(); i++) { %>
      <tr>
        <% for (String col : columnsToShow) { %>
          <td><%= df.getValue(col, i) %></td>
        <% } %>
      </tr>
    <% } %>
  </table>

  <%
    } else {
  %>
      <p>No patient data available.</p>
  <%
    }
  %>

</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>