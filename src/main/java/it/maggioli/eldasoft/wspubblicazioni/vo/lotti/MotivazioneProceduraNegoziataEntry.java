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
 * Motivazione ricorso a procedura negoziata.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"condizione"
})
@ApiModel(description="*Motivazione ricorso a procedura negoziata")
public class MotivazioneProceduraNegoziataEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@ApiModelProperty(value="Codice della gara", hidden = true)
	private Long idGara;
	@ApiModelProperty(value="Codice del lotto", hidden = true)
	private Long idLotto;
	@ApiModelProperty(value="Numero progressivo condizione", hidden = true)
	private Long numCondizione;
	@ApiModelProperty(value="Condizione (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Condizione)", required = true)  
	private Long condizione;
	
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
	public void setNumCondizione(Long numCondizione) {
		this.numCondizione = numCondizione;
	}
	@XmlTransient
	public Long getNumCondizione() {
		return numCondizione;
	}
	public void setCondizione(Long condizione) {
		this.condizione = condizione;
	}
	public Long getCondizione() {
		return condizione;
	}
	

}
