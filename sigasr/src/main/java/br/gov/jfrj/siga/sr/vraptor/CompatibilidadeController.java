package br.gov.jfrj.siga.sr.vraptor;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.sr.model.SrSolicitacao;
import br.gov.jfrj.siga.sr.validator.SrValidator;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
@Path("solicitacao")
public class CompatibilidadeController extends SrController {

    public CompatibilidadeController(HttpServletRequest request, HttpServletResponse response, Result result, CpDao dao, SigaObjects so, EntityManager em, SrValidator srValidator) {
        super(request, response, result, dao, so, em, srValidator);
        result.on(AplicacaoException.class).forwardTo(this).appexception();
        result.on(Exception.class).forwardTo(this).exception();
    }
    
    @Get
    @Path("/exibir")
    public void exibir(Long id) throws Exception {
    	if (id == null || id <=0)
    		throw new AplicacaoException("Número não informado");
    	SrSolicitacao sol = SrSolicitacao.AR.findById(id);
    	result.forwardTo(SolicitacaoController.class).exibir(sol.getSiglaCompacta(), true, false);
    }

}
