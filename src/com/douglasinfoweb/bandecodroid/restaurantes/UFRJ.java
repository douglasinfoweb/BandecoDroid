package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
public class UFRJ extends Restaurante {
	boolean proximo;
	@Override
	public void atualizarCardapios(Main main) throws IOException {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapiosFinal=new ArrayList<Cardapio>();
		String URL = "http://www.nutricao.ufrj.br/cardapio.htm";
		//String URL = "http://www.felizardo.me/cardapio_ru.htm";
		Document doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(30 * 1000).header("Accept", "text/html").get();
		//Pega tabelas
		int tableN=0;
		for (Element table : doc.select("table")) {
			Log.v("bandeco","table "+tableN);
			ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
			Refeicao refeicao;
			int semana=-1;
			int trN=0;
			if (tableN >= 0 && tableN <= 1) {
				for (Element tr : table.select("tr")) {
					Log.v("bandeco","tr "+trN);
					if (trN==0) {
						String texto = Util.removerEspacosDuplicados(tr.text().toLowerCase());
						//Define refeicao
						if (texto.contains("almoço")) {
							refeicao = Refeicao.ALMOCO;
						} else {
							refeicao = Refeicao.JANTA;
						}
						//Define semana
						String strUltimaData = texto.substring(texto.length()-5, texto.length());
						Log.v("bandeco", "texto: '"+texto+"' strUltimaData: "+strUltimaData);
						String[] dataSplited = strUltimaData.split("/");
						DateTime ultimaData;
						if (dataSplited.length == 2) {
							ultimaData = new DateTime(
									DateTime.now().getYear(),
									Integer.parseInt(dataSplited[1]),
									Integer.parseInt(dataSplited[0]), 
									0, 0 ,0);
							semana = ultimaData.getWeekOfWeekyear();
						}
						//Cria cardapios da semana
						for (int i=0; i<5; i++) {
							Cardapio c = new Cardapio();
							if (semana != -1) {
								MutableDateTime data = new MutableDateTime();
								data.setDayOfWeek(i+1);
								data.setWeekOfWeekyear(semana);
								data.setYear(DateTime.now().getYear());
								c.setData(data.toDateTime());
							}
							c.setRefeicao(refeicao);
							cardapios.add(c);
						}
					} else if (trN >= 3 && trN <= 9) {
						int tdN=0;
						String titulo="";
						for (Element td : tr.select("td")) {
							Log.v("bandeco","td "+tdN);
							String texto = Util.removerEspacosDuplicados(td.text().trim());
							if (tdN==0) {
								titulo=texto.toLowerCase();
							} else {
								Cardapio cardapio = cardapios.get(tdN-1);
								Log.v("bandeco","ATRIBUTO "+titulo+": "+cardapio);
								if (titulo.contains("salada")) {
									cardapio.setSalada(texto);
								} else if (titulo.contains("principal")) {
									cardapio.setPratoPrincipal(texto);
								} else if (titulo.contains("guarnição")) {
									cardapio.setPratoPrincipal(cardapio.getPratoPrincipal()+"\n"+texto);
								} else if (titulo.contains("sobremesa")) {
									cardapio.setSobremesa(texto);
								} else if (titulo.contains("suco")) {
									cardapio.setSuco(texto);
								}
							}
							tdN++;
						}
					}
					trN++;
				}
				//Adiciona somente os cardapios q foram coletados com sucesso
				Log.v("bandeco","cardapios: "+cardapios);
				for (Cardapio c : cardapios) {
					if (c.getData() != null 
							&& c.getRefeicao() != null
							&& c.getPratoPrincipal() != null 
							&& c.getPratoPrincipal().trim().length() > 2) {
						cardapiosFinal.add(c);
						Log.v("bandeco","adicionou "+c);
					}
				}
			}
			tableN++;
		}
		Collections.sort(cardapiosFinal);
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
				} else { //Se eh de hoje, ver se ja passou a hora do almo�o/janta
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
		return R.drawable.logo_ufrj;
	}

	@Override
	public String getNome() {
		return "UFRJ";
	}


	@Override
	public String getSite() {
		return "http://www.nutricao.ufrj.br/cardapio_ru.htm";
	}
	
	  
}
