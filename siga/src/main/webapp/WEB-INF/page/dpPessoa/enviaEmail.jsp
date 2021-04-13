<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<script type="text/javascript" language="Javascript1.1">
function sbmt(offset) {
	var idOrgaoUsu = document.getElementsByName('idOrgaoUsu')[0].value;

	if(idOrgaoUsu==null || idOrgaoUsu == 0) {
		mensagemAlerta("Selecione um órgão");
		document.getElementById('idOrgaoUsu')[0].focus();
		return;	
	}

	if (offset == null) {
		offset = 0;
	}
	frm.elements["paramoffset"].value = offset;
	frm.elements["p.offset"].value = offset;
	frm.submit();
}

function enviar() {
	var frm = document.getElementById('enviarEmail');
	frm.action = 'enviar';
	frm.submit();
}

function mensagemAlerta(mensagem) {
	$('#alertaModal').find('.mensagem-Modal').text(mensagem);
	$('#alertaModal').modal();
}

function validarCPF(Objcpf){
	var strCPF = Objcpf.replace(".","").replace(".","").replace("-","").replace("/","");
    var Soma;
    var Resto;
    Soma = 0;
	
    for (i=1; i<=9; i++) Soma = Soma + parseInt(strCPF.substring(i-1, i)) * (11 - i);
    Resto = (Soma * 10) % 11;
	
    if ((Resto == 10) || (Resto == 11))  Resto = 0;
    if (Resto != parseInt(strCPF.substring(9, 10)) ) {
    	
    	alert('CPF Inválido!');
        return false;
	}
    Soma = 0;
    for (i = 1; i <= 10; i++) Soma = Soma + parseInt(strCPF.substring(i-1, i)) * (12 - i);
    Resto = (Soma * 10) % 11;

    if ((Resto == 10) || (Resto == 11))  Resto = 0;
    if (Resto != parseInt(strCPF.substring(10, 11) ) ) {
    	
    	alert('CPF Inválido!');
    	return false;
    }
    return true;
         
}
function cpf_mask(v){
	v=v.replace(/\D/g,"");
	v=v.replace(/(\d{3})(\d)/,"$1.$2");
	v=v.replace(/(\d{3})(\d)/,"$1.$2");
	v=v.replace(/(\d{3})(\d{1,2})$/,"$1-$2");

	if(v.length == 14) {
    	validarCPF(v);
    }
	return v;
	}
</script>

<siga:pagina titulo="Listar Pessoas">
	<!-- main content -->
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
	<div class="container-fluid">
	<form name="frm" id="enviarEmail" action="enviarEmail" class="form100" method="GET">
		<input type="hidden" name="paramoffset" value="0" />
		<input type="hidden" name="p.offset" value="0" />
		<input type="hidden" name="retornarEnvioEmail" value="true" />
		<div class="card bg-light mb-3" >
			<div class="card-header">
				<h5>Envio de E-mail para Novos Usuários</h5>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-md-4">
						<div class="form-group">
							<label for="uidOrgaoUsu">Órgão</label>
							<select name="idOrgaoUsu" value="${idOrgaoUsu}" onchange="carregarRelacionados(this.value)" class="form-control">
								<option value="0">Selecione</option> 
								<c:forEach items="${orgaosUsu}" var="item">
									<option value="${item.idOrgaoUsu}"
										${item.idOrgaoUsu == idOrgaoUsu ? 'selected' : ''}>
										${item.nmOrgaoUsu}</option>
								</c:forEach>
							</select>
						</div>					
					</div>
					<div class="col-md-3">
						<div class="form-group">
							<label for="idLotacaoPesquisa">Lota&ccedil;&atilde;o</label>
							<select name="idLotacaoPesquisa" value="${idLotacaoPesquisa}" class="form-control">
								<c:forEach items="${listaLotacao}" var="item">
									<option value="${item.idLotacao}"
										${item.idLotacao == idLotacaoPesquisa ? 'selected' : ''}>
										${item.descricao}</option>
								</c:forEach>
							</select>
						</div>					
					</div>
				</div>
				<div class="row">
					<div class="col-md-4">
						<div class="form-group">
							<label for="nome">Nome</label>
							<input type="text" id="nome" name="nome" value="${nome}" maxlength="100" class="form-control"/>
						</div>					
					</div>					
					<div class="col-md-2">
						<div class="form-group">
							<label for="nome">CPF</label>
							<input type="text" id="cpfPesquisa" name="cpfPesquisa" value="${cpfPesquisa}" maxlength="14" onkeyup="this.value = cpf_mask(this.value)" class="form-control"/>
						</div>					
					</div>					
				</div>
				<div class="row">
					<div class="col-sm-2">
						<button type="button" onclick="javascript: sbmt();" class="btn btn-primary">Pesquisar</button>
					</div>
				</div>				
			</div>
		</div>
	
		<h3 class="gt-table-head">Pessoas</h3>
		<table border="0" class="table table-sm table-striped">
			<thead class="${thead_color}">
				<tr>
					<th align="left">Nome</th>
					<th align="left">CPF</th>
					<th align="left">Data de Nascimento</th>
					<th align="left">Usuário</th>
					<th align="left">Lotação</th>			
				</tr>
			</thead>
			<tbody>
				<siga:paginador maxItens="15" maxIndices="10" totalItens="${tamanho}"
					itens="${itens}" var="pessoa">
					<tr>
						<td align="left">${pessoa.descricao}</td>
						<td align="left">${pessoa.cpfFormatado}</td>
						<td align="left"><fmt:formatDate pattern = "dd/MM/yyyy" value = "${pessoa.dataNascimento}" /></td>
						<td align="left">${pessoa.sigla}</td>
						<td align="left">${pessoa.lotacao.nomeLotacao}</td>				
					</tr>
				</siga:paginador>
			</tbody>
		</table>				
		<div class="gt-table-buttons">
			<c:url var="url" value="/app/pessoa/enviar"></c:url>
			<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">
			  Enviar E-mail
			</button>
		</div>				
	</form>
	<!-- Modal -->
	<div class="modal fade" id="alertaModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
	    	<div class="modal-content">
	      		<div class="modal-header">
			        <h5 class="modal-title" id="alertaModalLabel">Alerta</h5>
			        <button type="button" class="close" data-dismiss="modal" aria-label="Fechar">
			          <span aria-hidden="true">&times;</span>
			    	</button>
			    </div>
		      	<div class="modal-body">
		        	<p class="mensagem-Modal"></p>
		      	</div>
				<div class="modal-footer">
				  <button type="button" class="btn btn-primary" data-dismiss="modal">Fechar</button>
				</div>
	    	</div>
	  	</div>
	</div>				
	<!--Fim Modal -->
	
	<!-- Modal -->
	<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
	          <span aria-hidden="true">&times;</span>
	        </button>
	      </div>
	      <div class="modal-body">
	        Deseja realmente enviar e-mail para Novo(s) Usuário(s)?
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-primary" onclick="enviar()">OK</button>
	        <button type="button" class="btn btn-secondary" data-dismiss="modal">Cancelar</button>
	      </div>
	    </div>
	  </div>
	</div>
	
	
	</div>

<script>
function carregarRelacionados(id) {
	frm.method = "POST";
	frm.action = 'carregarCombos';
	frm.submit();
	frm.method = "GET";
}
</script>
</siga:pagina>