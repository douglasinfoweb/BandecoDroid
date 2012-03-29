package com.douglasinfoweb.bandecodroid;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

@SuppressWarnings("serial")
public class BandecoUnicamp extends Restaurante {
	boolean proximo;
	@Override
	public boolean atualizarCardapios(Main main) {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		proximo = true;
		int pagina=1;
		try {
			while (proximo) {
					proximo=false;
					String URL = "http://www.prefeitura.unicamp.br/busca_cardapio.php?pagina="+pagina;
					Document doc = Jsoup.connect(URL).userAgent("Mozilla").header("Accept", "text/html").get();
					Elements linhas = doc.select("tr");
					Cardapio cardapio = new Cardapio();
					cardapios.add(cardapio);
					boolean duasLinhasPratoPrincipal=false;
					for (Element e : linhas) {
						String text = e.text().toLowerCase().trim();
						String textoNormal = e.text().trim();
						/** INFORMAÇÕES **/
						if (text.contains("feira")) {
							String dataTxt = text.substring(0, 10).trim();
							String[] dataSplited = dataTxt.split("/");
							if (dataSplited.length == 3) {
								DateTime data = new DateTime(
										Integer.parseInt(dataSplited[2]),
										Integer.parseInt(dataSplited[1]),
										Integer.parseInt(dataSplited[0]), 
										0, 0 ,0);
								cardapio.setData(data);
							}
						} else if (text.contains("prato principal")) {
							cardapio.setPratoPrincipal(separaEPegaValor(textoNormal));
							duasLinhasPratoPrincipal=true;
						} else if (text.contains("sobremesa")) {
							cardapio.setSobremesa(capitalize(separaEPegaValor(textoNormal)));
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("salada")) {
							cardapio.setSalada(capitalize(separaEPegaValor(textoNormal)));
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("suco")) {
							cardapio.setSuco(capitalize(separaEPegaValor(textoNormal)));
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("pts")) {
							cardapio.setPts(capitalize(textoNormal));
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("obs")) {
							cardapio.setObs(capitalize(textoNormal));
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("jantar")) {
							cardapio.setRefeicao(Cardapio.Refeicao.JANTA);
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("almoço")) {
							cardapio.setRefeicao(Cardapio.Refeicao.ALMOCO);
							duasLinhasPratoPrincipal=false;
						} else if (text.contains("próximo")) {
							proximo=true;
							duasLinhasPratoPrincipal=false;
						} else if (duasLinhasPratoPrincipal) {
							cardapio.setPratoPrincipal(cardapio.getPratoPrincipal()+"\n"+textoNormal);
							duasLinhasPratoPrincipal=false;
						}
					}		
	
				pagina++;	
			}
			removeCardapiosAntigos();
			FileOutputStream fos = main.openFileOutput("cardapios", Context.MODE_WORLD_READABLE);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			oos.flush();
			oos.close();
			setCardapios(cardapios);
			Log.v("bandeco","salvou objeto com sucesso :D");
			return true;
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
	private String separaEPegaValor(String text) { //String no formato "A: BCD" retorna BCD
		String [] splited = text.split(":");
		if (splited.length > 1) {
			return splited[1].trim();
		}
		return null;
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
				Log.v("bandeco","nao precisa atualizar :D");
				return false;
			}
		}
		return true;
	}

	@Override
	public void removeCardapiosAntigos() {
		Log.v("bandeco","removeu os antigos");
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

	private String capitalizeOneWord(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
	}
	private String capitalize(String s) {
		if (s == null || s.length() == 0) return s;
		String[] splited = s.split(" ");
		StringBuilder sb = new StringBuilder();
        for (int i=0; i<splited.length; i++) {
        	sb.append(capitalizeOneWord(splited[i]));
        }
        return sb.toString().trim();
	}
	

}
