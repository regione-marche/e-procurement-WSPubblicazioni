/*
 * Created on 30/mag/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wspubblicazioni.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.maggioli.eldasoft.wspubblicazioni.bl.AttiManager;
import it.maggioli.eldasoft.wspubblicazioni.vo.PubblicazioneAttoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.TokenValidationResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.ValidateEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.DettaglioAttoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.PubblicaAttoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.TipoAttoResult;
//import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.TipoAttoResult;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
//import javax.ws.rs.GET;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;


/**
 * Servizio REST esposto al path "/BO/Pubblicazioni".
 */
@Path("/Atti")
@Component
@Api(value="/Atti")
public class AttiRestService extends BaseRestService{
  
	/** Logger di classe. */
	protected Logger logger = LoggerFactory
			.getLogger(AttiRestService.class);
	
	@Context ServletContext context;
  /**
   * Wrapper della business logic a cui viene demandata la gestione
   */
  private AttiManager attiManager;

  /**
   * @param attiManager attiManager da settare internamente alla classe.
   */
  @Required
  @Autowired
  public void setAttiManager(AttiManager attiManager) {
    this.attiManager = attiManager;
  }
  
  @POST
	@Path("/Pubblica")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "AttiRestService.pubblica", value = "Pubblica i dati e i documenti relativi ad Atti Ex Art.29", notes = "Ritorna il risultato della pubblicazione e l'id assegnato dal sistema, che dovrà essere riutilizzato per successive pubblicazioni", response = PubblicazioneAttoResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
	public Response pubblica(
			@ApiParam(value = "Atti Ex Art.29 da pubblicare [Model=PubblicaAttoEntry]", required = true) PubblicaAttoEntry atto,
			@ApiParam(value = "Se valorizzato a '1' effettua solo il controllo dei dati, '2' effettua controllo e pubblicazione", required = true) @QueryParam("modalitaInvio") String modalitaInvio,
			@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
			throws ParseException, IOException {

	  	PubblicazioneAttoResult risultato;

	  	// TOKEN VALIDATION
	  	TokenValidationResult tokenValidate = validateToken(token);
	  	if(!tokenValidate.isValidated()){
			risultato = new PubblicazioneAttoResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		modalitaInvio = StringUtils.stripToNull(modalitaInvio);
		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA
		// BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("pubblica(" + atto.getIdGara() 
					+ "," + atto.getNumeroPubblicazione() + "," + token
					+ "): inizio metodo");
		}

		List<ValidateEntry> controlli = new ArrayList<ValidateEntry>();
		atto.setClientId(tokenValidate.getClientId());
		if (atto.getGara() != null) {
			atto.getGara().setSyscon(tokenValidate.getSyscon());
		}
		try{
			this.validateManager.validatePubblicaAtto(atto, controlli);
		}
		catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante la validazione dell'atto",
					ex);
			risultato = new PubblicazioneAttoResult();
			risultato.setError(PubblicazioneAttoResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).entity(risultato).build();
		}
		//verifico se ci sono errori di validazione bloccante
		boolean validazioneNonSuperata = false; 
		for(ValidateEntry item:controlli){
			if ("E".equals(item.getTipo())){
				validazioneNonSuperata = true;
				break;
			}
		}
		if (validazioneNonSuperata) {
			// se non supero la validazione
			risultato = new PubblicazioneAttoResult();
			risultato.setError(PubblicazioneAttoResult.ERROR_VALIDATION);
			risultato.setValidate(controlli);
		} else {
			if (modalitaInvio != null && (modalitaInvio.equals("2") || modalitaInvio.equals("3"))) {
				// procedo con l'inserimento
				try {
					risultato = this.attiManager.pubblica(atto, modalitaInvio);
					try {
						//aggiorna numero lotti in w9gara
						this.attiManager.AggiornaNumeroLotti(atto.getIdGara());
					} catch (Exception ex) {
						logger.error("Errore durante l'aggiornamento del numero dei lotti",
								ex);
					}
				} catch (Exception ex) {
					logger.error(
							"Errore inaspettato durante la pubblicazione della pubblicazione",
							ex);
					risultato = new PubblicazioneAttoResult();
					risultato.setError(PubblicazioneAttoResult.ERROR_UNEXPECTED
							+ ": " + ex.getMessage());
				}
			} else {
				risultato = new PubblicazioneAttoResult();
				risultato.setValidate(controlli);
			}
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (PubblicazioneAttoResult.ERROR_VALIDATION.equals(risultato
					.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("pubblica: fine metodo [http status " + status.value()
				+ "]");
		return Response.status(status.value()).entity(risultato).build();
	}
 
  
  @GET
  @Path("/Tipologie")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
  @ApiResponse(code = 404, message = "Il dettaglio richiesto non è stato trovato (si veda l'attributo error della risposta)"),
  @ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
  @ApiOperation(nickname = "AttiRestService.tipologie", value = "Metodo di servizio per recuperare i campi obbligatori per il Tipo di Atto", response = TipoAttoResult.class)
  public Response tipologie(
		  @ApiParam(value = "Token", required = true) @QueryParam("token") String token) {
    if (logger.isDebugEnabled()) {
      logger.debug("tipologie(" + token + "): inizio metodo");
    }
    TipoAttoResult risultato;
  	// TOKEN VALIDATION
  	TokenValidationResult tokenValidate = validateToken(token);
	if(!tokenValidate.isValidated()){
		risultato = new TipoAttoResult();
		risultato.setError(tokenValidate.getError());
		return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
	}
    
    risultato = this.attiManager.getTipoAtti();
    HttpStatus status = HttpStatus.OK;
    if (risultato.getError() != null) {
      if (TipoAttoResult.ERROR_NOT_FOUND.equals(risultato.getError())) {
        status = HttpStatus.NOT_FOUND;
      } else {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
      }
    }
    logger.debug("tipologie: fine metodo [http status " + status.value() + "]");
    return Response.status(status.value()).entity(risultato).build();
  }
  
  /**
	 * Estrae il dettaglio di un atto mediante chiamata GET e risposta JSON.
	 *
	 * @param idRicevuto
	 *        identificativo SCP dell'atto (id_generato/id_ricevuto)
	 * @param token
	 *        token
	 * @return JSON contenente la classe DettaglioAttoResult
	 */
	
	@GET
	@Path("/DettaglioAtto")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "AttiRestService.dettaglioAtto", value = "Estrae il dettaglio di un atto in base all'id passato come parametro", response = DettaglioAttoResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 401, message = "Non si possiedono le credenziali per questo atto (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 404, message = "L'atto richiesto non è stato trovato (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response dettaglioAtto(
					@ApiParam(value = "Identificativo univoco SCP - IdRicevuto", required = true) @QueryParam("idRicevuto") Long idRicevuto, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		DettaglioAttoResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new DettaglioAttoResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("dettaglioAtto(" + idRicevuto + "): inizio metodo");
		}
		try {
			String error = this.validateManager.verificaIdRicevutoAtti(idRicevuto);

			if (error.equals("")) {
				risultato = this.attiManager.getDettaglioAtto(idRicevuto);
			} else {
				risultato = new DettaglioAttoResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante il recupero del dettaglio dell'atto",
					ex);
			risultato = new DettaglioAttoResult();
			risultato.setError(DettaglioAttoResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (DettaglioAttoResult.ERROR_NOT_FOUND.equals(risultato.getError())) {
				status = HttpStatus.NOT_FOUND;
			} else if (DettaglioAttoResult.ERROR_UNAUTHORIZED.equals(risultato.getError())) {
				status = HttpStatus.UNAUTHORIZED;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("dettaglioAtto: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}  
	
	@GET
	  @Path("/Version")
	  @ApiOperation(nickname = "AttiRestService.version", value = "Metodo che ritorna la versione del servizio", response = String.class)
	  public Response version() {
	    if (logger.isDebugEnabled()) {
	      logger.debug("version(): inizio metodo");
	    }
	    String risultato = "NON DISPONIBILE";
	    HttpStatus status = HttpStatus.OK;
	    try {
	    	String pathFileVersione = "/WEB-INF/WSPubblicazioni_VER.TXT";
	        InputStream inputStreamVersione = context.getResourceAsStream(pathFileVersione);
	        if (inputStreamVersione != null) {
	          BufferedReader br = new BufferedReader(new InputStreamReader(inputStreamVersione));
	          try {
	        	  risultato = br.readLine();
	          } finally {
	            br.close();
	            inputStreamVersione.close();
	          }
	        }
	    } catch(Exception ex) {
	    	risultato = "NON DISPONIBILE";
	    }
	    logger.debug("version: fine metodo [http status " + status.value() + "]");
	    return Response.status(status.value()).entity(risultato).build();
	  }
	
}
