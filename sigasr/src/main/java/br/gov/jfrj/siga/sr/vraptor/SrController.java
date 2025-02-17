package br.gov.jfrj.siga.sr.vraptor;

import static br.com.caelum.vraptor.view.Results.http;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.HttpResult;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.validator.SrError;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class SrController extends SigaController {

	protected SrValidator srValidator;

	public SrController(HttpServletRequest request, HttpServletResponse response, Result result, CpDao dao, SigaObjects so, EntityManager em, SrValidator srValidator) {
		super(request, response, result, dao, so, em);
		this.srValidator = srValidator;
	}

	public void enviarErroValidacao() {
		HttpResult res = this.result.use(http());
		res.setStatusCode(HttpStatus.SC_BAD_REQUEST);
		res.addHeader("Validation", "true");

		result.use(Results.http()).body(jsonErrors().toString());
	}

	private JsonArray jsonErrors() {
		JsonArray jsonArray = new JsonArray();

		for (SrError error : srValidator.getErros()) {
			jsonArray.add(new Gson().toJsonTree(error));
		}

		return jsonArray;
	}
}
