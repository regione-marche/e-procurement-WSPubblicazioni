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
		"categoria",
		"classe",
		"scorporabile",
		"subappaltabile"
})
@ApiModel(description="Categoria del lotto")
public class CategoriaLottoEntry implements Serializable {
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
	private Long numCategoria;
	@ApiModelProperty(value="Categoria (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Categorie)", required = true)
	@Size(max=10)
	private String categoria;
	@ApiModelProperty(value="Classe importo categoria (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=Classe)", required = true)
	@Size(max=5)
	private String classe;
	@ApiModelProperty(value="Categoria scorporabile? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", required = true)
	@Size(max=1)
	private String scorporabile;
	@ApiModelProperty(value="Categoria subappaltabile? (/WSTabelleDiContesto/rest/Tabellati/Valori?cod=SN)", required = true)
	@Size(max=1)
	private String subappaltabile;
	
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
	public void setNumCategoria(Long numCategoria) {
		this.numCategoria = numCategoria;
	}
	@XmlTransient
	public Long getNumCategoria() {
		return numCategoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = StringUtils.stripToNull(categoria);
	}
	public String getCategoria() {
		return categoria;
	}
	public void setClasse(String classe) {
		this.classe = StringUtils.stripToNull(classe);
	}
	public String getClasse() {
		return classe;
	}
	public void setScorporabile(String scorporabile) {
		this.scorporabile = StringUtils.stripToNull(scorporabile);
	}
	public String getScorporabile() {
		return scorporabile;
	}
	public void setSubappaltabile(String subappaltabile) {
		this.subappaltabile = StringUtils.stripToNull(subappaltabile);
	}
	public String getSubappaltabile() {
		return subappaltabile;
	}

}
