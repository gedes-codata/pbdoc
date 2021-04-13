package br.gov.jfrj.siga.pp.vraptor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.pp.dao.PpDao;
import br.gov.jfrj.siga.pp.models.Foruns;
import br.gov.jfrj.siga.pp.models.Locais;
import br.gov.jfrj.siga.pp.models.UsuarioForum;
import br.gov.jfrj.siga.vraptor.SigaObjects;

@Resource
public class PrincipalController extends PpController {
    
    public PrincipalController(HttpServletRequest request, HttpServletResponse response, Result result, CpDao dao, SigaObjects so, EntityManager em) {
        super(request, response, result, PpDao.getInstance(), so, em);
    }

    @Path("/app/principal")
    public void principal() throws Exception {
        result.redirectTo(this).home();
    }
    
    @Path("/app/home")
    public void home() {
        String matriculaSessao = getCadastrante().getMatricula().toString();
        String sesb_pessoaSessao = getCadastrante().getSesbPessoa().toString();
        UsuarioForum objUsuario = UsuarioForum.findByMatricula(matriculaSessao, sesb_pessoaSessao);
        if (objUsuario != null) {
            try {
                List<Locais> lstLocais = Locais.AR.find("forumFk=" + objUsuario.getForumFk().getCod_forum() + "order by ordem_apresentacao ").fetch();
                Foruns objForum = Foruns.AR.find("cod_forum=" + objUsuario.getForumFk().getCod_forum()).first();
                ArrayList<String> vetorForuns = new ArrayList<String>();
                String texto = objForum.getMural();
                while (texto.length() > 4) {
                    vetorForuns.add(texto.substring(0, texto.indexOf("<br>")));
                    texto = texto.substring(texto.indexOf("<br>"),
                            texto.length());
                    texto = texto.substring(4, texto.length());
                }
                result.include("lstLocais", lstLocais);
                result.include("vetorForuns", vetorForuns);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            redirecionaPaginaErro("Usuario sem permiss&atilde;o", null);
        }
    }
    
    @Path("/app/creditos")
    public void creditos() {
        //Creditos
    }
    
    public void erro(String msg , String link) {
        result.include("msg", msg);
        result.include("link", link);
    }
}