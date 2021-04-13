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
 * Criado em  05/01/2006
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package br.gov.jfrj.siga.vraptor;

import org.apache.commons.lang.StringUtils;

import br.gov.jfrj.siga.base.util.CPFUtils;

public class UsuarioAction {

	private String cpf;

	private String matricula;

	private String nomeUsuario;

	private String senhaAtual;

	private String senhaConfirma;

	private String senhaNova;

	private String titulo;

	private String tit;
	
	private Integer metodo;
	
	private String auxiliar1;
	
	private String cpf1;
	
	private String senha1;
	
	private String auxiliar2;
	
	private String cpf2;
	
	private String senha2;
	
	private Long idOrgao;
	
	private String trocarSenhaRede;
	
	private String ajaxMsgErro;

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		if (StringUtils.isNotEmpty(cpf)) {
			this.cpf = CPFUtils.limpaCpf(cpf);
		}
	}

	public String getMatricula() {
		return matricula;
	}

	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getSenhaConfirma() {
		return senhaConfirma;
	}

	public void setSenhaConfirma(String senhaConfirma) {
		this.senhaConfirma = senhaConfirma;
	}

	public String getSenhaNova() {
		return senhaNova;
	}

	public void setSenhaNova(String senhaNova) {
		this.senhaNova = senhaNova;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getTit() {
		return tit;
	}

	public void setTit(String tit) {
		this.tit = tit;
	}

	public Integer getMetodo() {
		return metodo;
	}

	public void setMetodo(Integer metodo) {
		this.metodo = metodo;
	}

	public String getAuxiliar1() {
		return auxiliar1;
	}

	public void setAuxiliar1(String auxiliar1) {
		this.auxiliar1 = auxiliar1;
	}

	public String getCpf1() {
		return cpf1;
	}

	public void setCpf1(String cpf1) {
		if (StringUtils.isNotEmpty(cpf1)) {
			this.cpf1 = CPFUtils.limpaCpf(cpf1);
		}
	}

	public String getSenha1() {
		return senha1;
	}

	public void setSenha1(String senha1) {
		this.senha1 = senha1;
	}

	public String getAuxiliar2() {
		return auxiliar2;
	}

	public void setAuxiliar2(String auxiliar2) {
		this.auxiliar2 = auxiliar2;
	}

	public String getCpf2() {
		return cpf2;
	}

	public void setCpf2(String cpf2) {
		if (StringUtils.isNotEmpty(cpf2)) {
			this.cpf2 = CPFUtils.limpaCpf(cpf2);
		}
	}

	public String getSenha2() {
		return senha2;
	}

	public void setSenha2(String senha2) {
		this.senha2 = senha2;
	}

	public Long getIdOrgao() {
		return idOrgao;
	}

	public void setIdOrgao(Long idOrgao) {
		this.idOrgao = idOrgao;
	}

	public String getTrocarSenhaRede() {
		return trocarSenhaRede;
	}

	public void setTrocarSenhaRede(String trocarSenhaRede) {
		this.trocarSenhaRede = trocarSenhaRede;
	}

	public String getAjaxMsgErro() {
		return ajaxMsgErro;
	}

	public void setAjaxMsgErro(String ajaxMsgErro) {
		this.ajaxMsgErro = ajaxMsgErro;
	}
}
