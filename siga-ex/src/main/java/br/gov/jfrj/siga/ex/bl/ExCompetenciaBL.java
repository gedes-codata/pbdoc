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
package br.gov.jfrj.siga.ex.bl;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.SigaBaseProperties;
import br.gov.jfrj.siga.base.SigaMessages;
import br.gov.jfrj.siga.cp.CpComplexo;
import br.gov.jfrj.siga.cp.CpSituacaoConfiguracao;
import br.gov.jfrj.siga.cp.CpTipoConfiguracao;
import br.gov.jfrj.siga.cp.bl.CpCompetenciaBL;
import br.gov.jfrj.siga.dp.CpMarca;
import br.gov.jfrj.siga.dp.CpMarcador;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpCargo;
import br.gov.jfrj.siga.dp.DpFuncaoConfianca;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.DpResponsavel;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.ex.ExClassificacao;
import br.gov.jfrj.siga.ex.ExConfiguracao;
import br.gov.jfrj.siga.ex.ExDocumento;
import br.gov.jfrj.siga.ex.ExFormaDocumento;
import br.gov.jfrj.siga.ex.ExMobil;
import br.gov.jfrj.siga.ex.ExModelo;
import br.gov.jfrj.siga.ex.ExMovimentacao;
import br.gov.jfrj.siga.ex.ExNivelAcesso;
import br.gov.jfrj.siga.ex.ExPapel;
import br.gov.jfrj.siga.ex.ExTipoDocumento;
import br.gov.jfrj.siga.ex.ExTipoFormaDoc;
import br.gov.jfrj.siga.ex.ExTipoMovimentacao;
import br.gov.jfrj.siga.ex.ExVia;
import br.gov.jfrj.siga.hibernate.ExDao;

public class ExCompetenciaBL extends CpCompetenciaBL {

	public ExConfiguracaoBL getConf() {
		return (ExConfiguracaoBL) super.getConfiguracaoBL();
	}

	/**
	 * Retorna se é possível acessar o documento ao qual pertence o móbil
	 * passado por parâmetro. Considera se o documento ainda não foi assinado
	 * (sendo então considerado aberto; estando já assinado, retorna verdadeiro)
	 * e se <i>uma das</i> seguintes condições é satisfeita:
	 * <ul>
	 * <li>Usuário é da lotação cadastrante do documento</li>
	 * <li>Usuário é subscritor do documento</li>
	 * <li>Usuário é titular do documento</li>
	 * <li><i>podeMovimentar()</i> é verdadeiro para o usuário / móbil</li>
	 * <li>Usuário é um dos cossignatários do documento</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAcessarAberto(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		if (mob == null)
			return false;
		if (mob.getDoc().isPendenteDeAssinatura()
				&& !mob.getDoc().getLotaCadastrante().equivale(lotaTitular)				
				&& !titular.equivale(mob.getDoc().getTitular())
				&& !podeMovimentar(titular, lotaTitular, mob)) {			
			return false;
		}
		return true;
	}

	/**
	 * * Retorna se é possível acessar um documento ao qual pertence o móbil
	 * passado por parâmetro. Considera se o documento está cancelado (não
	 * estando cancelado, retorna verdadeiro) e se <i>uma das</i> seguintes
	 * condições é satisfeita:
	 * <ul>
	 * <li>Usuário é da lotação cadastrante do documento</li>
	 * <li>Usuário é subscritor do documento</li>
	 * <li>Usuário é titular do documento</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarCancelado(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (mob == null)
			return false;
		if (mob.getDoc().isCancelado()
				&& !mob.getDoc().getLotaCadastrante().equivale(lotaTitular)
				&& !titular.equivale(mob.getDoc().getSubscritor())
				&& !titular.equivale(mob.getDoc().getTitular()))
			return false;
		return true;

	}
	
	/**
	 * * Retorna se é possível acessar um documento ao qual pertence o móbil
	 * passado por parâmetro. Considera se o documento está sem efeito (não
	 * estando sem efeito, retorna verdadeiro) e se <i>uma das</i> seguintes
	 * condições é satisfeita:
	 * <ul>
	 * <li>Usuário é da lotação cadastrante do documento</li>
	 * <li>Usuário é subscritor do documento</li>
	 * <li>Usuário é titular do documento</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarSemEfeito(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (mob == null)
			return false;
		if (mob.getDoc().isSemEfeito()
				&& !mob.getDoc().getLotaCadastrante().equivale(lotaTitular)
				&& !titular.equivale(mob.getDoc().getSubscritor())
				&& !titular.equivale(mob.getDoc().getTitular()))
			return false;
		return true;

	}

	/**
	 * Retorna se é possível acessar o documento ao qual pertence o móbil
	 * passado por parâmetro, analisando <i>podeAcessarAberto()</i>,
	 * <i>podeAcessarCancelado()</i> e <i>podeAcessarPorNivel()</i>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAcessarDocumentoAntigo(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		
		if(mob.getDoc().getOrgaoUsuario() != null  
				&& mob.getDoc().getOrgaoUsuario().getIdOrgaoUsu() != null) {
			if(podePorConfiguracao(titular, lotaTitular, CpTipoConfiguracao.TIPO_CONFIG_ACESSAR, null, null, null, null, null, mob.getDoc().getOrgaoUsuario())) {
				return true;	
			}
		}		
		
		for (DpPessoa autorizado : mob.getDoc().getSubscritorECosignatarios()) {
			if (titular.equivale(autorizado))
				return true;
		}	
		
		if (pessoaTemPerfilVinculado(mob.getDoc(), titular, lotaTitular)) {
			return true;
		}
		

		return /*
				 * podeAcessarPublico(titular, lotaTitular, mob) &&
				 */podeAcessarAberto(titular, lotaTitular, mob)
				&& podeAcessarPorNivel(titular, lotaTitular, mob)
				&& podeAcessarCancelado(titular, lotaTitular, mob)
				&& podeAcessarSemEfeito(titular, lotaTitular, mob);
	}

	public boolean podeAcessarDocumento(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		ExDocumento doc = mob.getDoc();

		if(doc.getOrgaoUsuario() != null  
				&& doc.getOrgaoUsuario().getIdOrgaoUsu() != null) {
			if(podePorConfiguracao(titular, lotaTitular, CpTipoConfiguracao.TIPO_CONFIG_ACESSAR, null, null, null, null, null, doc.getOrgaoUsuario())) {
				return true;	
			}
		}		
		
		if (doc.getDnmAcesso() == null || doc.isDnmAcessoMAisAntigoQueODosPais()) {
			Ex.getInstance().getBL().atualizarDnmAcesso(doc);
		}
		AcessoConsulta ac = new AcessoConsulta(titular == null ? 0L
				: titular.getIdInicial(), lotaTitular == null ? 0
				: lotaTitular.getIdInicial(), titular == null ? 0L : titular
				.getOrgaoUsuario().getId(), lotaTitular == null ? 0L
				: lotaTitular.getOrgaoUsuario().getId());
		return ac.podeAcessar(doc.getDnmAcesso());
	}


	/**
	 * Retorna se é possível acessar documento reservado, considerando se o
	 * documento é reservado entre lotações (se não for, retorna verdadeiro) e
	 * se <i>podeAcessarReservadoEntreLotacoes().</i>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarReservado(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		switch (mob.getDoc().getExNivelAcessoAtual().getGrauNivelAcesso().intValue()) {
		case (int) ExNivelAcesso.NIVEL_RESERVADO_ENTRE_LOTACOES:
			return podeAcessarReservadoEntreLotacoes(titular, lotaTitular, mob);
		default:
			return true;
		}
	}

	/**
	 * Retorna se é possível acessar o documento a que pertence o móbil passado
	 * por parâmetro, considerando seu nível de acesso, e também o seguinte:
	 * <ul>
	 * <li>Se há documento pai e usuário pode acessá-lo <i>por nível</i>,
	 * retorna verdadeiro</li>
	 * <li>Se o usuário tem perfil vinculado ao documento, retorna verdadeiro</li>
	 * <li>Nos demais casos, retorna conforme a resposta de
	 * <i>podeAcessarPorNivelN()</i>, dependendo do grau do nível de acesso do
	 * documento</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception 
	 */
	public boolean podeAcessarPorNivel(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		if (mob == null)
			return false;

		if (mob.getDoc().getExNivelAcessoAtual() == null) {
			return true;
		}
		
		if (mob.getExMobilPai() != null
				&& podeAcessarPorNivel(titular, lotaTitular, mob
						.getExMobilPai()))
			return true;

		if (pessoaTemPerfilVinculado(mob.getDoc(), titular, lotaTitular)) {
			return true;
		}
		
		// Verifica se o titular é subscritor de algum despacho do documento
		if (mob.getDoc().getSubscritorDespacho().contains(titular))
            return true;
		
		// Verifica se o titular é subscritor ou cosignatário do documento
		if (mob.getDoc().getSubscritorECosignatarios().contains(titular))
			return true;

		// for (int k = numViaIniBusca; k <= numViaFimBusca; k++)
		switch (mob.getDoc().getExNivelAcessoAtual().getGrauNivelAcesso().intValue()) {
		case (int) ExNivelAcesso.NIVEL_ACESSO_ENTRE_ORGAOS:
			return podeAcessarNivel20(titular, lotaTitular, mob);
		case (int) ExNivelAcesso.NIVEL_ACESSO_PUBLICO:
			return podeAcessarPublico(titular, lotaTitular, mob);
		case (int) ExNivelAcesso.NIVEL_ACESSO_PESSOA_SUB:
			return podeAcessarNivel30(titular, lotaTitular, mob);
		case (int) ExNivelAcesso.NIVEL_ACESSO_SUB_PESSOA:
			return podeAcessarNivel40(titular, lotaTitular, mob);
		case (int) ExNivelAcesso.NIVEL_ACESSO_ENTRE_LOTACOES:
			return podeAcessarNivel60(titular, lotaTitular, mob);
		case (int) ExNivelAcesso.NIVEL_ACESSO_PESSOAL:
			return podeAcessarNivel100(titular, lotaTitular, mob);
		default:
			return true;
		}
	}

	/**
	 * Retorna se é possível acessar documento público. Sempre verdade.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarPublico(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return true;
	}

	/**
	 * Retorna se é possível acessar o documento que contém o móbil passado por
	 * parâmetro. Não é checado o nível de acesso. Presume-se que o documento
	 * seja limitado ao órgão. <i>Uma das</i> seguintes condições tem de ser
	 * satisfeita:
	 * <ul>
	 * <li>Órgão a que pertence a lotação passada por parâmetro tem de ser o
	 * órgão da lotação cadastrante do documento</li>
	 * <li>Usuário é o subscritor do documento, não importando de que órgão seja
	 * </li>
	 * <li>Usuário é o titular do documento, não importando de que órgão seja</li>
	 * <li>Usuário é o destinatario do documento ou da lotação deste, não
	 * importando de que órgão seja</li>
	 * <li><i>jaEsteveNoOrgao()</i> é verdadeiro para esses parâmetros</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarNivel20(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if ((lotaTitular.getOrgaoUsuario().equivale(mob.getDoc()
				.getLotaCadastrante().getOrgaoUsuario()))
				|| (mob.getDoc().getSubscritor() != null && mob.getDoc()
						.getSubscritor().equivale(titular))
				|| (mob.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular))
				|| (mob.getDoc().getDestinatario() != null && mob.getDoc()
						.getDestinatario().equivale(titular))
				|| (mob.getDoc().getLotaDestinatario() != null && mob.getDoc()
						.getLotaDestinatario().equivale(lotaTitular))
				|| (jaEsteveNoOrgao(mob.getDoc(), titular, lotaTitular)))
			return true;
		else
			return false;
	}

	/**
	 * Retorna se um documento já esteve num órgão (TRF, JFRJ, JFES),
	 * verificando se alguma movimentação de algum móbil do documento teve
	 * lotação atendente pertencente ao órgão onde está a lotação passada por
	 * parâmetro ou se, tendo sido definida uma pessoa atendente para a
	 * movimentação, o órgão a que a pessoa <i>pertencia*</i> é o órgão da
	 * pessoa passada por parâmetro. <br/>
	 * * Rever o restante deste documento, se os verbos estão no tempo correto
	 * 
	 * @param doc
	 * @param titular
	 * @param lotaTitular
	 * @return
	 */
	public static boolean jaEsteveNoOrgao(ExDocumento doc, DpPessoa titular,
			DpLotacao lotaTitular) {
		// TODO Auto-generated method stub
		for (ExMobil m : doc.getExMobilSet()) {
			for (ExMovimentacao mov : m.getExMovimentacaoSet()) {
				if ((mov.getLotaResp() != null && mov.getLotaResp()
						.getOrgaoUsuario().equivale(
								lotaTitular.getOrgaoUsuario()))
						|| (mov.getResp() != null && mov.getResp()
								.getOrgaoUsuario().equivale(
										titular.getOrgaoUsuario())))
					return true;
			}
		}
		return false;
	}

	/**
	 * Retorna se é possível acessar o documento que contém o móbil passado por
	 * parâmetro. Não é checado o nível de acesso. Presume-se que o documento
	 * seja limitado de pessoa para subsecretaria. <i>Uma das</i> seguintes
	 * condições tem de ser satisfeita:
	 * <ul>
	 * <li>Usuário é o próprio cadastrante do documento</li>
	 * <li>Usuário é o subscritor do documento</li>
	 * <li>Usuário é o titular do documento</li>
	 * <li>Usuário pertence à subsecretaria da lotação destinatária do documento
	 * </li>
	 * <li><i>pessoaJaTeveAcesso()</i> é verdadeiro para esses parâmetros</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarNivel30(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		DpLotacao subLotaTitular = getSubsecretaria(lotaTitular);
		DpLotacao subLotaDest = getSubsecretaria(mob.getDoc()
				.getLotaDestinatario());

		if (titular.equivale(mob.getDoc().getCadastrante())
				|| (mob.getDoc().getSubscritor() != null && mob.getDoc()
						.getSubscritor().equivale(titular))
				|| (mob.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular))
				|| (subLotaTitular.equivale(subLotaDest))
				|| pessoaJaTeveAcesso(mob.getDoc(), titular, lotaTitular))
			return true;
		else
			return false;
	}

	/**
	 * Retorna se é possível acessar o documento que contém o móbil passado por
	 * parâmetro. Não é checado o nível de acesso. Presume-se que o documento
	 * seja limitado de subsecretaria para pessoa. <i>Uma das</i> seguintes
	 * condições tem de ser satisfeita:
	 * <ul>
	 * <li>Usuário pertence à subsecretaria da lotação cadastrante do documento</li>
	 * <li>Usuário é o destinatário do documento</li>
	 * <li>Usuário é o subscritor do documento</li>
	 * <li>Usuário é o titular do documento</li>
	 * <li>Usuário é da lotação destinatária do documento, se não tiver sido
	 * definida pessoa destinatária</li>
	 * <li><i>pessoaJaTeveAcesso()</i> é verdadeiro para esses parâmetros</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarNivel40(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		DpLotacao subLotaTitular = getSubsecretaria(lotaTitular);
		DpLotacao subLotaDoc = getSubsecretaria(mob.getDoc().getLotaCadastrante());

		if ((subLotaTitular.equivale(subLotaDoc))
				|| (mob.getDoc().getDestinatario() != null && mob.getDoc()
						.getDestinatario().equivale(titular))
				|| (mob.getDoc().getSubscritor() != null && mob.getDoc()
						.getSubscritor().equivale(titular))
				|| (mob.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular))
				|| (mob.getDoc().getDestinatario() == null
						&& mob.getDoc().getLotaDestinatario() != null && mob.getDoc()
						.getLotaDestinatario().equivale(lotaTitular))
				|| pessoaJaTeveAcesso(mob.getDoc(), titular, lotaTitular))
			return true;
		else
			return false;

	}

	/**
	 * Retorna se é possível acessar documento reservado entre lotações, com
	 * base em <i>podeAcessarPorNivel20()</i> e <i>podeAcessarPorNivel60().</i>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarReservadoEntreLotacoes(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return podeAcessarNivel20(titular, lotaTitular, mob)
				&& podeAcessarNivel60(titular, lotaTitular, mob);
	}

	/**
	 * Retorna se é possível acessar o documento que contém o móbil passado por
	 * parâmetro. Não é checado o nível de acesso. Presume-se que o documento
	 * seja limitado entre lotações. <i>Uma das</i> seguintes condições tem de
	 * ser satisfeita:
	 * <ul>
	 * <li>Usuário é da lotação cadastrante do documento</li>
	 * <li>Usuário é o subscritor do documento</li>
	 * <li>Usuário é o titular do documento</li>
	 * <li>Usuário pertence à subsecretaria da lotação destinatária do documento
	 * </li>
	 * <li>Usuário é da lotação destinatária do documento</li>
	 * <li><i>jaPassouPor()</i> é verdadeiro para esses parâmetros</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarNivel60(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if ((lotaTitular.equivale(mob.getDoc().getLotaCadastrante()))
				|| (mob.getDoc().getSubscritor() != null && mob.getDoc()
						.getSubscritor().equivale(titular))
				|| (mob.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular))
				|| (mob.getDoc().getLotaDestinatario() != null && mob.getDoc()
						.getLotaDestinatario().equivale(lotaTitular))
				|| (jaPassouPor(mob.getDoc(), lotaTitular)))
			return true;
		else
			return false;
	}

	/**
	 * Retorna se é possível acessar o documento que contém o móbil passado por
	 * parâmetro. Não é checado o nível de acesso. Presume-se que o documento
	 * seja limitado entre pessoas. <i>Uma das</i> seguintes condições tem de
	 * ser satisfeita:
	 * <ul>
	 * <li>Usuário é o próprio cadastrante do documento</li>
	 * <li>Usuário é o subscritor do documento</li>
	 * <li>Usuário é o titular do documento</li>
	 * <li>Usuário é o próprio destinatário do documento</li>
	 * <li>Usuário é da lotação destinatária do documento</li>
	 * <li><i>pessoaJaTeveAcesso()</i> é verdadeiro para esses parâmetros</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeAcessarNivel100(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (titular.equivale(mob.getDoc().getCadastrante())
				|| (mob.getDoc().getSubscritor() != null && mob.getDoc()
						.getSubscritor().equivale(titular))
				|| (mob.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular))
				|| (mob.getDoc().getDestinatario() != null && mob.getDoc()
						.getDestinatario().equivale(titular))
				|| pessoaJaTeveAcesso(mob.getDoc(), titular, lotaTitular))
			return true;
		else
			return false;
	}

	/**
	 * Retorna se é possível anexar arquivo a um móbil. As condições são as
	 * seguintes:
	 * <ul>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar juntado</li>
	  * <li>Móbil não pode estar arquivado</li>
	 * <li>Volume não pode estar encerrado</li>
	 * <li>Móbil tem de estar finalizado</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o móbil / usuário</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAnexarArquivo(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		Boolean podePorConf = podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR, null, null, null, null, null, null);
	
		if (mob.getDoc().isFinalizado()) {
			return !mob.isEmTransito()
					&& (!mob.isGeral() || (mob.getDoc().isExterno() && !mob.getDoc().jaTransferido()))
					&& !mob.isJuntado()
					&& !mob.isArquivado()
					&& !mob.isVolumeEncerrado()
					&& !mob.isSobrestado()
					&& podeMovimentar(titular, lotaTitular, mob)
					&& !mob.getDoc().isSemEfeito()
					&& podePorConf;
		}
		
		if(mob.isGeral() && mob.getDoc().isProcesso())
			return false;
			
		return mob.isGeral() && podePorConf;
	}

	/**
	 * Retorna se é possível incluir uma cópia a um móbil. As condições são as
	 * seguintes:
	 * <ul>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar juntado</li>
	  * <li>Móbil não pode estar arquivado</li>
	 * <li>Volume não pode estar encerrado</li>
	 * <li>Móbil tem de estar finalizado</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o móbil / usuário</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCopiar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		Boolean podePorConf = podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_COPIA,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR, null, null, null, null, null, null);
	
		return mob.getDoc().isFinalizado() && !mob.isEmTransito()
				&& (!mob.isGeral() || mob.getDoc().isExterno())
				&& !mob.isJuntado()
				&& !mob.isArquivado()
				&& !mob.isVolumeEncerrado()
				&& !mob.isSobrestado()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.getDoc().isSemEfeito()
				&& podePorConf;
	}

	/**
	 * Retorna um configuração existente para a combinação dos dados passados
	 * como parâmetros, caso exista.
	 * 
	 * @param titularIniciador
	 * @param lotaTitularIniciador
	 * @param tipoConfig
	 * @param procedimento
	 * @param raia
	 * @param tarefa
	 * @return
	 * @throws Exception
	 */
	private ExConfiguracao preencherFiltroEBuscarConfiguracao(
			DpPessoa titularIniciador, DpLotacao lotaTitularIniciador,
			long tipoConfig, long tipoMov, ExTipoDocumento exTipoDocumento,
			ExTipoFormaDoc exTipoFormaDoc, ExFormaDocumento exFormaDocumento,
			ExModelo exModelo, ExClassificacao exClassificacao, ExVia exVia,
			ExNivelAcesso exNivelAcesso, ExPapel exPapel, DpPessoa pessoaObjeto, 
			DpLotacao lotacaoObjeto, CpComplexo complexoObjeto, DpCargo cargoObjeto, 
			DpFuncaoConfianca funcaoConfiancaObjeto, CpOrgaoUsuario orgaoObjeto) {
		ExConfiguracao cfgFiltro = new ExConfiguracao();

		cfgFiltro.setCargo(titularIniciador.getCargo());
		cfgFiltro.setOrgaoUsuario(lotaTitularIniciador.getOrgaoUsuario());
		cfgFiltro.setFuncaoConfianca(titularIniciador.getFuncaoConfianca());
		cfgFiltro.setLotacao(lotaTitularIniciador);
		cfgFiltro.setDpPessoa(titularIniciador);
		cfgFiltro.setCpTipoConfiguracao(CpDao.getInstance().consultar(
				tipoConfig, CpTipoConfiguracao.class, false));
		if (cfgFiltro.getCpTipoConfiguracao() == null)
			throw new RuntimeException(
					"Não é permitido buscar uma configuração sem definir seu tipo.");
		if (tipoMov != 0)
			cfgFiltro.setExTipoMovimentacao(CpDao.getInstance().consultar(
					tipoMov, ExTipoMovimentacao.class, false));
		cfgFiltro.setExTipoDocumento(exTipoDocumento);
		cfgFiltro.setExTipoFormaDoc(exTipoFormaDoc);
		cfgFiltro.setExFormaDocumento(exFormaDocumento);
		cfgFiltro.setExModelo(exModelo);
		cfgFiltro.setExClassificacao(exClassificacao);
		cfgFiltro.setExVia(exVia);
		cfgFiltro.setExNivelAcesso(exNivelAcesso);
		cfgFiltro.setExPapel(exPapel);
		cfgFiltro.setPessoaObjeto(pessoaObjeto);
		cfgFiltro.setLotacaoObjeto(lotacaoObjeto);
		cfgFiltro.setComplexoObjeto(complexoObjeto);
		cfgFiltro.setCargoObjeto(cargoObjeto);
		cfgFiltro.setOrgaoObjeto(orgaoObjeto);

		ExConfiguracao cfg = (ExConfiguracao) getConfiguracaoBL()
				.buscaConfiguracao(cfgFiltro, new int[] { 0 }, null);

		// Essa linha é necessária porque quando recuperamos um objeto da classe
		// WfConfiguracao do TreeMap estático que os armazena, este objeto está
		// detached, ou seja, não está conectado com a seção atual do hibernate.
		// Portanto, quando vamos acessar alguma propriedade dele que seja do
		// tipo LazyRead, obtemos um erro. O método lock, attacha ele novamente
		// na seção atual.
		
		// Dasabilitado porque estava dando erro de "Illegal attempt to associate a collection with two open sessions"
		//if (cfg != null)
		//	ExDao.getInstance().getSessao().lock(cfg, LockMode.NONE);

		return cfg;
	}

	/**
	 * Verifica se uma pessoa ou lotação tem permissão em uma configuração
	 * passada como parâmetro.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param tipoConfig
	 * @param tipoMovimentacao
	 *            - Configuração que terá a permissão verificada.
	 * @return
	 * @throws Exception
	 */
	private Boolean podePorConfiguracao(DpPessoa titular,
			DpLotacao lotaTitular, long tipoMov, long tipoConfig, DpPessoa pessoaObjeto, 
			DpLotacao lotacaoObjeto, CpComplexo complexoObjeto, DpCargo cargoObjeto, 
			DpFuncaoConfianca funcaoConfiancaObjeto, CpOrgaoUsuario orgaoObjeto) {
		CpSituacaoConfiguracao situacao;
		
		ExConfiguracao cfg = preencherFiltroEBuscarConfiguracao(titular,
				lotaTitular, tipoConfig, tipoMov, null, null, null, null, null, null, null, null, pessoaObjeto, 
				lotacaoObjeto, complexoObjeto, cargoObjeto, 
				funcaoConfiancaObjeto, orgaoObjeto);

		if (cfg != null) {
			situacao = cfg.getCpSituacaoConfiguracao();
		} else {
			situacao = CpDao.getInstance().consultar(tipoConfig,
					CpTipoConfiguracao.class, false).getSituacaoDefault();

		}

		if (situacao != null
				&& situacao.getIdSitConfiguracao() == CpSituacaoConfiguracao.SITUACAO_PODE)
			return true;
		return false;
	}

	private Boolean podePorConfiguracao(DpPessoa titular,
			DpLotacao lotaTitular, long tipoConfig) {
		return podePorConfiguracao(titular, lotaTitular, 0L, tipoConfig, null, null, null, null, null, null);
	}
	
	private Boolean podePorConfiguracao(DpPessoa titular,
			DpLotacao lotaTitular, long tipoConfig, DpPessoa pessoaObjeto, 
			DpLotacao lotacaoObjeto, CpComplexo complexoObjeto, DpCargo cargoObjeto, 
			DpFuncaoConfianca funcaoConfiancaObjeto, CpOrgaoUsuario orgaoObjeto) {
		return podePorConfiguracao(titular, lotaTitular, 0L, tipoConfig, pessoaObjeto, lotacaoObjeto, complexoObjeto, cargoObjeto, funcaoConfiancaObjeto, orgaoObjeto);
	}

	/**
	 * Retorna se é possível fazer arquivamento corrente de um móbil, segundo as regras a

	 * seguir:
	 * <ul>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil tem de ser via ou geral de processo (caso em que se condidera a
	 * situação do último volume)</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeArquivarCorrente(final DpPessoa titular,
			final DpLotacao lotaTitular, ExMobil mob) {

		if (!(mob.isVia() || mob.isGeralDeProcesso())
				|| mob.getDoc().isSemEfeito())
			return false;

		if (mob.isGeralDeProcesso() && mob.getDoc().isFinalizado())
			mob = mob.getDoc().getUltimoVolume();

		if (mob.getDoc().isEletronico()
				&& (mob.temAnexosNaoAssinados() || mob
						.temDespachosNaoAssinados() || mob.getDoc().getMobilGeral().temAnexosNaoAssinados()
						|| mob.getDoc().getMobilGeral().temDespachosNaoAssinados()))
			return false;

		return mob != null
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.isArquivado()
				&& !mob.isSobrestado()
				&& !mob.isJuntado()
				&& !mob.isEmTransito()
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_ARQUIVAMENTO_CORRENTE,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	/**
	 * Retorna se é possível fazer sobrestar um móbil, segundo as
	 * regras a seguir:


	 * <ul>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil tem de ser via ou volume (não pode ser geral)</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeSobrestar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if(mob.getDoc().isPendenteDeAssinatura())
			return false;
		
		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();
		
		return mob.getDoc().isFinalizado()
				&& (mob.isVia() || mob.isVolume())
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.isArquivado()
				&& !mob.isApensadoAVolumeDoMesmoProcesso()
				&& !mob.isSobrestado()
				&& !mob.isJuntado()
				&& !mob.isEmTransito()
				&& !mob.getDoc().isSemEfeito()
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_SOBRESTAR,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível tornar um documento sem efeito, segundo as
	 * regras a seguir:


	 * <ul>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil tem de ser via ou volume (não pode ser geral)</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeTornarDocumentoSemEfeito(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();
		
		if(mob.getDoc().isSemEfeito())
			return false;
		
		if(!mob.getDoc().isEletronico() || mob.getDoc().isPendenteDeAssinatura())
			return false;
		
		if(mob.getDoc().getSubscritor() == null || !mob.getDoc().getSubscritor().equivale(titular))
			return false;
		
		//Verifica se o documento está com pedido de publicação no DJE ou BIE.
		if(mob.getDoc().isPublicacaoSolicitada() ||  
				mob.getDoc().isPublicacaoAgendada() || 	
				mob.getDoc().isPublicacaoBoletimSolicitada() ||
				mob.getDoc().isBoletimPublicado() ||
				mob.getDoc().isDJEPublicado()) 
			return false;
				
		return  getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_TORNAR_SEM_EFEITO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}	
	
	/**
	 * Retorna se é possível criar subprocesso, segundo as regras abaixo:
	 * <ul>
	 * <li>Documento tem de ser processo</li>
	 * <li>Móbil não pode ser geral</li>

	 * <li>Documento não pode ter um móbil pai</li>
	 * <li>Documento não pode estar cancelado</li>
	 * <li>Usuário tem de ter permissão para acessar o documento que contém o
	 * móbil. <b>É mesmo necessário verificar isso?</b></li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração: Criar
	 * Documento Filho</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCriarSubprocesso(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (!mob.getDoc().isProcesso())
			return false;
		if (!mob.isGeral())
			return false;

		if (mob.getDoc().getExMobilPai() != null)
			return false;

		return !mob.getDoc().isCancelado()
				&& !mob.getDoc().isSemEfeito()
				&& !mob.isArquivado()
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& podeAcessarDocumento(titular, lotaTitular, mob)
				&& podePorConfiguracao(titular, lotaTitular,
						CpTipoConfiguracao.TIPO_CONFIG_CRIAR_DOC_FILHO);
	}

	/**
	 * Retorna se é possível criar um documento filho do móbil passado como
	 * parâmetro, de acordo com as regras:
	 * <ul>
	 * <li>Documento não pode estar cancelado</li>
	 * <li>Volume não pode estar encerrado, pelo fato de documento filho
	 * representar conteúdo agregado ao móbil</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCriarDocFilho(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		return !mob.getDoc().isCancelado()
				&& !mob.getDoc().isSemEfeito()
				&& !mob.isVolumeEncerrado()
				&& !mob.isArquivado()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& podePorConfiguracao(titular, lotaTitular,
						CpTipoConfiguracao.TIPO_CONFIG_CRIAR_DOC_FILHO);
	}

	/**
	 * Retorna se é possível fazer simulação, com base na configuração de tipo
	 * "Pode Simular Usuário"
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @return
	 * @throws Exception
	 */
	public boolean podeSimularUsuario(final DpPessoa titular,
			final DpLotacao lotaTitular) throws Exception {
		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_SIMULAR_USUARIO);
	}

	/**
	 * Retorna se é possível mostrar o link para arquivamento intermediário de
	 * um móbil, de acordo com as condições a seguir:
	 * <ul>
	 * <li>Móbil tem de ser via ou geral de processo</li>

	 * <li>Móbil tem de estar assinado</li>
	 * <li>Móbil tem de estar arquivado corrente</li>
	 * <li>PCTT tem de prever, para o móbil, tempo de permanência no arquivo
	 * intermediário</li>
	 * <li>Móbil não pode estar arquivado intermediário nem permanente</li>
	 * <li>Móbil não pode estar em edital de eliminação</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */

	public boolean podeBotaoArquivarIntermediario(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!(mob.isVia() || mob.isGeralDeProcesso())
				|| mob.getDoc().isSemEfeito() || mob.isEliminado())
			return false;

		return mob != null
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& mob.isArquivadoCorrente()
				&& mob.temTemporalidadeIntermediario()
				&& !mob.isArquivadoIntermediario()
				&& !mob.isArquivadoPermanente()
				&& !mob.isEmEditalEliminacao()
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_ARQUIVAMENTO_INTERMEDIARIO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível fazer o arquivamento intermediário do móbil, ou
	 * seja, se é possível mostrar o link para movimentação e se, além disso, o
	 * móbil encontra-se na lotação titular ou é digital.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeArquivarIntermediario(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return podeBotaoArquivarIntermediario(titular, lotaTitular, mob)
				&& (lotaTitular.equivale(mob
						.getUltimaMovimentacaoNaoCancelada().getLotaResp()) || mob
						.getDoc().isEletronico());
	}

	/**
	 * Retorna se é possível exibir o link para arquivamento permanente de um
	 * móbil, de acordo com as condições a seguir:
	 * <ul>
	 * <li>Móbil tem de ser via ou geral de processo</li>
	 * <li>Móbil não pode estar sem efeito</li>
	 * <li>Móbil tem de estar assinado</li>
	 * <li>Móbil tem de estar arquivado corrente ou intermediário; não pode ter
	 * sido arquivado permanentemente</li>
	 * <li>Tem de estar prevista guarda permanente, seja por PCTT ou por
	 * indicação</li>
	 * <li>Móbil não pode estar em edital de eliminação</li>
	 * <li>Móbil não pode ter sido eliminado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeBotaoArquivarPermanente(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!(mob.isVia() || mob.isGeralDeProcesso())
				|| mob.getDoc().isSemEfeito() || mob.isEliminado())
			return false;


		return mob != null
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& ((!mob.temTemporalidadeIntermediario() && mob.isArquivadoCorrente()) || mob
						.isArquivadoIntermediario())
				&& !mob.isArquivadoPermanente()
				&& mob.isDestinacaoGuardaPermanente()
				&& !mob.isEmEditalEliminacao()
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_ARQUIVAMENTO_PERMANENTE,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível fazer o arquivamento permanente do móbil, ou seja,
	 * se é possível mostrar o link para movimentação e se, além disso, o móbil
	 * encontra-se na lotação titular ou é digital.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeArquivarPermanente(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return podeBotaoArquivarPermanente(titular, lotaTitular, mob)
				&& (lotaTitular.equivale(mob
						.getUltimaMovimentacaoNaoCancelada().getLotaResp()) || mob
						.getDoc().isEletronico());
	}
	
	/*
	 * Retorna se é possível assinar digitalmente o documento a que pertence o
	 * móbil passado por parâmetro, conforme as seguintes condições:
	 * <ul>
	 * <li>Documento não pode ser processo interno importado</li>
	 * <li>Usuário tem de ser cossignatário do documento ou subscritor ou
	 * [cadastrante, caso o documento seja externo], ou <i>podeMovimentar()</i>
	 * tem de ser verdadeiro para o móbil / usuário</li>
	 * <li>Documento tem de estar finalizado</li>
	 * <li>Documento não pode estar cancelado</li>
	 * <li>Documento não pode estar em algum arquivo nem eliminado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAssinar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if(mob.getDoc().isCancelado())
			return false;
		
		//Edson: este isEletronico() está aqui porque o físico deixa de estar pendente de 
		//assinatura logo que a primeira pessoa assina. Porém, isso não significa que os demais 
		//cossignatários não podem mais assinar
		if (mob.getDoc().isEletronico() && !mob.getDoc().isPendenteDeAssinatura())
			return false;

		if (mob.getExDocumento().isProcesso()
				&& mob.getExDocumento().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_FOLHA_DE_ROSTO)
			return false;

		if (mob.isArquivado() || mob.isEliminado())
			return false;
		
		if (mob.isPendenteDeColaboracao())
			return false;
		
		if (mob.getDoc().getSubscritor() == null)
			return false;
		
		if (mob.getDoc().isExternoCapturado()){
			if (mob.getDoc().getAutenticacoesComTokenOuSenha().isEmpty())
				return false;
		}

		//condições para assinatura digital de cosignatários em documentos físicos 
		if (mob.getDoc().isFisico() && mob.getDoc().isFinalizado() 
			&& ((mob.getDoc().getSubscritor() != null && mob.getDoc().getSubscritor().equivale(titular)) || mob.getDoc().isCossignatario(titular)))
			return true;
		
		// Se o subscritor ou algum cossignatário requer solicitação de assinatura, não deve permitir assinar sem ela
/*		if (!mob.doc().isAssinaturaSolicitada()) {
			if (!getConf().podePorConfiguracao(mob.doc().getSubscritor(), CpTipoConfiguracao.TIPO_CONFIG_PODE_ASSINAR_SEM_SOLICITACAO))
				return false;
			for (DpPessoa cossig : mob.doc().getCosignatarios()) { 
				if (!getConf().podePorConfiguracao(mob.doc().getSubscritor(), CpTipoConfiguracao.TIPO_CONFIG_PODE_ASSINAR_SEM_SOLICITACAO))
					return false;
			}
		}
*/		
		
		return (mob.getDoc().getSubscritor().equivale(titular)
				|| (mob.getDoc().isExterno() && mob.getDoc().getCadastrante().equivale(titular))
				|| (mob.getDoc().isCossignatario(titular) && mob.getDoc().isPendenteDeAssinatura() && mob.getDoc().isAssinadoPeloSubscritorComTokenOuSenha())
				|| podeMovimentar(titular, lotaTitular, mob))
				&& (mob.getDoc().isFinalizado() || podeFinalizar(titular, lotaTitular, mob))
				&& !mob.getDoc().isCancelado()
				&& !mob.getDoc().isSemEfeito()
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_DOCUMENTO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	/*
	 * Retorna se é possível assinar um documento com senha:
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAssinarComSenha(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		ExTipoMovimentacao exTpMov = ExDao.getInstance().consultar(ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_COM_SENHA,
				ExTipoMovimentacao.class, false);

		return getConf().podePorConfiguracao(null, null, null, null, mob.getDoc().getExFormaDocumento(), mob.getDoc().getExModelo(), null,
				null, exTpMov, null, null, null, lotaTitular, titular, null,null,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	/*
	 * Retorna se pode autenticar um documento que só foi assinado com senha.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAutenticarDocumento(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExDocumento doc) {
		
		if (!doc.isEletronico() || !doc.getAutenticacoesComTokenOuSenha().isEmpty())
			return false;
		
		if(SigaBaseProperties.getString("siga.local") != null && "GOVSP".equals(SigaBaseProperties.getString("siga.local")) &&
				!Long.valueOf(ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO_CAPTURADO).equals(Long.valueOf(doc.getExTipoDocumento().getId())) &&
				!Long.valueOf(ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_CAPTURADO).equals(Long.valueOf(doc.getExTipoDocumento().getId()))				
				) {
			return false;
		}
		
		return doc.isExternoCapturado() || doc.isInternoCapturado() || doc.getAssinaturasComSenha().size() > 0;
	}
	
	/*
	 * Retorna se pode revisar um documento.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeSolicitarAssinatura(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExDocumento doc) {
		
		if (doc.isAssinadoPorTodosOsSignatariosComTokenOuSenha())
			return false;
		
		if (doc.isAssinaturaSolicitada())
			return false;
		
		if (doc.getLotaSubscritor() == null)
			return false;
				
		ExTipoMovimentacao exTpMov = ExDao.getInstance().consultar(ExTipoMovimentacao.TIPO_MOVIMENTACAO_SOLICITACAO_DE_ASSINATURA,
				ExTipoMovimentacao.class, false);
		
		return getConf().podePorConfiguracao(null, null, null, null, doc.getExFormaDocumento(), doc.getExModelo(), null,
				null, exTpMov, null, null, null, lotaTitular, titular, null,null,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR, doc.getSubscritor(), null, null, null, null, null);
	}
	
	/*
	 * Retorna se pode autenticar uma movimentação que só foi assinada com senha.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAutenticarMovimentacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMovimentacao mov) {
		
		//Não é necessário autenticar movimentação de anexação pois o link para assinar/autenticar sempre está disponível. 
		if(mov.getExTipoMovimentacao().getIdTpMov().equals(ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO))
			return false;
		
		if (mov.getExDocumento().isEletronico() &&  !mov.isAutenticada() && mov.temAssinaturasComSenha()) {
			 return true;
		}

		return false;
	}
	
	/*
	 * Retorna se é possível assinar uma movimentação com senha:
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAssinarMovimentacaoComSenha(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMovimentacao mov) {

		ExTipoMovimentacao exTpMov = ExDao.getInstance().consultar(ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA,
				ExTipoMovimentacao.class, false);

		return getConf().podePorConfiguracao(null, null, null, null, mov.getExMobil().getExDocumento().getExFormaDocumento(), mov.getExMobil().getExDocumento().getExModelo(), null,
				null, exTpMov, null, null, null, lotaTitular, titular, null,null,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	/*
	 * Retorna se é possível assinar movimentações do mobil com senha:
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAssinarMovimentacaoComSenha(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {

		ExTipoMovimentacao exTpMov = ExDao.getInstance().consultar(ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA,
				ExTipoMovimentacao.class, false);

		return getConf().podePorConfiguracao(null, null, null, null, mob.getExDocumento().getExFormaDocumento(), mob.getExDocumento().getExModelo(), null,
				null, exTpMov, null, null, null, lotaTitular, titular, null,null,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	/*
	 * Retorna se é possível assinar uma movimentação com senha:
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAutenticarMovimentacaoComSenha(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMovimentacao mov) throws Exception {
		
		if(mov == null)
			return false;
		
		if(!mov.getExTipoMovimentacao().getIdTpMov().equals(ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO))
			return false;

		ExTipoMovimentacao exTpMov = ExDao.getInstance().consultar(ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA,
				ExTipoMovimentacao.class, false);

		return getConf().podePorConfiguracao(null, null, null, null, mov.getExMobil().getExDocumento().getExFormaDocumento(), mov.getExMobil().getExDocumento().getExModelo(), null,
				null, exTpMov, null, null, null, lotaTitular, titular, null,null,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}	
	
	/*
	 * Retorna se é possível cópia de um movimentações do mobil com senha:
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAutenticarComSenha(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		
		if(mob == null)
			return false;

		ExTipoMovimentacao exTpMov = ExDao.getInstance().consultar(ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA,
				ExTipoMovimentacao.class, false);

		return getConf().podePorConfiguracao(null, null, null, null, mob.getExDocumento().getExFormaDocumento(), mob.getExDocumento().getExModelo(), null,
				null, exTpMov, null, null, null, lotaTitular, titular, null,null,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}	
	public boolean podeSerSubscritor(final ExDocumento doc) {
		
		if(doc.isExterno() || doc.isExternoCapturado())
			return true;
		
		return podeSerSubscritor(doc.getTitular(), doc.getLotaTitular(), doc.getExModelo());
	}
	
	/**
	 * Retorna se é possível ser subscritor de um documento.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeSerSubscritor(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExModelo mod) {
		
		if(titular == null || mod == null)
			return false;
		
		return getConf()
				.podePorConfiguracao(
						titular,
						lotaTitular,
						titular.getCargo(),
						titular.getFuncaoConfianca(),
						mod.getExFormaDocumento(),
						mod,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_DOCUMENTO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível publicar o Boletim Interno que possui mob, segundo
	 * as regras:
	 * <ul>
	 * <li>Documento tem de estar finalizado</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Documento não pode estar publicado</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Movimentar / Publicação Boletim</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podePublicar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return (mob.getDoc().isFinalizado())
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& !mob.getDoc().isBoletimPublicado()
				&& !mob.getDoc().isSemEfeito()
				&& !mob.getDoc().isEliminado()
				&& !mob.isPendenteDeAnexacao()
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								mob.getDoc().getExModelo(),
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_PUBLICACAO_BOLETIM,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se uma pessoa já teve acesso a um documento. Para isso, verifica
	 * se pessoa já foi atendente de alguma das movimentações de qualquer móbil
	 * do documento, caso a movimentação não tenha como atendente toda uma
	 * lotação, ou se ela é da lotação atendente de alguma dessas movimentações.
	 * Se a movimentação em questão for uma redefinição de nível de acesso,
	 * verifica se foi o próprio usuário que cadastrou a redefinição.
	 * <b>Documentar a razão dessas regras.</b>
	 * 
	 * @param doc
	 * @param titular
	 * @param lotaTitular
	 * @return
	 */
	public static boolean pessoaJaTeveAcesso(ExDocumento doc, DpPessoa titular,
			DpLotacao lotaTitular) {
		for (ExMobil m : doc.getExMobilSet()) {
			for (ExMovimentacao mov : m.getExMovimentacaoSet()) {
				if ((mov.getLotaResp() != null
						&& mov.getLotaResp().equivale(lotaTitular) && mov
						.getResp() == null)
						|| (mov.getResp() != null && mov.getResp().equivale(
								titular)))
					return true;
				if (mov.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_REDEFINICAO_NIVEL_ACESSO
						&& ((mov.getLotaCadastrante() != null
								&& mov.getLotaCadastrante().equivale(
										lotaTitular) && mov.getCadastrante() == null) || (mov
								.getCadastrante() != null && mov
								.getCadastrante().equivale(titular))))
					return true;

			}
		}
		return false;
	}

	/**
	 * Retorna se uma pessoa/lotação está vinculada a um documento por meio de algum
	 * perfil. Para isso, verifica cada movimentação não cancelada de vinculação
	 * de perfil registrada no móbil geral do documento e analisa se a pessoa/lotação
	 * passada por parâmetro é <i>titular/lotaTitular</i> de alguma dessas movimentações.

	 * 
	 * @param doc
	 * @param titular
	 * @param lotaTitular
	 * @return
	 */
	public static boolean pessoaTemPerfilVinculado(ExDocumento doc,
			DpPessoa titular, DpLotacao lotaTitular) {
		for (ExMovimentacao mov : doc.getMobilGeral().getExMovimentacaoSet()) {

			if (!mov.isCancelada()
					&& mov
							.getExTipoMovimentacao()

							.getIdTpMov()
							.equals(
									ExTipoMovimentacao.TIPO_MOVIMENTACAO_VINCULACAO_PAPEL)) {

				if (mov.getSubscritor() != null){
					 if (mov.getSubscritor().equivale(titular)) {
						 return true;
				     }
				}else {
					if (mov.getLotaSubscritor().equivale(lotaTitular)){
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * Retorna se uma <i>lotação</i> já foi atendente em alguma movimentação
	 * (mesmo cancelada) de algum móbil do documento passado por parãmetro
	 * 
	 * @param doc
	 * @param lota
	 * @return
	 */
	public static boolean jaPassouPor(ExDocumento doc, DpLotacao lota) {
		for (ExMobil m : doc.getExMobilSet()) {
			for (ExMovimentacao mov : m.getExMovimentacaoSet())
				if (mov.getLotaResp() != null
						&& mov.getLotaResp().equivale(lota))
					return true;
		}
		return false;
	}

	/**
	 * Retorna se é possível exibir a opção de criar via em documento.
	 * <b>Verificar qual a utilidade desse método</b>. Condições:
	 * <ul>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Tem de ser possível criar via (<i>podeCriarVia()</i>)</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeBotaoCriarVia(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		
		if (mob.getDoc().isSemEfeito() || mob.getDoc().isEliminado())
			return false;

		if (!mob.isEmTransito() && podeCriarVia(titular, lotaTitular, mob)
				&& podeMovimentar(titular, lotaTitular, mob))
			// && (mob.doc().getNumUltimaViaNaoCancelada() == numVia))
			return true;

		return false;
	}

	/**
	 * Retorna se é possível desentranhar móbil de outro. Regras:
	 * <ul>
	 * <li>Móbil tem de ser via</li>
	 * <li>Móbil tem de estar juntado externo ou interno (verifica-se juntada
	 * interna pela existência de móbil pai)</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>A não ser que o móbil esteja juntado externo, <i>podeMovimentar()</i>
	 * tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil tem de estar juntado. <b>Obs.: essa checagem não torna
	 * desnecessários os processamentos acima?</b></li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Juntada</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarJuntada(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();

		if (!mob.isVia())
			return false;

		if (ultMovNaoCancelada == null)
			return false;

		ExMobil mobPai = null;
		if (!mob.isJuntadoExterno()) {
			mobPai = mob.getExMobilPai();
			if (mobPai == null)
				return false;
		}
		
		ExMobil mobUlt = mobPai;
		if (mobPai.isApensado()) {			
			mobUlt = mobPai.getGrandeMestre();			
		}

		if (mob.isEmTransito()
				|| mob.isCancelada()
				|| (!mob.isJuntadoExterno() && !podeMovimentar(titular,
						lotaTitular, mobUlt)) || (!mob.isJuntado()))
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_JUNTADA,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível cancelar a última movimentação não cancelada do
	 * móbil mob, segundo as regras abaixo.
	 * <ul>
	 * <li>Última movimentação não cancelada do móbil não pode ser apensação nem
	 * desapensação</li>
	 * <li>Última movimentação não cancelada do móbil não pode ser assinatura do
	 * documento ou de movimentação</li>
	 * <li>Última movimentação não cancelada do móbil não pode ser recebimento</li>
	 * <li>Última movimentação não cancelada do móbil não pode ser inclusão em
	 * edital de eliminação</li>
	 * <li>Última movimentação não cancelada do móbil não pode ser atualização
	 * resultante de assinatura do documento ou de movimentação</li>
	 * <li>Última movimentação não cancelada do móbil não pode ser publicação do
	 * Boletim nem notificação de publicação do Boletim</li>
	 * <li>Se a última movimentação não cancelada for agendamento de publicação
	 * direta no DJE, o usuário que tem permissão para atender pedidos de
	 * publicação indireta pode cancelar, não importando se
	 * <i>podeMovimentar()</i> é verdadeiro</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Se a última movimentação já for um cancelamento, não permite cancelar
	 * a última movimentação não cancelada, a não ser que a última movimentação
	 * seja cancelamento de atualização ou de recebimento transitório</li>
	 * <li>Apenas o usuário que seja da lotação cadastrante da última
	 * movimentação não cancelada pode cancelá-la, se for dos seguintes tipos:</li>
	 * <ul>
	 * <li>Transferência</li>
	 * <li>Transferência Externa</li>
	 * <li>Despacho Interno com Transferência</li>
	 * <li>Despacho com transferência Externa</li>
	 * <li>Despacho com Transferência</li>
	 * <li>Recebimento Transitório</li>
	 * <li>Recebimento</li>
	 * <li><b>Registro de Assinatura do Documento (desnecessário)</li>
	 * <li>Assinatura Digital do Documento (desnecessário)</b></li>
	 * </ul>
	 * <li>Excetuadas as condições acima, para cancelar a última movimentação
	 * não cancelada do móbil o usuário terá de ser 1) o atendente da
	 * movimentação, 2) o subscritor da movimentação, 3) o titular da
	 * movimentação ou 4) da lotação cadastrante da movimentação</li> <li>Se
	 * última movimentação não cancelada for de registro de assinatura, só deixa
	 * cancelar se não houver alguma movimentação posterior em alguma das vias.
	 * <b>Obs.: regra em desuso. Parece também haver erro no código
	 * (before(dt)?)</b></li> <li>Não pode haver configuração impeditiva. Tipo
	 * de configuração: Cancelar Movimentação</li> </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarMovimentacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		//Não deixa cancelar movimentação de um mobil diferente de geral quando um documento está sem efeito.

		if(!mob.isGeral() && mob.getDoc().isSemEfeito())
			return false;
		
		if (mob.isEliminado())
			return false;
			
		final ExMovimentacao exUltMovNaoCanc = mob
				.getUltimaMovimentacaoNaoCancelada();
		final ExMovimentacao exUltMov = mob.getUltimaMovimentacao();
		if (exUltMov == null || exUltMovNaoCanc == null)
			return false;
		
		//Só deixa cancelar movimentação de tornar documento sem efeito, se o titular for o subscritor do documento
		//Também não é permitido os cosignatários cancelar essa movimentação
		if(mob.isGeral() && 
				exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_TORNAR_SEM_EFEITO &&
				!exUltMovNaoCanc.getSubscritor().equivale(titular))



			return false;

		// Não deixa cancelar apensação ou desapensação
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_APENSACAO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESAPENSACAO)
			return false;

		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_INCLUSAO_EM_EDITAL_DE_ELIMINACAO)
			return false;
			
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO)
			return false;

		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_PENDENCIA_DE_ANEXACAO)
			return false;

		// Não deixa cancelar assinatura
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_REGISTRO_ASSINATURA_DOCUMENTO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_DOCUMENTO
						|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_COM_SENHA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_MOVIMENTACAO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_DOCUMENTO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA)
			return false;

		// Não deixa cancelar a atualização (por enquanto, só ser resultar
		// da
		// assinatura)
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ATUALIZACAO
				&& exUltMovNaoCanc.getExMovimentacaoRef() != null
				&& (exUltMovNaoCanc.getExMovimentacaoRef()
						.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_REGISTRO_ASSINATURA_DOCUMENTO || exUltMovNaoCanc
						.getExMovimentacaoRef().getExTipoMovimentacao()
						.getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_DOCUMENTO))
			return false;

		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_NOTIFICACAO_PUBL_BI
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_PUBLICACAO_BOLETIM
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_DISPONIBILIZACAO)
			return false;
		
		//Não deixa cancelar juntada quando o documento está juntado a um expediente/processo que já sofreu outra movimentação
		if(exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA) {
			
			if (exUltMovNaoCanc.getExMobilRef().isArquivado())
				return false;
			
			ExMovimentacao ultimaMovimentacaoDaReferencia = exUltMovNaoCanc.getExMobilRef().getUltimaMovimentacao();

			if(ultimaMovimentacaoDaReferencia.getExTipoMovimentacao().getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANOTACAO
					&& ultimaMovimentacaoDaReferencia.getData().after(exUltMovNaoCanc.getData()))
				return false;
			
			//Verifica se o mobil de referência já recebeu outras movimentações depois da movimentação que vai ser cancelada.
			if(mob.getDoc().isEletronico()
					&& exUltMovNaoCanc.getExMobilRef() != null
					&& exUltMovNaoCanc.getExMobilRef().getDoc().isNumeracaoUnicaAutomatica()) {
				
				for (ExMovimentacao movDoMobilRef : exUltMovNaoCanc.getExMobilRef().getCronologiaSet()) {
					if(movDoMobilRef.getIdMov().equals(exUltMovNaoCanc.getIdMov()))
						break;

					if(!movDoMobilRef.isCancelada() &&
							movDoMobilRef.getExTipoMovimentacao().getId() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_REFERENCIA &&
							movDoMobilRef.getExTipoMovimentacao().getId() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANOTACAO &&
							movDoMobilRef.getDtIniMov().after(exUltMovNaoCanc.getDtIniMov()))
						return false;
				}
			}
		}
		

		// Verifica se a última movimentação não cancelada é agendamento de
		// publicação no DJE
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_AGENDAMENTO_DE_PUBLICACAO
				&& podeAtenderPedidoPublicacao(titular, lotaTitular, mob))
			return true;


		// Não deixa cancelar a mov se a via estiver cancelada ou há um
		// cancelamento imediatamente anterior, a não ser se este for
		// cancelamento de receb transitório ou de atualização
		if (mob.isCancelada()
				|| (exUltMovNaoCanc.getIdMov() != exUltMov.getIdMov()
						&& exUltMov.getExMovimentacaoRef() != null
						&& exUltMov.getExMovimentacaoRef()
								.getExTipoMovimentacao().getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO_TRANSITORIO && exUltMov
						.getExMovimentacaoRef().getExTipoMovimentacao()
						.getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_ATUALIZACAO)
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CRIACAO)
			return false;
		// Essas só a lota do cadastrante pode cancelar
		else if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_TRANSFERENCIA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_TRANSFERENCIA_EXTERNA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_INTERNO_TRANSFERENCIA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_TRANSFERENCIA_EXTERNA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_TRANSFERENCIA
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO_TRANSITORIO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_REGISTRO_ASSINATURA_DOCUMENTO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_DOCUMENTO
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_SOLICITACAO_DE_ASSINATURA) {
			return exUltMovNaoCanc.getLotaTitular().equivale(lotaTitular);
		} else {
			if (exUltMovNaoCanc.getLotaResp() != null) {
				if (!exUltMovNaoCanc.getLotaResp().equivale(lotaTitular))
					return false;
			} else if (exUltMovNaoCanc.getSubscritor() != null) {
				if (!exUltMovNaoCanc.getSubscritor().getLotacao().equivale(
						lotaTitular))
					return false;
			} else if (exUltMovNaoCanc.getTitular() != null) {
				if (!exUltMovNaoCanc.getTitular().getLotacao().equivale(
						lotaTitular))
					return false;
			} else {
				if (!exUltMovNaoCanc.getLotaCadastrante().equivale(lotaTitular))
					return false;
			}
		}

		// Antes de deixar cancelar a assinatura, vê antes se houve
		// movimentações posteriores em qualquer via
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_REGISTRO_ASSINATURA_DOCUMENTO) {
			Date dt = exUltMovNaoCanc.getDtIniMov();
			for (ExMobil m : mob.getDoc().getExMobilSet()) {
				ExMovimentacao move = m.getUltimaMovimentacaoNaoCancelada();
				if (move != null && move.getDtIniMov().before(dt)) {
					return false;
				}
			}
		}
		
		//Não deixa desfazer os antigos arquivamentos feitos em volume de processo
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ARQUIVAMENTO_CORRENTE 
				&& !podeDesarquivarCorrente(titular, lotaTitular, mob)) {
			return false;
			
		}
		
		if (exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO 
				&& !podeCancelarAnexo(titular, lotaTitular, mob, exUltMovNaoCanc)) {
			return false;
			
		}

		return getConf()
				.podePorConfiguracao(
						titular,
						lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_MOVIMENTACAO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível cancelar uma movimentação mov, segundo as regras
	 * abaixo. <b>Método em desuso?</b>
	 * <ul>
	 * <li>Movimentação não pode estar cancelada</li>
	 * <li>Usuário tem de ser da lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Movimentação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) throws Exception {
		if (mov == null)
			return false;

		if (mob.isCancelada())
			return false;

		if ((!mov.getIdTpMov().equals(ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO_DE_ARQUIVO_AUXILIAR)) 
				&& !mov.getLotaCadastrante().equivale(lotaTitular))
			return false;
		
		return getConf().podePorConfiguracao(titular, lotaTitular,
				mov.getIdTpMov(),
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}

	/**
	 * Retorna se é possível cancelar uma ciência de documento
	 * <ul>
	 * <li>Precisa ser via ou volume</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Não pode cancelar ciência se a última mov não for Ciência, Definir Marcação ou Definir Perfil
	 * <li>Última mov de ciência não pode ter sido cancelada</li>
	 * <li>Somente o usuário que criou a ciência pode desfazer a mesma</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarCiencia(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		ExMovimentacao movCiencia = null;

		if (!mob.isVia() && !mob.isVolume())
			return false;
		
		if (mob.isEmTransito()
				|| mob.isCancelada())
				return false;

		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();
		final ExMovimentacao ultMov = mob.getUltimaMovimentacao();
		
		if (ultMov == null || ultMovNaoCancelada == null)
			return false;

		if (ultMovNaoCancelada.getExTipoMovimentacao().getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_CIENCIA &&
				ultMovNaoCancelada.getExTipoMovimentacao().getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_MARCACAO &&
				ultMovNaoCancelada.getExTipoMovimentacao().getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_VINCULACAO_PAPEL)
			return false;
		
		Set <ExMovimentacao> setMovCiente = mob.getMovsNaoCanceladas(ExTipoMovimentacao.TIPO_MOVIMENTACAO_CIENCIA);

		if (setMovCiente == null) 
			return false;
		
		for (ExMovimentacao mov : setMovCiente) {
			if (mov.getCadastrante() != null &&  mov.getCadastrante().equivale(titular)) {
				movCiencia = mov;
				break;
			}
		}
		
		if (movCiencia == null || movCiencia.isCancelada())
				return false;
		
		return (mob.isCiente(titular)
				&& getConf()
					.podePorConfiguracao(
						titular,
						lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_MOVIMENTACAO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}

	/**
	 * Retorna se é possível cancelar a via mob, conforme estabelecido a seguir:
	 * <ul>
	 * <li>Móbil tem de ser via</li>
	 * <li>Documento que contém a via não pode estar assinado</li>
	 * <li>Via não pode estar cancelada</li>
	 * <li>Última movimentação não cancelada da via tem de ser a sua criação</li>
	 * <li>Não pode haver movimentações canceladas posteriores à criação</li>
	 * <li>Com relação à movimentação de criação (última movimentação não
	 * cancelada), o usuário tem de ser 1) da lotação atendente da movimentação,
	 * 2) o subscritor da movimentação, 3) o titular da movimentação ou 4) da
	 * lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarVia(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (!mob.isVia())
			return false;
		if (!mob.getExDocumento().isPendenteDeAssinatura())
			return false;
		final ExMovimentacao exUltMovNaoCanc = mob
				.getUltimaMovimentacaoNaoCancelada();
		final ExMovimentacao exUltMov = mob.getUltimaMovimentacao();
		if (mob.isCancelada()
				|| exUltMovNaoCanc.getExTipoMovimentacao().getIdTpMov() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_CRIACAO
				|| exUltMovNaoCanc.getIdMov() != exUltMov.getIdMov())
			return false;
		else if (exUltMovNaoCanc.getLotaResp() != null) {
			if (!exUltMovNaoCanc.getLotaResp().equivale(lotaTitular))
				return false;
		} else if (exUltMovNaoCanc.getSubscritor() != null) {
			if (!exUltMovNaoCanc.getSubscritor().getLotacao().equivale(
					lotaTitular))
				return false;
		} else if (exUltMovNaoCanc.getTitular() != null) {
			if (!exUltMovNaoCanc.getTitular().getLotacao()
					.equivale(lotaTitular))
				return false;
		} else {
			if (!exUltMovNaoCanc.getCadastrante().getLotacao().equivale(
					lotaTitular))
				return false;
		}
		
		//Não é possível cancelar a última via de um documento pois estava gerando erros nas marcas do mobil geral.
		boolean isUnicaViaNaoCancelada = true;
		for (ExMobil outroMobil : mob.getDoc().getExMobilSet()) {
			if(!outroMobil.isGeral() &&  !outroMobil.isCancelada()
					&& !outroMobil.getIdMobil().equals(mob.getIdMobil())) {
				isUnicaViaNaoCancelada = false;
				break;
			}
		}
		
		if(isUnicaViaNaoCancelada)
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_VIA);
	}

	/**
	 * Retorna se é possível criar via para o documento que contém o móbil
	 * passado por parâmetro, de acordo com as seguintes condições:
	 * <ul>
	 * <li>Documento tem de ser expediente</li>
	 * <li>Documento não pode ter pai, pois não é permitido criar vias em
	 * documento filho</li>
	 * <li>Número da última via não pode ser maior ou igual a 21</li>
	 * <li>Documento tem de estar finalizado</li>
	 * <li>Documento não pode ter sido eliminado</li>
	 * <li>Documento tem de possuir alguma via não cancelada</li>
	 * <li>Lotação do titular igual a do cadastrante ou a do subscritor ou 

	 * o titular ser o próprio subscritor</li>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception 
	 */
	public boolean podeCriarVia(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if(mob.getDoc().isSemEfeito())
			return false;

		if (!mob.getDoc().isExpediente())
			return false;
		
		if (mob.getDoc().isEliminado())
			return false;
			
		if (mob.getDoc().getMobilGeral().isPendenteDeColaboracao())
			return false;
			
		if (mob.getDoc().getExMobilPai() != null && mob.getDoc().isPendenteDeAssinatura())
			return false;

		if (mob.getDoc().getNumUltimaVia() >= 21)
			return false;

		if(mob.isEmTransito())
			return false;
		
		return mob.getDoc().getNumUltimaViaNaoCancelada() > 0
				&& mob.getDoc().isFinalizado() && 
				(podeMovimentar(titular, lotaTitular, mob) 
				   || mob.getDoc().getLotaCadastrante().equivale(lotaTitular)				        
				   || (mob.getDoc().getLotaSubscritor() != null && mob.getDoc().getLotaSubscritor().equivale(lotaTitular))
			       || (mob.getDoc().getSubscritor() != null &&  mob.getDoc().getSubscritor().equivale(titular))) // subscritor é null para documentos externos
		       && getConf().podePorConfiguracao(mob.getDoc().getExModelo(), CpTipoConfiguracao.TIPO_CONFIG_CRIAR_VIA);
	}

	/**
	 * Retorna se é possível criar volume para o documento que contém o móbil
	 * passado por parâmetro, de acordo com as seguintes condições:
	 * <ul>
	 * <li>Documento tem de ser processo</li>
	 * <li>Processo tem de estar finalizado</li>
	 * <li>Último volume tem de estar encerrado</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeCriarVolume(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!mob.getDoc().isProcesso())
			return false;

		if (mob.getDoc().getUltimoVolume() != null
				&& (mob.getDoc().getUltimoVolume().isEmTransito() || mob.getDoc()
						.getUltimoVolume().isSobrestado()))
			return false;
			
		if (mob.isArquivadoCorrente())
			return false;
		
		if (!podeMovimentar(titular, lotaTitular, mob))
			return false;
		
		if(mob.getDoc().isPendenteDeAssinatura())
			return false;
		
		if (mob.getDoc().isFinalizado()
				&& mob.getDoc().getUltimoVolume().isVolumeEncerrado()) {
			
			if(mob.getDoc().isEletronico() && 
					(mob.getDoc().getUltimoVolume().temAnexosNaoAssinados() || mob.getDoc().getUltimoVolume().temDespachosNaoAssinados()))
				return false;

			return true;
		}

		return false;
	}

	/**
	 * Retorna se é possível encerrar um volume, dadas as seguintes condições:
	 * <ul>
	 * <li>Móbil tem de ser volume</li>
	 * <li>Volume não pode estar encerrado</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode estar spbrestado</li>
	 * <li>Volume não pode estar em trânsito</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeEncerrarVolume(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if (!mob.isVolume())
			return false;

		if (mob.isVolumeEncerrado())
			return false;

		if (mob.isArquivado())
			return false;

		if (mob.isEmTransito())
			return false;
		
		if(mob.getDoc().isPendenteDeAssinatura())
			return false;

		if (mob.isSobrestado())
			return false;

		return podeMovimentar(titular, lotaTitular, mob)
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_ENCERRAMENTO_DE_VOLUME,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível reabrir um móbil, segundo as seguintes regras:
	 * <ul>
	 * <li>Móbil tem de ser via ou geral de processo.</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil tem de estar arquivado corrente ou intermediário, mas não permanentemente</li>
	 * <li>Móbil não pode estar em edital de eliminação</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDesarquivarCorrente(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!mob.isVia() && !mob.isGeralDeProcesso())
			return false;

		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();
		if (ultMovNaoCancelada == null)
			return false;
		return podeMovimentar(titular, lotaTitular, mob)
				&& (mob.isArquivadoCorrente() || mob.isArquivadoIntermediario())
				&& !mob.isArquivadoPermanente()
				&& !mob.isEmEditalEliminacao()
				&& !mob.isEmTransito()
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESARQUIVAMENTO_CORRENTE,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível reabrir um móbil, segundo as seguintes regras:
	 * <ul>
	 * <li>Móbil tem de ser via ou geral de processo.</li>
	 * <li>Móbil tem de estar em arquivo intermediário, não permanente</li>
	 * <li>Móbil não pode estar em edital de eliminação</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeBotaoDesarquivarIntermediario(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!mob.isVia() && !mob.isGeralDeProcesso())
			return false;

		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();
		if (ultMovNaoCancelada == null)
			return false;
		return (mob.isArquivadoIntermediario())
				&& !mob.isArquivadoPermanente()
				&& !mob.isEmEditalEliminacao()
				&& !mob.isEmTransito()
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESARQUIVAMENTO_INTERMEDIARIO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível fazer o desarquivamento intermediário do móbil, ou
	 * seja, se é possível mostrar o link para movimentação e se, além disso, o
	 * móbil encontra-se na lotação titular ou é digital.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDesarquivarIntermediario(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return podeBotaoDesarquivarIntermediario(titular, lotaTitular, mob)
				&& (lotaTitular.equivale(mob
						.getUltimaMovimentacaoNaoCancelada().getLotaResp()) || mob
						.getDoc().isEletronico());
	}

	/**
	 * Retorna se é possível desobrestar um móbil, segundo as seguintes regras:
	 * <ul>
	 * <li>Móbil tem de ser via ou volume. Não pode ser geral</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil tem de estar sobrestado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDesobrestar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if (!(mob.isVia() || mob.isVolume()))
			return false;
		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();
		if (ultMovNaoCancelada == null)
			return false;
		return podeMovimentar(titular, lotaTitular, mob)
				&& (mob.isSobrestado())
				&& !mob.isApensadoAVolumeDoMesmoProcesso()
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESOBRESTAR,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}	
	
	/**
	 * Retorna se é possível fazer despacho no móbil, conforme as regras a
	 * seguir:
	 * <ul>
	 * <li>Móbil não pode ter despacho pendente de assinatura</li>
	 * <li>Móbil tem de ser via ou volume. Não pode ser geral</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode estar em edital de eliminação</li>
	 * <li>Móbil tem de estar assinado ou ser externo. <b>Mas documento externo
	 * não é cnsiderado assinado? <i>isAssinado</i> não deveria retornar
	 * verdadeiro?</b></li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDespachar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		return (mob.isVia() || mob.isVolume())
				&& !mob.isEmTransito()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.isJuntado()
				&& !mob.isArquivado()
				&& !mob.isEmEditalEliminacao()
				&& !mob.isApensadoAVolumeDoMesmoProcesso()
				&& !mob.isSobrestado()
				&& !mob.isPendenteDeAnexacao()
				&& !mob.getDoc().isSemEfeito()
				&& (!mob.getDoc().isPendenteDeAssinatura() || (mob.getDoc().getExTipoDocumento()
						.getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO_FOLHA_DE_ROSTO) || 
						(mob.getDoc().isProcesso() && mob.getDoc().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_FOLHA_DE_ROSTO))
				// && mob.doc().isAssinadoPorTodosOsSignatarios()
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível fazer download do conteúdo. Método em desuso,
	 * retornando sempre <i>false</i>.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeDownloadConteudo(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return false;
	}

	/**
	 * Retorna se é possível duplicar o documento ue contém o móbil mob. Basta
	 * não estar eliminado o documento e não haver configuração impeditiva, o
	 * que significa que, tendo acesso a um documento não eliminado, qualquer
	 * usuário pode duplicá-lo.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDuplicar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return !mob.isEliminado()
				&& podeAcessarDocumento(titular, lotaTitular, mob)
				&& getConf().podePorConfiguracao(titular, lotaTitular, mob.getDoc().getExTipoDocumento(), mob.getDoc().getExFormaDocumento(), 
						mob.getDoc().getExModelo(), CpTipoConfiguracao.TIPO_CONFIG_DUPLICAR);
	}
	
	/**
	 * Retorna se é possível exibir informações completas.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeExibirInformacoesCompletas(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return true;
	}

	/**
	 * Retorna se é possível editar um documento, conforme as seguintes regras:
	 * <ul>
	 * <li>Se o documento for físico, não pode estar finalizado</li>
	 * <li>Documento não pode estar cancelado</li>
	 * <li>Se o documento for digital, não pode estar assinado</li>
	 * <li>Usuário tem de ser 1) da lotação cadastrante do documento,
	 * 2)subscritor do documento ou 3) titular do documento</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeEditar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.getDoc().isEletronico()){
			if (!mob.getDoc().getAssinaturasEAutenticacoesComTokenOuSenhaERegistros().isEmpty())
				return false;
		} else {
			if (mob.getDoc().isFinalizado())
				return false;
		}
		if (mob.getDoc().isCancelado() || mob.getDoc().isSemEfeito())
			return false;
		if (!mob.getDoc().isCapturado() && !mob.getDoc().isPendenteDeAssinatura())
			return false;
		if (!lotaTitular.equivale(mob.getDoc().getLotaCadastrante())
				&& (mob.getDoc().getSubscritor() != null && !mob.getDoc()
						.getSubscritor().equivale(titular))
				&& (mob.getDoc().getTitular() != null && !mob.getDoc().getTitular()
						.equivale(titular)))
			if(!mob.getExDocumento().temPerfil(titular, lotaTitular, ExPapel.PAPEL_GESTOR)
					&& !mob.getExDocumento().temPerfil(titular, lotaTitular, ExPapel.PAPEL_REVISOR))
				return false;
		
		if (!getConf().podePorConfiguracao(titular, lotaTitular, mob.getDoc().getExFormaDocumento(),
						CpTipoConfiguracao.TIPO_CONFIG_CRIAR) &&
						!getConf().podePorConfiguracao(titular, lotaTitular, mob.getDoc().getExModelo(),
								CpTipoConfiguracao.TIPO_CONFIG_CRIAR))
			return false;
		
		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_EDITAR);
				
	}

	/**
	 * Retorna se é possível editar a data de um documento, conforme configuração específica.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mod
	 * @return
	 */
	public boolean podeEditarData(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExModelo mod) {

		return getConf().podePorConfiguracao(null, null, null, null, mod.getExFormaDocumento(),
				mod, null, null, null, titular.getCargo(), titular.getOrgaoUsuario(),
				titular.getFuncaoConfianca(), lotaTitular, titular, null, null, 
				CpTipoConfiguracao.TIPO_CONFIG_EDITAR_DATA);
	}

	/**
	 * Retorna se é possível editar a descrição de um documento, conforme configuração específica.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mod
	 * @return
	 */
	public boolean podeEditarDescricao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExModelo mod) {

		return getConf().podePorConfiguracao(null, null, null, null, mod.getExFormaDocumento(),
				mod, null, null, null, titular.getCargo(), titular.getOrgaoUsuario(),
				titular.getFuncaoConfianca(), lotaTitular, titular, null, null, 
				CpTipoConfiguracao.TIPO_CONFIG_EDITAR_DESCRICAO);
	}

	/**
	 * Retorna se é possível agendar publicação direta, de acordo com as
	 * seguintes regras:
	 * <ul>
	 * <li>Documento tem de estar fechado</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Não pode haver agendamento de publicação direta em aberto</li>
	 * <li>Não pode haver agendamento de publicação indireta em aberto</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode estar eliminado</li>
	 * <li>Nada é dito a respeito do Boletim Interno</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAgendarPublicacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if (!mob.getDoc().isFinalizado()
				|| mob.getDoc().isPendenteDeAssinatura() 
				|| mob.getDoc().isPublicacaoAgendada()
				|| mob.getDoc().isSemEfeito()
				|| mob.getDoc().isEliminado()
				|| mob.isArquivado())
			return false;
		
		if (mob.isPendenteDeAnexacao())
			return false;

		if (podeAtenderPedidoPublicacao(titular, lotaTitular,null))
			return true;
		
		return (!mob.getDoc().isPublicacaoSolicitada()
				&& podeMovimentar(titular, lotaTitular, mob) 
				&&  getConf().podePorConfiguracao(titular,
						lotaTitular,
						mob.getDoc().getExModelo(),
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_AGENDAMENTO_DE_PUBLICACAO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));		
		
	



	}

	/**
	 * Retorna se é possível fazer o agendamento de publicação solicitada
	 * indiretamente. Basta haver permissão para atender pedido de publicação e
	 * estar com publicação indireta solicitada o documento a que pertence o
	 * móbil passado por parâmetro.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeRemeterParaPublicacaoSolicitada(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		if (mob.isPendenteDeAnexacao())
			return false;
		return mob.getDoc().isPublicacaoSolicitada()
				&& podeAtenderPedidoPublicacao(titular, lotaTitular, mob);
	}
	
	/**
	 * Retorna se é possível solicitar publicação indireta no DJE, conforme as
	 * regras a seguir:
	 * <ul>
	 * <li>Não pode ser possível agendar publicação direta</li>
	 * <li>Documento tem de estar fechado (verificação desnecessária, visto que
	 * abaixo se checa se está assinado)</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Não pode haver outra solicitação de publicação no DJE em aberto</li>
	 * <li>Não pode pode haver solicitação de publicação no Boletim em aberto</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode estar eliminado</li>
	 * <li>Não pode haver agendamento de publicação direta em aberto
	 * <b>(verificação desnecessária?)</b></li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podePedirPublicacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if (podeAgendarPublicacao(titular, lotaTitular, mob)){		
			return false;
		}	
		else
			return (mob.getDoc().isFinalizado())
					&& (podeMovimentar(titular, lotaTitular, mob) || podeAtenderPedidoPublicacao(
							titular, lotaTitular, mob))
					&& !mob.getDoc().isPendenteDeAssinatura()
					&& !mob.getDoc().isPublicacaoSolicitada()
					&& !mob.getDoc().isPublicacaoBoletimSolicitada()
					&& !mob.getDoc().isPublicacaoAgendada()
					&& !mob.getDoc().isSemEfeito()
					&& !mob.getDoc().isEliminado()
					&& !mob.isArquivado()
					&& getConf()
							.podePorConfiguracao(
									titular,
									lotaTitular,
									mob.getDoc().getExModelo(),
									ExTipoMovimentacao.TIPO_MOVIMENTACAO_PEDIDO_PUBLICACAO,
									CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível utilizar o recurso Criar Anexo, com base nas
	 * seguintes regras:
	 * <ul>
	 * <li>Documento tem de estar finalizado</li>
	 * <li>Documento tem de ser interno produzido</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeIncluirDocumento(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if(mob.isGeral() && !mob.getDoc().isPendenteDeAssinatura())
			return false;
		
		if (mob.getDoc().isProcesso() && mob.isArquivadoCorrente())
			return false;
		
		if (mob.isArquivado())
			return false;
		
		if(mob.getDoc().isSemEfeito())
			return false;

		if(mob.isVolumeEncerrado())
			return false;
		
		if(mob.isSobrestado())
			return false;
		
		if(mob.isJuntado())
			return false;
		
		return (mob.getExDocumento().isFinalizado())								
				&& !mob.isEmTransito()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& getConf().podePorConfiguracao(titular, lotaTitular, mob.getDoc().getExTipoDocumento(), mob.getDoc().getExFormaDocumento(), 
						mob.getDoc().getExModelo(), CpTipoConfiguracao.TIPO_CONFIG_INCLUIR_DOCUMENTO);
		
	}
	
	/**
	 * Retorna se é possível, com base em configuração, utilizar a rotina de
	 * atendimento de pedidos indiretos de publicação no DJE. Não é utilizado o
	 * parãmetro mob.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAtenderPedidoPublicacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_ATENDER_PEDIDO_PUBLICACAO);
	}

	/**
	 * Retorna se é possível excluir o documento cujo móbil é o representado
	 * pelo parâmetro mob. As regras para o documento são as seguintes:
	 * <ul>
	 * <li>Documento não pode estar finalizado, seja físico ou eletrônico</li>
	 * <li>Lotação do usuário tem de ser a do cadastrante do documento</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param doc
	 * @param numVia
	 * @return
	 * @throws Exception
	 */
	public boolean podeExcluir(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.getDoc().isFinalizado())
			return false;

		if (!mob.getDoc().getLotaCadastrante().equivale(lotaTitular))
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_EXCLUIR);
	}

	/**
	 * Retorna se é possível excluir uma movimentação de anexação, representada
	 * por mov, conforme as regras a seguir:
	 * <ul>
	 * <li>Anexação não pode estar cancelada</li>	
	 * <li>Anexo não pode estar assinado</li>
	 * <li>Se o documento for físico, não pode estar finalizado</li>
	 * <li>Se o documento for eletrônico, não pode estar assinado</li>
	 * <li>Lotação do usuário tem de ser a lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração: Excluir
	 * Anexo</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeExcluirAnexo(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;
		
		if (mov.isAssinada())
			return false;

		if (mob.getDoc().isFinalizado() && !mob.getDoc().isEletronico()) {
			return false;
		}

		if (!(mov.getLotaCadastrante().equivale(lotaTitular)))
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_EXCLUIR_ANEXO);
	}
	
	/**
	 * Retorna se é possível excluir uma movimentação de Inclusão de Cossignatário 
	 * <ul>
	 * <li>Não pode estar cancelada</li>	
	 * <li>Não pode estar assinado</li>
	 * <li>Se o documento for físico, não pode estar finalizado</li>
	 * <li>Não pode estar assinado</li>
	 * <li>Lotação do usuário tem de ser a lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração: Excluir
	 * Anexo</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeExcluirCosignatario(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {
		
		if (mov.isCancelada())
			return false;

		if (mob.getDoc().isFinalizado() && !mob.getDoc().isEletronico()) {
			return false;
		}

		if (!(mov.getLotaCadastrante().equivale(lotaTitular)))
			return false;

		if(!mov.getExDocumento().isPendenteDeAssinatura())
			return false;
		
		if(mov.getExDocumento().isEletronico() && !mov.getExDocumento().getAssinaturasEAutenticacoesComTokenOuSenhaERegistros().isEmpty())
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_EXCLUIR);
	}	

	/**
	 * Retorna se é possível cancelar uma movimentação mov, de anexação de
	 * arquivo. Regras:
	 * <ul>
	 * <li>Anexação não pode estar cancelada</li>	
	 * <li>Não pode mais ser possível <i>excluir</i> a anexação</li>
	 * <li>Se o documento for físico, anexação não pode ter sido feita antes da
	 * finalização</li>
	 * <li>Se o documento for digital, anexação não pode ter sido feita antes da
	 * assinatura</li>	
	 * <li>Lotação do usuário tem de ser a lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Movimentação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarAnexo(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;
		
		if (podeExcluirAnexo(titular, lotaTitular, mob, mov))
			return false;

		Calendar calMov = new GregorianCalendar();
		Calendar cal2 = new GregorianCalendar();
		calMov.setTime(mov.getDtIniMov());

		if (mob.getDoc().isFinalizado() && !mob.getDoc().isEletronico()) {
			cal2.setTime(mob.getDoc().getDtFinalizacao());
			if (calMov.before(cal2))
				return false;
		}

		if (!mob.getDoc().isPendenteDeAssinatura()
				&& mob.getDoc().getExTipoDocumento().getIdTpDoc() == 1
				&& mob.getDoc().isEletronico()) {
			cal2.setTime(mob.getDoc().getDtAssinatura());
			if (calMov.before(cal2))
				return false;
		}

		if (!(mov.getLotaCadastrante().equivale(lotaTitular)))
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO,
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}

	/**
	 * Retorna se é possível cancelar uma movimentação mov, de anexação de
	 * arquivo. Regras:
	 * <ul>
	 * <li>Anexação não pode estar cancelada</li>	
	 * <li>Não pode mais ser possível <i>excluir</i> a anexação</li>
	 * <li>Se o documento for físico, anexação não pode ter sido feita antes da
	 * finalização</li>
	 * <li>Se o documento for digital, anexação não pode ter sido feita antes da
	 * assinatura</li>	
	 * <li>Lotação do usuário tem de ser a lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Movimentação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarArquivoAuxiliar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;
		
		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO_DE_ARQUIVO_AUXILIAR,
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}

	/**
	 * Retorna se é possível anexar um arquivo auxiliar no mob.
	 * Regras:
	 * <ul>
	 * <li>Não pode haver configuração impeditiva.</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAnexarArquivoAuxiliar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO_DE_ARQUIVO_AUXILIAR,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível cancelar uma movimentação de vinculação de perfil,
	 * passada através do parâmetro mov. As regras são as seguintes:
	 * <ul>
	 * <li>Vinculação de perfil não pode estar cancelada</li>
	 * <li>Lotação do usuário tem de ser a lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Movimentação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarVinculacaoPapel(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;
		
		if ((mov.getSubscritor() != null && mov.getSubscritor().equivale(titular))||( mov.getSubscritor()==null && mov.getLotaSubscritor()!=null && mov.getLotaSubscritor().equivale(lotaTitular)))
			return true;

		if ((mov.getCadastrante() != null && mov.getCadastrante().equivale(titular)) || (mov.getCadastrante()==null && mov.getLotaCadastrante() != null && mov.getLotaCadastrante().equivale(lotaTitular)))
			return true;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				mov.getIdTpMov(),
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}

	/**
	 * Retorna se é possível cancelar uma movimentação de vinculação de perfil,
	 * passada através do parâmetro mov. As regras são as seguintes:
	 * <ul>
	 * <li>Vinculação de perfil não pode estar cancelada</li>
	 * <li>Lotação do usuário tem de ser a lotação cadastrante da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Movimentação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarVinculacaoMarca(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;
		
		if ((mov.getSubscritor()!= null && mov.getSubscritor().equivale(titular))||( mov.getSubscritor()==null && mov.getLotaSubscritor().equivale(lotaTitular)))
			return true;

		if ((mov.getCadastrante()!= null && mov.getCadastrante().equivale(titular))||( mov.getCadastrante()==null && mov.getLotaCadastrante().equivale(lotaTitular)))
			return true;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				mov.getIdTpMov(),
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}
	
	/**
	 * <b>(Quando é usado este método?)</b> Retorna se é possível cancelar
	 * movimentação do tipo despacho, representada pelo parâmetro mov. São estas
	 * as regras:
	 * <ul>
	 * <li>Despacho não pode estar cancelado</li>
	 * <li>Lotação do usuário tem de ser a lotação cadastrante do despacho</li>
	 * <li>Despacho não pode estar assinado</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração:
	 * Cancelar Movimentação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeCancelarDespacho(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;

		if (!(mov.getLotaCadastrante().equivale(lotaTitular)))
			return false;
		
		if(mov.isUltimaMovimentacao())
			return false;

		for (ExMovimentacao movAssinatura : mov.getExMobil()
				.getExMovimentacaoSet()) {
			if (!movAssinatura.isCancelada()
					&& (movAssinatura.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_MOVIMENTACAO 
					     || movAssinatura.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA)
					&& movAssinatura.getExMovimentacaoRef().getIdMov() == mov
							.getIdMov())
				return false;
		}

		return getConf().podePorConfiguracao(titular, lotaTitular,
				mov.getIdTpMov(),
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}

	/**
	 * Retorna se é possível excluir anotação realizada no móbil, passada pelo
	 * parâmetro mov. As seguintes regras incidem sobre a movimentação a ser
	 * excluída:
	 * <ul>
	 * <li>Não pode estar cancelada</li>
	 * <li>Lotação do usuário tem de ser a do cadastrante ou do subscritor
	 * (responsável) da movimentação</li>
	 * <li>Não pode haver configuração impeditiva. Tipo de configuração: Excluir
	 * Anotação</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 * @throws Exception
	 */
	public boolean podeExcluirAnotacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;
		
		//Verifica se foi a pessoa ou lotação que fez a anotação
		if (!mov.getCadastrante().getIdInicial().equals(titular.getIdInicial())
				&& !mov.getSubscritor().getIdInicial().equals(titular.getIdInicial())
				&& !mov.getLotaCadastrante().getIdInicial().equals(
				lotaTitular.getIdInicial())
				&& !mov.getLotaSubscritor().getIdInicial().equals(
						lotaTitular.getIdInicial()))
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_EXCLUIR_ANOTACAO);
	}

	/**
	 * Retorna se é possível exibir todos os móbil's. Basta o documento estar
	 * finalizado.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeExibirTodasVias(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return (mob != null && mob.getDoc().isFinalizado());
	}

	/**
	 * Retorna se é possível fazer anotação no móbil. Basta o móbil não estar
	 * eliminado, não estar em trânsito, não ser geral e não haver configuração
	 * impeditiva, o que significa que, tendo acesso a um documento não
	 * eliminado fora de trânsito, qualquer usuário pode fazer anotação.

	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeFazerAnotacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		return (!mob.isEmTransitoInterno() && !mob.isEliminado() && !mob
				.isGeral())
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANOTACAO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível vincular perfil ao documento. Basta não estar
	 * eliminado o documento e não haver configuração impeditiva, o que
	 * significa que, tendo acesso a um documento não eliminado, qualquer
	 * usuário pode se cadastrar como interessado.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeFazerVinculacaoPapel(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {


		if (mob.getDoc().isCancelado() || mob.getDoc().isSemEfeito()



				|| mob.isEliminado())
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_VINCULACAO_PAPEL,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível vincular uma marca ao documento. Basta não estar
	 * eliminado o documento e não haver configuração impeditiva, o que
	 * significa que, tendo acesso a um documento não eliminado, qualquer
	 * usuário pode colocar marcas.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeMarcar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (mob.getDoc().isCancelado() || mob.getDoc().isSemEfeito()
				|| mob.isEliminado())
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_MARCACAO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível finalizar o documento ao qual o móbil passado por
	 * parâmetro pertence. São estas as regras:
	 * <ul>
	 * <li>Documento não pode estar finalizado</li>
	 * <li>Se o documento for interno produzido, usuário tem de ser: 1) da
	 * lotação cadastrante do documento, 2) o subscritor do documento ou 3) o
	 * titular do documento. <b>Obs.: por que a origem do documento está sendo
	 * considerada nesse caso?</b></li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeFinalizar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.getDoc().isFinalizado())
			return false;
		if (lotaTitular.isFechada())
			return false;
		if (mob.isPendenteDeAnexacao())
			return false;
		if (mob.isPendenteDeColaboracao())
			return false;
		if (mob.getDoc().getExTipoDocumento().getIdTpDoc() != 2
				&& mob.getDoc().getExTipoDocumento().getIdTpDoc() != 3)
			if (!mob.getDoc().getLotaCadastrante().equivale(lotaTitular)
					&& (mob.getDoc().getSubscritor() != null && !mob.getDoc()
							.getSubscritor().equivale(titular))
					&& (mob.getDoc().getTitular() != null && !mob.getDoc()
							.getTitular().equivale(titular))
					&& !mob.getExDocumento().temPerfil(titular, lotaTitular, ExPapel.PAPEL_GESTOR)
					&& !mob.getExDocumento().temPerfil(titular, lotaTitular, ExPapel.PAPEL_REVISOR))
				return false;
		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_FINALIZAR);
	}

	/**
	 * Retorna se é possível que o usuário finalize o documento e assine
	 * digitalmente numa única operação. Os requisitos são os mesmos que têm de
	 * ser cumpridos para se poder finalizar
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeFinalizarAssinar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		return podeFinalizar(titular, lotaTitular, mob)
				&& mob.getDoc().isEletronico();

	}

	/**
	 * Retorna se é possível incluir cossignatário no documento que contém o
	 * móbil passado por parâmetro. O documento tem de atender as seguintes
	 * condições:
	 * <ul>
	 * <li>Não pode estar cancelado</li>
	 * <li>Sendo documento físico, não pode estar finalizado</li>
	 * <li>Sendo documento digital, não pode estar assinado</li>
	 * <li>Lotação do usuário tem de ser a lotação cadastrante do documento</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeIncluirCosignatario(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.getDoc().getSubscritor() == null)
			return false;
		if (mob.getDoc().isCancelado())
			return false;
		if (mob.getDoc().isEletronico()){
			if (!mob.getDoc().getAssinaturasEAutenticacoesComTokenOuSenhaERegistros().isEmpty())
				return false;
		} else {
			if (mob.getDoc().isFinalizado())
				return false;
		}
		if (!mob.getDoc().isPendenteDeAssinatura())
			return false;
		if (!mob.getDoc().getLotaCadastrante().equivale(lotaTitular))
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_INCLUSAO_DE_COSIGNATARIO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível incluir o móbil em edital de eliminação, de acordo
	 * com as condições a seguir:
	 * <ul>
	 * <li>Móbil tem de ser via ou geral de processo</li>
	 * <li>Móbil tem de estar em arquivo corrente ou intermediário</li>
	 * <li>PCTT tem de prever, para o móbil, destinação final Eliminação</li>
	 * <li>Móbil não pode estar arquivado permanentemente</li>
	 * <li>Documento a que o móbil pertence tem de ser digital ou estar na
	 * lotação titular</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeIncluirEmEditalEliminacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {

		if (!(mob.isVia() || mob.isGeralDeProcesso())
				|| mob.getDoc().isSemEfeito() || mob.isEliminado())
			return false;

		ExMobil mobVerif = mob;

		if (mob.isGeralDeProcesso())
			mobVerif = mob.getDoc().getUltimoVolume();

		return mobVerif != null
				&& (mobVerif.isArquivadoCorrente() || mobVerif
						.isArquivadoIntermediario())
				&& !mobVerif.isArquivadoPermanente()
				&& mobVerif.isDestinacaoEliminacao()
				&& (lotaTitular.equivale(mob
						.getUltimaMovimentacaoNaoCancelada().getLotaResp()) || mob
						.getDoc().isEletronico())
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_INCLUSAO_EM_EDITAL_DE_ELIMINACAO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível junta este móbil a outro. Seguem as regras:
	 * <ul>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Volume não pode estar encerrado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o móbil/usuário</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil não pode estar juntado <b>(mas pode ser juntado estando
	 * apensado?)</b></li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeJuntar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!mob.isVia())
			return false;

		if (mob.isPendenteDeAnexacao())
			return false;
		
		return !mob.isCancelada()
				&& !mob.isVolumeEncerrado()
				&& !mob.isEmTransito()
				&& podeMovimentar(titular, lotaTitular, mob)

				&& (!mob.getDoc().isPendenteDeAssinatura() || mob.getDoc().isInternoCapturado())
				&& !mob.isJuntado()
				&& !mob.isApensado()
				&& !mob.isArquivado()
				&& !mob.isSobrestado()
				&& !mob.getDoc().isSemEfeito()
				&& podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR, null, null, null, null, null, null);

		// return true;
	}

	/**
	 * Retorna se é possível apensar este móbil a outro, conforme as regras:
	 * <ul>
	 * <li>Móbil precisa ser via ou volume</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Móbil não pode estar em trânsito <b>(o que é isEmTransito?)</b></li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o móbil/usuário</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeApensar(DpPessoa titular, DpLotacao lotaTitular,
			ExMobil mob) {

		if (!mob.isVia() && !mob.isVolume())
			return false;

		return !mob.isCancelada()
				&& !mob.getDoc().isSemEfeito()
				&& !mob.isEmTransito()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& !mob.isApensado()
				&& !mob.isJuntado()
				&& !mob.isArquivado()
				&& !mob.isSobrestado()
				&& getConf().podePorConfiguracao(titular, lotaTitular, titular.getCargo(), titular.getFuncaoConfianca(), mob.getDoc().getExFormaDocumento(), mob.getDoc().getExModelo(), 
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_APENSACAO,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível desapensar este móbil de outro, conforme as
	 * seguintes condições para o móbil:
	 * <ul>
	 * <li>Precisa ser via ou volume</li>
	 * <li>Precisa ter movimentação não cancelada</li>
	 * <li>Precisa estar apensado</li>
	 * <li>Não pode estar em trânsito <b>(o que é isEmTransito?)</b></li>
	 * <li>Não pode estar cancelado</li>
	 * <li>Não pode estar em algum arquivo</li>
	 * <li>Não pode estar juntado <b>(mas pode ser juntado estando
	 * apensado?)</b></li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o móbil/usuário</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDesapensar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		final ExMovimentacao ultMovNaoCancelada = mob
				.getUltimaMovimentacaoNaoCancelada();

		if (!mob.isVia() && !mob.isVolume())
			return false;

		if (ultMovNaoCancelada == null)
			return false;
		
		if(mob.getDoc().isEletronico() && mob.isApensadoAVolumeDoMesmoProcesso())
			return false;

		ExMobil mobilAVerificarSePodeMovimentar = mob.isApensadoAVolumeDoMesmoProcesso() 
				? mob.getDoc().getUltimoVolume() : mob;
		
		if (!mob.isApensado() || mob.isEmTransito() || mob.isCancelada()
				|| mob.isArquivado()
				|| mob.isSobrestado()
				|| !podeMovimentar(titular, lotaTitular, mobilAVerificarSePodeMovimentar)
				|| mob.isJuntado())
			return false;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESAPENSACAO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se o usuário tem a permissão básica para movimentar o documento.
	 * Método usado como premissa para várias outras permissões de movimentação.
	 * Regras:
	 * <ul>
	 * <li>Se móbil é geral, <i>podeMovimentar()</i> tem de ser verdadeiro para
	 * algum móbil do mesmo documento</li>
	 * <li>Móbil tem de ser geral, via ou volume</li>
	 * <li>Móbil tem de de ter alguma movimentação não cancelada</li>
	 * <li><b>Móbil não pode estar cancelado nem aberto</b></li>
	 * <li>Usuário tem de ser da lotação atendente definida na última
	 * movimentação não cancelada</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 */
	public boolean podeMovimentar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {


		if (!podeSerMovimentado(mob))
			return false;

		if (mob.isGeral()) {
			if (mob.getDoc().isProcesso())
				return podeMovimentar(titular, lotaTitular, mob.getDoc().getUltimoVolume());
			else {
				for (ExMobil m : mob.getDoc().getExMobilSet()) {
					if (!m.isGeral() && podeMovimentar(titular, lotaTitular, m))
						return true;
				}
				return false;
			}
		}

		final ExMovimentacao exMov = mob.getUltimaMovimentacaoNaoCancelada();
		if (exMov == null) {
			return false;
		}

		/*
		 * Orlando: Inclui a condição "&& !exMov.getResp().equivale(titular))"
		 * no IF ,abaixo, para permitir que um usuário possa transferir quando
		 * ele for o atendente do documento, mesmo que ele não esteja na lotação

		 * do documento
		 */

		if (exMov.getLotaResp() != null
				&& !exMov.getLotaResp().equivale(lotaTitular)){
			return false;
		}

		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	public boolean podeSerMovimentado(final ExMobil mob) {
		if (mob.getDoc().isSemEfeito())
			return false;

		if (mob.getDoc().isFinalizado() && mob.isGeral()) {
			if (mob.getDoc().isProcesso())
				return podeSerMovimentado(mob.getDoc().getUltimoVolume());
			else {
				for (ExMobil m : mob.getDoc().getExMobilSet()) {
					if (!m.isGeral() && podeSerMovimentado(m))
						return true;
				}
				return false;
			}
		}
		if (!mob.isVia() && !mob.isVolume())
			return false;

		final ExMovimentacao exMov = mob.getUltimaMovimentacaoNaoCancelada();
		if (exMov == null) {
			return false;
		}
		if (mob.isCancelada() || !mob.getDoc().isFinalizado())
			return false;

		return true;
	}
	
	/**
	 * Retorna se o usuário tem é o atendente do documento. Regras:
	 * <ul>
	 * <li>Se móbil é geral, <i>isAtendente()</i> tem de ser verdadeiro para
	 * algum móbil do mesmo documento</li>
	 * <li>Móbil tem de ser geral, via ou volume</li>
	 * <li>Móbil tem de de ter alguma movimentação não cancelada</li>
	 * <li><b>Móbil não pode estar cancelado</b></li>
	 * <li>Usuário tem de ser da lotação atendente definida na última
	 * movimentação não cancelada, ou no documento se ainda não for finalizado.</li>
	 * </ul>
	 */
	public static boolean isAtendente(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		if (mob.isGeral()) {
			for (ExMobil m : mob.getDoc().getExMobilSet()) {
				if (!m.isGeral() && isAtendente(titular, lotaTitular, m))
					return true;
			}
			return false;
		}
		if (!mob.isVia() && !mob.isVolume())
			return false;

		final ExMovimentacao exMov = mob.getUltimaMovimentacaoNaoCancelada();
		if (exMov == null) {
			return false;
		}
		if (mob.isCancelada())
			return false;

		DpLotacao lot = exMov.getLotaResp();


		if (!mob.getDoc().isFinalizado())
			lot = mob.getDoc().getLotaCadastrante();

		if (lot != null && !lot.equivale(lotaTitular))
			// && !exMov.getCadastrante().getLotacao().equivale(lotaTitular))
			return false;

		return true;
	}

	public static DpResponsavel getAtendente(ExMobil mob)
			throws Exception {
		
		if (mob.isGeral()) {
			for (ExMobil m : mob.getDoc().getExMobilSet()) {
				if (!m.isGeral() && getAtendente(m) != null)
					return getAtendente(m);
			}
			return null;
		}
		if (!mob.isVia() && !mob.isVolume())
			return null;

		final ExMovimentacao exMov = mob.getUltimaMovimentacaoNaoCancelada();
		if (exMov == null) {
			return null;
		}
		if (mob.isCancelada())
			return null;


		if (!mob.getDoc().isFinalizado())
			return mob.getDoc().getLotaCadastrante();

		DpLotacao lot = exMov.getLotaResp();
		return lot;
	}

	/**
	 * Retorna se é possível refazer um documento. Têm de ser satisfeitas as
	 * seguintes condições:
	 * <ul>
	 * <li>Documento tem de estar finalizado</li>
	 * <li>Usuário tem de ser o subscritor ou o titular do documento ou ser da
	 * lotação cadastrante do documento</li>
	 * <li>Documento não pode estar assinado, a não ser que seja dos tipos
	 * externo ou interno importado, que são naturalmente considerados
	 * assinados. Porém, se for documento de um desses tipos, não pode haver pdf
	 * anexado <b>(verificar por quê)</b></li>
	 * <li>Documento tem de possuir via não cancelada ou volume não cancelado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeRefazer(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		return (mob.getDoc().isFinalizado())
				&& !mob.getDoc().isRecebeuJuntada()
				&& ((mob.getDoc().getLotaCadastrante().equivale(lotaTitular)
						|| (mob.getDoc().getSubscritor() != null && mob.getDoc()
								.getSubscritor().equivale(titular)) || (mob
						.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular)))
						&& !mob.getDoc().isColaborativo()
						&& (mob.getDoc().isPendenteDeAssinatura() || (mob.getDoc()
								.getExTipoDocumento().getIdTpDoc() != 1L && !mob
								.getDoc().hasPDF())) && (mob.getDoc()
						.getNumUltimaViaNaoCancelada() != 0 || (mob.getDoc()
						.getUltimoVolume() != null && !mob.getDoc()
						.getUltimoVolume().isCancelada())))
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						CpTipoConfiguracao.TIPO_CONFIG_REFAZER);
	}

	/**
	 * Retorna se é possível indicar um móbil para guarda permanente. Têm de ser
	 * satisfeitas as seguintes condições:
	 * <ul>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil tem de ser via ou geral de processo</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode ter sido já indicado para guarda permanente</li>
	 * <li>Móbil não pode ter sido arquivado permanentemente nem eliminado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeIndicarPermanente(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.isPendenteDeAnexacao())
			return false;
		
		return (!mob.getDoc().isPendenteDeAssinatura()
				&& (mob.isVia() || mob.isGeralDeProcesso())
				&& !mob.isCancelada() && !mob.isEmTransito()
				&& !mob.isJuntado()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.isindicadoGuardaPermanente()
				&& !mob.isArquivadoPermanente() && !mob.isEmEditalEliminacao() && getConf()
				.podePorConfiguracao(
						titular,
						lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_INDICACAO_GUARDA_PERMANENTE,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}

	/**
	 * Retorna se é possível reclassificar um documento. Têm de ser satisfeitas
	 * as seguintes condições:
	 * <ul>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil tem de ser geral</li>
	 * <li>Móbil não pode ter sido eliminado</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 ** 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeReclassificar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		return (!mob.getDoc().isPendenteDeAssinatura() && mob.isGeral() && !mob.isCancelada()
				&& !mob.isEliminado() && getConf().podePorConfiguracao(titular,
				lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECLASSIFICACAO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}

	/**
	 * Retorna se é possível avaliar um documento. Têm de ser satisfeitas as
	 * seguintes condições:
	 * <ul>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Móbil tem de ser geral</li>
	 * <li>Móbil não pode ter sido eliminado</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAvaliar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		return (!mob.getDoc().isPendenteDeAssinatura() && mob.isGeral() && !mob.isCancelada()
				&& !mob.isEliminado() && getConf().podePorConfiguracao(titular,
				lotaTitular, ExTipoMovimentacao.TIPO_MOVIMENTACAO_AVALIACAO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}

	/**
	 * Retorna se é possível reverter a indicação de um móbil para guarda
	 * permanente. Têm de ser satisfeitas as seguintes condições:
	 * <ul>
	 * <li>Móbil tem de estar indicado para guarda permanente</li>
	 * <li>Móbil tem de ser via ou geral de processo</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode ter sido arquivado permanentemente nem eliminado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeReverterIndicacaoPermanente(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		return (mob.isindicadoGuardaPermanente()
				&& (mob.isVia() || mob.isGeralDeProcesso()) && !mob.isJuntado()
				&& !mob.isArquivadoPermanente() && !mob.isCancelada()
				&& !mob.isEmTransito() && getConf()
				.podePorConfiguracao(
						titular,
						lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_REVERSAO_INDICACAO_GUARDA_PERMANENTE,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}

	/**
	 * Retorna se é possível retirar um móbil de edital de eliminação. Têm de
	 * ser satisfeitas as seguintes condições:
	 * <ul>
	 * <li>Móbil não pode ter sido eliminado</li>
	 * <li>Móbil tem de estar em edital de eliminação</li>
	 * <li>Edital contendo o móbil precisa estar assinado</li>
	 * <li>Pessoa a fazer a retirada tem de ser o subscritor ou titular do
	 * edital</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeRetirarDeEditalEliminacao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.isEliminado())
			return false;

		ExMovimentacao movInclusao = mob
				.getUltimaMovimentacaoNaoCancelada(
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_INCLUSAO_EM_EDITAL_DE_ELIMINACAO,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_RETIRADA_DE_EDITAL_DE_ELIMINACAO);

		if (movInclusao == null)
			return false;

		ExDocumento edital = movInclusao.getExMobilRef().getExDocumento();

		if (edital.isPendenteDeAssinatura())
			return false;

		if (edital.getSubscritor() == null)
			return lotaTitular.equivale(edital.getLotaCadastrante());
		else
			return titular.equivale(edital.getSubscritor())
					|| titular.equivale(edital.getTitular());

	}

	/**
	 * Retorna se a lotação ou pessoa tem permissão para receber documento
	 * 
	 * @param pessoa
	 * @param lotacao	
	 * @return
	 * @throws Exception
	 */
	public boolean podeReceberPorConfiguracao(final DpPessoa pessoa,
			final DpLotacao lotacao) {
		
		return getConf().podePorConfiguracao(pessoa, lotacao,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	/**
	 * Retorna se é possível receber o móbil. conforme as regras a seguir:
	 * <ul>
	 * <li>Móbil tem de ser via ou volume</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil tem de estar em trânsito</li>
	 * <li>Lotação do usuário tem de ser a do atendente definido na última
	 * movimentação</li>
	 * <li>Se o móbil for eletrônico, não pode estar marcado como Despacho
	 * pendente de assinatura, ou seja, móbil em que tenha havido despacho ou
	 * despacho com transferência não pode ser recebido antes de assinado o
	 * despacho</li>
	 * </ul>
	 * <b>Obs.: Teoricamente, qualquer pessoa pode receber móbil transferido
	 * para órgão externo</b>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeReceber(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (!(mob.isVia() || mob.isVolume()))
			return false;
		final ExMovimentacao exMov = mob.getUltimaMovimentacaoNaoCancelada();

		if (mob.isCancelada() || mob.isApensadoAVolumeDoMesmoProcesso() 
				|| mob.isSobrestado() || !mob.isEmTransito() )
			return false;
		else if (!mob.isEmTransitoExterno()) {
			if (!exMov.getLotaResp().equivale(lotaTitular))
				return false;
		}


		// Verifica se o despacho já está assinado, em caso de documentos
		// eletrônicos
		if (mob.getDoc().isEletronico()) {
			for (CpMarca marca : mob.getExMarcaSet()) {
				if (marca.getCpMarcador().getIdMarcador() == CpMarcador.MARCADOR_DESPACHO_PENDENTE_DE_ASSINATURA)
					return false;
			}
		}

		return getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível receber o móbil eletronicamente, de acordo com as
	 * regras a seguir, <b>que deveriam ser parecidas com as de podeReceber(),
	 * para não haver incoerência</b>:
	 * <ul>
	 * <li>Móbil tem de ser via ou volume</li>
	 * <li>A última movimentação não cancelada do móbil não pode ser
	 * transferência externa <b>(regra falha, pois pode ser feita anotação)</b></li>
	 * e não pode ser Recebimento <b>(corrige recebimentos duplicados)</b></li>
	 * <li>Móbil não pode estar marcado como "Despacho pendente de assinatura",
	 * ou seja, tendo havido despacho ou despacho com transferência, este
	 * precisa ter sido assinado para haver transferência</li>
	 * <li>Se houver pessoa atendente definida na última movimentação não
	 * cancelada, o usuário tem de ser essa pessoa</li>
	 * <li>Não havendo pessoa atendente definida na última movimentação, apenas
	 * lotação atendente, a lotação do usuário tem de ser essa</li>
	 * <li>Documento tem de ser eletrônico <b>(melhor se fosse verificado no
	 * início)</b></li>
	 * <li>Móbil tem de estar em trãnsito <b>(melhor se fosse verificado no
	 * início)</b></li>
	 * <li>Não pode haver configuração impeditiva para recebimento (não para
	 * recebimento eletrônico)</li>
	 * </ul>
	 * 
	 * @param cadastrante
	 * @param lotaCadastrante
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeReceberEletronico(DpPessoa cadastrante,
			DpLotacao lotaCadastrante, final ExMobil mob) {
		if (!(mob.isVia() || mob.isVolume()))
			return false;
		ExMovimentacao ultMov = mob.getUltimaMovimentacaoNaoCancelada();
		if (ultMov == null)
			return false;
		if (ultMov.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_TRANSFERENCIA_EXTERNA
				|| ultMov.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_TRANSFERENCIA_EXTERNA
				|| ultMov.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO )
			return false;
		// Verifica se o despacho já está assinado
		for (CpMarca marca : mob.getExMarcaSet()) {
			if (marca.getCpMarcador().getIdMarcador() == CpMarcador.MARCADOR_DESPACHO_PENDENTE_DE_ASSINATURA)
				return false;
		}

		if ((ultMov.getResp() != null && !ultMov.getResp()
				.equivale(cadastrante))
				|| (ultMov.getResp() == null && ultMov.getLotaResp() != null && !ultMov
						.getLotaResp().equivale(lotaCadastrante)))
			return false;
		if (!mob.getDoc().isEletronico() || !mob.isEmTransito())
			return false;
		return getConf().podePorConfiguracao(cadastrante, lotaCadastrante,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_RECEBIMENTO,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível vincular este móbil a outro, conforme as regras:
	 * <ul>
	 * <li>Móbil tem de ser via ou volume</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o móbil/usuário</li>
	 * <li>Móbil não pode estar em trãnsito</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode estar cancelado</li>
	 * <li>Móbil não pode ter sido eliminado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeReferenciar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (!(mob.isVia() || mob.isVolume()))
			return false;

		return !mob.isEmTransito()
				&& podeMovimentar(titular, lotaTitular, mob)
				&& !mob.isJuntado()
				&& !mob.isEliminado()
				&& !mob.getDoc().isCancelado()
				&& !mob.getDoc().isSemEfeito()
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_REFERENCIA,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);

		// return true;
	}

	/**
	 * Retorna se é possível registrar assinatura manual de documento que contém
	 * o móbil passado por parâmetro. As regras são as seguintes:
	 * <ul>
	 * <li>Móbil tem de ser geral</li>
	 * <li>Não pode ser móbil de processo interno importado</li>
	 * <li>Não pode ser móbil de documento externo</li>
	 * <li>Documento não pode estar cancelado</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro ou usuário tem de ser o
	 * próprio subscritor ou titular do documento</li>
	 * <li>Documento não pode ser eletrônico</li>
	 * <li>Documento tem de estar finalizado</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Móbil não pode ter sido eliminado</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeRegistrarAssinatura(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (!mob.isGeral())
			return false;
		if (mob.isArquivado() || mob.isEliminado())
			return false;
		if (mob.getExDocumento().isProcesso()
				&& mob.getExDocumento().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_FOLHA_DE_ROSTO)
			return false;
		if (mob.getDoc().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO_FOLHA_DE_ROSTO
				|| mob.getDoc().isCancelado())
			return false;
		return ((mob.getDoc().getSubscritor() != null && mob.getDoc().getSubscritor()
				.equivale(titular))
				|| (mob.getDoc().getTitular() != null && mob.getDoc().getTitular()
						.equivale(titular)) || podeMovimentar(titular,
				lotaTitular, mob))
				/*
				 * || (ultMovNaoCancelada .getExEstadoDoc().getIdEstadoDoc() ==
				 * ExEstadoDoc.ESTADO_DOC_EM_ANDAMENTO || ultMovNaoCancelada
				 * .getExEstadoDoc().getIdEstadoDoc() ==
				 * ExEstadoDoc.ESTADO_DOC_PENDENTE_DE_ASSINATURA)
				 */
				&& !mob.getDoc().isEletronico()

				&& (mob.getDoc().isFinalizado())
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_REGISTRO_ASSINATURA_DOCUMENTO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível agendar publicação no Boletim. É necessário que não
	 * sejam ainda 17 horas e que <i>podeBotaoAgendarPublicacaoBoletim()</i>
	 * seja verdadeiro para este móbil e usuário.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAgendarPublicacaoBoletim(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {

		GregorianCalendar agora = new GregorianCalendar();
		agora.setTime(new Date());
		return podeBotaoAgendarPublicacaoBoletim(titular, lotaTitular, mob)
				&& (agora.get(Calendar.HOUR_OF_DAY) < 17 
					|| podeGerenciarPublicacaoBoletimPorConfiguracao(titular, lotaTitular, mob));
	}

	/**
	 * Retorna se é possível exibir a opção para agendar publicação no Boletim.
	 * Seguem as regras:
	 * <ul>
	 * <li>Móbil não pode ser geral</li>
	 * <li>Documento tem de estar finalizado</li>
	 * <li>Documento tem de ser do tipo interno produzido</li>
	 * <li><i>podeGerenciarPublicacaoBoletimPorConfiguracao()</i> ou
	 * <i>podeMovimentar()</i>tem de ser verdadeiro para o usuário</li>
	 * <li>Documento não pode já ter sido publicado em boletim</li>
	 * <li>Publicação no boletim não pode ter sido já agendada para o documento</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Documento não pode ter sido eliminado</li>
	 * <li>Móbil não pode estar em algum arquivo</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * 
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeBotaoAgendarPublicacaoBoletim(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (!mob.isGeral())
			return false;

		if (!mob.getDoc().isFinalizado())
			return false;
		if (mob.getDoc().isEliminado())
			return false;
		if (mob.getDoc().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO_FOLHA_DE_ROSTO)
			return false;
		if (mob.getDoc().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_FOLHA_DE_ROSTO)
			return false;
		boolean gerente = podeGerenciarPublicacaoBoletimPorConfiguracao(
				titular, lotaTitular, mob);
		return (podeMovimentar(titular, lotaTitular, mob) || gerente)
				// && !mob.doc().isEletronico()
				&& !mob.getDoc().isBoletimPublicado()
				&& !mob.getDoc().isPendenteDeAssinatura()
				&& !mob.getDoc().isPublicacaoBoletimSolicitada()
				&& !mob.isArquivado()
				&& (getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								mob.getDoc().getExModelo(),
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_AGENDAMENTO_DE_PUBLICACAO_BOLETIM,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR) || gerente);
	}

	/**
	 * Retorna se é possível alterar o nível de accesso do documento. É
	 * necessário apenas que o usuário possa acessar o documento e que não haja
	 * configuração impeditiva. <b>Obs.: Não é verificado se
	 * <i>podeMovimentar()</i></b>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeRedefinirNivelAcesso(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if(mob.getDoc().isBoletimPublicado() || mob.getDoc().isDJEPublicado()) {
			if(podeAtenderPedidoPublicacao(titular, lotaTitular, mob) || podeGerenciarPublicacaoBoletimPorConfiguracao(titular, lotaTitular, mob))
				return true;
			
			return false;
		}


		return !mob.isEliminado()
				&& podeAcessarDocumento(titular, lotaTitular, mob)
				&& podeMovimentar(titular, lotaTitular, mob)
				&& getConf()
						.podePorConfiguracao(
								titular,
								lotaTitular,
								ExTipoMovimentacao.TIPO_MOVIMENTACAO_REDEFINICAO_NIVEL_ACESSO,
								CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}

	/**
	 * Retorna se é possível que algum móbil seja juntado a este, segundo as
	 * seguintes regras:
	 * <ul>
	 * <li>Não pode estar cancelado</li>
	 * <li>Volume não pode estar encerrado</li>
	 * <li>Não pode estar em algum arquivo</li>
	 * <li>Não pode estar juntado</li>
	 * <li>Não pode estar em trânsito</li>
	 * <li><i>podeMovimentar()</i> precisa retornar verdadeiro para ele</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeSerJuntado(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		return !mob.isCancelada() && !mob.isVolumeEncerrado()
				&& !mob.isJuntado()
				&& !mob.isEmTransito() && !mob.isArquivado()
				&& podeMovimentar(titular, lotaTitular, mob);
	}

	/**
	 * Retorna se é possível a uma lotação, com base em configuração, receber
	 * móbil de documento não assinado. Não é aqui verificado se o móbil está
	 * realmente pendente de assinatura
	 * 
	 * @param pessoa
	 * @param lotacao
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeReceberDocumentoSemAssinatura(final DpPessoa pessoa,
			final DpLotacao lotacao, final ExMobil mob) {
		return getConf().podePorConfiguracao(pessoa, lotacao,
				CpTipoConfiguracao.TIPO_CONFIG_RECEBER_DOC_NAO_ASSINADO);
	}

	/**
	 * Retorna se é possível fazer transferência. As regras são as seguintes
	 * para este móbil: <ul <li>Precisa ser via ou volume (não pode ser geral)</li>
	 * <li>Não pode estar em trânsito</li> <li>Não pode estar juntado.</li> <li>
	 * Não pode estar em arquivo permanente.</li> <li><i>podeMovimentar()</i>
	 * precisa retornar verdadeiro para ele</li> <li>Não pode haver configuração
	 * impeditiva</li> </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeTransferir(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if(!podeSerTransferido(mob))
			return false;

		return podeMovimentar(titular, lotaTitular, mob)
				&& getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_TRANSFERENCIA,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR);
	}
	
	public boolean podeSerTransferido(final ExMobil mob) {
		if (mob.isPendenteDeAnexacao())
			return false;

		return (mob.isVia() || mob.isVolume())
				&& !mob.isEmTransito() && !mob.isJuntado()
				&& !mob.isApensadoAVolumeDoMesmoProcesso()
				&& !mob.isArquivado()
				&& (!mob.getDoc().isPendenteDeAssinatura() || (mob.getDoc().getExTipoDocumento()
						.getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_EXTERNO_FOLHA_DE_ROSTO) || 
						(mob.getDoc().isProcesso() && mob.getDoc().getExTipoDocumento().getIdTpDoc() == ExTipoDocumento.TIPO_DOCUMENTO_INTERNO_FOLHA_DE_ROSTO))
				&& !mob.isEmEditalEliminacao()
				&& !mob.isSobrestado()
				&& !mob.getDoc().isSemEfeito()
				&& podeSerMovimentado(mob);
		// return true;
	}
	
	/**
	 * Retorna se é possível fazer transferência imediatamente antes da tela de assinatura. As regras são as seguintes
	 * para este móbil:
	 * <ul>
	 * <li><i>Destinatario esta definido</i>
	 *  <li><i>Destinatario pode receber documento</i>
	 *  <li><i>Se temporário, o documento está na mesma lotação do titular</i> 
	 * <li><i>Se finalizado, podeMovimentar()</i>
	 *  <li>Não pode haver configuração impeditiva</li> </ul>
	 * 
	 * @param destinatario
	 * @param lotaDestinatario 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * 
	 */
	public boolean podeTramitarPosAssinatura(final DpPessoa destinatario, final DpLotacao lotaDestinatario, 
			final DpPessoa titular, final DpLotacao lotaTitular, final ExMobil mob) {

		if (lotaDestinatario == null && destinatario == null) 
			return false;
		
		if (!podeReceberPorConfiguracao(destinatario, lotaDestinatario))
			return false;
		
		if (!getConf().podePorConfiguracao(titular, lotaTitular,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_TRANSFERENCIA,
				CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR))
			return false;

		if(!mob.getDoc().isFinalizado()) { /* documento temporário e não sofreu movimentação. A lotação onde se encontra é a do cadastrante */
			if (mob.getDoc().getLotaCadastrante() != null && !mob.getDoc().getLotaCadastrante().equivale(lotaTitular)) 
				return false;			 	
		} else {
			return podeMovimentar(titular, lotaTitular, mob);				 
		}
		
		return true;
		
	}


	/**
	 * Retorna se é possível fazer vinculação deste mobil a outro, conforme as
	 * seguintes regras para <i>este</i> móbil:
	 * <ul>
	 * <li>Precisa ser via ou volume (não pode ser geral)</li>
	 * <li>Não pode estar em trânsito</li>
	 * <li>Não pode estar juntado.</li>
	 * <li><i>podeMovimentar()</i> precisa retornar verdadeiro para ele</li>
	 * </ul>
	 * Não é levada em conta, aqui, a situação do mobil ao qual se pertende
	 * vincular.
	 * 
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeVincular(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) throws Exception {

		if (!(mob.isVia() || mob.isVolume()))
			return false;

		return !mob.isEmTransito() && podeMovimentar(titular, lotaTitular, mob)
				&& !mob.isJuntado();

		// return true;

	}


	public boolean podeCancelarVinculacaoDocumento(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob,
			final ExMovimentacao mov) {

		if (mov.isCancelada())
			return false;


		if ((mov.getCadastrante() != null && mov.getCadastrante().equivale(
				titular))
				|| (mov.getCadastrante() == null && mov.getLotaCadastrante()
						.equivale(lotaTitular)))
			return true;


		if ((mov.getSubscritor() != null && mov.getSubscritor().equivale(
				titular))
				|| (mov.getSubscritor() == null && mov.getLotaSubscritor()
						.equivale(lotaTitular)))
			return true;


		if ((mov.getLotaSubscritor().equivale(lotaTitular)))
			return true;

		return getConf().podePorConfiguracao(titular, lotaTitular,
				mov.getIdTpMov(),
				CpTipoConfiguracao.TIPO_CONFIG_CANCELAR_MOVIMENTACAO);
	}

	/**
	 * Retorna se é possível visualizar impressão do móbil. Sempre retorna
	 * <i>true</i>, a não ser que o documento esteja finalizado e o mobil em
	 * questão não seja via ou volume. isso impede que se visualize impressão do
	 * mobil geral após a finalização.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeVisualizarImpressao(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		if (!mob.isVia() && !mob.isVolume() && mob.getDoc().isFinalizado())

			return false;
		return !mob.isEliminado();/*
								 * if ((mob.doc().getConteudo() == null ||
								 * ExCompetenciaBL.viaCancelada(titular,
								 * lotaTitular, doc, numVia))) return false;

								 * return true;
								 */
	}

	/**
	 * Retorna se é possível visualizar impressão do documento em questão e de
	 * todos os filhos, com base na permissão para visualização da impressão de
	 * cada um dos filhos.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public boolean podeVisualizarImpressaoCompleta(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		Set<ExDocumento> filhos = mob.getExDocumentoFilhoSet();
		return podeVisualizarImpressao(titular, lotaTitular, mob)
				&& filhos != null && filhos.size() > 0;
	}

	/*
	 * public boolean podeAtenderPedidoPublicacaoPorConfiguracao( DpPessoa
	 * titular, DpLotacao lotaTitular, final ExMobil mob) throws Exception { if
	 * (lotaTitular == null) return false; return
	 * getConf().podePorConfiguracao(titular, lotaTitular,
	 * CpTipoConfiguracao.TIPO_CONFIG_ATENDER_PEDIDO_PUBLICACAO); }
	 */

	/**
	 * Retorna se é possível, com base em configuração, utilizar rotina para
	 * redefinição de permissões de publicação no DJE. Não é utilizado o
	 * parãmetro mob. <b>Atenção: Método em desuso.</b>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeDefinirPublicadoresPorConfiguracao(DpPessoa titular,
			DpLotacao lotaTitular, final ExMobil mob) throws Exception {
		if (lotaTitular == null)
			return false;
		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_DEFINIR_PUBLICADORES);

	}

	/**
	 * Retorna se é possível, com base em configuração, utilizar rotina para
	 * redefinição de permissões de publicação no Boletim. Não é utilizado o
	 * parâmetro mob.
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeGerenciarPublicacaoBoletimPorConfiguracao(
			DpPessoa titular, DpLotacao lotaTitular, final ExMobil mob) {
		if (lotaTitular == null)
			return false;
		return getConf().podePorConfiguracao(titular, lotaTitular,
				CpTipoConfiguracao.TIPO_CONFIG_GERENCIAR_PUBLICACAO_BOLETIM);
	}

	/**
	 * Método genérico que recebe função por String e concatena com o método de
	 * checagem de permissão correspondente. Por exemplo, para a função juntar,
	 * é invocado <i>podeJuntar()</i>
	 * 
	 * @param funcao
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 */
	public static boolean testaCompetencia(final String funcao,
			final DpPessoa titular, final DpLotacao lotaTitular,
			final ExMobil mob) {
		final Class[] classes = new Class[] { DpPessoa.class, DpLotacao.class,
				ExMobil.class };
		Boolean resposta = false;
		try {
			final Method method = ExCompetenciaBL.class.getDeclaredMethod(
					"pode" + funcao.substring(0, 1).toUpperCase()
							+ funcao.substring(1), classes);
			resposta = (Boolean) method.invoke(Ex.getInstance().getComp(),
					new Object[] { titular, lotaTitular, mob });
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return resposta.booleanValue();
	}
	/*
	 * Retorna se é possível incluir ciencia do documento a que pertence o
	 * móbil passado por parâmetro, conforme as seguintes condições:
	 * <ul>
	 * <li>Modelo do documento pode incluir documentos</li>
	 * <li>Documento não foi tramitado</li>
	 * <li>Documento tem de estar assinado ou autenticado</li>
	 * <li>Documento não pode estar juntado a outro</li>
	 * <li>Usuario não fez ciência ainda</li>
	 * <li>Não pode haver configuração impeditiva</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeFazerCiencia(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {

		if (mob.getDoc().isExternoCapturado() && mob.getDoc().getAutenticacoesComTokenOuSenha().isEmpty())
				return false;
		
		return (SigaMessages.isSigaSP()
					&& !mob.getDoc().isPendenteDeAssinatura() 
					&& !mob.isCiente(titular) 
					&& !mob.isEmTransito() 
					&& !mob.isEliminado() 
					&& !mob.isJuntado()
					&& !mob.isArquivado()
					&& !mob.isVolumeEncerrado()
					&& !getConf()
							.podePorConfiguracao(
									titular, 
									lotaTitular, 
									mob.getDoc().getExTipoDocumento(), 
									mob.getDoc().getExFormaDocumento(), 
									mob.getDoc().getExModelo(), CpTipoConfiguracao.TIPO_CONFIG_INCLUIR_DOCUMENTO)
					&& getConf()
							.podePorConfiguracao(
									titular,
									lotaTitular,
									ExTipoMovimentacao.TIPO_MOVIMENTACAO_CIENCIA,
									CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}
	
	/**
	 * Método genérico que recebe função por String e concatena com o método de
	 * checagem de permissão correspondente. Por exemplo, para a função
	 * excluirAnexo, é invocado <i>podeExcluirAnexo()</i>
	 * 
	 * @param funcao
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @param mov
	 * @return
	 */
	public static boolean testaCompetenciaMov(final String funcao,
			final DpPessoa titular, final DpLotacao lotaTitular,
			final ExMobil mob, final ExMovimentacao mov) {
		final Class[] classes = new Class[] { DpPessoa.class, DpLotacao.class,
				ExMobil.class, ExMovimentacao.class };
		Boolean resposta = false;
		try {
			/*
			 * final Method method = ExCompetenciaBL.class.getDeclaredMethod(
			 * "pode" + funcao.substring(0, 1).toUpperCase() +
			 * funcao.substring(1), classes);
			 */
			ExCompetenciaBL comp = Ex.getInstance().getComp();
			final Method method = comp.getClass().getDeclaredMethod(
					"pode" + funcao.substring(0, 1).toUpperCase()
							+ funcao.substring(1), classes);

			resposta = (Boolean) method.invoke(comp, new Object[] { titular,
					lotaTitular, mob, mov });
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return resposta.booleanValue();
	}


	/**
	 * 
	 */
	public boolean podeDesfazerCancelamentoDocumento(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {


		ExDocumento documento = mob.getDoc();


		if (documento.isEletronico()
				&& documento.isCancelado()
				&& (documento.getLotaCadastrante().equivale(lotaTitular) || documento
						.getSubscritor().equivale(titular)))
			return true;


		return false;
	}


	/**
	 * 
	 */
	public boolean podeReiniciarNumeracao(ExDocumento doc) throws Exception {
		if (doc == null || doc.getOrgaoUsuario() == null
				|| doc.getExFormaDocumento() == null)
			return false;

		return getConf().podePorConfiguracao(doc.getOrgaoUsuario(),
				doc.getExFormaDocumento(),
				CpTipoConfiguracao.TIPO_CONFIG_REINICIAR_NUMERACAO_TODO_ANO);
	}


	/**


	 * Retorna se é possível autuar um expediente, com base nas seguintes
	 * regras:
	 * <ul>
	 * <li>Documento tem de ser expediente</li>
	 * <li>Documento tem de estar assinado</li>
	 * <li>Documento não pode estar sem efeito</li>
	 * <li>Móbil não pode ser geral</li>
	 * <li>Móbil não pode estar em edital de eliminação</li>
	 * <li>Móbil não pode estar juntado</li>
	 * <li>Móbil não pode estar apensado</li>
	 * <li>Móbil não pode estar em trânsito</li>
	 * <li>Móbil não pode estar arquivado permanentemente</li>
	 * <li><i>podeMovimentar()</i> tem de ser verdadeiro para o usuário / móbil</li>
	 * </ul>
	 * 
	 * @param titular
	 * @param lotaTitular
	 * @param mob
	 * @return
	 * @throws Exception
	 */
	public boolean podeAutuar(final DpPessoa titular,
			final DpLotacao lotaTitular, final ExMobil mob) {
		
		if (mob.isPendenteDeAnexacao())
			return false;

		if (mob.getDoc().isSemEfeito())
			return false;

		if (mob.isEmEditalEliminacao() || mob.isArquivadoPermanente())
			return false;

		if (mob.isJuntado())
			return false;

		if (mob.isApensadoAVolumeDoMesmoProcesso())
			return false;
			
		if(mob.isArquivado())
			return false;
		
		if(mob.isSobrestado())
			return false;
			
		final boolean podeMovimentar = podeMovimentar(titular, lotaTitular, mob);

		return (!mob.isGeral() && mob.getDoc().isExpediente()
				&& !mob.getDoc().isPendenteDeAssinatura() && !mob.isEmTransito() && podeMovimentar && getConf().podePorConfiguracao(titular, lotaTitular,
						ExTipoMovimentacao.TIPO_MOVIMENTACAO_AUTUAR,
						CpTipoConfiguracao.TIPO_CONFIG_MOVIMENTAR));
	}

}
