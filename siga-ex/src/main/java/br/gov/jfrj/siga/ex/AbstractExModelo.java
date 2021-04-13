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
 */
package br.gov.jfrj.siga.ex;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

import br.gov.jfrj.siga.cp.model.HistoricoAuditavelSuporte;
import br.gov.jfrj.siga.model.Assemelhavel;

/**
 * A class that represents a row in the EX_MODELO table. You can customize the
 * behavior of this class by editing the class, {@link ExModelo()}.
 */
@MappedSuperclass
@NamedQueries({ @NamedQuery(name = "consultarModeloAtual", query = "select mod from ExModelo mod where mod.hisIdIni = :hisIdIni and mod.hisDtFim = null") })
public abstract class AbstractExModelo extends HistoricoAuditavelSuporte
		implements Serializable {
	/** The composite primary key value. */
	@Id
	@SequenceGenerator(sequenceName = "EX_MODELO_SEQ", name = "EX_MODELO_SEQ")
	@GeneratedValue(generator = "EX_MODELO_SEQ")
	@Column(name = "ID_MOD", unique = true, nullable = false)
	private java.lang.Long idMod;

	/** The value of the simple conteudoBlobMod property. */
	@Column(name = "CONTEUDO_BLOB_MOD")
	@Basic(fetch = FetchType.EAGER)
	private byte[] conteudoBlobMod;

	/** The value of the simple conteudoTpBlob property. */
	@Column(name = "CONTEUDO_TP_BLOB", length = 128)
	private java.lang.String conteudoTpBlob;

	/** The value of the simple descMod property. */
	@Column(name = "DESC_MOD", length = 256)
	private java.lang.String descMod;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_CLASS_CRIACAO_VIA")
	private ExClassificacao exClassCriacaoVia;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_CLASSIFICACAO")
	private ExClassificacao exClassificacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_FORMA_DOC")
	private ExFormaDocumento exFormaDocumento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_NIVEL_ACESSO")
	private ExNivelAcesso exNivelAcesso;

	/** The value of the exModeloTipologiaSet one-to-many association. */

	@Column(name = "NM_ARQ_MOD", length = 256)
	private java.lang.String nmArqMod;

	/** The value of the simple nomeModelo property. */
	@Column(name = "NM_MOD", nullable = false, length = 128)
	private java.lang.String nmMod;

	@Column(name = "NM_DIRETORIO", length = 128)
	private java.lang.String nmDiretorio;

	@Column(name = "HIS_IDE", length = 128)
	private java.lang.String uuid;

	// private Set classificacaoSet;

	/**
	 * Simple constructor of AbstractExModelo instances.
	 */
	public AbstractExModelo() {
	}

	/**
	 * Constructor of AbstractExModelo instances given a simple primary key.
	 * 
	 * @param idMod
	 */
	public AbstractExModelo(final java.lang.Long idMod) {
		this.setIdMod(idMod);
	}

	/**
	 * Implementation of the equals comparison on the basis of equality of the
	 * primary key values.
	 * 
	 * @param rhs
	 * @return boolean
	 */
	@Override
	public boolean equals(final Object rhs) {
		if ((rhs == null) || !(rhs instanceof ExModelo))
			return false;
		final ExModelo that = (ExModelo) rhs;
		if ((this.getIdMod() == null ? that.getIdMod() == null : this
				.getIdMod().equals(that.getIdMod()))) {
			if ((this.getDescMod() == null ? that.getDescMod() == null : this
					.getDescMod().equals(that.getDescMod())))
				return true;
		}
		return false;

	}

	/**
	 * Return the value of the CONTEUDO_BLOB_MOD column.
	 * 
	 * @return java.lang.String
	 */
	public byte[] getConteudoBlobMod() {
		return this.conteudoBlobMod;
	}

	/**
	 * Return the value of the CONTEUDO_TP_BLOB column.
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getConteudoTpBlob() {
		return this.conteudoTpBlob;
	}

	/**
	 * Return the value of the DESC_MOD column.
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDescMod() {
		return this.descMod;
	}

	public ExClassificacao getExClassCriacaoVia() {
		return exClassCriacaoVia;
	}

	public ExClassificacao getExClassificacao() {
		return exClassificacao;
	}

	/*    *//**
	 * @return Retorna o atributo tipologiaSet.
	 */
	/*
	 * public Set getClassificacaoSet() { return classificacaoSet; }
	 */

	public ExFormaDocumento getExFormaDocumento() {
		return exFormaDocumento;
	}

	/**
	 * Return the simple primary key value that identifies this object.
	 * 
	 * @return java.lang.Long
	 */
	public java.lang.Long getIdMod() {
		return idMod;
	}

	/*    *//**
	 * @param classificacaoSet
	 *            Atribui a classificacaoSet o valor.
	 */
	/*
	 * public void setClassificacaoSet(Set classificacaoSet) {
	 * this.classificacaoSet = classificacaoSet; }
	 */

	public java.lang.String getNmArqMod() {
		return nmArqMod;
	}

	/**
	 * Return the value of the NM_MOD column.
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getNmMod() {
		return this.nmMod;
	}

	/**
	 * Implementation of the hashCode method conforming to the Bloch pattern
	 * with the exception of array properties (these are very unlikely primary
	 * key types).
	 * 
	 * @return int
	 */
	@Override
	public int hashCode() {
		int result = 17;
		int idValue = this.getIdMod() == null ? 0 : this.getIdMod().hashCode();
		result = result * 37 + idValue;
		idValue = this.getDescMod() == null ? 0 : this.getDescMod().hashCode();
		return result * 37 + idValue;
	}

	/**
	 * Set the value of the CONTEUDO_BLOB_MOD column.
	 * 
	 * @param conteudoBlobMod
	 */
	public void setConteudoBlobMod(byte[] conteudoBlobMod) {
		this.conteudoBlobMod = conteudoBlobMod;
	}

	/**
	 * Set the value of the CONTEUDO_TP_BLOB column.
	 * 
	 * @param conteudoTpBlob
	 */
	public void setConteudoTpBlob(final java.lang.String conteudoTpBlob) {
		this.conteudoTpBlob = conteudoTpBlob;
	}

	/**
	 * Set the value of the DESC_MOD column.
	 * 
	 * @param descMod
	 */
	public void setDescMod(final java.lang.String descMod) {
		this.descMod = descMod;
	}

	public void setExClassCriacaoVia(ExClassificacao exClassCriacaoVia) {
		this.exClassCriacaoVia = exClassCriacaoVia;
	}

	public void setExClassificacao(final ExClassificacao exClassificacao) {
		this.exClassificacao = exClassificacao;
	}

	public void setExFormaDocumento(final ExFormaDocumento exFormaDocumento) {
		this.exFormaDocumento = exFormaDocumento;
	}

	/**
	 * Set the simple primary key value that identifies this object.
	 * 
	 * @param idMod
	 */
	public void setIdMod(final java.lang.Long idMod) {
		this.idMod = idMod;
	}

	public void setNmArqMod(final java.lang.String nmArqMod) {
		this.nmArqMod = nmArqMod;
	}

	/**
	 * Set the value of the NM_MOD column.
	 * 
	 * @param nmMod
	 */
	public void setNmMod(final java.lang.String nmMod) {
		this.nmMod = nmMod;
	}

	public ExNivelAcesso getExNivelAcesso() {
		return exNivelAcesso;
	}

	public void setExNivelAcesso(ExNivelAcesso exNivelAcesso) {
		this.exNivelAcesso = exNivelAcesso;
	}

	public java.lang.String getNmDiretorio() {
		return nmDiretorio;
	}

	public void setNmDiretorio(java.lang.String nmDiretorio) {
		this.nmDiretorio = nmDiretorio;
	}

	public java.lang.String getUuid() {
		return uuid;
	}

	public void setUuid(java.lang.String uuid) {
		this.uuid = uuid;
	}

	public boolean semelhante(Assemelhavel obj, int profundidade) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		AbstractExModelo other = (AbstractExModelo) obj;
		if (conteudoBlobMod == null) {
			if (other.conteudoBlobMod != null)
				return false;
		} else {
			if (other.getConteudoBlobMod() == null)
				return false;
			byte[] abthis = getConteudoBlobMod();
			byte[] abother = other.getConteudoBlobMod();
			try {
				String sthis = new String(abthis, "UTF-8");
				String sother = new String(abother, "UTF-8");

				sthis = sthis.replace("\r\n", "\n");
				sother = sother.replace("\r\n", "\n");

				if (!sthis.equals(sother)) {
					// System.out.println(Hex.encodeHexString(abthis));
					// System.out.println(Hex.encodeHexString(abother));
					return false;
				}
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		if (conteudoTpBlob == null) {
			if (other.conteudoTpBlob != null)
				return false;
		} else if (!conteudoTpBlob.equals(other.conteudoTpBlob))
			return false;
		if (descMod == null) {
			if (other.descMod != null)
				return false;
		} else if (!descMod.equals(other.descMod))
			return false;
		if (exClassCriacaoVia == null) {
			if (other.exClassCriacaoVia != null)
				return false;
		} else if (!exClassCriacaoVia.equals(other.exClassCriacaoVia))
			return false;
		if (exClassificacao == null) {
			if (other.exClassificacao != null)
				return false;
		} else if (!exClassificacao.equals(other.exClassificacao))
			return false;
		if (exFormaDocumento == null) {
			if (other.exFormaDocumento != null)
				return false;
		} else if (!exFormaDocumento.equals(other.exFormaDocumento))
			return false;
		if (exNivelAcesso == null) {
			if (other.exNivelAcesso != null)
				return false;
		} else if (!exNivelAcesso.equals(other.exNivelAcesso))
			return false;
		if (nmArqMod == null) {
			if (other.nmArqMod != null)
				return false;
		} else if (!nmArqMod.equals(other.nmArqMod))
			return false;
		if (nmDiretorio == null) {
			if (other.nmDiretorio != null)
				return false;
		} else if (!nmDiretorio.equals(other.nmDiretorio))
			return false;
		if (nmMod == null) {
			if (other.nmMod != null)
				return false;
		} else if (!nmMod.equals(other.nmMod))
			return false;
		return true;
	}

}
