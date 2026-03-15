<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <jsp:include page="/meta.jsp"/>
  <title>Patient Data App</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <h1>Search Result</h1>
  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String searchString = (String) request.getAttribute("searchString");
    if (errorMessage != null)
    {
  %>
      <p class="message message-error"><%= errorMessage %></p>
  <%
    }
    List<String> patients = (List<String>) request.getAttribute("result");
    if (searchString == null) {
      searchString = "";
    }
    if (!searchString.isEmpty()) {
  %>
    <p>Search term: <strong><%= searchString %></strong></p>
  <%
    }
    if (patients != null && patients.size() != 0)
    {
    %>
    <ul>
      <%
        for (String patient : patients)
        {
      %>
      <li><%= patient %></li>
     <% }
    } else if (errorMessage == null)
    {%>
      <p>Nothing found</p>
  <%}%>
  </ul>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>