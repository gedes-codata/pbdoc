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
package br.gov.jfrj.siga.acesso;

import java.util.List;

import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;

public interface ConheceUsuario {

	void setIdentidadeCadastrante(CpIdentidade idc);

	CpIdentidade getIdentidadeCadastrante();

	void setCadastrante(DpPessoa pessoa);

	DpPessoa getCadastrante();

	void setTitular(DpPessoa pesSubstituindo);

	DpPessoa getTitular();

	void setLotaTitular(DpLotacao lotaSubstituindo);

	DpLotacao getLotaTitular();

	void setOutrasLotacoes(List<DpLotacao> outrasLotacoes);

	List<DpLotacao> getOutrasLotacoes();

}
