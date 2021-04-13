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
package br.gov.jfrj.siga.cp.bl;

import static java.lang.Character.toUpperCase;
import static org.apache.commons.lang.StringUtils.stripToNull;

// TODO: _LAGS - eliminar (deixar a String livre) - Ver com Renato o CpDao.criarHibernateCfg(String) já existe para a passagem de datasource 
public enum CpAmbienteEnumBL {

	PRODUCAO("prod", "Produção"),
	HOMOLOGACAO("homolo", "Homologação"),
	TREINAMENTO("treina", "Treinamento"),
	DESENVOLVIMENTO("desenv", "Desenvolvimento");

	private final String sigla;
	private final String nome;

	CpAmbienteEnumBL(String sigla, String nome) {
		this.sigla = sigla;
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public String getNome() {
		return nome;
	}

	public static CpAmbienteEnumBL pelaPrimeiraLetra(String propriedade) {
		propriedade = stripToNull(propriedade);
		if (propriedade == null) {
			throw new IllegalArgumentException("Propriedade do ambiente não pode ser nulo.");
		}

		char primeiraLetra = toUpperCase(propriedade.charAt(0));
		switch (primeiraLetra) {
		case 'P':
			return PRODUCAO;
		case 'H':
			return HOMOLOGACAO;
		case 'T':
			return TREINAMENTO;
		case 'D':
			return DESENVOLVIMENTO;
		default:
			throw new IllegalArgumentException("Ambiente " + propriedade + " não especificado entre os conhecidos: " + values() + ".");
		}
	}

}
