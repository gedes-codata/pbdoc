package br.gov.jfrj.siga.uteis;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

import java.io.IOException;
import java.io.UncheckedIOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import br.gov.jfrj.siga.base.AplicacaoException;

public class ExceptionUtils {

	private static final Logger log = Logger.getLogger(ExceptionUtils.class);
	private static final String EXCEPTION_MESSAGE_KEY = "exceptionMessage";
	private static final String EXCEPTION_GERAL_KEY = "exceptionGeral";
	private static final String EXCEPTION_STACK_GERAL = "exceptionStackGeral";
	private static final String ERRO_DESCONHECIDO = "Erro Desconhecido";

	private ExceptionUtils() {
		throw new UnsupportedOperationException("Do not instantiate utility classes.");
	}

	public static void tratarExcecao(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
		request.setAttribute(EXCEPTION_MESSAGE_KEY, throwable.getMessage());
		request.setAttribute(EXCEPTION_GERAL_KEY, throwable);
		request.setAttribute(EXCEPTION_STACK_GERAL, org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(throwable));

		int httpResponseCode = SC_INTERNAL_SERVER_ERROR;
		if (throwable instanceof AplicacaoException) {
			httpResponseCode = SC_BAD_REQUEST;
		} else {
			log.error(ERRO_DESCONHECIDO, throwable);
		}

		try {
			response.sendError(httpResponseCode, throwable.getMessage());
		} catch (IOException ioe) {
			throw new UncheckedIOException(ioe);
		}
	}

}
