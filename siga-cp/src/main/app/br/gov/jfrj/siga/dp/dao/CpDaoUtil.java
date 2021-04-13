package br.gov.jfrj.siga.dp.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;

import br.gov.jfrj.siga.model.dao.HibernateUtil;

@Deprecated
public class CpDaoUtil {

	@Deprecated
	public static Blob createBlob(String conteudo) {
		try {
			return HibernateUtil.getSessao().getLobHelper().createBlob(conteudo.getBytes("ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

}
