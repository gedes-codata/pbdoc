<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page isErrorPage="true" import="java.io.*" contentType="text/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<c:catch var="selectException">
	<c:if test="${empty exceptionGeral or empty exceptionStackGeral}">
		<%
			java.lang.Throwable t = (Throwable) pageContext.getRequest().getAttribute("exception");
			if (t == null){
				t = (Throwable) exception;
			}
			if (t != null) {
				if (!t.getClass().getSimpleName()
						.equals("AplicacaoException")
						&& t.getCause() != null) {
					if (t.getCause().getClass().getSimpleName()
							.equals("AplicacaoException")) {
						t = t.getCause();
					} else if (t.getCause().getCause() != null
							&& t.getCause().getCause().getClass()
									.getSimpleName()
									.equals("AplicacaoException")) {
						t = t.getCause().getCause();
					}
				}
				// Get the ErrorData
				pageContext.getRequest().setAttribute("exceptionGeral", t);
				java.io.StringWriter sw = new java.io.StringWriter();
				java.io.PrintWriter pw = new java.io.PrintWriter(sw);
				t.printStackTrace(pw);
				pageContext.getRequest().setAttribute("exceptionStackGeral",
						sw.toString());
			}
		%>
	</c:if>
</c:catch>
<c:catch var="catchException">
	<siga:pagina titulo="Erro Geral" desabilitarbusca="sim" desabilitarmenu="sim" desabilitarComplementoHEAD="sim">

		<div class="container my-5" style="min-height: 480px;">
			<div class="row">
				<div class="col-md-12">
					<div class="error-template">
						<h1 class="text-danger">
							<i class="fa fa-times-circle"></i>
							<strong> N&atilde;o foi poss&iacute;vel completar a opera&ccedil;&atilde;o!</strong>
						</h1>
						<div class="error-details my-5">
							<p class="h4"><strong>${exceptionMessage}</strong></p>
						</div>
						<div class="error-actions mb-5">
							<button type="button" class="btn btn-primary" onclick="javascript:history.back();">
								<i class="fa fa-undo"></i> Voltar
							</button>
	
							<div class="btn-group">
								<a href="https://portal.pbdoc.pb.gov.br/atendimentos" class="btn btn-danger" target="_blank">
									<i class="fa fa-envelope"></i> Solicitar Atendimento
								</a>
								<button type="button" class="btn btn-danger dropdown-toggle dropdown-toggle-split" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
									<span class="sr-only">Mais opções...</span>
								</button>
								<div class="dropdown-menu">
									<a data-toggle="collapse" href="#stackCollapse" role="button" aria-expanded="false" aria-controls="stackCollapse" class="dropdown-item">
										Visualizar detalhes t&eacute;cnicos
									</a>
								</div>
							</div>
						</div>
						<div class="error-stacktrace collapse" id="stackCollapse">
							<div class="card bg-light">
								<div class="card-header">
									<strong>Informa&ccedil;&otilde;es T&eacute;cnicas: envie estas informa&ccedil;&otilde;es para o suporte</strong>
								</div>
								<div class="card-body">
									<pre style="font-size: 0.6em;">${exceptionStackGeral}</pre>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!--
		<div class="container-fluid">
			<div class="card bg-light mb-3" >
				<div class="card-header">
					<h5>
						N&atilde;o Foi Poss&iacute;vel Completar a Opera&ccedil;&atilde;o (${pageContext.getRequest().serverName})
					</h5>
				</div>

				<div class="card-body">
					<div class="row">
						<div class="col">
							<div class="form-group">
								<c:catch>
									<c:if test="${not empty exceptionGeral}">
										<c:if test="${not empty exceptionGeral.message}">
											<h3>${exceptionGeral.message}</h3>
										</c:if>
										<c:if test="${not empty exceptionGeral.cause}">
											<h4>${exceptionGeral.cause.message}</h4>
										</c:if>
									</c:if>
								</c:catch>
							</div>
						</div>
					</div>
					<c:if test="${siga_cliente != 'GOVSP'}">
						<div class="row">
							<div class="col">
								<div class="form-group">
									<div style="display: none; padding: 8pt;" align="left" id="stack">
										<pre style="font-size: 8pt;">${exceptionStackGeral}</pre>
									</div>
								</div>
							</div>
						</div>
					</c:if>
					<div class="row">
						<div class="col">
							<div class="form-group">		
								<input type="button" value="Voltar" class="btn btn-primary"  onclick="javascript:history.back();" />
								<c:if test="${siga_cliente != 'GOVSP'}">
									<input type="button" id="show_stack" value="Mais detalhes" class="btn btn-primary" onclick="javascript: document.getElementById('stack').style.display=''; document.getElementById('show_stack').style.display='none';" />
								</c:if>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		-->
	</siga:pagina>
</c:catch>

<c:if test="${catchException!=null}">
	Erro: ${catchException.message}
	<br>
	<br>
	<br>

	<pre>
		Erro original:
		${exceptionStack}
	</pre>
</c:if>


