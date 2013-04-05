package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.util.Log;

import com.douglasinfoweb.bandecodroid.Cardapio;
import com.douglasinfoweb.bandecodroid.Cardapio.Refeicao;
import com.douglasinfoweb.bandecodroid.Main;
import com.douglasinfoweb.bandecodroid.R;
import com.douglasinfoweb.bandecodroid.Restaurante;
import com.douglasinfoweb.bandecodroid.Util;

@SuppressWarnings("serial")
public class UERJ extends Restaurante {
	boolean proximo;
	@Override
	public void atualizarCardapios(Main main) throws IOException {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapiosFinal=new ArrayList<Cardapio>();
		String URL = "http://www.restauranteuniversitario.uerj.br/cardapio.html";
		Document doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(30*1000).header("Accept", "text/html").get();
		Element texto_cardapio = doc.select("#texto_cardapio").get(0);
		Element titulo = texto_cardapio.select("h1").get(0);
		//Pegando data
			String[] tituloSplited = titulo.text().split(" ");
			int ano = Integer.parseInt(tituloSplited[tituloSplited.length-1]);
			int mes = Util.mes2int(tituloSplited[tituloSplited.length-3]);
			int dia = Integer.parseInt(tituloSplited[tituloSplited.length-5]);
			DateTime ultimoDia = new DateTime(ano,
											mes,
											dia,
											0,0,0);
		for (int i=1; i<=5; i++) {
			Cardapio cardapio = new Cardapio();
			MutableDateTime data = new MutableDateTime();
			data.setYear(ultimoDia.getYear());
			data.setWeekOfWeekyear(ultimoDia.getWeekOfWeekyear());
			data.setDayOfWeek(i);
			cardapio.setData(data.toDateTime());
			cardapio.setRefeicao(Refeicao.ALMOCO);
			cardapiosFinal.add(cardapio);
		}
		int trN =0;
		for (Element tr : texto_cardapio.select("tr")) {
			int tdN=0;
			for (Element td : tr.select("td")) {
				String texto = td.text().trim();
				if (tdN > 0) {
					Cardapio cardapio = cardapiosFinal.get(tdN-1);
					switch (trN) {
						case 1: cardapio.setPratoPrincipal(texto); break;
						case 2: cardapio.setPratoPrincipal(cardapio.getPratoPrincipal() + " / " + texto); break;
						case 3: cardapio.setPratoPrincipal(cardapio.getPratoPrincipal() + "\n" + texto); break;
						case 4: cardapio.setSalada(texto); break;
						case 5: case 6: cardapio.setSalada(cardapio.getSalada()+" / "+texto); break;
						case 8: cardapio.setSobremesa(texto); break;
						case 9: cardapio.setSuco(texto); break;
						case 10: cardapio.setSuco(cardapio.getSuco()+"\nChá de "+texto);
					}
				}
				tdN++;
			}
			trN++;
		}
		for (Cardapio c : new ArrayList<Cardapio>(cardapiosFinal)) {
			if (c.getPratoPrincipal() != null
				&& c.getRefeicao () != null
				&& c.getData() != null
				&& c.getPratoPrincipal().length() <= 6) {
				cardapiosFinal.remove(c);
			}
		}
		//Collections.sort(cardapiosFinal);
		setCardapios(cardapiosFinal);
		removeCardapiosAntigos();
		main.save();
	}
	
	
	@Override
	public Boolean temQueAtualizar() {
		DateTime now = new DateTime(new Date());
		ArrayList<Cardapio> cardapios = getCardapios();
		if (cardapios.size() >= 1) {
			Cardapio ultimoCardapio = cardapios.get(cardapios.size() -1);
			//Se o ultimo que esta na memoria ainda eh dessa semana, nao precisa atualizar.
			if (ultimoCardapio.getData().getWeekOfWeekyear() >= now.getWeekOfWeekyear()
					&& ultimoCardapio.getData().getYear() >= now.getYear()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void removeCardapiosAntigos() {
		DateTime now = DateTime.now();
		for (Cardapio c : new ArrayList<Cardapio>(getCardapios())) {
			//Pra remover, tem que ser no minimo do mesmo dia
			if (c.getData().getDayOfYear() <= now.getDayOfYear() 
					&& c.getData().getYear() <= now.getYear()) {
				//Se for de dias que ja passaram, remove
				if (c.getData().getDayOfYear() < now.getDayOfYear()) { 
					getCardapios().remove(c);
				} else { //Se eh de hoje, ver se ja passou a hora do almoço/janta
					switch (c.getRefeicao()) {
						case ALMOCO: if (now.getHourOfDay() >= 14) getCardapios().remove(c);
						case JANTA: if (now.getHourOfDay() >= 20) getCardapios().remove(c);
					}
				}
			}
		}
	}

	@Override
	public int getImagem() {
		return R.drawable.logo_uerj;
	}

	@Override
	public String getNome() {
		return "UERJ";
	}


	@Override
	public String getSite() {
		return "http://www.restauranteuniversitario.uerj.br/cardapio.html";
	}
	
	  
}
