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
package it.maggioli.eldasoft.wspubblicazioni.vo.gare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlType;

/**
 * Dati Generali Atto Gara.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"tipoDocumento",
		"idRicevuto"
})
@ApiModel(description="Riferimento all'Atto di gara")
public class AttoGaraEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@ApiModelProperty(value="Tipologia pubblicazione (/WSTabelleDiContesto/rest/Tabellati/Atti)")  
	private Long tipoDocumento;  

	@ApiModelProperty(value = "Id univoco generato dal sistema remoto; deve essere utilizzato per le chiamate successive")
	private Long idRicevuto;

	public void setTipoDocumento(Long tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Long getTipoDocumento() {
		return tipoDocumento;
	}

	public void setIdRicevuto(Long idRicevuto) {
		this.idRicevuto = idRicevuto;
	}

	public Long getIdRicevuto() {
		return idRicevuto;
	}

}
