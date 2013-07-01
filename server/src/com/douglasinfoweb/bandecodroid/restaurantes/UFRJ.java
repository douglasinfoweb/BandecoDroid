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
		//Pega tabelas
		int tableN=0;
		for (Element table : doc.select("table")) {
			ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
			Refeicao refeicao;
			int semana=-1;
			int trN=0;
			if (tableN == 1 || tableN == 2) {
				for (Element tr : table.select("tr")) {
					if (trN==0) {
						String texto = Util.removerEspacosDuplicados(tr.text().toLowerCase(Util.getBRLocale()));
						//Define refeicao
						if (texto.contains("almoço")) {
							refeicao = Refeicao.ALMOCO;
						} else {
							refeicao = Refeicao.JANTA;
						}
						//Define semana
						String strUltimaData = texto.substring(texto.length()-5, texto.length());
						if (strUltimaData.contains("/")) {
							DateTime ultimaData = Util.str2date(strUltimaData);
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
							String texto = Util.removerEspacosDuplicados(td.text().trim());
							if (tdN==0) {
								titulo=texto.toLowerCase();
							} else {
								Cardapio cardapio = cardapios.get(tdN-1);
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
				for (Cardapio c : cardapios) {
					if (c.getData() != null 
							&& c.getRefeicao() != null
							&& c.getPratoPrincipal() != null 
							&& c.getPratoPrincipal().trim().length() > 2) {
						cardapiosFinal.add(c);
					}
				}
			}
			tableN++;
		}
		Collections.sort(cardapiosFinal);
		setCardapios(cardapiosFinal);
	}
	
	  
}
