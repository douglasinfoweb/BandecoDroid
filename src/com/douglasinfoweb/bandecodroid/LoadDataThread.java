package com.douglasinfoweb.bandecodroid;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.util.ByteArrayBuffer;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

public class LoadDataThread extends Thread
{
    private Handler handler;
    private int pagina;
    private Button bttProximo;
    private Button bttAnterior;
   
    LoadDataThread(Handler handler, Button bttProximo, Button bttAnterior) 
    {
        this.handler = handler;
        this.bttAnterior = bttAnterior;
        this.bttProximo = bttProximo;
        this.pagina = 1;
    }
   
    @Override
	public void run() 
    {
    	String conc = "";
    	int isBttProxOn = 0;
    	int isBttAntOn = 0;
    	try
    	{    		
            URL updateURL = new URL("http://www.caco.ic.unicamp.br/cardapio.php/"+pagina);
            URLConnection conn = updateURL.openConnection();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(50);

            int current = 0;
            while((current = bis.read()) != -1){
                baf.append((byte)current);
            }

            /* Convert the Bytes read to a String. */
            String html = new String(baf.toByteArray());
            html = html.replaceAll("<b>", "");
            html = html.replaceAll("</b>", "");
            
            String vet[] = html.split("<br>");
            
            vet[0] = vet[0].split("1px>")[1];
            vet[1] = vet[1].trim();
            
            if(html.indexOf("Próximo<br>")!=-1)
            {
            	isBttProxOn = 0;
            }
            else
            {
            	isBttProxOn = 1;
            }
            
            if(html.indexOf("<br>Anterior")!=-1)
            {
            	isBttAntOn = 0;
            }
            else
            {
            	isBttAntOn = 1;
            }
            
            conc = vet[0];
            for(int i=1;i<vet.length-2;i++)
            	conc+="\n"+vet[i];
    	}
    	catch (MalformedURLException e) 
    	{	
    		Log.e("error", e.getLocalizedMessage());
		} 
    	catch (IOException e) 
		{
    		Log.e("error", e.getLocalizedMessage());
		}
    	
    	Message msg = handler.obtainMessage();
        msg.obj = conc;
        msg.arg1 = isBttAntOn;
        msg.arg2 = isBttProxOn;
        handler.sendMessage(msg);
    }
    
    public synchronized void start(int pag) 
    {
    	this.pagina = pag;
    	super.start();
    }
}
