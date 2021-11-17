package it.maggioli.eldasoft.wspubblicazioni.bl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import it.maggioli.eldasoft.wspubblicazioni.dao.AvvisiMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.SqlMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.TecniciMapper;
import it.maggioli.eldasoft.wspubblicazioni.utils.Costanti;
import it.maggioli.eldasoft.wspubblicazioni.utils.Funzioni;
import it.maggioli.eldasoft.wspubblicazioni.vo.FlussoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.PubblicazioneResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.AllegatoAvvisiEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.DettaglioAvvisoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.PubblicaAvvisoEntry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component(value = "avvisiManager")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AvvisiManager {

	/** Logger di classe. */
	private Logger logger = LoggerFactory.getLogger(AvvisiManager.class);

	/**
	 * Dao MyBatis con le primitive di estrazione dei dati.
	 */
	@Autowired
	private AvvisiMapper avvisiMapper;

	@Autowired
	private SqlMapper sqlMapper;

	@Autowired
	private TecniciMapper tecniciMapper;
	
	/**
	 * @param avvisiMapper
	 *            avvisiMapper da settare internamente alla classe.
	 */
	public void setAvvisiMapper(AvvisiMapper avvisiMapper) {
		this.avvisiMapper = avvisiMapper;
	}

	/**
	 * @param sqlMapper
	 *            sqlMapper da settare internamente alla classe.
	 */
	public void setSqlMapper(SqlMapper sqlMapper) {
		this.sqlMapper = sqlMapper;
	}

	/**
	 * @param soggettiMapper
	 *            soggettiMapper da settare internamente alla classe.
	 */
	public void setTecniciMapper(TecniciMapper tecniciMapper) {
		this.tecniciMapper = tecniciMapper;
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	private Long getNextId(String table) {
		this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='" + table + "'");
		Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='" + table + "'");
		if (i == null) {
			i = 1;
		}
		return new Long(i);
	}
	/**
	 * @return ritorna la tipologia dell'applicazione dov'è installato il servizio 
	 */
	private Long tipoInstallazione() {
		if (Funzioni.tipoInstallazione == null) {
			String valore = sqlMapper.getConfigValue(Costanti.CONFIG_CODICE_APP,
					Costanti.CONFIG_TIPO_INSTALL_WSREST);
			if (valore != null) {
				Funzioni.tipoInstallazione = new Long(valore);
			} else {
				Funzioni.tipoInstallazione = new Long(3);
			}
		}
		return Funzioni.tipoInstallazione;
	}
	/**
	 * pubblica un avviso nel DB
	 * 
	 * @param avviso
	 *            avviso
	 * @param modalitaInvio
	 * 			(2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di pubblicazione dell'avviso
	 *         
	 */
	public PubblicazioneResult pubblica(PubblicaAvvisoEntry avviso, String modalitaInvio)
	throws Exception {
		logger.info("pubblica avviso");
		PubblicazioneResult risultato= new PubblicazioneResult();
		//ricavo il codice della Stazione appaltante, se non esiste la creo
		String codiceSA = "";
		if (avviso.getCodiceUnitaOrganizzativa() != null) {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + avviso.getCodiceFiscaleSA() + "' and CODEIN_UO='" + avviso.getCodiceUnitaOrganizzativa() + "'");
		} else {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + avviso.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
		}
		avviso.setCodiceSA(codiceSA);
		if (avviso.getRup() != null) {
			//ricavo il codice del rup, se non esiste lo creo
			String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + avviso.getRup().getCfPiva() + "' AND CGENTEI = '" + avviso.getCodiceSA() + "'");
			if (codiceRup == null) {
				//il rup non esiste lo creo
				codiceRup = calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
				avviso.getRup().setCodice(codiceRup);
				avviso.getRup().setCodiceSA(codiceSA);
				this.tecniciMapper.insertTecnico(avviso.getRup());
			}
			avviso.setCodiceRup(codiceRup);
		}
		//ricavo l'id ufficio
		if (avviso.getUfficio() != null) {
			String idUfficio = this.sqlMapper.executeReturnString("SELECT MAX(ID) FROM UFFICI WHERE CODEIN='" + codiceSA + "' and DENOM='" + avviso.getUfficio().replaceAll("'", "''") + "'");
			if (idUfficio == null) {
				//l'ufficio non esiste, lo creo
				//ricavo l'id dell'ufficio
				Integer i = this.sqlMapper.execute("SELECT MAX(ID) FROM UFFICI");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				idUfficio = id.toString();
				this.avvisiMapper.insertUfficio(id, codiceSA, avviso.getUfficio());
			}
			avviso.setIdUfficio(new Long(idUfficio));
		}
		//verifico se l'avviso ha Provincia ubicazione valorizzato
		//altrimenti metto la provincia della SA
		if (avviso.getProvincia() == null || avviso.getProvincia().equals("")) {
			String provinciaSA = this.sqlMapper.executeReturnString("SELECT PROEIN FROM UFFINT WHERE CODEIN='" + codiceSA + "'");
			avviso.setProvincia(provinciaSA);
		}
		//verifico se devo aggiornare o inserire l'avviso
		//se l'id ricevuto non è nullo devo aggiornare l'avviso
		Long tipoInvio = new Long(1);
		if (avviso.getIdRicevuto() == null) {
			//inserisco un nuovo avviso
			//ricavo l'id dell'avviso per la stazione applatante
			Integer i = this.sqlMapper.execute("SELECT MAX(IDAVVISO) FROM AVVISO WHERE CODEIN='" + avviso.getCodiceSA() + "'");
			Long idAvviso = new Long(1);
			if (i != null) {
				idAvviso = new Long(i) + 1;
			}
			avviso.setId(idAvviso);
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.getNextId("W9PUBBLICAZIONI_GEN");
			//i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			risultato.setId(idRicevuto);
			avviso.setIdRicevuto(idRicevuto);
			//this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9PUBBLICAZIONI_GEN'");
			this.avvisiMapper.pubblicaAvviso(avviso);
		} else {
			tipoInvio = new Long(2);
			//aggiorno l'avviso
			//ricavo l'id dell'avviso per la stazione applatante
			Integer i = this.sqlMapper.execute("SELECT IDAVVISO FROM AVVISO WHERE ID_GENERATO=" + avviso.getIdRicevuto());
			avviso.setId(new Long(i));
			this.avvisiMapper.modificaPubblicazioneAvviso(avviso);
			risultato.setId(avviso.getIdRicevuto());
			//se esistono documenti li cancello
			this.avvisiMapper.deleteAllDoc(avviso);
		}
		//inserisco i documenti
		int nrDoc = 1;
		if(avviso.getDocumenti() != null) {
			for(AllegatoAvvisiEntry documento: avviso.getDocumenti()) {
				documento.setCodiceSA(codiceSA);
				documento.setCodiceSistema(avviso.getCodiceSistema());
				documento.setId(avviso.getId());
				documento.setNrDoc(new Long(nrDoc));
				this.avvisiMapper.insertAllegato(documento);
				nrDoc++;
			}
		}
		
		//inserisco flusso
		FlussoEntry flusso = new FlussoEntry();
		if (tipoInstallazione().equals(new Long(3))) {
			Long idFlusso = this.getNextId("W9FLUSSI");
			//Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9FLUSSI'");
			//Long idFlusso = new Long(1);
		    //if (i != null) {
		    //	idFlusso = new Long(i) + 1;
		    //}
		    //this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9FLUSSI'");
		    flusso.setId(idFlusso);
		}
	    flusso.setArea(new Long(3));
	    flusso.setKey01(avviso.getId());
	    flusso.setKey02(avviso.getCodiceSistema());
	    flusso.setKey03(new Long(989));
	    flusso.setTipoInvio(tipoInvio);
	    flusso.setDataInvio(new Date());
	    flusso.setCodiceFiscaleSA(avviso.getCodiceFiscaleSA());
	    ObjectMapper mapper = new ObjectMapper();
	    flusso.setJson(mapper.writeValueAsString(avviso));
	    flusso.setOggetto(avviso.getId().toString());
	    flusso.setIdComunicazione(this.insertInboxOutbox(flusso, modalitaInvio, avviso.getCodiceUnitaOrganizzativa()));
	    if (tipoInstallazione().equals(new Long(3)) && !modalitaInvio.equals("3")) {
	    	this.avvisiMapper.insertFlusso(flusso);
	    }
	    logger.info("pubblica avviso terminato con successo");
		return risultato;
	}
	
	/**
	 * Estrae il dettaglio di un avviso.
	 *
	 * @param idRicevuto
	 *        identificativo dell'avviso
	 * @return dati di dettaglio dell'avviso; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public DettaglioAvvisoResult getDettaglioAvviso(Long idRicevuto) {
		DettaglioAvvisoResult risultato = new DettaglioAvvisoResult();
		try {
			// estrazione dei dati generali dell'avviso
			PubblicaAvvisoEntry avviso = this.avvisiMapper.getAvviso(idRicevuto);

			if (avviso == null) {
				// non ho estratto nulla, allora l'input era errato
				risultato.setError(DettaglioAvvisoResult.ERROR_NOT_FOUND);
			} else {
				avviso.setIdRicevuto(idRicevuto);
				risultato.setAvviso(avviso);
				//rup avviso
				if (StringUtils.isNotEmpty(avviso.getCodiceRup())) {
					avviso.setRup(this.tecniciMapper.getTecnico(avviso.getCodiceRup()));
				}
				//documenti avviso 
				avviso.setDocumenti(this.avvisiMapper.getDocumenti(avviso.getCodiceSA(), avviso.getId(), avviso.getCodiceSistema()));
				//date pubblicazione
				Date primaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MIN(DATINV) FROM W9FLUSSI WHERE AREA = 3 and KEY03 = 989 and KEY01 = " + avviso.getId() + " AND KEY02 = " + avviso.getCodiceSistema() + " AND CFSA='" + avviso.getCodiceFiscaleSA() + "'");
				if (primaPubblicazione != null) {
					avviso.setPrimaPubblicazioneSCP(primaPubblicazione);
				}
				Date ultimaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MAX(DATINV) FROM W9FLUSSI WHERE AREA = 3 and KEY03 = 989 and KEY01 = " + avviso.getId() + " AND KEY02 = " + avviso.getCodiceSistema() + " AND CFSA='" + avviso.getCodiceFiscaleSA() + "'");
				if (ultimaPubblicazione != null) {
					avviso.setUltimaModificaSCP(ultimaPubblicazione);
				}
			}
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione del dettaglio di un avviso con id ricevuto " + idRicevuto, t);
			risultato.setError(DettaglioAvvisoResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	} 
	
	/**
	 * inserisce un record nella inbox per l'avvenuto salvataggio dell'avviso
	 * genera se richiesto un record in w9outbox 
	 * 
	 * @param flusso
	 *            flusso
	 * @param modalitaInvio
	 *            (2 - pubblica, 3 - pubblica senza inoltro SCP)
	 */
	private Long insertInboxOutbox(FlussoEntry flusso, String modalitaInvio, String codiceUnitaOrganizzativa) throws Exception{
		//Inserimento pubblicazione in W9Inbox
		Long idComun = this.getNextId("W9INBOX");
		//Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='W9INBOX'");
		//Long idComun = new Long(1);
	    //if (i != null) {
	    //	idComun = new Long(i) + 1;
	    //}
	    //this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='W9INBOX'");
	    this.sqlMapper.insertInbox(idComun, new Date(), new Long(2), flusso.getJson());
	    if (modalitaInvio.equals("2")) {
	    	Integer i = this.sqlMapper.execute("SELECT MAX(IDCOMUN) FROM W9OUTBOX");
		    Long idComunOut = new Long(1);
		    if (i != null) {
		    	idComunOut = new Long(i) + 1;
		    }
		    this.sqlMapper.insertOutbox(idComunOut, flusso.getArea(), flusso.getKey01(), flusso.getKey02(), flusso.getKey03(), flusso.getKey04(), new Long(1), flusso.getCodiceFiscaleSA(), codiceUnitaOrganizzativa);
	    }
	    return idComun;
	}
	
	public String calcolaCodificaAutomatica(String entita, String campoChiave) throws Exception {
		String codice = "1";
		String formatoCodice = null;
		String codcal = null;
		Long cont = null;
		try {
			String query = "select CODCAL, CONTAT from G_CONFCOD where NOMENT = '" + entita + "'";
			List<Map<String,Object>> confcod = sqlMapper.select(query);
			if (confcod!= null && confcod.size() > 0) {
				for(Map<String,Object> row:confcod) {
					if (row.containsKey("CODCAL")) {
						codcal = row.get("CODCAL").toString();
					} else {
						codcal = row.get("codcal").toString();
					}
					if (row.containsKey("CONTAT")) {
						cont = new Long(row.get("CONTAT").toString());
					} else {
						cont = new Long(row.get("contat").toString());
					}
					break;
				}
				boolean codiceUnivoco = false;
				int numeroTentativi = 0;
				StringBuffer strBuffer = null;
				long tmpContatore = cont.longValue();
				while (!codiceUnivoco
						&& numeroTentativi < Costanti.NUMERO_MAX_TENTATIVI_INSERT) {
					strBuffer = new StringBuffer("");
					// Come prima cosa eseguo l'update del contatore
					tmpContatore++;
					sqlMapper.execute("update G_CONFCOD set contat = " + tmpContatore + " where NOMENT = '" + entita + "'");

					strBuffer = new StringBuffer("");
					formatoCodice = codcal;
					while (formatoCodice.length() > 0) {
						switch (formatoCodice.charAt(0)) {
						case '<': // Si tratta di un'espressione numerica
							String strNum = formatoCodice.substring(1, formatoCodice.indexOf('>'));
							if (strNum.charAt(0) == '0') {
								// Giustificato a destra
								for (int i = 0; i < (strNum.length() - String.valueOf(tmpContatore).length()); i++)
									strBuffer.append('0');
							}
							strBuffer.append(String.valueOf(tmpContatore));

							formatoCodice = formatoCodice.substring(formatoCodice.indexOf('>') + 1);
							break;
						case '"': // Si tratta di una parte costante
							strBuffer.append(formatoCodice.substring(1, formatoCodice.indexOf('"', 1)));
							formatoCodice = formatoCodice.substring(formatoCodice.indexOf('"', 1) + 1);
							break;
						}
					}
					int occorrenze = sqlMapper.count(entita + " WHERE " + campoChiave + " ='" + strBuffer.toString() + "'");
					if (occorrenze == 0) {
						codiceUnivoco = true;
						codice = strBuffer.toString();
					}
					else {
						numeroTentativi++;
					}
				}
				if (!codiceUnivoco) {
					logger.error("numeroTentativi esaurito durante il calcolo per la codifica automatica " + entita);
					throw new Exception("numeroTentativi esaurito durante il calcolo per la codifica automatica " + entita);
				}
			}
		} catch (Exception ex) {
			logger.error("Errore inaspettato durante il calcolo per la codifica automatica " + entita, ex);
			throw new Exception(ex);
		}
		return codice;
	}
	
}
