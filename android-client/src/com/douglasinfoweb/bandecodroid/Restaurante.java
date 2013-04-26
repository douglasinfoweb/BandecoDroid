package com.douglasinfoweb.bandecodroid;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;

import android.util.Log;

import com.douglasinfoweb.bandecodroid.restaurantes.UERJ;
import com.douglasinfoweb.bandecodroid.restaurantes.UFRJ;
import com.douglasinfoweb.bandecodroid.restaurantes.Unicamp;
import com.douglasinfoweb.bandecodroid.restaurantes.UspGenerico;
import com.douglasinfoweb.bandecodroid.restaurantes.UspSaoCarlos;

public abstract class Restaurante implements Serializable {
	public static Restaurante[] possiveisRestaurantes = new Restaurante[] {
			new Unicamp(),
			new UspGenerico("USP Central", R.drawable.logo_usp_central,
					"http://www.usp.br/coseas/cardapio.html"),
			new UspGenerico("USP Quimica", R.drawable.logo_usp_quimica,
					"http://www.usp.br/coseas/cardapioquimica.html"),
			new UspGenerico("USP Física", R.drawable.logo_usp_fisica,
					"http://www.usp.br/coseas/cardapiofisica.html"),
			new UspGenerico("USP Prefeitura", R.drawable.logo_usp_prefeitura,
					"http://www.usp.br/coseas/cardcocesp.html"),
			new UspSaoCarlos(), new UFRJ(), new UERJ() };
	private static final long serialVersionUID = -1612436480775220733L;

	public abstract int getImagem();

	private ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();

	public abstract String getNome();

	public abstract void atualizarCardapios(Main main) throws IOException,
			Exception;

	public Boolean temQueAtualizar() {
		DateTime now = new DateTime(new Date());
		ArrayList<Cardapio> cardapios = getCardapios();
		if (cardapios.size() >= 1) {
			Cardapio ultimoCardapio = cardapios.get(cardapios.size() - 1);
			// Se o ultimo que esta na memoria ainda eh dessa semana, nao
			// precisa atualizar.
			if (ultimoCardapio.getData().getWeekOfWeekyear() >= now
					.getWeekOfWeekyear()
					&& ultimoCardapio.getData().getYear() >= now.getYear()) {
				Log.v("bandeco", "nao precisa atualizar :D");
				return false;
			}
		}
		return true;
	}

	public void removeCardapiosAntigos() {
		DateTime now = DateTime.now();
		for (Cardapio c : new ArrayList<Cardapio>(getCardapios())) {
			// Pra remover, tem que ser no minimo do mesmo dia
			if (c.getData().getDayOfYear() <= now.getDayOfYear()
					&& c.getData().getYear() <= now.getYear()) {
				// Se for de dias que ja passaram, remove
				if (c.getData().getDayOfYear() < now.getDayOfYear()) {
					getCardapios().remove(c);
				} else { // Se eh de hoje, ver se ja passou a hora do
							// almo�o/janta
					switch (c.getRefeicao()) {
					case ALMOCO:
						if (now.getHourOfDay() >= 15)
							getCardapios().remove(c);
					case JANTA:
						if (now.getHourOfDay() >= 22)
							getCardapios().remove(c);
					}
				}
			}
		}
	}

	public ArrayList<Cardapio> getCardapios() {
		return cardapios;
	}

	public void setCardapios(ArrayList<Cardapio> cardapios) {
		this.cardapios = cardapios;
	}

	public void atualizar(boolean forcar, Main main) throws Exception {
		removeCardapiosAntigos();
		if (temQueAtualizar() || forcar) {
			atualizarCardapios(main);
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

	public abstract String getSite();

}
