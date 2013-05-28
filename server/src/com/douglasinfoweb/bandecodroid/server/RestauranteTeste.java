package com.douglasinfoweb.bandecodroid.server;

import java.io.IOException;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.douglasinfoweb.bandecodroid.restaurantes.UFRJ;

public class RestauranteTeste {
	public static void main(String[] args) {
		Restaurante r = new UFRJ();

		System.out.println("====== "+r.getNome()+" ======");
		System.out.println("Codigo: "+r.getCodigo());
		System.out.println("Site: "+r.getSite());
		System.out.println("Imagem: "+r.getImageURL());
		System.out.println("TinyUrl: "+r.getTinyUrl());
		System.out.println("");
		
		try {
			r.atualizarCardapios();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		r.removeCardapiosAntigos();
		
		for (Cardapio c : r.getCardapios()) {
			System.out.println("== "+c.getRefeicao()+" "+c.getData().toString("d/M/y")+" ==");
			System.out.println("Prato principal: "+c.getPratoPrincipal());
			if (c.getSobremesa() != null)
				System.out.println("Sobremesa: "+c.getSobremesa());
			if (c.getSalada() != null)
				System.out.println("Salada: "+c.getSalada());
			if (c.getSuco() != null)
				System.out.println("Suco: "+c.getSuco());
			if (c.getPts() != null)
				System.out.println("Pts: "+c.getPts());
			if (c.getObs() != null)
				System.out.println("Obs: "+c.getObs());
			System.out.println("");
		}
	}
}
