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
package it.maggioli.eldasoft.wspubblicazioni.vo.centriCosto;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

/**
 * Dati generali di un centro di costo.
 *
 * @author Mirco.Franzoni
 */

public class DatiGeneraliCentroCostoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	private Long id;

	private String stazioneAppaltante;

	private String codice;
	
	private String denominazione;
	
	private String note;
	
	private Long tipologia;

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setStazioneAppaltante(String stazioneAppaltante) {
		this.stazioneAppaltante = StringUtils.stripToNull(stazioneAppaltante);
	}

	public String getStazioneAppaltante() {
		return stazioneAppaltante;
	}

	public void setCodice(String codice) {
		this.codice = StringUtils.stripToNull(codice);
	}

	public String getCodice() {
		return codice;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = StringUtils.stripToNull(denominazione);
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setNote(String note) {
		this.note = StringUtils.stripToNull(note);
	}

	public String getNote() {
		return note;
	}

	public void setTipologia(Long tipologia) {
		this.tipologia = tipologia;
	}

	public Long getTipologia() {
		return tipologia;
	}
	 
}
