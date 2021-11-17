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
import it.maggioli.eldasoft.wspubblicazioni.vo.AllegatoEntry;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Allegato pubblicazione.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"titolo",
		"url",
		"allegato"
})
@ApiModel(description="Nome e numero allegato")
public class AllegatoAttiEntry extends AllegatoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Numero progressivo pubblicazione", hidden = true)  
  private Long nrPubblicazione;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Codice della gara", hidden = true)  
  private Long idGara;
  

public void setNrPubblicazione(Long nrPubblicazione) {
	this.nrPubblicazione = nrPubblicazione;
}
@XmlTransient
public Long getNrPubblicazione() {
	return nrPubblicazione;
}

public void setIdGara(Long idGara) {
	this.idGara = idGara;
}
@XmlTransient
public Long getIdGara() {
	return idGara;
}

}
