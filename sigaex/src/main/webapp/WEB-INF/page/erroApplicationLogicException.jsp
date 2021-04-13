<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page isErrorPage="true" import="java.io.*" contentType="text/html"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="Opera&ccedil;&atilde;o n&atilde;o permitida" desabilitarbusca="sim" desabilitarmenu="sim" desabilitarComplementoHEAD="sim">
	<div class="container my-5" style="min-height: 480px;">
		<div class="row">
			<div class="col-md-12">
				<div class="error-template">
					<h1 class="text-danger">
						<i class="fa fa-user-slash"></i>
						<strong> Oooops! Opera&ccedil;&atilde;o n&atilde;o permitida.</strong>
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
</siga:pagina>
