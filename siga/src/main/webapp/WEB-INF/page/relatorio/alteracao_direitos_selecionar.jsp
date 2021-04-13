<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="32kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>

<siga:pagina titulo="Relatório de Alteração de Direitos de Utilização">
	<!-- main content -->
	<div class="container-fluid">
		<div class="card bg-light mb-3" >
			<div class="card-header">
				<h5>Relatório de Alteração de Direitos de Utilização</h5>
			</div>
			<div class="card-body">
				<div class="row">
					<div class="col-sm-2">
						<div class="form-group">
							<label for="itxDtInicio">Data Inicial</label>
							<input name="itxDtInicio" id="itxDtInicio" tabindex="10" type="text" maxlength="10" onkeypress="return FormataData(this, event);"
								 onblur="javascript:verificarData(this);" class="form-control"></input>						
						</div>
					</div>
					<div class="col-sm-2">
						<div class="form-group">
							<label for="itxDtFim">Data Final</label>
							<input name="itxDtFim" id="itxDtFim" tabindex="20" type="text" maxlength="10" onkeypress="return FormataData(this, event);"
									onblur="javascript:verificarData(this);" class="form-control"></input>						
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group">
							<label for="idOrgaoUsuario">Órgão Usuário</label>
							<select name="idOrgaoUsuario" id="idOrgaoUsuario" tabindex="30" class="form-control">
								<c:forEach var="ousu" items="${cpOrgaosUsuario}">
									<option value="${ousu.idOrgaoUsu}">${ousu.nmOrgaoUsu}
									</option>
								</c:forEach>
							</select>						
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-sm-2">
						<div class="form-group">
							<button class="btn btn-primary" tabindex="40" onclick="javascript:submeter();">Gerar...</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- Modal Alert-->
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
		<!--Fim Modal Alert -->
	</div>
	<br />
	<div id="div-tempo" style="display: none; position: absolute; text-align: center; filter: alpha(opacity =         60); opacity: 0.4; background-color: #dcdcdc; vertical-align: middle; border-width: 2px; border-color: darkblue; border-style: solid;">
		<div style="position: absolute; font-family: sans-serif50 %; left: 40%; top: 50%; font-size: medium; large; font-weight: bolder; color: purple;">Aguarde...</div>
	</div>
</siga:pagina>
<script type="text/javascript">
	function submeter() {
		var t_nodDataInicio = document.getElementById("itxDtInicio");
		if (!t_nodDataInicio) {
			mensagemAlerta ("Data Inicial não preenchida !");
			focus(t_nodDataInicio);
			return false;
		}
		var t_strDataInicio = t_nodDataInicio.value;
		if (!t_strDataInicio) {
			mensagemAlerta ("Data Inicial não preenchida !");
			focus(t_nodDataInicio);
			return false;
		}
		var t_nodDataFim = document.getElementById("itxDtFim");
		if (!t_nodDataFim) {
			mensagemAlerta ("Data Final não preenchida !");
			return false;
		}
		var t_strDataFim = t_nodDataFim.value;
		if (!t_strDataFim) {
			mensagemAlerta ("Data Final não preenchida !");
			focus(t_nodDataFim);
			return false;
		}
		var t_nodSelOrgaoUsuario = document.getElementById("idOrgaoUsuario");
		var t_intCodSel;
		if (t_nodSelOrgaoUsuario) {
			t_intCodSel = t_nodSelOrgaoUsuario.options[t_nodSelOrgaoUsuario.selectedIndex].value;
		} else {
			t_intCodSel = "-1";
		}
		setTimeout( 
				function () {
					exibirAguarde();
				}
		, 50); 
		var t_strUrl = "emitir_alteracao_direitos";
		t_strUrl += "?dataInicio=" + t_strDataInicio;
		t_strUrl += "&dataFim=" + t_strDataFim;
		t_strUrl += "&idOrgaoUsuario=" + t_intCodSel;
		location.href = t_strUrl;
		
	}
	function verificarData(p_nodData) {
		var t_strData = p_nodData.value;
		if (!t_strData) {
			return true;
		}
		var t_arr1CamposData = t_strData.split("/");
		if (t_arr1CamposData.length != 3) {
			mensagemAlerta("Por favor, digite a data no formato DD/MM/AAAA");
			focus(p_nodData);
			return false;
		}
		var t_intDia = parseInt(t_arr1CamposData[0],10);
		if (! ((t_intDia <= 31) && ( t_intDia >= 1))) {
			mensagemAlerta("Dia inválido!");
			focus(p_nodData);
			return false;
		}
		var t_intMes = parseInt(t_arr1CamposData[1],10);
		if (! ((t_intMes <= 12) && ( t_intDia >= 1))) {
			mensagemAlerta("Mês inválido!");
			focus(p_nodData);
			return false;
		}
		if ((t_intMes == 2) && (t_intDia > 29)) {
			mensagemAlerta("Dia inválido!");
			focus(p_nodData);
			return false;
		}
		var t_intAno = parseInt(t_arr1CamposData[2],10);
		if (! ((t_intAno <= (new Date()).getFullYear()  ) && ( t_intAno >= 2010))) {
			mensagemAlerta("Ano inválido! Deve estar entre 2010 e o da data atual.");
			focus(p_nodData);
			return false;
		}
		p_nodData.value = t_intDia + "/" + t_intMes + "/" + t_intAno;
		return true;
	}
	function focus(p_nodNodo) {
		setTimeout( function () {p_nodNodo.focus()},50);
	} 
	function mensagemAlerta(mensagem) {
		$('#alertaModal').find('.mensagem-Modal').text(mensagem);
		$('#alertaModal').modal();
	}
</script>
<script type="text/javascript">
	 /*
		 ***** Exibição do  Aguarde **** 
	*/
	function exibirAguarde() {
		var nodDivConteudo = document.getElementById("tblfrm");
		if (nodDivConteudo) {
			var nodDivTempo = document.getElementById("div-tempo");
			if (nodDivTempo) {
				nodDivTempo.style.display = "block"
				var intScrollYSize = getScrollingYSize(); 
				var intScrollXSize = getScrollingXSize(); 
				var intScrollYPos = getScrollingYPos() ; 
				var intScrollXPos = getScrollingXPos() ; 
				nodDivTempo.style.zIndex = '3';
				nodDivTempo.style.left = intScrollXPos.toString()     + 'px' ;
				nodDivTempo.style.top =  intScrollYPos.toString()     + 'px' ;
				nodDivTempo.style.width =  intScrollXSize.toString() + 'px' ;
				nodDivTempo.style.height =  intScrollYSize.toString() + 'px' ;
				// correção do bug do IE
				var arr1NodSelect = document.getElementsByTagName('select');
				if (arr1NodSelect) {
					for (var intConta = 0; intConta < arr1NodSelect.length; intConta++) {
						var nodSelect = arr1NodSelect[intConta];
						nodSelect.disabled=true;
					}
				}
			}
		} 
	}
	function voltarDoAguarde() {
	 var nodDivConteudo = document.getElementById("tblfrm");
		if (nodDivConteudo) {
			var nodDivTempo = document.getElementById("div-tempo");
			if (nodDivTempo) {
				nodDivTempo.style.display = "none"
				var arr1NodSelect = document.getElementsByTagName('select');
				if (arr1NodSelect) {
					for (var intConta = 0; intConta < arr1NodSelect.length; intConta++) {
						var nodSelect = arr1NodSelect[intConta];
						nodSelect.disabled=false;
					}
				}	
			}
		} 
	}
	function getScrollingXPos() {
		if (navigator.appName == "Microsoft Internet Explorer"){
			return document.body.scrollLeft ;
		} else {
			return  window.pageXOffset ;
		}
	}
	
	function getScrollingYPos() {
		if (navigator.appName == "Microsoft Internet Explorer"){
			return document.body.scrollTop ;
		} else {
			return  window.pageYOffset ;
		}
	}
	function getScrollingXSize() {
		if (navigator.appName == "Microsoft Internet Explorer"){
			return document.body.clientWidth ;
		} else { 
			//return  window.screen.width ;
			return  window.innerWidth  ;
		}
	}
	
	function getScrollingYSize() {
		if (navigator.appName == "Microsoft Internet Explorer"){
			return document.body.clientHeight ;
		} else {
			//return  window.screen.height ;
			return  window.innerHeight ;
		}
	}

	 function FormataData(campo, e) {

			var tecla = (window.e) ? e.keyCode : e.which;

			// só entra se for número
			if ((tecla >= 48) && (tecla <= 57)) {
				if (campo.value.length == 2 || campo.value.length == 5) {// verifica
					campo.value += '/';
				}
				return true;
			} else if (tecla == 8 || tecla == 0) {
				return true;
			} else {
				return false;
			}
		}	
 </script>
<script type="text/javascript">
  	/*
 	* Impede o uso do F5
 	*/
 	f5Hold = function getcode(ev) {
 		var oEvent = (window.event) ? window.event : ev;
 		if (oEvent.keyCode == 116) {
 			//alert("F5 Pressionado !"); 
 			if(document.all) {
 				oEvent.keyCode = 0;
 				oEvent.cancelBubble = true;
 			} else {
 				oEvent.preventDefault();
 				oEvent.stopPropagation();
 			}
 			return false;
 		}
 	};
 	if(document.all) {
 		document.onkeydown =  f5Hold;
 	} else {
 		document.onkeypress = f5Hold;
 	}
 	/*
 	*
 	*/
 	/*
 	* Inicialização
 	*/
 	var t_nodDataInicio = document.getElementById("itxDtInicio");
	if (!t_nodDataInicio) {
		focus(t_nodDataInicio);
	}
	voltarDoAguarde();
</script>