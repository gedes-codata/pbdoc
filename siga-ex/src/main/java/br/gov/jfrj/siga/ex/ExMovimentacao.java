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
 * Created Mon Nov 14 13:30:45 GMT-03:00 2005.
 */
package br.gov.jfrj.siga.ex;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.xerces.impl.dv.util.Base64;
import org.hibernate.annotations.BatchSize;

import br.gov.jfrj.itextpdf.Documento;
import br.gov.jfrj.siga.armazenamento.zip.ZipItem;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.SigaMessages;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.ex.util.DatasPublicacaoDJE;
import br.gov.jfrj.siga.ex.util.ProcessadorHtml;
import br.gov.jfrj.siga.ex.util.ProcessadorReferencias;
import br.gov.jfrj.siga.ex.util.PublicacaoDJEBL;

/**
 * A class that represents a row in the 'EX_MOVIMENTACAO' table. This class may
 * be customized as it is never re-generated after being created.
 */

@Entity
@BatchSize(size = 500)
@Table(name = "EX_MOVIMENTACAO", catalog = "SIGA")
public class ExMovimentacao extends AbstractExMovimentacao implements
		Serializable, Comparable<ExMovimentacao> {

	private static final long serialVersionUID = 2559924666592487436L;

	private static final Set<Long> TIPOS_MOVIMENTACAO_CANCELAMENTO = Collections.unmodifiableSet(new TreeSet<>(Arrays.asList(
			ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_MOVIMENTACAO,
			ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_JUNTADA
	)));

	/**
	 * Simple constructor of ExMovimentacao instances.
	 */
	public ExMovimentacao() {
	}

	/**
	 * Constructor of ExMovimentacao instances given a simple primary key.
	 * 
	 * @param idMov
	 */
	public ExMovimentacao(final java.lang.Long idMov) {
		super(idMov);
	}

	@Override
	public Long getId() {
		return this.getIdMov();
	}

	@Override
	public Long getIdMov() {
		return super.getIdMov();
	}

	/* Add customized code below */

	public String getDescrTipoMovimentacao() {
		String s = getExTipoMovimentacao().getSigla();
		if (getCadastrante() == null || getSubscritor() == null)
			return s;
		if (!getSubscritor().getId().equals(getCadastrante().getId())
			&& !SigaMessages.isSigaSP())
			s = s + " de Ordem";
		if (getExMovimentacaoCanceladora() != null)
			s = s + " (Cancelada)";
		return s;
	}

	public String getLotaPublicacao() {
		Map<String, String> atributosXML = new HashMap<String, String>();
		try {
			String xmlString = this.getConteudoXmlString("boletimadm");
			if (xmlString != null) {
				atributosXML = PublicacaoDJEBL.lerXMLPublicacao(xmlString);
				return atributosXML.get("UNIDADE");
			}
			return PublicacaoDJEBL.obterUnidadeDocumento(this.getExDocumento());
		} catch (Exception e) {
			return "Erro na leitura do arquivo XML (lotação de publicação)";

		}

	}

	public String getDescrPublicacao() {
		Map<String, String> atributosXML = new HashMap<String, String>();
		try {
			String xmlString = this.getConteudoXmlString("boletimadm");
			if (xmlString != null) {
				atributosXML = PublicacaoDJEBL.lerXMLPublicacao(this
						.getConteudoXmlString("boletimadm"));
				return atributosXML.get("DESCREXPEDIENTE");
			}
			return this.getExDocumento().getDescrDocumento();
		} catch (Exception e) {
			return "Erro na leitura do arquivo XML (descrição de publicação)";
		}

	}

	public Long getIdTpMov() {
		return getExTipoMovimentacao().getIdTpMov();
	}

	/**
	 * Retorna a data da movimentação no formato dd/mm/aa, por exemplo,
	 * 01/02/10.
	 * 
	 * @return Data da movimentação no formato dd/mm/aa, por exemplo, 01/02/10.
	 * 
	 */
	public String getDtMovDDMMYY() {
		if (this.getData() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
			return df.format(this.getData());
		}
		return "";
	}

	public String getDtMovDDMMYYYY() {
		if (this.getData() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			return df.format(this.getData());
		}
		return "";
	}

	public String getDtMovYYYYMMDD() {
		if (this.getData() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			return df.format(this.getData());
		}
		return "";
	}

	/**
	 * Retorna a data de início da movimentação no formato dd/mm/aa, por
	 * exemplo, 01/02/10.
	 * 
	 * @return Data de início da movimentação no formato dd/mm/aa, por exemplo,
	 *         01/02/10.
	 * 
	 */
	public String getDtRegMovDDMMYY() {
		if (getDtIniMov() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
			return df.format(getDtIniMov());
		}
		return "";
	}

	/**
	 * Retorna a data de início da movimentação no formato dd/mm/aa HH:MI:SS,
	 * por exemplo, 01/02/10 14:10:00.
	 * 
	 * @return Data de início da movimentação no formato dd/mm/aa HH:MI:SS, por
	 *         exemplo, 01/02/10 14:10:00.
	 * 
	 */
	public String getDtRegMovDDMMYYHHMMSS() {
		if (getDtIniMov() != null) {
			final SimpleDateFormat df = new SimpleDateFormat(
					"dd/MM/yy HH:mm:ss");
			return df.format(getDtIniMov());
		}
		return "";
	}

	/**
	 * Retorna a data de início da movimentação no formato dd/mm/aaaa HH:MI:SS,
	 * por exemplo, 01/02/2010 14:10:00.
	 * 
	 * @return Data de início da movimentação no formato dd/mm/aaaa HH:MI:SS,
	 *         por exemplo, 01/02/2010 14:10:00.
	 * 
	 */
	public String getDtRegMovDDMMYYYYHHMMSS() {
		if (getDtIniMov() != null) {
			final SimpleDateFormat df = new SimpleDateFormat(
					"dd/MM/yyyy HH:mm:ss");
			return df.format(getDtIniMov());
		}
		return "";
	}

	/**
	 * Retorna a data de retorno da movimentação no formato dd/mm/aa, por
	 * exemplo, 01/02/10.
	 * 
	 * @return Data de retorno da movimentação no formato dd/mm/aa, por exemplo,
	 *         01/02/10.
	 * 
	 */
	public String getDtFimMovDDMMYY() {
		if (getDtFimMov() != null) {
			final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
			return df.format(getDtFimMov());
		}
		return "";
	}

	/**
	 * Retorna a data de retorno da movimentação no formato dd/mm/aa HH:MI:SS,
	 * por exemplo, 01/02/10 14:10:00.
	 * 
	 * @return Data de retorno da movimentação no formato dd/mm/aa HH:MI:SS, por
	 *         exemplo, 01/02/10 14:10:00.
	 * 
	 */
	public String getDtFimMovDDMMYYHHMMSS() {
		if (getDtFimMov() != null) {
			final SimpleDateFormat df = new SimpleDateFormat(
					"dd/MM/yy HH:mm:ss");
			return df.format(getDtFimMov());
		}
		return "";
	}

	/**
	 * Retorna a data da movimentação por extenso. no formato "Rio de Janeiro,
	 * 01 de fevereiro de 2010", por exemplo.
	 * 
	 * @return Data da movimentação por extenso. no formato "Rio de Janeiro, 01
	 *         de fevereiro de 2010", por exemplo.
	 */
	public String getDtExtenso() {
		SimpleDateFormat df1 = new SimpleDateFormat();
		try {
			df1.applyPattern("dd/MM/yyyy");
			df1.applyPattern("dd 'de' MMMM 'de' yyyy.");

			String s = getNmLocalidade();

			DpLotacao lotaBase = null;
			if (getLotaTitular() != null)
				lotaBase = getLotaTitular();
			else if (getLotaSubscritor() != null)
				lotaBase = getLotaSubscritor();
			else if (getLotaCadastrante() != null)
				lotaBase = getLotaCadastrante();

			if (s == null && lotaBase != null) {
				s = lotaBase.getLocalidadeString();
				/*
				 * s = getLotaTitular().getOrgaoUsuario().getMunicipioOrgaoUsu()
				 * + ", ";
				 */
			}

			return s + ", " + df1.format(this.getData()).toLowerCase();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Retorna verdadeiro se a diferença entre a data de disponibilização no DJE
	 * e a data atual for igual a 2 e falso caso contrário.
	 * 
	 * @return Verdadeiro se a diferença entre a data de disponibilização no DJE
	 *         e a data atual for igual a 2 e falso caso contrário.
	 */
	public boolean isARemeterHojeDJE() {
		try {
			DatasPublicacaoDJE d = new DatasPublicacaoDJE(getDtDispPublicacao());
			return d.isDisponibilizacaoDMais2();
		} catch (AplicacaoException ae) {
			return false;
		}
	}

	/**
	 * Retorna a descrição da movimentação.
	 * 
	 * @return Descrição da movimentação.
	 */
	@Override
	public String getDescrMov() {
		return super.getDescrMov();
	}

	/**
	 * Retorna informações da movimentação como Nome do Órgão Externo,
	 * Observação do Órgão, Descrição do Tipo de Movimentação e Descrição da
	 * Movimentação.
	 * 
	 * @return Informações da movimentação como Nome do Órgão Externo,
	 *         Observação do Órgão, Descrição do Tipo de Movimentação e
	 *         Descrição da Movimentação.
	 */
	public String getObs() {
		String s = "";
		if (getOrgaoExterno() != null)
			s = s + getOrgaoExterno().getNmOrgao();

		if (getObsOrgao() != null) {
			if (s.length() > 0)
				s = s + "; ";

			s = s + getObsOrgao().trim();

			final String provObs = getObsOrgao().trim();
			if (!provObs.endsWith(".") && !provObs.endsWith("!")
					&& !provObs.endsWith("?"))
				s = s + ". ";
			else
				s = s + " ";
		}

		if (getExTipoDespacho() != null)
			s = s + getExTipoDespacho().getDescTpDespacho();

		if (getDescrMov() != null) {
			s = s + getDescrMov();
		}

		return s;
	}

	/**
	 * Retorna o número de sequência da via como um inteiro.
	 * 
	 * @return Número de sequência como um inteiro se for uma via e 0 caso
	 *         contrário.
	 */
	public int getNumVia2() {
		return getExMobil().isVia() ? getExMobil().getNumSequencia().intValue()
				: 0;
	}

	/**
	 * Retorna o número de sequência da via como uma String.
	 * 
	 * @return Número de sequência como uma String se for uma via e "" caso
	 *         contrário.
	 */
	public String getNumViaString() {
		if (getNumVia2() == 0)
			return "";
		return String.valueOf(getNumVia2());
	}

	private Integer tpMovDesempatePosicao(Long idTpMov) {
		final List<Long> tpMovDesempate = Arrays.asList(new Long[] {ExTipoMovimentacao.TIPO_MOVIMENTACAO_CRIACAO,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_DOCUMENTO,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_COM_SENHA,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_DOCUMENTO,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_JUNTADA,
				ExTipoMovimentacao.TIPO_MOVIMENTACAO_TRANSFERENCIA});

		if (idTpMov == null)
			return Integer.MAX_VALUE;
		
		int i = tpMovDesempate.indexOf(idTpMov);
		if (i == -1)
			return Integer.MAX_VALUE;
		return i;
	}

	public int compareTo(final ExMovimentacao mov) {
		try {
			int i = 0;
			if (getDtIniMov() != null)
				i = getDtIniMov().compareTo(mov.getDtIniMov());
			if (i != 0)
				return i;
			
			if (getExTipoMovimentacao() != null && mov.getExTipoMovimentacao() != null) {
				i = tpMovDesempatePosicao(getExTipoMovimentacao().getId()).compareTo(
						tpMovDesempatePosicao(mov.getExTipoMovimentacao().getId()));
				if (i != 0)
					return i;
			}
			
			i = getIdMov().compareTo(mov.getIdMov());
			return i;
		} catch (final Exception ex) {
			return 0;
		}
	}

	/**
	 * Retorna o nome do responsável pela movimentação.
	 * 
	 * @return Nome do responsável pela movimentação.
	 */
	public String getRespString() {
		if (getOrgaoExterno() != null)
			return getObs();
		else {
			String strReturn = "";
			if (getLotaResp() != null)
				strReturn = getLotaResp().getDescricao();
			if (getResp() != null)
				strReturn = strReturn + " - " + getResp().getDescricao();
			return strReturn;
		}
	}
	
	/**
	 * Retorna o nome do responsável pela movimentação sem a descricao.
	 * 
	 * @return Nome do responsável pela movimentação sem a descricao.
	 */
	public String getRespSemDescrString() {
			return getOrgaoExterno().getNmOrgao();
	}

	/**
	 * Retorna Descrição da Movimentação
	 * 
	 * @return Descrição da Movimentação
	 */
	public String getRespDescrString() {
		if (getObsOrgao() != null) {
			return getObsOrgao().trim();
		}
		return "";
	}
	
	/**
	 * Retorna o nome do responsável pela movimentação.
	 * 
	 * @return Nome do responsável pela movimentação.
	 */
	public String getCadastranteString() {
		String strReturn = "";
		if (getLotaResp() != null)
			strReturn = getLotaResp().getDescricao();
		if (getResp() != null)
			strReturn = strReturn + " - " + getResp().getDescricao();
		return strReturn;
	}

	public String getConteudoBlobHtmlB64() {
		return Base64.encode(getConteudoBlobHtml());
	}

	public void setConteudoBlobHtml(final byte[] conteudo) {
		setConteudoBlob(ZipItem.Tipo.HTM, conteudo);
	}

	public void setConteudoBlobPdf(final byte[] conteudo) {
		setConteudoBlob(ZipItem.Tipo.PDF, conteudo);
	}

	public void setConteudoBlobForm(final byte[] conteudo) {
		setConteudoBlob(ZipItem.Tipo.FORM, conteudo);
	}

	public void setConteudoBlobXML(final String nome, final byte[] conteudo) {
		setConteudoBlob(ZipItem.Tipo.XML.comNome(nome), conteudo);
	}

	public void setConteudoBlobRTF(final String nome, final byte[] conteudo) {
		setConteudoBlob(ZipItem.Tipo.RTF.comNome(nome), conteudo);
	}

	public byte[] getConteudoBlobHtml() {
		return getConteudoBlob(ZipItem.Tipo.HTM);
	}

	public String getConteudoBlobHtmlString() {
		if (getConteudoBlobHtml() == null) {
			return null;
		}
		try {
			return new String(getConteudoBlobHtml(), "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			return new String(getConteudoBlobHtml());
		}
	}

	public byte[] getConteudoBlobPdfNecessario() {
		if (getConteudoBlobHtml() == null)
			return getConteudoBlobPdf();
		return null;
	}

	/**
	 * Retorna o documento relacionado a movimentação.
	 * 
	 * @return Documento relacionado a movimentação.
	 */
	public ExDocumento getExDocumento() {
		return super.getExMobil().getExDocumento();
	}

	public byte[] getConteudoBlobPdf() {
		return getConteudoBlob(ZipItem.Tipo.PDF);
	}

	public byte[] getConteudoBlobForm() {
		return getConteudoBlob(ZipItem.Tipo.FORM);
	}

	public byte[] getConteudoBlobXML() {
		return getConteudoBlob(ZipItem.Tipo.XML);
	}

	public String getConteudoXmlString(String nome) throws UnsupportedEncodingException {
		byte[] xmlByte = this.getConteudoBlob(ZipItem.Tipo.XML.comNome(nome));
		if (xmlByte != null) {
			return new String(xmlByte, "ISO-8859-1");
		}
		return null;
	}

	public byte[] getConteudoBlobRTF() {
		return getConteudoBlob(ZipItem.Tipo.RTF);
	}

	public void setConteudoBlobHtmlString(final String s) throws Exception {
		final String sHtml = (new ProcessadorHtml()).canonicalizarHtml(s,
				false, true, false, false, false);
		setConteudoBlob(ZipItem.Tipo.HTM, sHtml.getBytes("ISO-8859-1"));
	}

	/**
	 * Retorna o nome da Função do Subscritor da Movimentação.
	 * 
	 * @return Nome da Função do Subscritor da Movimentação.
	 */
	public java.lang.String getNmFuncao() {
		if (getNmFuncaoSubscritor() == null)
			return null;
		String a[] = getNmFuncaoSubscritor().split(";");
		if (a.length < 1)
			return null;
		if (a[0].length() == 0)
			return null;
		return a[0];
	}

	/**
	 * Retorna o nome do arquivo anexado a movimentação.
	 * 
	 * @return Nome do arquivo anexado a movimentação.
	 */
	public String getNmArqMov() {
		String s = super.getNmArqMov();

		if (s != null) {
			s = s.trim();
			if (s.length() == 0)
				return null;
		}
		return s;
	}

	/**
	 * Retorna o nome do arquivo anexado a movimentação sem extensão.
	 * 
	 * @return Nome do arquivo anexado a movimentação sem extensão.
	 */
	public String getNmArqMovSemExtensao() {
		String s = super.getNmArqMov();
		if (s != null) {
			s = s.trim();
			if (s.length() == 0)
				return null;

			try {
				return s.split("\\.")[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return s;
	}

	/**
	 * Retorna o nome da lotação do subscritor da movimentação.
	 * 
	 * @return Nome da lotação do subscritor da movimentação.
	 */
	public java.lang.String getNmLotacao() {
		if (getNmFuncaoSubscritor() == null)
			return null;
		String a[] = getNmFuncaoSubscritor().split(";");
		if (a.length < 2)
			return null;
		if (a[1].length() == 0)
			return null;
		return a[1];
	}

	/**
	 * Retorna o nome da localidade da lotação do subscritor da movimentação.
	 * 
	 * @return Nome da localidade da lotação do subscritor da movimentação.
	 */
	public java.lang.String getNmLocalidade() {
		if (getNmFuncaoSubscritor() == null)
			return null;
		String a[] = getNmFuncaoSubscritor().split(";");
		if (a.length < 3)
			return null;
		if (a[2].length() == 0)
			return null;
		return a[2];
	}

	/**
	 * Retorna o nome do subscritor da movimentação.
	 * 
	 * @return Nome do subscritor da movimentação.
	 */
	public java.lang.String getNmSubscritor() {
		if (getNmFuncaoSubscritor() == null)
			return null;
		String a[] = getNmFuncaoSubscritor().split(";");
		if (a.length < 4)
			return null;
		if (a[3].length() == 0)
			return null;
		return a[3];
	}

	/**
	 * Retorna o código da movimentação de referência da movimentação Atual.
	 * 
	 * @return Código da movimentação de referência da movimentação Atual.
	 */
	public java.lang.String getReferencia() {
		return getExMobil().getCodigoCompacto() + ":" + getIdMov();
		/*
		 * este atributo é utilizado p/ compor nmPdf (abaixo), não retirar o
		 * caracter ":" /* pois este é utilizado no método
		 * ExMovimentacaoAction.recuperarAssinaturaAppletB64()
		 */
	}

	public java.lang.String getNmPdf() {
		return getReferencia() + ".pdf";
	}

	// public String getNumViaToChar() {
	// if (getNumVia2() == 0)
	// return "";
	//
	// return "" + Character.toChars(getNumVia2() + 64)[0];
	// }
	//
	// public String getNumViaDocPaiToChar() {
	// return ""
	// + Character.toChars(getExMobilPai().getNumSequencia()
	// .intValue() + 64)[0];
	// }
	//
	// public String getNumViaDocRefToChar() {
	// return ""
	// + Character.toChars(getExMobilRef().getNumSequencia()
	// .intValue() + 64)[0];
	// }

	@Override
	public String getHtml() {
		return getConteudoBlobHtmlString();
	}

	@Override
	public String getHtmlComReferencias() throws Exception {
		return getConteudoBlobHtmlStringComReferencias();
	}

	private String getConteudoBlobHtmlStringComReferencias() throws Exception {
		String sHtml = getConteudoBlobHtmlString();
		ProcessadorReferencias pr = new ProcessadorReferencias();
		pr.ignorar(this.getExMobil().getExDocumento().getSigla());
		pr.ignorar(this.getExMobil().getSigla());
		sHtml = pr.marcarReferencias(sHtml);
		return sHtml;
	}

	@Override
	public byte[] getPdf() {
		return getConteudoBlobPdf();
	}

	@Override
	public boolean isPdf() {
		return (getNumPaginas() != null && getNumPaginas() > 0)
				|| (getPdf() != null);
	}

	/**
	 * verifica se uma movimentação está cancelada. Uma movimentação está
	 * cancelada quando o seu atributo movimentacaoCanceladora está preenchido
	 * com um código de movimentação de cancelamento.
	 * 
	 * @return Verdadeiro se a movimentação está cancelada e Falso caso
	 *         contrário.
	 */
	public boolean isCancelada() {
		return getExMovimentacaoCanceladora() != null;
	}

	/**
	 * verifica se uma movimentação é canceladora, ou seja, se é do tipo
	 * Cancelamento de Movimentação.
	 * 
	 * @return Verdadeiro ou Falso.
	 */
	public boolean isCanceladora() {
		return getExTipoMovimentacao() != null && TIPOS_MOVIMENTACAO_CANCELAMENTO.contains(getExTipoMovimentacao().getId());
	}

	/**
	 * verifica se uma movimentação de anexação de arquivo está assinada e não
	 * está cancelada. Este tipo de movimentação está assinada quando existe
	 * alguma movimentação de assinatura de movimentação com o seu atributo
	 * movimentacaoReferenciadora igual ao código da movimentação de anexação de
	 * arquivo.
	 * 
	 * @return Verdadeiro se a movimentação está assinada e Falso caso
	 *         contrário.
	 */
	public boolean isAssinada() {
		if (this.isCancelada()
				|| this.getExMobil().getExMovimentacaoSet() == null)
			return false;

		// Usamos getExMovimentacaoSet() em vez de
		// getExMovimentacaoReferenciadoraSet() porque o segundo faz lazy
		// initialization e não recebe as movimentações mais recentes quando se
		// está calculando os marcadores.
		for (ExMovimentacao assinatura : this.getExMobil()
				.getExMovimentacaoSet()) {
			long l = assinatura.getIdTpMov();
			if (l != ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_MOVIMENTACAO
					&& l != ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_DOCUMENTO
					&& l != ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA
					&& l != ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA)
				continue;
			if (assinatura.getExMovimentacaoRef() == null)
				continue;
			if (this.getIdMov().equals(
					assinatura.getExMovimentacaoRef().getIdMov()))
				return true;
		}

		return false;
	}

	/**
	 * Uma movimentação está autenticado quando ela possui pelo menos uma
	 * assinatura com senha.
	 */
	public boolean isAutenticada() {
		if (!this.isCancelada()
				&& this.getExMovimentacaoReferenciadoraSet() != null) {
			for (ExMovimentacao movRef : this
					.getExMovimentacaoReferenciadoraSet()) {
				if (movRef.getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_MOVIMENTACAO
						|| movRef.getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_DOCUMENTO)
					return true;
			}
		}

		return false;
	}

	/**
	 * Retorna se uma movimentação possui assinaturas com senha.
	 */
	public boolean temAssinaturasComSenha() {
		if (getApenasAssinaturasComSenha() != null
				&& getApenasAssinaturasComSenha().size() > 0)
			return true;

		return false;
	}

	public String getSiglaAssinatura() {
		return getExDocumento().getIdDoc()
				+ "."
				+ getIdMov()
				+ "-"
				+ Math.abs((getExDocumento().getDescrCurta() + getIdMov())
						.hashCode() % 10000);
	}

	public String getSiglaAssinaturaExterna() {
		return getExDocumento().getIdDoc()
				+ "."
				+ getIdMov()
				+ "-"
				+ Math.abs((getExDocumento().getDescrCurta() + getIdMov() + "AssinaturaExterna")
						.hashCode() % 10000);
	}

	public Set<ExMovimentacao> getAssinaturasDigitais() {
		TreeSet<ExMovimentacao> movs = new TreeSet<ExMovimentacao>();
		movs.addAll(getApenasAssinaturas());
		movs.addAll(getApenasConferenciasCopia());
		return movs;
	}

	/**
	 * Retorna uma coleção de movimentações dos tipo:
	 * ASSINATURA_DIGITAL_MOVIMENTACAO.
	 * 
	 * @return Coleção de movimentações de assinaturas digitais.
	 */
	public Set<ExMovimentacao> getApenasAssinaturas() {
		Set<ExMovimentacao> set = new TreeSet<ExMovimentacao>();

		for (ExMovimentacao m : getExMovimentacaoReferenciadoraSet()) {
			if ((m.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_MOVIMENTACAO || m
					.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA)
					&& m.getExMovimentacaoCanceladora() == null) {
				set.add(m);
			}
		}
		return set;
	}

	/**
	 * Retorna uma coleção de movimentações dos tipo:
	 * ASSINATURA_DIGITAL_MOVIMENTACAO.
	 * 
	 * @return Coleção de movimentações de assinaturas com Token.
	 */
	public Set<ExMovimentacao> getApenasAssinaturasComToken() {
		Set<ExMovimentacao> set = new TreeSet<ExMovimentacao>();

		for (ExMovimentacao m : getExMovimentacaoReferenciadoraSet()) {
			if ((m.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_DIGITAL_MOVIMENTACAO)
					&& m.getExMovimentacaoCanceladora() == null) {
				set.add(m);
			}
		}
		return set;
	}

	/**
	 * Retorna uma coleção de movimentações dos tipo:
	 * ASSINATURA_DIGITAL_MOVIMENTACAO.
	 * 
	 * @return Coleção de movimentações de assinaturas com Senha.
	 */
	public Set<ExMovimentacao> getApenasAssinaturasComSenha() {
		Set<ExMovimentacao> set = new TreeSet<ExMovimentacao>();

		for (ExMovimentacao m : getExMovimentacaoReferenciadoraSet()) {
			if ((m.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_ASSINATURA_MOVIMENTACAO_COM_SENHA)
					&& m.getExMovimentacaoCanceladora() == null) {
				set.add(m);
			}
		}
		return set;
	}

	/**
	 * Retorna uma coleção de movimentações dos tipo
	 * CONFERENCIA_COPIA_DOCUMENTO.
	 * 
	 * @return Coleção de movimentações de conferências de cópia.
	 */
	public Set<ExMovimentacao> getApenasConferenciasCopia() {
		Set<ExMovimentacao> set = new TreeSet<ExMovimentacao>();

		for (ExMovimentacao m : getExMovimentacaoReferenciadoraSet()) {
			if ((m.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_DOCUMENTO || m
					.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA)
					&& m.getExMovimentacaoCanceladora() == null) {
				set.add(m);
			}
		}
		return set;
	}

	/**
	 * Retorna uma coleção de movimentações dos tipo
	 * CONFERENCIA_COPIA_DOCUMENTO.
	 * 
	 * @return Coleção de movimentações de conferências de cópia com token.
	 */
	public Set<ExMovimentacao> getApenasConferenciasCopiaComToken() {
		Set<ExMovimentacao> set = new TreeSet<ExMovimentacao>();

		for (ExMovimentacao m : getExMovimentacaoReferenciadoraSet()) {
			if ((m.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_DOCUMENTO)
					&& m.getExMovimentacaoCanceladora() == null) {
				set.add(m);
			}
		}
		return set;
	}

	/**
	 * Retorna uma coleção de movimentações dos tipo
	 * CONFERENCIA_COPIA_DOCUMENTO.
	 * 
	 * @return Coleção de movimentações de conferências de cópia com senha.
	 */
	public Set<ExMovimentacao> getApenasConferenciasCopiaComSenha() {
		Set<ExMovimentacao> set = new TreeSet<ExMovimentacao>();

		for (ExMovimentacao m : getExMovimentacaoReferenciadoraSet()) {
			if ((m.getExTipoMovimentacao().getIdTpMov() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CONFERENCIA_COPIA_COM_SENHA)
					&& m.getExMovimentacaoCanceladora() == null) {
				set.add(m);
			}
		}
		return set;
	}

	public String getAssinantesComTokenString() {
		return Documento.getAssinantesString(getApenasAssinaturasComToken());
	}

	public String getAssinantesComSenhaString() {
		return Documento.getAssinantesString(getApenasAssinaturasComSenha());
	}

	public String getConferentesString() {
		return Documento.getAssinantesString(getApenasConferenciasCopia());
	}

	public String getAssinaturaComSenhaDataHoraString() {
		return Documento.getAssinaturaComSenhaDataHoraString(getApenasAssinaturasComSenha());
	}

	public String getAssinantesCompleto() {
		
		String conferentes = getConferentesString();
		String assinantesToken = getAssinantesComTokenString();
		String retorno = "";
		String assinantesSenhaDataHora = getAssinaturaComSenhaDataHoraString();
		
		retorno += assinantesToken.length() > 0 ? "Assinado digitalmente por "
				+ assinantesToken + ".\n" : "";
		retorno += assinantesSenhaDataHora.length() > 0 ? "Assinado com senha por "
				+ assinantesSenhaDataHora + ".\n" : "";

		retorno += conferentes.length() > 0 ? "Autenticado digitalmente por "
				+ conferentes + " em " + "dataAssinatura" + "hs.\n" : "";

		return retorno;
	}

	/**
	 * verifica se uma movimentação está cancelada. Uma movimentação está
	 * cancelada quando o seu atributo movimentacaoCanceladora está preenchido
	 * com um código de movimentação de cancelamento.
	 * 
	 * @return Verdadeiro se a movimentação está cancelada e Falso caso
	 *         contrário.
	 */
	@Override
	public boolean isCancelado() {
		return isCancelada();
	}

	@Override
	public boolean isRascunho() {
		return ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO == getExTipoMovimentacao().getIdTpMov() && mob().getDoc().isEletronico() && !isAssinada();
	}

	@Override
	public boolean isSemEfeito() {
		if (getExDocumento().isSemEfeito()) {
			// Não gera marca de "Sem Efeito em Folha de Desentranhamento"
			return getExTipoMovimentacao().getId() != ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_JUNTADA;
		}
		return false;
	}

	/**
	 * Retorna da lotação do titular da movimentação.
	 * 
	 * @return Lotação do titular da movimentação.
	 */
	@Override
	public DpLotacao getLotacao() {
		return getLotaTitular();
	}

	/**
	 * Retorna uma descrição da movimentação formada pelos campos: Sigla,
	 * Descrição do Tipo de Movimentação e Descrição da Movimentação.
	 * 
	 * @return Uma descrição da movimentação
	 */
	@Override
	public String toString() {
		return (getExMobil() != null ? getExMobil().getSigla() : "")
				+ ": "
				+ (getExTipoMovimentacao() != null ? getExTipoMovimentacao()
						.getDescricao() : "") + ": " + getDescrMov();
	}

	/**
	 * @return Verdadeiro se o tipo de movimentação for CANCELAMENTO_JUNTADA ou
	 *         CANCELAMENTO_DE_MOVIMENTACAO e Falso caso contrário
	 */
	public boolean isInserirDocumentoNoDossieDoMobilRef() {
		return getExTipoMovimentacao().getId() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_JUNTADA
				|| getExTipoMovimentacao().getId() == ExTipoMovimentacao.TIPO_MOVIMENTACAO_CANCELAMENTO_DE_MOVIMENTACAO;
	}

	/**
	 * @return Data de início da movimentação de referência se o método
	 *         isInserirDocumentoNoDossieDoMobilRef() for verdadeiro ou retorna
	 *         a data de início da movimentação caso contrário.
	 * 
	 */
	public Date getDtIniMovParaInsercaoEmDossie() {
		if (getExTipoMovimentacao() == null)
			return null;
		if (isInserirDocumentoNoDossieDoMobilRef()) {
			return getExMovimentacaoRef().getDtIniMov();
		}
		return getDtIniMov();
	}

	/**
	 * Retorna o Mobil relacionado a movimentação atual.
	 * 
	 * @return Data de início da movimentação de referência se o método
	 *         isInserirDocumentoNoDossieDoMobilRef() for verdadeiro ou retorna
	 *         a data de início da movimentação caso contrário.
	 * 
	 */
	public ExMobil mob() {
		return getExMobil();
	}

	@Override
	public boolean isInternoProduzido() {
		switch (getExTipoMovimentacao().getIdTpMov().intValue()) {
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO:
			return false;
		}
		return true;
	}

	public boolean isUltimaMovimentacao() {
		return getIdMov().equals(
				getExMobil().getUltimaMovimentacao().getIdMov());
	}

	@Override
	public boolean isCodigoParaAssinaturaExterna(String num) {

		int hash = Integer.parseInt(num.substring(num.indexOf("-") + 1));

		for (ExMovimentacao mov : getExDocumento().getExMovimentacaoSet())
			if (Math.abs((getExDocumento().getDescrCurta() + mov.getIdMov() + "AssinaturaExterna")
					.hashCode() % 10000) == hash)
				return true;
		return false;

	}

	@Override
	public String getTipoDescr() {
		switch (getExTipoMovimentacao().getIdTpMov().intValue()) {
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_ANEXACAO:
			return "Anexo";
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO:
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_TRANSFERENCIA:
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_INTERNO:
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_INTERNO_TRANSFERENCIA:
		case (int) ExTipoMovimentacao.TIPO_MOVIMENTACAO_DESPACHO_TRANSFERENCIA_EXTERNA:
			return "Despacho";
		}
		return "Outro";
	}
}