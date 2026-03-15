COMP0004 Coursework Summary

This web application implements the core coursework requirements using Java, Maven, embedded Tomcat, servlets, JSP, HTML, and CSS.

Implemented features:
- Column and DataFrame classes for storing CSV data in a general tabular structure.
- DataLoader class to load patient CSV data from the resources folder.
- Model class that manages the DataFrame and provides search and summary operations.
- Patient list page with pagination and keyword search.
- Search results page for matching patients.
- Operations page showing total patients, oldest patient, counts by city, top states, and gender distribution.
- Add, edit, and delete patient records with changes written back to CSV.
- JSONWriter class to export the current DataFrame to JSON from the web application.
- Improved error handling for unavailable data and a dedicated error page.

Highlights:
- Search and data operations are implemented in the model, keeping servlets as controllers.
- CSS has been moved into a shared stylesheet rather than being embedded in JSP pages.
- JSP pages now rely more on request attributes prepared by the servlets instead of direct model access.
- The update features now include both CSV persistence and JSON export.

Charts have not yet been implemented.