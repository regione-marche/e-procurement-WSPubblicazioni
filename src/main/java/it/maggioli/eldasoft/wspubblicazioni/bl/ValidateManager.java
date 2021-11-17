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
package it.maggioli.eldasoft.wspubblicazioni.bl;

import it.eldasoft.utils.sicurezza.CriptazioneException;
import it.eldasoft.utils.sicurezza.FactoryCriptazioneByte;
import it.eldasoft.utils.sicurezza.ICriptazioneByte;
import it.eldasoft.utils.utility.UtilityFiscali;
import it.maggioli.eldasoft.tabellati.dao.TabellatiMapper;
import it.maggioli.eldasoft.tabellati.vo.TabellatoEntry;
import it.maggioli.eldasoft.wspubblicazioni.dao.AttiMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.SqlMapper;
import it.maggioli.eldasoft.wspubblicazioni.utils.Costanti;
import it.maggioli.eldasoft.wspubblicazioni.utils.Funzioni;
import it.maggioli.eldasoft.wspubblicazioni.vo.ControlloEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.ImpresaEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.ValidateEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.AllegatoAvvisiEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.DettaglioAvvisoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.PubblicaAvvisoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.AttoGaraEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.DettaglioGaraResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.PubblicaGaraEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.PubblicazioneBandoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.AppaFornEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.AppaLavEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.CategoriaLottoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.CpvLottoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.MotivazioneProceduraNegoziataEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.PubblicaLottoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.AggiudicatarioEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.AllegatoAttiEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.DettaglioAttoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.PubblicaAttoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.TipoAttoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.tecnici.DatiGeneraliTecnicoEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manager per la gestione della business logic.
 *
 * @author Mirco.Franzoni
 */
@Component(value = "validateManager")
public class ValidateManager {

	/** Logger di classe. */
	private Logger             logger = LoggerFactory.getLogger(ValidateManager.class);

	@Autowired
	private TabellatiMapper tabellatiMapper;

	@Autowired
	private SqlMapper sqlMapper;
	
	@Autowired
	private AttiMapper attiMapper;
	/**
	 * @param tabellatiMapper
	 *        tabellatiMapper da settare internamente alla classe.
	 */
	public void setTabellatiMapper(TabellatiMapper tabellatiMapper) {
		this.tabellatiMapper = tabellatiMapper;
	}
  
	/**
	 * @param sqlMapper
	 *            sqlMapper da settare internamente alla classe.
	 */
	public void setSqlMapper(SqlMapper sqlMapper) {
		this.sqlMapper = sqlMapper;
	}
	
	public void setAttiMapper(AttiMapper attiMapper) {
		this.attiMapper = attiMapper;
	}
	
	/**
	 * @return bloccare l'inserimento di gara e lotti se il CIG esiste già nel database di destinazione
	 */
	private boolean bloccareCigSeEsiste() {
		if (Funzioni.bloccaCigSeEsiste == null) {
			String valore = sqlMapper.getConfigValue(Costanti.CONFIG_CODICE_APP,
					Costanti.CONFIG_BLOCCA_CIG_SE_ESISTE);
			if (valore != null && valore.equals("1")) {
				Funzioni.bloccaCigSeEsiste = true;
			} else {
				Funzioni.bloccaCigSeEsiste = false;
			}
		}
		return Funzioni.bloccaCigSeEsiste.booleanValue();
	}
	
	public String getJWTKey() throws CriptazioneException {

		String criptedKey = this.sqlMapper.getConfigValue(Costanti.CONFIG_CODICE_APP,
				Costanti.CONFIG_CHIAVE_APP);
		try {
			ICriptazioneByte decript = FactoryCriptazioneByte.getInstance(
					FactoryCriptazioneByte.CODICE_CRIPTAZIONE_LEGACY, 	 
					criptedKey.getBytes(),
            		ICriptazioneByte.FORMATO_DATO_CIFRATO);
			
			return new String(decript.getDatoNonCifrato());
		} catch (CriptazioneException e) {
			logger.error("Errore in fase di decrypt della chiave hash per generazione token",e);
			throw e;
		}
	}
	
	public void validatePubblicaAvviso(PubblicaAvvisoEntry avviso, List<ValidateEntry> controlli) throws IOException {
		
		if (avviso.getIdRicevuto() != null) {
			//l'avviso è già stato inviato devo verificare se esiste nel DB
			int i = sqlMapper.count("AVVISO WHERE ID_GENERATO=" + avviso.getIdRicevuto());
			if (i == 0){
				ValidateEntry item = new ValidateEntry("idRicevuto", "L'id avviso indicato non esiste nella banca dati");
				controlli.add(item);
			} else {
				//se esiste verifico che idclient sia uguale
				i = sqlMapper.count("AVVISO WHERE ID_GENERATO=" + avviso.getIdRicevuto() + " and ID_CLIENT='" + avviso.getClientId() + "'");
				if (i == 0){
					ValidateEntry item = new ValidateEntry("idRicevuto", "L'id avviso indicato esiste già per un'altra utenza");
					controlli.add(item);
				}
			}
		}
		
		if (avviso.getCodiceSistema() == null) {
			avviso.setCodiceSistema(new Long(1));
		}

		if (avviso.getUfficio() != null && avviso.getUfficio().length()>250) {
			ValidateEntry item = new ValidateEntry("ufficio", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (avviso.getTipologia() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3996", avviso.getTipologia());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("tipologia", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("tipologia", "Valorizzare il campo");
			controlli.add(item);
		}

		/*if (avviso.getData() == null) {
			ValidateEntry item = new ValidateEntry("data", "Valorizzare il campo");
			controlli.add(item);
		}*/

		if (avviso.getDescrizione() != null && avviso.getDescrizione().length()>0) {
			if (avviso.getDescrizione().length()>500) {
				ValidateEntry item = new ValidateEntry("descrizione", "Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("descrizione", "Valorizzare il campo");
			controlli.add(item);
		}

		if (avviso.getCig() != null) {
			int index = Funzioni.controlloCIG(avviso.getCig());
			if (index == -1) {
				ValidateEntry item = new ValidateEntry("Cig", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (avviso.getCup() != null && avviso.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("cup", "Valore non valido");
			controlli.add(item);
		}
		
		if (avviso.getCui() != null && avviso.getCui().length()>20) {
			ValidateEntry item = new ValidateEntry("cui", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (avviso.getIndirizzo() != null && avviso.getIndirizzo().length()>100) {
			ValidateEntry item = new ValidateEntry("indirizzo", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (avviso.getComune() != null && avviso.getComune().length()>32) {
			ValidateEntry item = new ValidateEntry("comune", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (avviso.getProvincia() != null && avviso.getProvincia().length()!=2) {
			ValidateEntry item = new ValidateEntry("provincia", "Il numero di caratteri non coincide");
			controlli.add(item);
		}
		
		if (avviso.getRup() == null) {
			ValidateEntry item = new ValidateEntry("rup", "Valorizzare il campo");
			controlli.add(item);
		} else {
			this.validatePubblicaTecnico(avviso.getRup(), "Rup", controlli);
		}
		
		if (avviso.getCodiceFiscaleSA() != null) {
			//l'avviso è già stato inviato devo verificare se esiste nel DB
			int i = 0;
			if (avviso.getCodiceUnitaOrganizzativa() != null) {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + avviso.getCodiceFiscaleSA() + "' and CODEIN_UO='" + avviso.getCodiceUnitaOrganizzativa() + "'");
			} else {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + avviso.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
			}
			if (i == 0){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "La stazione appaltante indicata non esiste nell'archivio di destinazione");
				controlli.add(item);
			} else if (i > 1){
				ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Esistono piu' stazioni appaltanti con lo stesso codice fiscale e codice unita' organizzativa. Contattare l'amministratore");
				controlli.add(item);
			} else {
				//controllo solo in inserimento
				//se esiste la stazione appaltante e la gara deve essere ancora inserita verifico se c'è l'aasociazione nell'usrein
				//oppure l'tente è "Amministrazione parametri di sistema"
				//ricavo il codice della Stazione appaltante
				String codiceSA = "";
				if (avviso.getCodiceUnitaOrganizzativa() != null) {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + avviso.getCodiceFiscaleSA() + "' and CODEIN_UO='" + avviso.getCodiceUnitaOrganizzativa() + "'");
				} else {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + avviso.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
				}
				int usrEinExist = sqlMapper.count("USR_EIN WHERE SYSCON = " + avviso.getSyscon() + " AND CODEIN = '" + codiceSA + "'");
				int isSuperUser = sqlMapper.count("USRSYS WHERE SYSCON = " + avviso.getSyscon() + " AND syspwbou LIKE '%ou89|%'");
				if (usrEinExist == 0 && isSuperUser == 0) {
					ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Non si dispone delle credenziali per la Stazione Appaltante indicata");
					controlli.add(item);
				}
			}
		} else {
			ValidateEntry item = new ValidateEntry("codiceFiscaleSA", "Valorizzare il campo");
			controlli.add(item);
		}
		
		this.validateWSControlli("PUBBLICA_AVVISO", "AVVISO", avviso, controlli, null);
		
		if (avviso.getDocumenti() == null || avviso.getDocumenti().size() == 0) {
			ValidateEntry item = new ValidateEntry("documenti", "Inserire almeno un documento");
			controlli.add(item);
		} else {
			int i = 1;
			for(AllegatoAvvisiEntry doc:avviso.getDocumenti()) {
				this.validatePubblicaDocumentoAvviso(doc, "documento" + i, controlli);
				i++;
			}
		}
	}
	
	public void validatePubblicaAtto(PubblicaAttoEntry pubblicazione, List<ValidateEntry> controlli) throws IOException {
		
		if (pubblicazione.getIdRicevuto() != null) {
			//la pubblicazione è già stata inviata devo verificare se esiste nel DB
			int i = sqlMapper.count("W9PUBBLICAZIONI WHERE ID_GENERATO=" + pubblicazione.getIdRicevuto());
			if (i == 0){
				ValidateEntry item = new ValidateEntry("idRicevuto", "L'id pubblicazione indicato non esiste nella banca dati");
				controlli.add(item);
			} else {
				//se esiste verifico che faccia riferimento alla stessa gara 
				if (pubblicazione.getGara() != null) {
					if (pubblicazione.getGara().getIdRicevuto() != null) {
						String idGeneratoGara = sqlMapper.executeReturnString("SELECT MAX(ID_GENERATO) FROM W9GARA WHERE CODGARA IN(SELECT CODGARA FROM W9PUBBLICAZIONI WHERE ID_GENERATO = " +  pubblicazione.getIdRicevuto() + ")");
						if (idGeneratoGara == null || !pubblicazione.getGara().getIdRicevuto().equals(new Long(idGeneratoGara))) {
							ValidateEntry item = new ValidateEntry("idRicevuto", "L'id pubblicazione indicato non appartiene a nessuna gara o a una gara diversa da quella inviata");
							controlli.add(item);
						}
					} 
				}
			}
		}
		
		if (pubblicazione.getTipoDocumento() == null) {
			ValidateEntry item = new ValidateEntry("tipoDocumento", "Valorizzare il campo");
			controlli.add(item);
		} else {
			TipoAttoEntry tipoAtto = this.attiMapper.getTipoAtto(pubblicazione.getTipoDocumento());
			//se sto pubblicando un esito verifico se sono stati inviati aggiudicatari
			if (tipoAtto != null && tipoAtto.getTipo() != null && (tipoAtto.getTipo().equals(Costanti.TIPO_ATTO_ESITO) || tipoAtto.getTipo().equals(Costanti.TIPO_ATTO_ESITO_DEFAULT))) {
				//Se è un esito la data e l'importo di aggiudicazione è obbligatorio
				/*if (pubblicazione.getDataAggiudicazione() == null) {
					ValidateEntry item = new ValidateEntry("dataAggiudicazione", "Valorizzare il campo");
					controlli.add(item);
				}
				if (pubblicazione.getImportoAggiudicazione() == null || pubblicazione.getImportoAggiudicazione().equals(new Double(0))) {
					ValidateEntry item = new ValidateEntry("importoAggiudicazione", "Valorizzare il campo e diverso da 0");
					controlli.add(item);
				}*/
				if (pubblicazione.getAggiudicatari() != null && pubblicazione.getAggiudicatari().size() > 0) {
					for(AggiudicatarioEntry aggiudicatario:pubblicazione.getAggiudicatari()) {
						this.validatePubblicaAggiudicatario(aggiudicatario, controlli);
					}
				}
				/*if (pubblicazione.getAggiudicatari() == null || pubblicazione.getAggiudicatari().size() == 0) {
					ValidateEntry item = new ValidateEntry("aggiudicatari", "Inserire almeno un aggiudicatario");
					controlli.add(item);
				} else {
					for(AggiudicatarioEntry aggiudicatario:pubblicazione.getAggiudicatari()) {
						this.validatePubblicaAggiudicatario(aggiudicatario, controlli);
					}
				}*/
			} else if (tipoAtto != null && tipoAtto.getTipo() != null && tipoAtto.getTipo().equals(Costanti.TIPO_ATTO_BANDO)) {
				//SE è un bando la data di scadenza è obbligatoria
				/*if (pubblicazione.getDataScadenza() == null) {
					ValidateEntry item = new ValidateEntry("dataScadenza", "Valorizzare il campo");
					controlli.add(item);
				}*/
			} else if (tipoAtto == null) {
				ValidateEntry item = new ValidateEntry("tipoDocumento", "Valore non valido");
				controlli.add(item);
			}
		}
		
		if (pubblicazione.getEventualeSpecificazione() != null && pubblicazione.getEventualeSpecificazione().length()>100) {
			ValidateEntry item = new ValidateEntry("eventualeSpecificazione", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (pubblicazione.getNumeroProvvedimento() != null && pubblicazione.getNumeroProvvedimento().length()>50) {
			ValidateEntry item = new ValidateEntry("numeroProvvedimento", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (pubblicazione.getNumeroRepertorio() != null && pubblicazione.getNumeroRepertorio().length()>50) {
			ValidateEntry item = new ValidateEntry("numeroRepertorio", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (pubblicazione.getUrlCommittente() != null && pubblicazione.getUrlCommittente().length()>2000) {
			ValidateEntry item = new ValidateEntry("urlCommittente", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (pubblicazione.getUrlEProcurement() != null && pubblicazione.getUrlEProcurement().length()>2000) {
			ValidateEntry item = new ValidateEntry("urlEProcurement", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		/*if (pubblicazione.getDataPubblicazione() == null) {
			ValidateEntry item = new ValidateEntry("dataPubblicazione", "Valorizzare il campo");
			controlli.add(item);
		}*/
		
		this.validateWSControlli("PUBBLICA_ATTO", "W9PUBBLICAZIONI", pubblicazione, controlli, null);
		
		if (pubblicazione.getGara() == null) {
			ValidateEntry item = new ValidateEntry("gara", "Gara obbligatoria");
			controlli.add(item);
		} else {
			pubblicazione.getGara().setClientId(pubblicazione.getClientId());
			this.validatePubblicaGara(pubblicazione.getGara(), controlli);
		}
		
		if (pubblicazione.getDocumenti() == null || pubblicazione.getDocumenti().size() == 0) {
			ValidateEntry item = new ValidateEntry("documenti", "Inserire almeno un documento");
			controlli.add(item);
		} else {
			int i = 1;
			for(AllegatoAttiEntry doc:pubblicazione.getDocumenti()) {
				this.validatePubblicaDocumentoAtto(doc, "documento" + i, controlli);
				i++;
			}
		}
	}
	
	public void validatePubblicaGara(PubblicaGaraEntry gara, List<ValidateEntry> controlli) throws IOException {
		
		if (gara.getIdRicevuto() != null) {
			//la gara è già stato inviata devo verificare se esiste nel DB
			int i = sqlMapper.count("W9GARA WHERE ID_GENERATO=" + gara.getIdRicevuto());
			if (i == 0){
				ValidateEntry item = new ValidateEntry("gara.idRicevuto", "L'id gara indicato non esiste nella banca dati");
				controlli.add(item);
			} else {
				//se esiste verifico che idclient sia uguale
				i = sqlMapper.count("W9GARA WHERE ID_GENERATO=" + gara.getIdRicevuto() + " and ID_CLIENT='" + gara.getClientId() + "'");
				if (i == 0){
					ValidateEntry item = new ValidateEntry("idRicevuto", "L'id gara indicato esiste già per un'altra utenza");
					controlli.add(item);
				}
			}
		} 
		
		if (gara.getOggetto() == null) {
			ValidateEntry item = new ValidateEntry("gara.oggetto", "Valorizzare il campo");
			controlli.add(item);
		}
		else if (gara.getOggetto().length()>1024) {
			ValidateEntry item = new ValidateEntry("gara.oggetto", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (gara.getImportoGara() == null || gara.getImportoGara().equals(new Double(0))) {
			ValidateEntry item = new ValidateEntry("gara.importoGara", "Valorizzare il campo e diverso da 0");
			controlli.add(item);
		} /*else if (gara.getImportoGara() >= 40000) {
			if (gara.getIdAnac() == null) {
				ValidateEntry item = new ValidateEntry("gara.idAnac", "Valorizzare il campo");
				controlli.add(item);
			} 
		}*/
		
		/*if (gara.getIdAnac() == null) {
			ValidateEntry item = new ValidateEntry("gara.idAnac", "Valorizzare il campo");
			controlli.add(item);
		} else if (gara.getIdAnac().length()>20) {
			ValidateEntry item = new ValidateEntry("gara.idAnac", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}*/
		if (gara.getIdAnac() != null && gara.getIdAnac().length()>20) {
			ValidateEntry item = new ValidateEntry("gara.idAnac", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (gara.getSettore() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z08", gara.getSettore());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("gara.settore", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (gara.getModoIndizione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3008", gara.getModoIndizione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("gara.modoIndizione", "Valore non valido");
				controlli.add(item);
			}
		}

		if (gara.getRealizzazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3999", gara.getRealizzazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("gara.realizzazione", "Valore non valido");
				controlli.add(item);
			}
		} 
		
		if (gara.getCodiceFiscaleSA() != null) {
			//l'avviso è già stato inviato devo verificare se esiste nel DB
			int i = 0;
			if (gara.getCodiceUnitaOrganizzativa() != null) {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + gara.getCodiceFiscaleSA() + "' and CODEIN_UO='" + gara.getCodiceUnitaOrganizzativa() + "'");
			} else {
				i = sqlMapper.count("UFFINT WHERE CFEIN='" + gara.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
			}
			if (i == 0){
				ValidateEntry item = new ValidateEntry("gara.codiceFiscaleSA", "La stazione appaltante indicata non esiste nell'archivio di destinazione");
				controlli.add(item);
			} else if (i > 1){
				ValidateEntry item = new ValidateEntry("gara.codiceFiscaleSA", "Esistono piu' stazioni appaltanti con lo stesso codice fiscale e codice unita' organizzativa. Contattare l'amministratore");
				controlli.add(item);
			} else {
				//controllo solo in inserimento
				//se esiste la stazione appaltante e la gara deve essere ancora inserita verifico se c'è l'aasociazione nell'usrein
				//oppure l'tente è "Amministrazione parametri di sistema"
				//ricavo il codice della Stazione appaltante
				String codiceSA = "";
				if (gara.getCodiceUnitaOrganizzativa() != null) {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + gara.getCodiceFiscaleSA() + "' and CODEIN_UO='" + gara.getCodiceUnitaOrganizzativa() + "'");
				} else {
					codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + gara.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
				}
				int usrEinExist = sqlMapper.count("USR_EIN WHERE SYSCON = " + gara.getSyscon() + " AND CODEIN = '" + codiceSA + "'");
				int isSuperUser = sqlMapper.count("USRSYS WHERE SYSCON = " + gara.getSyscon() + " AND syspwbou LIKE '%ou89|%'");
				if (usrEinExist == 0 && isSuperUser == 0) {
					ValidateEntry item = new ValidateEntry("gara.codiceFiscaleSA", "Non si dispone delle credenziali per la Stazione Appaltante indicata");
					controlli.add(item);
				}
			}
		} else {
			ValidateEntry item = new ValidateEntry("gara.codiceFiscaleSA", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (gara.getTecnicoRup() == null) {
			ValidateEntry item = new ValidateEntry("gara.tecnicoRup", "Valorizzare il campo");
			controlli.add(item);
		} else {
			this.validatePubblicaTecnico(gara.getTecnicoRup(), "gara.tecnicoRup", controlli);
		}
		
		if (gara.getCodiceCentroCosto() != null && gara.getCodiceCentroCosto().length()>40) {
			ValidateEntry item = new ValidateEntry("gara.codiceCentroCosto", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (gara.getCentroCosto() != null && gara.getCentroCosto().length()>250) {
			ValidateEntry item = new ValidateEntry("gara.centroCosto", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (gara.getUfficio() != null && gara.getUfficio().length()>250) {
			ValidateEntry item = new ValidateEntry("gara.ufficio", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (gara.getCigAccQuadro() != null && gara.getCigAccQuadro().length()>10) {
			ValidateEntry item = new ValidateEntry("gara.cigAccQuadro", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (gara.getRicostruzioneAlluvione() != null && !gara.getRicostruzioneAlluvione().equals("1") && !gara.getRicostruzioneAlluvione().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.ricostruzioneAlluvione", "Valore non valido");
			controlli.add(item);
		} else if (gara.getRicostruzioneAlluvione() == null) {
			gara.setRicostruzioneAlluvione("2");
		}
		
		if (gara.getIndirizzo() != null && gara.getIndirizzo().length()>100) {
			ValidateEntry item = new ValidateEntry("gara.indirizzo", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (gara.getComune() != null && gara.getComune().length()>32) {
			ValidateEntry item = new ValidateEntry("gara.comune", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (gara.getProvincia() != null && gara.getProvincia().length()!=2) {
			ValidateEntry item = new ValidateEntry("gara.provincia", "Il numero di caratteri non coincide");
			controlli.add(item);
		}

		if (gara.getSaAgente() != null && !gara.getSaAgente().equals("1") && !gara.getSaAgente().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.saAgente", "Valore non valido");
			controlli.add(item);
		} 

		if (gara.getTipoSA() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3001", gara.getTipoSA());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("gara.tipoSA", "Valore non valido");
				controlli.add(item);
			}
		}

		if (gara.getNomeSA() != null && gara.getNomeSA().length()>254) {
			ValidateEntry item = new ValidateEntry("gara.nomeSA", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (gara.getCfAgente() != null && gara.getCfAgente().length()>16) {
			ValidateEntry item = new ValidateEntry("gara.cfAgente", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (gara.getAltreSA() != null && gara.getAltreSA().length()>500) {
			ValidateEntry item = new ValidateEntry("gara.altreSA", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (gara.getTipoProcedura() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3024", gara.getTipoProcedura());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("gara.tipoProcedura", "Valore non valido");
				controlli.add(item);
			}
		}

		if (gara.getCentraleCommittenza() != null && !gara.getCentraleCommittenza().equals("1") && !gara.getCentraleCommittenza().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.centraleCommittenza", "Valore non valido");
			controlli.add(item);
		}

		if (gara.getFunzioniDelegate() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3038", gara.getFunzioniDelegate());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("gara.funzioniDelegate", "Valore non valido");
				controlli.add(item);
			}
		}
		/*if (gara.getSommaUrgenza() != null && !gara.getSommaUrgenza().equals("1") && !gara.getSommaUrgenza().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.sommaUrgenza", "Valore non valido");
			controlli.add(item);
		}*/
		
		this.validateWSControlli("PUBBLICA_ATTO", "W9GARA", gara, controlli, null);
		
		//validate pubblicazione bando
		if (gara.getPubblicazioneBando() != null) {
			this.validatePubblicazioneBando(gara.getPubblicazioneBando(), controlli);
		}
		
		if (gara.getLotti() == null || gara.getLotti().size() == 0) {
			ValidateEntry item = new ValidateEntry("gara.lotti", "Inserire almeno un lotto");
			controlli.add(item);
		} else {
			//verifico se ci sono lotti con lo stesso CIG
			boolean checkCIG = true;
			//se idRicevuto è vuoto verifico che la gara non sia già presente perchè creata manualmente o importata da simog
			if (gara.getCodiceFiscaleSA() != null) {
				for(PubblicaLottoEntry lotto:gara.getLotti()) {
					if (lotto.getCig() != null && !lotto.getCig().equals("")) {
						//verifico che non ci sia già il CIG presente nel DB
						int occorrenze = sqlMapper.count("W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
						if (occorrenze > 0) {
							//se il cig già esiste verifico che la gara che lo contiene abbia la stessa SA e che l'id_generato e idClient siano vuoti
							Integer i = this.sqlMapper.execute("SELECT MAX(CODGARA) FROM W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
							String cfein = this.sqlMapper.executeReturnString("SELECT UFFINT.CFEIN FROM W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN WHERE CODGARA=" + i);
							String codiceUnitaOrganizzativa = this.sqlMapper.executeReturnString("SELECT UFFINT.CODEIN_UO FROM W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN WHERE CODGARA=" + i);
							if (codiceUnitaOrganizzativa != null) {
								codiceUnitaOrganizzativa = codiceUnitaOrganizzativa.toUpperCase();
							}
							if (cfein.toUpperCase().equals(gara.getCodiceFiscaleSA().toUpperCase()) && ((codiceUnitaOrganizzativa == null && gara.getCodiceUnitaOrganizzativa() == null) || (codiceUnitaOrganizzativa != null && gara.getCodiceUnitaOrganizzativa() != null && codiceUnitaOrganizzativa.equals(gara.getCodiceUnitaOrganizzativa())))) {
								//se la stazione appaltante è la stessa
								String idClient = this.sqlMapper.executeReturnString("SELECT ID_CLIENT FROM W9GARA WHERE CODGARA=" + i);
								if (idClient == null || idClient.equals("")) {
									Integer idGenerato = this.sqlMapper.execute("SELECT ID_GENERATO FROM W9GARA WHERE CODGARA =" + i);
									if (idGenerato == null) {
										checkCIG = false;
										break;
									}
								}
							}
						}
					}
				}
			}
			
			List<String> cigLotti = new ArrayList<String>();
			for(PubblicaLottoEntry lotto:gara.getLotti()) {
				if (lotto.getCig() != null && !lotto.getCig().equals("")) {
					//se la gara è nuova verifico che non ci sia già il CIG presente nel DB
					if (this.bloccareCigSeEsiste() && checkCIG && gara.getIdRicevuto() == null) {
						int occorrenze = sqlMapper.count("W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
						if (occorrenze > 0) {
							ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".Cig", "Il codice CIG indicato e' gia' presente nell'anagrafica");
							controlli.add(item);
						}
					}
					if (cigLotti.contains(lotto.getCig())) {
						ValidateEntry item = new ValidateEntry("gara.lotti", "Esistono lotti con lo stesso CIG: " + lotto.getCig());
						controlli.add(item);
					} else {
						cigLotti.add(lotto.getCig());
					}
				}
				this.validatePubblicaLotto(lotto, controlli);
			}
		}
	}

	private void validatePubblicazioneBando(PubblicazioneBandoEntry bando, List<ValidateEntry> controlli) throws IOException {
		
		if (bando.getProfiloCommittente() != null && !bando.getProfiloCommittente().equals("1") && !bando.getProfiloCommittente().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.pubblicazioneBando.profiloCommittente", "Valore non valido");
			controlli.add(item);
		} 
		
		if (bando.getProfiloInfTrasp() != null && !bando.getProfiloInfTrasp().equals("1") && !bando.getProfiloInfTrasp().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.pubblicazioneBando.sitoInformaticoMinisteroInfrastrutture", "Valore non valido");
			controlli.add(item);
		} 
		
		if (bando.getProfiloOsservatorio() != null && !bando.getProfiloOsservatorio().equals("1") && !bando.getProfiloOsservatorio().equals("2")) {
			ValidateEntry item = new ValidateEntry("gara.pubblicazioneBando.profiloOsservatorio", "Valore non valido");
			controlli.add(item);
		} 
	}

	private void validatePubblicaLotto(PubblicaLottoEntry lotto, List<ValidateEntry> controlli) throws IOException {
		
		if (lotto.getCig() == null) {
			ValidateEntry item = new ValidateEntry("lotto.Cig", "Valorizzare il campo");
			controlli.add(item);
			return;
		} else {
			int index = Funzioni.controlloCIG(lotto.getCig());
			if (index == -1) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".Cig", "Valore non valido");
				controlli.add(item);
			}
		} 
		
		if (lotto.getOggetto() == null) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".oggetto", "Valorizzare il campo");
			controlli.add(item);
		}
		else if (lotto.getOggetto().length()>1024) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".oggetto", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (lotto.getImportoLotto() == null || lotto.getImportoLotto().equals(new Double(0))) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".importoLotto", "Valorizzare il campo e diverso da 0");
			controlli.add(item);
		}

		if (lotto.getCpv() != null) {
			int i = sqlMapper.count("TABCPV WHERE CPVCOD='S2020' AND CPVCOD4='" + lotto.getCpv() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".cpv", "Valore non valido");
				controlli.add(item);
			} 
		} 

		if (lotto.getIdSceltaContraente() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3005", lotto.getIdSceltaContraente());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".idSceltaContraente", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (lotto.getIdSceltaContraente50() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W9020", lotto.getIdSceltaContraente50());
			if (riga == null) {
				riga = this.tabellatiMapper.getValore1("W3005", lotto.getIdSceltaContraente50());
				if (riga == null) {
					ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".idSceltaContraente50", "Valore non valido");
					controlli.add(item);
				}
			}
		} 

		if (lotto.getCategoria() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z03", lotto.getCategoria());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".categoria", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (lotto.getClasse() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z11", lotto.getClasse());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".classe", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (lotto.getLottoPrecedente() != null && !lotto.getLottoPrecedente().equals("1") && !lotto.getLottoPrecedente().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".lottoPrecedente", "Valore non valido");
			controlli.add(item);
		}

		if (lotto.getMotivo() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3997", lotto.getMotivo());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".motivo", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (lotto.getCigCollegato() != null && lotto.getCigCollegato().length()>10) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".cigCollegato", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}

		if (lotto.getTipoAppalto() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z05", lotto.getTipoAppalto());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".tipoAppalto", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".tipoAppalto", "Valorizzare il campo");
			controlli.add(item);
		}

		if (lotto.getSettore() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z08", lotto.getSettore());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".settore", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (lotto.getCriterioAggiudicazione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3007", lotto.getCriterioAggiudicazione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".criterioAggiudicazione", "Valore non valido");
				controlli.add(item);
			}
		} 

		if (lotto.getLuogoIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + lotto.getLuogoIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".luogoIstat", "Valore non valido");
				controlli.add(item);
			} 
		}

		if (lotto.getLuogoNuts()  != null) {
			int i = sqlMapper.count("TABNUTS WHERE CODICE='" + lotto.getLuogoNuts() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".luogoNuts", "Valore non valido");
				controlli.add(item);
			} 
		}

		/*if (lotto.getCupEsente() == null) {
			ValidateEntry item = new ValidateEntry("lotto.cupEsente", "Valorizzare il campo");
			controlli.add(item);
		} else if (!lotto.getCupEsente().equals("1") && !lotto.getCupEsente().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto.cupEsente", "Valore non valido");
			controlli.add(item);
		} else if (lotto.getCupEsente().equals("2")) {
			if (lotto.getCup() == null) {
				ValidateEntry item = new ValidateEntry("lotto.cup", "Valorizzare il campo");
				controlli.add(item);
			}
		}*/

		if (lotto.getCupEsente()!= null && !lotto.getCupEsente().equals("1") && !lotto.getCupEsente().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".cupEsente", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getCup() != null && lotto.getCup().length()!=15) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".cup", "Valore non valido");
			controlli.add(item);
		}

		if (lotto.getCui() != null && lotto.getCui().length()>25) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".cui", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getSommaUrgenza() != null && !lotto.getSommaUrgenza().equals("1") && !lotto.getSommaUrgenza().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".sommaUrgenza", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getManodopera() != null && !lotto.getManodopera().equals("1") && !lotto.getManodopera().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".manodopera", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getCodiceIntervento() != null && lotto.getCodiceIntervento().length()>25) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".codiceIntervento", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getPrestazioniComprese() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3003", lotto.getPrestazioniComprese());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".prestazioniComprese", "Valore non valido");
				controlli.add(item);
			}
		} 
		
		if (lotto.getContrattoEsclusoArt19e26() != null && !lotto.getContrattoEsclusoArt19e26().equals("1") && !lotto.getContrattoEsclusoArt19e26().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".contrattoEsclusoArt19e26", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getContrattoEsclusoArt16e17e18() != null && !lotto.getContrattoEsclusoArt16e17e18().equals("1") && !lotto.getContrattoEsclusoArt16e17e18().equals("2")) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".contrattoEsclusoArt16e17e18", "Valore non valido");
			controlli.add(item);
		}
		
		if (lotto.getTecnicoRup() != null) {
			this.validatePubblicaTecnico(lotto.getTecnicoRup(), "lotto " + lotto.getCig() + ".TecnicoRup", controlli);
		} /*else {
			ValidateEntry item = new ValidateEntry("lotto.tecnicoRup", "Valorizzare il campo");
			controlli.add(item);
		}*/
		
		if (lotto.getCigMaster() != null && lotto.getCigMaster().length()>10) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".cigMaster", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (lotto.getIdSchedaLocale() != null && lotto.getIdSchedaLocale().length()>50) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".idSchedaLocale", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (lotto.getIdSchedaSimog() != null && lotto.getIdSchedaSimog().length()>50) {
			ValidateEntry item = new ValidateEntry("lotto " + lotto.getCig() + ".idSchedaSimog", "Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		this.validateWSControlli("PUBBLICA_ATTO", "W9LOTT", lotto, controlli, null);
		
		
		if (lotto.getCpvSecondari() != null && lotto.getCpvSecondari().size() > 0) {
			for(CpvLottoEntry cpvSecondario:lotto.getCpvSecondari()) {
				this.validateCpvLotto(cpvSecondario, "lotto " + lotto.getCig(), controlli);
			}
		}
		
		if (lotto.getCategorie() != null && lotto.getCategorie().size() > 0) {
			for(CategoriaLottoEntry categoriaSecondaria:lotto.getCategorie()) {
				this.validateCategoriaLotto(categoriaSecondaria, "lotto " + lotto.getCig(), controlli);
			}
		}
		
		if (lotto.getModalitaAcquisizioneForniture() != null && lotto.getModalitaAcquisizioneForniture().size() > 0) {
			for(AppaFornEntry modalita:lotto.getModalitaAcquisizioneForniture()) {
				this.validateModalitaAcquisizioneForniture(modalita, "lotto " + lotto.getCig(), controlli);
			}
		}
		
		if (lotto.getTipologieLavori() != null && lotto.getTipologieLavori().size() > 0) {
			for(AppaLavEntry tipologia:lotto.getTipologieLavori()) {
				this.validateTipologiaLavoro(tipologia, "lotto " + lotto.getCig(), controlli);
			}
		}
		
		if (lotto.getMotivazioniProceduraNegoziata() != null && lotto.getMotivazioniProceduraNegoziata().size() > 0) {
			for(MotivazioneProceduraNegoziataEntry condizione:lotto.getMotivazioniProceduraNegoziata()) {
				this.validateCondizione(condizione, "lotto " + lotto.getCig(), controlli);
			}
		}
	}
	
	private void validateCondizione(MotivazioneProceduraNegoziataEntry condizione, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (condizione.getCondizione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3006", condizione.getCondizione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry(riferimento + ".motivazioniProceduraNegoziata.condizione", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry(riferimento + ".motivazioniProceduraNegoziata.condizione", "Valorizzare il campo");
			controlli.add(item);
		}
	}

	private void validateTipologiaLavoro(AppaLavEntry tipologia, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (tipologia.getTipologiaLavoro() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3002", tipologia.getTipologiaLavoro());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry(riferimento + ".tipologieLavori.tipologiaLavoro", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry(riferimento + ".tipologieLavori.tipologiaLavoro", "Valorizzare il campo");
			controlli.add(item);
		}
	}

	private void validateModalitaAcquisizioneForniture(AppaFornEntry modalita, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (modalita.getModalitaAcquisizione() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3019", modalita.getModalitaAcquisizione());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry(riferimento + ".modalitaAcquisizioneForniture.modalitaAcquisizione", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry(riferimento + ".modalitaAcquisizioneForniture.modalitaAcquisizione", "Valorizzare il campo");
			controlli.add(item);
		}
	}

	private void validateCpvLotto(CpvLottoEntry cpv, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (cpv.getCpv() != null) {
			int i = sqlMapper.count("TABCPV WHERE CPVCOD='S2020' AND CPVCOD4='" + cpv.getCpv() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry(riferimento + ".cpvSecondario.cpv", "Valore non valido");
				controlli.add(item);
			} 
		} else {
			ValidateEntry item = new ValidateEntry(riferimento + ".cpvSecondario.cpv", "Valorizzare il campo");
			controlli.add(item);
		}

	}

	private void validateCategoriaLotto(CategoriaLottoEntry categoria, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (categoria.getCategoria() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z03", categoria.getCategoria());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.categoria", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.categoria", "Valorizzare il campo");
			controlli.add(item);
		}

		if (categoria.getClasse() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore2("W3z11", categoria.getClasse());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.classe", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.classe", "Valorizzare il campo");
			controlli.add(item);
		}

		if (categoria.getScorporabile() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.scorporabile", "Valorizzare il campo");
			controlli.add(item);
		} else if (!categoria.getScorporabile().equals("1") && !categoria.getScorporabile().equals("2")) {
			ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.scorporabile", "Valore non valido");
			controlli.add(item);
		}
		
		if (categoria.getSubappaltabile() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.subappaltabile", "Valorizzare il campo");
			controlli.add(item);
		} else if (!categoria.getSubappaltabile().equals("1") && !categoria.getSubappaltabile().equals("2")) {
			ValidateEntry item = new ValidateEntry(riferimento + ".categoriaSecondaria.subappaltabile", "Valore non valido");
			controlli.add(item);
		}
		
	}

	private void validatePubblicaTecnico(DatiGeneraliTecnicoEntry tecnico, String riferimento, List<ValidateEntry> controlli) throws IOException {
		if (tecnico.getCognome() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cognome", "Valorizzare il campo");
			controlli.add(item);
		} else if (tecnico.getCognome().length() > 80) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cognome",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (tecnico.getNome() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".nome", "Valorizzare il campo");
			controlli.add(item);
		} else if (tecnico.getNome().length() > 80) {
			ValidateEntry item = new ValidateEntry(riferimento + ".nome",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (tecnico.getNomeCognome() == null) {
			tecnico.setNomeCognome(tecnico.getNome() + " " + tecnico.getCognome());
		}
		if (tecnico.getCfPiva() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cfPiva", "Valorizzare il campo");
			controlli.add(item);
		} else if (!(UtilityFiscali.isValidCodiceFiscale(tecnico.getCfPiva()) || UtilityFiscali
						.isValidPartitaIVA(tecnico.getCfPiva()))) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cfPiva",
					"Il dato non e' un codice fiscale o una partita iva valida");
			controlli.add(item);
		}
		if(tecnico.getIndirizzo() != null && tecnico.getIndirizzo().length()>60) {
			ValidateEntry item = new ValidateEntry(riferimento + ".indirizzo",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if(tecnico.getCivico() != null && tecnico.getCivico().length()>10) {
			ValidateEntry item = new ValidateEntry(riferimento + ".civico",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if(tecnico.getCap() != null && tecnico.getCap().length()>5) {
			ValidateEntry item = new ValidateEntry(riferimento + ".cap",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if (tecnico.getLuogoIstat() != null) {
			int i = sqlMapper.count("TABSCHE WHERE TABCOD='S2003' AND TABCOD1='09' AND TABCOD3='" + tecnico.getLuogoIstat() + "'");
			if (i == 0){
				ValidateEntry item = new ValidateEntry(riferimento + ".luogoIstat", "Valore non valido");
				controlli.add(item);
			} 
		}
		if(tecnico.getProvincia() != null && tecnico.getProvincia().length()>2) {
			ValidateEntry item = new ValidateEntry(riferimento + ".provincia",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
	}
	
	private void validatePubblicaDocumentoAvviso(AllegatoAvvisiEntry documento, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (documento.getTitolo() == null) {
			  ValidateEntry item = new ValidateEntry(riferimento + ".Titolo", "Valorizzare il campo");
			  controlli.add(item);
		  }
		  else if (documento.getTitolo().length()>250) {
		  		ValidateEntry item = new ValidateEntry(riferimento + ".Titolo", "Il numero di caratteri eccede la lunghezza massima");
		  		controlli.add(item);
		  } 
		  
		  if (documento.getUrl() != null && documento.getUrl().length()>2000) {
		  		ValidateEntry item = new ValidateEntry(riferimento + ".Url", "Il numero di caratteri eccede la lunghezza massima");
		  		controlli.add(item);
		  } 
		  
		  if ((documento.getFile() != null && documento.getUrl() != null) || (documento.getFile() == null && documento.getUrl() == null)) {
			  //ValidateEntry item = new ValidateEntry("file", "Selezionare il file o la url");
			  //controlli.add(item);
			  ;
		  }
		  
		  if (documento.getTipoFile() != null && documento.getTipoFile().length()>20) {
		  		ValidateEntry item = new ValidateEntry(riferimento + ".TipoFile", "Il numero di caratteri eccede la lunghezza massima");
		  		controlli.add(item);
		  } 
		  this.validateWSControlli("PUBBLICA_AVVISO", "W9DOCAVVISO", documento, controlli, riferimento);
	}
	
	private void validatePubblicaDocumentoAtto(AllegatoAttiEntry documento, String riferimento, List<ValidateEntry> controlli) throws IOException {
		
		if (documento.getTitolo() == null) {
			  ValidateEntry item = new ValidateEntry(riferimento + ".Titolo", "Valorizzare il campo");
			  controlli.add(item);
		  }
		  else if (documento.getTitolo().length()>250) {
		  		ValidateEntry item = new ValidateEntry(riferimento + ".Titolo", "Il numero di caratteri eccede la lunghezza massima");
		  		controlli.add(item);
		  } 
		  
		  if (documento.getUrl() != null && documento.getUrl().length()>2000) {
		  		ValidateEntry item = new ValidateEntry(riferimento + ".Url", "Il numero di caratteri eccede la lunghezza massima");
		  		controlli.add(item);
		  } 
		  
		  if ((documento.getFile() != null && documento.getUrl() != null) || (documento.getFile() == null && documento.getUrl() == null)) {
			  //ValidateEntry item = new ValidateEntry("file", "Selezionare il file o la url");
			  //controlli.add(item);
			  ;
		  }
		  
		  if (documento.getTipoFile() != null && documento.getTipoFile().length()>20) {
		  		ValidateEntry item = new ValidateEntry(riferimento + ".TipoFile", "Il numero di caratteri eccede la lunghezza massima");
		  		controlli.add(item);
		  } 
		  
		  this.validateWSControlli("PUBBLICA_ATTO", "W9DOCGARA", documento, controlli, riferimento);
			
	}
	
	private void validatePubblicaAggiudicatario(AggiudicatarioEntry aggiudicatario, List<ValidateEntry> controlli) throws IOException {
		
		if (aggiudicatario.getTipoAggiudicatario() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3010", aggiudicatario.getTipoAggiudicatario());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("aggiudicatario.tipoAggiudicatario", "Valore non valido");
				controlli.add(item);
			}
		} else {
			ValidateEntry item = new ValidateEntry("aggiudicatario.tipoAggiudicatario", "Valorizzare il campo");
			controlli.add(item);
		}
		
		if (aggiudicatario.getRuolo() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("W3011", aggiudicatario.getRuolo());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry("aggiudicatario.ruolo", "Valore non valido");
				controlli.add(item);
			}
		} /*else {
			ValidateEntry item = new ValidateEntry("aggiudicatario.ruolo", "Valorizzare il campo");
			controlli.add(item);
		}*/
		
		this.validateWSControlli("PUBBLICA_ATTO", "ESITI_AGGIUDICATARI", aggiudicatario, controlli, null);
		
		if (aggiudicatario.getImpresa() == null) {
			ValidateEntry item = new ValidateEntry("aggiudicatario.impresa", "Valorizzare il campo");
			controlli.add(item);
		} else {
			this.validatePubblicaImpresa(aggiudicatario.getImpresa(), "aggiudicatario.impresa", controlli);
		}
	}
	
	private void validatePubblicaImpresa(ImpresaEntry impresa, String riferimento, List<ValidateEntry> controlli) throws IOException {
		if (impresa.getRagioneSociale() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".ragioneSociale", "Valorizzare il campo");
			controlli.add(item);
		} else if (impresa.getRagioneSociale().length() > 61) {
			ValidateEntry item = new ValidateEntry(riferimento + ".ragioneSociale",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		//decodifico se è valorizzata il codice Nazione dell'impresa
		if (impresa.getNazione() != null) {
			String codiceNazione = this.sqlMapper.executeReturnString("SELECT TAB2D1 FROM TAB2 WHERE TAB2COD='W3z12' AND UPPER(TAB2TIP)='" + impresa.getNazione().toUpperCase() + "'");
			if (codiceNazione != null && codiceNazione.length()>0) {
				impresa.setCodiceNazione(new Long(codiceNazione));
			}
		} else if (impresa.getCodiceNazione() != null) {
			String nazione = this.sqlMapper.executeReturnString("SELECT TAB2TIP FROM TAB2 WHERE TAB2COD='W3z12' AND TAB2D1='" + impresa.getCodiceNazione() + "'");
			if (nazione != null && nazione.length()>0) {
				impresa.setNazione(nazione);
			}
		}
		
		if (impresa.getFormaGiuridica() != null) {
			TabellatoEntry riga = this.tabellatiMapper.getValore1("G_043", impresa.getFormaGiuridica());
			if (riga == null) {
				ValidateEntry item = new ValidateEntry(riferimento + ".formaGiuridica", "Valore non valido");
				controlli.add(item);
			}
		}
		if (impresa.getCodiceFiscale() == null) {
			ValidateEntry item = new ValidateEntry(riferimento + ".codiceFiscale", "Valorizzare il campo");
			controlli.add(item);
		} else if (impresa.getNazione() == null || impresa.getNazione().toUpperCase().equals("IT")) {
			if (!(UtilityFiscali.isValidCodiceFiscale(impresa.getCodiceFiscale()) || UtilityFiscali
					.isValidPartitaIVA(impresa.getCodiceFiscale()))) {
				ValidateEntry item = new ValidateEntry(riferimento + ".codiceFiscale",
				"Il dato non e' un codice fiscale o una partita iva valida");
				controlli.add(item);
			}
		} 
		else if (impresa.getCodiceFiscale().length() > 16) {
			ValidateEntry item = new ValidateEntry(riferimento + ".codiceFiscale",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		if(impresa.getProvincia() != null && impresa.getProvincia().length()>2) {
			ValidateEntry item = new ValidateEntry(riferimento + ".provincia",
			"Il numero di caratteri eccede la lunghezza massima");
			controlli.add(item);
		}
		
		if (impresa.getPartitaIva() != null) {
			if (impresa.getNazione() == null || impresa.getNazione().toUpperCase().equals("IT")) {
				if (!(UtilityFiscali.isValidCodiceFiscale(impresa.getPartitaIva()) || UtilityFiscali
						.isValidPartitaIVA(impresa.getPartitaIva()))) {
					ValidateEntry item = new ValidateEntry(riferimento + ".partitaIva",
					"Il dato non e' un codice fiscale o una partita iva valida");
					controlli.add(item);
				}
			} else if (impresa.getPartitaIva().length()>16) {
				ValidateEntry item = new ValidateEntry(riferimento + ".partitaIva",
				"Il numero di caratteri eccede la lunghezza massima");
				controlli.add(item);
			}
		}
		
	}
	
	public String verificaIdRicevutoAtti(Long idRicevuto) throws Exception {
		String error = "";
		int i = sqlMapper.count("W9PUBBLICAZIONI WHERE ID_GENERATO=" + idRicevuto);
		if (i == 0){
			//l'atto richiesto non esiste
			error = DettaglioAttoResult.ERROR_NOT_FOUND;
		} 
		return error;
	}

	public String verificaIdRicevutoGara(Long idRicevuto) throws Exception {
		String error = "";
		int i = sqlMapper.count("W9GARA WHERE ID_GENERATO=" + idRicevuto);
		if (i == 0){
			//la gara richiesta non esiste
			error = DettaglioGaraResult.ERROR_NOT_FOUND;
		} 
		return error;
	}
	
	public String verificaIdRicevutoAvviso(Long idRicevuto) throws Exception {
		String error = "";
		int i = sqlMapper.count("AVVISO WHERE ID_GENERATO=" + idRicevuto);
		if (i == 0){
			//l'avviso richiesto non esiste
			error = DettaglioAvvisoResult.ERROR_NOT_FOUND;
		} 
		return error;
	}
	
	private void validateWSControlli(String codFunzione, String entita, Object json, List<ValidateEntry> controlli, String riferimento)
	throws IOException {
		PubblicaAttoEntry atto = null;
		PubblicaAvvisoEntry avviso = null;
		PubblicaGaraEntry gara = null;
		PubblicaLottoEntry lotto = null;
		AggiudicatarioEntry aggiudicatario = null;
		AllegatoAttiEntry allegatoAtti = null;
		AllegatoAvvisiEntry allegatoAvvisi = null;
		String oggetto = "";
		if(codFunzione.equals("PUBBLICA_ATTO")) {
			if(entita.equals("W9PUBBLICAZIONI")) {
				atto = (PubblicaAttoEntry)json;
			} else if(entita.equals("W9GARA")) {
				gara = (PubblicaGaraEntry)json;
				oggetto = "gara.";
			} else if(entita.equals("W9LOTT")) {
				lotto = (PubblicaLottoEntry)json;
				oggetto = "lotto " + lotto.getCig() + ".";
			} else if(entita.equals("ESITI_AGGIUDICATARI")) {
				aggiudicatario = (AggiudicatarioEntry)json;
				oggetto = "aggiudicatario.";
			} else if(entita.equals("W9DOCGARA")) {
				allegatoAtti = (AllegatoAttiEntry)json;
				oggetto = "documento.";
			} 
		} else if(codFunzione.equals("PUBBLICA_AVVISO")) {
			if(entita.equals("AVVISO")) {
				avviso = (PubblicaAvvisoEntry)json;
			} else if(entita.equals("W9DOCAVVISO")) {
				allegatoAvvisi = (AllegatoAvvisiEntry)json;
				oggetto = "documento.";
			} 
		}
		if (riferimento != null) {
			oggetto = riferimento + ".";
		}
		List<ControlloEntry> controlliDaFare = sqlMapper.getControlli(codFunzione, entita);
		if(codFunzione.equals("PUBBLICA_ATTO")) {
			for(ControlloEntry controllo:controlliDaFare) {
				boolean condizioneVerificata = false;
				switch (controllo.getNumero().intValue()) {
				case 10:
					//dataPubblicazione
					if (atto.getDataPubblicazione() == null && 
							(new Long(2).equals(atto.getTipoDocumento()) ||
							new Long(3).equals(atto.getTipoDocumento()) ||
							new Long(17).equals(atto.getTipoDocumento()) ||
							new Long(19).equals(atto.getTipoDocumento()) ||
							new Long(20).equals(atto.getTipoDocumento()) ||
							new Long(27).equals(atto.getTipoDocumento()))) {
						condizioneVerificata = true;
					}
					break;
				case 11:
					//dataPubblicazione
					if (atto.getDataPubblicazione() != null && 
							atto.getDataPubblicazione().compareTo(new Date()) > 0) {
						condizioneVerificata = true;
					}
					break;
				case 20:
					//dataScadenza
					if (atto.getDataScadenza() == null && 
							(new Long(2).equals(atto.getTipoDocumento()) ||
							new Long(3).equals(atto.getTipoDocumento()) ||
							new Long(27).equals(atto.getTipoDocumento()))) {
						condizioneVerificata = true;
					}
					break;
				case 21:
					//dataScadenza
					if (atto.getDataScadenza() != null && atto.getDataPubblicazione() != null && 
							atto.getDataScadenza().compareTo(atto.getDataPubblicazione()) < 0) {
						condizioneVerificata = true;
					}
					break;
				case 22:
					//dataProvvedimento
					if (atto.getDataProvvedimento() != null && 
							atto.getDataProvvedimento().compareTo(new Date()) > 0) {
						condizioneVerificata = true;
					}
					break;
				case 30:
					//Aggiudicatari
					if ((atto.getAggiudicatari() == null || atto.getAggiudicatari().size() == 0) &&
							(new Long(19).equals(atto.getTipoDocumento()) ||
							new Long(20).equals(atto.getTipoDocumento()))) {
						condizioneVerificata = true;
					}
					break;
				case 31:
					//Check esito
					//Negli atti, dovrà essere possibile impedire la pubblicazione di un esito (atto con W9CF_PUBB.TIPO= 'E') se la procedura è aperta o ristretta (W9LOTT.ID_SCELTA_CONTRAENTE in (1,2)) e non è stato pubblicato il bando (atto con W9CF_PUBB.TIPO= 'B')
					TipoAttoEntry tipoAtto = this.attiMapper.getTipoAtto(atto.getTipoDocumento());
					//se sto pubblicando un esito verifico se la procedura dei lotti interessati è aperta o ristretta
					if (tipoAtto != null && tipoAtto.getTipo() != null && (tipoAtto.getTipo().equals(Costanti.TIPO_ATTO_ESITO) || tipoAtto.getTipo().equals(Costanti.TIPO_ATTO_ESITO_DEFAULT))) {
						if (atto.getGara() != null) {
							if (atto.getGara().getLotti() != null) {
								for(PubblicaLottoEntry lottoEsito:atto.getGara().getLotti()) {
									if ((lottoEsito.getIdSceltaContraente() != null && (lottoEsito.getIdSceltaContraente().equals(new Long(1)) || lottoEsito.getIdSceltaContraente().equals(new Long(2)))) ||
									(lottoEsito.getIdSceltaContraente50() != null && (lottoEsito.getIdSceltaContraente50().equals(new Long(1)) || lottoEsito.getIdSceltaContraente50().equals(new Long(2))))) {
										if (atto.getGara().getIdRicevuto() == null) {
											//la gara non esiste, segnalo l'errore
											condizioneVerificata = true;
										} else {
											//ricavo il codice gara dall'id_ricevuto
											Integer i = this.sqlMapper.execute("SELECT MAX(CODGARA) FROM W9GARA WHERE ID_GENERATO=" + atto.getGara().getIdRicevuto());
											if (i != null) {
												List<AttoGaraEntry> atti = this.attiMapper.getAttiGara(new Long(i));
												boolean bandoExist = false;
												for(AttoGaraEntry itemAtto:atti) {
													tipoAtto = this.attiMapper.getTipoAtto(itemAtto.getTipoDocumento());
													if (tipoAtto != null && tipoAtto.getTipo() != null && tipoAtto.getTipo().equals(Costanti.TIPO_ATTO_BANDO)) {
														bandoExist = true;
													}
												}
												if (!bandoExist) {
													condizioneVerificata = true;
												}
											}
										}
										break;
									}
								}
							}
						}
					}
					break;					
				case 40:
					//settore
					if (gara.getSettore() == null) {
						condizioneVerificata = true;
					}
					break;
				case 41:
					//idAnac
					if (gara.getIdAnac() == null) {
						//verifico se si tratta di lotti con cig o smartcig
						if (gara.getLotti() != null && gara.getLotti().size()>0) {
							if (gara.getLotti().get(0).getCig() != null) {
								int index = Funzioni.controlloCIG(gara.getLotti().get(0).getCig());
								if (index == 0) {
									condizioneVerificata = true;
								} else if (index == 1) {
									gara.setIdAnac("0");
								}
							}
						}
					}
					break;
				case 50:
					//realizzazione
					if (gara.getRealizzazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 60:
					//numeroLotto
					if (lotto.getNumeroLotto() == null) {
						condizioneVerificata = true;
					}
					break;
				case 70:
					//cpv
					if (lotto.getCpv() == null) {
						condizioneVerificata = true;
					}
					break;
				case 80:
					//idSceltaContraente50
					if (lotto.getIdSceltaContraente50() == null && lotto.getIdSceltaContraente() == null) {
						condizioneVerificata = true;
					} else if (lotto.getIdSceltaContraente50() != null && lotto.getIdSceltaContraente() == null) {
						lotto.setIdSceltaContraente(lotto.getIdSceltaContraente50());
					}
					break;
				case 90:
					//categoria
					if (lotto.getCategoria() == null) {
						condizioneVerificata = true;
					}
					break;
				case 100:
					//classe
					if (lotto.getClasse() == null && "L".equals(lotto.getTipoAppalto())) {
						condizioneVerificata = true;
					}
					break;
				case 110:
					//criterioAggiudicazione
					if (lotto.getCriterioAggiudicazione() == null) {
						condizioneVerificata = true;
					}
					break;
				case 120:
					//Istat o Nuts
					if (lotto.getLuogoIstat() == null && lotto.getLuogoNuts() == null) {
						condizioneVerificata = true;
					}
					break;
				case 130:
					//cupEsente
					if (lotto.getCupEsente() == null) {
						condizioneVerificata = true;
					}
					break;
				case 140:
					//cup
					if (lotto.getCup() == null && "2".equals(lotto.getCupEsente())) {
						condizioneVerificata = true;
					}
					break;
				case 150:
					//ruolo
					if (aggiudicatario.getRuolo() == null && "1".equals(aggiudicatario.getTipoAggiudicatario())) {
						condizioneVerificata = true;
					}
					break;
				case 160:
					//idGruppo
					if (aggiudicatario.getIdGruppo() == null && "3".equals(aggiudicatario.getTipoAggiudicatario())) {
						condizioneVerificata = true;
					}
					break;
				case 170:
					//Url documento
					if (allegatoAtti.getUrl() == null || 
							!(allegatoAtti.getUrl().toLowerCase().startsWith("http") || allegatoAtti.getUrl().toLowerCase().startsWith("ftp"))) {
						condizioneVerificata = true;
					}
					break;
				case 171:
					//Url documento o file
					if ((allegatoAtti.getUrl() == null || 
							!(allegatoAtti.getUrl().toLowerCase().startsWith("http") || allegatoAtti.getUrl().toLowerCase().startsWith("ftp"))) 
						&& (allegatoAtti.getFile()==null)) {
						condizioneVerificata = true;
					}
					break;					
				default:
					break;
				}
				if(condizioneVerificata) {
					ValidateEntry item = new ValidateEntry(oggetto + controllo.getTitolo(), controllo.getMessaggio(), controllo.getTipo());
					controlli.add(item);
				}
			}
			
		} else if(codFunzione.equals("PUBBLICA_AVVISO")) {
			for(ControlloEntry controllo:controlliDaFare) {
				boolean condizioneVerificata = false;
				switch (controllo.getNumero().intValue()) {
				case 10:
					//data
					if (avviso.getData() == null) {
						condizioneVerificata = true;
					}
					break;
				case 11:
					//data
					if (avviso.getData() != null && avviso.getData().compareTo(new Date())>0) {
						condizioneVerificata = true;
					}
					break;
				case 20:
					//Scadenza
					if (avviso.getScadenza() != null && avviso.getData() != null && 
							avviso.getScadenza().compareTo(avviso.getData()) < 0) {
						condizioneVerificata = true;
					}
					break;
				case 30:
					//Url documento
					if (allegatoAvvisi.getUrl() == null || 
							!(allegatoAvvisi.getUrl().toLowerCase().startsWith("http") || allegatoAvvisi.getUrl().toLowerCase().startsWith("ftp"))) {
						condizioneVerificata = true;
					}
					break;
				case 31:
					//Url documento o file
					if ((allegatoAvvisi.getUrl() == null || 
							!(allegatoAvvisi.getUrl().toLowerCase().startsWith("http") || allegatoAvvisi.getUrl().toLowerCase().startsWith("ftp"))) &&
							allegatoAvvisi.getFile() == null)	{
						condizioneVerificata = true;
					}
					break;					
				default:
					break;
				}
				if(condizioneVerificata) {
					ValidateEntry item = new ValidateEntry(oggetto + controllo.getTitolo(), controllo.getMessaggio(), controllo.getTipo());
					controlli.add(item);
				}
			}
			
		}
	}
}
