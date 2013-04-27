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

import com.douglasinfoweb.bandecodroid.Util;
import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.restaurantes.*;

public class update {

	/**
	 * @param args
	 */
	private static ArrayList<Restaurante> restaurantes = new ArrayList<Restaurante>(
		Arrays.asList(
		new Unicamp(),
		new UspGenerico("USP Central",
				"http://www.usp.br/coseas/cardapio.html",
				"usp_central"),
		new UspGenerico("USP Química",
				"http://www.usp.br/coseas/cardapioquimica.html",
				"usp_quimica"),
		new UspGenerico("USP Física",
				"http://www.usp.br/coseas/cardapiofisica.html",
				"usp_fisica"),
		new UspGenerico("USP Prefeitura",
				"http://www.usp.br/coseas/cardcocesp.html",
				"usp_prefeitura"),
		new UspSaoCarlos(), 
		new UFRJ(), 
		new UERJ()
		)
	);
	
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
	
	private static String gerarLayout(String titulo, String conteudo) {
		File file = new File("www/layout.html");
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
	
	private static void gerarPaginaIndex() {
		String html="<h2 class=\"first\">Restaurantes</h2><p><table align='center'><tr>";
		int i=0;
		for (Restaurante r : restaurantes) {
			i++;
			html+="<td style=\"background-color:black;\"><a href='"+r.getCodigo()+".html'><img src='images/logo_"+r.getCodigo()+".gif' width='100px' height='100px' border=0/></a></td>";
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
	
	public static void main(String[] args) {
		System.out.println("Gravando restaurantes...");
		write(restaurantes, "www/obj/restaurantes");
		gerarPaginaIndex();
		for (Restaurante r : restaurantes) {
			//Atualizar
			System.out.println("=== Atualizando: "+r.getNome()+" ===");
			try {
				r.atualizarCardapios();
				gerarPaginaRestaurante(r);
				System.out.println("Sucesso!");
				//NO CASO DE SUCESSO, grava o objeto com cardapios
				write(r, "www/obj/"+r.getCodigo());
			} catch (Exception e) {
				System.err.println("ERRO ATUALIZANDO "+r.getNome()+" :(");
				e.printStackTrace();
			}
		}
	}

}
