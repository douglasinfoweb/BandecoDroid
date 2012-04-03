package com.douglasinfoweb.bandecodroid;

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

public class Configuracoes implements Serializable {
	private static final long serialVersionUID = -8872480683935782637L;
	private ArrayList<Restaurante> restaurantesEscolhidos = new ArrayList<Restaurante>();

	public ArrayList<Restaurante> getRestaurantesEscolhidos() {
		return restaurantesEscolhidos;
	}

	public void setRestaurantesEscolhidos(ArrayList<Restaurante> restaurantesEscolhidos) {
		this.restaurantesEscolhidos = restaurantesEscolhidos;
	}
	
	public static void save(Activity act, Configuracoes m) throws IOException {
		FileOutputStream fos = act.openFileOutput("config", Context.MODE_WORLD_READABLE);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(m);
		oos.flush();
		oos.close();
	}
	public static Configuracoes read(Activity act) {
		Configuracoes configuracoes = new Configuracoes();
        /* Tentar recuperar dados */
        try {
			FileInputStream fis = act.openFileInput("config");
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
