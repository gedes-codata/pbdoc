package br.gov.jfrj.siga.gc.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.vraptor.SigaController;
import br.gov.jfrj.siga.vraptor.SigaObjects;

public class GcController extends SigaController {

	public GcController(HttpServletRequest request, HttpServletResponse response, Result result, SigaObjects so, EntityManager em) {
		super(request, response, result, CpDao.getInstance(), so, em);
	}

	public void assertAcesso(String pathServico) throws AplicacaoException {
		so.assertAcesso("GC:Módulo de Gestão de Conhecimento;" + pathServico);
	}

	protected CpDao dao() {
		return CpDao.getInstance();
	}
}
