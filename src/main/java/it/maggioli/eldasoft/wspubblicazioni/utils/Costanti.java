package it.maggioli.eldasoft.wspubblicazioni.utils;

public class Costanti {

	public static final boolean COMPILAZIONE_SCP = true;
	
	public enum Operazione {
	    INSERIMENTO,
	    CANCELLAZIONE,
	    MODIFICA;
	}
	
	public static final int NUMERO_MAX_TENTATIVI_INSERT = 10;
	
	public static final String TIPO_ATTO_ESITO = "E";
	public static final String TIPO_ATTO_ESITO_DEFAULT = "A";
	
	public static final String TIPO_GARA_DESERTA = "D";
	
	public static final String TIPO_ATTO_BANDO = "B";
	
	public static final String CONFIG_CODICE_APP = "W9";
	public static final String CONFIG_CHIAVE_APP = "it.maggioli.eldasoft.wslogin.jwtKey";
	
	public static final String CONFIG_NO_AGGIORNA_GARA_SE_ESISTE = "it.eldasoft.wspubblicazioni.noAggiornaGaraSeEsiste";
	public static final String CONFIG_BLOCCA_CIG_SE_ESISTE = "it.eldasoft.wspubblicazioni.bloccaCigSeEsiste";
	public static final String CONFIG_TIPO_INSTALL_WSREST = "it.eldasoft.wspubblicazioni.tipoInstallazione";
}
