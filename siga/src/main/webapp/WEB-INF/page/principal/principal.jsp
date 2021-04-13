<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="32kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://localhost/libstag" prefix="f"%>
<siga:pagina titulo="P&aacute;gina Inicial"
	incluirJs="/siga/javascript/principal.js">

	<div class="container-fluid content">
		<c:if test="${not empty acessoAnteriorData}">
			<div class="row">
				<div class="col">
					<p id="bem-vindo" class="alert alert-success mt-3 mb-0">Último
						acesso em ${acessoAnteriorData} no endereço
						${acessoAnteriorMaquina}.</p>
					<script>
						setTimeout(function() {
							$('#bem-vindo').fadeTo(1000, 0, function() {
								$('#bem-vindo').slideUp(1000);
							});
						}, 5000);
					</script>
				</div>
			</div>
		</c:if>
		<c:if test="${not empty mensagem}">
			<div class="row">
				<div style="">
					<p id="mensagem" class="alert alert-success">${mensagem}</p>
					<script>
						setTimeout(function() {
							$('#mensagem').fadeTo(1000, 0, function() {
								$('#mensagem').slideUp(1000);
							});
						}, 5000);
					</script>
				</div>
			</div>
		</c:if>
		<div class="row mt-4">
			<c:if
				test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;DOC:Módulo de Documentos')}">
				<div class="col col-sm-12 col-md-6">
					<div class="card bg-light mb-3">
						<div class="card-header">Expedientes</div>
						<div class="card-body">
							<div id='left'>
								<jsp:include page="loading.jsp" />
							</div>
						</div>
					</div>

					<div class="card bg-light mb-3">
						<div class="card-header">Processos Administrativos</div>
						<div class="card-body">
							<div id='leftbottom'>
								<jsp:include page="loading.jsp" />
							</div>
						</div>
					</div>

					<div class="mt-2">
						<a class="btn btn-primary float-right btn-sm ml-2"
							href="javascript: window.location.href='/sigaex/app/expediente/doc/editar'"
							title="Criar novo expediente ou processo administrativo">
							<fmt:message key = "documento.novo"/></a> <a
							class="btn btn-primary float-right btn-sm ml-2"
							href="javascript: window.location.href='/sigaex/app/expediente/doc/listar?primeiraVez=sim'"
							title="Pesquisar expedientes e processos administrativos">
							<fmt:message key = "documento.pesquisar"/></a>
					</div>

				</div>
			</c:if>
			<c:if
				test="${(f:resource('isWorkflowEnabled') and f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA:Sistema Integrado de Gestão Administrativa;WF:Módulo de Workflow')) or f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;SR') or f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GC:Módulo de Gestão de Conhecimento') or f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;TP:Módulo de Transportes')}">
				<div class="col col-sm-12 col-md-6">
					<c:if
						test="${(f:resource('isWorkflowEnabled') and f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA:Sistema Integrado de Gestão Administrativa;WF:Módulo de Workflow'))}">
						<div class="card bg-light mb-3">
							<div class="card-header">Tarefas</div>
							<div class="card-body">
								<div id='right'>
									<jsp:include page="loading.jsp" />
								</div>
							</div>
						</div>
					</c:if>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;SR')}">
						<div class="card bg-light mb-3">
							<div class="card-header">Solicitações</div>
							<div class="card-body">
								<div id='rightbottom'>
									<jsp:include page="loading.jsp" />
								</div>
							</div>
						</div>
					</c:if>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;GC:Módulo de Gestão de Conhecimento')}">
						<div class="card bg-light mb-3">
							<div class="card-header">Gestão de Conhecimento</div>
							<div class="card-body">
								<div id='rightbottom2'>
									<jsp:include page="loading.jsp" />
								</div>
							</div>
						</div>
					</c:if>
					<c:if
						test="${f:podeUtilizarServicoPorConfiguracao(titular,lotaTitular,'SIGA;TP:Módulo de Transportes')}">
						<div class="card bg-light mb-3">
							<div class="card-header">Transportes</div>
							<div class="card-body">
								<div id='rightbottom3'>
									<jsp:include page="loading.jsp" />
								</div>
							</div>
						</div>
					</c:if>
				</div>
			</c:if>
		</div>
	</div>
</siga:pagina>
