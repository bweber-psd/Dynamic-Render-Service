<%@page import="com.perceptivesoftware.renderservice.util.ApplicationVersion"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<title>SAPERION Render Service</title>
	<link rel="stylesheet" type="text/css" href="css/stylesheet.css" />
</head>
<body>
    <h1>SAPERION Render Service version <%= ApplicationVersion.getVersionString() %></h1>
    <p><a href="webapi/application.wadl">WADL</a></p>
    <p><a href="test-form.html">Test Form</a></p>
    <p><a href="documentation.html">Documentation</a></p>
</body>
</html>
