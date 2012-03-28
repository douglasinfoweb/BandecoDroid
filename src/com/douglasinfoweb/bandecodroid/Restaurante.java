package com.douglasinfoweb.bandecodroid;

import java.io.Serializable;
import java.util.ArrayList;


public abstract class Restaurante implements Serializable {
	private static final long serialVersionUID = -1612436480775220733L;
	private ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
	
	public abstract void atualizarCardapios(Main main);
	
	public abstract Boolean temQueAtualizar();
	
	public abstract void removeCardapiosAntigos();
	
	public ArrayList<Cardapio> getCardapios() {
		return cardapios;
	}
}
