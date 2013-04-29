package com.douglasinfoweb.bandecodroid.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;

import android.util.Log;

import com.douglasinfoweb.bandecodroid.Util;

public class Restaurante implements Serializable {
	private static final long serialVersionUID = 1;

	private ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
	
	protected String nome;
	
	protected String codigo;
	
	protected String site;
	
	protected String tinyUrl;
	
	public Restaurante(){}
	
	public Restaurante(Restaurante r) {
		this.nome = new String(r.nome);
		this.codigo = new String(r.codigo);
		this.site = new String(r.site);
		this.tinyUrl = new String(r.tinyUrl);
		this.cardapios = new ArrayList<Cardapio> ();
		for (Cardapio c: r.cardapios) {
			this.cardapios.add(new Cardapio(c));
		}
	}
	/**
	 * Retorna URL absoluta para a imagem no servidor
	 * @return URL absoltua da imagem
	 */
	public String getImageURL() {
		return Util.getBaseSite()+"images/"+getImageFilename();
	}
	/**
	 * Retorna o nome do arquivo onde a imagem vai ser guardada
	 * @return nome do arquivo onde a imagem vai ser guardada
	 */
	public String getImageFilename() {
		return "logo_"+codigo+".gif";
	}

	public String getNome() {
		return nome;
	}
	
	public String getCodigo() {
		return codigo;
	}

	public  String getSite() {
		return site;
	}
	
	public String getTinyUrl() {
		return tinyUrl;
	}
	
	public void atualizarCardapios() throws IOException,
			Exception {
		//ESPECIFICO DE CADA IMPLEMENTACAO
	}

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

	public void atualizar(boolean forcar) throws Exception {
		removeCardapiosAntigos();
		if (temQueAtualizar() || forcar) {
			atualizarCardapios();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Restaurante) {
			Restaurante r = (Restaurante) o;
			if (r.getCodigo().equals(getCodigo())) {
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
