package it.maggioli.eldasoft.wspubblicazioni.utils;

import org.apache.commons.lang.StringUtils;

public class Funzioni {

	public static Boolean noAggiornaGaraSeEsiste = null;
	public static Boolean bloccaCigSeEsiste = null;
	public static Long tipoInstallazione = null;	//1 - Vigilanza, 2 - SA, 3 - OR
	
	  /**
	   * Restituisce la validita' del CIG
	   *
	   * @param codiceCIG cig
	   * @return validita'(-1:cig non valido, 0:cig, 1:smartcig)
	   */
	  public static int controlloCIG(final String codiceCIG) {

		    String strC1_7 = "";// primi 7 caratteri
		    String strC4_10 = "";// dal 4 al 10 carattere
		    String strK = ""; //Firma
		    long nDecStrK_chk = 0;//Firma decimale
		    long nDecStrK = 0;//controllo della firma decimale
		    int result = -1;

		    if ("".equals(codiceCIG) || codiceCIG.length() != 10 || "0000000000".equals(codiceCIG)) {
		      //Errori di struttura
		      return result;
		    }
		    //Verifico se si tratta di cig o smart cig
		    String strC1 = "" + codiceCIG.charAt(0); //Estraggo il primo carattere
		    strC1 = strC1.toUpperCase();
		    if(StringUtils.isNumeric(strC1)){

		    //CIG
		      try {
		        strK = codiceCIG.substring(7,10); //Estraggo la firma
		        nDecStrK = Integer.parseInt (strK, 16); //trasformo in decimale
		        strC1_7 = codiceCIG.substring(0,7); //Estraggo la parte significativa
		        long nStrC1 = Integer.parseInt(strC1_7);
		         //Calcola Firma
		        nDecStrK_chk = ((nStrC1 * 1/1) * 211 % 4091);
		        result = 0;
		      }catch(Exception e){
		          //Impossibile calcolare la firma
		          return result;
		      }

		    }else{

		      //SMART CIG
		      if(!strC1.equals("X") && !strC1.equals("Z") && !strC1.equals("Y")){
		        return result;
		      }
		      try {
		        strK=codiceCIG.substring(1,3);//Estraggo la firma
		        nDecStrK = Integer.parseInt (strK, 16); //trasformo in decimale
		        strC4_10 = codiceCIG.substring(3,10);
		        long nDecStrC4_10 = Integer.parseInt (strC4_10, 16); //trasformo in decimale
		        //Calcola Firma
		        nDecStrK_chk = ((nDecStrC4_10 * 1/1) * 211 % 251);
		        result = 1;
		      }catch(Exception e){
		        //Impossibile calcolare la firma
		        return result;
		      }
		    }

		    if (nDecStrK_chk != nDecStrK) {
		      //La firma non coincide
		      return result;
		    }

		    return result;

		  }
}
