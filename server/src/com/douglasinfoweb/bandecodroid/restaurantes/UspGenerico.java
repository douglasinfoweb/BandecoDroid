package com.douglasinfoweb.bandecodroid.restaurantes;

import java.util.ArrayList;

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
public class UspGenerico extends Restaurante {

	public UspGenerico(String nome,  String site, String codigo) {
		this.nome = nome;
		this.site = site;
		this.codigo = codigo;
	}

	@Override
	public void atualizarCardapios() throws Exception {
		ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
		String URL = site;
		Document doc = Jsoup.connect(URL).userAgent("Mozilla").timeout(30000)
				.header("Accept", "text/html").get();
		// Pega semana
		int semana = 0;
		DateTime ultimaData = new DateTime();
		for (Element pre : doc.select("pre")) {
			String text = pre.text().toLowerCase(Util.getBRLocale());
			if (text.contains("semana")) {
				String[] textoSplited = Util.removerEspacosDuplicados(text)
						.split(" ");
				int i=0;
				for (String s : textoSplited) {
					if (s.contains("/")) {
						break;
					}
					i++;
				}
				String[] dataSplited = textoSplited[i]
						.split("/");
				// FORMATO EH DD/MM/AAAA OU DD/MM/AA OU DD/MM/AAA
				if (dataSplited.length == 3) {
					// FORMATO EH DD/MM/AA OU DD/MM/AAA
					if (dataSplited[2].length() == 2 || dataSplited[2].length() == 3) {
						ultimaData = new DateTime(
								Integer.parseInt(dataSplited[2]) + 2000,
								Integer.parseInt(dataSplited[1]),
								Integer.parseInt(dataSplited[0]), 0, 0, 0);
					// FORMATO EH DD/MM/AAAA
					} else if (dataSplited[2].length() == 4) {
						ultimaData = new DateTime(
								Integer.parseInt(dataSplited[2]),
								Integer.parseInt(dataSplited[1]),
								Integer.parseInt(dataSplited[0]), 0, 0, 0);
					}
				// FORMATO EH DD/MM
				} else if (dataSplited.length == 2) {
					ultimaData = new DateTime(ultimaData.getYear(),
							Integer.parseInt(dataSplited[1]),
							Integer.parseInt(dataSplited[0]), 0, 0, 0);
				} else {
					throw new Exception("Erro ao recuperar data");
				}
				semana = ultimaData.getWeekOfWeekyear();
				break;
			}
		}
		// Pega infos
		int rowN = 0;
		for (Element row : doc.select("tr")) {
			// Log.v("usp-fisica", "tr "+rowN+": "+row);
			rowN++;
			if (rowN == 1)
				continue;
			// TODO USP
			int tdN = 0;
			for (Element td : row.select("td")) {
				String text = td.text();
				String textP = td.text().toLowerCase(Util.getBRLocale());
				Cardapio cardapio = new Cardapio();
				if (tdN == 0)
					cardapio.setRefeicao(Refeicao.ALMOCO);
				else
					cardapio.setRefeicao(Refeicao.JANTA);

				int diaDaSemana = 0;
				if (textP.contains("segunda")) {
					diaDaSemana = 1;
				} else if (textP.contains("terça")) {
					diaDaSemana = 2;
				} else if (textP.contains("quarta")) {
					diaDaSemana = 3;
				} else if (textP.contains("quinta")) {
					diaDaSemana = 4;
				} else if (textP.contains("sexta")) {
					diaDaSemana = 5;
				} else if (textP.contains("sábado")) {
					diaDaSemana = 6;
				} else if (textP.contains("domingo")) {
					diaDaSemana = 7;
				}
				// Nao foi possivel identificar dia da semana
				if (diaDaSemana == 0) {
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
						&& Util.removerEspacosDuplicados(
								cardapio.getPratoPrincipal().trim()).length() > 2
						&& !cardapio.getPratoPrincipal()
								.toLowerCase(Util.getBRLocale())
								.contains("fechado")) {
					cardapios.add(cardapio);
				}
			}
		}

		setCardapios(cardapios);
		removeCardapiosAntigos();
	}

}
