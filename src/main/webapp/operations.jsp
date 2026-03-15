<%@ page import="java.util.Map" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private String escapeHtml(String value) {
    if (value == null) {
      return "";
    }
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;");
  }
%>

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
    String message = (String) request.getAttribute("message");
    String errorMessage = (String) request.getAttribute("errorMessage");
    Integer totalPatients = (Integer) request.getAttribute("totalPatients");
    String oldestName = (String) request.getAttribute("oldestName");
    String oldestBirthdate = (String) request.getAttribute("oldestBirthdate");
    String oldestCity = (String) request.getAttribute("oldestCity");
    Integer sameCityCount = (Integer) request.getAttribute("sameCityCount");
    String youngestName = (String) request.getAttribute("youngestName");
    String youngestBirthdate = (String) request.getAttribute("youngestBirthdate");
    String youngestCity = (String) request.getAttribute("youngestCity");
    String lookupField = (String) request.getAttribute("lookupField");
    String lookupValue = (String) request.getAttribute("lookupValue");
    Integer lookupCount = (Integer) request.getAttribute("lookupCount");
    Map<String, Integer> raceCounts = (Map<String, Integer>) request.getAttribute("raceCounts");
    Map<String, Integer> topStates = (Map<String, Integer>) request.getAttribute("topStates");
    Map<String, Integer> genderDistribution = (Map<String, Integer>) request.getAttribute("genderDistribution");

    if (message != null) {
  %>
      <p class="message message-success"><%= escapeHtml(message) %></p>
  <%
    }

    if (errorMessage != null) {
  %>
      <p class="message message-error"><%= escapeHtml(errorMessage) %></p>
  <%
    } else {
  %>

  <h3>Export Data</h3>
  <p>Save the current patient data to a JSON file generated from the DataFrame.</p>
  <form method="post" action="saveJson" class="inline-form">
    <button type="submit">Save to JSON</button>
  </form>

  <h3>Summary Statistics</h3>
  <table class="data-table">
    <tr>
      <th>Statistic</th>
      <th>Value</th>
    </tr>
    <tr>
      <td>Total patients</td>
      <td><%= totalPatients %></td>
    </tr>
    <tr>
      <td>Oldest patient</td>
      <td><%= oldestName %> &mdash; born: <%= oldestBirthdate %></td>
    </tr>
    <tr>
      <td>Oldest patient's city</td>
      <td><%= oldestCity %> (<%= sameCityCount %> patients in this city)</td>
    </tr>
    <tr>
      <td>Youngest patient</td>
      <td><%= youngestName %> &mdash; born: <%= youngestBirthdate %></td>
    </tr>
    <tr>
      <td>Youngest patient's city</td>
      <td><%= youngestCity %></td>
    </tr>
  </table>

  <h3>Field Count Lookup</h3>
  <p>Select a field and enter a value to count how many patients match.</p>
  <form method="post" action="operations" class="ops-lookup-form">
    <label>Field:
      <select name="lookupField">
        <option value="CITY"<%= "CITY".equals(lookupField) ? " selected" : "" %>>City</option>
        <option value="STATE"<%= "STATE".equals(lookupField) ? " selected" : "" %>>State</option>
        <option value="RACE"<%= "RACE".equals(lookupField) ? " selected" : "" %>>Race</option>
        <option value="ETHNICITY"<%= "ETHNICITY".equals(lookupField) ? " selected" : "" %>>Ethnicity</option>
        <option value="GENDER"<%= "GENDER".equals(lookupField) ? " selected" : "" %>>Gender</option>
        <option value="ZIP"<%= "ZIP".equals(lookupField) ? " selected" : "" %>>ZIP Code</option>
        <option value="MARITAL"<%= "MARITAL".equals(lookupField) ? " selected" : "" %>>Marital Status</option>
      </select>
    </label>
    <label>Value:
      <input type="text" name="lookupValue" value="<%= escapeHtml(lookupValue) %>" />
    </label>
    <button type="submit">Count</button>
  </form>
  <%
    if (lookupCount != null) {
  %>
    <p class="message">Patients where <strong><%= escapeHtml(lookupField) %></strong> =
      &ldquo;<strong><%= escapeHtml(lookupValue) %></strong>&rdquo;: <strong><%= lookupCount %></strong></p>
  <%
    }
  %>

  <h3>Race / Ethnicity Distribution</h3>
  <table class="data-table">
    <tr>
      <th>Race</th>
      <th>Count</th>
      <th>Percentage</th>
    </tr>
    <%
      if (raceCounts != null && totalPatients != null && totalPatients > 0) {
        for (Map.Entry<String, Integer> entry : raceCounts.entrySet()) {
          double pct = Math.round(entry.getValue() * 1000.0 / totalPatients) / 10.0;
    %>
    <tr>
      <td><%= escapeHtml(entry.getKey()) %></td>
      <td><%= entry.getValue() %></td>
      <td><%= pct %>%</td>
    </tr>
    <%
        }
      }
    %>
  </table>

  <h3>Top 5 States by Patient Count</h3>
  <table class="data-table">
    <tr>
      <th>State</th>
      <th>Count</th>
    </tr>
    <%
      if (topStates != null) {
        for (Map.Entry<String, Integer> entry : topStates.entrySet()) {
    %>
    <tr>
      <td><%= escapeHtml(entry.getKey()) %></td>
      <td><%= entry.getValue() %></td>
    </tr>
    <%
        }
      }
    %>
  </table>

  <h3>Gender Distribution</h3>
  <table class="data-table">
    <tr>
      <th>Gender</th>
      <th>Count</th>
    </tr>
    <%
      if (genderDistribution != null) {
        for (Map.Entry<String, Integer> entry : genderDistribution.entrySet()) {
    %>
    <tr>
      <td><%= escapeHtml(entry.getKey()) %></td>
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
