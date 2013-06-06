package com.douglasinfoweb.bandecodroid.restaurantes;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;

import org.joda.time.DateTime;

import com.douglasinfoweb.bandecodroid.Util;
import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Cardapio.Refeicao;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

public class UspEach extends Restaurante {

	private static final long serialVersionUID = 1L;

	public UspEach() {
		nome= "USP EACH";
		site= "http://each.uspnet.usp.br/site/restaurante.php?item=cardapio";
		codigo="usp_each";
		tinyUrl="http://goo.gl/UYuiH";
	}
	private enum Modo {SALADA, PRATO_PRINCIPAL, PTS, SOBREMESA,NENHUM};
	
	@Override
	public void atualizarCardapios() throws IOException, Exception {
		//Baixa PDF e converte para txt com xpdf
		//USING wget to download file
		//String ex=
				execute("wget http://each.uspnet.usp.br/infra/restaurante/cardapio-almoco.pdf");
		//System.out.println("wget: "+ex);
		File pdfFile = new File("cardapio-almoco.pdf");
		//USING XPDF PDF TO TEXT to convert pdf to txt
		//ex=
				execute("pdftotext -enc UTF-8 -layout cardapio-almoco.pdf");
		//System.out.println("xpdf-pdftotext: "+ex);
		pdfFile.delete(); //Deleta pdf
		pdfFile=null;

		File txtFile = new File("cardapio-almoco.txt");
		FileInputStream inputTxt = new FileInputStream(txtFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputTxt, Charset.forName("UTF-8")));
		String line;
		ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
		ArrayList<Cardapio> cardapiosTemp = new ArrayList<Cardapio>();
		ArrayList<Integer> splittingPoints=new ArrayList<Integer>();
		Modo modo=Modo.NENHUM;
		Refeicao refeicao=Cardapio.Refeicao.ALMOCO;
		while ((line = br.readLine()) != null) {
			//Identificando refeicao
			if (line.contains("ALMO")) {
				refeicao=Cardapio.Refeicao.ALMOCO;
			} else if (line.contains("JANTAR")) {
				refeicao=Cardapio.Refeicao.JANTA;
			}
			
			//Identificando o que eh
			if (line.contains("SALADA")) {
				modo=Modo.SALADA;
			} else if (line.contains("PRATO")) {
				modo=Modo.PRATO_PRINCIPAL;
			} else if (line.contains("VEGETARIANA")) {
				modo=Modo.PTS;
			} else if (line.contains("GUARNI")) {
				modo=Modo.PRATO_PRINCIPAL;
			} else if (line.contains("SOBREMESA")) {
				modo=Modo.SOBREMESA;
			}
			//Acabou tabela
			if (Util.removerEspacosDuplicados(line).equals("") || line.contains("SUJEITO")) {
				splittingPoints.clear();
				cardapios.addAll(cardapiosTemp);
				cardapiosTemp.clear();
				modo=Modo.NENHUM;
			}
			// tem conteudo
			if (line.contains("/")) {
				//Vamos achar os pontos de quebra (splitting points)
				//Começa depois do cabeçalho
				int lastSeparator=0;
				int separator;
				while ((separator = line.indexOf('/',lastSeparator+1)) != -1) {
					//pega ponto medio entre separadores
					splittingPoints.add(lastSeparator+(separator-lastSeparator)/2);
					lastSeparator=separator;
				}
			}
			if (splittingPoints.size() > 0) {
				//System.out.println("splitting points: "+splittingPoints);
				//Nesse caso tem que separar a string
				ArrayList<Integer> realSplit = new ArrayList<Integer>();
				realSplit.add(0);
				realSplit.addAll(splittingPoints);
				realSplit.add(line.length());
				int splitAnterior=0;
				int i=0;
				for (Integer split : realSplit) {
					if (split>0) {
						String splittedLine;
						//Se o ponto esta mto longe,
						//significa que a linha ja acabou
						if (splitAnterior > line.length()) {
							splittedLine = "";
						} else {
							splittedLine = line.substring(splitAnterior,
									Math.min(split,line.length())).trim();
						}
						//Vamos criar os cardapios inicias agora
						//Recuperando data
						if (splittedLine.contains("/")) {
							DateTime ultimaData = Util.str2date(splittedLine);
							//Cria novo objeto Cardapio
							Cardapio c = new Cardapio();
							c.setSalada("");
							c.setSobremesa("");
							c.setPratoPrincipal("");
							c.setPts("");
							c.setData(ultimaData);
							c.setRefeicao(refeicao);
							//Adiciona cardapio 
							cardapiosTemp.add(c);
						} else if (cardapiosTemp.size() > i-1 && i > 0) {
							//Poe os dados no cardapio correspondente
							Cardapio c = cardapiosTemp.get(i-1);
							switch (modo) {
							case SALADA: c.setSalada(Util.removerEspacosDuplicados(c.getSalada()+" "+splittedLine).trim()); break;
							case PRATO_PRINCIPAL: c.setPratoPrincipal(Util.removerEspacosDuplicados(c.getPratoPrincipal()+" "+splittedLine).trim()); break;
							case PTS: c.setPts(Util.removerEspacosDuplicados(c.getPts()+" "+splittedLine).trim()); break;
							case SOBREMESA: c.setSobremesa(Util.removerEspacosDuplicados(c.getSobremesa()+" "+splittedLine).trim()); break;
							}
						}
						
						i++;
					}
					//System.out.println("Split "+split);
					splitAnterior=split;
				}
			}
			//System.out.println(line);
		}
		txtFile.delete(); //Deleta txt
		//Limpa cardapios
		for (Cardapio c : new ArrayList<Cardapio>(cardapios)) {
			if (c.getPratoPrincipal() == null
				|| c.getRefeicao () == null
				|| c.getData() == null
				|| c.getPratoPrincipal().length() <= 6) {
				cardapios.remove(c);
			}
		}
		Collections.sort(cardapios);
		setCardapios(cardapios);
	}
	
	public static String execute(String command){
		StringBuilder sb = new StringBuilder();
		String[] commands = new String[]{"/bin/bash","-c", "export PATH=\"$PATH:/opt/local/bin\"; "+command};
		try {
			Process proc = new ProcessBuilder(commands).start();
			BufferedReader stdInput = new BufferedReader(new 
					InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(proc.getErrorStream()));

			String s = null;
			while ((s = stdInput.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}

			while ((s = stdError.readLine()) != null) {
				sb.append(s);
				sb.append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
