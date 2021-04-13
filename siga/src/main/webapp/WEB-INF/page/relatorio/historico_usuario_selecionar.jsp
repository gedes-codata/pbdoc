<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="32kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>


<siga:pagina titulo="Relatório de Histórico de Permissões do Usuário">
	<!-- main content -->
	<div class="container-fluid">
		<div class="card bg-light mb-3" >
			<div class="card-header">
				<h5>Relatório de Histórico de Permissões do Usuário</h5>
			</div>
			<div class="card-body">
			<form method="get" action="javascript:submeter()">
				<div class="row">
					<div class="col-sm-6">
						<div class="form-group">
							<label>Usuário</label>
							<siga:selecao tipo="pessoa" tema="simple" propriedade="pessoa" modulo="siga"/>
						</div>
					</div>				
				</div>
				<div class="row">
					<div class="col-sm-2">
						<div class="form-group">
							<button type="submit" class="btn btn-primary">Gerar...</button>
						</div>
					</div>				
				</div>
			</form>
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
	<div id="div-tempo"
		style="display: none; position: absolute; text-align: center; filter: alpha(opacity =           60); opacity: 0.4; background-color: #dcdcdc; vertical-align: middle; border-width: 2px; border-color: darkblue; border-style: solid;">
		<div
			style="position: absolute; font-family: sans-serif50 %; left: 40%; top: 50%; font-size: medium; large; font-weight: bolder; color: purple;">Aguarde...</div>
	</div>
</siga:pagina>
<script type="text/javascript">
function submeter() { 
	var t_strIdPessoa = document.getElementsByName("pessoa_pessoaSel.id")[0];
	if (t_strIdPessoa) {
		if (t_strIdPessoa.value) {
			location.href = 'emitir_historico_usuario?idPessoa=' + t_strIdPessoa.value;
			setTimeout( 
					function () {
						exibirAguarde();
					}
			, 50); 
		} else {
			mensagemAlerta("Por favor, é necessário preencher o campo pessoa!");
		}
	}
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
