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

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.maggioli.eldasoft.wspubblicazioni.bl.AttiManager;
import it.maggioli.eldasoft.wspubblicazioni.vo.PubblicazioneResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.TokenValidationResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.ValidateEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.DettaglioGaraResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.PubblicaGaraEntry;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
 * Servizio REST esposto al path "/BO/Anagrafiche".
 */
@Path("/Anagrafiche")
@Component
@Api(value="/Anagrafiche")
public class AnagraficheRestService extends BaseRestService{
  
	/** Logger di classe. */
	protected Logger logger = LoggerFactory
			.getLogger(AnagraficheRestService.class);
	
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
	@Path("/GaraLotti")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "AnagraficheRestService.garaLotti", value = "Salva l'anagrafica della gara e dei lotti", notes = "Ritorna il risultato del salvataggio e l'id assegnato dal sistema, che dovrà essere riutilizzato per successivi aggiornamenti", response = PubblicazioneResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
	public Response garaLotti(
			@ApiParam(value = "Gara da salvare [Model=PubblicaGaraEntry]", required = true) PubblicaGaraEntry gara,
			@ApiParam(value = "Se valorizzato a '1' effettua solo il controllo dei dati, '2' effettua controllo e salvataggio", required = true) @QueryParam("modalitaInvio") String modalitaInvio,
			@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
			throws ParseException, IOException {

	  	PubblicazioneResult risultato;

	  	// TOKEN VALIDATION
	  	TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new PubblicazioneResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		modalitaInvio = StringUtils.stripToNull(modalitaInvio);
		// FASE PRELIMINARE DI CONTROLLO DEI PARAMETRI PRIMA DI INOLTRARE ALLA
		// BUSINESS LOGIC
		if (logger.isDebugEnabled()) {
			logger.debug("garaLotti(" + gara.getIdAnac()
					+ "," + gara.getCodiceFiscaleSA() + "," + token
					+ "): inizio metodo");
		}

		List<ValidateEntry> controlli = new ArrayList<ValidateEntry>();
		gara.setClientId(tokenValidate.getClientId());
		gara.setSyscon(tokenValidate.getSyscon());
		try{
			this.validateManager.validatePubblicaGara(gara, controlli);
		}
		catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante la validazione della gara",
					ex);
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
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
			risultato = new PubblicazioneResult();
			risultato.setError(PubblicazioneResult.ERROR_VALIDATION);
			risultato.setValidate(controlli);
		} else {
			if (modalitaInvio != null && (modalitaInvio.equals("2") || modalitaInvio.equals("3"))) {
				// procedo con l'inserimento
				try {
					risultato = this.attiManager.garaLotti(gara, modalitaInvio);
					try {
						//aggiorna numero lotti in w9gara
						this.attiManager.AggiornaNumeroLotti(gara.getId());
					} catch (Exception ex) {
						logger.error("Errore durante l'aggiornamento del numero dei lotti",
								ex);
					}
				} catch (Exception ex) {
					logger.error(
							"Errore inaspettato durante il salvataggio dell'anagrafica gara e lotti",
							ex);
					risultato = new PubblicazioneResult();
					risultato.setError(PubblicazioneResult.ERROR_UNEXPECTED
							+ ": " + ex.getMessage());
				}
			} else {
				risultato = new PubblicazioneResult();
				risultato.setValidate(controlli);
			}
		}

		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (PubblicazioneResult.ERROR_VALIDATION.equals(risultato
					.getError())) {
				status = HttpStatus.BAD_REQUEST;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}

		logger.debug("garaLotti: fine metodo [http status " + status.value()
				+ "]");
		return Response.status(status.value()).entity(risultato).build();
	}
  
  /**
	 * Estrae il dettaglio di una gara mediante chiamata GET e risposta JSON.
	 *
	 * @param idRicevuto
	 *        identificativo SCP della gara (id_generato/id_ricevuto)
	 * @param token
	 *        token
	 * @return JSON contenente la classe PubblicaGaraEntry
	 */
	
	@GET
	@Path("/DettaglioGara")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "AnagraficheRestService.dettaglioGara", value = "Estrae il dettaglio di una gara in base all'id passato come parametro", response = DettaglioGaraResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 401, message = "Non si possiedono le credenziali per questa gara (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 404, message = "La gara richiesta non è stata trovata (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response dettaglioGara(
					@ApiParam(value = "Identificativo univoco SCP - IdRicevuto", required = true) @QueryParam("idRicevuto") Long idRicevuto, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		DettaglioGaraResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new DettaglioGaraResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("dettaglioGara(" + idRicevuto + "): inizio metodo");
		}
		try {
			String error = this.validateManager.verificaIdRicevutoGara(idRicevuto);

			if (error.equals("")) {
				risultato = this.attiManager.getDettaglioGara(idRicevuto);
			} else {
				risultato = new DettaglioGaraResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante il recupero del dettaglio della gara",
					ex);
			risultato = new DettaglioGaraResult();
			risultato.setError(DettaglioGaraResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (DettaglioGaraResult.ERROR_NOT_FOUND.equals(risultato.getError())) {
				status = HttpStatus.NOT_FOUND;
			} else if (DettaglioGaraResult.ERROR_UNAUTHORIZED.equals(risultato.getError())) {
				status = HttpStatus.UNAUTHORIZED;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("dettaglioGara: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}  
}
