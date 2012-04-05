package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
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
public class UFJF extends Restaurante {
	boolean proximo;
	@Override
	public boolean atualizarCardapios(Main main) {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		try {
			String URL = "http://www.ufjf.br/portal/utilidade/restaurante/";
			Document doc = Jsoup.connect(URL).userAgent("Mozilla").header("Accept", "text/html").get();
			//Pega tabelas
			for (Element table : doc.select("table")) {
				int trN=0;
				for (Element tr : table.select("tr")) {
					if (trN>0) {
						int tdN=0;
						Cardapio cardapio = new Cardapio();
						for (Element td : tr.select("td")) {
							String texto = Util.removerEspacosDuplicados(td.text());
							Log.v("bandeco","td "+tdN+" "+texto);
							switch (tdN) {
								case 0: 
									String[] dataRefeicao = texto.split(" ");
									if (dataRefeicao.length == 2) {
										String[] dataSplited = dataRefeicao[0].split("/");
										if (dataSplited.length == 2) {
											//Pega data
											DateTime data = new DateTime(
													DateTime.now().getYear(),
													Integer.parseInt(dataSplited[1]),
													Integer.parseInt(dataSplited[0]), 
													0, 0 ,0);
											cardapio.setData(data);
										}
										//Pega refeicao
										if (dataRefeicao[1].toLowerCase().contains("jantar")) {
											cardapio.setRefeicao(Refeicao.JANTA);
										} else {
											cardapio.setRefeicao(Refeicao.ALMOCO);
										}
									}
									break;
									case 1: cardapio.setSalada(texto); break;
									case 2: cardapio.setPratoPrincipal(texto); break;
									case 3: cardapio.setPratoPrincipal(cardapio.getPratoPrincipal() +"\n"+texto); break;
									case 5: cardapio.setSuco(texto); break;
									case 6: cardapio.setSobremesa(texto); break;
									case 7: cardapio.setSalada(cardapio.getSalada()+"\n"+texto); break;
								}
								tdN++;
							}
							if (cardapio.getData() != null && cardapio.getPratoPrincipal() != null && cardapio.getPratoPrincipal().length() > 2)
								cardapios.add(cardapio);
						}
					trN++;
				}
			}
			setCardapios(cardapios);
			removeCardapiosAntigos();
			main.save();
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
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
		return R.drawable.logo_ufjf;
	}

	@Override
	public String getNome() {
		return "UFJF";
	}
	
	  
}
