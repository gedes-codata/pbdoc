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
package br.gov.jfrj.siga.ex.vo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import br.gov.jfrj.siga.ex.bl.ExBL;

public class ExVO {

	List<ExAcaoVO> acoes = new ArrayList<ExAcaoVO>();

	private class NomeExAcaoVOComparator implements Comparator<ExAcaoVO> {

		public int compare(ExAcaoVO o1, ExAcaoVO o2) {
			return o1.getNome().replace("_", "").compareTo(o2.getNome().replace("_", ""));
		}
	}

	public List<ExAcaoVO> getAcoes() {
		return acoes;
	}

	public List<ExAcaoVO> getAcoesOrdenadasPorNome() {
		return ExAcaoVO.ordena(getAcoes(), new NomeExAcaoVOComparator());
	}

	public void setAcoes(List<ExAcaoVO> acoes) {
		this.acoes = acoes;
	}

	protected void addAcao(String icone, String nome, String nameSpace,
			String action, boolean pode) {
		addAcao(icone, nome, nameSpace, action, pode, null, null, null, null, null, nome);
	}

	protected void addAcao(String icone, String nome, String nameSpace,
			String action, boolean pode, String msgConfirmacao,
			String parametros, String pre, String pos, String classe) {
		addAcao(icone, nome, nameSpace, action, pode, msgConfirmacao, parametros, pre, pos, classe, nome);
	}

	protected void addAcao(String icone, String nome, String nameSpace,
			String action, boolean pode, String msgConfirmacao,
			String parametros, String pre, String pos, String classe, String hint) {
		TreeMap<String, Object> params = new TreeMap<String, Object>();

		if (this instanceof ExMovimentacaoVO) {
			params.put("id",
					Long.toString(((ExMovimentacaoVO) this).getIdMov()));
			// params.put("sigla", ((ExMovimentacaoVO)
			// this).getMobilVO().getSigla());
		} else if (this instanceof ExMobilVO) {
			params.put("sigla", ((ExMobilVO) this).getSigla());
		} else if (this instanceof ExDocumentoVO) {
			params.put("sigla", ((ExDocumentoVO) this).getSigla());
		}

		if (parametros != null) {
			if (parametros.startsWith("&")) {
				parametros = parametros.substring(1);
			} else {
				params.clear();
			}
			ExBL.mapFromUrlEncodedForm(params, parametros.getBytes(StandardCharsets.ISO_8859_1));
		}

		if (pode) {
			String hintEscapado = StringUtils.replace(nome, "_", "");
			ExAcaoVO acao = new ExAcaoVO(icone, nome, nameSpace, action, pode,
					msgConfirmacao, params, pre, pos, classe, hintEscapado);
			acoes.add(acao);
		}
	}
}
