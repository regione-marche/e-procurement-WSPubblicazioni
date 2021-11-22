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

/**
 * Dettaglio di una tipologia di pubblicazione.
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
	    "id",
	    "nome",
	    "clausolaWhere",
	    "campiVisualizzati",
	    "campiObbligatori",
	    "tipo"
})
@ApiModel(description="Contenitore per i dati di configurazione di una pubblicazione")
public class TipoAttoEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -6611269573839884401L;
  
  @ApiModelProperty(value="Codice della configurazione")
  private Long id;
  
  @ApiModelProperty(value="Nome della pubblicazione", notes="Descrive la tipologia di pubblicazione, ad es. 'Decreto di indizione o determina a contrarre', 'Bando di gara di appalto, concessione o concorso ed estratto', 'Avviso in merito alla modifica dell'ordine di importanza dei criteri, bando di concessione (art.173)', ecc.")
  private String nome;
  
  @ApiModelProperty(value="Clausola where per visualizzazione", notes="Permette di specificare la condizione per la quale è da utilizzare la pubblicazione (ad esempio in base al valore di W9GARA.ID_SCELTA_CONTRAENTE_50)")
  private String clausolaWhere;
  
  @ApiModelProperty(value="Campi visualizzati", notes="Elenco dei campi della tabella w9pubblicazioni da mostrare")
  private String campiVisualizzati;
  
  @ApiModelProperty(value="Campi obbligatori", notes="Elenco dei campi della tabella w9pubblicazioni da mostrare come obbligatori")
  private String campiObbligatori;
  
  @ApiModelProperty(value="Flag bando/esito", notes="B=Bando - E=Esito - D=Gara Deserta - ")
  private String tipo;
  

public void setId(Long id) {
	this.id = id;
}

public Long getId() {
	return id;
}

public void setNome(String nome) {
	this.nome = StringUtils.stripToNull(nome);
}

public String getNome() {
	return nome;
}

public void setClausolaWhere(String clausolaWhere) {
	this.clausolaWhere = StringUtils.stripToNull(clausolaWhere);
}

public String getClausolaWhere() {
	return clausolaWhere;
}

public void setCampiVisualizzati(String campiVisualizzati) {
	this.campiVisualizzati = StringUtils.stripToNull(campiVisualizzati);
}

public String getCampiVisualizzati() {
	return campiVisualizzati;
}

public void setCampiObbligatori(String campiObbligatori) {
	this.campiObbligatori = StringUtils.stripToNull(campiObbligatori);
}

public String getCampiObbligatori() {
	return campiObbligatori;
}

public void setTipo(String tipo) {
	this.tipo = StringUtils.stripToNull(tipo);
}

public String getTipo() {
	return tipo;
}

 
}
