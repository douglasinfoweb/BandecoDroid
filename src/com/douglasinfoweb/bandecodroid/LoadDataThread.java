package com.douglasinfoweb.bandecodroid;

import android.app.AlertDialog;
import android.util.Log;


public class LoadDataThread extends Thread
{
    private boolean dataReady=false;
    private Main main;
    private boolean forcarAtualizacao=false;
   
    LoadDataThread(Main main) 
    {
    	this.main=main;
    }
	
	public synchronized void start(boolean forcarAtualizacao) {
		this.forcarAtualizacao=forcarAtualizacao;
		start();
	}
    public void run() 
    {
    	Log.v("bandeco", "rodou thread");
    	for (Restaurante r : main.getConfig().getRestaurantesEscolhidos()) {
    		if (!r.atualizar(forcarAtualizacao, main)) {
        		main.runOnUiThread(new Runnable() {
    				public void run() {
    			        AlertDialog alertDialog = new AlertDialog.Builder(main).create();
    			        alertDialog.setTitle("Erro!");
    			        alertDialog.setMessage("Impossível atualizar cardápios. Verifique conexão.");
    			        alertDialog.setCanceledOnTouchOutside(true);
    			        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
    			        alertDialog.setCancelable(true);
    			        alertDialog.show();
    				}
    			});
    			break;
        	}
    	}
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
