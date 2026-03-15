<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <jsp:include page="/meta.jsp"/>
  <title>Error</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <%
    String errorTitle = (String) request.getAttribute("errorTitle");
    String errorMessage = (String) request.getAttribute("errorMessage");
    if (errorTitle == null || errorTitle.isBlank()) {
      errorTitle = "Something went wrong";
    }
    if (errorMessage == null || errorMessage.isBlank()) {
      errorMessage = "The request could not be completed.";
    }
  %>
  <h2><%= errorTitle %></h2>
  <p class="message message-error"><%= errorMessage %></p>
  <p><a href="index.html">Return to the home page</a></p>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>