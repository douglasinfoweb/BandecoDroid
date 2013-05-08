package com.douglasinfoweb.bandecodroid;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class Util {
	
	public static String getBaseSite() {
		return "http://bandecodroid.no-ip.org/";
	}
	
	public static Locale getBRLocale() {
		return new Locale("pt", "BR");
	}
	
	public static DateTime str2date(String str) throws Exception {
		DateTime result = new DateTime();
		String[] dataSplited = str.split("/");
		// FORMATO EH DD/MM/AAAA OU DD/MM/AA OU DD/MM/AAA
		if (dataSplited.length == 3) {
			// FORMATO EH DD/MM/AA OU DD/MM/AAA
			if (dataSplited[2].length() == 2 || dataSplited[2].length() == 3) {
				result = new DateTime(
						Integer.parseInt(dataSplited[2]) + 2000,
						Integer.parseInt(dataSplited[1]),
						Integer.parseInt(dataSplited[0]), 0, 0, 0);
			// FORMATO EH DD/MM/AAAA
			} else if (dataSplited[2].length() == 4) {
				result = new DateTime(
						Integer.parseInt(dataSplited[2]),
						Integer.parseInt(dataSplited[1]),
						Integer.parseInt(dataSplited[0]), 0, 0, 0);
			}
		// FORMATO EH DD/MM
		} else if (dataSplited.length == 2) {
			result = new DateTime(result.getYear(),
					Integer.parseInt(dataSplited[1]),
					Integer.parseInt(dataSplited[0]), 0, 0, 0);
		} else {
			throw new Exception("Erro ao recuperar data");
		}
		return result;
	}

	public static String int2diaDaSemana(int dayOfWeek, boolean resumido) {
		String result;
		switch (dayOfWeek) {
		case 1:
			result = "Segunda";
			break;
		case 2:
			result = "Terça";
			break;
		case 3:
			result = "Quarta";
			break;
		case 4:
			result = "Quinta";
			break;
		case 5:
			result = "Sexta";
			break;
		case 6:
			result = "Sábado";
			break;
		case 7:
			result = "Domingo";
			break;
		default:
			result = "";
			break;
		}
		if (!resumido && dayOfWeek >= 1 && dayOfWeek <= 5) {
			result += "-feira";
		}
		return result;
	}

	public static String removerEspacosDuplicados(String str) {
		String patternStr = "\\s+";
		String replaceStr = " ";
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(str);
		return matcher.replaceAll(replaceStr);
	}

	public static String separaEPegaValor(String text) { // String no formato
															// "A: BCD" retorna
															// BCD
		String[] splited = text.split(":");
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
		} catch (Exception e2) {
			return "bad stack2string";
		}
	}

	private static String capitalizeOneWord(String s) {
		if (s.length() == 0)
			return s;
		return s.substring(0, 1).toUpperCase(Util.getBRLocale())
				+ s.substring(1).toLowerCase(Util.getBRLocale()) + " ";
	}

	public static String capitalize(String s) {
		if (s == null || s.length() == 0)
			return s;
		String[] splited = s.split(" ");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < splited.length; i++) {
			sb.append(capitalizeOneWord(splited[i]));
		}
		return sb.toString().trim();
	}

	public static int mes2int(String mes) {
		String mesC = mes.trim().toLowerCase(Util.getBRLocale());
		if (mesC.equals("janeiro"))
			return 1;
		else if (mesC.equals("fevereiro"))
			return 2;
		else if (mesC.equals("março") || mesC.equals("marco"))
			return 3;
		else if (mesC.equals("abril"))
			return 4;
		else if (mesC.equals("maio"))
			return 5;
		else if (mesC.equals("junho"))
			return 6;
		else if (mesC.equals("julho"))
			return 7;
		else if (mesC.equals("agosto"))
			return 8;
		else if (mesC.equals("setembro"))
			return 9;
		else if (mesC.equals("outubro"))
			return 10;
		else if (mesC.equals("novembro"))
			return 11;
		else if (mesC.equals("dezembro"))
			return 12;
		return DateTime.now().getMonthOfYear();
	}
}
