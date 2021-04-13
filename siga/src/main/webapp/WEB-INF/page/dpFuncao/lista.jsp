<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="Listar Fun&ccedil;&otilde;es de Confian&ccedil;a">
	<script type="text/javascript" language="Javascript1.1">
		function sbmt(offset) {
			if (offset == null) {
				offset = 0;
			}
			frm.elements["paramoffset"].value = offset;
			frm.elements["p.offset"].value = offset;
			frm.submit();
		}
	</script>
	<div class="container-fluid">
		<c:if test="${not empty mensagemUsuario}">
			<div class="row" id="row-mensagem-retorno">
				<div class="col">
					<p id="mensagem-retorno" class="alert alert-success mb-3 mb-0">${mensagemUsuario}.</p>
					<script>
						setTimeout(function() {
							$('#mensagem-retorno').fadeTo(2000, 0, function() {
								$('#row-mensagem-retorno').slideUp(1000);
							});
						}, 5000);
					</script>
				</div>
			</div>
		</c:if>
	</div>
	<form name="frm" action="listar" class="form" method="GET">
		<input type="hidden" name="paramoffset" value="0" /> <input
			type="hidden" name="p.offset" value="0" />
		<div class="container-fluid">
			<div class="card bg-light mb-3">
				<div class="card-header">
					<h5>Cadastro de Fun&ccedil;&atilde;o de Confian&ccedil;a</h5>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col-sm-6">
							<div class="form-group">
								<label>Órgão</label>
								</td> <select name="idOrgaoUsu" value="${idOrgaoUsu}"
									class="form-control">
									<c:forEach items="${orgaosUsu}" var="item">
										<option value="${item.idOrgaoUsu}"
											${item.idOrgaoUsu == idOrgaoUsu ? 'selected' : ''}>
											${item.nmOrgaoUsu}</option>
									</c:forEach>
								</select>
							</div>
						</div>
						<div class="col-sm-6">
							<div class="form-group">
								<label>Nome</label> <input type="text" id="nome" name="nome"
									value="${nome}" maxlength="100" size="30" class="form-control" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-6">
							<input type="submit" value="Pesquisar" class="btn btn-primary" />
						</div>
					</div>
				</div>

			</div>
			<!-- main content -->
			<h5>
				Funç&otilde;es de Confian&ccedil;a cadastrados
				</h2>

				<table border="0" class="table table-sm table-striped">
					<thead class="${thead_color}">
						<tr>
							<th align="left">Nome</th>
							<th colspan="2" align="center">Op&ccedil;&otilde;es</th>
						</tr>
					</thead>

					<tbody>
						<siga:paginador maxItens="15" maxIndices="10"
							totalItens="${tamanho}" itens="${itens}" var="funcao">
							<tr>
								<td align="left">${funcao.descricao}</td>
								<td align="left"><c:url var="url"
										value="/app/funcao/editar">
										<c:param name="id" value="${funcao.id}"></c:param>
									</c:url> <input type="button" value="Alterar" class="btn btn-primary"
									onclick="javascript:location.href='${url}'" /></td>
								<%--	<td align="left">									
					 					<a href="javascript:if (confirm('Deseja excluir o orgão?')) location.href='/siga/app/orgao/excluir?id=${orgao.idOrgao}';">
										<img style="display: inline;"
										src="/siga/css/famfamfam/icons/cancel_gray.png" title="Excluir orgão"							
										onmouseover="this.src='/siga/css/famfamfam/icons/cancel.png';" 
										onmouseout="this.src='/siga/css/famfamfam/icons/cancel_gray.png';"/>
									</a>															
								</td>
							 --%>
							</tr>
						</siga:paginador>
					</tbody>
				</table>

				<div class="gt-table-buttons">
					<c:url var="url" value="/app/funcao/editar"></c:url>
					<input type="button" value="Incluir"
						onclick="javascript:window.location.href='${url}'"
						class="btn btn-primary">
				</div>
		</div>
	</form>
</siga:pagina>