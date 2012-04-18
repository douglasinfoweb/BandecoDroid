package com.douglasinfoweb.bandecodroid;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static String int2diaDaSemana(int dayOfWeek, boolean resumido) {
		String result;
		switch (dayOfWeek) {
			case 1: result = "Segunda"; break;
			case 2: result = "Terça"; break;
			case 3: result = "Quarta"; break;
			case 4: result = "Quinta"; break;
			case 5: result = "Sexta"; break;
			case 6: result = "Sábado"; break;
			case 7: result = "Domingo"; break;
			default: result = ""; break;
		}
		if (!resumido && dayOfWeek >= 1 && dayOfWeek <= 5) {
			result += "-feira";
		}
		return result;
	}
	public static String removerEspacosDuplicados(String str){
	     String patternStr = "\\s+";
	     String replaceStr = " ";
	     Pattern pattern = Pattern.compile(patternStr);
	     Matcher matcher = pattern.matcher(str);
	     return matcher.replaceAll(replaceStr);
	  }
	public static String separaEPegaValor(String text) { //String no formato "A: BCD" retorna BCD
		String [] splited = text.split(":");
		if (splited.length > 1) {
			return splited[1].trim();
		}
		return null;
	}
	 public static String stack2string(Exception e) {
		  try {
		    StringWriter sw = new StringWriter();
		    PrintWriter pw = new PrintWriter(sw);
		    e.printStackTrace(pw);
		    return "------\r\n" + sw.toString() + "------\r\n";
		  }
		  catch(Exception e2) {
		    return "bad stack2string";
		  }
	}
	private static String capitalizeOneWord(String s) {
        if (s.length() == 0) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase() + " ";
	}
	public static String capitalize(String s) {
		if (s == null || s.length() == 0) return s;
		String[] splited = s.split(" ");
		StringBuilder sb = new StringBuilder();
        for (int i=0; i<splited.length; i++) {
        	sb.append(capitalizeOneWord(splited[i]));
        }
        return sb.toString().trim();
	}
}
