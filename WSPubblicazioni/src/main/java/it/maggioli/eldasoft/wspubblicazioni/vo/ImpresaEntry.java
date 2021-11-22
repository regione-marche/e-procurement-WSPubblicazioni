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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati generali di una impresa.
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
		"ragioneSociale",
		"formaGiuridica",
		"indirizzo",
		"numeroCivico",
		"localita",
		"provincia",
		"cap",
		"telefono",
		"fax",
		"codiceFiscale",
		"partitaIva",
		"numeroCCIAA",
		"nazione"
})
@ApiModel(description="Contenitore per i dati generali dell'Operatore economico")
public class ImpresaEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden = true)
	private String id;

	@ApiModelProperty(value="Ragione sociale dell'impresa", required = true)
	@Size(max=61, min=1)
	private String ragioneSociale;

	@ApiModelProperty(value="Forma giuridica (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=FormaGiuridica)")  
	private Long formaGiuridica;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Indirizzo (via/piazza/corso) dell'impresa")
	@Size(max=60)
	private String indirizzo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Numero civico dell'impresa")
	@Size(max=10)
	private String numeroCivico;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Localita' dell'impresa")
	@Size(max=32)
	private String localita;

	@ApiModelProperty(value="Provincia dell'impresa (/WSTabelleDiContesto/rest/Tabellati/Province)")
	@Size(max=2)
	private String provincia;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Codice di avviamento postale")
	@Size(max=5)
	private String cap;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Telefono")
	@Size(max=50)
	private String telefono;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*FAX")
	@Size(max=50)
	private String fax;

	@ApiModelProperty(value="Codice fiscale", required = true)
	@Size(max=16, min=11)
	private String codiceFiscale;

	@ApiModelProperty(value="Partita I.V.A. o V.A.T.")
	@Size(max=16)
	private String partitaIva;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Numero dell'iscrizione al Registro Imprese")
	@Size(max=16)
	private String numeroCCIAA;

	@ApiModelProperty(value="Nazionalità (Codice ISO 3166-1 alpha-2)")
	@Size(max=2)
	private String nazione;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Codice nazione",hidden = true)
	private Long codiceNazione;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden = true)
	private String codiceSA;
	
	public void setId(String id) {
		this.id = StringUtils.stripToNull(id);
	}

	@XmlTransient
	public String getId() {
		return id;
	}

	public void setRagioneSociale(String ragioneSociale) {
		this.ragioneSociale = StringUtils.stripToNull(ragioneSociale);
	}

	public String getRagioneSociale() {
		return ragioneSociale;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = StringUtils.stripToNull(indirizzo);
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setNumeroCivico(String numeroCivico) {
		this.numeroCivico = StringUtils.stripToNull(numeroCivico);
	}

	public String getNumeroCivico() {
		return numeroCivico;
	}

	public void setLocalita(String localita) {
		this.localita = StringUtils.stripToNull(localita);
	}

	public String getLocalita() {
		return localita;
	}

	public void setProvincia(String provincia) {
		this.provincia = StringUtils.stripToNull(provincia);
	}

	public String getProvincia() {
		return provincia;
	}

	public void setCap(String cap) {
		this.cap = StringUtils.stripToNull(cap);
	}

	public String getCap() {
		return cap;
	}

	public void setTelefono(String telefono) {
		this.telefono = StringUtils.stripToNull(telefono);
	}

	public String getTelefono() {
		return telefono;
	}

	public void setFax(String fax) {
		this.fax = StringUtils.stripToNull(fax);
	}

	public String getFax() {
		return fax;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = StringUtils.stripToNull(codiceFiscale);
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = StringUtils.stripToNull(partitaIva);
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setNumeroCCIAA(String numeroCCIAA) {
		this.numeroCCIAA = StringUtils.stripToNull(numeroCCIAA);
	}

	public String getNumeroCCIAA() {
		return numeroCCIAA;
	}

	public void setCodiceSA(String codiceSA) {
		this.codiceSA = StringUtils.stripToNull(codiceSA);
	}

	@XmlTransient
	public String getCodiceSA() {
		return codiceSA;
	}

	public void setFormaGiuridica(Long formaGiuridica) {
		this.formaGiuridica = formaGiuridica;
	}

	public Long getFormaGiuridica() {
		return formaGiuridica;
	}

	public void setNazione(String nazione) {
		this.nazione = StringUtils.stripToNull(nazione);
	}

	public String getNazione() {
		return nazione;
	}

	public void setCodiceNazione(Long codiceNazione) {
		this.codiceNazione = codiceNazione;
	}

	public Long getCodiceNazione() {
		return codiceNazione;
	}

}
