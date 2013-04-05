package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.annotation.SuppressLint;
import android.util.Log;

import com.douglasinfoweb.bandecodroid.Cardapio;
import com.douglasinfoweb.bandecodroid.Cardapio.Refeicao;
import com.douglasinfoweb.bandecodroid.Main;
import com.douglasinfoweb.bandecodroid.R;
import com.douglasinfoweb.bandecodroid.Restaurante;
import com.douglasinfoweb.bandecodroid.Util;

@SuppressLint("DefaultLocale")
@SuppressWarnings("serial")
public class UspCentral extends Restaurante {
	boolean proximo;
	@Override
	public void atualizarCardapios(Main main) throws IOException {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		String URL = "http://www.usp.br/coseas/cardapio.html";
		Document doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(30000).header("Accept", "text/html").get();
		//Pega semana
		int semana=0;
		DateTime ultimaData = new DateTime();
		for (Element pre : doc.select("pre")) {
			String text = pre.text();
			if (text.contains("Semana")) {
				String[] dataSplited = Util.removerEspacosDuplicados(text).split(" ")[4].split("/");
				ultimaData = new DateTime(
						Integer.parseInt(dataSplited[2])+2000,
						Integer.parseInt(dataSplited[1]),
						Integer.parseInt(dataSplited[0]), 
						0, 0 ,0);
				Log.v("usp-fisica", "ultimaData: "+ultimaData.toString());
				semana = ultimaData.getWeekOfWeekyear();
			}
		}
		//Pega infos
		int rowN=0;
		for (Element row : doc.select("tr")) {
			//Log.v("usp-fisica", "tr "+rowN+": "+row);
			rowN++;
			if (rowN == 1) continue;
			//TODO USP
			int tdN=0;
			for (Element td : row.select("td")) {
				String text = td.text();
				String textP = td.text().toLowerCase();
				Log.v("usp-fisica", "td "+tdN+": "+text);
				Cardapio cardapio = new Cardapio();
				if (tdN == 0)
					cardapio.setRefeicao(Refeicao.ALMOCO);
				else
					cardapio.setRefeicao(Refeicao.JANTA);

				int diaDaSemana=0;
				if (textP.contains("segunda")) {
					diaDaSemana=1;
				} else if (textP.contains("tera")) {
					diaDaSemana=2;
				} else if (textP.contains("quarta")) {
					diaDaSemana=3;
				} else if (textP.contains("quinta")) {
					diaDaSemana=4;
				} else if (textP.contains("sexta")) {
					diaDaSemana=5;
				} else if (textP.contains("s‡bado")) {
					diaDaSemana=6;
				} else if (textP.contains("domingo")) {
					diaDaSemana=7;
				}
				//Nao foi possivel identificar dia da semana
				if (diaDaSemana==0) {
					continue;
				}
				MutableDateTime data = new MutableDateTime();
				data.setDayOfWeek(diaDaSemana);
				data.setWeekOfWeekyear(semana);
				data.setYear(ultimaData.getYear());
				cardapio.setData(data.toDateTime());
				cardapio.setPratoPrincipal(text);
				tdN++;
				if (cardapio.getPratoPrincipal() != null 
						&& cardapio.getRefeicao() != null
						&& cardapio.getData() != null
						&& Util.removerEspacosDuplicados(cardapio.getPratoPrincipal().trim()).length() > 2
						&& !cardapio.getPratoPrincipal().toLowerCase().contains("fechado")) {
					cardapios.add(cardapio);
				}
			}
			//TODO: TESTAR QUANDO VOLTAR DA MANUTENÇÃO
		}		

		setCardapios(cardapios);
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
		return R.drawable.logo_usp_central;
	}

	@Override
	public String getNome() {
		return "USP Central";
	}


	@Override
	public String getSite() {
		return "http://www.usp.br/coseas/cardapio.html";
	}
	
	  
}
