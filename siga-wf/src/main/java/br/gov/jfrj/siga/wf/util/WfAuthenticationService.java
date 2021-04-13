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
package br.gov.jfrj.siga.wf.util;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

import java.util.List;

import org.jbpm.security.authentication.DefaultAuthenticationService;

import br.gov.jfrj.siga.acesso.ConheceUsuario;
import br.gov.jfrj.siga.acesso.UsuarioAutenticado;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.model.ContextoPersistencia;

/**
 * Classe que representa o serviço de autenticação do sistema de workflow. Esta
 * classe é definida em siga-wf/src/jbpm.cfg.xml.
 * 
 * @author kpf
 * 
 */
public class WfAuthenticationService extends DefaultAuthenticationService
		implements ConheceUsuario {

	String actorId = null;

	DpPessoa cadastrante = null;
	DpPessoa titular = null;
	DpLotacao lotaTitular = null;
	List<DpLotacao> outrasLotacoes;
	CpIdentidade identidadeCadastrante = null;

	/**
	 * Retorna o id do ator.
	 */
	@Override
	public String getActorId() {
		if (this.actorId == null) {
			if (ContextoPersistencia.getUserPrincipal() == null)
				return null;
			try {
				String principal = ContextoPersistencia.getUserPrincipal();
				UsuarioAutenticado.carregarUsuarioAutenticado(principal, this);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			if (getCadastrante() == null)
				return null;

			this.actorId = getCadastrante().getSigla();
		}
		return this.actorId;
	}

	/**
	 * Define o id do ator.
	 */
	@Override
	public void setActorId(String actorId) {
		this.actorId = actorId;
	}

	/**
	 * Retorna o cadastrante, ou seja, a pessoa que está operando o sistema.
	 */
	public DpPessoa getCadastrante() {
		return cadastrante;
	}

	/**
	 * Define o cadastrante, ou seja, a pessoa que está operando o sistema.
	 */
	public void setCadastrante(DpPessoa cadastrante) {
		this.cadastrante = cadastrante;
	}

	/**
	 * Retorna o titular, ou seja, quem é responsável oficial.
	 */
	public DpPessoa getTitular() {
		return titular;
	}

	/**
	 * Define o titular, ou seja, quem é responsável oficial.
	 */
	public void setTitular(DpPessoa titular) {
		this.titular = titular;
	}

	/**
	 * Retorna a lotação do titular
	 */
	public DpLotacao getLotaTitular() {
		return lotaTitular;
	}

	/**
	 * Define a lotação do titular.
	 */
	public void setLotaTitular(DpLotacao lotaTitular) {
		this.lotaTitular = lotaTitular;
	}

	public CpIdentidade getIdentidadeCadastrante() {
		return identidadeCadastrante;
	}

	public void setIdentidadeCadastrante(CpIdentidade identidadeCadastrante) {
		this.identidadeCadastrante = identidadeCadastrante;
	}

	@Override
	public List<DpLotacao> getOutrasLotacoes() {
		return nonNull(outrasLotacoes) ? outrasLotacoes : emptyList();
	}

	@Override
	public void setOutrasLotacoes(List<DpLotacao> outrasLotacoes) {
		this.outrasLotacoes = outrasLotacoes;
	}

}
