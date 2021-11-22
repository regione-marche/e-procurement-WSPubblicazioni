package it.maggioli.eldasoft.wspubblicazioni.dao;

import it.maggioli.eldasoft.wspubblicazioni.vo.tecnici.DatiGeneraliTecnicoEntry;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * DAO Interface per l'estrazione delle informazioni relative ai soggetti
 * mediante MyBatis.
 * 
 * @author Alessandro.Sernagiotto
 */
public interface TecniciMapper {

	@Insert("INSERT INTO TECNI (CODTEC,COGTEI,NOMETEI,NOMTEC,INDTEC,NCITEC,LOCTEC,PROTEC,CAPTEC,TELTEC,FAXTEC,CFTEC,EMATEC, CITTEC, CGENTEI) VALUES (#{codice},#{cognome},#{nome},#{nomeCognome},#{indirizzo},#{civico},#{localita},#{provincia},#{cap},#{telefono},#{fax},#{cfPiva},#{mail},#{luogoIstat},#{codiceSA})")
	public void insertTecnico(DatiGeneraliTecnicoEntry tecnico);

	/**
	 * Estrae i dati del tecnico
	 *
	 * @param codiceTecnico
	 *        identificativo del tecnico
	 * @return dati del tecnico
	 */
	@Select("select COGTEI, NOMETEI, INDTEC, NCITEC, PROTEC, CFTEC, CITTEC "
			+"from TECNI where CODTEC = #{codiceTecnico}")
	@Results({
		@Result(property = "cognome", column = "COGTEI"),
		@Result(property = "nome", column = "NOMETEI"),
		@Result(property = "indirizzo", column = "INDTEC"),
		@Result(property = "civico", column = "NCITEC"),
		@Result(property = "provincia", column = "PROTEC"),
		@Result(property = "cap", column = "CAPTEC"),
		@Result(property = "cfPiva", column = "CFTEC"),
		@Result(property = "luogoIstat", column = "CITTEC")
	})
	public DatiGeneraliTecnicoEntry getTecnico(@Param("codiceTecnico") String codiceTecnico);

}
