package com.douglasinfoweb.bandecodroid;

import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
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
    	try {
    		for (Restaurante r : main.getConfig().getRestaurantesEscolhidos()) 
   				r.atualizar(forcarAtualizacao, main);

    	} catch (final IOException e) {
			main.runOnUiThread(new Runnable() {
				public void run() {
			        AlertDialog alertDialog = new AlertDialog.Builder(main).create();
			        alertDialog.setTitle("Erro!");
			        alertDialog.setMessage("Impossivel atualizar cardápios. Verifique conexão.");
			        alertDialog.setCanceledOnTouchOutside(true);
			        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
			        alertDialog.setCancelable(true);
			        Log.e("bandeco", "Erro conexao: "+e+"\n"+Util.stack2string(e));
			        alertDialog.show();
				}
			});
			
		} catch (final Exception e) {
			main.runOnUiThread(new Runnable() {
				public void run() {
			        AlertDialog alertDialog = new AlertDialog.Builder(main).create();
			        alertDialog.setTitle("Erro!");
			        alertDialog.setMessage("Erro inesperado ao atualizar cardápio. Verifique se há atualização do aplicativo. Se não, favor contatar-nos.");
			        alertDialog.setCanceledOnTouchOutside(true);
			        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
			        alertDialog.setCancelable(true);
			        Log.e("bandeco", "Erro inesperado: "+e+"\n"+Util.stack2string(e));
			        alertDialog.show();
			        alertDialog.setOnCancelListener(new OnCancelListener() {
						
						@Override
						public void onCancel(DialogInterface arg0) {
							main.abrirConfiguracoes();
						}
					});
				}
			});
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
