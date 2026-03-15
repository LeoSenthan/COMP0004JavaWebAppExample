<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private String escapeHtml(String value) {
    if (value == null) {
      return "";
    }
    return value.replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;");
  }
%>

<html>
<head>
  <jsp:include page="/meta.jsp"/>
  <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <h2>Patients:</h2>

  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String message = (String) request.getAttribute("message");
    String searchQueryString = (String) request.getAttribute("searchQueryString");
    Integer selectedRow = (Integer) request.getAttribute("selectedRow");
    List<Integer> matchingRows = (List<Integer>) request.getAttribute("matchingRows");
    List<String> summaryColumns = (List<String>) request.getAttribute("summaryColumns");
    List<Map<String, String>> patientRows = (List<Map<String, String>>) request.getAttribute("patientRows");
    Map<String, String> selectedPatient = (Map<String, String>) request.getAttribute("selectedPatient");
    Map<String, String> searchCriteria = (Map<String, String>) request.getAttribute("searchCriteria");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");

    if (searchQueryString == null) searchQueryString = "";
    if (matchingRows == null) matchingRows = java.util.Collections.emptyList();
    if (summaryColumns == null) summaryColumns = java.util.Collections.emptyList();
    if (patientRows == null) patientRows = java.util.Collections.emptyList();
    if (searchCriteria == null) searchCriteria = java.util.Collections.emptyMap();
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;

    if (message != null) {
  %>
      <p class="message message-success"><%= escapeHtml(message) %></p>
  <%
    }

    if (errorMessage != null) {
  %>
      <p class="message message-error"><%= escapeHtml(errorMessage) %></p>
  <%
    }

    String preservedSearchParameters = searchQueryString.isEmpty() ? "" : "&" + searchQueryString;
  %>

  <form method="get" action="patientList" class="search-form">
    <table class="data-table search-table">
      <tr>
        <% for (String col : summaryColumns) { %>
          <th><%= col %></th>
        <% } %>
      </tr>
      <tr>
        <% for (String col : summaryColumns) {
             String value = searchCriteria.get(col);
             if (value == null) value = "";
             String valueEscaped = escapeHtml(value);
        %>
          <td>
            <input type="text" name="<%= col %>" value="<%= valueEscaped %>" placeholder="<%= col %>" />
          </td>
        <% } %>
      </tr>
    </table>
    <div class="search-actions">
      <input type="submit" value="Search" />
      <a href="patientList">Clear</a>
    </div>
  </form>

  <%
    if (!matchingRows.isEmpty()) {
  %>

  <table class="data-table">
    <tr>
      <% for (String col : summaryColumns) { %>
        <th><%= col %></th>
      <% } %>
    </tr>

    <% for (Map<String, String> row : patientRows) { %>
      <% String rowIndex = row.get("__rowIndex"); %>
      <tr>
        <% for (String col : summaryColumns) { %>
          <td>
            <a href="patientList?page=<%= currentPage %><%= preservedSearchParameters %>&selectedRow=<%= rowIndex %>#details"><%= escapeHtml(row.get(col)) %></a>
          </td>
        <% } %>
      </tr>
    <% } %>
  </table>

  <p>Showing <%= matchingRows.size() %> matching records.</p>
  <p>Page <%= currentPage %> of <%= totalPages %></p>
  <div class="pagination-links">
    <% if (currentPage > 1) { %>
      <a href="patientList?page=<%= currentPage - 1 %><%= preservedSearchParameters %>">Previous</a>
    <% } %>
    <% if (currentPage < totalPages) { %>
      <% if (currentPage > 1) { %> | <% } %>
      <a href="patientList?page=<%= currentPage + 1 %><%= preservedSearchParameters %>">Next</a>
    <% } %>
  </div>

  <form method="get" action="patientList" class="page-form">
    <% for (String col : summaryColumns) {
         String value = searchCriteria.get(col);
         if (value == null) {
           value = "";
         }
         String valueEscaped = escapeHtml(value);
    %>
      <input type="hidden" name="<%= col %>" value="<%= valueEscaped %>" />
    <% } %>
    <label for="pageInput">Go to page:</label>
    <input id="pageInput" type="number" name="page" min="1" max="<%= totalPages %>" value="<%= currentPage %>" required />
    <input type="submit" value="Go" />
  </form>

  <%
    if (selectedRow != null) {
  %>
  <h3 id="details">Patient Details</h3>
  <table class="data-table">
    <tr>
      <th>Field</th>
      <th>Value</th>
    </tr>
    <% for (Map.Entry<String, String> entry : selectedPatient.entrySet()) { %>
      <tr>
        <td><%= escapeHtml(entry.getKey()) %></td>
        <td><%= escapeHtml(entry.getValue()) %></td>
      </tr>
    <% } %>
  </table>

  <h3>Edit Patient</h3>
  <form method="post" action="updatePatient" class="patient-edit-form">
    <input type="hidden" name="rowIndex" value="<%= selectedRow %>" />
    <input type="hidden" name="page" value="<%= currentPage %>" />
    <input type="hidden" name="returnQuery" value="<%= escapeHtml(searchQueryString) %>" />
    <table class="data-table">
      <tr>
        <th>Field</th>
        <th>Value</th>
      </tr>
      <% for (Map.Entry<String, String> entry : selectedPatient.entrySet()) { %>
      <tr>
        <td><%= escapeHtml(entry.getKey()) %></td>
        <td>
          <input class="patient-edit-input" type="text" name="<%= escapeHtml(entry.getKey()) %>" value="<%= escapeHtml(entry.getValue()) %>" />
        </td>
      </tr>
      <% } %>
    </table>
    <div class="search-actions">
      <input type="submit" value="Save Changes" />
    </div>
  </form>

  <form method="post" action="deletePatient" class="delete-form">
    <input type="hidden" name="rowIndex" value="<%= selectedRow %>" />
    <input type="hidden" name="page" value="<%= currentPage %>" />
    <input type="hidden" name="returnQuery" value="<%= escapeHtml(searchQueryString) %>" />
    <input type="submit" value="Delete Patient" onclick="return confirm('Delete this patient?');" />
  </form>
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