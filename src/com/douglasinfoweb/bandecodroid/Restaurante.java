package com.douglasinfoweb.bandecodroid;

import java.io.Serializable;
import java.util.ArrayList;


public abstract class Restaurante implements Serializable {
	private static final long serialVersionUID = -1612436480775220733L;
	private ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
	
	public abstract boolean atualizarCardapios(Main main);
	
	public abstract Boolean temQueAtualizar();
	
	public abstract void removeCardapiosAntigos();
	
	public ArrayList<Cardapio> getCardapios() {
		return cardapios;
	}
	
	public void setCardapios(ArrayList<Cardapio> cardapios) {
		this.cardapios=cardapios;
	}
}
