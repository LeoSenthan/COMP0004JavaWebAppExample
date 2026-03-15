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
  <title>Add Patient</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <h2>Add Patient</h2>
  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    List<String> allColumns = (List<String>) request.getAttribute("allColumns");
    Map<String, String> patientValues = (Map<String, String>) request.getAttribute("patientValues");

    if (allColumns == null) allColumns = java.util.Collections.emptyList();
    if (patientValues == null) patientValues = java.util.Collections.emptyMap();
    if (errorMessage != null) {
  %>
    <p class="message message-error"><%= escapeHtml(errorMessage) %></p>
  <%
    }
  %>

  <form method="post" action="add" class="patient-edit-form">
    <table class="data-table">
      <tr>
        <th>Field</th>
        <th>Value</th>
      </tr>
      <% for (String columnName : allColumns) {
           String value = patientValues.get(columnName);
      %>
      <tr>
        <td><%= escapeHtml(columnName) %></td>
        <td><input class="patient-edit-input" type="text" name="<%= escapeHtml(columnName) %>" value="<%= escapeHtml(value) %>" /></td>
      </tr>
      <% } %>
    </table>
    <div class="search-actions">
      <input type="submit" value="Add Patient" />
    </div>
  </form>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>