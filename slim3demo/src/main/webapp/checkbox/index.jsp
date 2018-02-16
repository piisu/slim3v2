<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>checkbox Index</title>
<link rel="stylesheet" type="text/css" href="/css/global.css" />
</head>
<body>
Source:
<ul>
<li><a href="https://github.com/piisu/slim3v2/tree/master/slim3demo/src/main/java/slim3/demo/controller/checkbox/IndexController.java">IndexController</a></li>
<li><a href="https://github.com/piisu/slim3v2/tree/master/slim3demo/src/main/webapp/checkbox/index.jsp">index.jsp</a></li>
</ul>
<hr />

<form method="post" action="${f:url('')}">
aaa:${f:h(aaa)}<br />
<input type="checkbox" ${f:checkbox("aaa")}/><br />
<input type="submit" value="Submit"/>
</form>
</body>
</html>
