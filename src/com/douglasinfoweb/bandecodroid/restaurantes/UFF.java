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
public class UFF extends Restaurante {
	boolean proximo;
	@Override
	public void atualizarCardapios(Main main) throws IOException {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		String URL = "http://www.uff.br/dac/cardapio.htm";
		//String URL = "http://www.felizardo.me/cardapio.htm";
		Document doc = Jsoup.connect(URL).userAgent("Mozilla").header("Accept", "text/html").get();
		//Pega tabelas
		int pN=-99999;
		Cardapio cardapio=null;
		for (Element p : doc.select("p")) {
			Log.v("bandeco","p "+pN);
			String texto = Util.removerEspacosDuplicados(p.text().trim());
			if (texto.toLowerCase().contains("feira")) {
				pN=0;
				if (cardapio != null && cardapio.getData() != null && cardapio.getPratoPrincipal() != null && cardapio.getPratoPrincipal().trim().length() > 2)
					cardapios.add(cardapio);
				cardapio = new Cardapio();
				cardapio.setRefeicao(Refeicao.ALMOCO);
				//Pega data
				String[] dataSplited = texto.split("/");
				DateTime data = new DateTime(
						Integer.parseInt(dataSplited[2].substring(0, 2))+2000,
						Integer.parseInt(dataSplited[1]),
						Integer.parseInt(dataSplited[0].substring(dataSplited[0].length()-2,dataSplited[0].length())), 
						0, 0 ,0);
				cardapio.setData(data);
			}
			switch (pN) {
				case 2: cardapio.setPratoPrincipal(texto); break;
				case 3: cardapio.setPratoPrincipal(cardapio.getPratoPrincipal()+"\n"+texto); break;
				case 4: cardapio.setSalada(Util.separaEPegaValor(texto)); break;
				case 5: cardapio.setSobremesa(Util.separaEPegaValor(texto)); break;
				case 6: cardapio.setSuco(texto); break;
			}
			
			pN++;
		}
		if (cardapio != null && cardapio.getData() != null && cardapio.getPratoPrincipal() != null && cardapio.getPratoPrincipal().trim().length() > 2)
			cardapios.add(cardapio);
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
		return R.drawable.logo_uff;
	}

	@Override
	public String getNome() {
		return "UFF";
	}


	@Override
	public String getSite() {
		return "http://www.uff.br/dac/cardapio.htm";
	}
	
	  
}
