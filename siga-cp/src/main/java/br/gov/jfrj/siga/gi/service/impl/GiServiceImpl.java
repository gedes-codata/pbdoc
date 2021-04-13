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
package br.gov.jfrj.siga.gi.service.impl;

import static org.apache.commons.lang.StringUtils.EMPTY;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.jws.WebService;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.jboss.logging.Logger;

import br.gov.jfrj.siga.acesso.ConfiguracaoAcesso;
import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.base.GeraMessageDigest;
import br.gov.jfrj.siga.base.LoginTokenKey;
import br.gov.jfrj.siga.base.Texto;
import br.gov.jfrj.siga.cp.CpIdentidade;
import br.gov.jfrj.siga.cp.CpServico;
import br.gov.jfrj.siga.cp.bl.Cp;
import br.gov.jfrj.siga.cp.bl.CpPropriedadeBL;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpCargo;
import br.gov.jfrj.siga.dp.DpFuncaoConfianca;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.dp.DpPessoa;
import br.gov.jfrj.siga.dp.dao.CpDao;
import br.gov.jfrj.siga.dp.dao.DpLotacaoDaoFiltro;
import br.gov.jfrj.siga.dp.dao.DpPessoaDaoFiltro;
import br.gov.jfrj.siga.gi.integracao.IntegracaoLdapViaWebService;
import br.gov.jfrj.siga.gi.service.GiService;

/**
 * Esta classe implementa os métodos de gestão de identidade O acesso à esta
 * classe é realizado via web-services, com interfaces definidas no módulo
 * siga-ws, conforme o padrão adotados para o SIGA.
 * 
 * @author tah
 * 
 */
@WebService(serviceName = "GiService", endpointInterface = "br.gov.jfrj.siga.gi.service.GiService", targetNamespace = "http://impl.service.gi.siga.jfrj.gov.br/")
public class GiServiceImpl implements GiService {

	private static final Logger log = Logger.getLogger(GiServiceImpl.class);

    private boolean autenticaViaBanco(CpIdentidade identidade, String senha) {
    	// caso o campo senha esteja vazio ou nulo, retorna false. 
    	// Não autentica usuários com senha em branco.
    	if(identidade.getDscSenhaIdentidade() == null || identidade.getDscSenhaIdentidade().equals(EMPTY)) 
    		return false;
    	
    	try {
    		final String hashAtual = GeraMessageDigest.executaHash(senha.getBytes(), "MD5");
    		if (identidade != null && identidade.getDscSenhaIdentidade().equals(hashAtual)) return true;
		} catch (Exception e) {
			return false;
		}
    	return false;
    }
    
    private boolean autenticaViaLdap(String matricula, String senha) {
    	try {
			return IntegracaoLdapViaWebService.getInstancia().autenticarUsuario(matricula, senha);
		} catch (Exception e) {
			return false;
		}
    }
    
    public String buscarModoAutenticacao(String login) {
    	CpIdentidade id = null;
		CpDao dao = CpDao.getInstance();
		id = dao.consultaIdentidadeCadastrante(login, true);
		return buscarModoAutenticacao(id);
    }

    private String buscarModoAutenticacao(CpIdentidade id) {
    	String orgao = id.getCpOrgaoUsuario().getSiglaOrgaoUsu();
    	String retorno = _MODO_AUTENTICACAO_DEFAULT;
    	CpPropriedadeBL props = new CpPropriedadeBL();
    	try {
			String modo = props.getModoAutenticacao(orgao);
			if(modo != null) 
				retorno = modo;
		} catch (Exception e) {
		}
    	return retorno;
    }
    
    @Override
    public String login(String matricula, String senha) {
		String resultado = EMPTY;

		CpIdentidade id = null;
		CpDao dao = CpDao.getInstance();
		id = dao.consultaIdentidadeCadastrante(matricula, true);
		String modoAut = buscarModoAutenticacao(id);

		try {
			if(modoAut.equals(_MODO_AUTENTICACAO_BANCO)) {
				if (autenticaViaBanco(id, senha)) {
					resultado = parseLoginResult(id);
				}
			} else if(modoAut.equals(_MODO_AUTENTICACAO_LDAP)) {
				if(autenticaViaLdap(matricula, senha)) {
					resultado = parseLoginResult(id);
				}
			}

		} catch (AplicacaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultado;
	}

    @Override
	public String dadosUsuario(String matricula) {
		String resultado = EMPTY;
		try {
			CpDao dao = CpDao.getInstance();

			CpIdentidade id = null;
			id = dao.consultaIdentidadeCadastrante(matricula, true);
			if (id != null) {
				resultado = parseLoginResult(id);
			}
		} catch (AplicacaoException e) {
			e.printStackTrace();
		}
		return resultado;
	}
    
    @Override
	public String perfilAcessoPorCpf(String cpf) {
		String resultado = EMPTY;
		try {
			if( Pattern.matches( "\\d+", cpf) && cpf.length() == 11) {
				List<CpIdentidade> lista = new CpDao().consultaIdentidadesCadastrante(cpf, Boolean.TRUE);
				if(!lista.isEmpty()) {
					resultado = parseAcessosResult(lista);
				} else {
					resultado = "Não foi possível buscar acessos. Acessos não localizados.";
				}
			} else {
				resultado = "Não foi possível buscar acessos. CPF inválido.";
			}

		} catch (AplicacaoException e) {
			e.printStackTrace();
		}
		return resultado;
	}
    
	private String parseAcessosResult(List<CpIdentidade> lista) {
		JSONArray acessos = new JSONArray();

		try {
			if (!lista.isEmpty()) {
		        for (CpIdentidade identidade : lista) {
		    		JSONObject pessoa = new JSONObject();
		    		JSONObject lotacao = new JSONObject();
		    		JSONObject cargo = new JSONObject();
		    		JSONObject funcao = new JSONObject();
		    		
		        	//Pessoa
		        	DpPessoa p = identidade.getPessoaAtual();
		        	pessoa.put(LoginTokenKey.KEY_SIGLA_PESSOA, p.getSiglaCompleta());
		        	pessoa.put(LoginTokenKey.KEY_NOME_PESSOA, p.getNomePessoa());
		        	
		        	//Lotacao Pessoa
		        	DpLotacao l = p.getLotacao();
		        	lotacao.put(LoginTokenKey.KEY_ID_LOTACAO, l.getId());
		        	lotacao.put(LoginTokenKey.KEY_NOME_LOTACAO, l.getNomeLotacao());
		        	lotacao.put(LoginTokenKey.KEY_SIGLA_LOTACAO, l.getSigla());
		        	
		        	//Cargo Pessoa
					DpCargo c = p.getCargo();
					if (c != null) {
						cargo.put(LoginTokenKey.KEY_ID_CARGO, c.getId());
						cargo.put(LoginTokenKey.KEY_NOME_CARGO, c.getNomeCargo());
					}
					//Função Pessoa
					DpFuncaoConfianca f = p.getFuncaoConfianca();
					if (f != null) {
						funcao.put(LoginTokenKey.KEY_ID_FUNCAO_CONFIANCA, f.getId());
						funcao.put(LoginTokenKey.KEY_NOME_FUNCAO_CONFIANCA, f.getNomeFuncao());
					}
					
					pessoa.put(LoginTokenKey.KEY_LOTACAO, lotacao);
					pessoa.put(LoginTokenKey.KEY_CARGO, cargo);
					pessoa.put(LoginTokenKey.KEY_FUNCAO_CONFIANCA, funcao);
					
					acessos.put(pessoa);
		        }
			}
			return acessos.toString();
		} catch (Exception e) {
			List<Long> acessosIds = lista.stream().map(CpIdentidade::getId).collect(Collectors.toList());
			log.warn("Não foi possível criar token de acessos para CpIdentidades: " + acessosIds);
			return EMPTY;
		}
	}

	private String parseLoginResult(CpIdentidade id) {
		JSONObject pessoa = new JSONObject();
		JSONObject lotacao = new JSONObject();
		JSONObject cargo = new JSONObject();
		JSONObject funcao = new JSONObject();

		try {
			DpPessoa p = id.getPessoaAtual();
			pessoa.put(LoginTokenKey.KEY_ID_PESSOA, p.getId());
			pessoa.put(LoginTokenKey.KEY_ID_EXTERNA_PESSOA, p.getIdExterna());
			pessoa.put(LoginTokenKey.KEY_MATRICULA_PESSOA, p.getMatricula());
			pessoa.put(LoginTokenKey.KEY_CPF, p.getCpfPessoa());
			pessoa.put(LoginTokenKey.KEY_SIGLA_PESSOA, p.getSiglaCompleta());
			pessoa.put(LoginTokenKey.KEY_NOME_PESSOA, p.getNomePessoa());
			pessoa.put(LoginTokenKey.KEY_EMAIL_PESSOA, p.getEmailPessoaAtual());
			pessoa.put(LoginTokenKey.KEY_SIGLA_PESSOA_WEMUL, p.getSiglaPessoa());
			pessoa.put(LoginTokenKey.KEY_TIPO_SERVIDOR, p.getCpTipoPessoa() != null ? p.getCpTipoPessoa().getIdTpPessoa() : "null");

			DpLotacao l = p.getLotacao();
			lotacao.put(LoginTokenKey.KEY_ID_LOTACAO, l.getId());
			lotacao.put(LoginTokenKey.KEY_ID_EXTERNA_LOTACAO, l.getIdExterna());
			lotacao.put(LoginTokenKey.KEY_NOME_LOTACAO, l.getNomeLotacao());
			lotacao.put(LoginTokenKey.KEY_SIGLA_LOTACAO, l.getSigla());
			lotacao.put(LoginTokenKey.KEY_ID_LOTACAO_PAI, l.getIdLotacaoPai());
			lotacao.put(LoginTokenKey.KEY_ID_LOTACAO, l.getCpTipoLotacao() != null ? l.getCpTipoLotacao().getIdTpLotacao() : "null");
			lotacao.put(LoginTokenKey.KEY_SIGLA_TP_LOTACAO, l.getCpTipoLotacao() != null ? l.getCpTipoLotacao().getSiglaTpLotacao() : "null");

			DpCargo c = p.getCargo();
			if (c != null) {
				cargo.put(LoginTokenKey.KEY_ID_CARGO, c.getId());
				cargo.put(LoginTokenKey.KEY_ID_EXTERNA_CARGO, c.getIdExterna());
				cargo.put(LoginTokenKey.KEY_NOME_CARGO, c.getNomeCargo());
				cargo.put(LoginTokenKey.KEY_SIGLA_CARGO, c.getSigla());
			}

			DpFuncaoConfianca f = p.getFuncaoConfianca();
			if (f != null) {
				funcao.put(LoginTokenKey.KEY_ID_FUNCAO_CONFIANCA, f.getId());
				funcao.put(LoginTokenKey.KEY_ID_EXTERNA_FUNCAO_CONFIANCA, f.getIdeFuncao());
				funcao.put(LoginTokenKey.KEY_NOME_FUNCAO_CONFIANCA, f.getNomeFuncao());
				funcao.put(LoginTokenKey.KEY_SIGLA_FUNCAO_CONFIANCA, f.getSigla());
				funcao.put(LoginTokenKey.KEY_ID_PAI_FUNCAO_CONFIANCA, f.getIdFuncaoPai());
			}

			pessoa.put(LoginTokenKey.KEY_LOTACAO, lotacao);
			pessoa.put(LoginTokenKey.KEY_CARGO, cargo);
			pessoa.put(LoginTokenKey.KEY_FUNCAO_CONFIANCA, funcao);

			return pessoa.toString();
		} catch (Exception e) {
			log.warn("Não foi possível criar token de login para CpIdentidade " + id.getId());
			return EMPTY;
		}
	}

	@SuppressWarnings("unused")
	public String acesso(String matricula, String lotacao, String servico) {
		JSONObject servicos = new JSONObject();
		String resultado = EMPTY;
		try {
			CpDao dao = CpDao.getInstance();

			DpPessoaDaoFiltro flt = new DpPessoaDaoFiltro();
			flt.setSigla(matricula);
			DpPessoa p = (DpPessoa) dao.consultarPorSigla(flt);

			DpLotacao lot = null;
			if (lotacao != null) {
				DpLotacaoDaoFiltro fltLot = new DpLotacaoDaoFiltro();
				fltLot.setSiglaCompleta(lotacao);
				lot = (DpLotacao) dao.consultarPorSigla(fltLot);
			}

			boolean pode = Cp.getInstance().getConf().podeUtilizarServicoPorConfiguracao(p, lot, servico);

			CpServico srv = dao.consultarCpServicoPorChave(servico);

			if (p != null) {
				ConfiguracaoAcesso ac;
				ac = ConfiguracaoAcesso.gerar(null, p, lot, null, srv, null);
				if (ac != null)
					servicos.put(ac.getServico().getSigla(), ac.getSituacao()
							.getDscSitConfiguracao());
			}
			resultado = servicos.toString(2);
		} catch (AplicacaoException e) {
			return EMPTY;
		} catch (JSONException e) {
			return EMPTY;
		} catch (Exception e) {
			return EMPTY;
		}
		return resultado;
	}

	public String acessos(String matricula, String lotacao) {
		JSONObject servicos = new JSONObject();
		String resultado = EMPTY;
		try {
			CpDao dao = CpDao.getInstance();

			DpPessoaDaoFiltro flt = new DpPessoaDaoFiltro();
			flt.setSigla(matricula);
			DpPessoa p = (DpPessoa) dao.consultarPorSigla(flt);

			DpLotacao lot = null;
			if (lotacao != null) {
				DpLotacaoDaoFiltro fltLot = new DpLotacaoDaoFiltro();
				fltLot.setSiglaCompleta(lotacao);
				lot = (DpLotacao) dao.consultarPorSigla(fltLot);
			}

			if (p != null) {
				List<CpServico> l = dao.listarServicos();
				for (CpServico srv : l) {
					ConfiguracaoAcesso ac;
					try {
						ac = ConfiguracaoAcesso.gerar(null, p, lot, null, srv,
								null);
						if (ac != null)
							servicos.put(ac.getServico().getSigla(), ac
									.getSituacao().getDscSitConfiguracao());
					} catch (Exception e) {
					}
				}
			}
			resultado = servicos.toString(2);
		} catch (AplicacaoException e) {
			return EMPTY;
		} catch (JSONException e) {
			return EMPTY;
		}
		return resultado;
	}
	
	@Override
	public String esqueciSenha(String cpf, String email) {
		String resultado = EMPTY;
		try {
			resultado = Cp.getInstance().getBL().alterarSenha(cpf, email, null, EMPTY);
		} catch (Exception e) {
			return EMPTY;
		}
		return resultado;
	}

	@Override
	public String criarUsuario(String orgaoUsu,String lotacao, String cargo, String funcao,String nmPessoa, String dtNascimento, String cpf, String email) {
		
		String resultado = EMPTY;
		try {
			
			if(orgaoUsu == null || EMPTY.equals(orgaoUsu.trim()))
				throw new AplicacaoException("Órgão não informado");
			
			if(cargo == null || EMPTY.equals(cargo.trim()))
				throw new AplicacaoException("Cargo não informado");
			
			if(lotacao == null || EMPTY.equals(lotacao.trim()))
				throw new AplicacaoException("Unidade não informada");
			
			if(nmPessoa == null || EMPTY.equals(nmPessoa.trim()))
				throw new AplicacaoException("Nome não informado");
			
			if(cpf == null || EMPTY.equals(cpf.trim())) 
				throw new AplicacaoException("CPF não informado");
			
			if(email == null || EMPTY.equals(email.trim())) 
				throw new AplicacaoException("E-mail não informado");
			
			if(nmPessoa != null && !nmPessoa.matches("[a-zA-ZáâãéêíóôõúçÁÂÃÉÊÍÓÔÕÚÇ'' ]+")) 
				throw new AplicacaoException("Nome com caracteres não permitidos");
			
			Long idOrgaoUsu = null;
			Long idCargoUsu = null;
			Long idLotacaoUsu = null;
			Long idFuncaoUsu = null;

			//Obtém Id Órgão
			CpOrgaoUsuario orgaoUsuario = new CpOrgaoUsuario();
			orgaoUsuario.setNmOrgaoUsu(Texto.removeAcento(orgaoUsu));
			orgaoUsuario = CpDao.getInstance().consultarPorNome(orgaoUsuario);
			if (orgaoUsuario == null){
				throw new AplicacaoException("Órgão não localizado");
			} else {
				idOrgaoUsu = orgaoUsuario.getIdOrgaoUsu();
			}		
			
			//Obtém Cargo
			DpCargo cargoUsuario = new DpCargo();
			cargoUsuario.setNomeCargo(Texto.removeAcento(cargo));
			cargoUsuario.setOrgaoUsuario(orgaoUsuario);
			cargoUsuario = CpDao.getInstance().consultarPorNomeOrgao(cargoUsuario);
			if (cargoUsuario == null){
				throw new AplicacaoException("Cargo não localizado");
			} else {
				idCargoUsu = cargoUsuario.getId();
			}
			
			//Obtém Unidade
			DpLotacao lotacaoUsuario = new DpLotacao();
			lotacaoUsuario.setNomeLotacao(Texto.removeAcento(lotacao));
			lotacaoUsuario.setOrgaoUsuario(orgaoUsuario);
			lotacaoUsuario = CpDao.getInstance().consultarPorNomeOrgao(lotacaoUsuario);	
			if (lotacaoUsuario == null){
				throw new AplicacaoException("Unidade não localizada");
			} else {
				idLotacaoUsu = lotacaoUsuario.getId();
			}
			
			
			//Obtém Função
			if(funcao != null && !EMPTY.equals(funcao.trim())) {
				DpFuncaoConfianca funcaoConfianca = new DpFuncaoConfianca();
				funcaoConfianca.setNomeFuncao(Texto.removeAcento(funcao));
				funcaoConfianca.setOrgaoUsuario(orgaoUsuario);
				funcaoConfianca = CpDao.getInstance().consultarPorNomeOrgao(funcaoConfianca);
				if (funcaoConfianca == null){
					throw new AplicacaoException("Função não localizada");
				} else {
					idFuncaoUsu = funcaoConfianca.getId();
				}	
			}
		
			resultado = Cp.getInstance().getBL().criarUsuario(null, idOrgaoUsu, idCargoUsu, idFuncaoUsu, idLotacaoUsu, nmPessoa, dtNascimento, cpf, email);

		} catch (Exception e) {
			return e.getMessage();
		}
		return resultado;
	}
}