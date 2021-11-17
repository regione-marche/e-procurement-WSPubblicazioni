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
import it.maggioli.eldasoft.wspubblicazioni.vo.ImpresaEntry;

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;
/**
 * Dati aggiudicatario.
 *
 * @author Mirco.Franzoni
 */
@XmlType(propOrder = {
    "tipoAggiudicatario",
    "ruolo",
    "codiceImpresa",
    "impresa",
    "idGruppo"
})
    
@ApiModel(description="Dati relativi ad un aggiudicatario")
public class AggiudicatarioEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Codice della gara", hidden = true)  
	private Long idGara;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Numero progressivo pubblicazione", hidden = true)  
	private Long numeroPubblicazione;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Numero progressivo aggiudicatario", hidden = true)  
	private Long numeroAggiudicatario;  
	
	@ApiModelProperty(value="Tipologia del soggetto aggiudicatario (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaAggiudicatario)", required=true)  
	private Long tipoAggiudicatario;  
	@ApiModelProperty(value="Ruolo nell'associazione (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=RuoloAssociazione)")  
	private Long ruolo; 
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Collegamento all'archivio delle imprese", hidden = true)
	@Size(max=10)
	private String codiceImpresa; 
	
	@ApiModelProperty(value="Dati dell'Operatore economico", required=true)  
	private ImpresaEntry impresa;  
	
	@ApiModelProperty(value="Numero raggruppamento")  
	private Long idGruppo; 


	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}
	@XmlTransient
	public Long getIdGara() {
		return idGara;
	}
	public void setNumeroPubblicazione(Long numeroPubblicazione) {
		this.numeroPubblicazione = numeroPubblicazione;
	}
	@XmlTransient
	public Long getNumeroPubblicazione() {
		return numeroPubblicazione;
	}
	public void setNumeroAggiudicatario(Long numeroAggiudicatario) {
		this.numeroAggiudicatario = numeroAggiudicatario;
	}
	@XmlTransient
	public Long getNumeroAggiudicatario() {
		return numeroAggiudicatario;
	}
	public void setTipoAggiudicatario(Long tipoAggiudicatario) {
		this.tipoAggiudicatario = tipoAggiudicatario;
	}
	public Long getTipoAggiudicatario() {
		return tipoAggiudicatario;
	}
	public void setRuolo(Long ruolo) {
		this.ruolo = ruolo;
	}
	public Long getRuolo() {
		return ruolo;
	}
	public void setCodiceImpresa(String codiceImpresa) {
		this.codiceImpresa = StringUtils.stripToNull(codiceImpresa);
	}
	@XmlTransient
	public String getCodiceImpresa() {
		return codiceImpresa;
	}
	public void setIdGruppo(Long idGruppo) {
		this.idGruppo = idGruppo;
	}
	public Long getIdGruppo() {
		return idGruppo;
	}
	public void setImpresa(ImpresaEntry impresa) {
		this.impresa = impresa;
	}
	public ImpresaEntry getImpresa() {
		return impresa;
	}

}
