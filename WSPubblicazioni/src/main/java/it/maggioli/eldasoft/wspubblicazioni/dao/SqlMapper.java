package it.maggioli.eldasoft.wspubblicazioni.dao;

import it.maggioli.eldasoft.wspubblicazioni.vo.ControlloEntry;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectProvider;

public interface SqlMapper {
	
	
    class PureSqlProvider {
        public String sql(String sql) {
            return sql;
        }

        public String sqlObject(String sql) {
            return sql;
        }
        
        public String count(String from) {
            return "SELECT count(*) FROM " + from;
        }
    }

    
    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public List<Map<String,Object>> select(String sql);

    @SelectProvider(type = PureSqlProvider.class, method = "count")
    public Integer count(String from);

    @SelectProvider(type = PureSqlProvider.class, method = "sql")
    public Integer execute(String query);

    @SelectProvider(type = PureSqlProvider.class, method = "sqlObject")
    public String executeReturnString(String query);
    
    @SelectProvider(type = PureSqlProvider.class, method = "sqlObject")
    public Date executeReturnDate(String query);
    
    /**
	 * Restituisce il valore richiesto dalla chiave salvato nella w_config 
	 * 
	 * @param codapp
	 *            codapp
	 * @param chiave
	 *            chiave
	 * @return valore valore
	 */
	@Select("select valore from w_config where UPPER(codapp) = #{codapp} AND chiave = #{chiave}")
	public String getConfigValue(@Param("codapp") String codapp, @Param("chiave") String chiave);

	@Insert("INSERT INTO W9INBOX(IDCOMUN, DATRIC, STACOM, XML) VALUES (#{id},#{data},#{stato},#{json})")
	public void insertInbox(@Param("id")Long id, @Param("data")Date data, @Param("stato")Long stato, @Param("json")String json);

    @Insert("INSERT INTO W9OUTBOX(IDCOMUN, AREA, KEY01, KEY02, KEY03, KEY04, STATO, CFSA, CODEIN_UO) VALUES (#{id},#{area},#{key01},#{key02},#{key03},#{key04},#{stato},#{cfsa},#{codein_uo})")
	public void insertOutbox(@Param("id")Long id, @Param("area")Long area, @Param("key01")Long key01, @Param("key02")Long key02, @Param("key03")Long key03, @Param("key04")Long key04, @Param("stato")Long stato, @Param("cfsa")String cfsa, @Param("codein_uo")String codein_uo);

    @Select("select NUM, SEZIONE, TITOLO, MSG, TIPO from WS_CONTROLLI where CODAPP='W9' and CODFUNZIONE= #{codFunzione} and entita= #{entita} and (tipo = 'E' or tipo = 'W') order by NUM")
	@Results({
		@Result(property = "numero", column = "NUM"),
		@Result(property = "sezione", column = "SEZIONE"),
		@Result(property = "titolo", column = "TITOLO"),
		@Result(property = "messaggio", column = "MSG"),
		@Result(property = "tipo", column = "TIPO")
	})
	public List<ControlloEntry> getControlli(@Param("codFunzione") String codFunzione, @Param("entita") String entita);

}