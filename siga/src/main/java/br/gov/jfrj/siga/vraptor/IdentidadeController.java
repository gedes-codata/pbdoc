package br.gov.jfrj.siga.vraptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.cp.model.DpPessoaSelecao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;

@Resource
public class IdentidadeController extends GiControllerSupport {

	public IdentidadeController(HttpServletRequest request, HttpServletResponse response, Result result, SigaObjects so, EntityManager em) {
		super(request, response, result, CpDao.getInstance(), so, em);

		result.on(AplicacaoException.class).forwardTo(this).appexception();
		result.on(Exception.class).forwardTo(this).exception();
	}
	
	@Get("/app/gi/identidade/listar")
	public void lista(DpPessoaSelecao pessoaSel) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		DpPessoa pes = definirPessoa(pessoaSel);

		if (pes != null) {
			result.include("itens", dao().consultaIdentidades(pes));
		}
		result.include("pessoaSel", enviarPessoaSelecao(pessoaSel));
	}

	private DpPessoaSelecao enviarPessoaSelecao(DpPessoaSelecao pessoaSel) {
		return (pessoaSel == null) ? new DpPessoaSelecao() : pessoaSel;
	}
	
	@Get("/app/gi/identidade/editar_gravar")
	public void aEditarGravar(DpPessoaSelecao pessoaSel, String dtExpiracao, Long id) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		if (id == null)
			throw new AplicacaoException("Não foi informada id");

		Date dataExpiracao = null;
		final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		try {
			dataExpiracao = df.parse(dtExpiracao + " 00:00");
		} catch (final ParseException e) {
		} catch (final NullPointerException e) {
		}

		CpIdentidade ident = daoId(id);
		Cp.getInstance().getBL().alterarIdentidade(ident, dataExpiracao, getIdentidadeCadastrante());
		
		result.forwardTo(this).lista(pessoaSel);
	}

	@Get("/app/gi/identidade/cancelar")
	public void aCancelar(Long id, DpPessoaSelecao pessoaSel) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		if (id == null)
			throw new AplicacaoException("Não foi informada id");

		CpIdentidade ident = daoId(id);
		Cp.getInstance().getBL().cancelarIdentidade(ident, getIdentidadeCadastrante());
		
		result.forwardTo(this).lista(pessoaSel);
	}

	@Get("/app/gi/identidade/desbloquear")
	public void aBloquear(Long id, DpPessoaSelecao pessoaSel) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		if (id != null) {
			CpIdentidade ident = daoId(id);
			Cp.getInstance().getBL().bloquearIdentidade(ident, getIdentidadeCadastrante(), true);
			result.include("mensagemUsuario", "Pessoa desbloqueada com sucesso");
			result.forwardTo(this).lista(pessoaSel);
		} else
			throw new AplicacaoException("Não foi informada id");
	}

	@Get("/app/gi/identidade/bloquear")
	public void aDesbloquear(Long id, DpPessoaSelecao pessoaSel) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		if (id != null) {
			CpIdentidade ident = daoId(id);
			Cp.getInstance().getBL().bloquearIdentidade(ident,getIdentidadeCadastrante(), false);
			result.include("mensagemUsuario", "Pessoa bloqueada com sucesso");
			result.forwardTo(this).lista(pessoaSel);
		} else
			throw new AplicacaoException("Não foi informada id");
	}

	@Get("/app/gi/identidade/bloquear_pessoa")
	public void aBloquearPessoa(DpPessoaSelecao pessoaSel) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		DpPessoa pes = definirPessoa(pessoaSel);

		if (pes != null) {
			Cp.getInstance().getBL().bloquearPessoa(pes,getIdentidadeCadastrante(), true);
			result.include("mensagemUsuario", "Pessoa bloqueada com sucesso");
			result.forwardTo(this).lista(pessoaSel);
		} else
			throw new AplicacaoException("Não foi informada a pessoa");
	}

	@Get("/app/gi/identidade/desbloquear_pessoa")
	public void aDesbloquearPessoa(DpPessoaSelecao pessoaSel) throws Exception {
		assertAcesso("ID:Gerenciar identidades");
		DpPessoa pes = definirPessoa(pessoaSel);

		if (pes != null) {
			Cp.getInstance().getBL().bloquearPessoa(pes,getIdentidadeCadastrante(), false);
			result.include("mensagemUsuario", "Pessoa desbloqueada com sucesso");
			result.forwardTo(this).lista(pessoaSel);
		} else
			throw new AplicacaoException("Não foi informada a pessoa");
	}
	
	public CpIdentidade daoId(long id) {
		return dao().consultar(id, CpIdentidade.class, false);
	}
	
	private DpPessoa definirPessoa(DpPessoaSelecao pessoaSel) {
		DpPessoa pessoa = null;
		
		if (pessoaSel != null) {
			pessoa = pessoaSel.buscarObjeto();
		}
		return pessoa;
	}
	
}
