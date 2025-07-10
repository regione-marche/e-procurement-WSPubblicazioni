package it.maggioli.eldasoft.wspubblicazioni.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

		    String strC1_7 = "";    // primi 7 caratteri
		    String strC4_10 = "";   // dal 4 al 10 carattere
		    String strK = "";       // Firma
		    long nDecStrK_chk = 0;  // Firma decimale
		    long nDecStrK = 0;      // controllo della firma decimale
		    int result = -1;

		    if ("".equals(codiceCIG) || codiceCIG.length() != 10 || "0000000000".equals(codiceCIG)) {
		      // Errori di struttura
		      return -1;
		    }

		    // Verifico se si tratta di cig o smart cig
		    String strC1 = "" + codiceCIG.charAt(0); //Estraggo il primo carattere
		    strC1 = strC1.toUpperCase();

		    if (StringUtils.isNumeric(strC1)) {
		    // CIG che cominciano con un numero 
		      try {
		        strK = codiceCIG.substring(7,10); //Estraggo la firma
		        nDecStrK = Integer.parseInt(strK, 16); //trasformo in decimale
		        strC1_7 = codiceCIG.substring(0,7); //Estraggo la parte significativa
		        long nStrC1 = Integer.parseInt(strC1_7);
		         //Calcola Firma
		        nDecStrK_chk = ((nStrC1 * 1/1) * 211 % 4091);
		        if (nDecStrK_chk != nDecStrK) {
		        	result = -1;
		        } else {
		        result = 0;
		        }
	    	} catch(Exception e) {
		          //Impossibile calcolare la firma
		          return -1;
		      }
		    } else {

		      //SMART CIG
		      if (strC1.equals("X") || strC1.equals("Z") || strC1.equals("Y")) {
			      try {
			        strK = codiceCIG.substring(1,3);//Estraggo la firma
			        nDecStrK = Integer.parseInt (strK, 16); //trasformo in decimale
		    	    strC4_10 = codiceCIG.substring(3,10);
		        	long nDecStrC4_10 = Integer.parseInt(strC4_10, 16); //trasformo in decimale
		        	//Calcola Firma
		        	nDecStrK_chk = ((nDecStrC4_10 * 1/1) * 211 % 251);
					if (nDecStrK_chk != nDecStrK) {
						result = -1;
					} else {
		        		result = 1;
					}
		      } catch(Exception e) {
				  //Impossibile calcolare la firma
		          return -1;
		      }
	    	} else {
	    		// CIG che comincia lettera da A-U
	    		Pattern p1 = Pattern.compile("[A-U]{1}");
	    		Matcher m1 = p1.matcher(strC1);
	    		if (m1.matches()) {   // se il primo carattere e' una lettera maiuscola tra A e U
	    			String NNNNNN = StringUtils.substring(codiceCIG.trim(), 1, 7);
	    			String KKK = StringUtils.substring(codiceCIG.trim(), 7);
	    			nDecStrK = Integer.parseInt(KKK, 16); //trasformo in decimale
				  
	    			if (!"000000".equals(NNNNNN)) {
	    				Pattern p2 = Pattern.compile("[0-9A-F]{6}"); // NNNNNN e' un numero in esadecimale
	    				Matcher m2 = p2.matcher(NNNNNN);
						
	    				Pattern p3 = Pattern.compile("[0-9A-F]{3}"); // KKK e' un numero in esadecimale
	    				Matcher m3 = p3.matcher(KKK);
						
	    				if (m2.matches() && m3.matches()) {
	    					while (NNNNNN.indexOf('0') == 0) {
	    						NNNNNN = NNNNNN.substring(1);
		    }

	    					long oper1 = Long.parseLong(NNNNNN, 16) + (strC1.charAt(0) - 'A' + 1);
	    					long resto = (oper1 * 211) % 4091;
							
	    					String strResto = StringUtils.leftPad(Long.toHexString(resto), 3, '0').toUpperCase();
							
	    					if (KKK.equalsIgnoreCase(strResto)) {
	    						result = 0;
	    					} else {
	    						result = -1;
	    					}
	    				}
	    			}
	    		}
	    	}
	    }

	    return result;
	}
	
}
