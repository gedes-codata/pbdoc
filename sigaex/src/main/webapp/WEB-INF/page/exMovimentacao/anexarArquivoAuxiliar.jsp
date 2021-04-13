<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://localhost/customtag" prefix="tags"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>
<%@ taglib uri="http://localhost/functiontag" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<style type="text/css" media="screen">
	.custom-file-label::after{content: 'Escolher arquivo' !important;}
</style>

<siga:pagina titulo="Anexação de Arquivo Auxiliar">

	<c:if test="${not mob.doc.eletronico}">
		<script type="text/javascript">
			$("html").addClass("fisico");
			$("body").addClass("fisico");
		</script>
	</c:if>

	<!-- main content bootstrap -->
	<div class="container-fluid">
		<div class="card bg-light mb-3">
			<div class="card-header">
				<h5>
					Anexação de Arquivo Auxiliar - ${mob.siglaEDescricaoCompleta}
				</h5>
			</div>
			<div class="card-body">
				<form action="anexar_arquivo_auxiliar_gravar" method="POST" onsubmit="sbmt.disabled=true;"
					enctype="multipart/form-data" class="form">
					<input type="hidden" name="postback" value="1" />
					<input type="hidden" name="sigla" value="${sigla}" />
					<div class="row">
						<div class="col-sm-6">
							<div class="form-control custom-file">
								<input class="custom-file-input" id="idSelecaoArquivo" type="file" name="arquivo" accept="*.*"/>
								<label class="custom-file-label text-truncate" for="idSelecaoArquivo" data-browse="Escolha o Arquivo">Clique para selecionar o arquivo a anexar</label>
							</div>
						</div>
					</div>
					<div class="row mt-4">
						<div class="col-sm">
							<input type="submit" value="Ok" class="btn btn-primary"
								onclick="javascript: return validaSelecaoAnexo( this.form );" name="sbmt"/>
							<input type="button" value="Voltar"
								onclick="javascript:window.location.href='/sigaex/app/expediente/doc/exibir?sigla=${sigla}'"
								class="btn btn-cancel ml-2" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
	<script>
		/**
		 * Valida se o anexo foi selecionado ao clicar em OK
		 */
		function validaSelecaoAnexo(form) {
			var result = true;
			var arquivo = form.arquivo;
			var fileExtension = arquivo.value.substring(arquivo.value.lastIndexOf("."));

			if (arquivo == null || arquivo.value == '') {
				alert("O arquivo a ser anexado não foi selecionado!");
				result = false;
			}

 			if (fileExtension == ".bat" || fileExtension == ".exe" || fileExtension == ".sh" || fileExtension == ".dll" ) {
 				alert("Extensão " + fileExtension + " inválida para inclusão do arquivo.");
 				result = false;
 			}
			return result;
		}
	</script>

	<script js>
	$('.custom-file-input').on('change', function() { 
		   let fileName = $(this).val().split('\\').pop(); 
		   $(this).next('.custom-file-label').addClass("selected").html(fileName); 
		});
	</script>
</siga:pagina>
