package br.gov.pb.codata.selenium.tests;

import br.gov.pb.codata.selenium.DriverBase;
import br.gov.pb.codata.selenium.page_objects.PBDocEditDocumentPage;

public class EditDocumentIt extends DriverBase {

	// Objetivo: Editar documentos no sistema1

	public void edit(String action) throws Exception {
		PBDocEditDocumentPage editDocumentPage = new PBDocEditDocumentPage();
		editDocumentPage.doAction(action);
	}
}
