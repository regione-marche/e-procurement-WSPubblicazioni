/*
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wspubblicazioni.vo.tecnici;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.Alias;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati generali di un soggetto.
 * 
 * @author Alessandro.Sernagiotto
 */
@XmlRootElement
@XmlType(propOrder = {"cognome", "nome", "nomeCognome", "indirizzo",
		"civico", "localita", "provincia", "cap", "luogoIstat", "telefono", "fax", "cfPiva",
		"mail" })
@Alias("datigeneralitecnicoentry")
@ApiModel(description = "Contenitore per i dati generali del soggetto")
public class DatiGeneraliTecnicoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@XmlElement(required = true)
	@ApiModelProperty(value = "*Codice del tecnico", hidden = true)
	@Size(max=10)
	private String codice;
	
	@ApiModelProperty(value = "Cognome del tecnico", required = true)
	@Size(max=80, min=1)
	private String cognome;
	
	@ApiModelProperty(value = "Nome del tecnico", required = true)
	@Size(max=80, min=1)
	private String nome;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*Cognome e nome del tecnico")
	@Size(max=161)
	private String nomeCognome;
	
	@ApiModelProperty(value = "Indirizzo (via/piazza/corso)")
	@Size(max=60)
	private String indirizzo;
	
	@ApiModelProperty(value = "Numero civico")
	@Size(max=10)
	private String civico;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*Localita` di residenza")
	@Size(max=32)
	private String localita;
	
	@ApiModelProperty(value = "Provincia di residenza (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Provincia)")
	@Size(max=2)
	private String provincia; //PROV. - Agx15
	
	@ApiModelProperty(value = "Codice di avviamento postale")
	@Size(max=5)
	private String cap;
	
	@ApiModelProperty(value="Codice ISTAT del Comune luogo di esecuzione del contratto")
	@Size(max=9)
	private String luogoIstat;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*Numero di telefono")
	@Size(max=50)
	private String telefono;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*FAX")
	@Size(max=20)
	private String fax;
	
	@ApiModelProperty(value = "Codice fiscale", required = true)
	@Size(max=16, min=11)
	private String cfPiva;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*Indirizzo E-mail")
	@Size(max=100)
	private String mail;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*Codice Stazione appaltante di appartenenza", hidden = true)
	@Size(max=16)
	private String codiceSA;
	
	@XmlTransient
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = StringUtils.stripToNull(codice);
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = StringUtils.stripToNull(cognome);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = StringUtils.stripToNull(nome);
	}

	public String getNomeCognome() {
		return nomeCognome;
	}

	public void setNomeCognome(String nomeCognome) {
		this.nomeCognome = StringUtils.stripToNull(nomeCognome);
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = StringUtils.stripToNull(indirizzo);
	}

	public String getCivico() {
		return civico;
	}

	public void setCivico(String civico) {
		this.civico = StringUtils.stripToNull(civico);
	}

	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = StringUtils.stripToNull(localita);
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = StringUtils.stripToNull(provincia);
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = StringUtils.stripToNull(cap);
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = StringUtils.stripToNull(telefono);
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = StringUtils.stripToNull(fax);
	}

	public String getCfPiva() {
		return cfPiva;
	}

	public void setCfPiva(String cfPiva) {
		this.cfPiva = StringUtils.stripToNull(cfPiva);
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = StringUtils.stripToNull(mail);
	}

	public void setCodiceSA(String codiceSA) {
		this.codiceSA = StringUtils.stripToNull(codiceSA);
	}

	public String getCodiceSA() {
		return codiceSA;
	}

	public void setLuogoIstat(String luogoIstat) {
		this.luogoIstat = StringUtils.stripToNull(luogoIstat);
	}

	public String getLuogoIstat() {
		return luogoIstat;
	}

}
