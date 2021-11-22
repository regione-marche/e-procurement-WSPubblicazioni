/*
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
import java.util.Date;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Pubblicazione Bando.
 * 
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {"dataGuce", "dataGuri", "dataAlbo", "quotidianiNazionali",
		"quotidianiLocali", "profiloCommittente", "profiloInfTrasp", "profiloOsservatorio", "dataBore", "periodici" })
@ApiModel(description = "*Contenitore per i dati della pubblicazione di un bando")
public class PubblicazioneBandoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@ApiModelProperty(hidden = true)
	private Long idGara;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data Gazzetta Ufficiale Comunità Europea")  
	private Date dataGuce;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data Gazzetta ufficiale Repubblica Italiana")  
	private Date dataGuri;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data Albo pretorio del comuni ove si eseguono i lavori")  
	private Date dataAlbo;
	
	@ApiModelProperty(value="Numero quotidiani nazionali")
	private Long quotidianiNazionali;
	
	@ApiModelProperty(value="Numero quotidiani locali")
	private Long quotidianiLocali;
	
	@ApiModelProperty(value="Profilo del committente (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String profiloCommittente;
	
	@ApiModelProperty(value="Sito Informatico Ministero Infrastrutture (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String profiloInfTrasp;
	
	@ApiModelProperty(value="Sito Informatico Osservatorio Contratti Pubblici (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String profiloOsservatorio;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	@ApiModelProperty(value="Data Gazzetta ufficiale o bollettino regionale")  
	private Date dataBore;
	
	@ApiModelProperty(value="Numero periodici")
	private Long periodici;

	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}

	public Long getIdGara() {
		return idGara;
	}

	public void setDataGuce(Date dataGuce) {
		this.dataGuce = dataGuce;
	}

	public Date getDataGuce() {
		return dataGuce;
	}

	public void setDataGuri(Date dataGuri) {
		this.dataGuri = dataGuri;
	}

	public Date getDataGuri() {
		return dataGuri;
	}

	public void setDataAlbo(Date dataAlbo) {
		this.dataAlbo = dataAlbo;
	}

	public Date getDataAlbo() {
		return dataAlbo;
	}

	public void setQuotidianiNazionali(Long quotidianiNazionali) {
		this.quotidianiNazionali = quotidianiNazionali;
	}

	public Long getQuotidianiNazionali() {
		return quotidianiNazionali;
	}

	public void setQuotidianiLocali(Long quotidianiLocali) {
		this.quotidianiLocali = quotidianiLocali;
	}

	public Long getQuotidianiLocali() {
		return quotidianiLocali;
	}

	public void setProfiloCommittente(String profiloCommittente) {
		this.profiloCommittente = StringUtils.stripToNull(profiloCommittente);
	}

	public String getProfiloCommittente() {
		return profiloCommittente;
	}

	public void setProfiloInfTrasp(String profiloInfTrasp) {
		this.profiloInfTrasp = StringUtils.stripToNull(profiloInfTrasp);
	}

	public String getProfiloInfTrasp() {
		return profiloInfTrasp;
	}

	public void setProfiloOsservatorio(String profiloOsservatorio) {
		this.profiloOsservatorio = StringUtils.stripToNull(profiloOsservatorio);
	}

	public String getProfiloOsservatorio() {
		return profiloOsservatorio;
	}

	public void setDataBore(Date dataBore) {
		this.dataBore = dataBore;
	}

	public Date getDataBore() {
		return dataBore;
	}

	public void setPeriodici(Long periodici) {
		this.periodici = periodici;
	}

	public Long getPeriodici() {
		return periodici;
	}

}
