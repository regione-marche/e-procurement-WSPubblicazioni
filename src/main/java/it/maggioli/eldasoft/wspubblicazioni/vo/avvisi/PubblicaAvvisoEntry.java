/*
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
import it.maggioli.eldasoft.wspubblicazioni.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati per la pubblicazione di un avviso.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = { 
		"codiceFiscaleSA",
	    "ufficio",
		"tipologia", 
		"data",
		"descrizione", 
		"cig",
		"cup",
		"cui",
		"scadenza",
		"rup",
		"documenti",
		"idRicevuto"
		})
@ApiModel(description = "Contenitore per i dati di pubblicazione dell'avviso")
public class PubblicaAvvisoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private String codiceSA;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private Long id;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private Long syscon;
	
	@ApiModelProperty(value = "Codice Fiscale Stazione appaltante", required = true)
	@Size(max=16, min=11)
	private String codiceFiscaleSA;

	@ApiModelProperty(value = "Codice Unità Organizzativa")
	@Size(max=20)
	private String codiceUnitaOrganizzativa;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private Long codiceSistema;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private String clientId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private Long idUfficio;
	  
	@ApiModelProperty(value="Ufficio/area di pertinenza")
	@Size(max=250)
	private String ufficio;
	  
	@ApiModelProperty(value = "Tipologia avviso (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipoAvviso)", required = true)
	private Long tipologia;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value = "Data dell'avviso")
	private Date data;

	@ApiModelProperty(value = "Descrizione dell'avviso", required = true)
	@Size(max=500, min=1)
	private String descrizione;

	@ApiModelProperty(value = "CIG")
	@Size(max=10)
	private String cig;
	
	@ApiModelProperty(value = "Codice CUP")
	@Size(max=15)
	private String cup;

	@ApiModelProperty(value = "Numero intervento CUI")
	@Size(max=20)
	private String cui;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value = "Data scadenza")
	private Date scadenza;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data pubblicazione atto su avviso SCP (valorizzato solo nel metodo /Avvisi/DettaglioAvviso)")  
	private Date primaPubblicazioneSCP;
	  
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Utima modifica pubblicazione avviso su sito SCP (valorizzato solo nel metodo /Avvisi/DettaglioAvviso)")  
	private Date ultimaModificaSCP;
	  
	@ApiModelProperty(value="Documenti allegati alla pubblicazione", required = true)
	@Size(min=1)
	private List<AllegatoAvvisiEntry> documenti = new ArrayList<AllegatoAvvisiEntry>();
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden=true)
	private String codiceRup;
	
	@ApiModelProperty(value = "Responsabile dell'avviso", required = true)
	private DatiGeneraliTecnicoEntry rup;
	
	@ApiModelProperty(value = "Id univoco generato dal sistema remoto; deve essere utilizzato per le chiamate successive")
	private Long idRicevuto;
	
	@ApiModelProperty(value="Indirizzo ubicazione")
	@Size(max=100)
	private String indirizzo;
	  
	@ApiModelProperty(value="Comune ubicazione")
	@Size(max=32)
	private String comune;
	  
	@ApiModelProperty(value="Provincia ubicazione (/WSTabelleDiContesto/rest/Tabellati/Province)")
	@Size(max=2)
	private String provincia;
	  
	public void setIdRicevuto(Long idRicevuto) {
		this.idRicevuto = idRicevuto;
	}

	public Long getIdRicevuto() {
		return idRicevuto;
	}

	public void setRup(DatiGeneraliTecnicoEntry rup) {
		this.rup = rup;
	}

	public DatiGeneraliTecnicoEntry getRup() {
		return rup;
	}

	public void setCodiceFiscaleSA(String codiceFiscaleSA) {
		this.codiceFiscaleSA = StringUtils.stripToNull(codiceFiscaleSA);
	}

	public String getCodiceFiscaleSA() {
		return codiceFiscaleSA;
	}

	public void setCodiceSistema(Long codiceSistema) {
		this.codiceSistema = codiceSistema;
	}

	@XmlTransient
	public Long getCodiceSistema() {
		return codiceSistema;
	}

	public void setTipologia(Long tipologia) {
		this.tipologia = tipologia;
	}

	public Long getTipologia() {
		return tipologia;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Date getData() {
		return data;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = StringUtils.stripToNull(descrizione);
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setCig(String cig) {
		this.cig = StringUtils.stripToNull(cig);
	}

	public String getCig() {
		return cig;
	}

	public void setCup(String cup) {
		this.cup = StringUtils.stripToNull(cup);
	}

	public String getCup() {
		return cup;
	}

	public void setCui(String cui) {
		this.cui = StringUtils.stripToNull(cui);
	}

	public String getCui() {
		return cui;
	}

	public void setScadenza(Date scadenza) {
		this.scadenza = scadenza;
	}

	public Date getScadenza() {
		return scadenza;
	}

	public void setDocumenti(List<AllegatoAvvisiEntry> documenti) {
		this.documenti = documenti;
	}

	public List<AllegatoAvvisiEntry> getDocumenti() {
		return documenti;
	}

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

	public void setCodiceRup(String codiceRup) {
		this.codiceRup = StringUtils.stripToNull(codiceRup);
	}

	@XmlTransient
	public String getCodiceRup() {
		return codiceRup;
	}

	public void setClientId(String clientId) {
		this.clientId = StringUtils.stripToNull(clientId);
	}

	@XmlTransient
	public String getClientId() {
		return clientId;
	}

	public void setIdUfficio(Long idUfficio) {
		this.idUfficio = idUfficio;
	}

	@XmlTransient
	public Long getIdUfficio() {
		return idUfficio;
	}

	public void setUfficio(String ufficio) {
		this.ufficio = StringUtils.stripToNull(ufficio);
	}

	public String getUfficio() {
		return ufficio;
	}

	public void setSyscon(Long syscon) {
		this.syscon = syscon;
	}

	@XmlTransient
	public Long getSyscon() {
		return syscon;
	}

	public void setPrimaPubblicazioneSCP(Date primaPubblicazioneSCP) {
		this.primaPubblicazioneSCP = primaPubblicazioneSCP;
	}

	public Date getPrimaPubblicazioneSCP() {
		return primaPubblicazioneSCP;
	}

	public void setUltimaModificaSCP(Date ultimaModificaSCP) {
		this.ultimaModificaSCP = ultimaModificaSCP;
	}

	public Date getUltimaModificaSCP() {
		return ultimaModificaSCP;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = StringUtils.stripToNull(indirizzo);
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setComune(String comune) {
		this.comune = StringUtils.stripToNull(comune);
	}

	public String getComune() {
		return comune;
	}

	public void setProvincia(String provincia) {
		this.provincia = StringUtils.stripToNull(provincia);
	}

	public String getProvincia() {
		return provincia;
	}

	public void setCodiceUnitaOrganizzativa(String codiceUnitaOrganizzativa) {
		this.codiceUnitaOrganizzativa = StringUtils.stripToNull(codiceUnitaOrganizzativa);
	}

	public String getCodiceUnitaOrganizzativa() {
		if (codiceUnitaOrganizzativa != null)
			return codiceUnitaOrganizzativa.toUpperCase();
		else
			return codiceUnitaOrganizzativa;
	}

}
