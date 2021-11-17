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

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati allegato.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"titolo",
		"url",
		"file"
})
@ApiModel(description="Nome e numero allegato")
public class AllegatoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -4433185026855332865L;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long nrDoc;
  
  @ApiModelProperty(value="Titolo del documento", required = true)  
  @Size(max=250, min=1)
  private String titolo;
  @ApiModelProperty(value="URL documentazione")  
  @Size(max=2000)
  private String url;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Contenuto file allegato", hidden = true)  
  private byte[] file;

  @ApiModelProperty(value="*Tipo file")  
  @Size(max=20)
  private String tipoFile;
    
public void setNrDoc(Long nrDoc) {
	this.nrDoc = nrDoc;
}

@XmlTransient
public Long getNrDoc() {
	return nrDoc;
}
public void setTitolo(String titolo) {
	this.titolo = StringUtils.stripToNull(titolo);
}
public String getTitolo() {
	return titolo;
}
public void setUrl(String url) {
	this.url = StringUtils.stripToNull(url);
}
public String getUrl() {
	return url;
}
public void setFile(byte[] allegato) {
	this.file = allegato;
}
public byte[] getFile() {
	return file;
}

public void setTipoFile(String tipoFile) {
	this.tipoFile = tipoFile;
}

public String getTipoFile() {
	return tipoFile;
}

}
