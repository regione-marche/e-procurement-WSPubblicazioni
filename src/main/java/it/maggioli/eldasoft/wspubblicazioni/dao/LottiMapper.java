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

import java.util.List;

import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.AppaFornEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.AppaLavEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.CategoriaLottoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.CpvLottoEntry;
import it.maggioli.eldasoft.wspubblicazioni.vo.lotti.MotivazioneProceduraNegoziataEntry;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

/**
 * DAO Interface per l'estrazione delle informazioni relative ai lotti mediante
 * MyBatis.
 * 
 * @author Mirco.Franzoni
 */
public interface LottiMapper {

	@Insert("INSERT into W9LOTTCATE(CODGARA, CODLOTT, NUM_CATE, CATEGORIA, CLASCAT, SCORPORABILE, SUBAPPALTABILE)" +
	" VALUES(#{idGara}, #{idLotto}, #{numCategoria}, #{categoria}, #{classe}, #{scorporabile}, #{subappaltabile})")
	public void insertCategoria(CategoriaLottoEntry lottcate);
	
	@Insert("INSERT into W9CPV(CODGARA, CODLOTT, NUM_CPV, CPV)" +
	" VALUES(#{idGara}, #{idLotto}, #{numCpv}, #{cpv})")
	public void insertCpv(CpvLottoEntry cpvLotto);
	
	@Insert("INSERT into W9APPAFORN(CODGARA, CODLOTT, NUM_APPAF, ID_APPALTO)" +
	" VALUES(#{idGara}, #{idLotto}, #{numAppaForn}, #{modalitaAcquisizione})")
	public void insertModalitaAcquisizioneForniture(AppaFornEntry modalita);
	
	@Insert("INSERT into W9APPALAV(CODGARA, CODLOTT, NUM_APPAL, ID_APPALTO)" +
	" VALUES(#{idGara}, #{idLotto}, #{numAppaLav}, #{tipologiaLavoro})")
	public void insertTipologiaLavori(AppaLavEntry tipologia);
	
	@Insert("INSERT into W9COND(CODGARA, CODLOTT, NUM_COND, ID_CONDIZIONE)" +
	" VALUES(#{idGara}, #{idLotto}, #{numCondizione}, #{condizione})")
	public void insertCondizione(MotivazioneProceduraNegoziataEntry motivazione);
	
	/**
	 * Estrae le ulteriori categorie del lotto
	 *
	 * @param idGara
	 *        idGara
	 * @param codLotto
	 *        codLotto
	 * @return lista ulteriori categorie
	 */
	@Select("select CATEGORIA, CLASCAT, SCORPORABILE, SUBAPPALTABILE "
			+"from W9LOTTCATE where CODGARA = #{idGara} and CODLOTT = #{codLotto} ORDER BY NUM_CATE")
	@Results({
		@Result(property = "categoria", column = "CATEGORIA"),
		@Result(property = "classe", column = "CLASCAT"),
		@Result(property = "scorporabile", column = "SCORPORABILE"),
		@Result(property = "subappaltabile", column = "SUBAPPALTABILE")
	})
	public List<CategoriaLottoEntry> getUlterioriCategorie(@Param("idGara") Long idGara, @Param("codLotto") Long codLotto);

	/**
	 * Estrae i cpv secondari del lotto
	 *
	 * @param idGara
	 *        idGara
	 * @param codLotto
	 *        codLotto
	 * @return lista cpv secondari
	 */
	@Select("select CPV "
			+"from W9CPV where CODGARA = #{idGara} and CODLOTT = #{codLotto} ORDER BY NUM_CPV")
	@Results({
		@Result(property = "cpv", column = "CPV")
	})
	public List<CpvLottoEntry> getCpvSecondari(@Param("idGara") Long idGara, @Param("codLotto") Long codLotto);
	
}
