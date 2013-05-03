package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

@SuppressWarnings("serial")
public class UFF extends Restaurante {
	public UFF() {
		nome="UFF";
		site="http://www.pcasc.usp.br/pop_cardapio.php";
		codigo="uff";
		tinyUrl="http://goo.gl/U8Id6";
	}
	@Override
	public void atualizarCardapios() throws IOException, Exception {
		ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		String URL = "http://www.restaurante.uff.br/historico.xml";
		//Baixa cardapio
		String XML = getXmlFromUrl(URL);
		XML=XML.replaceAll("<></>", "");
		//Pega elemento dom
		Document doc = getDomElement(XML);
		//Faz lista de elementos <node>
		NodeList nList = doc.getElementsByTagName("node");
		
		//Itera sobre todos os <node>
		for (int temp = 0; temp < nList.getLength(); temp++) {
			 
			Node nNode = nList.item(temp);
	 
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Cardapio cardapio = new Cardapio();
				Element eElement = (Element) nNode;
				//Pega dados do XML
				String dia = eElement.getElementsByTagName("Dia").item(0).getTextContent();
				String pratoPrincipal = eElement.getElementsByTagName("Prato-principal").item(0).getTextContent()
						+ ", " + eElement.getElementsByTagName("Guarni-o").item(0).getTextContent();
				String salada = eElement.getElementsByTagName("Salada-1").item(0).getTextContent()
						+ ", " + eElement.getElementsByTagName("Salada-2").item(0).getTextContent();
				String sobremesa = eElement.getElementsByTagName("Sobremesa").item(0).getTextContent();
				String bebida = eElement.getElementsByTagName("Refresco").item(0).getTextContent();
				
				//Decodifica

				String[] dataSplited = dia.split("/");
				if (dataSplited.length == 3) {
					DateTime data = new DateTime(
							Integer.parseInt(dataSplited[2]),
							Integer.parseInt(dataSplited[1]),
							Integer.parseInt(dataSplited[0]), 
							0, 0 ,0);
					
					cardapio.setData(data);
					cardapio.setPratoPrincipal(pratoPrincipal);
					cardapio.setSalada(salada);
					cardapio.setSobremesa(sobremesa);
					cardapio.setSuco(bebida);
					//Cria p almoço e janta
					Cardapio almoco = new Cardapio(cardapio);
					almoco.setRefeicao(Cardapio.Refeicao.ALMOCO);
					Cardapio janta = new Cardapio(cardapio);
					janta.setRefeicao(Cardapio.Refeicao.JANTA);
					//Checa antes de adicionar
					if (cardapio.getPratoPrincipal() != null 
							&& cardapio.getData() != null) {
						cardapios.add(almoco);
						cardapios.add(janta);
					}
				} else {
					throw new Exception("Data no formato errado");
				}
				
			}
		}
		setCardapios(cardapios);
	}
	
	private String getXmlFromUrl(String url) throws IOException {
    	
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla");
        httpGet.setHeader("Accept","text/html");
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity,Charset.forName("UTF-8"));
    }
	private Document getDomElement(String xml) throws Exception{
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
 
        DocumentBuilder db = dbf.newDocumentBuilder();

        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(xml));
        doc = db.parse(is); 
        // return DOM
        return doc;
    }
	  
}
