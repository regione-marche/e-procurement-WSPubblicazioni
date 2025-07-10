package it.maggioli.eldasoft.wspubblicazioni.bl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import it.maggioli.eldasoft.wspubblicazioni.dao.SqlMapper;

/**
 * @author luca.giacomazzo
 */
@Component(value = "wgenChiaviManager")
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
public class WGenChiaviManager {

	/** Logger di classe. */
	private Logger logger = LoggerFactory.getLogger(WGenChiaviManager.class);
	
	/**
	 * Dao MyBatis con le primitive di estrazione dei dati.
	 */

	@Autowired
	private SqlMapper sqlMapper;

	/**
	 * @param sqlMapper
	 *        sqlMapper da settare internamente alla classe.
	 */
	public void setSqlMapper(SqlMapper sqlMapper) {
		this.sqlMapper = sqlMapper;
	}
	  
	@Transactional(isolation = Isolation. READ_COMMITTED)
	public long getNextId(String tabella) {
		this.sqlMapper.execute("UPDATE W_GENCHIAVI SET CHIAVE=CHIAVE+1 WHERE UPPER(TABELLA) = '" + tabella.toUpperCase() + "'");
		Integer i = this.sqlMapper.execute("SELECT CHIAVE FROM W_GENCHIAVI WHERE UPPER(TABELLA) = '" + tabella.toUpperCase() + "'");
		if (i == null) {
			i = 1;
		}
		return i.intValue();
	}
	
}
