<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
  <jsp:include page="/meta.jsp"/>
  <title>Patient Operations</title>
</head>
<body>
<jsp:include page="/header.jsp"/>
<div class="main">
  <h2>Requirement 7 Operations</h2>

  <%
    String errorMessage = (String) request.getAttribute("errorMessage");
    Integer totalPatients = (Integer) request.getAttribute("totalPatients");
    String oldestName = (String) request.getAttribute("oldestName");
    String oldestBirthdate = (String) request.getAttribute("oldestBirthdate");
    String oldestCity = (String) request.getAttribute("oldestCity");
    Integer sameCityCount = (Integer) request.getAttribute("sameCityCount");
    Map<String, Integer> topStates = (Map<String, Integer>) request.getAttribute("topStates");
    Map<String, Integer> genderDistribution = (Map<String, Integer>) request.getAttribute("genderDistribution");

    if (errorMessage != null) {
  %>
      <p style="color: red;"><%= errorMessage %></p>
  <%
    } else {
  %>

  <h3>Summary</h3>
  <ul>
    <li>Total patients: <%= totalPatients %></li>
    <li>Oldest patient: <%= oldestName %> (birthdate: <%= oldestBirthdate %>)</li>
    <li>People living in the same city as the oldest patient (<%= oldestCity %>): <%= sameCityCount %></li>
  </ul>

  <h3>Top 5 States by Patient Count</h3>
  <table>
    <tr>
      <th>State</th>
      <th>Count</th>
    </tr>
    <%
      if (topStates != null) {
        for (Map.Entry<String, Integer> entry : topStates.entrySet()) {
    %>
      <tr>
        <td><%= entry.getKey() %></td>
        <td><%= entry.getValue() %></td>
      </tr>
    <%
        }
      }
    %>
  </table>

  <h3>Distribution by Gender</h3>
  <table>
    <tr>
      <th>Gender</th>
      <th>Count</th>
    </tr>
    <%
      if (genderDistribution != null) {
        for (Map.Entry<String, Integer> entry : genderDistribution.entrySet()) {
    %>
      <tr>
        <td><%= entry.getKey() %></td>
        <td><%= entry.getValue() %></td>
      </tr>
    <%
        }
      }
    %>
  </table>

  <%
    }
  %>
</div>
<jsp:include page="/footer.jsp"/>
</body>
</html>
