/**
 * 
 */
package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;
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
		this.site = "http://www.ru.ufu.br/santa-monica";
		this.codigo = "ufusantamonica";
		this.tinyUrl="http://goo.gl/tTZTk";
	}
	@Override
	public void atualizarCardapios() throws IOException, Exception {
		ArrayList<Cardapio> newCardapios = new ArrayList<Cardapio>();
		Document doc = Jsoup.connect(this.site).userAgent("Mozilla").timeout(30*1000).header("Accept", "text/html").get();
		Elements tables = doc.select("table");
		
		if (tables.size() < 3)
			throw new Exception("FALTA TABELA");
		
		newCardapios.addAll(readFromTable(tables.get(0),Refeicao.ALMOCO));
		newCardapios.addAll(readFromTable(tables.get(2),Refeicao.JANTA));
		Collections.sort(newCardapios);
		//Limpa cardapio
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
	private void getValuesFromPage(Cardapio c,
			Elements cols) {
		c.setPratoPrincipal("Arroz " + cols.get(1).text()+", Feijão "+cols.get(2).text()+", "+cols.get(3).text()+", "+cols.get(4).text());
		c.setSalada(cols.get(5).text());
		c.setSobremesa(cols.get(6).text());
		c.setSuco(cols.get(7).text());
	}
	
	private ArrayList<Cardapio> readFromTable(Element table, Refeicao refeicao) throws Exception {
		ArrayList<Cardapio> newCardapios=new ArrayList<Cardapio>();
		//Pega linhas da tabela do ALMOÇO
		Elements rows = table.select("tr");
		for (Element row : rows){
			Elements cols = row.select("td");
			if (cols.size() < 1)
				continue;
			String text = cols.get(0).text().toLowerCase(Util.getBRLocale()).trim();
			if (text.contains("feira")){
				//Pega data no formato dd/mm/yyyy
				String[] dataStrSplit = text.trim().split(" "); 
				String dataStr = dataStrSplit[dataStrSplit.length-1];
				//Cria objeto de data
				DateTime date = Util.str2date(dataStr);
				//Cria cardapio
				Cardapio cardapio = new Cardapio();
				cardapio.setData(date.toDateTime());
				cardapio.setRefeicao(refeicao);
				getValuesFromPage(cardapio, cols);
				newCardapios.add(cardapio);
			}
		}
		return newCardapios;
	}
}
