package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;

import com.douglasinfoweb.bandecodroid.Cardapio;
import com.douglasinfoweb.bandecodroid.Main;
import com.douglasinfoweb.bandecodroid.R;
import com.douglasinfoweb.bandecodroid.Restaurante;
import com.douglasinfoweb.bandecodroid.Util;

@SuppressWarnings("serial")
public class Unicamp extends Restaurante {
	boolean proximo;
	@Override
	public void atualizarCardapios(Main main) throws IOException {
		Log.v("bandeco","ATUALIZAR");
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		proximo = true;
		int pagina=1;
		while (proximo) {
				proximo=false;
				String URL = "http://www.prefeitura.unicamp.br/cardapio_pref.php?pagina="+pagina;
				Document doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(30000).header("Accept", "text/html").get();
				Elements linhas = doc.select("tr");
				Cardapio cardapio = new Cardapio();
				boolean duasLinhasPratoPrincipal=false;
				for (Element e : linhas) {
					String text = e.text().toLowerCase().trim();
					String textoNormal = e.text().trim();
					/** INFORMCOES **/
					if (text.contains("feira")) {
						String dataTxt = Util.removerEspacosDuplicados(text).trim().substring(0, 10);
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
						cardapio.setPratoPrincipal(Util.capitalize(Util.separaEPegaValor(textoNormal)));
						duasLinhasPratoPrincipal=true;
					} else if (text.contains("sobremesa")) {
						cardapio.setSobremesa(Util.capitalize(Util.separaEPegaValor(textoNormal)));
						duasLinhasPratoPrincipal=false;
					} else if (text.contains("salada")) {
						cardapio.setSalada(Util.capitalize(Util.separaEPegaValor(textoNormal)));
						duasLinhasPratoPrincipal=false;
					} else if (text.contains("suco")) {
						cardapio.setSuco(Util.capitalize(Util.separaEPegaValor(textoNormal)));
						duasLinhasPratoPrincipal=false;
					} else if (text.contains("pts")) {
						cardapio.setPts(Util.capitalize(textoNormal));
						duasLinhasPratoPrincipal=false;
					} else if (text.contains("obs")) {
						cardapio.setObs(Util.capitalize(textoNormal));
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
						cardapio.setPratoPrincipal(cardapio.getPratoPrincipal()+"\n"+Util.capitalize(textoNormal));
						duasLinhasPratoPrincipal=false;
					}
				}
				if (cardapio.getRefeicao() != null 
						&& cardapio.getPratoPrincipal() != null 
						&& cardapio.getData() != null) {
					cardapios.add(cardapio);
				}

			pagina++;	
		}
		setCardapios(cardapios);
		removeCardapiosAntigos();
		main.save();
		Log.v("bandeco","salvou objeto com sucesso :D");
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
		return R.drawable.logo_unicamp;
	}

	@Override
	public String getNome() {
		return "UNICAMP";
	}


	@Override
	public String getSite() {
		return "http://www.prefeitura.unicamp.br/servicos.php?servID=119";
	}
	

}
