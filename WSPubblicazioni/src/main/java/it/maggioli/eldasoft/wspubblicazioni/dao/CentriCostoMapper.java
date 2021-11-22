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

import it.maggioli.eldasoft.wspubblicazioni.vo.centriCosto.DatiGeneraliCentroCostoEntry;

import org.apache.ibatis.annotations.Insert;

/**
 * DAO Interface per l'estrazione delle informazioni relative ai centri di costo mediante MyBatis.
 *
 * @author Mirco.Franzoni
 */
public interface CentriCostoMapper {

	
	@Insert("INSERT INTO CENTRICOSTO (IDCENTRO, CODEIN, CODCENTRO, DENOMCENTRO, NOTE, TIPOLOGIA) VALUES (#{id},#{stazioneAppaltante},#{codice},#{denominazione},#{note},#{tipologia})")
	public void insertCentroCosto(DatiGeneraliCentroCostoEntry cc);

}
