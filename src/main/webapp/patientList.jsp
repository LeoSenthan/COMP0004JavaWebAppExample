<%@ page import="uk.ac.ucl.model.DataFrame" %>
<%@ page import="java.util.List" %>
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
    DataFrame df = (DataFrame) request.getAttribute("dataframe");
    List<Integer> matchingRows = (List<Integer>) request.getAttribute("matchingRows");
    String search = (String) request.getAttribute("search");
    String searchEncoded = (String) request.getAttribute("searchEncoded");
    Integer selectedRow = (Integer) request.getAttribute("selectedRow");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer startIndex = (Integer) request.getAttribute("startIndex");
    Integer endIndex = (Integer) request.getAttribute("endIndex");

    if (search == null) search = "";
    if (searchEncoded == null) searchEncoded = "";
    if (matchingRows == null) matchingRows = java.util.Collections.emptyList();
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (startIndex == null) startIndex = 0;
    if (endIndex == null) endIndex = matchingRows.size();

    if (errorMessage != null) {
  %>
      <p style="color: red;"><%= errorMessage %></p>
  <%
    }

    String searchEscaped = search.replace("&", "&amp;").replace("<", "&lt;").replace("\"", "&quot;");
  %>

  <form method="get" action="patientList" style="margin-bottom: 10px;">
    <label for="searchInput">Search keyword:</label>
    <input id="searchInput" type="text" name="search" value="<%= searchEscaped %>" />
    <input type="submit" value="Search" />
  </form>

  <%
    if (df != null && !matchingRows.isEmpty()) {
      // Show 8 relevant summary fields in the list view.
      String[] columnsToShow = {"ID", "FIRST", "LAST", "GENDER", "BIRTHDATE", "CITY", "STATE", "ZIP"};
  %>

  <table>
    <tr>
      <% for (String col : columnsToShow) { %>
        <th><%= col %></th>
      <% } %>
    </tr>

    <% for (int i = startIndex; i < endIndex; i++) { %>
      <% int rowIndex = matchingRows.get(i); %>
      <tr>
        <% for (int j = 0; j < columnsToShow.length; j++) { %>
          <% String col = columnsToShow[j]; %>
          <td>
            <% if ("FIRST".equals(col) || "LAST".equals(col)) { %>
              <a href="patientList?page=<%= currentPage %>&search=<%= searchEncoded %>&selectedRow=<%= rowIndex %>#details"><%= df.getValue(col, rowIndex) %></a>
            <% } else { %>
              <%= df.getValue(col, rowIndex) %>
            <% } %>
          </td>
        <% } %>
      </tr>
    <% } %>
  </table>

  <p>Showing <%= matchingRows.size() %> matching records.</p>
  <p>Page <%= currentPage %> of <%= totalPages %></p>
  <div>
    <% if (currentPage > 1) { %>
      <a href="patientList?page=<%= currentPage - 1 %>&search=<%= searchEncoded %>">Previous</a>
    <% } %>
    <% if (currentPage < totalPages) { %>
      <% if (currentPage > 1) { %> | <% } %>
      <a href="patientList?page=<%= currentPage + 1 %>&search=<%= searchEncoded %>">Next</a>
    <% } %>
  </div>

  <form method="get" action="patientList" style="margin-top: 10px;">
    <input type="hidden" name="search" value="<%= searchEscaped %>" />
    <label for="pageInput">Go to page:</label>
    <input id="pageInput" type="number" name="page" min="1" max="<%= totalPages %>" value="<%= currentPage %>" required />
    <input type="submit" value="Go" />
  </form>

  <%
    if (selectedRow != null) {
  %>
  <h3 id="details">Patient Details</h3>
  <table>
    <tr>
      <th>Field</th>
      <th>Value</th>
    </tr>
    <% for (String col : df.getColumnNames()) { %>
      <tr>
        <td><%= col %></td>
        <td><%= df.getValue(col, selectedRow) %></td>
      </tr>
    <% } %>
  </table>
  <%
    }
  %>

  <%
    } else {
  %>
      <p>No patient records match your search.</p>
  <%
    }
  %>

</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>