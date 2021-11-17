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
 * Modalità acquisizione forniture.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"modalitaAcquisizione"
})
@ApiModel(description="*Modalità acquisizione forniture")
public class AppaFornEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@ApiModelProperty(value="Codice della gara", hidden = true)
	private Long idGara;
	@ApiModelProperty(value="Codice del lotto", hidden = true)
	private Long idLotto;
	@ApiModelProperty(value="Numero progressivo modalità", hidden = true)
	private Long numAppaForn;
	@ApiModelProperty(value="modalità di acquisizione forniture/servizi (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=ModalitaAcquisizioneForniture)", required = true)  
	private Long modalitaAcquisizione;
	
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
	public void setNumAppaForn(Long numAppaForn) {
		this.numAppaForn = numAppaForn;
	}
	@XmlTransient
	public Long getNumAppaForn() {
		return numAppaForn;
	}
	public void setModalitaAcquisizione(Long modalitaAcquisizione) {
		this.modalitaAcquisizione = modalitaAcquisizione;
	}
	public Long getModalitaAcquisizione() {
		return modalitaAcquisizione;
	}

}
