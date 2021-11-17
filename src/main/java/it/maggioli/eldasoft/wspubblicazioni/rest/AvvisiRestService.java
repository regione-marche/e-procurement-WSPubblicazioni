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
import it.maggioli.eldasoft.wspubblicazioni.bl.AvvisiManager;
import it.maggioli.eldasoft.wspubblicazioni.vo.PubblicazioneResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.TokenValidationResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.ValidateEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.DettaglioAvvisoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.PubblicaAvvisoEntry;

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
 * Servizio REST esposto al path "/BO/Avvisi".
 */
@Path("/Avvisi")
@Component
@Api(value = "/Avvisi")
public class AvvisiRestService extends BaseRestService{

	/** Logger di classe. */
	protected Logger logger = LoggerFactory.getLogger(AvvisiRestService.class);

	/**
	 * Wrapper della business logic a cui viene demandata la gestione
	 */
	private AvvisiManager avvisiManager;

	/**
	 * @param avvisiManager
	 *            avvisiManager da settare internamente alla classe.
	 */
	@Required
	@Autowired
	public void setAvvisiManager(AvvisiManager avvisiManager) {
		this.avvisiManager = avvisiManager;
	}
	
	@POST
	@Path("/Pubblica")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "AvvisiRestService.pubblica", value = "Pubblica i dati e i documenti relativi ad un'avviso", notes = "Ritorna il risultato della pubblicazione e l'id assegnato dal sistema, che dovrà essere riutilizzato per successive pubblicazioni", response = PubblicazioneResult.class)
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 400, message = "Controlli falliti sui parametri in input (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
	public Response pubblica(
			@ApiParam(value = "Avviso da pubblicare [Model=PubblicaAvvisoEntry]", required = true) PubblicaAvvisoEntry avviso,
			@ApiParam(value = "Se valorizzato a '1' effettua solo il controllo dei dati, '2' effettua controllo e pubblicazione", required = true) @QueryParam("modalitaInvio") String modalitaInvio,
			@ApiParam(value = "Token", required = true) @QueryParam("token") String token
			)
			throws ParseException, IOException {

		PubblicazioneResult risultato;
		
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
			logger.debug("pubblica(" + avviso.getCodiceSA() 
					+ "," + token
					+ "): inizio metodo");
		}

		List<ValidateEntry> controlli = new ArrayList<ValidateEntry>();
		avviso.setClientId(tokenValidate.getClientId());
		avviso.setSyscon(tokenValidate.getSyscon());
		try{
			this.validateManager.validatePubblicaAvviso(avviso, controlli);
		}
		catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante la validazione dell'avviso",
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
					risultato = this.avvisiManager.pubblica(avviso, modalitaInvio);
				} catch (Exception ex) {
					logger.error(
							"Errore inaspettato durante la pubblicazione dell'avviso",
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

		logger.debug("pubblica: fine metodo [http status " + status.value()
				+ "]");
		return Response.status(status.value()).entity(risultato).build();
	}

	/**
	 * Estrae il dettaglio di un avviso mediante chiamata GET e risposta JSON.
	 *
	 * @param idRicevuto
	 *        identificativo SCP dell'avviso (id_generato/id_ricevuto)
	 * @param token
	 *        token
	 * @return JSON contenente la classe PubblicaAvvisoEntry
	 */
	
	@GET
	@Path("/DettaglioAvviso")
	@Produces(MediaType.APPLICATION_JSON)
	@ApiOperation(nickname = "AvvisiRestService.dettaglioAvviso", value = "Estrae il dettaglio di un avviso in base all'id passato come parametro", response = DettaglioAvvisoResult.class)
	@ApiResponses(value = {@ApiResponse(code = 200, message = "Operazione effettuata con successo"),
			@ApiResponse(code = 401, message = "Non si possiedono le credenziali per questo avviso (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 404, message = "L'avviso richiesto non è stata trovato (si veda l'attributo error della risposta)"),
			@ApiResponse(code = 500, message = "Errori durante l'esecuzione dell'operazione (si veda l'attributo error della risposta)") })
			public Response dettaglioAvviso(
					@ApiParam(value = "Identificativo univoco SCP - IdRicevuto", required = true) @QueryParam("idRicevuto") Long idRicevuto, 
					@ApiParam(value = "Token", required = true) @QueryParam("token") String token) {

		DettaglioAvvisoResult risultato;

		TokenValidationResult tokenValidate = validateToken(token);
		if(!tokenValidate.isValidated()){
			risultato = new DettaglioAvvisoResult();
			risultato.setError(tokenValidate.getError());
			return Response.status(HttpStatus.UNAUTHORIZED.value()).entity(risultato).build();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("dettaglioAvviso(" + idRicevuto + "): inizio metodo");
		}
		try {
			String error = this.validateManager.verificaIdRicevutoAvviso(idRicevuto);

			if (error.equals("")) {
				risultato = this.avvisiManager.getDettaglioAvviso(idRicevuto);
			} else {
				risultato = new DettaglioAvvisoResult();
				risultato.setError(error);
			}
		} catch (Exception ex) {
			logger.error(
					"Errore inaspettato durante il recupero del dettaglio dell'avviso",
					ex);
			risultato = new DettaglioAvvisoResult();
			risultato.setError(DettaglioAvvisoResult.ERROR_UNEXPECTED
					+ ": " + ex.getMessage());
		}
		HttpStatus status = HttpStatus.OK;
		if (risultato.getError() != null) {
			if (DettaglioAvvisoResult.ERROR_NOT_FOUND.equals(risultato.getError())) {
				status = HttpStatus.NOT_FOUND;
			} else if (DettaglioAvvisoResult.ERROR_UNAUTHORIZED.equals(risultato.getError())) {
				status = HttpStatus.UNAUTHORIZED;
			} else {
				status = HttpStatus.INTERNAL_SERVER_ERROR;
			}
		}
		logger.debug("dettaglioAvviso: fine metodo [http status " + status.value() + "]");
		return Response.status(status.value()).entity(risultato).build();
	}  
}
