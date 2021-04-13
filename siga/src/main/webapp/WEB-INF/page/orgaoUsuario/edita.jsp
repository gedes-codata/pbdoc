<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>


<siga:pagina titulo="Cadastro de Orgãos">

	<script type="text/javascript">
		function validar() {
			var nmOrgaoUsuario = document.getElementsByName('nmOrgaoUsuario')[0].value;
			var siglaOrgaoUsuario = document.getElementsByName('siglaOrgaoUsuario')[0].value;
			var siglaOrgaoUsuarioCompleta = document.getElementsByName('siglaOrgaoUsuarioCompleta')[0].value;
			var id = document.getElementsByName('id')[0].value;

			if (nmOrgaoUsuario == null || nmOrgaoUsuario == "") {
				mensagemAlerta("Preencha o nome do Órgão.");
				document.getElementById('nmOrgaoUsuario').focus();
			} else {
				if (siglaOrgaoUsuario == null || siglaOrgaoUsuario == "") {
					mensagemAlerta("Preencha a sigla abreviada do Órgão.");
					document.getElementById('siglaOrgaoUsuario').focus();
				} else {
					if (siglaOrgaoUsuarioCompleta == null
							|| siglaOrgaoUsuarioCompleta == "") {
						mensagemAlerta("Preencha a sigla Completa do Órgão.");
						document.getElementById('siglaOrgaoUsuarioCompleta').focus();
					} else {
						if (id == null || id == "") {
							mensagemAlerta("Preencha ID do Órgão.");
							document.getElementById('id').focus();
						} else {
							if (siglaOrgaoUsuario.length != 3) {
								mensagemAlerta("Preencha sigla abreviada com exatamente 3 letras.");
								document.getElementById('siglaOrgaoUsuario').focus();
							} else {
								frm.submit();
							}
						}
					}
				}
			}
		}

		function somenteLetras() {
			tecla = event.keyCode;
			if ((tecla >= 65 && tecla <= 90) || (tecla >= 97 && tecla <= 122)) {
				return true;
			} else {
				return false;
			}
		}

		function mensagemAlerta(mensagem) {
			$('#alertaModal').find('.mensagem-Modal').text(mensagem);
			$('#alertaModal').modal();
		}
	</script>

	<body>

		<div class="container-fluid">
			<div class="card bg-light mb-3">
				<form name="frm"
					action="${request.contextPath}/app/orgaoUsuario/gravar"
					method="POST" >
					<input type="hidden" name="postback" value="1" />
					<div class="card-header">
						<h5>Cadastro de Órgão Usuário</h5>
					</div>
					<div class="card-body">
						<div class="row">
							<div class="col-sm-1">
								<div class="form-group">
									<label>ID</label>
									<c:choose>
										<c:when test="${empty id}">
											<input type="text" id="id" name="id" value="${id}"
												maxlength="6" size="6"
												onKeypress="return verificaNumero(event);"
												class="form-control" />
											<input type="hidden" name="acao" value="i" />
										</c:when>
										<c:otherwise>
											<label class="form-control">${id}</label>
											<input type="hidden" name="id" value="${id}" />
											<input type="hidden" name="acao" value="a" />
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="col-sm-5">
								<div class="form-group">
									<label>Nome</label> <input type="text" id="nmOrgaoUsuario"
										name="nmOrgaoUsuario" value="${nmOrgaoUsuario}" maxlength="80"
										size="80" class="form-control" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="form-group">
									<label>Sigla Oficial</label> <input type="text"
										id="siglaOrgaoUsuarioCompleta"
										name="siglaOrgaoUsuarioCompleta"
										value="${siglaOrgaoUsuarioCompleta}" maxlength="10" size="10"
										class="form-control" />
								</div>
							</div>
							<div class="col-sm-3">
								<div class="form-group">
									<label>Sigla Abreviada</label>
									<c:choose>
										<c:when test="${empty siglaOrgaoUsuario || podeAlterarSigla}">
											<input type="text" name="siglaOrgaoUsuario"
												id="siglaOrgaoUsuario" value="${siglaOrgaoUsuario}"
												minlength="3" maxlength="3" size="3"
												style="text-transform: uppercase"
												onKeypress="return somenteLetras(event);"
												onkeyup="this.value = this.value.trim()"
												class="form-control" />
										</c:when>
										<c:otherwise>
											<label class="form-control">${siglaOrgaoUsuario}</label>
											<input type="hidden" name="siglaOrgaoUsuario"
												value="${siglaOrgaoUsuario}" />
										</c:otherwise>
									</c:choose>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-sm-6">
								<div class="form-group">
									<input type="button" value="Ok"
										onclick="javascript: validar();" class="btn btn-primary" /> <input
										type="button" value="Cancelar"
										onclick="javascript:history.back();" class="btn btn-primary" />
								</div>
							</div>
						</div>
					</div>
			</div>
			<br />
			</form>
			<!-- Modal -->
			<div class="modal fade" id="alertaModal" tabindex="-1" role="dialog"
				aria-labelledby="exampleModalLabel" aria-hidden="true">
				<div class="modal-dialog" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title" id="alertaModalLabel">Alerta</h5>
							<button type="button" class="close" data-dismiss="modal"
								aria-label="Fechar">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body">
							<p class="mensagem-Modal"></p>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-primary"
								data-dismiss="modal">Fechar</button>
						</div>
					</div>
				</div>
			</div>
			<!--Fim Modal -->
		</div>
		</div>

	</body>

</siga:pagina>