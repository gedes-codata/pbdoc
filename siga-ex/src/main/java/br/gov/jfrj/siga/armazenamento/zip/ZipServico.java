package br.gov.jfrj.siga.armazenamento.zip;

import static br.gov.jfrj.siga.ex.util.Compactador.adicionarStream;
import static br.gov.jfrj.siga.ex.util.Compactador.compactarStream;
import static br.gov.jfrj.siga.ex.util.Compactador.descompactarStream;
import static br.gov.jfrj.siga.ex.util.Compactador.listarStream;
import static br.gov.jfrj.siga.ex.util.Compactador.removerStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.validation.constraints.NotNull;

import org.apache.commons.io.FileUtils;
import org.jboss.logging.Logger;

import br.gov.jfrj.siga.base.AplicacaoException;
import br.gov.jfrj.siga.ex.ExArquivo;

public abstract class ZipServico {

	private static final Logger log = Logger.getLogger(ZipServico.class);
	private static final int CODIGO_ERRO_ZIP_SERVICO = 1050;

	private static File referenciaArquivo(ExArquivo exArquivo) {
		try {
			Path caminhoBase = ZipPropriedades.getInstance().obterCaminhoBase();
			File arquivo = exArquivo.getPathConteudo(caminhoBase).toFile();
			FileUtils.forceMkdir(arquivo.getParentFile());
			log.debugf("Capturando referência para arquivo #%d %s", exArquivo.getId(), arquivo.getAbsolutePath());
			return arquivo;
		} catch (Exception e) {
			throw new AplicacaoException("Não foi possível fazer referência ao arquivo do documento " + exArquivo.getId() + " no sistema de arquivos", CODIGO_ERRO_ZIP_SERVICO, e);
		}
	}

	public static List<String> nomesItens(@NotNull ExArquivo exArquivo) {
		byte[] zipBytes = ler(exArquivo);
		return nomesItens(zipBytes);
	}

	public static List<String> nomesItens(@NotNull byte[] zipBytes) {
		List<String> nomesItens = new ArrayList<>();
		try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
			ZipEntry zipEntry = zipInputStream.getNextEntry();
			while (zipEntry != null) {
				nomesItens.add(zipEntry.getName());
				zipEntry = zipInputStream.getNextEntry();
			}
			return nomesItens;
		} catch (IOException e) {
			throw new AplicacaoException("Não foi possível capturar nomes dos itens ZIP a partir dos bytes repassados", CODIGO_ERRO_ZIP_SERVICO, e);
		}
	}

	public static List<ZipItem> listarItens(@NotNull byte[] zipBytes) {
		List<String> nomes = nomesItens(zipBytes);
		List<ZipItem> itens = new ArrayList<>(nomes.size());
		for (String nome : nomes) {
			itens.add(ZipItem.Tipo.porNomeItem(nome));
		}
		return itens;
	}

	public static byte[] lerItem(@NotNull ExArquivo exArquivo, @NotNull ZipItem item) {
		byte[] zipBytes = ler(exArquivo);
		return lerItem(zipBytes, item);
	}

	public static byte[] lerItem(@NotNull byte[] zipBytes, @NotNull ZipItem item) {
		return descompactarStream(zipBytes, item);
	}

	public static byte[] ler(@NotNull ExArquivo exArquivo) {
		try {
			File zip = referenciaArquivo(exArquivo);
			if (!zip.exists()) {
				return null;
			}
			return FileUtils.readFileToByteArray(zip);
		} catch (Exception e) {
			throw new AplicacaoException("Não foi possível ler os dados do documento " + exArquivo.getId() + " no sistema de arquivos", CODIGO_ERRO_ZIP_SERVICO, e);
		}
	}

	public static void gravarItem(@NotNull ExArquivo exArquivo, byte[] itemBytes, @NotNull ZipItem item) {
		// Capturando dados do ZIP original (se existir) e atualizando seus itens
		byte[] originalZipBytes = ler(exArquivo);
		byte[] novoZipBytes = criarOuAtualizarZipComItem(originalZipBytes, itemBytes, item);
		gravar(exArquivo, novoZipBytes);
	}

	public static void gravar(@NotNull ExArquivo exArquivo, @NotNull byte[] zipBytes) {
		File zipFile = referenciaArquivo(exArquivo);
		try {
			if (zipBytes == null) {
				apagarArquivo(zipFile);
			} else {
				log.debugf("Despejando arquivo %s no filesystem", zipFile.getAbsolutePath());
				FileUtils.writeByteArrayToFile(zipFile, zipBytes, false);
			}
		} catch (Exception e) {
			throw new AplicacaoException("Não foi possível gravar arquivo do documento " + exArquivo.getId() + " no sistema de arquivos", CODIGO_ERRO_ZIP_SERVICO, e);
		}
	}

	public static void apagar(@NotNull ExArquivo exArquivo) {
		File zip = referenciaArquivo(exArquivo);
		apagarArquivo(zip);
	}

	private static void apagarArquivo(File referencia) {
		try {
			if (referencia.exists()) {
				FileUtils.forceDelete(referencia);
			}
		} catch (Exception e) {
			throw new AplicacaoException("Não foi possível apagar os dados do arquivo " + referencia.getAbsolutePath() + " no sistema de arquivos", CODIGO_ERRO_ZIP_SERVICO, e);
		}
	}

	/**
	 * Cria ou altera um arquivo zip a partir dos dados a serem inseridos. Caso o
	 * parâmetro zipOriginal seja nulo, gera os bytes do novo arquivo ZIP. Se
	 * existir, atualiza seu conteúdo a partir do item enviado.
	 * 
	 * @param originalZipBytes
	 * @param zipItemBytes
	 * @param tipo
	 * @return
	 */
	private static byte[] criarOuAtualizarZipComItem(
			byte[] originalZipBytes,
			byte[] zipItemBytes,
			@NotNull ZipItem tipo) {

		String nome = tipo.getNome();
		byte[] novoZipBytes = null;
		if (originalZipBytes == null || listarStream(originalZipBytes) == null) {
			if (zipItemBytes != null) {
				novoZipBytes = compactarStream(nome, zipItemBytes);
			} else {
				novoZipBytes = null;
			}
		} else {
			if (zipItemBytes != null) {
				novoZipBytes = adicionarStream(nome, zipItemBytes, originalZipBytes);
			} else {
				novoZipBytes = removerStream(nome, originalZipBytes);
			}
		}
		return novoZipBytes;
	}

}
