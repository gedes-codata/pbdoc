package br.gov.jfrj.siga.sr.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class PrincipalController  extends SrController {

	public PrincipalController(HttpServletRequest request, HttpServletResponse response, Result result, CpDao dao, SigaObjects so, EntityManager em,  SrValidator srValidator, Validator validator) {
        super(request, response, result, dao, so, em, srValidator);
    }
	@Path("/app/principal")
	public void principal() throws Exception {
		//Principal
		result.redirectTo(SolicitacaoController.class).buscar(null, null, false, false);
	}
}