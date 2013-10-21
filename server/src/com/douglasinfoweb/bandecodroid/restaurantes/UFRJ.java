package com.douglasinfoweb.bandecodroid.restaurantes;

import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.model.Cardapio.Refeicao;
import com.douglasinfoweb.bandecodroid.Util;

@SuppressWarnings("serial")
public class UFRJ extends Restaurante {
	public UFRJ () {
		nome="UFRJ";
		codigo="ufrj";
		site="http://www.nutricao.ufrj.br/cardapio.htm";
		tinyUrl="http://goo.gl/65Pgx";
	}
	@Override
	public void atualizarCardapios() throws Exception {
		ArrayList<Cardapio> cardapiosFinal=new ArrayList<Cardapio>();
		String URL = "http://www.nutricao.ufrj.br/cardapio.htm";
		//String URL = "http://www.felizardo.me/cardapio_ru.htm";
		Document doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(30 * 1000).header("Accept", "text/html").get();
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();;
		
		int semana;
		
		
		//Cria todos os cardapios com data
		for (Element p : doc.select("p")) {
			if (p.text().contains("Atualizado em")) {
				cardapios=new ArrayList<Cardapio>();
				String texto = Util.removerEspacosDuplicados(p.text().toLowerCase(Util.getBRLocale()));
				String[] splitedData = texto.split(" ");
				
				DateTime ultimaData = Util.str2date(splitedData[2]);				
				semana = ultimaData.getWeekOfWeekyear();
				
				//Cria cardapios da semana
				for (int i=0; i<7; i++) {
					Cardapio c = new Cardapio();
					Cardapio c2 = new Cardapio();
					
					MutableDateTime data = new MutableDateTime();
					data.setDayOfWeek(i+1);
					data.setWeekOfWeekyear(semana);
					data.setYear(DateTime.now().getYear());
					c.setData(data.toDateTime());
					c2.setData(data.toDateTime());
					//Almoco
					cardapios.add(c);
					//Jantar
					cardapios.add(c2);
				}
				
			}
		}
		
		
		for (Element table : doc.select("table")) {
			Refeicao refeicao;
			
			int trN=0;
			
			if (table.text().contains("Segunda") 
					&& table.text().contains("Terça")
					&& table.text().contains("Quarta")
					&& table.text().contains("Quinta")
					&& table.text().contains("Sexta")
					&& table.text().contains("Sábado")
					&& table.text().contains("Domingo")) {

				if (table.text().contains("Almoço")) {
					refeicao = Refeicao.ALMOCO;
				} else {
					refeicao = Refeicao.JANTA;
				}
				
				for (Element tr : table.select("tr")) {
					if (trN >= 3 && trN <= 9) {
						int tdN=0;
						String titulo="";
						for (Element td : tr.select("td")) {
							String texto = Util.removerEspacosDuplicados(td.text().trim());
							if (tdN==0) {
								titulo=texto.toLowerCase();
							} else if (tdN <= 7) {
								Cardapio cardapio;
								if (refeicao == Refeicao.ALMOCO) {
									cardapio = cardapios.get(tdN*2-2);
								} else {
									cardapio = cardapios.get(tdN*2-1);
								}
								cardapio.setRefeicao(refeicao);
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
			}
		}				
		//Adiciona somente os cardapios q foram coletados com sucesso
		for (Cardapio c : cardapios) {
			if (c.getData() != null 
					&& c.getRefeicao() != null
					&& c.getPratoPrincipal() != null 
					&& c.getPratoPrincipal().trim().length() > 2) {
				cardapiosFinal.add(c);
			}
		}
		Collections.sort(cardapiosFinal);
		setCardapios(cardapiosFinal);
	}
	
	  
}
