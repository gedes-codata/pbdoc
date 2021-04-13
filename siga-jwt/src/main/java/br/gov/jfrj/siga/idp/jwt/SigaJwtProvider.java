package br.gov.jfrj.siga.idp.jwt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

/**
 * Provedor de tokens JWT Propriedades idp.jwt.token.ttl - Tempo de vida padrão
 * do token idp.jwt.modulo.pwd.[nome-do-modulo] - senha do módulo para o qual
 * será gerado o token. Todos as aplicações que utilizarem o token precisarão
 * saber a senha para validar o JWT
 * 
 * @author kpf
 *
 */
public class SigaJwtProvider {

	public static final String PROVIDER_ISSUER = "sigaidp";
	public static final int DEFAULT_TTL_TOKEN = 3600; // default 1 hora
	public static final int DEFAULT_TTL_TOKEN_RENEW = (DEFAULT_TTL_TOKEN * 50) / 100;
	public static final String SIGA_JWT_AUDIENCE = System.getProperty("idp.jwt.modulo.cookie.domain");

	private long defaultTTLToken = DEFAULT_TTL_TOKEN;
	private SigaJwtOptions options;

	private SigaJwtProvider(SigaJwtOptions options) throws SigaJwtProviderException {
		try {
			this.options = options;
			defaultTTLToken = options.getTtlToken();
		} catch (Exception e) {
			throw new SigaJwtProviderException("Problema ao definir o algoritimo", e);
		}
	}

	public static SigaJwtProvider getInstance(SigaJwtOptions options) throws SigaJwtProviderException {
		return new SigaJwtProvider(options);
	}

	public String criarToken(String subject, Long cpf, String config, Map<String, Object> claimsMap, Integer ttl) {
		final JWTSigner signer = new JWTSigner(options.getPassword());
		final HashMap<String, Object> claims = new HashMap<>();

		setTimes(claims, ttl);

		claims.put("sub", subject);
		claims.put("cpf", cpf);
		claims.put("iss", PROVIDER_ISSUER);
		claims.put("mod", options.getModulo());

		if ("GOVSP".equals(System.getProperty("siga.local")) && SIGA_JWT_AUDIENCE != null) {
			claims.put("aud", SIGA_JWT_AUDIENCE);
		}

		if (claimsMap != null) {
			for (String claimName : claimsMap.keySet()) {
				claims.put(claimName, claimsMap.get(claimName));
			}
		}

		if (config != null) {
			claims.put("cfg", config);
		}

		return signer.sign(claims);
	}

	public String renovarToken(String token, Integer ttl)
			throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, IOException, JWTVerifyException {

		final JWTSigner signer = new JWTSigner(options.getPassword());
		Map<String, Object> claims = validarToken(token);
		setTimes(claims, ttl);
		return signer.sign(claims);
	}

	public void setTimes(final Map<String, Object> claims, final Integer ttl) {
		final long iat = System.currentTimeMillis() / 1000L; // issued at claim
		final long exp = iat + (ttl == null ? defaultTTLToken : ttl);
		claims.put("iat", iat);
		claims.put("nbf", iat);
		claims.put("exp", exp);
	}

	public Map<String, Object> validarToken(String token) throws InvalidKeyException, NoSuchAlgorithmException,
			IllegalStateException, SignatureException, IOException, JWTVerifyException {

		final JWTVerifier verifier;
		if ("GOVSP".equals(System.getProperty("siga.local")) && SIGA_JWT_AUDIENCE != null) {
			verifier = new JWTVerifier(options.getPassword(), SIGA_JWT_AUDIENCE);
		} else {
			verifier = new JWTVerifier(options.getPassword());
		}

		return verifier.verify(token);
	}

}
