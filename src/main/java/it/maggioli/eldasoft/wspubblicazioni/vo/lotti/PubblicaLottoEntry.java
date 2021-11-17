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
package it.maggioli.eldasoft.wspubblicazioni.vo.lotti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.maggioli.eldasoft.wspubblicazioni.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati di pubblicazione di un lotto.
 *
 * @author Mirco.Franzoni
 */
@XmlRootElement
@XmlType(propOrder = {
		"oggetto",
		"importoLotto",
		"importoSicurezza",
		"importoTotale",
		"numeroLotto",		
		"cpv",
		"idSceltaContraente",
		"idSceltaContraente50",
		"categoria",
		"classe",
		"lottoPrecedente",
		"motivo",
		"cigCollegato",
		"tipoAppalto",
		"settore",
		"criterioAggiudicazione",
		"luogoIstat",
		"luogoNuts",
		"cig",
		"cupEsente",
		"cup",
		"urlCommittente",
		"urlPiattaformaTelematica",
		"cui",
		"sommaUrgenza",
		"manodopera",
		"codiceIntervento",
		"prestazioniComprese",
		"contrattoEsclusoArt19e26",
		"contrattoEsclusoArt16e17e18",
		"modalitaAcquisizioneForniture",
		"motivazioniProceduraNegoziata",
		"tipologieLavori",
	    "categorie",
	    "cpvSecondari",
		"tecnicoRup"
})

@ApiModel(description="Contenitore per i dati di pubblicazione del lotto")
public class PubblicaLottoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -6611269573839884401L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden = true)
	private Long idGara;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden = true)
	private Long idLotto;

	@ApiModelProperty(value="Oggetto del lotto", required = true)
	@Size(max=1024, min=1)
	private String oggetto;

	@ApiModelProperty(value="Importo del lotto", required = true)
	private Double importoLotto;

	@ApiModelProperty(value="*Importo totale per l'attuazione della sicurezza")
	private Double importoSicurezza;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Importo totale del lotto", hidden = true)
	private Double importoTotale;

	@ApiModelProperty(value="Numero del lotto della gara")
	private Long numeroLotto;
	
	@ApiModelProperty(value="Codice CPV")
	@Size(max=12)
	private String cpv;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Procedura di scelta del contraente (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SceltaContraente)", hidden = true)
	private Long idSceltaContraente;

	@ApiModelProperty(value="Procedura di scelta contraente ai sensi del D.lgs. 50/2016 (/Tabellati/Valori?cod=SceltaContraente50)")
	private Long idSceltaContraente50;

	@ApiModelProperty(value="Categoria prevalente (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Categorie)")
	@Size(max=10)
	private String categoria;

	@ApiModelProperty(value="Classe importo categoria prevalente (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Classe)")
	@Size(max=5)
	private String classe;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Lotto discendente da precedente procedura? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", hidden = true)
	@Size(max=1)
	private String lottoPrecedente;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Motivo del completamento del contratto (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=MotivoCompletamento)", hidden = true)
	private Long motivo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*CIG a cui il lotto è collegato", hidden = true)
	@Size(max=10)
	private String cigCollegato;

	@ApiModelProperty(value="Tipo appalto (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=TipoAppalto)", required = true)
	@Size(max=5, min=1)
	private String tipoAppalto;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Tipo di settore (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Settore)", hidden = true)
	@Size(max=5)
	private String settore;

	@ApiModelProperty(value="Criterio di aggiudicazione (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=CriterioAggiudicazione)")
	private Long criterioAggiudicazione;
	  
	@ApiModelProperty(value="Codice ISTAT del Comune luogo di esecuzione del contratto")
	@Size(max=9)
	private String luogoIstat;

	@ApiModelProperty(value="Luogo di esecuzione del contratto - NUTS")
	@Size(max=20)
	private String luogoNuts;

	@ApiModelProperty(value="Codice individuazione CIG", required = true)
	@Size(max=10)
	private String cig;

	@ApiModelProperty(value="Esente dalla richiesta CUP? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String cupEsente;

	@ApiModelProperty(value="Codice CUP")
	@Size(max=15)
	private String cup;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(hidden = true)
	private String idRup;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Indirizzo/link profilo del committente")
	@Size(max=2000)
	private String urlCommittente;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Indirizzo/link piattaforma telematica")
	@Size(max=2000)
	private String urlPiattaformaTelematica;
	
	@ApiModelProperty(value="CUI progettazione")
	@Size(max=25)
	private String cui;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Somma urgenza? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String sommaUrgenza;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Posa in opera o manodopera? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String manodopera;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Codice intervento")
	@Size(max=25)
	private String codiceIntervento;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Prestazioni comprese nell'appalto (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=PrestazioniComprese)")
	private Long prestazioniComprese;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Contratto escluso ex artt. 19/26 D.Lgs 163/06? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String contrattoEsclusoArt19e26;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Contratto escluso ex artt. 16,17,18 D.Lgs 163/06? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	@Size(max=1)
	private String contrattoEsclusoArt16e17e18;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Modalità acquisizione forniture")
	@Size(min=0)
 	private List<AppaFornEntry> modalitaAcquisizioneForniture;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Tipologie dei lavori")
	@Size(min=0)
 	private List<AppaLavEntry> tipologieLavori;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*[Attributo aggiuntivo, previsto in Simog] Motivazione ricorso a procedura negoziata")
	@Size(min=0)
 	private List<MotivazioneProceduraNegoziataEntry> motivazioniProceduraNegoziata;
	
	@ApiModelProperty(value="Categorie del lotto")
	@Size(min=0)
 	private List<CategoriaLottoEntry> categorie;
	  
	@ApiModelProperty(value="Categorie CPV del lotto")
	@Size(min=0)
	private List<CpvLottoEntry> cpvSecondari;
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value = "*Responsabile unico procedimento", hidden = true)
	private DatiGeneraliTecnicoEntry tecnicoRup;

	@ApiModelProperty(value="*Ex Sottosoglia? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", hidden = true)
	@Size(max=1)
	private String exSottosoglia;
	
	@ApiModelProperty(value="Contratto escluso o rientrante nel regime alleggerito? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	private String contrattoEsclusoAlleggerito;
	
	@ApiModelProperty(value="Esclusione o regime speciale? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)")
	private String esclusioneRegimeSpeciale;
	
	@ApiModelProperty(value="*Cig master multilotto")
	@Size(max=10)
	private String cigMaster;
	
	@ApiModelProperty(value="*Identificativo scheda locale", hidden = true)
	private String idSchedaLocale;
	
	@ApiModelProperty(value="*Identificativo scheda simog", hidden = true)
	private String idSchedaSimog;
	
	public void setSettore(String settore) {
		this.settore = StringUtils.stripToNull(settore);
	}

	public String getSettore() {
		return settore;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = StringUtils.stripToNull(oggetto);
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setIdRup(String idRup) {
		this.idRup = StringUtils.stripToNull(idRup);
	}

	@XmlTransient
	public String getIdRup() {
		return idRup;
	}

	public void setIdSceltaContraente50(Long idSceltaContraente50) {
		this.idSceltaContraente50 = idSceltaContraente50;
	}

	public Long getIdSceltaContraente50() {
		return idSceltaContraente50;
	}

	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}

	@XmlTransient
	public Long getIdGara() {
		return idGara;
	}

	public void setIdLotto(Long idLotto) {
		this.idLotto = idLotto;
	}

	@XmlTransient
	public Long getIdLotto() {
		return idLotto;
	}

	public void setImportoLotto(Double importoLotto) {
		this.importoLotto = importoLotto;
	}

	public Double getImportoLotto() {
		if (importoLotto != null)
			return importoLotto;
		else
			return new Double(0);
	}

	public void setImportoSicurezza(Double importoSicurezza) {
		this.importoSicurezza = importoSicurezza;
	}

	public Double getImportoSicurezza() {
		if (importoSicurezza != null)
			return importoSicurezza;
		else
			return new Double(0);
	}

	public void setImportoTotale(Double importoTotale) {
		this.importoTotale = importoTotale;
	}

	public Double getImportoTotale() {
		return importoTotale;
	}

	public void setCpv(String cpv) {
		this.cpv = StringUtils.stripToNull(cpv);
	}

	public String getCpv() {
		return cpv;
	}

	public void setIdSceltaContraente(Long idSceltaContraente) {
		this.idSceltaContraente = idSceltaContraente;
	}

	public Long getIdSceltaContraente() {
		return idSceltaContraente;
	}

	public void setCategoria(String categoria) {
		this.categoria = StringUtils.stripToNull(categoria);
	}

	public String getCategoria() {
		return categoria;
	}

	public void setNumeroLotto(Long numeroLotto) {
		this.numeroLotto = numeroLotto;
	}

	public Long getNumeroLotto() {
		return numeroLotto;
	}

	public void setClasse(String classe) {
		this.classe = StringUtils.stripToNull(classe);
	}

	public String getClasse() {
		return classe;
	}

	public void setLottoPrecedente(String lottoPrecedente) {
		this.lottoPrecedente = StringUtils.stripToNull(lottoPrecedente);
	}

	public String getLottoPrecedente() {
		return lottoPrecedente;
	}

	public void setMotivo(Long motivo) {
		this.motivo = motivo;
	}

	public Long getMotivo() {
		return motivo;
	}

	public void setCigCollegato(String cigCollegato) {
		this.cigCollegato = StringUtils.stripToNull(cigCollegato);
	}

	public String getCigCollegato() {
		return cigCollegato;
	}

	public void setTipoAppalto(String tipoAppalto) {
		this.tipoAppalto = StringUtils.stripToNull(tipoAppalto);
	}

	public String getTipoAppalto() {
		return tipoAppalto;
	}

	public void setCriterioAggiudicazione(Long criterioAggiudicazione) {
		this.criterioAggiudicazione = criterioAggiudicazione;
	}

	public Long getCriterioAggiudicazione() {
		return criterioAggiudicazione;
	}

	public void setLuogoIstat(String luogoIstat) {
		this.luogoIstat = StringUtils.stripToNull(luogoIstat);
	}

	public String getLuogoIstat() {
		return luogoIstat;
	}

	public void setLuogoNuts(String luogoNuts) {
		this.luogoNuts = StringUtils.stripToNull(luogoNuts);
	}

	public String getLuogoNuts() {
		return luogoNuts;
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

	public void setUrlCommittente(String urlCommittente) {
		this.urlCommittente = StringUtils.stripToNull(urlCommittente);
	}

	public String getUrlCommittente() {
		return urlCommittente;
	}

	public void setUrlPiattaformaTelematica(String urlPiattaformaTelematica) {
		this.urlPiattaformaTelematica = StringUtils.stripToNull(urlPiattaformaTelematica);
	}

	public String getUrlPiattaformaTelematica() {
		return urlPiattaformaTelematica;
	}
	
	public void setCategorie(List<CategoriaLottoEntry> categorie) {
		this.categorie = categorie;
	}

	public List<CategoriaLottoEntry> getCategorie() {
		return categorie;
	}

	public void setCpvSecondari(List<CpvLottoEntry> cpvSecondari) {
		this.cpvSecondari = cpvSecondari;
	}

	public List<CpvLottoEntry> getCpvSecondari() {
		return cpvSecondari;
	}

	public void setTecnicoRup(DatiGeneraliTecnicoEntry tecnicoRup) {
		this.tecnicoRup = tecnicoRup;
	}

	public DatiGeneraliTecnicoEntry getTecnicoRup() {
		return tecnicoRup;
	}

	public void setCupEsente(String cupEsente) {
		this.cupEsente = StringUtils.stripToNull(cupEsente);
	}

	public String getCupEsente() {
		return cupEsente;
	}

	public void setCui(String cui) {
		this.cui = StringUtils.stripToNull(cui);
	}

	public String getCui() {
		return cui;
	}

	public void setSommaUrgenza(String sommaUrgenza) {
		this.sommaUrgenza = StringUtils.stripToNull(sommaUrgenza);
	}

	public String getSommaUrgenza() {
		return sommaUrgenza;
	}

	public void setManodopera(String manodopera) {
		this.manodopera = StringUtils.stripToNull(manodopera);
	}

	public String getManodopera() {
		return manodopera;
	}

	public void setCodiceIntervento(String codiceIntervento) {
		this.codiceIntervento = StringUtils.stripToNull(codiceIntervento);
	}

	public String getCodiceIntervento() {
		return codiceIntervento;
	}

	public void setPrestazioniComprese(Long prestazioniComprese) {
		this.prestazioniComprese = prestazioniComprese;
	}

	public Long getPrestazioniComprese() {
		return prestazioniComprese;
	}

	public void setContrattoEsclusoArt19e26(String contrattoEsclusoArt19e26) {
		this.contrattoEsclusoArt19e26 = StringUtils.stripToNull(contrattoEsclusoArt19e26);
	}

	public String getContrattoEsclusoArt19e26() {
		return contrattoEsclusoArt19e26;
	}

	public void setContrattoEsclusoArt16e17e18(
			String contrattoEsclusoArt16e17e18) {
		this.contrattoEsclusoArt16e17e18 = StringUtils.stripToNull(contrattoEsclusoArt16e17e18);
	}

	public String getContrattoEsclusoArt16e17e18() {
		return contrattoEsclusoArt16e17e18;
	}

	public void setModalitaAcquisizioneForniture(
			List<AppaFornEntry> modalitaAcquisizioneForniture) {
		this.modalitaAcquisizioneForniture = modalitaAcquisizioneForniture;
	}

	public List<AppaFornEntry> getModalitaAcquisizioneForniture() {
		return modalitaAcquisizioneForniture;
	}

	public void setTipologieLavori(List<AppaLavEntry> tipologieLavori) {
		this.tipologieLavori = tipologieLavori;
	}

	public List<AppaLavEntry> getTipologieLavori() {
		return tipologieLavori;
	}

	public void setMotivazioniProceduraNegoziata(
			List<MotivazioneProceduraNegoziataEntry> motivazioniProceduraNegoziata) {
		this.motivazioniProceduraNegoziata = motivazioniProceduraNegoziata;
	}

	public List<MotivazioneProceduraNegoziataEntry> getMotivazioniProceduraNegoziata() {
		return motivazioniProceduraNegoziata;
	}

	public void setExSottosoglia(String exSottosoglia) {
		this.exSottosoglia = StringUtils.stripToNull(exSottosoglia);
	}

	public String getExSottosoglia() {
		return exSottosoglia;
	}

	public String getContrattoEsclusoAlleggerito() {
		return contrattoEsclusoAlleggerito;
	}

	public void setContrattoEsclusoAlleggerito(String contrattoEsclusoAlleggerito) {
		this.contrattoEsclusoAlleggerito = contrattoEsclusoAlleggerito;
	}

	public String getEsclusioneRegimeSpeciale() {
		return esclusioneRegimeSpeciale;
	}

	public void setEsclusioneRegimeSpeciale(String esclusioneRegimeSpeciale) {
		this.esclusioneRegimeSpeciale = esclusioneRegimeSpeciale;
	}

	public void setCigMaster(String cigMaster) {
		this.cigMaster = cigMaster;
	}

	public String getCigMaster() {
		return cigMaster;
	}

	public void setIdSchedaLocale(String idSchedaLocale) {
		this.idSchedaLocale = idSchedaLocale;
	}

	public String getIdSchedaLocale() {
		return idSchedaLocale;
	}

	public void setIdSchedaSimog(String idSchedaSimog) {
		this.idSchedaSimog = idSchedaSimog;
	}

	public String getIdSchedaSimog() {
		return idSchedaSimog;
	}

}
