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

import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.model.Objeto;

/**
 * A class that represents a row in the EX_TIPO_DESPACHO table. You can
 * customize the behavior of this class by editing the class, {@link
 * ExTipoDespacho()}.
 */
@MappedSuperclass
@NamedQueries({
		@NamedQuery(name = "consultarPorFiltroExPreenchimento", query = "from ExPreenchimento pre "
				+ "	      where upper(pre.nomePreenchimento) like upper('%' || :nomePreenchimento || '%') "
				+ "			and (:lotacao = null or :lotacao = 0 or pre.dpLotacao = :lotacao)"
				+ "			and (:modelo=null or :modelo = 0 or pre.exModelo.hisIdIni = :modelo)"),
		@NamedQuery(name = "excluirPorIdExPreenchimento", query = "delete from ExPreenchimento where idPreenchimento = :id") })
public abstract class AbstractExPreenchimento extends Objeto implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The composite primary key value. */
	@Id
	@SequenceGenerator(sequenceName = "EX_PREENCHIMENTO_SEQ", name = "EX_PREENCHIMENTO_SEQ")
	@GeneratedValue(generator = "EX_PREENCHIMENTO_SEQ")
	@Column(name = "ID_PREENCHIMENTO", unique = true, nullable = false)
	private java.lang.Long idPreenchimento;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_LOTACAO", nullable = false)
	private DpLotacao dpLotacao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_MOD", nullable = false)
	private ExModelo exModelo;

	@Column(name = "EX_NOME_PREENCHIMENTO", nullable = false, length = 256)
	private String nomePreenchimento;

	@Column(name = "PREENCHIMENTO_BLOB")
	private byte[] preenchimentoBlob;

	/**
	 * Simple constructor of AbstractExTipoDespacho instances.
	 */
	public AbstractExPreenchimento() {
	}

	public AbstractExPreenchimento(final java.lang.Long idPreenchimento) {
		this.setIdPreenchimento(idPreenchimento);
	}

	public DpLotacao getDpLotacao() {
		return dpLotacao;
	}

	public void setDpLotacao(DpLotacao dpLotacao) {
		this.dpLotacao = dpLotacao;
	}

	public ExModelo getExModelo() {
		return exModelo;
	}

	public void setExModelo(ExModelo exModelo) {
		this.exModelo = exModelo;
	}

	public java.lang.Long getIdPreenchimento() {
		return idPreenchimento;
	}

	public void setIdPreenchimento(java.lang.Long idPreenchimento) {
		this.idPreenchimento = idPreenchimento;
	}

	public byte[] getPreenchimentoBlob() {
		return preenchimentoBlob;
	}

	public void setPreenchimentoBlob(byte[] preenchimentoBlob) {
		this.preenchimentoBlob = preenchimentoBlob;
	}

	@Override
	public boolean equals(final Object rhs) {
		if ((rhs == null) || !(rhs instanceof ExPreenchimento))
			return false;
		final ExPreenchimento that = (ExPreenchimento) rhs;
		if ((this.getIdPreenchimento() == null ? that.getIdPreenchimento() == null
				: this.getIdPreenchimento().equals(that.getPreenchimentoBlob())))
			return true;
		return false;

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
		final int idDocValue = this.getIdPreenchimento() == null ? 0 : this
				.getIdPreenchimento().hashCode();
		return result * 37 + idDocValue;
	}

	public String getNomePreenchimento() {
		return nomePreenchimento;
	}

	public void setNomePreenchimento(String nomePreenchimento) {
		this.nomePreenchimento = nomePreenchimento;
	}

}
