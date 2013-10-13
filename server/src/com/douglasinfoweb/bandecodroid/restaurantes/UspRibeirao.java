package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.jsoup.*;
import org.jsoup.nodes.*;

import com.douglasinfoweb.bandecodroid.Util;
import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.model.Cardapio.Refeicao;

public class UspRibeirao extends Restaurante {

	private static final long serialVersionUID = 1L;

	public UspRibeirao() {
		this.nome = "USP Ribeirão";
		this.site = "http://www.ccrp.usp.br/pages/restaurante/conteudo.asp";
		this.codigo = "usp_ribeirao";
		this.tinyUrl = "http://goo.gl/kaytZ";
    }
	@Override
	public void atualizarCardapios() throws IOException,Exception {
		Document doc = Jsoup.connect(site).userAgent("Mozilla").timeout(30000)
				.header("Accept", "text/html").get();
		ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
		//Pega form que tem a tabela que precisamos
		Element form = doc.select("form").first();
		Element table = form.select("table").first();
		for (Element tr : table.select("tr")) {
			//Ve se tem a data na linha, se tiver é pq tem o cardapio
			if (tr.text().contains("/")) {
				int tdN=0;
				Cardapio cAlmoco = new Cardapio();
				cAlmoco.setRefeicao(Refeicao.ALMOCO);
				Cardapio cJanta = new Cardapio();
				cJanta.setRefeicao(Refeicao.JANTA);
				for (Element td : tr.select("td")) {
					switch (tdN) {
					    case 0: //Pega data
					    DateTime data = Util.str2date(td.text().split(" ")[1]);
						cAlmoco.setData(data);
						cJanta.setData(data);
						break;
						
					    case 1: //Pega almoço
					    if (populaCardapio(td, cAlmoco)) {
					    	cardapios.add(cAlmoco);
					    }
					    break;
					    
					    case 2: //Pega janta
					    if (populaCardapio(td, cJanta)) {
					    	cardapios.add(cJanta);
					    }
					    break;
					}
					tdN++;
				}
			}
		}
		setCardapios(cardapios);
	}
	private boolean populaCardapio(Element td, Cardapio c) {
		String rawHtml = td.html().replace("<br />", "-BREAK-");
		Element td2 = Jsoup.parse(rawHtml);
		String text = td2.text().replace("-BREAK-", "\n").toUpperCase(Util.getBRLocale());
		//Ignora aviso de funcionamento
		if (text.contains("FUNCIONAMENTO"))
			return false;
		
		String[] splitText = text.split("\n");
		boolean jaApareceuSuco=false;
		for (String line : splitText) {
			if (line.contains("SALADA")) {
				if (c.getSalada() != null)
					c.setSalada(c.getSalada()+"\n"+line);
				else
					c.setSalada(line);
			} else if (line.contains("SUCO")) {
				jaApareceuSuco=true;
			} else if (jaApareceuSuco) {
				c.setSuco(line);
			} else if (!line.contains("ARROZ") && !line.contains("FEIJAO") && !line.contains("FEIJÃO")) {

				if (c.getPratoPrincipal() != null)
					c.setPratoPrincipal(c.getPratoPrincipal()+"\n"+line);
				else
					c.setPratoPrincipal(line);
			}
		}
		return c.getPratoPrincipal() != null
				&& c.getRefeicao() != null
				&& c.getData() != null
				&& Util.removerEspacosDuplicados(
						c.getPratoPrincipal().trim()).length() > 2;
	}
}
