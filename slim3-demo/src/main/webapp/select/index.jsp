<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>select Index</title>
<link rel="stylesheet" type="text/css" href="/css/global.css" />
</head>
<body>
Source:
<ul>
<li><a href="https://github.com/piisu/slim3v2/tree/master/slim3-demo/src/main/java/slim3/demo/controller/select/IndexController.java">IndexController</a></li>
<li><a href="https://github.com/piisu/slim3v2/tree/master/slim3-demo/src/main/webapp/select/index.jsp">index.jsp</a></li>
</ul>
<hr />

<form method="post" action="${f:url('')}">
aaa:${f:h(aaa)}<br />
<select name="aaa">
<option ${f:select("aaa", "1")}>One</option>
<option ${f:select("aaa", "2")}>Two</option>
<option ${f:select("aaa", "3")}>Three</option>
</select><br />
<input type="submit" value="Submit"/>
</form>
</body>
</html>
