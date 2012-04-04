package com.douglasinfoweb.bandecodroid;

import java.io.Serializable;
import java.util.ArrayList;

import com.douglasinfoweb.bandecodroid.restaurantes.Unicamp;
import com.douglasinfoweb.bandecodroid.restaurantes.UspCentral;
import com.douglasinfoweb.bandecodroid.restaurantes.UspFisica;
import com.douglasinfoweb.bandecodroid.restaurantes.UspPrefeitura;
import com.douglasinfoweb.bandecodroid.restaurantes.UspQuimica;
import com.douglasinfoweb.bandecodroid.restaurantes.UspSaoCarlos;


public abstract class Restaurante implements Serializable {
	public static Restaurante[] possiveisRestaurantes = new Restaurante[] {
		new Unicamp(),
		new UspCentral(),
		new UspQuimica(),
		new UspFisica(),
		new UspPrefeitura(),
		new UspSaoCarlos()
	};
	private static final long serialVersionUID = -1612436480775220733L;
	public abstract int getImagem();
	private ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
	
	public abstract String getNome();
	
	public abstract boolean atualizarCardapios(Main main);
	
	public abstract Boolean temQueAtualizar();
	
	public abstract void removeCardapiosAntigos();
	
	public ArrayList<Cardapio> getCardapios() {
		return cardapios;
	}
	
	public void setCardapios(ArrayList<Cardapio> cardapios) {
		this.cardapios=cardapios;
	}
	
	public boolean atualizar(boolean forcar, Main main) {
		removeCardapiosAntigos();
    	if (temQueAtualizar() || forcar) {
    		return atualizarCardapios(main);
    	} else {
    		return true;
    	}
	}
	@Override
	public boolean equals(Object o) {
		if (o instanceof Restaurante) {
			Restaurante r = (Restaurante) o;
			if (r.getImagem() == getImagem() && r.getNome().equals(getNome())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return getNome().hashCode();
	}
	@Override
	public String toString() {
		return getNome();
	}
}
