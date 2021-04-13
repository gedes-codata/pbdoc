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
package br.rj.jfrj.siga.dp;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.Dialect;
import org.hibernate.jdbc.Work;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.cp.CpConfiguracao;
import br.gov.jfrj.siga.cp.CpGrupo;
import br.gov.jfrj.siga.cp.CpGrupoDeEmail;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.CpTipoGrupo;
import br.gov.jfrj.siga.cp.CpTipoServico;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.cp.bl.CpAmbienteEnumBL;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.CpTipoLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.dp.dao.DpPessoaDaoFiltro;
import br.gov.jfrj.siga.model.Objeto;
import br.gov.jfrj.siga.model.dao.DaoFiltro;
import br.gov.jfrj.siga.model.dao.HibernateUtil;
import junit.framework.TestCase;

public class CpDaoTest extends TestCase {

	private static final String NOVA_SENHA = "123456";
	private static final String CPF = "11111111111";
	private static final String LOGIN = "RJ11111";
	private CpDao dao;

	public CpDaoTest() throws Exception {
		if (false) {
			CpAmbienteEnumBL ambiente = CpAmbienteEnumBL.DESENVOLVIMENTO;
			Cp.getInstance().getProp().setPrefixo(ambiente.getSigla());
			Configuration cfg = CpDao.criarHibernateCfg(ambiente);
			// HibernateUtil.configurarHibernate(cfg);

			dao = CpDao.getInstance();
		}
	}

	public void testGravarGrupoEPegarDataDeAtualizacao()
			throws AplicacaoException {
		if (true)
			return;
		Date dt2 = dao.consultarDataUltimaAtualizacao();

		CpTipoGrupo tpGrp = dao.consultar(
				CpTipoGrupo.TIPO_GRUPO_GRUPO_DE_DISTRIBUICAO,
				CpTipoGrupo.class, false);
		dao.iniciarTransacao();

		CpGrupo grpNovo = new CpGrupoDeEmail();
		grpNovo.setCpTipoGrupo(tpGrp);
		grpNovo.setOrgaoUsuario(dao.consultar(1L, CpOrgaoUsuario.class, false));
		grpNovo.setHisDtIni(dao.consultarDataEHoraDoServidor());
		grpNovo.setCpGrupoPai(null);
		grpNovo.setDscGrupo("Teste");
		grpNovo.setSiglaGrupo("TESTE");
		grpNovo = (CpGrupo) dao.gravarComHistorico(grpNovo, null);
		Long idCpGrupo = grpNovo.getIdGrupo();

		// CpGrupo grpTest = CpDao.getInstance().consultar(idCpGrupo,
		// CpGrupo.class, false);

		Date dt1 = dao.consultarDataUltimaAtualizacao();
	}

	public void testGravarGrupoEAtualizar() throws AplicacaoException,
			Exception, IllegalAccessException {
		if (true)
			return;

		CpTipoGrupo tpGrp = dao.consultar(
				CpTipoGrupo.TIPO_GRUPO_GRUPO_DE_DISTRIBUICAO,
				CpTipoGrupo.class, false);
		dao.iniciarTransacao();

		CpGrupo grpNovo = new CpGrupoDeEmail();
		grpNovo.setCpTipoGrupo(tpGrp);
		grpNovo.setOrgaoUsuario(dao.consultar(1L, CpOrgaoUsuario.class, false));
		grpNovo.setCpGrupoPai(null);
		grpNovo.setDscGrupo("Teste");
		grpNovo.setSiglaGrupo("TESTE");
		grpNovo = (CpGrupo) dao.gravarComHistorico(grpNovo, null, null, null);
		Long idCpGrupo = grpNovo.getIdGrupo();

		// dao.commitTransacao();

		CpGrupo grp = grpNovo;
		grpNovo = grp.getClass().newInstance();
		PropertyUtils.copyProperties(grpNovo, grp);
		grpNovo.setIdGrupo(null);
		grpNovo.setCpGrupoPai(null);
		grpNovo.setDscGrupo("Teste");
		grpNovo.setSiglaGrupo("TESTE");

		// dao.iniciarTransacao();
		grp = (CpGrupo) dao.gravarComHistorico(grpNovo, grp, null, null);

	}

	public void testAtualizarGrupoSemAlteracao() throws AplicacaoException,
			Exception, IllegalAccessException {
		if (true)
			return;

		dao.iniciarTransacao();

		CpGrupo grpIni = dao.listarGruposDeEmail().get(0);

		CpGrupo grp = (CpGrupo) Objeto.getImplementation(dao.consultar(
				grpIni.getId(), CpGrupo.class, false));
		CpGrupo grpNovo = ((CpGrupo) Objeto.getImplementation(grp)).getClass()
				.newInstance();
		PropertyUtils.copyProperties(grpNovo, grp);
		grpNovo.setIdGrupo(null);
		CpGrupo grpRecebido = (CpGrupo) dao.gravarComHistorico(grpNovo, grp,
				null, null);

		assertEquals(grpRecebido, grp);
	}

	public void testAtualizarGrupoComAlteracao() throws AplicacaoException,
			Exception, IllegalAccessException {
		if (true)
			return;

		dao.iniciarTransacao();

		CpGrupo grpIni = dao.listarGruposDeEmail().get(0);

		CpGrupo grp = (CpGrupo) Objeto.getImplementation(dao.consultar(
				grpIni.getId(), CpGrupo.class, false));
		CpGrupo grpNovo = ((CpGrupo) Objeto.getImplementation(grp)).getClass()
				.newInstance();
		PropertyUtils.copyProperties(grpNovo, grp);
		grpNovo.setIdGrupo(null);
		grpNovo.setDscGrupo(grp.getDscGrupo() + ".");
		CpGrupo grpRecebido = (CpGrupo) dao.gravarComHistorico(grpNovo, grp,
				null, null);

		assertEquals(grpRecebido, grpNovo);
	}

	public void testPesquisarConfiguracaoPorTipoLotacao()
			throws AplicacaoException, Exception, IllegalAccessException {
		if (true)
			return;
		CpTipoLotacao t_ctlTipoLotacao = dao.consultar(101L,
				CpTipoLotacao.class, false);
		final Query query = dao.getSessao().getNamedQuery(
				"consultarCpConfiguracoesPorTipoLotacao");
		query.setLong("idTpLotacao", t_ctlTipoLotacao.getIdTpLotacao());
		ArrayList<CpConfiguracao> t_arlConfigServicos = (ArrayList<CpConfiguracao>) query
				.list();
		ArrayList<CpServico> t_arlServicos = new ArrayList<CpServico>();
		for (CpConfiguracao t_cfgConfiguracao : t_arlConfigServicos) {
			t_arlServicos.add(t_cfgConfiguracao.getCpServico());
		}
		assertTrue(t_arlServicos.size() > 0);
	}

	public void testConsultarTipoServico() {
		if (true)
			return;
		CpTipoServico tpsrv = dao.consultar(CpTipoServico.TIPO_CONFIG_SISTEMA,
				CpTipoServico.class, false);
		assertNotNull(tpsrv);
	}

	/**
	 * @param args
	 * @throws Exception
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 */
	public static void main(String[] args) throws SecurityException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, Exception {
		if (true)
			return;

		CpAmbienteEnumBL ambiente = CpAmbienteEnumBL.DESENVOLVIMENTO;
		Cp.getInstance().getProp().setPrefixo(ambiente.getSigla());

		Configuration cfg = CpDao.criarHibernateCfg(ambiente);
		// HibernateUtil.configurarHibernate(cfg);

		CpDao dao = CpDao.getInstance();

		System.out.println("Data e hora da ultima atualização - "
				+ dao.consultarDataUltimaAtualizacao());

		dao.iniciarTransacao();
		// dao.importarAcessoTomcat();
		dao.commitTransacao();

		if (true)
			return;

		CpServico ser = dao.consultar(3L, CpServico.class, false);
		System.out.println(ser.getSiglaServico() + " - " + ser.getDscServico());

		DpPessoa pesSigla = new DpPessoa();
		pesSigla.setSesbPessoa("RJ");
		pesSigla.setMatricula(13635L);
		DpPessoa pes = dao.consultarPorSigla(pesSigla);

		System.out.println(pes.getSigla() + " - " + pes.getDescricao());
		System.out.println(pes.getCargo().getDescricao());
		System.out.println(pes.getFuncaoConfianca().getDescricao());
		System.out.println(pes.getLotacao().getSigla() + " - "
				+ pes.getLotacao().getDescricao());

		DpPessoaDaoFiltro flt = new DpPessoaDaoFiltro();
		flt.setSigla(LOGIN);
		System.out.print("consultarQuantidade: ");
		System.out.println(dao.consultarQuantidade((DaoFiltro) flt));

		CpDao.freeInstance();
	}

	public static void printSchema(SessionFactory fact, Configuration cfg) {
		Dialect dialect = Dialect.getDialect(cfg.getProperties());
		// printDropSchemaScript(cfg, dialect);
		// printSchemaCreationScript(cfg, dialect);
		printSchemaUpdateScript(fact, cfg, dialect);
	}

	public static void printSchemaCreationScript(final Configuration cfg,
			final Dialect dialect) {
		String[] schemaCreationScript = cfg
				.generateSchemaCreationScript(dialect);
		for (String stmt : schemaCreationScript) {
			System.out.println(stmt + ";");
		}
	}

	public static void printDropSchemaScript(final Configuration cfg,
			final Dialect dialect) {
		String[] dropSchemaScript = cfg.generateDropSchemaScript(dialect);
		for (String stmt : dropSchemaScript) {
			System.out.println(stmt + ";");
		}
	}

	public static void printSchemaUpdateScript(final SessionFactory sf,
			final Configuration cfg, final Dialect dialect) {
		HibernateUtil.getSessao().doWork(new Work() {
			@Override
			public void execute(Connection conn) throws SQLException {
				DatabaseMetadata metadata = new DatabaseMetadata(conn, dialect);
				String[] schemaUpdateScript = cfg.generateSchemaUpdateScript(
						dialect, metadata);

				for (String stmt : schemaUpdateScript) {
					System.out.println(stmt + ";");
				}

			}
		});

	}

}
