<!DOCTYPE1 HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	buffer="64kb"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://localhost/customtag" prefix="tags"%>
<%@ taglib uri="http://localhost/jeetags" prefix="siga"%>


<siga:pagina titulo="Apensar Documento">
	<c:if test="${not mob.doc.eletronico}">
		<script type="text/javascript">$("html").addClass("fisico");$("body").addClass("fisico");</script>
	</c:if>
	
	<!-- main content bootstrap -->
	<div class="container-fluid">
		<div class="card bg-light mb-3">
			<div class="card-header">
				<h5>
					Apensação de Documento - ${mob.siglaEDescricaoCompleta}
				</h5>
			</div>
			<div class="card-body">
				<form action="apensar_gravar" enctype="multipart/form-data" class="form" method="POST">
					<input type="hidden" name="postback" value="1" />
					<input type="hidden" name="sigla" value="${sigla}"/>
					<!-- Checa se o documento é eletronico ou não. Caso seja, seu valor default para Data é o atual e o Responsável é quem fez o Login. -->
					<c:if test="${!doc.eletronico}">
						<!-- Documento Eletronico -->
						<div class="row">
							<div class="col-md-2 col-sm-3">
								<div class="form-group">
									<label for="dtMovString">Data</label>
									<input class="form-control" type="text" name="dtMovString"
										value="${dtMovString}" onblur="javascript:verifica_data(this, true);" />
								</div>
							</div>
							<div class="col-sm-6">
								<div class="form-group">
									<label>Responsável</label>
									<siga:selecao tema="simple" propriedade="subscritor" modulo="siga"/>
								</div>
							</div>
							<div class="col-sm-2 mt-4">
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" 
										theme="simple" name="substituicao"  value="${substituicao}" 
										onclick="javascript:displayTitular(this);" />
									<label class="form-check-label">Substituto</label>
								</div>
							</div>
						</div>
						<div class="row">
							<div class="col-sm-6">
								<div class="form-group">
								<c:choose>
									<c:when test="${!substituicao}">
										<div id="tr_titular" style="display: none">
									</c:when>
									<c:otherwise>
										<div id="tr_titular" style="">
									</c:otherwise>
								</c:choose>
											<label>Titular</label>
											<input type="hidden" name="campos" value="titularSel.id" />
											<siga:selecao propriedade="titular" tema="simple" modulo="siga"/>
										</div>
								</div>
							</div>
						</div>
					</c:if>

					<div class="row">
						<div class="col-sm-6">
							<div class="form-group">
								<label><fmt:message key="documento.mestre"/></label>
								<siga:selecao tema='simple' titulo="Documento Mestre:" propriedade="documentoRef" urlAcao="expediente/buscar" urlSelecionar="expediente/selecionar" modulo="sigaex" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm">
							<input type="submit" value="Ok" class="btn btn-primary" />
							<input type="button" value="Cancela" onclick="javascript:history.back();" class="btn btn-cancel ml-2" />
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</siga:pagina>