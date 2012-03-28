package com.douglasinfoweb.bandecodroid;

import android.util.Log;


public class LoadDataThread extends Thread
{
    private boolean dataReady=false;
    private Main main;
    private Restaurante restaurante;
    private boolean forcarAtualizacao;
   
    LoadDataThread(Main main, Restaurante restaurante) 
    {
    	this.main=main;
    	this.restaurante = restaurante;
    }
	
	public synchronized void start(boolean forcarAtualizacao) {
		this.forcarAtualizacao=forcarAtualizacao;
		start();
	}
    public void run() 
    {
    	Log.v("bandeco", "rodou thread");
    	main.runOnUiThread(new Runnable() {
   	     public void run() {
   	    	 		main.updateScreen();
   	    	    }
   	    	});
    	restaurante.removeCardapiosAntigos();
    	boolean temQueAtualizar = restaurante.temQueAtualizar();
    	Log.v("bandeco", temQueAtualizar+" OU "+forcarAtualizacao);
    	if (temQueAtualizar || forcarAtualizacao) 
    		restaurante.atualizarCardapios(main);
    	dataReady=true;
    	main.runOnUiThread(new Runnable() {
    	     public void run() {
    	    	 		main.updateScreen();
    	    	    }
    	    	});
    }

	public boolean isDataReady() {
		return dataReady;
	}
}
