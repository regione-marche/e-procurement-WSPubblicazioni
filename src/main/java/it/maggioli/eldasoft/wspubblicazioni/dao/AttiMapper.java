package it.maggioli.eldasoft.wspubblicazioni.dao;

import it.maggioli.eldasoft.wspubblicazioni.vo.gare.AttoGaraEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.PubblicaGaraEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.gare.PubblicazioneBandoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.PubblicaLottoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.AggiudicatarioEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.AllegatoAttiEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.PubblicaAttoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.pubblicazioni.TipoAttoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.FlussoEntry;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface AttiMapper {
	
    /**
     * Estrae gli attributi di tutte le tipologie di atti.
     *
     * @return attributi di tutte le tipologie di atti.
     */
    @Select("select ID, NOME, CL_WHERE_VIS, CAMPI_VIS, CAMPI_OBB, TIPO from w9cf_pubb order by NUMORD")
    @Results({
      @Result(property = "id", column = "ID"),
      @Result(property = "nome", column = "NOME"),
      @Result(property = "clausolaWhere", column = "CL_WHERE_VIS"),
      @Result(property = "campiVisualizzati", column = "CAMPI_VIS"),
      @Result(property = "campiObbligatori", column = "CAMPI_OBB"),
      @Result(property = "tipo", column = "TIPO")
    })
    public List<TipoAttoEntry> getTipoAtti();


    /**
     * Estrae gli attributi di un tipo pubblicazione.
     *
     * @param id
     *        Codice tipo pubblicazione
     * @return attributi di un tipo pubblicazione
     */
    @Select("select ID, NOME, CL_WHERE_VIS, CAMPI_VIS, CAMPI_OBB, TIPO from w9cf_pubb where id=#{id}")
    @Results({
      @Result(property = "id", column = "ID"),
      @Result(property = "nome", column = "NOME"),
      @Result(property = "clausolaWhere", column = "CL_WHERE_VIS"),
      @Result(property = "campiVisualizzati", column = "CAMPI_VIS"),
      @Result(property = "campiObbligatori", column = "CAMPI_OBB"),
      @Result(property = "tipo", column = "TIPO")
    })
    public TipoAttoEntry getTipoAtto(@Param("id") Long id);

    @Insert("INSERT into W9DOCGARA(CODGARA, NUM_PUBB, NUMDOC, TITOLO, FILE_ALLEGATO, URL, TIPO_FILE)" +
    		  " VALUES(#{idGara}, #{nrPubblicazione}, #{nrDoc}, #{titolo}, #{file,jdbcType=BINARY}, #{url}, #{tipoFile})")
    public void insertAllegato(AllegatoAttiEntry file);

    @Insert("INSERT into W9GARA(CODGARA, OGGETTO, PROV_DATO, SITUAZIONE, IDAVGARA, FLAG_ENTE_SPECIALE, ID_MODO_INDIZIONE," +
  		  " TIPO_APP, IMPORTO_GARA, RUP, RIC_ALLUV, CODEIN, IDCC, INDSEDE, COMSEDE," +
  		  " PROSEDE, FLAG_SA_AGENTE, ID_TIPOLOGIA_SA, DENOM_SA_AGENTE, CF_SA_AGENTE, TIPOLOGIA_PROCEDURA, FLAG_CENTRALE_STIPULA," + 
  		  " IDUFFICIO, CIG_ACCQUADRO, CAM, SISMA, DGURI, DSCADE, SOMMA_URGENZA, DURATA_ACCQUADRO, VER_SIMOG, DATA_PUBBLICAZIONE, ID_F_DELEGATE, ID_GENERATO, ID_CLIENT, SYSCON)" +
  		  " VALUES(#{id}, #{oggetto}, #{provenienzaDato}, 1, #{idAnac}, #{settore}, #{modoIndizione}, #{realizzazione}, #{importoGara}," +
  		  " #{idRup},#{ricostruzioneAlluvione},#{idEnte},#{idCentroCosto},#{indirizzo},#{comune},#{provincia}," +
  		  " #{saAgente},#{tipoSA},#{nomeSA},#{cfAgente},#{tipoProcedura},#{centraleCommittenza}," +
  		  " #{idUfficio},#{cigAccQuadro},#{criteriAmbientali},#{sisma},#{dataPubblicazione},#{dataScadenza},#{sommaUrgenza},#{durataAccordoQuadro},#{versioneSimog},#{dataPerfezionamentoBando},#{funzioniDelegate},#{idRicevuto},#{clientId},#{syscon})")
    public void pubblicaGara(PubblicaGaraEntry gara);
    
    @Update("UPDATE W9GARA SET CODGARA = #{id}, OGGETTO = #{oggetto}, PROV_DATO = #{provenienzaDato}, IDAVGARA = #{idAnac}, FLAG_ENTE_SPECIALE = #{settore}, ID_MODO_INDIZIONE = #{modoIndizione}," +
    		  " TIPO_APP = #{realizzazione}, IMPORTO_GARA = #{importoGara}, RUP = #{idRup}, RIC_ALLUV = #{ricostruzioneAlluvione}, CODEIN = #{idEnte}, INDSEDE = #{indirizzo}, COMSEDE = #{comune}," +
    		  " PROSEDE = #{provincia}, FLAG_SA_AGENTE = #{saAgente}, ID_TIPOLOGIA_SA = #{tipoSA}, DENOM_SA_AGENTE = #{nomeSA}, CF_SA_AGENTE = #{cfAgente}, TIPOLOGIA_PROCEDURA = #{tipoProcedura}, FLAG_CENTRALE_STIPULA = #{centraleCommittenza}," + 
    		  " IDUFFICIO = #{idUfficio}, CIG_ACCQUADRO = #{cigAccQuadro}, CAM = #{criteriAmbientali}, SISMA = #{sisma}, DGURI = #{dataPubblicazione}, DSCADE = #{dataScadenza}, SOMMA_URGENZA = #{sommaUrgenza}," +
    		  " DURATA_ACCQUADRO = #{durataAccordoQuadro}, VER_SIMOG = #{versioneSimog}, DATA_PUBBLICAZIONE = #{dataPerfezionamentoBando}, ID_F_DELEGATE = #{funzioniDelegate}, SYSCON = #{syscon} WHERE ID_GENERATO = #{idRicevuto}") 
    public void modificaPubblicazioneGara(PubblicaGaraEntry gara);
    
    @Insert("INSERT into W9PUBB(CODGARA, CODLOTT, NUM_APPA, NUM_PUBB, DATA_GUCE, DATA_GURI, DATA_ALBO, " +
  		  " QUOTIDIANI_NAZ, QUOTIDIANI_REG, PROFILO_COMMITTENTE, SITO_MINISTERO_INF_TRASP, SITO_OSSERVATORIO_CP, DATA_BORE, PERIODICI)" +
  		  " VALUES(#{idGara}, 1, 1, 1, #{dataGuce}, #{dataGuri}, #{dataAlbo}, " +
  		  " #{quotidianiNazionali}, #{quotidianiLocali}, #{profiloCommittente}, #{profiloInfTrasp}, #{profiloOsservatorio}, #{dataBore}, #{periodici})")
    public void insertBando(PubblicazioneBandoEntry bando);
    
    @Insert("INSERT into W9PUBBLICAZIONI(CODGARA, NUM_PUBB, TIPDOC, DESCRIZ, DATAPUBB, DATASCAD, DATA_DECRETO, " +
    		  " DATA_PROVVEDIMENTO, NUM_PROVVEDIMENTO, DATA_STIPULA, NUM_REPERTORIO, PERC_RIBASSO_AGG, PERC_OFF_AUMENTO, IMPORTO_AGGIUDICAZIONE, DATA_VERB_AGGIUDICAZIONE, URL_COMMITTENTE, URL_EPROC, ID_GENERATO)" +
    		  " VALUES(#{idGara}, #{numeroPubblicazione}, #{tipoDocumento}, #{eventualeSpecificazione}, #{dataPubblicazione}, #{dataScadenza}, #{dataDecreto}, " +
    		  " #{dataProvvedimento}, #{numeroProvvedimento}, #{dataStipula}, #{numeroRepertorio}, #{ribassoAggiudicazione}, #{offertaAumento}, #{importoAggiudicazione}, #{dataAggiudicazione},#{urlCommittente},#{urlEProcurement},#{idRicevuto})")
    public void pubblicaAtto(PubblicaAttoEntry pubblicazione);
    
    @Update("UPDATE W9PUBBLICAZIONI SET CODGARA = #{idGara}, NUM_PUBB = #{numeroPubblicazione}, TIPDOC = #{tipoDocumento}, DESCRIZ = #{eventualeSpecificazione}, DATAPUBB = #{dataPubblicazione}, DATASCAD = #{dataScadenza}, DATA_DECRETO = #{dataDecreto}, " +
  		  " DATA_PROVVEDIMENTO = #{dataProvvedimento}, NUM_PROVVEDIMENTO = #{numeroProvvedimento}, DATA_STIPULA = #{dataStipula}, NUM_REPERTORIO = #{numeroRepertorio}, PERC_RIBASSO_AGG = #{ribassoAggiudicazione}, PERC_OFF_AUMENTO = #{offertaAumento}, IMPORTO_AGGIUDICAZIONE = #{importoAggiudicazione}, DATA_VERB_AGGIUDICAZIONE = #{dataAggiudicazione}, URL_COMMITTENTE = #{urlCommittente}, URL_EPROC = #{urlEProcurement} WHERE ID_GENERATO = #{idRicevuto} ")
    public void modificaPubblicazioneAtto(PubblicaAttoEntry pubblicazione);

    @Update("UPDATE W9LOTT SET " 
			+ "OGGETTO = #{oggetto}, "
			+ "IMPORTO_LOTTO = #{importoLotto}, "
			+ "IMPORTO_ATTUAZIONE_SICUREZZA = #{importoSicurezza}, "
			+ "IMPORTO_TOT = #{importoTotale}, "
			+ "CPV = #{cpv}, "
			+ "ID_SCELTA_CONTRAENTE = #{idSceltaContraente}, "
			+ "ID_SCELTA_CONTRAENTE_50 = #{idSceltaContraente50}, "
			+ "ID_CATEGORIA_PREVALENTE = #{categoria}, "
			+ "NLOTTO = #{numeroLotto}, "
			+ "CLASCAT = #{classe}, "
			+ "COMCON = #{lottoPrecedente}, "
			+ "TIPO_CONTRATTO = #{tipoAppalto}, "
			+ "FLAG_ENTE_SPECIALE = #{settore}, "
			+ "ID_MODO_GARA = #{criterioAggiudicazione}, "
			+ "LUOGO_ISTAT = #{luogoIstat}, "
			+ "LUOGO_NUTS = #{luogoNuts}, "
			+ "CIG = #{cig}, "
			+ "CUPESENTE = #{cupEsente}, "
			+ "CUP = #{cup}, "
			+ "URL_EPROC = #{urlPiattaformaTelematica}, "
			+ "URL_COMMITTENTE = #{urlCommittente}, "
			+ "CUIINT = #{cui}, "
			+ "SOMMA_URGENZA = #{sommaUrgenza}, "
			+ "MANOD = #{manodopera}, "
			+ "CODINT = #{codiceIntervento}, "
			+ "ID_TIPO_PRESTAZIONE = #{prestazioniComprese}, "
			+ "ART_E1 = #{contrattoEsclusoArt19e26}, "
			+ "ART_E2 = #{contrattoEsclusoArt16e17e18}, "
			+ "EXSOTTOSOGLIA = #{exSottosoglia}, "
			+ "CONTRATTO_ESCLUSO_ALLEGGERITO = #{contrattoEsclusoAlleggerito}, "
			+ "ESCLUSIONE_REGIME_SPECIALE = #{esclusioneRegimeSpeciale}, "
			+ "CIG_MASTER_ML = #{cigMaster} "
			+ " WHERE CODGARA = #{idGara} AND CODLOTT = #{idLotto} ")
	public void modificaPubblicazioneLotto(PubblicaLottoEntry lotto);

    @Insert("INSERT into W9LOTT (CODGARA, CODLOTT, OGGETTO, SITUAZIONE, IMPORTO_LOTTO, IMPORTO_ATTUAZIONE_SICUREZZA, IMPORTO_TOT, CPV,"
			+ " ID_SCELTA_CONTRAENTE, ID_SCELTA_CONTRAENTE_50, ID_CATEGORIA_PREVALENTE, NLOTTO, CLASCAT, COMCON, DESCOM, CIGCOM,"
			+ " TIPO_CONTRATTO, FLAG_ENTE_SPECIALE, ID_MODO_GARA, LUOGO_ISTAT, LUOGO_NUTS, CIG, CUPESENTE,"
			+ " CUP, RUP, URL_EPROC, URL_COMMITTENTE, CUIINT, SOMMA_URGENZA, MANOD, CODINT, ID_TIPO_PRESTAZIONE, ART_E1, ART_E2, "
			+ " ID_SCHEDA_LOCALE, ID_SCHEDA_SIMOG, EXSOTTOSOGLIA, CONTRATTO_ESCLUSO_ALLEGGERITO, ESCLUSIONE_REGIME_SPECIALE, CIG_MASTER_ML)"
			+ " VALUES (#{idGara}, #{idLotto}, #{oggetto}, 1, #{importoLotto}, #{importoSicurezza}, #{importoTotale}, #{cpv},"
			+ " #{idSceltaContraente},#{idSceltaContraente50},#{categoria},#{numeroLotto},#{classe},#{lottoPrecedente},#{motivo}, #{cigCollegato},"
			+ " #{tipoAppalto},#{settore},#{criterioAggiudicazione},#{luogoIstat},#{luogoNuts},#{cig},#{cupEsente},"
			+ " #{cup},#{idRup},#{urlPiattaformaTelematica},#{urlCommittente},#{cui},#{sommaUrgenza},#{manodopera},#{codiceIntervento},"
			+ " #{prestazioniComprese},#{contrattoEsclusoArt19e26},#{contrattoEsclusoArt16e17e18},#{idSchedaLocale},#{idSchedaSimog},#{exSottosoglia},"
			+ " #{contrattoEsclusoAlleggerito},#{esclusioneRegimeSpeciale},#{cigMaster})")
	public void pubblicaLotto(PubblicaLottoEntry lotto);
    
    @Insert("INSERT INTO UFFICI (ID, CODEIN, DENOM) VALUES (#{id},#{codein},#{denom})")
	public void insertUfficio(@Param("id")Long id, @Param("codein")String codein, @Param("denom")String denom);

    @Insert("INSERT into ESITI_AGGIUDICATARI(CODGARA, NUM_PUBB, NUM_AGGI, ID_TIPOAGG, RUOLO, CODIMP, ID_GRUPPO)" +
	  " VALUES(#{idGara}, #{numeroPubblicazione}, #{numeroAggiudicatario}, #{tipoAggiudicatario}, #{ruolo}, #{codiceImpresa}, #{idGruppo})")
	  public void insertAggiudicatario(AggiudicatarioEntry aggiudicatario);
    
    @Insert("INSERT into W9FLUSSI(IDFLUSSO, AREA, KEY01, KEY02, KEY03, KEY04, TINVIO2, DATINV, NOTEINVIO, CODCOMP, CFSA, AUTORE, XML, CODOGG, DATIMP, IDCOMUN)" +
	  " VALUES(#{id}, #{area}, #{key01}, #{key02}, #{key03}, #{key04}, #{tipoInvio}, #{dataInvio}, #{note}, #{idAutore}, #{codiceFiscaleSA}, #{autore}, #{json}, #{oggetto}, #{dataInvio}, #{idComunicazione})")
	public void insertFlusso(FlussoEntry flusso);
    
    /**
	 * Estrae i dati generali dell'atto
	 *
	 * @param idGenerato
	 *        identificativo dell'atto
	 * @return dati dell'atto
	 */
	@Select("select CODGARA, NUM_PUBB, TIPDOC, DESCRIZ, DATAPUBB, DATASCAD, DATA_DECRETO, DATA_PROVVEDIMENTO, NUM_PROVVEDIMENTO, DATA_STIPULA, "
			+"NUM_REPERTORIO, PERC_RIBASSO_AGG, PERC_OFF_AUMENTO, IMPORTO_AGGIUDICAZIONE, DATA_VERB_AGGIUDICAZIONE, URL_COMMITTENTE, URL_EPROC "
			+"from W9PUBBLICAZIONI where ID_GENERATO = #{idGenerato}")
	@Results({
		@Result(property = "idGara", column = "CODGARA"),
		@Result(property = "numeroPubblicazione", column = "NUM_PUBB"),
		@Result(property = "tipoDocumento", column = "TIPDOC"),
		@Result(property = "eventualeSpecificazione", column = "DESCRIZ"),
		@Result(property = "dataPubblicazione", column = "DATAPUBB"),
		@Result(property = "dataScadenza", column = "DATASCAD"),
		@Result(property = "dataDecreto", column = "DATA_DECRETO"),
		@Result(property = "dataProvvedimento", column = "DATA_PROVVEDIMENTO"),
		@Result(property = "numeroProvvedimento", column = "NUM_PROVVEDIMENTO"),
		@Result(property = "dataStipula", column = "DATA_STIPULA"),
		@Result(property = "numeroRepertorio", column = "NUM_REPERTORIO"),
		@Result(property = "ribassoAggiudicazione", column = "PERC_RIBASSO_AGG"),
		@Result(property = "offertaAumento", column = "PERC_OFF_AUMENTO"),
		@Result(property = "importoAggiudicazione", column = "IMPORTO_AGGIUDICAZIONE"),
		@Result(property = "dataAggiudicazione", column = "DATA_VERB_AGGIUDICAZIONE"),
		@Result(property = "urlCommittente", column = "URL_COMMITTENTE"),
		@Result(property = "urlEProcurement", column = "URL_EPROC")
	})
	public PubblicaAttoEntry getDettaglioAtto(@Param("idGenerato") Long idGenerato);

	/**
	 * Estrae i documenti dell'atto
	 *
	 * @param idGara
	 *        idGara
	 * @param numeroPubblicazione
	 *        numeroPubblicazione
	 * @return dati dell'atto
	 */
	@Select("select TITOLO, URL "
			+"from W9DOCGARA where CODGARA = #{idGara} and NUM_PUBB = #{numeroPubblicazione} ORDER BY NUMDOC")
	@Results({
		@Result(property = "titolo", column = "TITOLO"),
		@Result(property = "url", column = "URL")
	})
	public List<AllegatoAttiEntry> getDocumenti(@Param("idGara") Long idGara, @Param("numeroPubblicazione") Long numeroPubblicazione);

	/**
	 * Estrae i dati della gara
	 *
	 * @param idGara
	 *        identificativo della gara
	 * @return dati della gara
	 */
	@Select("<script>select CODGARA, OGGETTO, IDAVGARA, FLAG_ENTE_SPECIALE, TIPO_APP, IMPORTO_GARA, RUP, UFFINT.CFEIN, UFFINT.CODEIN_UO, INDSEDE, COMSEDE, PROSEDE, "
			+"FLAG_SA_AGENTE, ID_TIPOLOGIA_SA, DENOM_SA_AGENTE, CF_SA_AGENTE, TIPOLOGIA_PROCEDURA, FLAG_CENTRALE_STIPULA, UFFICI.DENOM, CIG_ACCQUADRO, ID_GENERATO "
			+"from W9GARA LEFT JOIN UFFINT ON W9GARA.CODEIN=UFFINT.CODEIN LEFT JOIN UFFICI ON W9GARA.IDUFFICIO=UFFICI.ID where "
			+"<if test='idGara != null'> CODGARA = #{idGara}</if>"
			+"<if test='idGara == null'> ID_GENERATO = #{idRicevuto}</if>"
			+"</script>")
	@Results({
		@Result(property = "id", column = "CODGARA"),
		@Result(property = "oggetto", column = "OGGETTO"),
		@Result(property = "idAnac", column = "IDAVGARA"),
		@Result(property = "settore", column = "FLAG_ENTE_SPECIALE"),
		@Result(property = "realizzazione", column = "TIPO_APP"),
		@Result(property = "importoGara", column = "IMPORTO_GARA"),
		@Result(property = "idRup", column = "RUP"),
		@Result(property = "codiceFiscaleSA", column = "CFEIN"),
		@Result(property = "codiceUnitaOrganizzativa", column = "CODEIN_UO"),
		@Result(property = "indirizzo", column = "INDSEDE"),
		@Result(property = "comune", column = "COMSEDE"),
		@Result(property = "provincia", column = "PROSEDE"),
		@Result(property = "saAgente", column = "FLAG_SA_AGENTE"),
		@Result(property = "tipoSA", column = "ID_TIPOLOGIA_SA"),
		@Result(property = "nomeSA", column = "DENOM_SA_AGENTE"),
		@Result(property = "cfAgente", column = "CF_SA_AGENTE"),
		@Result(property = "tipoProcedura", column = "TIPOLOGIA_PROCEDURA"),
		@Result(property = "centraleCommittenza", column = "FLAG_CENTRALE_STIPULA"),
		@Result(property = "ufficio", column = "DENOM"),
		@Result(property = "cigAccQuadro", column = "CIG_ACCQUADRO"),
		@Result(property = "idRicevuto", column = "ID_GENERATO"),
	})
	public PubblicaGaraEntry getGara(@Param("idGara") Long idGara, @Param("idRicevuto") Long idRicevuto);

	/**
	 * Estrae i lotti relativi all'atto
	 *
	 * @param idGara
	 *        idGara
	 * @param numeroPubblicazione
	 *        numeroPubblicazione
	 * @return lista lotti relativi all'atto
	 */
	@Select("<script>select CODLOTT, OGGETTO, IMPORTO_LOTTO, IMPORTO_ATTUAZIONE_SICUREZZA, CPV, ID_SCELTA_CONTRAENTE_50, ID_CATEGORIA_PREVALENTE, NLOTTO, CLASCAT, "
			+"TIPO_CONTRATTO, ID_MODO_GARA, LUOGO_ISTAT, LUOGO_NUTS, CIG, CUPESENTE, CUP, CUIINT, CONTRATTO_ESCLUSO_ALLEGGERITO, ESCLUSIONE_REGIME_SPECIALE "
			+"from W9LOTT where CODGARA = #{idGara} and CODLOTT in (SELECT CODLOTT FROM W9PUBLOTTO WHERE CODGARA = #{idGara} "
			+"<if test='numeroPubblicazione != null'> and NUM_PUBB = #{numeroPubblicazione} </if>) ORDER BY CODLOTT "
			+"</script>")
	@Results({
		@Result(property = "idLotto", column = "CODLOTT"),
		@Result(property = "oggetto", column = "OGGETTO"),
		@Result(property = "importoLotto", column = "IMPORTO_LOTTO"),
		@Result(property = "importoSicurezza", column = "IMPORTO_ATTUAZIONE_SICUREZZA"),
		@Result(property = "cpv", column = "CPV"),
		@Result(property = "idSceltaContraente50", column = "ID_SCELTA_CONTRAENTE_50"),
		@Result(property = "categoria", column = "ID_CATEGORIA_PREVALENTE"),
		@Result(property = "numeroLotto", column = "NLOTTO"),
		@Result(property = "classe", column = "CLASCAT"),
		@Result(property = "tipoAppalto", column = "TIPO_CONTRATTO"),
		@Result(property = "criterioAggiudicazione", column = "ID_MODO_GARA"),
		@Result(property = "luogoIstat", column = "LUOGO_ISTAT"),
		@Result(property = "luogoNuts", column = "LUOGO_NUTS"),
		@Result(property = "cig", column = "CIG"),
		@Result(property = "cupEsente", column = "CUPESENTE"),
		@Result(property = "cup", column = "CUP"),
		@Result(property = "cui", column = "CUIINT"),
		@Result(property = "contrattoEsclusoAlleggerito", column = "CONTRATTO_ESCLUSO_ALLEGGERITO"),
		@Result(property = "esclusioneRegimeSpeciale", column = "ESCLUSIONE_REGIME_SPECIALE")
	})
	public List<PubblicaLottoEntry> getLotti(@Param("idGara") Long idGara, @Param("numeroPubblicazione") Long numeroPubblicazione);

	/**
	 * Estrae gli eventuali aggiudicatari dell'atto
	 *
	 * @param idGara
	 *        idGara
	 * @param numeroPubblicazione
	 *        numeroPubblicazione
	 * @return dati dell'atto
	 */
	@Select("select ID_TIPOAGG, RUOLO, CODIMP, ID_GRUPPO "
			+"from ESITI_AGGIUDICATARI where CODGARA = #{idGara} and NUM_PUBB = #{numeroPubblicazione} ORDER BY NUM_AGGI")
	@Results({
		@Result(property = "tipoAggiudicatario", column = "ID_TIPOAGG"),
		@Result(property = "ruolo", column = "RUOLO"),
		@Result(property = "codiceImpresa", column = "CODIMP"),
		@Result(property = "idGruppo", column = "ID_GRUPPO")
	})
	public List<AggiudicatarioEntry> getAggiudicatari(@Param("idGara") Long idGara, @Param("numeroPubblicazione") Long numeroPubblicazione);

	/**
	 * Estrae gli eventuali atti della gara
	 *
	 * @param idGara
	 *        idGara
	 * @return lista atti della gara
	 */
	@Select("select TIPDOC, ID_GENERATO "
			+"from W9PUBBLICAZIONI where CODGARA = #{idGara} ORDER BY NUM_PUBB")
	@Results({
		@Result(property = "tipoDocumento", column = "TIPDOC"),
		@Result(property = "idRicevuto", column = "ID_GENERATO")
	})
	public List<AttoGaraEntry> getAttiGara(@Param("idGara") Long idGara);

}