<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="io.arusland.allinone.MainApp" %>
<%@ page import="io.arusland.allinone.file.FileItem" %>
<%@ page import="io.arusland.allinone.Message" %>

<%
    MainApp app = new MainApp(request, response);
    app.handleIndex();
%>
<html>
<head>
    <title>Maven dependency helper</title>

    <script src="https://code.jquery.com/jquery-3.0.0.min.js"></script>
    <link rel="stylesheet" type="text/css" href="static/main.css">
</head>
<body>

<div id="messages"></div>

<form action="index.jsp" method="post">
    <p><strong>Edit pom.xml:</strong></p>
    <p><textarea rows="10" cols="145" name="pomContent"><%=app.getPomContent()%></textarea></p>
    <p><input type="submit" name="btnMaven1" value="Resolve" title="Rosolve all dependencies of pom.xml">
        <input type="checkbox" name="useNewMethod" value="true" checked></p>
    </p>

    <input type="hidden" name="path" value="<%=app.getPath()%>"/>
</form>

<%
    if (app.canShowDir()) {
%>

<form action="index.jsp" method="post">
    <h2>DIR: <%=app.getCurrentDirFullPath()%>
    </h2>
    <p>
        <input type="submit" name="btnDelete" value="Delete" onclick="return confirm('Are you sure you want to delete files?');"/>
        <input type="submit" name="btnZip" value="Zip">
    </p>

    <table border="1">
        <tr>
            <td>#</td>
            <td><strong>Name</strong></td>
            <td><strong>Size</strong></td>
            <td><strong>Last modified</strong></td>
        </tr>

        <%
            for (FileItem file : app.getFiles()) {
                if (file.isDirectory()) {
        %>
        <tr>
            <td><input type="checkbox" name="selected[]" value="<%=file.getName()%>"/></td>
            <td><a href="?path=<%=file.getPath()%>"><%=file.getName()%></a></td>
            <td><%=file.getSizeStr()%>
            </td>
            <td><%=file.getDateStr()%>
            </td>
        </tr>
        <%
        } else {
        %>
        <tr>
            <td><input type="checkbox" name="selected[]" value="<%=file.getName()%>"/></td>
            <td><a title="Download" href="download.jsp?path=<%=file.getPath()%>" target="_blank"><%=file.getName()%></a>
            </td>
            <td><%=file.getSizeStr()%>
            </td>
            <td><%=file.getDateStr()%>
            </td>
        </tr>
        <%
                }
            }
        %>
    </table>

    <input type="hidden" name="path" value="<%=app.getPath()%>"/>
</form>
<%
    }
%>

<script>
    var msgs = $("#messages");
    <%
    for (Message msg : app.getMessages()) {
        %>
        msgs.append("<div class='<%=msg.getType()%>'><%=msg.getMessageEscaped()%></div>");
    <%
    }
    %>
</script>
</body>
</html>
