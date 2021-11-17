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
import javax.xml.bind.annotation.XmlType;


/**
 * Errore di validazione
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
		"nome",
		"messaggio",
		"tipo"
})
@ApiModel(description="Errore di validazione")
public class ValidateEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@ApiModelProperty(value="Nome campo validato")
	private String nome;
	@ApiModelProperty(value="Messaggio di errore")  
	private String messaggio;
	@ApiModelProperty(value="Tipo di messaggio, 'E' = errore bloccante, 'W' = avviso")  
	private String tipo;
	
	public ValidateEntry(String nome, String messaggio) {
		this.nome = nome;
		this.messaggio = messaggio;
		this.tipo = "E";
	}
	
	public ValidateEntry(String nome, String messaggio, String tipo) {
		this.nome = nome;
		this.messaggio = messaggio;
		this.tipo = tipo;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNome() {
		return nome;
	}
	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}
	public String getMessaggio() {
		return messaggio;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipo() {
		return tipo;
	}
}
