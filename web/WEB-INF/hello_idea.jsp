<%--
  Created by IntelliJ IDEA.
  User: 75744
  Date: 2017/9/20
  Time: 10:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"
           uri="http://www.springframework.org/tags" %>
<html>
<head>
    <title>Hello</title>
</head>
<body>
<p>Hello. This is a dispached page.You are at Server 2</p>
<p>Your session id: ${pageContext.session.id},${Name}</p>

</body>
</html>
