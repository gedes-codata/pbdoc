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
package br.gov.jfrj.siga.ex;

import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import br.gov.jfrj.itextpdf.Documento;
import br.gov.jfrj.siga.armazenamento.zip.ZipItem;
import br.gov.jfrj.siga.armazenamento.zip.ZipServico;
import br.gov.jfrj.siga.dp.CpOrgaoUsuario;
import br.gov.jfrj.siga.dp.DpLotacao;
import br.gov.jfrj.siga.model.Objeto;

@MappedSuperclass
public abstract class ExArquivo extends Objeto {

	private static final long serialVersionUID = -7483037836759972415L;

	public static final String YEAR_PATTERN = "yyyy";
	public static final String ZIP_MIME_TYPE = "application/zip";
	public static final String EXTENSAO_ZIP = ".zip";

	protected static final String ERRO_CAMINHO_ARQUIVO = "Erro ao montar caminho para o arquivo \"%s\" de ID=%d: campo \"%s\" não pôde ser convertido em caminho";

	@Column(name = "NUM_PAGINAS")
	private Integer numPaginas;

	@Transient
	protected Map<ZipItem, byte[]> cacheConteudo;

	public abstract void setMimeType(String mimeType);

	public abstract String getAssinantesCompleto();

	public abstract Set<ExMovimentacao> getAssinaturasDigitais();

	/**
	 * Retorna o número de páginas de um arquivo.
	 * 
	 * @return Número de páginas de um arquivo.
	 * 
	 */
	public Integer getContarNumeroDePaginas() {
		try {
			byte[] abPdf = null;
			abPdf = getPdf();
			if (abPdf == null)
				return null;
			return Documento.getNumberOfPages(abPdf);
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Retorna o pdf do documento com stamp. Método criado para tentar stampar
	 * um documento que está sendo anexado.
	 * 
	 * @return pdf com stamp.
	 * 
	 */
	public byte[] getArquivoComStamp() {
		try {
			byte[] abPdf = null;
			abPdf = getPdf();
			if (abPdf == null)
				return null;

			// Verifica se é possível estampar o documento
			try {
				byte[] documentoComStamp = Documento.stamp(abPdf, "", true,
						false, false, false, false, null, null, null, null, null,
						null, null);

				return documentoComStamp;

			} catch (Exception e) {
				return null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Retorna o número de bytes no PDF.
	 * 
	 * @return número de bytes no PDF.
	 * 
	 */
	public int getNumBytes() {
		try {
			byte[] abPdf = null;
			abPdf = getPdf();
			if (abPdf == null)
				return 0;
			return abPdf.length;
		} catch (Exception e) {
			return 0;
		}
	}
	
	public abstract Date getData();

	public abstract void setData(Date data);

	public abstract String getHtml();

	public abstract String getHtmlComReferencias() throws Exception;

	public abstract Long getId();

	public abstract String getMimeType();

	public abstract DpLotacao getLotaTitular();

	public abstract DpLotacao getLotacao();

	/**
	 * Retorna uma mensagem informando quem assinou o documento e o endereço
	 * onde o usuário pode verificar a autenticidade de um documento com base em
	 * um código gerado.
	 * 
	 */
	public String getMensagem() {
		String sMensagem = "";
		if (isAssinadoDigitalmente()) {
			sMensagem += getAssinantesCompleto();
			sMensagem += "Documento Nº: " + getSiglaAssinatura()
					+ " - consulta à autenticidade em \n"
					+ SigaExProperties.getEnderecoAutenticidadeDocs() + "?n="+getSiglaAssinatura();
		}
		return sMensagem;
	}

	/**
	 * Caso o método esteja sendo executado em um objeto do tipo documento,
	 * retorna a código do documento. Caso o método esteja sendo executado em um
	 * objeto do tipo movimentação, retorna o nome do arquivo desta
	 * movimentação.
	 * 
	 */
	public String getNome() {
		if (this instanceof ExDocumento) {
			ExDocumento doc = (ExDocumento) this;
			return doc.getCodigo();
		}

		if (this instanceof ExMovimentacao) {
			ExMovimentacao mov = (ExMovimentacao) this;
			return mov.getNmArqMov();
		}
		return null;
	}

	/**
	 * Retorna o número de páginas do documento para exibir no dossiê.
	 * 
	 */
	public int getNumeroDePaginasParaInsercaoEmDossie() {
		if (this instanceof ExMovimentacao) {
			ExMovimentacao mov = (ExMovimentacao) this;
			if (mov.getNumPaginasOri() != null)
				return mov.getNumPaginasOri();
		}
		return getNumPaginas();
	}

	public Integer getNumPaginas() {
		return numPaginas;
	}

	public abstract byte[] getPdf();
	
	public abstract boolean isPdf();

	public long getByteCount() {
		byte[] ab = getPdf();
		if (ab == null)
			return 0;
		return ab.length;
	}

	// public byte[] getPdfToHash() throws Exception {
	// byte[] pdf = getPdf();
	// if (pdf == null)
	// return null;
	// return AssinaturaDigital.getHasheableRangeFromPDF(pdf);
	// }
	//
	// public String getPdfToHashB64() throws Exception {
	// return Base64.encode(getPdfToHash());
	// }

	public String getQRCode() {
		if (isAssinadoDigitalmente()) {
			String sQRCode;
			sQRCode = SigaExProperties.getEnderecoAutenticidadeDocs() + "?n="
					+ getSiglaAssinatura();
			return sQRCode;
		}
		return null;
	}

	/**
	 * Quando o objeto for do tipo documento retorna o código compacto do
	 * documento. Quando o objeto for do tipo movimentação retorna a referência
	 * da movimentação que é o codigo compacto da movimentação mais o id da
	 * movimentação.
	 * 
	 */
	public String getReferencia() {
		if (this instanceof ExDocumento) {
			ExDocumento doc = (ExDocumento) this;
			return doc.getCodigoCompacto();
		}

		if (this instanceof ExMovimentacao) {
			ExMovimentacao mov = (ExMovimentacao) this;
			return mov.getReferencia();
		}
		return null;
	}

	/**
	 * Retorna a referência do objeto mais o extensão ".html".
	 * 
	 */
	public String getReferenciaHtml() {
		if (getHtml() == null)
			return null;
		return getReferencia() + ".html";
	}

	/**
	 * Retorna a referência do objeto mais o extensão ".html" e um outro parâmetro de queryString para indicar o arquivo completo.
	 * 
	 */
	public String getReferenciaHtmlCompleto() {
		return getReferencia() + ".html&completo=1";
	}

	/**
	 * Retorna a referência do objeto mais o extensão ".pdf".
	 * 
	 */
	public String getReferenciaPDF() {
		if (getNumPaginas() == null || getNumPaginas() == 0)
			return null;
		return getReferencia() + ".pdf";
	};
	
	public String getReferenciaPDFCompleto() {
		if (getNumPaginas() == null || getNumPaginas() == 0)
			return null;
		return getReferencia() + ".pdf&completo=1";
	};
	
	/**
	 * Retorna a referência do objeto mais o extensão ".pdf".
	 * 
	 */
	public String getReferenciaZIP() {
		return getReferencia() + EXTENSAO_ZIP;
	};

	public Map<String, String> getResumo() {
		return null;
	};

	public abstract String getSiglaAssinatura();
	
	public abstract String getSiglaAssinaturaExterna();

	/**
	 * Verifica se um arquivo foi assinado digitalmente.
	 * 
	 * @return Verdadeiro caso o arquivo tenha sido assinado digitalmente e
	 *         Falso caso o arquivo não tenha sido assinado digitalmente.
	 * 
	 */
	public boolean isAssinadoDigitalmente() {
		return (getAssinaturasDigitais() != null)
				&& (getAssinaturasDigitais().size() > 0);
	}

	public abstract boolean isCancelado();

	public abstract boolean isRascunho();

	public abstract boolean isSemEfeito();

	public abstract boolean isInternoProduzido();

	public void setNumPaginas(Integer numPaginas) {
		this.numPaginas = numPaginas;
	}
	
	public abstract boolean isCodigoParaAssinaturaExterna(String num);
	
	public abstract String getTipoDescr();

	@PostPersist
	private void postPersist() {
		this.efetuarDespejoArquivosZip();
	}

	@PostUpdate
	private void postUpdate() {
		this.efetuarDespejoArquivosZip();
	}

	@PostRemove
	private void postRemove() {
		this.removerArquivosZip();
	}

	public abstract Path getPathConteudo(Path base);

	protected Path getPathConteudo(AbstractExDocumento documento, String tipoNome, Path base) {

		String acronimoOrgao = ofNullable(this.getLotaTitular())
				.map(DpLotacao::getOrgaoUsuario)
				.map(CpOrgaoUsuario::getAcronimoOrgaoUsu)
				.map(StringUtils::stripToNull)
				.orElseThrow(() -> new IllegalArgumentException(String.format(ERRO_CAMINHO_ARQUIVO, tipoNome, this.getId(), "ÓRGAO")));

		String siglaForma = ofNullable(documento)
				.map(AbstractExDocumento::getExFormaDocumento)
				.map(ExFormaDocumento::getSiglaFormaDoc)
				.orElseThrow(() -> new IllegalArgumentException(String.format(ERRO_CAMINHO_ARQUIVO, tipoNome, this.getId(), "FORMA DOCUMENTO")));

		String ano = ofNullable(this.getData())
				.map(dataHora -> DateFormatUtils.format(dataHora, YEAR_PATTERN))
				.orElseThrow(() -> new IllegalArgumentException(String.format(ERRO_CAMINHO_ARQUIVO, tipoNome, this.getId(), "DATA/HORA")));

		Long id = ofNullable(this.getId())
				.orElseThrow(() -> new IllegalArgumentException(String.format(ERRO_CAMINHO_ARQUIVO, tipoNome, this.getId(), "ID")));

		return base.resolve(tipoNome)
				.resolve(acronimoOrgao)
				.resolve(siglaForma)
				.resolve(ano)
				.resolve(id + EXTENSAO_ZIP);
	}

	protected void efetuarDespejoArquivosZip() {
		if (this.cacheConteudo != null && !this.cacheConteudo.isEmpty()) {
			for (Entry<ZipItem, byte[]> entry : this.cacheConteudo.entrySet()) {
				ZipServico.gravarItem(this, entry.getValue(), entry.getKey());
			}
		}
	}

	protected void removerArquivosZip() {
		ZipServico.apagar(this);
	}

	private Map<ZipItem, byte[]> atualizarCache(byte[] zip) {
		Map<ZipItem, byte[]> cache = new LinkedHashMap<>();
		List<ZipItem> itens = ZipServico.listarItens(zip);
		for (ZipItem item : itens) {
			byte[] itemBytes = ZipServico.lerItem(zip, item);
			if (itemBytes != null) {
				cache.put(item, itemBytes);
			}
		}

		// Liberando cache antiga
		if (this.cacheConteudo != null) {
			this.cacheConteudo.clear();
		}
		this.cacheConteudo = cache;
		return this.cacheConteudo;
	}

	private void inicializarCacheSeNecessario() {
		if (this.cacheConteudo == null) {
			this.cacheConteudo = new LinkedHashMap<>();
		}
	}

	public byte[] getConteudoBlobInicializarOuAtualizarCache() {
		this.inicializarCacheSeNecessario();
		if (this.getId() == null) {
			return null;
		}

		byte[] zip = ZipServico.ler(this);
		if (zip == null) {
			return null;
		}

		this.atualizarCache(zip);
		return zip;
	}

	public byte[] getConteudoBlob(final ZipItem zipItem) {
		if (this.cacheConteudo == null) {
			this.getConteudoBlobInicializarOuAtualizarCache();
		}
		// Retornando item a partir da cache
		return this.cacheConteudo.get(zipItem);
	}

	public void setConteudoBlob(final ZipItem zipItem, final byte[] conteudo) {
		if (zipItem != null && conteudo != null) {
			this.setMimeType(ZIP_MIME_TYPE);
			if (this.cacheConteudo == null) {
				this.getConteudoBlobInicializarOuAtualizarCache();
			}
			this.cacheConteudo.put(zipItem, conteudo);
		}
	}

	public void clonarConteudo(ExArquivo origem) {
		origem.getConteudoBlobInicializarOuAtualizarCache();
		this.inicializarCacheSeNecessario();
		this.cacheConteudo.putAll(origem.getCacheConteudo());
	}

	protected Map<ZipItem, byte[]> getCacheConteudo() {
		return cacheConteudo;
	}

	protected void setCacheConteudo(Map<ZipItem, byte[]> cacheConteudo) {
		this.cacheConteudo = cacheConteudo;
	}

}