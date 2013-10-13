package com.douglasinfoweb.bandecodroid.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.douglasinfoweb.bandecodroid.Util;
import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.restaurantes.UERJ;
import com.douglasinfoweb.bandecodroid.restaurantes.UFF;
import com.douglasinfoweb.bandecodroid.restaurantes.UFRJ;
import com.douglasinfoweb.bandecodroid.restaurantes.UFUSantaMonica;
import com.douglasinfoweb.bandecodroid.restaurantes.Unicamp;
import com.douglasinfoweb.bandecodroid.restaurantes.UspEach;
import com.douglasinfoweb.bandecodroid.restaurantes.UspGenerico;
import com.douglasinfoweb.bandecodroid.restaurantes.UspRibeirao;
import com.douglasinfoweb.bandecodroid.restaurantes.UspSaoCarlos;

/**
 * Atualiza site e objetos no servidor
 * @author feliz
 *
 */
public class update {
	private static ArrayList<Restaurante> restaurantes = new ArrayList<Restaurante>(
		Arrays.asList(
		new Unicamp(),
		new UspGenerico("USP Central",
				"http://www.usp.br/coseas/cardapio.html",
				"usp_central",
				"http://goo.gl/yy771"),
		new UspGenerico("USP Química",
				"http://www.usp.br/coseas/cardapioquimica.html",
				"usp_quimica",
				"http://goo.gl/gmKhO"),
		new UspGenerico("USP Física",
				"http://www.usp.br/coseas/cardapiofisica.html",
				"usp_fisica",
				"http://goo.gl/VDN1k"),
		new UspGenerico("USP Prefeitura",
				"http://www.usp.br/coseas/cardcocesp.html",
				"usp_prefeitura",
				"http://goo.gl/FBXux"),
		new UspEach(),
		new UspSaoCarlos(), 
		new UspRibeirao(),
		new UFRJ(), 
		new UERJ(),
		new UFF(),
		new UFUSantaMonica()
		)
	);
	/**
	 * Grava objeto no arquivo
	 * @param obj objeto a ser gravado
	 * @param filename arquivo
	 */
	private static void write(Serializable obj, String filename) {
		try{
			 
			FileOutputStream fout = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fout);   
			oos.writeObject(obj);
			oos.close();
	 
		   }catch(Exception ex){
			   ex.printStackTrace();
		   }
	}
	
	/**
	 * Poe html gerado no layout predeterminado por www/layout.html
	 * @param titulo Titulo da pagina
	 * @param conteudo Conteudo em html da pagina
	 * @return HTML com Layout + conteudo
	 */
	private static String gerarLayout(String titulo, String conteudo) {
		File file = new File("www/layout.htm");
		try {
			String layoutStr=FileUtils.readFileToString(file, "UTF-8");
			layoutStr=layoutStr.replace("[TITULO]", titulo);
			layoutStr=layoutStr.replace("[CONTEUDO]", conteudo);
			return layoutStr;
		} catch (IOException ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	/**
	 * Gera index.html
	 */
	private static void gerarPaginaIndex() {
		String html="<h2 class=\"first\">Restaurantes</h2><p><table align='center'><tr>";
		int i=0;
		for (Restaurante r : restaurantes) {
			i++;
			html+="<td style=\"background-color:black;\"><a href='"+r.getCodigo()+".html'><img src='"+r.getImageURL()+"' width='100px' height='100px' border=0/></a></td>";
			if (i%4 == 0) {
				html+="</tr><tr>";
			}
		}
		html += "</tr></table></p>";
		File file = new File("www/index.html");
		try {
			FileUtils.writeStringToFile(file,gerarLayout("INÍCIO",html));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gera pagina de um restaurante, salva em www/CODIGO.html
	 * @param r Restaurante a ter pagina gerada
	 */
	private static void gerarPaginaRestaurante(Restaurante r) {
		String html="";
		File file = new File("www/"+r.getCodigo()+".html");
		int i =0;
		for (Cardapio c : r.getCardapios()) {
			DateTime data = c.getData();
			String refeicao;
			if (c.getRefeicao() == Cardapio.Refeicao.ALMOCO) {
				refeicao = "ALMOÇO";
			} else {
				refeicao = "JANTAR";
			}
			
			html += "<h2 class=\"first\">"+Util.int2diaDaSemana(data.getDayOfWeek(), false)+" - "+refeicao+"</h2>";
			html += "<p>Prato principal: "+c.getPratoPrincipal()+"</p>";
			if (c.getSuco()!=null && c.getSuco().length() > 2) {
				html += "<p>Suco: "+c.getSuco()+"</p>";
			}
			if (c.getSobremesa()!=null && c.getSobremesa().length() > 2) {
				html += "<p>Sobremesa: "+c.getSobremesa()+"</p>";
			}
			if (c.getSalada()!=null && c.getSalada().length() > 2) {
				html += "<p>Salada: "+c.getSalada()+"</p>";
			}
			if (c.getPts()!=null && c.getPts().length() > 2) {
				html += "<p>Pts: "+c.getPts()+"</p>";
			}
			i++;
		}
		if (i==0)
			html+="<h2 class=\"first\">Nenhum cardápio relevante</h2>";
		html+="<h2 class=\"first\"><a href=\""+r.getSite()+"\">Site do restaurante</a></h2>";
		try {
			FileUtils.writeStringToFile(file,gerarLayout(r.getNome(),html));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void gerarJsonIndex() {
		JSONObject jObj = new JSONObject();
		try {
			JSONArray jRestaurantes = new JSONArray();
			
			for (Restaurante r : restaurantes) {
				jRestaurantes.put(r.getCodigo());
			}
			jObj.put("restaurantes", jRestaurantes);
			
			File file = new File("www/json/restaurantes");
			
			FileUtils.writeStringToFile(file,jObj.toString());
		} catch (JSONException je)  {
			je.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gera json de um restaurante, salva em www/json/CODIGO.html
	 * @param r Restaurante a ter pagina gerada
	 */
	private static void gerarJsonRestaurante(Restaurante r) {
		
		JSONObject jObj = new JSONObject();
		try {
			jObj.put("nome", r.getNome());
			jObj.put("codigo", r.getCodigo());
			jObj.put("site", r.getSite());
			jObj.put("tinyUrl", r.getTinyUrl());
			
			JSONArray jCardapios = new JSONArray();
			for (Cardapio c : r.getCardapios()) {
				JSONObject jCardapio = new JSONObject();
				
				DateTime data = c.getData();
				DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
				jCardapio.put("data", data.toString(formatter));
				
				String refeicao;
				if (c.getRefeicao() == Cardapio.Refeicao.ALMOCO) {
					refeicao = "ALMOCO";
				} else {
					refeicao = "JANTA";
				}
				jCardapio.put("refeicao", refeicao);
				
				jCardapio.put("pratoPrincipal", c.getPratoPrincipal());
				
				jCardapio.put("suco", c.getSuco());
				
				jCardapio.put("sobremesa", c.getSobremesa());
				
				jCardapio.put("salada", c.getSalada());
				
				jCardapio.put("pts", c.getPts());
				
				jCardapios.put(jCardapio);
			}
			jObj.put("cardapios", jCardapios);
			
			File file = new File("www/json/"+r.getCodigo());
			
			FileUtils.writeStringToFile(file,jObj.toString());
		} catch (JSONException je)  {
			je.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Funcao principal
	 * @param args Nao importa
	 */
	public static void main(String[] args) {
		ArrayList<Restaurante> restaurantesGenerico = new ArrayList<Restaurante>();
		gerarPaginaIndex();
		for (Restaurante r : restaurantes) {
			//Atualizar
			System.out.println("=== Atualizando: "+r.getNome()+" ===");
			restaurantesGenerico.add(new Restaurante(r));
			try {
				r.atualizarCardapios();
				r.removeCardapiosAntigos();
				gerarPaginaRestaurante(r);
				
				//Escrevendo o objeto serializado no java
				//NO CASO DE SUCESSO, grava generico Restaurante com cardapios
				write(new Restaurante(r), "www/obj/"+r.getCodigo());
				
				//Escrevendo objeto serializado json
				gerarJsonRestaurante(new Restaurante(r));
				
			} catch (Exception e) {
				System.err.println("ERRO ATUALIZANDO "+r.getNome()+" :(");
				e.printStackTrace();
			}
		}

		System.out.println("Gravando restaurantes...");
		gerarJsonIndex();
		write(restaurantesGenerico, "www/obj/restaurantes");
	}

}
