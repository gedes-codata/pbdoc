<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>


<script type="text/javascript">
	function validar() {
		var descricao = $('#dscFeriado').val();
		if (descricao == null || descricao == "") {
			alert("Preencha a descricao do feriado");
			document.getElementById('dscFeriado').focus();
		} else
			frm.submit();
	}

	function excluirGrupo(idExclusao) {
		var mensagem = "Deseja excluir o email?";
		document.getElementById("idExclusaoRegistro").value = idExclusao;
		mensagemConfirmacao(mensagem, excluir)
	}

	function mensagemConfirmacao(mensagem, funcaoConfirmacao) {
		$('#modalConfirmacao').find('.modal-body').html(mensagem);
		$('#btnConfirmacao').click(funcaoConfirmacao);
		$('#modalConfirmacao').modal();
	}

	var excluir = function() {
		var idExclusao = document.getElementById("idExclusaoRegistro").value;
		location.href = '/sigaex/app/expediente/emailNotificacao/excluir?id='
				+ idExclusao;
	}
</script>
<siga:pagina titulo="Lista Emails Notificação">
	<!-- main content -->
	<div class="container-fluid">
		<div class="card bg-light mb-3">
			<div class="card-header">
				<h5>Emails de notificação cadastrados</h5>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-sm-8">
						<c:url var="url" value="editar" />
						<button type="button"
							onclick="javascript:window.location.href='${url}'"
							class="btn btn-primary">Incluir</button>
					</div>
				</div>
			</div>
		</div>

		<c:choose>
			<c:when test="${not empty itens}">
				<table border="0" class="table table-sm table-striped">
					<thead class="${thead_color}">
						<tr>
							<th style="text-align: center;" colspan="2">Destinatário da
								Movimentação</th>
							<th rowspan="2" width="8%"></th>
							<th style="text-align: center;" colspan="4">Enviar
								notificação para:</th>
						</tr>
						<tr>
							<th align="left" width="15%">Pessoa</th>
							<th align="right" width="15%">Lotacao</th>
							<th align="right" width="25%">Email</th>
							<th align="right" width="15%">Pessoa</th>
							<th align="right" width="15%">Lotação</th>
							<th colspan=2></th>
						</tr>
					</thead>
					<tbody class="table-bordered">
						<c:forEach var="email" items="${itens}">
							<tr>
								<td align="left"><c:if test="${not empty email.dpPessoa}">
										<siga:selecionado
											sigla="${email.dpPessoa.pessoaAtual.nomeAbreviado}"
											descricao="${email.dpPessoa.pessoaAtual.descricao} - ${email.dpPessoa.pessoaAtual.sigla}" />
									</c:if></td>
								<td><c:if test="${not empty email.dpLotacao}">
										<siga:selecionado
											sigla="${email.dpLotacao.lotacaoAtual.sigla}"
											descricao="${email.dpLotacao.lotacaoAtual.descricaoAmpliada}" />
									</c:if></td>
								<td></td>
								<td>${email.email}</td>
								<td><c:if test="${not empty email.pessoaEmail}">
										<siga:selecionado
											sigla="${email.pessoaEmail.pessoaAtual.nomeAbreviado}"
											descricao="${email.pessoaEmail.pessoaAtual.descricao} - ${email.pessoaEmail.pessoaAtual.sigla}" />
									</c:if></td>
								<td><c:if test="${not empty email.lotacaoEmail}">
										<siga:selecionado
											sigla="${email.lotacaoEmail.lotacaoAtual.sigla}"
											descricao="${email.lotacaoEmail.lotacaoAtual.descricaoAmpliada}" />
									</c:if></td>
								<td align="center" width="5%"><a
									href="javascript:excluirGrupo(${email.idEmailNotificacao})">
										<img style="display: inline;"
										src="/siga/css/famfamfam/icons/cancel_gray.png"
										title="Excluir email"
										onmouseover="this.src='/siga/css/famfamfam/icons/cancel.png';"
										onmouseout="this.src='/siga/css/famfamfam/icons/cancel_gray.png';" />
								</a></td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>
			<c:otherwise>
				<b>Não há emails cadastrados</b>
			</c:otherwise>
		</c:choose>

	</div>
	<!-- Modal Confirmacao -->
	<input type="hidden" id="idExclusaoRegistro" value="" />
	<div class="modal fade" id="modalConfirmacao" tabindex="-1"
		role="dialog" aria-labelledby="modalConfirmacao" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="exampleModalLabel">Confirmação</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Fechar">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body"></div>
				<div class="modal-footer">
					<button id="btnConfirmacao" type="button" class="btn btn-primary">Ok</button>
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal">Cancelar</button>
				</div>
			</div>
		</div>
	</div>
	<!-- Fim Modal Confirmacao-->
</siga:pagina>
