/**
 * 
 */
package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.MutableDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.douglasinfoweb.bandecodroid.Util;
import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.model.Cardapio.Refeicao;

/**
 * @author reneoctavio
 * Valeu rené =D
 */
@SuppressWarnings("serial")
public class UFUSantaMonica extends Restaurante {
	
	public UFUSantaMonica(){
		this.nome = "UFU Santa Mônica";
		this.site = "http://www.rusantamonica.ufu.br/";
		this.codigo = "ufusantamonica";
		this.tinyUrl="http://goo.gl/tTZTk";
	}
	@Override
	public void atualizarCardapios() throws IOException {
		ArrayList<Cardapio> newCardapios = new ArrayList<Cardapio>();
		Document doc = Jsoup.connect(this.site).userAgent("Mozilla").timeout(30*1000).header("Accept", "text/html").get();
		Elements tables = doc.select("table");
		Elements rows = tables.get(3).select("tr");
		for (Element row : rows){
			Elements cols = row.select("td");
			String text = cols.get(0).text().toLowerCase(Util.getBRLocale()).trim();
			if (text.contains("feira")){
				text = text.trim().split(" ")[text.trim().split(" ").length-1];
				MutableDateTime date = new MutableDateTime();
				String dateString[] = text.split("/");
				date.setYear(Integer.parseInt(dateString[2]));
				date.setMonthOfYear(Integer.parseInt(dateString[1]));
				date.setDayOfMonth(Integer.parseInt(dateString[0]));
				Cardapio cardapio = new Cardapio();
				cardapio.setData(date.toDateTime());
				cardapio.setRefeicao(Refeicao.ALMOCO);
				newCardapios.add(cardapio);
				getValuesFromPage(newCardapios, cols);
			}
			else if(text.contains("janta")){
				Cardapio cardapio = new Cardapio();
				cardapio.setData(newCardapios.get(newCardapios.size()-1).getData());
				cardapio.setRefeicao(Refeicao.JANTA);
				newCardapios.add(cardapio);
				getValuesFromPage(newCardapios, cols);
			}

		}
		for (Cardapio c : new ArrayList<Cardapio>(newCardapios)) {
			if (c.getPratoPrincipal() == null
				|| c.getRefeicao () == null
				|| c.getData() == null
				|| c.getPratoPrincipal().length() <= 6) {
				newCardapios.remove(c);
			}
		}
		setCardapios(newCardapios);
	}
	/**
	 * @param newCardapios
	 * @param cols
	 */
	private void getValuesFromPage(ArrayList<Cardapio> newCardapios,
			Elements cols) {
		if(newCardapios.get(newCardapios.size()-1).getRefeicao()==Refeicao.ALMOCO){
			newCardapios.get(newCardapios.size()-1).setPratoPrincipal("Arroz " + cols.get(2).text()+"\nFeijão "+cols.get(3).text()+"\n"+cols.get(4).text()+"\n"+cols.get(5).text());
			newCardapios.get(newCardapios.size()-1).setSalada(cols.get(6).text());
			newCardapios.get(newCardapios.size()-1).setSobremesa(cols.get(7).text());
			newCardapios.get(newCardapios.size()-1).setSuco(cols.get(8).text());
		}
		else{
			newCardapios.get(newCardapios.size()-1).setPratoPrincipal("Arroz " + cols.get(1).text()+"\nFeijão "+cols.get(2).text()+"\n"+cols.get(3).text()+"\n"+cols.get(4).text());
			newCardapios.get(newCardapios.size()-1).setSalada(cols.get(5).text());
			newCardapios.get(newCardapios.size()-1).setSobremesa(cols.get(6).text());
			newCardapios.get(newCardapios.size()-1).setSuco(cols.get(7).text());
		}
	}
}
