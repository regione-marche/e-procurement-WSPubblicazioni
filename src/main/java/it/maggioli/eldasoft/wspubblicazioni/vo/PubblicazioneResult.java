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
package it.maggioli.eldasoft.wspubblicazioni.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wspubblicazioni.vo.InserimentoResult;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Risultato pubblicazione.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { "id", "error", "validate" })
@ApiModel(description = "Risultato della pubblicazione")
public class PubblicazioneResult extends InserimentoResult implements
		Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(value = "Id univoco generato dal sistema, deve essere utilizzato per le chiamate successive")
	private Long id;


	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

}
