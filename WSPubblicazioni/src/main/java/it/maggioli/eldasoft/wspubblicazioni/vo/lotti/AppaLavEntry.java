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
package it.maggioli.eldasoft.wspubblicazioni.vo.lotti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Tipologia del lavoro.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"tipologiaLavoro"
})
@ApiModel(description="*Tipologia del lavoro")
public class AppaLavEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@ApiModelProperty(value="Codice della gara", hidden = true)
	private Long idGara;
	@ApiModelProperty(value="Codice del lotto", hidden = true)
	private Long idLotto;
	@ApiModelProperty(value="Numero progressivo tipologia", hidden = true)
	private Long numAppaLav;
	@ApiModelProperty(value="Tipologia del lavoro (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaLavoro)", required = true)  
	private Long tipologiaLavoro;
	
	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}
	@XmlTransient
	public Long getIdGara() {
		return idGara;
	}
	public void setIdLotto(Long idLotto) {
		this.idLotto = idLotto;
	}
	@XmlTransient
	public Long getIdLotto() {
		return idLotto;
	}
	public void setNumAppaLav(Long numAppaLav) {
		this.numAppaLav = numAppaLav;
	}
	@XmlTransient
	public Long getNumAppaLav() {
		return numAppaLav;
	}
	public void setTipologiaLavoro(Long tipologiaLavoro) {
		this.tipologiaLavoro = tipologiaLavoro;
	}
	public Long getTipologiaLavoro() {
		return tipologiaLavoro;
	}
	
}
