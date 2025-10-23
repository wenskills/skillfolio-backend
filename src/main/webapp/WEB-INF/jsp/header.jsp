<!DOCTYPE html>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:url var="bootstrap_css"
	value="/webjars/bootstrap/5.3.3/css/bootstrap.min.css" />
<c:url var="bootstrap_js"
	value="/webjars/bootstrap/5.3.3/js/bootstrap.min.js" />
<c:url var="css"
	value="/style.css" />


<html>
	<head>
	<meta charset="UTF-8">
	<title>Spring boot application</title>
	<link rel="stylesheet" href="${bootstrap_css}">
	<link rel="stylesheet" href="${css}">
	<script src="${bootstrap_js}"></script>
</head>
<body>
