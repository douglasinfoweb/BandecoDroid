package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.douglasinfoweb.bandecodroid.Util;
import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

@SuppressWarnings("serial")
public class Unicamp extends Restaurante {
	boolean proximo;
	public Unicamp () {
		nome="Unicamp";
		codigo="unicamp";
		site="http://www.prefeitura.unicamp.br/servicos.php?servID=119";
		tinyUrl="http://goo.gl/D9yKZ";
	}
	
	@Override
	public void atualizarCardapios() throws IOException {
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
					String text = e.text().toLowerCase(Util.getBRLocale()).trim();
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
	}

}
