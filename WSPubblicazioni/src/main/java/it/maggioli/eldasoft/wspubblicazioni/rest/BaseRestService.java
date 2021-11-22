package it.maggioli.eldasoft.wspubblicazioni.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import it.maggioli.eldasoft.wspubblicazioni.bl.ValidateManager;
import it.maggioli.eldasoft.wspubblicazioni.vo.TokenValidationResult;

public class BaseRestService {
	
	/**
	 * Wrapper della business logic a cui viene demandata la gestione
	 */
	protected ValidateManager validateManager;
	
	/**
	 * @param validateManager
	 *            validateManager da settare internamente alla classe.
	 */
	@Required
	@Autowired
	public void setValidateManager(ValidateManager validateManager) {
		this.validateManager = validateManager;
	}
	
	//private static final String jwtKey = "14468e2f-8f1b-4938-acca-50ea2ead876f";
	
	public TokenValidationResult validateToken(String token){
		TokenValidationResult result = new TokenValidationResult();
		try {
			String jwtKey = "";
			jwtKey = this.validateManager.getJWTKey();
			Jws<Claims> claims = Jwts.parser().setSigningKey(jwtKey).parseClaimsJws(token);  
			String clientId = claims.getBody().get("aud").toString();
			Long syscon = new Long(claims.getBody().get("syscon").toString());
			result.setClientId(clientId);
			result.setSyscon(syscon);
			result.setValidated(true);
			return result;
		} catch (Exception e) {
			result.setValidated(false);
			result.setError("Il token non è valido.");
			return result;
		}
	}
}
