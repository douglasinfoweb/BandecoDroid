package com.douglasinfoweb.bandecodroid;

import java.io.Serializable;

import org.joda.time.DateTime;

public class Cardapio implements Serializable {
	private static final long serialVersionUID = 5627453443659151036L;
	private DateTime data;
	private String pratoPrincipal;
	private String pts;
	private String salada;
	private String sobremesa;
	private String suco;
	private String obs;
	public enum Refeicao {ALMOCO, JANTA};
	private Refeicao refeicao;
	
	public String getPratoPrincipal() {
		return pratoPrincipal;
	}
	public void setPratoPrincipal(String pratoPrincipal) {
		this.pratoPrincipal = pratoPrincipal;
	}
	public String getPts() {
		return pts;
	}
	public void setPts(String pts) {
		this.pts = pts;
	}
	public String getSalada() {
		return salada;
	}
	public void setSalada(String salada) {
		this.salada = salada;
	}
	public String getSobremesa() {
		return sobremesa;
	}
	public void setSobremesa(String sobremesa) {
		this.sobremesa = sobremesa;
	}
	public String getSuco() {
		return suco;
	}
	public void setSuco(String suco) {
		this.suco = suco;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public DateTime getData() {
		return data;
	}
	public void setData(DateTime data) {
		this.data = data;
	}
	public Refeicao getRefeicao() {
		return refeicao;
	}
	public void setRefeicao(Refeicao refeicao) {
		this.refeicao = refeicao;
	}
	

}

