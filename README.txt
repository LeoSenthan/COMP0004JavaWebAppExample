Project Overview
Patient data is loaded from CSV into a DataFrame model and exposed through an MVC design.

Requirements:
1: Implemented Column with column name and row storage, including getName, getSize, getRowValue, setRowValue, and addRowValue.
2: Implemented DataFrame with addColumn, getColumnNames, getRowCount, getValue, putValue, and addValue.
3: Implemented DataLoader to read CSV from resources and populate a DataFrame.
4: Implemented singleton Model that manages loading, searching, analytics, persistence, and export.
5: Implemented servlets and JSP pages for patient list, patient details, add/edit/delete flow, search, analytics, and error handling.
6: Implemented search logic in the Model.
7: Implemented analytics including oldest/youngest patient, place-based counts, race/ethnicity distribution, and gender/state summaries.
8: Implemented add, edit, and delete patient rows with CSV write-back.
9: Implemented JSONWriter and a Save to JSON option in the web app.
10: Implemented an age distribution chart on the Analytics page.

Features:
Patient list pagination with click-through to full patient details.
Add page supports full patient record entry across all 20 columns.
Edit and delete actions are available from the selected patient details section.
Data can be saved to CSV and exported to JSON.
Analytics page includes tabular summaries and an age distribution bar chart.

How To Run:
- Build: mvn compile
- Run: mvn clean compile exec:exec
- Default URL: http://localhost:8080/