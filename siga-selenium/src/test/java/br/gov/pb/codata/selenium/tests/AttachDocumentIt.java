package br.gov.pb.codata.selenium.tests;

import java.nio.file.Path;
import java.nio.file.Paths;

import br.gov.pb.codata.selenium.DriverBase;
import br.gov.pb.codata.selenium.PBDocSeleniumController;
import br.gov.pb.codata.selenium.page_objects.PBDocAttachDocumentPage;

/**
*
* @author Thomas Ribeiro
*/
public class AttachDocumentIt extends DriverBase {

	// Objetivo: Anexar arquivos em documentos no sistema
	private String subscritor = System.getenv("USUARIO");
	private String titular = System.getenv("USUARIO2");
	private static Path path = Paths.get("src/test/resources/hino.pdf");
	private static String file = path.toAbsolutePath().toString();

	public void attachDocument() throws Exception {
		PBDocAttachDocumentPage attachDocumentPage = new PBDocAttachDocumentPage();
		attachDocumentPage.fillinputDate("01012020");
		attachDocumentPage.selectSubscritor(subscritor);
		attachDocumentPage.selectTitular(titular);
		attachDocumentPage.selectFile(file);
		attachDocumentPage.fillinputDescription("Descrição de anexo no documento.");
		attachDocumentPage.submitForm();
		attachDocumentPage.authenticateAttachement();
		attachDocumentPage.returnToEditPage();
		PBDocSeleniumController.checkNoError("AttachDocumentIt.attachDocument");
	}
}
