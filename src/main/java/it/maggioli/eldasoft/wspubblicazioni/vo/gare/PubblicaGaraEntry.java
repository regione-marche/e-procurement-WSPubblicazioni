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
package it.maggioli.eldasoft.wspubblicazioni.vo.gare;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.PubblicaLottoEntry;
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
 * Dati di pubblicazione di una gara.
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
	    "oggetto",
	    "situazione",
	    "provenienzaDato",
	    "idAnac",
	    "ente",
	    "indirizzo",
	    "comune",
	    "provincia",
	    "codiceCentroCosto",
	    "centroCosto",
	    "ufficio",
	    "settore",
	    "realizzazione",
	    "modoIndizione",
	    "cigAccQuadro",
	    "saAgente",
	    "tipoSA",
	    "nomeSA",
	    "cfAgente",
	    "altreSA",
	    "tipoProcedura",
	    "centraleCommittenza",
	    "rup",
	    "ricostruzioneAlluvione",
	    "criteriAmbientali",
	    "sisma",
	    "importoGara",
	    "idSceltaContraente50",
	    "idModoGara",
	    "urlCommittente",
	    "urlPiattaformaTelematica",
	    "dataPubblicazione",
	    "dataScadenza",
	    "sommaUrgenza",
	    "durataAccordoQuadro",
	    "dataPerfezionamentoBando",
	    "versioneSimog",
	    "tecnicoRup",
	    "pubblicazioneBando",
		"lotti",
	    "idRicevuto"
})
@ApiModel(description="Contenitore per i dati di pubblicazione della gara")
public class PubblicaGaraEntry implements Serializable {
  /**
   * UID.
   */
  private static final long serialVersionUID = -6611269573839884401L;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long id;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private String clientId;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long syscon;
  
  @ApiModelProperty(value="Oggetto - descrizione", required = true)
  @Size(max=1024, min=1)
  private String oggetto;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Situazione della gara (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Situazione)", hidden = true)
  private Long situazione;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Provenienza del dato (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Provenienza)", hidden = true)
  private Long provenienzaDato;
  
  @ApiModelProperty(value="Numero gara ANAC (mettere 0 nel caso di richiesta Smartcig)")
  @Size(max=20)
  private String idAnac;
  
  @ApiModelProperty(value = "Codice Fiscale Stazione appaltante", required = true)
  @Size(max=16, min=11)
  private String codiceFiscaleSA;
  
  @ApiModelProperty(value = "Codice Unità Organizzativa")
  @Size(max=20)
  private String codiceUnitaOrganizzativa;
	
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private String idEnte;
  
  @ApiModelProperty(value="Indirizzo sede di gara")
  @Size(max=100)
  private String indirizzo;
  
  @ApiModelProperty(value="Comune sede di gara")
  @Size(max=32)
  private String comune;
  
  @ApiModelProperty(value="Provincia sede di gara (/WSTabelleDiContesto/rest/Tabellati/Province)")
  @Size(max=2)
  private String provincia;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long idCentroCosto;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Codice del Centro di Costo", hidden = true)
  @Size(max=40)
  private String codiceCentroCosto;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Denominazione del Centro di Costo", hidden = true)
  @Size(max=250)
  private String centroCosto;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private Long idUfficio;
  
  @ApiModelProperty(value="Ufficio/area di pertinenza")
  @Size(max=250)
  private String ufficio;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Modalità di indizione della gara (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Indizione)", hidden = true)
  private Long modoIndizione;
  
  @ApiModelProperty(value="CIG accordo quadro/convenzione")
  @Size(max=10)
  private String cigAccQuadro;
  
  @ApiModelProperty(value="La stazione appaltante agisce per conto di altri soggetti? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
  @Size(max=1)
  private String saAgente;
  
  @ApiModelProperty(value="Tipologia della stazione appaltante (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaSA)")
  private Long tipoSA;
  
  @ApiModelProperty(value="Denominazione del soggetto per cui agisce la S.A.")
  @Size(max=254)
  private String nomeSA;
  
  @ApiModelProperty(value="Codice fiscale del soggetto per cui agisce la S.A.")
  @Size(max=16)
  private String cfAgente;
  
  @ApiModelProperty(value="Altri soggetti per cui agisce la S.A.")
  @Size(max=500)
  private String altreSA;
  
  @ApiModelProperty(value="Tipologia procedura (nel caso agisca per conto di altri) (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipologiaProcedura)")
  private Long tipoProcedura;
  
  @ApiModelProperty(value="La centrale di committenza provvede alla stipula? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
  @Size(max=1)
  private String centraleCommittenza;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(hidden=true)
  private String idRup;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Ordinanza ricostruzione alluvione Lunigiana ed Elba? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", hidden = true)
  @Size(max=1)
  private String ricostruzioneAlluvione;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Ambito di applicazione Criteri Ambientali Minimi? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", hidden = true)
  @Size(max=1)
  private String criteriAmbientali;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="*Nesso di causalità con gli eventi sismici maggio 2012? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", hidden = true)
  @Size(max=1)
  private String sisma;
  
  @ApiModelProperty(value="Importo della gara", required = true)
  private Double importoGara;
  
  @ApiModelProperty(value="Tipo di settore (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Settore)")
  @Size(max=5)
  private String settore;	//FLAG_ENTE_SPECIALE - W3z08
  
  @ApiModelProperty(value="Modalità di realizzazione (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipoRealizzazione)")
  private Long realizzazione;	//TIPO_APP - W3999
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @ApiModelProperty(value="*[Attributo aggiuntivo, previsto in ambiti regionali] Data pubblicazione bando su GURI,se prevista,ovvero su albo pretorio")  
  private Date dataPubblicazione;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @ApiModelProperty(value="*[Attributo aggiuntivo, previsto in ambiti regionali] Data scadenza del bando")  
  private Date dataScadenza;
  
  @ApiModelProperty(value="*[Attributo aggiuntivo, previsto in ambiti regionali]Somma urgenza? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
  @Size(max=1)
  private String sommaUrgenza;
	
  @ApiModelProperty(value="*[Attributo aggiuntivo, previsto in ambiti regionali]Durata accordo quadro")
  private Long durataAccordoQuadro;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @ApiModelProperty(value="*[Attributo aggiuntivo, previsto in ambiti regionali]Data perfezionamento bando")  
  private Date dataPerfezionamentoBando;
  
  @ApiModelProperty(value="*[Attributo aggiuntivo, previsto in ambiti regionali]Versione SIMOG")
  private Long versioneSimog;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @ApiModelProperty(value="Data pubblicazione gara su sito SCP (valorizzato solo nel metodo /Anagrafiche/DettaglioGara)")  
  private Date primaPubblicazioneSCP;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
  @ApiModelProperty(value="Utima modifica pubblicazione gara su sito SCP (valorizzato solo nel metodo /Anagrafiche/DettaglioGara)")  
  private Date ultimaModificaSCP;
  
  @ApiModelProperty(value = "Responsabile unico procedimento", required = true)
  private DatiGeneraliTecnicoEntry tecnicoRup;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value = "*[Attributo aggiuntivo, previsto in Simog] Pubblicazione bando")
  private PubblicazioneBandoEntry pubblicazioneBando;
  
  @ApiModelProperty(value="Lista lotti da pubblicare" , required = true)
  @Size(min=1)
  private List<PubblicaLottoEntry> lotti = new ArrayList<PubblicaLottoEntry>();

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @ApiModelProperty(value="Lista atti della gara (valorizzato solo nel metodo /Anagrafiche/DettaglioGara)")
  private List<AttoGaraEntry> atti;

  @ApiModelProperty(value = "Id univoco generato dal sistema remoto; deve essere utilizzato per le chiamate successive")
  private Long idRicevuto;
  
  @ApiModelProperty(value="*Funzioni delegate (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=FunzioniDelegate)")
  private Long funzioniDelegate;
  
  public void setSettore(String settore) {
		this.settore = StringUtils.stripToNull(settore);
	}

	public String getSettore() {
		return settore;
	}

	public void setSaAgente(String saAgente) {
		this.saAgente = StringUtils.stripToNull(saAgente);
	}

	public String getSaAgente() {
		return saAgente;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@XmlTransient
	public Long getId() {
		return id;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = StringUtils.stripToNull(oggetto);
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setSituazione(Long situazione) {
		this.situazione = situazione;
	}

	public Long getSituazione() {
		return situazione;
	}

	public void setProvenienzaDato(Long provenienzaDato) {
		this.provenienzaDato = provenienzaDato;
	}

	public Long getProvenienzaDato() {
		return provenienzaDato;
	}

	public void setIdAnac(String idAnac) {
		this.idAnac = StringUtils.stripToNull(idAnac);
	}

	public String getIdAnac() {
		return idAnac;
	}

	public void setIdEnte(String idEnte) {
		this.idEnte = StringUtils.stripToNull(idEnte);
	}

	@XmlTransient
	public String getIdEnte() {
		return idEnte;
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

	public void setIdCentroCosto(Long idCentroCosto) {
		this.idCentroCosto = idCentroCosto;
	}

	@XmlTransient
	public Long getIdCentroCosto() {
		return idCentroCosto;
	}

	public void setCodiceCentroCosto(String codiceCentroCosto) {
		this.codiceCentroCosto = StringUtils.stripToNull(codiceCentroCosto);
	}

	public String getCodiceCentroCosto() {
		return codiceCentroCosto;
	}

	public void setCentroCosto(String centroCosto) {
		this.centroCosto = StringUtils.stripToNull(centroCosto);
	}

	public String getCentroCosto() {
		return centroCosto;
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

	public void setModoIndizione(Long modoIndizione) {
		this.modoIndizione = modoIndizione;
	}

	public Long getModoIndizione() {
		return modoIndizione;
	}

	public void setCigAccQuadro(String cigAccQuadro) {
		this.cigAccQuadro = StringUtils.stripToNull(cigAccQuadro);
	}

	public String getCigAccQuadro() {
		return cigAccQuadro;
	}

	public void setTipoSA(Long tipoSA) {
		this.tipoSA = tipoSA;
	}

	public Long getTipoSA() {
		return tipoSA;
	}

	public void setNomeSA(String nomeSA) {
		this.nomeSA = StringUtils.stripToNull(nomeSA);
	}

	public String getNomeSA() {
		return nomeSA;
	}

	public void setCfAgente(String cfAgente) {
		this.cfAgente = StringUtils.stripToNull(cfAgente);
	}

	public String getCfAgente() {
		return cfAgente;
	}

	public void setTipoProcedura(Long tipoProcedura) {
		this.tipoProcedura = tipoProcedura;
	}

	public Long getTipoProcedura() {
		return tipoProcedura;
	}

	public void setCentraleCommittenza(String centraleCommittenza) {
		this.centraleCommittenza = StringUtils.stripToNull(centraleCommittenza);
	}

	public String getCentraleCommittenza() {
		return centraleCommittenza;
	}

	public void setIdRup(String idRup) {
		this.idRup = StringUtils.stripToNull(idRup);
	}

	@XmlTransient
	public String getIdRup() {
		return idRup;
	}

	public void setRicostruzioneAlluvione(String ricostruzioneAlluvione) {
		this.ricostruzioneAlluvione = StringUtils.stripToNull(ricostruzioneAlluvione);
	}

	public String getRicostruzioneAlluvione() {
		return ricostruzioneAlluvione;
	}

	public void setCriteriAmbientali(String criteriAmbientali) {
		this.criteriAmbientali = StringUtils.stripToNull(criteriAmbientali);
	}

	public String getCriteriAmbientali() {
		return criteriAmbientali;
	}

	public void setSisma(String sisma) {
		this.sisma = StringUtils.stripToNull(sisma);
	}

	public String getSisma() {
		return sisma;
	}

	public void setImportoGara(Double importoGara) {
		this.importoGara = importoGara;
	}

	public Double getImportoGara() {
		return importoGara;
	}

	public void setRealizzazione(Long realizzazione) {
		this.realizzazione = realizzazione;
	}

	public Long getRealizzazione() {
		return realizzazione;
	}

	public void setAltreSA(String altreSA) {
		this.altreSA = StringUtils.stripToNull(altreSA);
	}

	public String getAltreSA() {
		return altreSA;
	}
	
public void setTecnicoRup(DatiGeneraliTecnicoEntry tecnicoRup) {
	this.tecnicoRup = tecnicoRup;
}

public DatiGeneraliTecnicoEntry getTecnicoRup() {
	return tecnicoRup;
}

public void setLotti(List<PubblicaLottoEntry> lotti) {
	this.lotti = lotti;
}

public List<PubblicaLottoEntry> getLotti() {
	return lotti;
}

public void setIdRicevuto(Long idRicevuto) {
	this.idRicevuto = idRicevuto;
}

public Long getIdRicevuto() {
	return idRicevuto;
}

public void setCodiceFiscaleSA(String codiceFiscaleSA) {
	this.codiceFiscaleSA = StringUtils.stripToNull(codiceFiscaleSA);
}

public String getCodiceFiscaleSA() {
	return codiceFiscaleSA;
}

public void setClientId(String clientId) {
	this.clientId = StringUtils.stripToNull(clientId);
}

@XmlTransient
public String getClientId() {
	return clientId;
}

public void setSyscon(Long syscon) {
	this.syscon = syscon;
}

@XmlTransient
public Long getSyscon() {
	return syscon;
}

public void setDataPubblicazione(Date dataPubblicazione) {
	this.dataPubblicazione = dataPubblicazione;
}

public Date getDataPubblicazione() {
	return dataPubblicazione;
}

public void setDataScadenza(Date dataScadenza) {
	this.dataScadenza = dataScadenza;
}

public Date getDataScadenza() {
	return dataScadenza;
}

public void setPubblicazioneBando(PubblicazioneBandoEntry pubblicazioneBando) {
	this.pubblicazioneBando = pubblicazioneBando;
}

public PubblicazioneBandoEntry getPubblicazioneBando() {
	return pubblicazioneBando;
}
/*
public void setSommaUrgenza(String sommaUrgenza) {
	this.sommaUrgenza = sommaUrgenza;
}

public String getSommaUrgenza() {
	return sommaUrgenza;
}

public void setDurataAccordoQuadro(Long durataAccordoQuadro) {
	this.durataAccordoQuadro = durataAccordoQuadro;
}

public Long getDurataAccordoQuadro() {
	return durataAccordoQuadro;
}

public void setVersioneSimog(Long versioneSimog) {
	this.versioneSimog = versioneSimog;
}

public Long getVersioneSimog() {
	return versioneSimog;
}

public void setDataPerfezionamentoBando(Date dataPerfezionamentoBando) {
	this.dataPerfezionamentoBando = dataPerfezionamentoBando;
}

public Date getDataPerfezionamentoBando() {
	return dataPerfezionamentoBando;
}
*/

public void setAtti(List<AttoGaraEntry> atti) {
	this.atti = atti;
}

public List<AttoGaraEntry> getAtti() {
	return atti;
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

public void setSommaUrgenza(String sommaUrgenza) {
	this.sommaUrgenza = StringUtils.stripToNull(sommaUrgenza);
}

public String getSommaUrgenza() {
	return sommaUrgenza;
}

public void setDurataAccordoQuadro(Long durataAccordoQuadro) {
	this.durataAccordoQuadro = durataAccordoQuadro;
}

public Long getDurataAccordoQuadro() {
	return durataAccordoQuadro;
}

public void setDataPerfezionamentoBando(Date dataPerfezionamentoBando) {
	this.dataPerfezionamentoBando = dataPerfezionamentoBando;
}

public Date getDataPerfezionamentoBando() {
	return dataPerfezionamentoBando;
}

public void setVersioneSimog(Long versioneSimog) {
	this.versioneSimog = versioneSimog;
}

public Long getVersioneSimog() {
	return versioneSimog;
}

public void setFunzioniDelegate(Long funzioniDelegate) {
	this.funzioniDelegate = funzioniDelegate;
}

public Long getFunzioniDelegate() {
	return funzioniDelegate;
}

public void setCodiceUnitaOrganizzativa(String codiceUnitaOrganizzativa) {
	this.codiceUnitaOrganizzativa = StringUtils.stripToNull(codiceUnitaOrganizzativa);
}

public String getCodiceUnitaOrganizzativa() {
	if (codiceUnitaOrganizzativa != null)
		return codiceUnitaOrganizzativa.toUpperCase();
	else
		return null;
}

}
