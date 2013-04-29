package com.douglasinfoweb.bandecodroid.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
/**
 * Representa todas as informacoes que devem persistir em diferentes execuções do programa.
 * @author feliz
 *
 */
public class Configuracoes implements Serializable {
	private static final long serialVersionUID = -8872480683935782637L;
	private ArrayList<Restaurante> restaurantesEscolhidos = new ArrayList<Restaurante>();

	/**
	 * Get restaurantes escolhidos
	 * @return Resturantes escolhidos
	 */
	public ArrayList<Restaurante> getRestaurantesEscolhidos() {
		return restaurantesEscolhidos;
	}
	
	/**
	 * Setter restaurantes escolhidos
	 * @param restaurantesEscolhidos
	 */
	public void setRestaurantesEscolhidos(ArrayList<Restaurante> restaurantesEscolhidos) {
		this.restaurantesEscolhidos = restaurantesEscolhidos;
	}
	
	/**
	 * Salva configuracoes em arquivo
	 * @param act Activity para usar recursos
	 * @param m Objeto a ser salvo
	 * @throws IOException Exceção de IO
	 */
	public static void save(Activity act, Configuracoes m) throws IOException {
		FileOutputStream fos = act.openFileOutput("config3", Context.MODE_PRIVATE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(m);
		oos.flush();
		oos.close();
	}
	
	/**
	 * Le configuracoes salvas
	 * @param act Activity para usar recursos
	 * @return Objeto com configuracoes
	 */
	public static Configuracoes read(Context act) {
		Configuracoes configuracoes = new Configuracoes();
        /* Tentar recuperar dados */
        try {
			FileInputStream fis = act.openFileInput("config3");
			ObjectInputStream ois = new ObjectInputStream(fis);
			configuracoes = (Configuracoes)ois.readObject();
			ois.close();
			Log.v("bandeco","recuperou objeto com sucesso :D");
		} catch (Exception e) {
			e.printStackTrace(); 
		}
        return configuracoes;
	}
}
