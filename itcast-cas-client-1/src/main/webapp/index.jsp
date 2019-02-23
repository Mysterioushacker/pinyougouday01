<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>一品优购</title>
</head>
<body>
<%=request.getRemoteUser()%>；欢迎来到一品优购。
---<a href="http://cas.pinyougou.com/logout?service=http://www.itcast.cn">退出</a>
</body>
</html>