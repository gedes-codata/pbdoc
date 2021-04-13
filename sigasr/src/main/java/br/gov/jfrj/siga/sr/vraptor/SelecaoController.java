package br.gov.jfrj.siga.sr.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.sr.dao.SrDao;
import br.gov.jfrj.siga.sr.model.SrSelecionavel;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("/selecao")
public class SelecaoController extends SrController {

	public SelecaoController(HttpServletRequest request, HttpServletResponse response, Result result, SigaObjects so, EntityManager em, SrValidator srValidator) {
		super(request, response, result, SrDao.getInstance(), so, em, srValidator);
	}

	@Path("/ajaxRetorno")
	public void ajaxRetorno(SrSelecionavel selecionavel) {
		result.include("sel", selecionavel);

		if (selecionavel == null) {
			result.forwardTo(this).ajaxVazio();
		}
	}

	@Path("/ajaxVazio")
	public void ajaxVazio() {
	}
}