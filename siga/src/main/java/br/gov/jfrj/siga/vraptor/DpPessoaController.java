/*******************************************************************************
 * Copyright (c) 2006 - 2011 SJRJ.
 * 
 *     This file is part of SIGA.
 * 
 *     SIGA is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     SIGA is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with SIGA.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
/*
 * Criado em  13/09/2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.gov.jfrj.siga.vraptor;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.interceptor.download.Download;
import br.com.caelum.vraptor.interceptor.download.InputStreamDownload;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.view.Results;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.GeraMessageDigest;
import br.gov.jfrj.siga.base.SigaCalendar;
import br.gov.jfrj.siga.base.Texto;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.CpTipoIdentidade;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.cp.bl.CpBL;
import br.gov.jfrj.siga.cp.bl.SituacaoFuncionalEnum;
import br.gov.jfrj.siga.cp.model.DpLotacaoSelecao;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpCargo;
import br.gov.jfrj.siga.dp.DpFuncaoConfianca;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.dp.dao.DpCargoDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpFuncaoConfiancaDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpLotacaoDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpPessoaDaoFiltro;
import br.gov.jfrj.siga.model.Selecionavel;

@Resource
public class DpPessoaController extends SigaSelecionavelControllerSupport<DpPessoa, DpPessoaDaoFiltro> {

	private Long orgaoUsu;
	private DpLotacaoSelecao lotacaoSel;

	public DpPessoaController(HttpServletRequest request, HttpServletResponse response, Result result, CpDao dao, SigaObjects so, EntityManager em) {
		super(request, response, result, dao, so, em);

		result.on(AplicacaoException.class).forwardTo(this).appexception();
		result.on(Exception.class).forwardTo(this).exception();

		setSel(new DpPessoa());
		setItemPagina(10);
	}

	@Get
	@Post
	@Path({ "/app/pessoa/buscar", "/app/cosignatario/buscar", "/pessoa/buscar.action", "/cosignatario/buscar.action" })
	public void buscar(String sigla, String postback, Integer paramoffset, Long idOrgaoUsu, DpLotacaoSelecao lotacaoSel)
			throws Exception {
		final DpLotacao lotacaoTitular = getLotaTitular();
		if (postback == null && lotacaoTitular != null) {
			orgaoUsu = lotacaoTitular.getIdOrgaoUsuario();
		} else {
			orgaoUsu = idOrgaoUsu;
		}
		if (lotacaoSel != null && lotacaoSel.getId() != null && lotacaoSel.getId() > 0) {
			this.lotacaoSel = lotacaoSel;
		}
		this.getP().setOffset(paramoffset);

		if (sigla == null) {
			sigla = "";
		}

		super.aBuscar(sigla, postback);

		result.include("param", getRequest().getParameterMap());
		result.include("request", getRequest());
		result.include("itens", getItens());
		result.include("tamanho", getTamanho());
		result.include("orgaosUsu", getOrgaosUsu());
		result.include("lotacaoSel", lotacaoSel == null ? new DpLotacaoSelecao() : lotacaoSel);
		result.include("idOrgaoUsu", orgaoUsu);
		result.include("sigla", sigla);
		result.include("postbak", postback);
		result.include("offset", paramoffset);
	}

	@Get("/app/pessoa/exibir")
	public void exibi(String sigla) {
		if (sigla != null) {
			result.include("pessoa", dao().getPessoaPorPrincipal(sigla));
		}
	}

	@Override
	public DpPessoaDaoFiltro createDaoFiltro() {
		final DpPessoaDaoFiltro flt = new DpPessoaDaoFiltro();
		flt.setNome(Texto.removeAcentoMaiusculas(getNome()));
		if (lotacaoSel != null) {
			flt.setLotacao(lotacaoSel.buscarObjeto());
		}
		flt.setIdOrgaoUsu(orgaoUsu);

		String buscarFechadas = param("buscarFechadas");
		flt.setBuscarFechadas(buscarFechadas != null ? Boolean.valueOf(buscarFechadas) : false);
		flt.setSituacaoFuncionalPessoa("");

		return flt;
	}

	@Override
	public Selecionavel selecionarPorNome(final DpPessoaDaoFiltro flt) throws AplicacaoException {
		Selecionavel sel = null;

		// Acrescenta o sesb e repete a busca
		final String sigla = flt.getSigla();
		flt.setSigla(getTitular().getSesbPessoa() + sigla);
		sel = dao().consultarPorSigla(flt);
		if (sel != null)
			return sel;
		flt.setSigla(sigla);

		// Procura por nome
		flt.setIdOrgaoUsu(getLotaTitular().getOrgaoUsuario().getIdOrgaoUsu());
		flt.setNome(Texto.removeAcentoMaiusculas(flt.getSigla()));
		flt.setSigla(null);
		final List pessoas = dao().consultarPorFiltro(flt);
		if (pessoas != null)
			if (pessoas.size() == 1)
				return (DpPessoa) pessoas.get(0);
		return null;
	}

	@Get
	@Post
	@Path({ "/public/app/pessoa/selecionar", "/app/pessoa/selecionar", "/app/cosignatario/selecionar",
			"/pessoa/selecionar.action", "/cosignatario/selecionar.action" })
	public void selecionar(String sigla) {
		String resultado = super.aSelecionar(sigla);
		if ("ajax_retorno".equals(resultado)) {
			result.include("sel", getSel());
			result.use(Results.page()).forwardTo("/WEB-INF/jsp/ajax_retorno.jsp");
		} else {
			result.use(Results.page()).forwardTo("/WEB-INF/jsp/ajax_vazio.jsp");
		}
	}

	@Get("app/pessoa/listar")
	public void lista(Integer paramoffset, Long idOrgaoUsu, String nome, String cpfPesquisa, Long idCargoPesquisa,
			Long idFuncaoPesquisa, Long idLotacaoPesquisa) throws Exception {
		result.include("request", getRequest());
		List<CpOrgaoUsuario> list = new ArrayList<CpOrgaoUsuario>();
		List<CpOrgaoUsuario> listSemZZZ = new ArrayList<CpOrgaoUsuario>();
		CpOrgaoUsuario ou = new CpOrgaoUsuario();

		// TODO Incluir acesso Global
		if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())) {
			list = dao().listarOrgaosUsuarios();

			for (CpOrgaoUsuario cpOrgaoUsuario : list) {
				if (!cpOrgaoUsuario.getSiglaOrgaoUsu().equals("ZZZ"))
					listSemZZZ.add(cpOrgaoUsuario);
			}
			list = new ArrayList<>();
			list.addAll(listSemZZZ);
			result.include("orgaosUsu", list);
			if (idOrgaoUsu == null) {
				carregarCombos(null, list.get(0).getId(), null, null, null, null, null, 0, Boolean.FALSE);
			}
		} else {
			ou = CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario());
			list.add(ou);
			result.include("orgaosUsu", list);
			if (idOrgaoUsu == null) {
				carregarCombos(null, ou.getId(), null, null, null, null, null, 0, Boolean.FALSE);
			}
		}
		if (idOrgaoUsu != null && ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())
				|| CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario()).getId().equals(idOrgaoUsu))) {
			DpPessoaDaoFiltro dpPessoa = new DpPessoaDaoFiltro();
			if (paramoffset == null) {
				paramoffset = 0;
			}
			dpPessoa.setIdOrgaoUsu(idOrgaoUsu);
			dpPessoa.setNome(Texto.removeAcento(nome != null ? nome : ""));
			if (idCargoPesquisa != null) {
				DpCargo cargo = new DpCargo();
				cargo.setId(idCargoPesquisa);
				dpPessoa.setCargo(cargo);
			}
			if (idLotacaoPesquisa != null) {
				DpLotacao lotacao = new DpLotacao();
				lotacao.setId(idLotacaoPesquisa);
				dpPessoa.setLotacao(lotacao);
			}
			if (idFuncaoPesquisa != null) {
				DpFuncaoConfianca funcao = new DpFuncaoConfianca();
				funcao.setIdFuncao(idFuncaoPesquisa);
				dpPessoa.setFuncaoConfianca(funcao);
			}
			if (cpfPesquisa != null && !StringUtils.EMPTY.equals(cpfPesquisa)) {
				dpPessoa.setCpf(Long.valueOf(cpfPesquisa.replace(".", "").replace("-", "")));
			}
			dpPessoa.setBuscarFechadas(Boolean.TRUE);
			dpPessoa.setId(Long.valueOf(0));
			setItens(CpDao.getInstance().consultarPorFiltro(dpPessoa, paramoffset, 15));
			result.include("itens", getItens());
			Integer tamanho = dao().consultarQuantidade(dpPessoa);
			result.include("tamanho", tamanho);

			result.include("idOrgaoUsu", idOrgaoUsu);
			result.include("nome", nome);
			result.include("cpfPesquisa", cpfPesquisa);
			result.include("idCargoPesquisa", idCargoPesquisa);
			result.include("idFuncaoPesquisa", idFuncaoPesquisa);
			result.include("idLotacaoPesquisa", idLotacaoPesquisa);

			carregarCombos(null, idOrgaoUsu, null, null, null, null, cpfPesquisa, paramoffset, Boolean.FALSE);

		}
	}

	@Get("/app/pessoa/ativarInativar")
	public void ativarInativar(final Long id, Integer offset, Long idOrgaoUsu, String nome, String cpfPesquisa,
			Long idCargoPesquisa, Long idFuncaoPesquisa, Long idLotacaoPesquisa) throws Exception {
		CpOrgaoUsuario ou = new CpOrgaoUsuario();
		DpPessoa pessoa = dao().consultar(id, DpPessoa.class, false);
		ou.setIdOrgaoUsu(pessoa.getOrgaoUsuario().getId());
		ou = CpDao.getInstance().consultarPorId(ou);

		if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())
				|| CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario()).getId().equals(ou.getId())) {
			pessoa = dao().consultar(id, DpPessoa.class, false);
			// inativar
			if (pessoa.getDataFimPessoa() == null || StringUtils.EMPTY.equals(pessoa.getDataFimPessoa())) {
				Calendar calendar = new GregorianCalendar();
				Date date = new Date();
				calendar.setTime(date);
				pessoa.setDataFimPessoa(calendar.getTime());

			} else {// ativar
				// não pode ativar caso já exista uma pessoa com mesmo órgão, cargo, função de
				// confiança, lotação e cpf

				DpPessoaDaoFiltro dpPessoa = new DpPessoaDaoFiltro();
				dpPessoa.setIdOrgaoUsu(pessoa.getOrgaoUsuario().getId());
				dpPessoa.setCargo(pessoa.getCargo());
				dpPessoa.setFuncaoConfianca(pessoa.getFuncaoConfianca());
				dpPessoa.setLotacao(pessoa.getLotacao());
				dpPessoa.setCpf(pessoa.getCpfPessoa());
				dpPessoa.setNome("");
				dpPessoa.setId(id);

				dpPessoa.setBuscarFechadas(Boolean.FALSE);
				Integer tamanho = dao().consultarQuantidade(dpPessoa);

				if (tamanho > 0) {
					throw new AplicacaoException(
							"Já existe outro usuário ativo com estes dados: Órgão, Cargo, Função, Unidade e CPF");
				}

				pessoa.setDataFimPessoa(null);
			}

			try {
				dao().iniciarTransacao();
				dao().gravar(pessoa);
				dao().commitTransacao();
			} catch (final Exception e) {
				dao().rollbackTransacao();
				throw new AplicacaoException("Erro na gravação", 0, e);
			}

			if (pessoa.getDataFimPessoa() == null) {
				result.include("mensagemUsuario", "Pessoa ativada com sucesso");
			} else {
				result.include("mensagemUsuario", "Pessoa inativada com sucesso");
			}
			this.result.forwardTo(this).lista(offset, idOrgaoUsu, nome, cpfPesquisa, idCargoPesquisa, idFuncaoPesquisa,
					idLotacaoPesquisa);
		}
	}

	@Get("/app/pessoa/editar")
	public void edita(final Long id) {
		CpOrgaoUsuario ou = new CpOrgaoUsuario();
		if (id != null) {
			DpPessoa pessoa = dao().consultar(id, DpPessoa.class, false);
			ou.setIdOrgaoUsu(pessoa.getOrgaoUsuario().getId());
			ou = CpDao.getInstance().consultarPorId(ou);

			// TODO Incluir acesso Global
			if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla()) || CpDao.getInstance()
							.consultarPorSigla(getTitular().getOrgaoUsuario()).getId().equals(ou.getId())) {
				result.include("nmPessoa", pessoa.getNomePessoa());
				result.include("cpf", pessoa.getCpfFormatado());
				result.include("email", pessoa.getEmailPessoa());
				result.include("idOrgaoUsu", pessoa.getOrgaoUsuario().getId());
				result.include("nmOrgaousu", pessoa.getOrgaoUsuario().getNmOrgaoUsu());
				if (pessoa.getDataNascimento() != null) {
					result.include("dtNascimento", pessoa.getDtNascimentoDDMMYYYY());
				}
				if (pessoa.getCargo() != null) {
					result.include("idCargo", pessoa.getCargo().getId());
				}
				if (pessoa.getFuncaoConfianca() != null) {
					result.include("idFuncao", pessoa.getFuncaoConfianca().getId());
				}
				if (pessoa.getLotacao() != null) {
					result.include("idLotacao", pessoa.getLotacao().getId());
				}
			}
		}
		if (id == null || (ou.getId() != null && ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())
				|| CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario()).getId().equals(ou.getId())))) {
			if (ou.getId() == null) {
				ou = CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario());
			}

			if (ou.getId() != null) {
				DpCargoDaoFiltro cargo = new DpCargoDaoFiltro();
				cargo.setNome("");
				cargo.setIdOrgaoUsu(ou.getId());
				List<DpCargo> lista = new ArrayList<DpCargo>();
				DpCargo c = new DpCargo();
				c.setId(0L);
				c.setDescricao("Selecione");
				lista.add(c);
				lista.addAll((List<DpCargo>) dao().getInstance().consultarPorFiltro(cargo));
				result.include("listaCargo", lista);

				DpLotacaoDaoFiltro lotacao = new DpLotacaoDaoFiltro();
				lotacao.setNome("");
				lotacao.setIdOrgaoUsu(ou.getId());
				List<DpLotacao> listaLotacao = new ArrayList<DpLotacao>();
				DpLotacao l = new DpLotacao();
				l.setNomeLotacao("Selecione");
				l.setId(0L);
				listaLotacao.add(l);
				listaLotacao.addAll(dao().getInstance().consultarPorFiltro(lotacao));
				result.include("listaLotacao", listaLotacao);

				DpFuncaoConfiancaDaoFiltro funcao = new DpFuncaoConfiancaDaoFiltro();
				funcao.setNome("");
				funcao.setIdOrgaoUsu(ou.getId());
				List<DpFuncaoConfianca> listaFuncao = new ArrayList<DpFuncaoConfianca>();
				DpFuncaoConfianca f = new DpFuncaoConfianca();
				f.setNomeFuncao("Selecione");
				f.setIdFuncao(0L);
				listaFuncao.add(f);
				listaFuncao.addAll(dao().getInstance().consultarPorFiltro(funcao));
				result.include("listaFuncao", listaFuncao);

				result.include("request", getRequest());
				result.include("id", id);
			}
		}
		List<CpOrgaoUsuario> list = new ArrayList<CpOrgaoUsuario>();

		// TODO Incluir acesso Global
		if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())) {
			list = dao().listarOrgaosUsuarios();

			List<CpOrgaoUsuario> list1 = new ArrayList<CpOrgaoUsuario>();
			for (CpOrgaoUsuario cpOrgaoUsuario : list) {
				if (!"ZZZ".equals(cpOrgaoUsuario.getSiglaOrgaoUsu())) {
					list1.add(cpOrgaoUsuario);
				}
			}

			CpOrgaoUsuario org = new CpOrgaoUsuario();
			org.setNmOrgaoUsu("Selecione");
			org.setIdOrgaoUsu(0L);
			list1.add(0, org);
			result.include("orgaosUsu", list1);
		} else {
			ou = CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario());
			list.add(ou);
			result.include("orgaosUsu", list);

		}
	}

	@Post("/app/pessoa/carregarCombos")
	public void carregarCombos(final Long id, final Long idOrgaoUsu, final String nmPessoa, final String dtNascimento,
			final String cpf, final String email, final String cpfPesquisa, final Integer paramoffset,
			Boolean retornarEnvioEmail) {
		result.include("request", getRequest());
		result.include("id", id);
		result.include("idOrgaoUsu", idOrgaoUsu);
		result.include("nmPessoa", nmPessoa);
		result.include("dtNascimento", dtNascimento);
		result.include("cpf", cpf);
		result.include("email", email);
		result.include("cpfPesquisa", cpfPesquisa);
		setItemPagina(15);
		result.include("currentPageNumber", calculaPaginaAtual(paramoffset));
		List<CpOrgaoUsuario> list = new ArrayList<CpOrgaoUsuario>();

		// TODO Incluir acesso Global
		if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())) {
			List<CpOrgaoUsuario> list1 = new ArrayList<CpOrgaoUsuario>();
			list = dao().consultaCpOrgaoUsuario();

			for (CpOrgaoUsuario cpOrgaoUsuario : list) {
				if (!"ZZZ".equals(cpOrgaoUsuario.getSiglaOrgaoUsu())) {
					list1.add(cpOrgaoUsuario);
				}
			}
			result.include("orgaosUsu", list1);
		} else {
			CpOrgaoUsuario ou = CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario());
			list.add(ou);
			result.include("orgaosUsu", list);
		}

		DpCargoDaoFiltro cargo = new DpCargoDaoFiltro();
		cargo.setNome("");
		cargo.setIdOrgaoUsu(idOrgaoUsu);
		List<DpCargo> lista = new ArrayList<DpCargo>();
		DpCargo c = new DpCargo();
		c.setId(0L);
		c.setDescricao("Selecione");
		lista.add(c);
		lista.addAll((List<DpCargo>) dao().getInstance().consultarPorFiltro(cargo));
		result.include("listaCargo", lista);

		DpLotacaoDaoFiltro lotacao = new DpLotacaoDaoFiltro();
		lotacao.setNome("");
		lotacao.setIdOrgaoUsu(idOrgaoUsu);
		List<DpLotacao> listaLotacao = new ArrayList<DpLotacao>();
		DpLotacao l = new DpLotacao();
		l.setNomeLotacao("Selecione");
		l.setId(0L);
		listaLotacao.add(l);
		if (idOrgaoUsu != null && idOrgaoUsu != 0)
			listaLotacao.addAll(dao().getInstance().consultarPorFiltro(lotacao));
		result.include("listaLotacao", listaLotacao);

		DpFuncaoConfiancaDaoFiltro funcao = new DpFuncaoConfiancaDaoFiltro();
		funcao.setNome("");
		funcao.setIdOrgaoUsu(idOrgaoUsu);
		List<DpFuncaoConfianca> listaFuncao = new ArrayList<DpFuncaoConfianca>();
		DpFuncaoConfianca f = new DpFuncaoConfianca();
		f.setNomeFuncao("Selecione");
		f.setIdFuncao(0L);
		listaFuncao.add(f);
		listaFuncao.addAll(dao().getInstance().consultarPorFiltro(funcao));
		result.include("listaFuncao", listaFuncao);

		if (paramoffset == null) {
			result.use(Results.page()).forwardTo("/WEB-INF/page/dpPessoa/edita.jsp");
		} else if (retornarEnvioEmail != null && retornarEnvioEmail) {
			result.use(Results.page()).forwardTo("/WEB-INF/page/dpPessoa/enviaEmail.jsp");
		} else {
			result.use(Results.page()).forwardTo("/WEB-INF/page/dpPessoa/lista.jsp");
		}
	}

	@Post("/app/pessoa/gravar")
	public void editarGravar(final Long id, final Long idOrgaoUsu, final Long idCargo, final Long idFuncao,
			final Long idLotacao, final String nmPessoa, final String dtNascimento, final String cpf,
			final String email) throws Exception {
		assertAcesso("GI:Módulo de Gestão de Identidade;CAD_PESSOA:Cadastrar Pessoa");

		if (idOrgaoUsu == null || idOrgaoUsu == 0)
			throw new AplicacaoException("Órgão não informado");

		if (idCargo == null || idCargo == 0)
			throw new AplicacaoException("Cargo não informado");

		if (idLotacao == null || idLotacao == 0)
			throw new AplicacaoException("Lotação não informado");

		if (isBlank(nmPessoa))
			throw new AplicacaoException("Nome não informado");

		if (isBlank(cpf))
			throw new AplicacaoException("CPF não informado");

		if (isBlank(email))
			throw new AplicacaoException("E-mail não informado");

		if (nmPessoa != null && !nmPessoa.matches("[a-zA-ZáâãéêíóôõúçÁÂÃÉÊÍÓÔÕÚÇ'' ]+"))
			throw new AplicacaoException("Nome com caracteres não permitidos");

		DpPessoa pessoa = new DpPessoa();

		if (id == null) {
			Date data = new Date(System.currentTimeMillis());
			pessoa.setDataInicio(data);
			pessoa.setMatricula(0L);
			pessoa.setSituacaoFuncionalPessoa(SituacaoFuncionalEnum.APENAS_ATIVOS.getValor()[0]);
		} else {
			pessoa = dao().consultar(id, DpPessoa.class, false);
		}

		if (dtNascimento != null && !StringUtils.EMPTY.equals(dtNascimento)) {
			Date dtNasc = new Date();
			dtNasc = SigaCalendar.converteStringEmData(dtNascimento);

			Calendar hj = Calendar.getInstance();
			Calendar dtNasci = new GregorianCalendar();
			dtNasci.setTime(dtNasc);

			if (hj.before(dtNasci)) {
				throw new AplicacaoException("Data de nascimento inválida");
			}
			pessoa.setDataNascimento(dtNasc);
		} else {
			pessoa.setDataNascimento(null);
		}

		pessoa.setNomePessoa(Texto.removerEspacosExtra(nmPessoa).trim());
		pessoa.setCpfPessoa(Long.valueOf(cpf.replace("-", "").replace(".", "")));
		pessoa.setEmailPessoa(Texto.removerEspacosExtra(email).trim().replace(" ", "").toLowerCase());

		CpOrgaoUsuario ou = new CpOrgaoUsuario();
		DpCargo cargo = new DpCargo();
		DpFuncaoConfianca funcao = new DpFuncaoConfianca();
		DpLotacao lotacao = new DpLotacao();

		ou.setIdOrgaoUsu(idOrgaoUsu);
		ou = CpDao.getInstance().consultarPorId(ou);
		cargo.setId(idCargo);
		lotacao.setId(idLotacao);
		funcao.setIdFuncao(idFuncao);

		pessoa.setOrgaoUsuario(ou);
		pessoa.setCargo(cargo);
		pessoa.setLotacao(lotacao);
		if (idFuncao != null && idFuncao != 0) {
			pessoa.setFuncaoConfianca(funcao);
		} else {
			pessoa.setFuncaoConfianca(null);
		}
		pessoa.setSesbPessoa(ou.getSigla());

		// ÓRGÃO / CARGO / FUNÇÃO DE CONFIANÇA / LOTAÇÃO e CPF iguais.
		DpPessoaDaoFiltro dpPessoa = new DpPessoaDaoFiltro();
		dpPessoa.setIdOrgaoUsu(pessoa.getOrgaoUsuario().getId());
		dpPessoa.setCargo(pessoa.getCargo());
		dpPessoa.setFuncaoConfianca(pessoa.getFuncaoConfianca());
		dpPessoa.setLotacao(pessoa.getLotacao());
		dpPessoa.setCpf(pessoa.getCpfPessoa());
		dpPessoa.setNome("");
		dpPessoa.setId(id);

		dpPessoa.setBuscarFechadas(Boolean.FALSE);
		Integer tamanho = dao().consultarQuantidade(dpPessoa);

		if (tamanho > 0) {
			throw new AplicacaoException("Usuário já cadastrado com estes dados: Órgão, Cargo, Função, Unidade e CPF");
		}

		try {
			dao().iniciarTransacao();
			dao().gravar(pessoa);
			if (pessoa.getIdPessoaIni() == null && pessoa.getId() != null) {
				pessoa.setIdPessoaIni(pessoa.getId());
				pessoa.setIdePessoa(pessoa.getId().toString());
				pessoa.setMatricula(10000 + pessoa.getId());
				pessoa.setIdePessoa(pessoa.getMatricula().toString());
				dao().gravar(pessoa);

				List<CpIdentidade> lista = CpDao.getInstance()
						.consultaIdentidadesPorCpf(cpf.replace(".", "").replace("-", ""));
				CpIdentidade usu = null;
				if (lista.size() > 0) {
					CpIdentidade usuarioExiste = lista.get(0);
					usu = new CpIdentidade();
					usu.setCpTipoIdentidade(dao().consultar(1, CpTipoIdentidade.class, false));
					usu.setDscSenhaIdentidade(usuarioExiste.getDscSenhaIdentidade());
					usu.setDtCriacaoIdentidade(dao().consultarDataEHoraDoServidor());
					usu.setCpOrgaoUsuario(ou);
					usu.setHisDtIni(usu.getDtCriacaoIdentidade());
					usu.setHisAtivo(true);
				}

				if (usu != null) {
					usu.setNmLoginIdentidade(pessoa.getSesbPessoa() + pessoa.getMatricula());
					usu.setDpPessoa(pessoa);
					dao().gravarComHistorico(usu, getIdentidadeCadastrante());
				}
			}
			dao().commitTransacao();
		} catch (final Exception e) {
			dao().rollbackTransacao();
			throw new AplicacaoException("Erro na gravação", 0, e);
		}

		if (id == null) {
			result.include("mensagemUsuario", "Pessoa cadastrada com sucesso");
		} else {
			result.include("mensagemUsuario", "Pessoa alterada com sucesso");
		}
		lista(0, null, "", "", null, null, null);
	}

	@Get("/app/pessoa/carregarExcel")
	public void carregarExcel() {
		if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())) {
			result.include("orgaosUsu", dao().listarOrgaosUsuarios());
		} else {
			result.include("nmOrgaousu", getTitular().getOrgaoUsuario().getNmOrgaoUsu());
		}

		result.use(Results.page()).forwardTo("/WEB-INF/page/dpPessoa/cargaPessoa.jsp");
	}

	@Post("/app/pessoa/carga")
	public Download carga(final UploadedFile arquivo, Long idOrgaoUsu) throws Exception {
		InputStream inputStream = null;
		try {
			String nomeArquivo = arquivo.getFileName();
			String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf("."), nomeArquivo.length());

			File file = Paths.get(AdminController.UPLOAD_DIRECTORY, "arq" + extensao).toFile();

			file.createNewFile();
			FileUtils.copyInputStreamToFile(arquivo.getFile(), file);

			CpOrgaoUsuario orgaoUsuario = new CpOrgaoUsuario();
			if (idOrgaoUsu != null) {
				orgaoUsuario.setIdOrgaoUsu(idOrgaoUsu);
			} else {
				orgaoUsuario = getTitular().getOrgaoUsuario();
			}

			CpBL cpbl = new CpBL();
			inputStream = cpbl.uploadPessoa(file, orgaoUsuario, extensao, getIdentidadeCadastrante());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		if (inputStream == null) {
			result.include("msg", "Arquivo processado com sucesso!");
			carregarExcel();
		} else {
			result.include("msg", "");
			return new InputStreamDownload(inputStream, "application/text", "inconsistencias.txt");
		}
		return null;

	}

	@Get("app/pessoa/enviarEmail")
	public void enviaEmail(Integer paramoffset, Long idOrgaoUsu, String nome, String cpfPesquisa,
			Long idLotacaoPesquisa) throws Exception {
		result.include("request", getRequest());
		List<CpOrgaoUsuario> list = new ArrayList<CpOrgaoUsuario>();
		List<CpOrgaoUsuario> listSemZZZ = new ArrayList<CpOrgaoUsuario>();
		CpOrgaoUsuario ou = new CpOrgaoUsuario();
		if ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())) {
			list = dao().listarOrgaosUsuarios();
			for (CpOrgaoUsuario cpOrgaoUsuario : list) {
				if (!cpOrgaoUsuario.getSiglaOrgaoUsu().equals("ZZZ"))
					listSemZZZ.add(cpOrgaoUsuario);
			}
			list = new ArrayList<>();
			list.addAll(listSemZZZ);
			result.include("orgaosUsu", list);
			if (idOrgaoUsu == null) {
				carregarCombos(null, idOrgaoUsu, null, null, null, null, null, 0, Boolean.TRUE);
			}
		} else {
			ou = CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario());
			list.add(ou);
			result.include("orgaosUsu", list);
			if (idOrgaoUsu == null) {
				carregarCombos(null, ou.getId(), null, null, null, null, null, 0, Boolean.TRUE);
			}
		}
		if (idOrgaoUsu != null && ("ZZZ".equals(getTitular().getOrgaoUsuario().getSigla())
				|| CpDao.getInstance().consultarPorSigla(getTitular().getOrgaoUsuario()).getId().equals(idOrgaoUsu))) {
			DpPessoaDaoFiltro dpPessoa = new DpPessoaDaoFiltro();
			if (paramoffset == null) {
				paramoffset = 0;
			}
			dpPessoa.setIdOrgaoUsu(idOrgaoUsu);
			dpPessoa.setNome(Texto.removeAcento(nome != null ? nome : ""));

			if (idLotacaoPesquisa != null) {
				DpLotacao lotacao = new DpLotacao();
				lotacao.setId(idLotacaoPesquisa);
				dpPessoa.setLotacao(lotacao);
			}
			if (cpfPesquisa != null && !StringUtils.EMPTY.equals(cpfPesquisa)) {
				dpPessoa.setCpf(Long.valueOf(cpfPesquisa.replace(".", "").replace("-", "")));
			}
			dpPessoa.setBuscarFechadas(Boolean.TRUE);
			setItens(CpDao.getInstance().consultarPorFiltroSemIdentidade(dpPessoa, paramoffset, 15));
			result.include("itens", getItens());
			Integer tamanho = dao().consultarQuantidadeDpPessoaSemIdentidade(dpPessoa);
			result.include("tamanho", tamanho);

			result.include("idOrgaoUsu", idOrgaoUsu);
			result.include("nome", nome);
			result.include("cpfPesquisa", cpfPesquisa);
			result.include("idLotacaoPesquisa", idLotacaoPesquisa);

			carregarCombos(null, idOrgaoUsu, null, null, null, null, cpfPesquisa, paramoffset, Boolean.TRUE);
		}
	}

	@Get("app/pessoa/enviar")
	public void enviar(Long idOrgaoUsu, String nome, String cpfPesquisa, Long idLotacaoPesquisa) throws Exception {
		String[] senhaGerada = new String[1];

		if (idOrgaoUsu == null || idOrgaoUsu == 0)
			throw new AplicacaoException("Órgão não informado");

		DpPessoaDaoFiltro dpPessoa = new DpPessoaDaoFiltro();

		dpPessoa.setIdOrgaoUsu(idOrgaoUsu);
		dpPessoa.setNome(Texto.removeAcento(nome != null ? nome : ""));

		if (idLotacaoPesquisa != null) {
			DpLotacao lotacao = new DpLotacao();
			lotacao.setId(idLotacaoPesquisa);
			dpPessoa.setLotacao(lotacao);
		}
		if (cpfPesquisa != null && !StringUtils.EMPTY.equals(cpfPesquisa)) {
			dpPessoa.setCpf(Long.valueOf(cpfPesquisa.replace(".", "").replace("-", "")));
		}

		List<DpPessoa> lista = CpDao.getInstance().consultarPorFiltroSemIdentidade(dpPessoa, 0, 0);
		String cpfAnterior = "";
		for (DpPessoa dpPessoa2 : lista) {

			if (!cpfAnterior.equals(dpPessoa2.getCpfPessoa().toString())) {
				senhaGerada[0] = GeraMessageDigest.geraSenha();
			}
			Cp.getInstance().getBL().criarIdentidade(dpPessoa2.getSesbPessoa() + dpPessoa2.getMatricula(),
					dpPessoa2.getCpfFormatado(), getIdentidadeCadastrante(), null, senhaGerada, Boolean.FALSE,
					getBaseUrl());
			cpfAnterior = dpPessoa2.getCpfPessoa().toString();
		}
		result.include("mensagemUsuario", "Email(s) enviado(s) com sucesso");
		this.result.forwardTo(this).enviaEmail(0, idOrgaoUsu, nome, cpfPesquisa, idLotacaoPesquisa);
	}
}
