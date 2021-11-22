package it.maggioli.eldasoft.wspubblicazioni.dao;

import java.util.List;

import it.maggioli.eldasoft.wspubblicazioni.vo.FlussoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.AllegatoAvvisiEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.avvisi.PubblicaAvvisoEntry;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * DAO Interface per l'estrazione delle informazioni relative agli avvisi
 * mediante MyBatis.
 * 
 * @author Alessandro.Sernagiotto
 */
public interface AvvisiMapper {


	/**
	 * Inserisce le informazioni per la pubblicazione di un avviso.
	 */
	@Insert("INSERT INTO AVVISO (CODEIN,IDAVVISO,CODSISTEMA,IDUFFICIO,TIPOAVV,RUP,DATAAVV,DESCRI,CIG,CUP,CUIINT,DATASCADENZA,INDSEDE,COMSEDE,PROSEDE,ID_GENERATO,ID_CLIENT,SYSCON) "
			+ "VALUES (#{codiceSA},#{id},#{codiceSistema},#{idUfficio},#{tipologia},#{codiceRup},#{data},#{descrizione},#{cig},#{cup},#{cui},#{scadenza},#{indirizzo},#{comune},#{provincia},#{idRicevuto},#{clientId},#{syscon})")
			public void pubblicaAvviso(PubblicaAvvisoEntry avviso);

	/**
	 * Modifica le informazioni per la pubblicazione di un avviso.
	 */
	@Update("UPDATE AVVISO SET IDUFFICIO = #{idUfficio}, TIPOAVV = #{tipologia}, RUP = #{codiceRup}, DATAAVV = #{data},DESCRI = #{descrizione},CIG = #{cig}, CUP = #{cup}, CUIINT = #{cui}, DATASCADENZA = #{scadenza}, SYSCON = #{syscon}, "
			+ " INDSEDE = #{indirizzo}, COMSEDE = #{comune}, PROSEDE = #{provincia} "
			+ " WHERE ID_GENERATO = #{idRicevuto}")
			public void modificaPubblicazioneAvviso(PubblicaAvvisoEntry avviso);


	@Delete("DELETE FROM W9DOCAVVISO  "
			+ " WHERE CODEIN = #{codiceSA} AND IDAVVISO = #{id} AND CODSISTEMA = #{codiceSistema} ")
			public void deleteAllDoc(PubblicaAvvisoEntry avviso);


	@Insert("INSERT into W9DOCAVVISO(CODEIN, IDAVVISO, CODSISTEMA, NUMDOC, TITOLO, FILE_ALLEGATO, URL, TIPO_FILE)" +
	" VALUES(#{codiceSA}, #{id}, #{codiceSistema}, #{nrDoc}, #{titolo}, #{file,jdbcType=BINARY}, #{url}, #{tipoFile})")
	public void insertAllegato(AllegatoAvvisiEntry file);

	@Insert("INSERT into W9FLUSSI(IDFLUSSO, AREA, KEY01, KEY02, KEY03, KEY04, TINVIO2, DATINV, NOTEINVIO, CODCOMP, CFSA, AUTORE, XML, CODOGG, DATIMP, IDCOMUN)" +
	" VALUES(#{id}, #{area}, #{key01}, #{key02}, #{key03}, #{key04}, #{tipoInvio}, #{dataInvio}, #{note}, #{idAutore}, #{codiceFiscaleSA}, #{autore}, #{json}, #{oggetto}, #{dataInvio}, #{idComunicazione})")
	public void insertFlusso(FlussoEntry flusso);

	@Insert("INSERT INTO UFFICI (ID, CODEIN, DENOM) VALUES (#{id},#{codein},#{denom})")
	public void insertUfficio(@Param("id")Long id, @Param("codein")String codein, @Param("denom")String denom);

	/**
	 * Estrae i dati dell'avviso
	 *
	 * @param idRicevuto
	 *        identificativo dell'avviso
	 * @return dati della gara
	 */
	@Select("select AVVISO.CODEIN, IDAVVISO, CODSISTEMA, TIPOAVV, RUP, DATAAVV, DESCRI, CIG, CUP,CUIINT, DATASCADENZA, UFFINT.CFEIN, UFFINT.CODEIN_UO,UFFICI.DENOM, INDSEDE, COMSEDE, PROSEDE "
			+"from AVVISO LEFT JOIN UFFINT ON AVVISO.CODEIN=UFFINT.CODEIN LEFT JOIN UFFICI ON AVVISO.IDUFFICIO=UFFICI.ID "
			+"WHERE ID_GENERATO = #{idRicevuto}")
			@Results({
				@Result(property = "id", column = "IDAVVISO"),
				@Result(property = "codiceSistema", column = "CODSISTEMA"),
				@Result(property = "codiceSA", column = "CODEIN"),
				@Result(property = "codiceFiscaleSA", column = "CFEIN"),
				@Result(property = "codiceUnitaOrganizzativa", column = "CODEIN_UO"),
				@Result(property = "ufficio", column = "DENOM"),
				@Result(property = "tipologia", column = "TIPOAVV"),
				@Result(property = "data", column = "DATAAVV"),
				@Result(property = "descrizione", column = "DESCRI"),
				@Result(property = "cig", column = "CIG"),
				@Result(property = "cup", column = "CUP"),
				@Result(property = "cui", column = "CUIINT"),
				@Result(property = "scadenza", column = "DATASCADENZA"),
				@Result(property = "codiceRup", column = "RUP"),
				@Result(property = "indirizzo", column = "INDSEDE"),
				@Result(property = "comune", column = "COMSEDE"),
				@Result(property = "provincia", column = "PROSEDE")
			})
			public PubblicaAvvisoEntry getAvviso(@Param("idRicevuto") Long idRicevuto);

	/**
	 * Estrae i documenti dell'avviso
	 *
	 * @param codiceSA
	 *        codiceSA
	 * @param id
	 *        id
	 * @param codiceSistema
	 *        codiceSistema
	 * @return documenti dell'avviso
	 */
	@Select("select TITOLO, URL "
			+"from W9DOCAVVISO where CODEIN = #{codiceSA} and IDAVVISO = #{id} and CODSISTEMA = #{codiceSistema} ORDER BY NUMDOC")
			@Results({
				@Result(property = "titolo", column = "TITOLO"),
				@Result(property = "url", column = "URL")
			})
			public List<AllegatoAvvisiEntry> getDocumenti(@Param("codiceSA") String codiceSA, @Param("id") Long id, @Param("codiceSistema") Long codiceSistema);

}
