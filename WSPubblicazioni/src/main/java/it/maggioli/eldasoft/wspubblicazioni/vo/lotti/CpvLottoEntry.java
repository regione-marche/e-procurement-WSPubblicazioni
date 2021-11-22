/*
 * Created on 01/giu/2017
 *
 * Copyright (c) Maggioli S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con Maggioli.
 */
package it.maggioli.eldasoft.wspubblicazioni.vo.lotti;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Dati Categoria del lotto.
 *
 * @author Stefano.Sabbadin
 */
@XmlType(propOrder = {
		"cpv"
})
@ApiModel(description="Categoria CPV del lotto")
public class CpvLottoEntry implements Serializable {
	/**
	 * UID.
	 */
	private static final long serialVersionUID = -4433185026855332865L;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Codice della gara", hidden = true)
	private Long idGara;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Codice del lotto", hidden = true)
	private Long idLotto;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@ApiModelProperty(value="*Numero progressivo", hidden = true)
	private Long numCpv;
	@ApiModelProperty(value="Codice CPV secondario", required = true)  
	@Size(max=12)
	private String cpv;
	
	public void setIdGara(Long idGara) {
		this.idGara = idGara;
	}
	@XmlTransient
	public Long getIdGara() {
		return idGara;
	}
	public void setIdLotto(Long idLotto) {
		this.idLotto = idLotto;
	}
	@XmlTransient
	public Long getIdLotto() {
		return idLotto;
	}
	
	public void setNumCpv(Long numCpv) {
		this.numCpv = numCpv;
	}
	@XmlTransient
	public Long getNumCpv() {
		return numCpv;
	}
	public void setCpv(String cpv) {
		this.cpv = StringUtils.stripToNull(cpv);
	}
	public String getCpv() {
		return cpv;
	}
	

}
