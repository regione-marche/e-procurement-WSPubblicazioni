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

import it.maggioli.eldasoft.wspubblicazioni.dao.AttiMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.CentriCostoMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.ImpreseMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.LottiMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.SqlMapper;
import it.maggioli.eldasoft.wspubblicazioni.dao.TecniciMapper;
import it.maggioli.eldasoft.wspubblicazioni.utils.Costanti;
import it.maggioli.eldasoft.wspubblicazioni.utils.Funzioni;
import it.maggioli.eldasoft.wspubblicazioni.vo.FlussoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.PubblicazioneAttoResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.PubblicazioneResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.centriCosto.DatiGeneraliCentroCostoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.DettaglioGaraResult;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.PubblicaGaraEntry;
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
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.TipoAttoResult;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Manager per la gestione della business logic.
 *
 * @author Mirco.Franzoni
 */
@Component(value = "pubblicazioniManager")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class AttiManager {

  /** Logger di classe. */
  private Logger             logger = LoggerFactory.getLogger(AttiManager.class);

  /**
   * Dao MyBatis con le primitive di estrazione dei dati.
   */
  @Autowired
  private AttiMapper attiMapper;

  @Autowired
  private SqlMapper sqlMapper;
  
  @Autowired
  private TecniciMapper tecniciMapper;
  
  @Autowired
  private CentriCostoMapper centriCostoMapper;
	
  @Autowired
  private LottiMapper lottiMapper;
  
  @Autowired
  private ImpreseMapper impreseMapper;
  
  @Autowired
  private WGenChiaviManager wgenChiaviManager;
  
  /**
   * @param attiMapper
   *        attiMapper da settare internamente alla classe.
   */
  public void setAttiMapper(AttiMapper attiMapper) {
    this.attiMapper = attiMapper;
  }

  /**
   * @param sqlMapper
   *        sqlMapper da settare internamente alla classe.
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
	
	/**
	 * @param centriCostoMapper
	 *            centriCostoMapper da settare internamente alla classe.
	 */
	public void setCentriCostoMapper(CentriCostoMapper centriCostoMapper) {
		this.centriCostoMapper = centriCostoMapper;
	}
	
	/**
	 * @param lottiMapper
	 *            lottiMapper da settare internamente alla classe.
	 */
	public void setLottiMapper(LottiMapper lottiMapper) {
		this.lottiMapper = lottiMapper;
	}
	
	/**
	 * @param impreseMapper
	 *            impreseMapper da settare internamente alla classe.
	 */
	public void setImpreseMapper(ImpreseMapper impreseMapper) {
		this.impreseMapper = impreseMapper;
	}
	
	/**
	 * @param wgenChiaviManager
	 * 			wgenChiaviManager da settare internamente alla classe.
	 */
	public void setWGenChiaviManager(WGenChiaviManager wgenChiaviManager) {
		this.wgenChiaviManager = wgenChiaviManager;
	}
	
	@Transactional(isolation = Isolation.READ_COMMITTED)
	/*private Long getNextId(String table) {
		this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE TABELLA='" + table + "'");
		Integer i = this.sqlMapper.execute("SELECT chiave FROM W_GENCHIAVI WHERE TABELLA='" + table + "'");
		if (i == null) {
			i = 1;
		}
		return new Long(i.longValue());
	}*/
	
	/**
	 * @return in aggiornamento non verranno sovrascritti dati di anagrafica gara e lotto già modificati in SITATSA
	 */
	private boolean nonAggiornareGaraSeEsiste() {
		if (Funzioni.noAggiornaGaraSeEsiste == null) {
			String valore = sqlMapper.getConfigValue(Costanti.CONFIG_CODICE_APP,
					Costanti.CONFIG_NO_AGGIORNA_GARA_SE_ESISTE);
			if (valore != null && valore.equals("1")) {
				Funzioni.noAggiornaGaraSeEsiste = true;
			} else {
				Funzioni.noAggiornaGaraSeEsiste = false;
			}
		}
		return Funzioni.noAggiornaGaraSeEsiste.booleanValue();
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
   * Estrae gli attributi per tutti i tipi di Atti
   *
   * @return attributi per tutti i tipi di Atti; nel caso di errore si setta il campo error con un identificativo di errore
   */
  public TipoAttoResult getTipoAtti() {
	  TipoAttoResult risultato = new TipoAttoResult();
    try {
    	List<TipoAttoEntry> tipologie = this.attiMapper.getTipoAtti();
    	if (tipologie == null) {
    		// non ho estratto nulla, allora l'input era errato
    		risultato.setError(TipoAttoResult.ERROR_NOT_FOUND);
    	} else {
    		risultato.setTipologie(tipologie);
    	}
    } catch (Throwable t) {
      // qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
      logger.error("Errore inaspettato durante l'estrazione degli attributi delle tipologie di atti ", t);
      risultato = new TipoAttoResult();
      risultato.setError(TipoAttoResult.ERROR_UNEXPECTED + ": " + t.getMessage());
    }

    return risultato;
  }
  
  /**
	 * pubblica una pubblicazione nel DB
	 * 
	 * @param pubblicazione
	 *            pubblicazione
	 * @param modalitaInvio
	 * 			(2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di pubblicazione della pubblicazione
	 *         
	 */
	public PubblicazioneAttoResult pubblica(PubblicaAttoEntry pubblicazione, String modalitaInvio)
	throws Exception {
		logger.info("pubblica atto");
		PubblicazioneAttoResult risultato= new PubblicazioneAttoResult();
		//ricavo il codice della Stazione appaltante, se non esiste la creo
		String codiceSA = "";
		if (pubblicazione.getGara().getCodiceUnitaOrganizzativa() != null) {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + pubblicazione.getGara().getCodiceFiscaleSA() + "' and CODEIN_UO='" + pubblicazione.getGara().getCodiceUnitaOrganizzativa() + "'");
		} else {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + pubblicazione.getGara().getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
		}
		pubblicazione.getGara().setIdEnte(codiceSA);
		//ricavo il codice del rup, se non esiste lo creo
		String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + pubblicazione.getGara().getTecnicoRup().getCfPiva() + "' AND CGENTEI = '" + codiceSA + "'");
		if (codiceRup == null) {
			//il rup non esiste lo creo
			codiceRup = calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
			pubblicazione.getGara().getTecnicoRup().setCodice(codiceRup);
			pubblicazione.getGara().getTecnicoRup().setCodiceSA(codiceSA);
			this.tecniciMapper.insertTecnico(pubblicazione.getGara().getTecnicoRup());
		}
		pubblicazione.getGara().setIdRup(codiceRup);
		//ricavo il codice centro di costo
		if (pubblicazione.getGara().getCodiceCentroCosto() != null) {
			String idCentroCosto = this.sqlMapper.executeReturnString("SELECT MAX(IDCENTRO) FROM CENTRICOSTO WHERE CODEIN='" + codiceSA + "' and CODCENTRO='" + pubblicazione.getGara().getCodiceCentroCosto().replaceAll("'", "''") + "'");
			if (idCentroCosto == null) {
				//il centro di costo non esiste, lo creo
				DatiGeneraliCentroCostoEntry cc = new DatiGeneraliCentroCostoEntry();
				//ricavo l'id del centro di costo
				Integer i = this.sqlMapper.execute("SELECT MAX(IDCENTRO) FROM CENTRICOSTO");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				cc.setId(id);
				cc.setStazioneAppaltante(codiceSA);
				cc.setCodice(pubblicazione.getGara().getCodiceCentroCosto());
				cc.setDenominazione(pubblicazione.getGara().getCentroCosto());
				idCentroCosto = id.toString();
				this.centriCostoMapper.insertCentroCosto(cc);
			}
			pubblicazione.getGara().setIdCentroCosto(new Long(idCentroCosto));
		}
		//ricavo l'id ufficio
		if (pubblicazione.getGara().getUfficio() != null) {
			String idUfficio = this.sqlMapper.executeReturnString("SELECT MAX(ID) FROM UFFICI WHERE CODEIN='" + codiceSA + "' and DENOM='" + pubblicazione.getGara().getUfficio().replaceAll("'", "''") + "'");
			if (idUfficio == null) {
				//l'ufficio non esiste, lo creo
				//ricavo l'id dell'ufficio
				Integer i = this.sqlMapper.execute("SELECT MAX(ID) FROM UFFICI");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				idUfficio = id.toString();
				this.attiMapper.insertUfficio(id, codiceSA, pubblicazione.getGara().getUfficio());
			}
			pubblicazione.getGara().setIdUfficio(new Long(idUfficio));
		}
		//verifico se la gara ha Provincia sede di gara valorizzato
		//altrimenti metto la provincia della SA
		if (pubblicazione.getGara().getProvincia() == null || pubblicazione.getGara().getProvincia().equals("")) {
			String provinciaSA = this.sqlMapper.executeReturnString("SELECT PROEIN FROM UFFINT WHERE CODEIN='" + codiceSA + "'");
			pubblicazione.getGara().setProvincia(provinciaSA);
		}
		
		//se idRicevuto è vuoto verifico che la gara non sia già presente perchè creata manualmente o importata da simog
		if (pubblicazione.getGara().getIdRicevuto() == null) {
			for(PubblicaLottoEntry lotto:pubblicazione.getGara().getLotti()) {
				if (lotto.getCig() != null && !lotto.getCig().equals("")) {
					//verifico che non ci sia già il CIG presente nel DB
					int occorrenze = sqlMapper.count("W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
					if (occorrenze > 0) {
						//se il cig già esiste verifico che la gara che lo contiene abbia la stessa SA e che l'id_generato e idClient siano vuoti
						Integer codgara = this.sqlMapper.execute("SELECT MAX(CODGARA) FROM W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
						String cfein = this.sqlMapper.executeReturnString("SELECT UFFINT.CFEIN FROM W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN WHERE CODGARA=" + codgara);
						String codiceUnitaOrganizzativa = this.sqlMapper.executeReturnString("SELECT UFFINT.CODEIN_UO FROM W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN WHERE CODGARA=" + codgara);
						if (codiceUnitaOrganizzativa != null) {
							codiceUnitaOrganizzativa = codiceUnitaOrganizzativa.toUpperCase();
						}
						if (cfein.toUpperCase().equals(pubblicazione.getGara().getCodiceFiscaleSA().toUpperCase()) && ((codiceUnitaOrganizzativa == null && pubblicazione.getGara().getCodiceUnitaOrganizzativa() == null) || (codiceUnitaOrganizzativa != null && pubblicazione.getGara().getCodiceUnitaOrganizzativa() != null && codiceUnitaOrganizzativa.equals(pubblicazione.getGara().getCodiceUnitaOrganizzativa())))) {
							//se la stazione appaltante è la stessa
							String idClient = this.sqlMapper.executeReturnString("SELECT ID_CLIENT FROM W9GARA WHERE CODGARA=" + codgara);
							if (idClient == null || idClient.equals("")) {
								Integer idGenerato = this.sqlMapper.execute("SELECT ID_GENERATO FROM W9GARA WHERE CODGARA =" + codgara);
								if (idGenerato == null) {
									//genero id_ricevuto
									Long idRicevuto = this.wgenChiaviManager.getNextId("W9PUBBLICAZIONI_GEN");
									pubblicazione.getGara().setIdRicevuto(idRicevuto);
									this.sqlMapper.execute("UPDATE W9GARA SET ID_GENERATO=" + idRicevuto + ", ID_CLIENT='" + pubblicazione.getClientId() + "' WHERE CODGARA=" + codgara);
									logger.info("PubblicaAtti Associazione ID_RICEVUTO = " + idRicevuto + " a GARA = "+ codgara + " già esistente");
									break;
								}
							}
						}
					}
				}
			}
		}
		
		if (pubblicazione.getGara().getIdRicevuto() == null) {
			//inserisco una nuova gara
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.wgenChiaviManager.getNextId("W9PUBBLICAZIONI_GEN");
			risultato.setIdGara(idRicevuto);
			Long id = this.insertGara(pubblicazione.getGara(), idRicevuto);
			pubblicazione.setIdGara(id);
			//this.attiMapper.pubblicaGara(pubblicazione.getGara());
		} else {
			//aggiorno la gara
			//ricavo l'id della gara
			Integer i = this.sqlMapper.execute("SELECT MAX(CODGARA) FROM W9GARA WHERE ID_GENERATO=" + pubblicazione.getGara().getIdRicevuto());
			pubblicazione.getGara().setId(new Long(i));
			pubblicazione.setIdGara(new Long(i));
			if (!this.nonAggiornareGaraSeEsiste()) {
				this.attiMapper.modificaPubblicazioneGara(pubblicazione.getGara());
			}
			risultato.setIdGara(pubblicazione.getGara().getIdRicevuto());
		}
		if (pubblicazione.getGara().getPubblicazioneBando() != null) {
			this.sqlMapper.execute("DELETE FROM W9PUBB WHERE CODGARA = " + pubblicazione.getIdGara());
			pubblicazione.getGara().getPubblicazioneBando().setIdGara(pubblicazione.getIdGara());
			this.attiMapper.insertBando(pubblicazione.getGara().getPubblicazioneBando());
		}
		
		Long tipoInvio = new Long(1);
		//Inserimento pubblicazione
		if (pubblicazione.getIdRicevuto() == null) {
			//inserimento
			//ricavo l'id della pubblicazione
			Integer i = this.sqlMapper.execute("SELECT MAX(NUM_PUBB) FROM W9PUBBLICAZIONI WHERE CODGARA=" + pubblicazione.getIdGara());
		    Long numeroPubblicazione = new Long(1);
		    if (i != null) {
		   		numeroPubblicazione = new Long(i) + 1;
		    }
			pubblicazione.setNumeroPubblicazione(numeroPubblicazione);
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.wgenChiaviManager.getNextId("W9PUBBLICAZIONI_GEN");
			risultato.setIdExArt29(idRicevuto);
			pubblicazione.setIdRicevuto(idRicevuto);
			this.attiMapper.pubblicaAtto(pubblicazione);
		} else {
			tipoInvio = new Long(2);
			//aggiornamento
			//ricavo il numero della pubblicazione della gara
			Integer i = this.sqlMapper.execute("SELECT NUM_PUBB FROM W9PUBBLICAZIONI WHERE ID_GENERATO=" + pubblicazione.getIdRicevuto());
			pubblicazione.setNumeroPubblicazione(new Long(i));
			this.attiMapper.modificaPubblicazioneAtto(pubblicazione);
			risultato.setIdExArt29(pubblicazione.getIdRicevuto());
			//cancello gli eventuali dati dalla W9PUBLOTTO e W9DOCGARA
			this.sqlMapper.execute("DELETE FROM W9PUBLOTTO WHERE CODGARA = " + pubblicazione.getIdGara() + " AND NUM_PUBB = " + pubblicazione.getNumeroPubblicazione());
			this.sqlMapper.execute("DELETE FROM W9DOCGARA WHERE CODGARA = " + pubblicazione.getIdGara() + " AND NUM_PUBB = " + pubblicazione.getNumeroPubblicazione());
			this.sqlMapper.execute("DELETE FROM ESITI_AGGIUDICATARI WHERE CODGARA = " + pubblicazione.getIdGara() + " AND NUM_PUBB = " + pubblicazione.getNumeroPubblicazione());
		}
		
		//inserisco i lotti
		for(PubblicaLottoEntry lotto: pubblicazione.getGara().getLotti()) {
			//ricavo il codice del rup, se non esiste lo creo
			/*String codiceRupLotto = this.sqlMapper.executeReturnString("SELECT CODTEC FROM TECNI WHERE CFTEC='" + lotto.getTecnicoRup().getCfPiva() + "' AND CGENTEI = '" + codiceSA + "'");
			if (codiceRupLotto == null) {
				//il rup non esiste lo creo
				codiceRupLotto = this.calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
				lotto.getTecnicoRup().setCodice(codiceRupLotto);
				lotto.getTecnicoRup().setCodiceSA(codiceSA);
				this.tecniciMapper.insertTecnico(lotto.getTecnicoRup());
			}*/
			//associo al lotto il rup della gara
			lotto.setIdRup(codiceRup);
			//verifico l'importo del lotto
			if (lotto.getImportoTotale() == null) {
				lotto.setImportoTotale(new Double(0));
				if (lotto.getImportoLotto() != null) {
					lotto.setImportoTotale(lotto.getImportoLotto());
				}
				if (lotto.getImportoSicurezza() != null) {
					lotto.setImportoTotale(lotto.getImportoTotale() + lotto.getImportoSicurezza());
				}
			}
			//verifico se il lotto già esiste confrontando il codice CIG
			lotto.setIdGara(pubblicazione.getIdGara());
			String codlott = this.sqlMapper.executeReturnString("SELECT MAX(CODLOTT) FROM W9LOTT WHERE CODGARA=" + pubblicazione.getIdGara() + " AND CIG='" + lotto.getCig() + "'");
			if (codlott == null) {
				//inserisco il lotto
				//ricavo l'id del lotto all'interno della gara
				//metto eventuali valori di default
				Integer i = this.sqlMapper.execute("SELECT MAX(CODLOTT) FROM W9LOTT WHERE CODGARA=" + pubblicazione.getIdGara());
				Long idLotto = new Long(1);
				if (i != null) {
					idLotto = new Long(i) + 1;
				}
				lotto.setIdLotto(idLotto);
				this.attiMapper.pubblicaLotto(lotto);
			} else {
				//aggiorno il lotto
				lotto.setIdLotto(new Long(codlott));
				if (!this.nonAggiornareGaraSeEsiste()) {
					this.attiMapper.modificaPubblicazioneLotto(lotto);
				}
			}
			if (codlott == null || !this.nonAggiornareGaraSeEsiste()) {
				//cancello categorie e CPV secondari, Modalità acquisizione forniture e inserisco
				this.sqlMapper.execute("DELETE FROM W9CPV WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9LOTTCATE WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9APPAFORN WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9APPALAV WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9COND WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				int numCategoria = 1;
				if (lotto.getCategorie() != null) {
					for(CategoriaLottoEntry categoria:lotto.getCategorie()) {
						categoria.setIdGara(lotto.getIdGara());
						categoria.setIdLotto(lotto.getIdLotto());
						categoria.setNumCategoria(new Long(numCategoria));
						this.lottiMapper.insertCategoria(categoria);
						numCategoria++;
					}
				}
				if (lotto.getCpvSecondari() != null) {
					int numCpv = 1;
					for(CpvLottoEntry cpv:lotto.getCpvSecondari()) {
						cpv.setIdGara(lotto.getIdGara());
						cpv.setIdLotto(lotto.getIdLotto());
						cpv.setNumCpv(new Long(numCpv));
						this.lottiMapper.insertCpv(cpv);
						numCpv++;
					}
				}
				if (lotto.getModalitaAcquisizioneForniture() != null) {
					int numAppaForn = 1;
					for(AppaFornEntry modalita:lotto.getModalitaAcquisizioneForniture()) {
						modalita.setIdGara(lotto.getIdGara());
						modalita.setIdLotto(lotto.getIdLotto());
						modalita.setNumAppaForn(new Long(numAppaForn));
						this.lottiMapper.insertModalitaAcquisizioneForniture(modalita);
						numAppaForn++;
					}
				}
				if (lotto.getTipologieLavori() != null) {
					int numAppaLav = 1;
					for(AppaLavEntry tipologia:lotto.getTipologieLavori()) {
						tipologia.setIdGara(lotto.getIdGara());
						tipologia.setIdLotto(lotto.getIdLotto());
						tipologia.setNumAppaLav(new Long(numAppaLav));
						this.lottiMapper.insertTipologiaLavori(tipologia);
						numAppaLav++;
					}
				}
				if (lotto.getMotivazioniProceduraNegoziata() != null) {
					int numCondizione = 1;
					for(MotivazioneProceduraNegoziataEntry condizione:lotto.getMotivazioniProceduraNegoziata()) {
						condizione.setIdGara(lotto.getIdGara());
						condizione.setIdLotto(lotto.getIdLotto());
						condizione.setNumCondizione(new Long(numCondizione));
						this.lottiMapper.insertCondizione(condizione);
						numCondizione++;
					}
				}
			}
			this.sqlMapper.execute("INSERT INTO W9PUBLOTTO (CODGARA, NUM_PUBB, CODLOTT) VALUES (" + lotto.getIdGara() + "," + pubblicazione.getNumeroPubblicazione() + "," + lotto.getIdLotto() + ")");
		}
		//aggiorno campo NLOTTI nell W9GARA
		//this.sqlMapper.execute("UPDATE W9GARA SET NLOTTI = (SELECT COUNT(*) FROM W9LOTT WHERE CODGARA = " + pubblicazione.getIdGara() + ")");
		//inserisco gli eventuali aggiudicatari
		int numeroAggiudicatario = 1;
		if(pubblicazione.getAggiudicatari() != null) {
			for(AggiudicatarioEntry aggiudicatario: pubblicazione.getAggiudicatari()) {
				//ricavo il codice dell'impresa, se non esiste la creo
				String codiceImpresa = this.sqlMapper.executeReturnString("SELECT MAX(CODIMP) FROM IMPR WHERE CFIMP='" + aggiudicatario.getImpresa().getCodiceFiscale() + "' AND CGENIMP = '" + codiceSA + "'");
				if (codiceImpresa == null) {
					//l'impresa non esiste la creo
					codiceImpresa = calcolaCodificaAutomatica("IMPR", "CODIMP");	//Autogenero un codice per l'impresa
					aggiudicatario.getImpresa().setId(codiceImpresa);
					aggiudicatario.getImpresa().setCodiceSA(codiceSA);
					this.impreseMapper.insertImpresa(aggiudicatario.getImpresa());
				} else {
					aggiudicatario.getImpresa().setId(codiceImpresa);
					this.impreseMapper.updateImpresa(aggiudicatario.getImpresa());
				}
				aggiudicatario.setCodiceImpresa(codiceImpresa);
				aggiudicatario.setIdGara(pubblicazione.getIdGara());
				aggiudicatario.setNumeroPubblicazione(pubblicazione.getNumeroPubblicazione());
				aggiudicatario.setNumeroAggiudicatario(new Long(numeroAggiudicatario));
				this.attiMapper.insertAggiudicatario(aggiudicatario);
				numeroAggiudicatario++;
			}
		}
		
		//inserisco i documenti
		int nrDoc = 1;
		if(pubblicazione.getDocumenti() != null) {
			for(AllegatoAttiEntry documento: pubblicazione.getDocumenti()) {
				documento.setIdGara(pubblicazione.getIdGara());
				documento.setNrPubblicazione(pubblicazione.getNumeroPubblicazione());
				documento.setNrDoc(new Long(nrDoc));
				this.attiMapper.insertAllegato(documento);
				nrDoc++;
			}
		}
		//inserisco flusso
		FlussoEntry flusso = new FlussoEntry();
		if (tipoInstallazione().equals(new Long(3))) {
			Long idFlusso = this.wgenChiaviManager.getNextId("W9FLUSSI");
		    flusso.setId(idFlusso);
		}
	    flusso.setArea(new Long(2));
	    flusso.setKey01(pubblicazione.getIdGara());
	    flusso.setKey03(new Long(901));
	    flusso.setKey04(pubblicazione.getNumeroPubblicazione());
	    flusso.setTipoInvio(tipoInvio);
	    flusso.setDataInvio(new Date());
	    flusso.setCodiceFiscaleSA(pubblicazione.getGara().getCodiceFiscaleSA());
	    ObjectMapper mapper = new ObjectMapper();
	    flusso.setJson(mapper.writeValueAsString(pubblicazione));
	    if(pubblicazione.getGara().getProvenienzaDato() != null && pubblicazione.getGara().getProvenienzaDato().equals(new Long(4))) {
	    	//se la gara è uno smartcig metto nell'oggetto il codice smartcig
	    	if (pubblicazione.getGara().getLotti() != null && pubblicazione.getGara().getLotti().size() > 0 &&
	    			pubblicazione.getGara().getLotti().get(0).getCig() != null) {
	    		flusso.setOggetto(pubblicazione.getGara().getLotti().get(0).getCig());
	    	} else {
	    		flusso.setOggetto(pubblicazione.getGara().getIdAnac());
	    	}
	    } else {
	    	flusso.setOggetto(pubblicazione.getGara().getIdAnac());
	    }
	    flusso.setIdComunicazione(this.insertInboxOutbox(flusso, modalitaInvio, pubblicazione.getGara().getCodiceUnitaOrganizzativa()));
	    if (tipoInstallazione().equals(new Long(3)) && !modalitaInvio.equals("3")) {
	    	this.attiMapper.insertFlusso(flusso);
	    }
	    logger.info("pubblica atto terminato con successo");
		return risultato;
	}

  	@Transactional(isolation = Isolation.READ_COMMITTED)
	private Long insertGara(PubblicaGaraEntry gara, Long idRicevuto) {
		long codgara = this.wgenChiaviManager.getNextId("W9GARA");
		gara.setId(codgara);
		gara.setIdRicevuto(idRicevuto);
		this.attiMapper.pubblicaGara(gara);
		return codgara;
	}
  	
	public void AggiornaNumeroLotti(Long codgara){
		  try {
			  String updateNumeroLotti = "UPDATE W9GARA SET NLOTTI = (SELECT COUNT(*) FROM W9LOTT WHERE CODGARA = " + codgara + ") WHERE CODGARA = " + codgara;
			  this.sqlMapper.execute(updateNumeroLotti);
		  } catch (Exception e) {
		      logger.error("Errore durante l'aggiornamento del numero di lotti", e);
		  }
	}
	
	/**
	 * salva una gara nel DB
	 * 
	 * @param gara
	 *            gara
	 * @param modalitaInvio
	 * 			(2 - pubblica, 3 - pubblica senza inoltro SCP)
	 * @return risultato dell'operazione di salvataggio dell'anagrafica gara e lotti
	 *         
	 */
	public PubblicazioneResult garaLotti(PubblicaGaraEntry gara, String modalitaInvio)
	throws Exception {
		logger.info("garaLotti gara");
		PubblicazioneResult risultato= new PubblicazioneResult();
		//ricavo il codice della Stazione appaltante
		String codiceSA = "";
		if (gara.getCodiceUnitaOrganizzativa() != null) {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + gara.getCodiceFiscaleSA() + "' and CODEIN_UO='" + gara.getCodiceUnitaOrganizzativa() + "'");
		} else {
			codiceSA = this.sqlMapper.executeReturnString("SELECT MAX(CODEIN) FROM UFFINT WHERE CFEIN='" + gara.getCodiceFiscaleSA() + "' and (CODEIN_UO is null or CODEIN_UO='')");
		}
		gara.setIdEnte(codiceSA);
		//ricavo il codice del rup, se non esiste lo creo
		String codiceRup = this.sqlMapper.executeReturnString("SELECT MAX(CODTEC) FROM TECNI WHERE CFTEC='" + gara.getTecnicoRup().getCfPiva() + "' AND CGENTEI = '" + codiceSA + "'");
		if (codiceRup == null) {
			//il rup non esiste lo creo
			codiceRup = calcolaCodificaAutomatica("TECNI", "CODTEC");	//Autogenero un codice per il tecnico
			gara.getTecnicoRup().setCodice(codiceRup);
			gara.getTecnicoRup().setCodiceSA(codiceSA);
			this.tecniciMapper.insertTecnico(gara.getTecnicoRup());
		}
		gara.setIdRup(codiceRup);
		//ricavo il codice centro di costo
		if (gara.getCodiceCentroCosto() != null) {
			String idCentroCosto = this.sqlMapper.executeReturnString("SELECT MAX(IDCENTRO) FROM CENTRICOSTO WHERE CODEIN='" + codiceSA + "' and CODCENTRO='" + gara.getCodiceCentroCosto().replaceAll("'", "''") + "'");
			if (idCentroCosto == null) {
				//il centro di costo non esiste, lo creo
				DatiGeneraliCentroCostoEntry cc = new DatiGeneraliCentroCostoEntry();
				//ricavo l'id del centro di costo
				Integer i = this.sqlMapper.execute("SELECT MAX(IDCENTRO) FROM CENTRICOSTO");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				cc.setId(id);
				cc.setStazioneAppaltante(codiceSA);
				cc.setCodice(gara.getCodiceCentroCosto());
				cc.setDenominazione(gara.getCentroCosto());
				idCentroCosto = id.toString();
				this.centriCostoMapper.insertCentroCosto(cc);
			}
			gara.setIdCentroCosto(new Long(idCentroCosto));
		}
		//ricavo l'id ufficio
		if (gara.getUfficio() != null) {
			String idUfficio = this.sqlMapper.executeReturnString("SELECT MAX(ID) FROM UFFICI WHERE CODEIN='" + codiceSA + "' and DENOM='" + gara.getUfficio().replaceAll("'", "''") + "'");
			if (idUfficio == null) {
				//l'ufficio non esiste, lo creo
				//ricavo l'id dell'ufficio
				Integer i = this.sqlMapper.execute("SELECT MAX(ID) FROM UFFICI");
				Long id = new Long(1);
				if (i != null) {
					id = new Long(i) + 1;
				}
				idUfficio = id.toString();
				this.attiMapper.insertUfficio(id, codiceSA, gara.getUfficio());
			}
			gara.setIdUfficio(new Long(idUfficio));
		}
		//verifico se la gara ha Provincia sede di gara valorizzato
		//altrimenti metto la provincia della SA
		if (gara.getProvincia() == null || gara.getProvincia().equals("")) {
			String provinciaSA = this.sqlMapper.executeReturnString("SELECT PROEIN FROM UFFINT WHERE CODEIN='" + codiceSA + "'");
			gara.setProvincia(provinciaSA);
		}
		Long tipoInvio = new Long(1);
		//se idRicevuto è vuoto verifico che la gara non sia già presente perchè creata manualmente o importata da simog
		if (gara.getIdRicevuto() == null) {
			for(PubblicaLottoEntry lotto:gara.getLotti()) {
				if (lotto.getCig() != null && !lotto.getCig().equals("")) {
					//verifico che non ci sia già il CIG presente nel DB
					int occorrenze = sqlMapper.count("W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
					if (occorrenze > 0) {
						//se il cig già esiste verifico che la gara che lo contiene abbia la stessa SA e che l'id_generato e idClient siano vuoti
						Integer codgara = this.sqlMapper.execute("SELECT MAX(CODGARA) FROM W9LOTT WHERE CIG ='" + lotto.getCig() + "'");
						String cfein = this.sqlMapper.executeReturnString("SELECT UFFINT.CFEIN FROM W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN WHERE CODGARA=" + codgara);
						String codiceUnitaOrganizzativa = this.sqlMapper.executeReturnString("SELECT UFFINT.CODEIN_UO FROM W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN WHERE CODGARA=" + codgara);
						if (codiceUnitaOrganizzativa != null) {
							codiceUnitaOrganizzativa = codiceUnitaOrganizzativa.toUpperCase();
						}
						if (cfein.toUpperCase().equals(gara.getCodiceFiscaleSA().toUpperCase()) && ((codiceUnitaOrganizzativa == null && gara.getCodiceUnitaOrganizzativa() == null) || (codiceUnitaOrganizzativa != null && gara.getCodiceUnitaOrganizzativa() != null && codiceUnitaOrganizzativa.equals(gara.getCodiceUnitaOrganizzativa())))) {
							//se la stazione appaltante è la stessa
							String idClient = this.sqlMapper.executeReturnString("SELECT ID_CLIENT FROM W9GARA WHERE CODGARA=" + codgara);
							if (idClient == null || idClient.equals("")) {
								Integer idGenerato = this.sqlMapper.execute("SELECT ID_GENERATO FROM W9GARA WHERE CODGARA =" + codgara);
								if (idGenerato == null) {
									//genero id_ricevuto
									Long idRicevuto = this.wgenChiaviManager.getNextId("W9PUBBLICAZIONI_GEN");
									gara.setIdRicevuto(idRicevuto);
									this.sqlMapper.execute("UPDATE W9GARA SET ID_GENERATO=" + idRicevuto + ", ID_CLIENT='" + gara.getClientId() + "' WHERE CODGARA=" + codgara);
									logger.info("AnagraficaGaraLotti Associazione ID_RICEVUTO = " + idRicevuto + " a GARA = "+ codgara + " già esistente");
									break;
								}
							}
						}
					}
				}
			}
		}
		
		if (gara.getIdRicevuto() == null) {
			//inserisco una nuova gara
			//ricavo l'id della gara
			//ricavo l'id univoco della pubblicazione
			Long idRicevuto = this.wgenChiaviManager.getNextId("W9PUBBLICAZIONI_GEN");
			risultato.setId(idRicevuto);
			this.insertGara(gara, idRicevuto);
		} else {
			tipoInvio = new Long(2);
			//aggiorno la gara
			//ricavo l'id della gara
			Integer i = this.sqlMapper.execute("SELECT CODGARA FROM W9GARA WHERE ID_GENERATO=" + gara.getIdRicevuto());
			gara.setId(new Long(i));
			if (!this.nonAggiornareGaraSeEsiste()) {
				this.attiMapper.modificaPubblicazioneGara(gara);
			}
			risultato.setId(gara.getIdRicevuto());
		}
		if (gara.getPubblicazioneBando() != null) {
			this.sqlMapper.execute("DELETE FROM W9PUBB WHERE CODGARA = " + gara.getId());
			gara.getPubblicazioneBando().setIdGara(gara.getId());
			this.attiMapper.insertBando(gara.getPubblicazioneBando());
		}
		//inserisco i lotti
		for(PubblicaLottoEntry lotto: gara.getLotti()) {
			//associo al lotto il rup della gara
			lotto.setIdRup(codiceRup);
			//verifico l'importo del lotto
			if (lotto.getImportoTotale() == null) {
				lotto.setImportoTotale(new Double(0));
				if (lotto.getImportoLotto() != null) {
					lotto.setImportoTotale(lotto.getImportoLotto());
				}
				if (lotto.getImportoSicurezza() != null) {
					lotto.setImportoTotale(lotto.getImportoTotale() + lotto.getImportoSicurezza());
				}
			}
			//verifico se il lotto già esiste confrontando il codice CIG
			lotto.setIdGara(gara.getId());
			String codlott = this.sqlMapper.executeReturnString("SELECT MAX(CODLOTT) FROM W9LOTT WHERE CODGARA=" + gara.getId() + " AND CIG='" + lotto.getCig() + "'");
			if (codlott == null) {
				//inserisco il lotto
				//ricavo l'id del lotto all'interno della gara
				//metto eventuali valori di default
				Integer i = this.sqlMapper.execute("SELECT MAX(CODLOTT) FROM W9LOTT WHERE CODGARA=" + gara.getId());
				Long idLotto = new Long(1);
				if (i != null) {
					idLotto = new Long(i) + 1;
				}
				lotto.setIdLotto(idLotto);
				this.attiMapper.pubblicaLotto(lotto);
			} else {
				//aggiorno il lotto
				lotto.setIdLotto(new Long(codlott));
				if (!this.nonAggiornareGaraSeEsiste()) {
					this.attiMapper.modificaPubblicazioneLotto(lotto);
				}
			}
			if (codlott == null || !this.nonAggiornareGaraSeEsiste()) {
				//cancello categorie e CPV secondari e inserisco
				this.sqlMapper.execute("DELETE FROM W9CPV WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9LOTTCATE WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9APPAFORN WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9APPALAV WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				this.sqlMapper.execute("DELETE FROM W9COND WHERE CODGARA = " + lotto.getIdGara() + " AND CODLOTT = " + lotto.getIdLotto());
				int numCategoria = 1;
				if (lotto.getCategorie()!=null) {
					for(CategoriaLottoEntry categoria:lotto.getCategorie()) {
						categoria.setIdGara(lotto.getIdGara());
						categoria.setIdLotto(lotto.getIdLotto());
						categoria.setNumCategoria(new Long(numCategoria));
						this.lottiMapper.insertCategoria(categoria);
						numCategoria++;
					}
				}
				
				int numCpv = 1;
				if (lotto.getCpvSecondari()!=null) {
					for(CpvLottoEntry cpv:lotto.getCpvSecondari()) {
						cpv.setIdGara(lotto.getIdGara());
						cpv.setIdLotto(lotto.getIdLotto());
						cpv.setNumCpv(new Long(numCpv));
						this.lottiMapper.insertCpv(cpv);
						numCpv++;
					}
				}
				
				int numAppaForn = 1;
				if (lotto.getModalitaAcquisizioneForniture()!=null) {
					for(AppaFornEntry modalita:lotto.getModalitaAcquisizioneForniture()) {
						modalita.setIdGara(lotto.getIdGara());
						modalita.setIdLotto(lotto.getIdLotto());
						modalita.setNumAppaForn(new Long(numAppaForn));
						this.lottiMapper.insertModalitaAcquisizioneForniture(modalita);
						numAppaForn++;
					}
				}
				
				int numAppaLav = 1;
				if (lotto.getTipologieLavori()!=null) {
					for(AppaLavEntry tipologia:lotto.getTipologieLavori()) {
						tipologia.setIdGara(lotto.getIdGara());
						tipologia.setIdLotto(lotto.getIdLotto());
						tipologia.setNumAppaLav(new Long(numAppaLav));
						this.lottiMapper.insertTipologiaLavori(tipologia);
						numAppaLav++;
					}
				}
				
				int numCondizione = 1;
				if (lotto.getMotivazioniProceduraNegoziata()!=null) {
					for(MotivazioneProceduraNegoziataEntry condizione:lotto.getMotivazioniProceduraNegoziata()) {
						condizione.setIdGara(lotto.getIdGara());
						condizione.setIdLotto(lotto.getIdLotto());
						condizione.setNumCondizione(new Long(numCondizione));
						this.lottiMapper.insertCondizione(condizione);
						numCondizione++;
					}
				}
			}
		}
		//inserisco flusso
		FlussoEntry flusso = new FlussoEntry();
		if (tipoInstallazione().equals(new Long(3))) {
			Long idFlusso = this.wgenChiaviManager.getNextId("W9FLUSSI");
		    flusso.setId(idFlusso);
		}
	    flusso.setArea(new Long(2));
	    flusso.setKey01(gara.getId());
	    flusso.setKey03(new Long(988));
	    flusso.setTipoInvio(tipoInvio);
	    flusso.setDataInvio(new Date());
	    flusso.setCodiceFiscaleSA(gara.getCodiceFiscaleSA());
	    ObjectMapper mapper = new ObjectMapper();
	    flusso.setJson(mapper.writeValueAsString(gara));
	    flusso.setOggetto(gara.getIdAnac());
	    flusso.setIdComunicazione(this.insertInboxOutbox(flusso, modalitaInvio, gara.getCodiceUnitaOrganizzativa()));
	    if (tipoInstallazione().equals(new Long(3)) && !modalitaInvio.equals("3")) {
	    	this.attiMapper.insertFlusso(flusso);
	    }
	    logger.info("garaLotti gara terminato con successo");
		return risultato;
	}

	/**
	 * Estrae il dettaglio di un atto.
	 *
	 * @param idRicevuto
	 *        identificativo dell'atto
	 * @return dati di dettaglio dell'atto; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public DettaglioAttoResult getDettaglioAtto(Long idRicevuto) {
		DettaglioAttoResult risultato = new DettaglioAttoResult();
		try {
			// estrazione dei dati generali dell'atto
			PubblicaAttoEntry atto = this.attiMapper.getDettaglioAtto(idRicevuto);

			if (atto == null) {
				// non ho estratto nulla, allora l'input era errato
				risultato.setError(DettaglioAttoResult.ERROR_NOT_FOUND);
			} else {
				atto.setIdRicevuto(idRicevuto);
				risultato.setAtto(atto);
				//documenti
				atto.setDocumenti(this.attiMapper.getDocumenti(atto.getIdGara(), atto.getNumeroPubblicazione()));
				//gara
				atto.setGara(this.attiMapper.getGara(atto.getIdGara(), null));
				//rup gara
				if (StringUtils.isNotEmpty(atto.getGara().getIdRup())) {
					atto.getGara().setTecnicoRup(this.tecniciMapper.getTecnico(atto.getGara().getIdRup()));
				}
				//lotti pubblicazione 
				atto.getGara().setLotti(this.attiMapper.getLotti(atto.getIdGara(), atto.getNumeroPubblicazione()));
				for(PubblicaLottoEntry lotto:atto.getGara().getLotti()) {
					lotto.setCategorie(this.lottiMapper.getUlterioriCategorie(atto.getIdGara(), lotto.getIdLotto()));
					lotto.setCpvSecondari(this.lottiMapper.getCpvSecondari(atto.getIdGara(), lotto.getIdLotto()));
				}
				//aggiudicatari
				atto.setAggiudicatari(this.attiMapper.getAggiudicatari(atto.getIdGara(), atto.getNumeroPubblicazione()));
				for(AggiudicatarioEntry aggiudicatario:atto.getAggiudicatari()) {
					aggiudicatario.setImpresa(this.impreseMapper.getOperatoreEconomico(aggiudicatario.getCodiceImpresa()));
				}
				//date pubblicazione
				Date primaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MIN(DATINV) FROM W9FLUSSI WHERE AREA = 2 and KEY03 = 901 and KEY01 = " + atto.getIdGara() + " AND KEY04 = " + atto.getNumeroPubblicazione());
				if (primaPubblicazione != null) {
					atto.setPrimaPubblicazioneSCP(primaPubblicazione);
				}
				Date ultimaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MAX(DATINV) FROM W9FLUSSI WHERE AREA = 2 and KEY03 = 901 and KEY01 = " + atto.getIdGara() + " AND KEY04 = " + atto.getNumeroPubblicazione());
				if (ultimaPubblicazione != null) {
					atto.setUltimaModificaSCP(ultimaPubblicazione);
				}
			}
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione del dettaglio di un atto con id ricevuto " + idRicevuto, t);
			risultato.setError(DettaglioAttoResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	}  
	
	/**
	 * Estrae il dettaglio di una gara.
	 *
	 * @param idRicevuto
	 *        identificativo della gara
	 * @return dati di dettaglio della gara; nel caso di errore si setta il campo error con un identificativo di errore
	 */
	public DettaglioGaraResult getDettaglioGara(Long idRicevuto) {
		DettaglioGaraResult risultato = new DettaglioGaraResult();
		try {
			// estrazione dei dati generali della gara
			PubblicaGaraEntry gara = this.attiMapper.getGara(null,idRicevuto);

			if (gara == null) {
				// non ho estratto nulla, allora l'input era errato
				risultato.setError(DettaglioGaraResult.ERROR_NOT_FOUND);
			} else {
				gara.setIdRicevuto(idRicevuto);
				risultato.setGara(gara);
				//lotti gara 
				gara.setLotti(this.attiMapper.getLotti(gara.getId(), null));
				for(PubblicaLottoEntry lotto:gara.getLotti()) {
					lotto.setCategorie(this.lottiMapper.getUlterioriCategorie(gara.getId(), lotto.getIdLotto()));
					lotto.setCpvSecondari(this.lottiMapper.getCpvSecondari(gara.getId(), lotto.getIdLotto()));
				}
				//rup gara
				if (StringUtils.isNotEmpty(gara.getIdRup())) {
					gara.setTecnicoRup(this.tecniciMapper.getTecnico(gara.getIdRup()));
				}
				//atti gara
				gara.setAtti(this.attiMapper.getAttiGara(gara.getId()));
				//date pubblicazione
				Date primaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MIN(DATINV) FROM W9FLUSSI WHERE AREA = 2 and KEY03 = 988 and KEY01 = " + gara.getId());
				if (primaPubblicazione != null) {
					gara.setPrimaPubblicazioneSCP(primaPubblicazione);
				}
				Date ultimaPubblicazione = this.sqlMapper.executeReturnDate("SELECT MAX(DATINV) FROM W9FLUSSI WHERE AREA = 2 and KEY03 = 988 and KEY01 = " + gara.getId());
				if (ultimaPubblicazione != null) {
					gara.setUltimaModificaSCP(ultimaPubblicazione);
				}
			}
		} catch (Throwable t) {
			// qualsiasi sia l'errore si traccia nel log e si ritorna un codice fisso ed il messaggio allegato all'eccezione come errore
			logger.error("Errore inaspettato durante l'estrazione del dettaglio di una gara con id ricevuto " + idRicevuto, t);
			risultato.setError(DettaglioGaraResult.ERROR_UNEXPECTED + ": " + t.getMessage());
		}

		return risultato;
	}  
	
	/**
	 * inserisce un record nella inbox per l'avvenuto salvataggio
	 * genera se richiesto un record in w9outbox 
	 * 
	 * @param flusso
	 *            flusso
	 * @param modalitaInvio
	 *            (2 - pubblica, 3 - pubblica senza inoltro SCP)
	 */
	private Long insertInboxOutbox(FlussoEntry flusso, String modalitaInvio, String codiceUnitaOrganizzativa) throws Exception{
		//Inserimento pubblicazione in W9Inbox
		Long idComun = this.wgenChiaviManager.getNextId("W9INBOX");
	    this.sqlMapper.insertInbox(idComun, new Date(), new Long(2), flusso.getJson());

	    if (modalitaInvio.equals("2") && !flusso.getKey03().equals(new Long(988))) {
	    	Long idComunOut = this.wgenChiaviManager.getNextId("W9OUTBOX");
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
