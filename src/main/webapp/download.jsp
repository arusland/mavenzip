<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="io.arusland.allinone.MainApp" %>
<%@ page import="io.arusland.allinone.file.FileItem" %>
<%@ page import="io.arusland.allinone.Message" %>

<%
    MainApp app = new MainApp(request, response);
    app.handleDownload();
%>
<html>
<head>
    <title>Download file</title>

    <script src="https://code.jquery.com/jquery-3.0.0.min.js"></script>
    <link rel="stylesheet" type="text/css" href="/static/main.css">
</head>
<body>

<div id="messages"></div>

<script>
    var msgs = $("#messages");
    <%
    for (Message msg : app.getMessages()) {
        %>
        msgs.append("<div class='<%=msg.getType()%>'><%=msg.getMessage()%></div>");
    <%
    }
    %>
</script>
</body>
</html>
