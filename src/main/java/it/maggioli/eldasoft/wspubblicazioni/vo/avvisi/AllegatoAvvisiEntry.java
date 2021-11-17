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
package it.maggioli.eldasoft.wspubblicazioni.vo.avvisi;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wspubblicazioni.vo.AllegatoEntry;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Allegato avvisi.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"titolo",
		"url",
		"allegato"
})
@ApiModel(description="Nome e numero allegato")
public class AllegatoAvvisiEntry extends AllegatoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private String codiceSA;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long id;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long codiceSistema;

public void setCodiceSA(String codiceSA) {
	this.codiceSA = StringUtils.stripToNull(codiceSA);
}

@XmlTransient
public String getCodiceSA() {
	return codiceSA;
}

public void setId(Long id) {
	this.id = id;
}

@XmlTransient
public Long getId() {
	return id;
}

public void setCodiceSistema(Long codiceSistema) {
	this.codiceSistema = codiceSistema;
}

@XmlTransient
public Long getCodiceSistema() {
	return codiceSistema;
}
  

}
