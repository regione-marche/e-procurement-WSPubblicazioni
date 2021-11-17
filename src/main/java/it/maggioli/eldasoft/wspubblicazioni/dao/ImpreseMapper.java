/*
 * Created on 05/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wspubblicazioni.dao;

import it.maggioli.eldasoft.wspubblicazioni.vo.ImpresaEntry;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * DAO Interface per l'estrazione delle informazioni relative alle imprese
 * mediante MyBatis.
 * 
 * @author Mirco.Franzoni
 */
public interface ImpreseMapper {

	@Insert("INSERT INTO IMPR (CODIMP,NOMIMP,NOMEST,NATGIUI,INDIMP,NCIIMP,LOCIMP,PROIMP,CAPIMP,TELIMP,FAXIMP,CFIMP,PIVIMP,NCCIAA,CGENIMP,NAZIMP) VALUES (#{id},#{ragioneSociale},#{ragioneSociale},#{formaGiuridica},#{indirizzo},#{numeroCivico},#{localita},#{provincia},#{cap},#{telefono},#{fax},#{codiceFiscale},#{partitaIva},#{numeroCCIAA},#{codiceSA},#{codiceNazione})")
	public void insertImpresa(ImpresaEntry impresa);

	@Update("UPDATE IMPR SET NOMIMP = #{ragioneSociale}, NOMEST = #{ragioneSociale}, NATGIUI = #{formaGiuridica}, INDIMP = #{indirizzo}, " + 
			"NCIIMP = #{numeroCivico}, LOCIMP = #{localita}, PROIMP = #{provincia}, CAPIMP = #{cap}, TELIMP = #{telefono}, " + 
			"FAXIMP = #{fax}, PIVIMP = #{partitaIva}, NCCIAA = #{numeroCCIAA}, NAZIMP = #{codiceNazione} "
			+ "WHERE CODIMP = #{id}")
	public void updateImpresa(ImpresaEntry impresa);
	/**
	 * Estrae i dati dell'operatore economico
	 *
	 * @param codiceOperatore
	 *        codiceOperatore
	 * @return dati dell'operatore economico
	 */
	@Select("select NOMIMP, NATGIUI, CFIMP, PIVIMP, NCCIAA "
			+"from IMPR where CODIMP = #{codiceOperatore}")
	@Results({
		@Result(property = "ragioneSociale", column = "NOMIMP"),
		@Result(property = "formaGiuridica", column = "NATGIUI"),
		@Result(property = "codiceFiscale", column = "CFIMP"),
		@Result(property = "partitaIva", column = "PIVIMP"),
		@Result(property = "numeroCCIAA", column = "NCCIAA")
	})
	public ImpresaEntry getOperatoreEconomico(@Param("codiceOperatore") String codiceOperatore);

}
