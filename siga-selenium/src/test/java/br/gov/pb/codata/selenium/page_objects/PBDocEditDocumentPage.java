package br.gov.pb.codata.selenium.page_objects;

import static com.lazerycode.selenium.util.AssignDriver.initQueryObjects;

import org.openqa.selenium.By;

import com.lazerycode.selenium.util.Query;

import br.gov.pb.codata.selenium.DriverBase;
import br.gov.pb.codata.selenium.PBDocSeleniumController;
import br.gov.pb.codata.selenium.tests.AccompanyDocumentIt;
import br.gov.pb.codata.selenium.tests.AnnotateDocumentIt;
import br.gov.pb.codata.selenium.tests.AttachDocumentIt;
import br.gov.pb.codata.selenium.tests.SignDocumentIT;
import br.gov.pb.codata.selenium.tests.exceptions.PBDocGenericError;
import br.gov.pb.codata.selenium.util.text.Dictionary;

/**
*
* @author Thomas Ribeiro
*/
public class PBDocEditDocumentPage {

	private final Query linkSignDocument = new Query().defaultLocator(By.xpath("//a[@title='Assinar']"));
	private final Query linkAnnotateDocument = new Query().defaultLocator(By.xpath("//a[@title='Anotar']"));
	private final Query linkFinishDocument = new Query().defaultLocator(By.xpath("//a[@title='Finalizar']"));
	private final Query linkAttachDocument = new Query().defaultLocator(By.xpath("//a[@title='Anexar']"));
	private final Query linkAccompanyDocument = new Query().defaultLocator(By.xpath("//a[@title='Definir Acompanhamento']"));

	public PBDocEditDocumentPage() throws Exception {
		initQueryObjects(this, DriverBase.getDriver());
	}

	public void doAction(String action) throws PBDocGenericError, Exception {
		switch (action) {
		case Dictionary.ASSINAR:
			linkSignDocument.findWebElement().click();
			PBDocSeleniumController.checkNoError("EditDocumentIt.edit:" + action);
			SignDocumentIT signDocumentIT = new SignDocumentIT();
			signDocumentIT.signDocument();
			break;
		case Dictionary.ANOTAR:
			linkAnnotateDocument.findWebElement().click();
			PBDocSeleniumController.checkNoError("EditDocumentIt.edit:" + action);
			AnnotateDocumentIt annotateDocumentIt = new AnnotateDocumentIt();
			annotateDocumentIt.annotateDocument();
			break;
		case Dictionary.FINALIZAR:
			linkFinishDocument.findWebElement().click();
			PBDocSeleniumController.checkNoError("EditDocumentIt.edit:" + action);
			break;
		case Dictionary.ANEXAR:
			linkAttachDocument.findWebElement().click();
			PBDocSeleniumController.checkNoError("EditDocumentIt.edit:" + action);
			AttachDocumentIt attachDocumentIt = new AttachDocumentIt();
			attachDocumentIt.attachDocument();
			break;
		case Dictionary.DEFINIR_ACOMPANHAMENTO:
			linkAccompanyDocument.findWebElement().click();
			PBDocSeleniumController.checkNoError("EditDocumentIt.edit:" + action);
			AccompanyDocumentIt accompanyDocumentIt = new AccompanyDocumentIt();
			accompanyDocumentIt.accompanyDocument();
			break;
		default:
			break;
		}
	}
}
