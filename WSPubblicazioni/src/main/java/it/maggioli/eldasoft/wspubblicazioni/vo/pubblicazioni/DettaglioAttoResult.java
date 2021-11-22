/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Risultato Dettaglio atto
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
		"error",
		"atto"
})
@ApiModel(description="Contenitore per il risultato del dettaglio di un atto")
public class DettaglioAttoResult implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	public static final String ERROR_NOT_FOUND = "Identificativo atto non trovato nella banca dati";
	public static final String ERROR_UNAUTHORIZED = "Non si possiedono le credenziali per questo atto";
	public static final String ERROR_UNEXPECTED = "Errore inaspettato";

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Eventuale messaggio di errore in caso di operazione fallita")
	private String error;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="Dettaglio dell'atto")
	private PubblicaAttoEntry atto;

	public void setError(String error) {
		this.error = StringUtils.stripToNull(error);
	}

	public String getError() {
		return error;
	}

	public void setAtto(PubblicaAttoEntry atto) {
		this.atto = atto;
	}

	public PubblicaAttoEntry getAtto() {
		return atto;
	}

}
