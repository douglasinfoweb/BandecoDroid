package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.xml.sax.SAXException;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.model.Cardapio.Refeicao;
import com.douglasinfoweb.bandecodroid.Util;

@SuppressWarnings("serial")
public class UspSaoCarlos extends Restaurante {
	public UspSaoCarlos() {
		nome="USP São Carlos";
		site="http://www.pcasc.usp.br/pop_cardapio.php";
		codigo="usp_saocarlos";
		tinyUrl="http://goo.gl/IZWZh";
	}
	@Override
	public void atualizarCardapios() throws IOException,Exception {
	ArrayList<Cardapio> cardapios=new ArrayList<Cardapio>();
		String URL = "http://www.pcasc.usp.br/restaurante.xml";
		String XML = getXmlFromUrl(URL);
		Document doc = getDomElement(XML);
		Element root = doc.getDocumentElement();
		NodeList dias = root.getChildNodes();
		for (int i=0; i < dias.getLength(); i++) {
			Node dia = dias.item(i);
			DateTime ultimaData = new DateTime();
			for (int j=0; j < dia.getChildNodes().getLength(); j++) {
				Node noDoDia = dia.getChildNodes().item(j);
				if (noDoDia.getNodeName().equals("data") && getNodeValue(noDoDia).length() >= 3) {
					ultimaData = Util.str2date(getNodeValue(noDoDia));
				} else if (noDoDia.getNodeName().equals("almoco") || noDoDia.getNodeName().equals("jantar")) {
					Cardapio cardapio = new Cardapio();
					cardapio.setData(ultimaData);
					if (noDoDia.getNodeName().equals("almoco")) {
						cardapio.setRefeicao(Refeicao.ALMOCO);
					} else {
						cardapio.setRefeicao(Refeicao.JANTA);
					}
					for (int k=0; k < noDoDia.getChildNodes().getLength(); k++) {
						Node atributo = noDoDia.getChildNodes().item(k);
						if (atributo.getNodeName().equals("principal")) {
							cardapio.setPratoPrincipal(getNodeValue(atributo));
						} else if (atributo.getNodeName().equals("acompanhamento")) {
							cardapio.setPratoPrincipal(cardapio.getPratoPrincipal()+"\n"+getNodeValue(atributo));
						} else if (atributo.getNodeName().equals("salada")) {
							cardapio.setSalada(getNodeValue(atributo));
						} else if (atributo.getNodeName().equals("sobremesa")) {
							cardapio.setSobremesa(getNodeValue(atributo));
						}
					}
					if (cardapio.getPratoPrincipal() != null 
							&& cardapio.getRefeicao() != null
							&& cardapio.getData() != null
							&& Util.removerEspacosDuplicados(cardapio.getPratoPrincipal().trim()).length() > 2) {
						cardapios.add(cardapio);
					}
				}
			}
		}
		setCardapios(cardapios);
	}
	
	private final String getNodeValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue().replace("\n","");
                    }
                }
            }
        }
        return "";
 }    
	private String getXmlFromUrl(String url) throws IOException {
    	
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla");
        httpGet.setHeader("Accept","text/html");
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        return EntityUtils.toString(httpEntity);
    }
	private Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
 
            DocumentBuilder db = dbf.newDocumentBuilder();
 
            InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is); 
 
            } catch (ParserConfigurationException e) {
                return null;
            } catch (SAXException e) {
                return null;
            } catch (IOException e) {
                return null;
            }
                // return DOM
            return doc;
    }
	  
}
