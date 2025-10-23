<%@ include file="/WEB-INF/jsp/header.jsp"%>

<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<c:url var="movies" value="/movies" />

<div class="container">
	<h1 class="p-3">Modifier un film</h1>

	<form:form method="post" modelAttribute="movie">

		<div class="mb-3">
			<label>Nom :</label>
			<form:input path="name" cssClass="form-control"
				cssErrorClass="form-control is-invalid" />
			<form:errors path="name" cssClass="mt-1 alert alert-warning" element="div" />
		</div>
		<div class="mb-3">
			<label>Année :</label>
			<form:input path="year" cssClass="form-control"
				cssErrorClass="form-control is-invalid" />
			<form:errors path="year" cssClass="mt-1 alert alert-warning" element="div" />
		</div>
		<div class="mb-3">
			<label>Description :</label>
			<form:textarea path="description" rows="10" cols="50"
				cssClass="form-control" cssErrorClass="form-control is-invalid" />
			<form:errors path="description" cssClass="mt-1 alert alert-warning"
				element="div" />
		</div>
		<div class="mb-3">
			<button type="submit" class="btn btn-primary">Enregistrer</button>
			<a class="ms-2 btn btn-primary" href="${movies}">Annuler</a>
		</div>
	</form:form>
</div>

<%@ include file="/WEB-INF/jsp/footer.jsp"%>
