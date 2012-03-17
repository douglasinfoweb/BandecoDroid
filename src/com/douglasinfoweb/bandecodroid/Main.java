package com.douglasinfoweb.bandecodroid;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    private int pagAtual;
    private TextView texto;
    private Button bttAnterior;
    private  Button bttProximo;
    
	private LoadDataThread loadDataThread;
	private ProgressDialog progressDialog;
    
 // Define the Handler that receives messages from the thread and update the progress
    private Handler handler; 
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		handler = new Handler()
	    {
	        @Override
			public void handleMessage(Message msg) 
	        {
	            String cardapio = (String) msg.obj;
	            int isBttAntOn = msg.arg1;
	            int isBttProxOn = msg.arg2;
	            texto.setText(cardapio);
	            
	            if(isBttProxOn==0)
	            {
	            	bttProximo.setEnabled(false);
	            }
	            else
	            {
	            	bttProximo.setEnabled(true);
	            }
	            
	            if(isBttAntOn==0)
	            {
	            	bttAnterior.setEnabled(false);
	            }
	            else
	            {
	            	bttAnterior.setEnabled(true);
	            }
	            
	            progressDialog.dismiss();
	        }
	    };
	
        pagAtual = 1;
        
        bttAnterior = (Button) this.findViewById(R.id.btt_anterior);
        bttAnterior.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(pagAtual>1)
					pagAtual--;
				loadDataThread = new LoadDataThread(handler, bttProximo, bttAnterior);
				loadDataThread.start(pagAtual);
				progressDialog.show();
			}
		});
        
        bttProximo = (Button) findViewById(R.id.btt_proximo);
        bttProximo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				pagAtual++;
				loadDataThread = new LoadDataThread(handler, bttProximo, bttAnterior);
				loadDataThread.start(pagAtual);
				progressDialog.show();
			}
		});
        
        texto = (TextView) findViewById(R.id.texto);
        
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Carregando cardapio");
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.show();
		
		loadDataThread = new LoadDataThread(handler, bttProximo, bttAnterior);
		loadDataThread.start(pagAtual);
    }
    
    private String getImage(String prato)
    {
        //http://www.google.com/search?q=almoço&tbm=isch
    	try
    	{
            URL updateURL = new URL("http://br.images.search.yahoo.com/search/images;_ylt=A0WTf2tjeVVOIlUAoOX06Qt.?p="+URLEncoder.encode(prato));
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

            html = html.split("alt=\"Go to fullsize image\"")[1];
            html = html.split("</a>")[0];
            return html;
            
    	}
    	catch (MalformedURLException e) 
    	{	
    		Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		} 
    	catch (IOException e) 
		{
    		Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
        
        return null;
    
    }
    /*
    private String getCardapio2(int pagina)
    {

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

            // Convert the Bytes read to a String. 
            String html = new String(baf.toByteArray());
            html = html.replaceAll("<b>", "");
            html = html.replaceAll("</b>", "");
            
            String vet[] = html.split("<br>");
            
            vet[0] = vet[0].split("1px>")[1];
            vet[1] = vet[1].trim();
            
            if(html.indexOf("Próximo<br>")!=-1)
            {
            	bttProximo.setEnabled(false);
            }
            else
            {
            	bttProximo.setEnabled(true);
            }
            
            if(html.indexOf("<br>Anterior")!=-1)
            {
            	bttAnterior.setEnabled(false);
            }
            else
            {
            	bttAnterior.setEnabled(true);
            }
            
            String conc = vet[0];
            for(int i=1;i<vet.length-2;i++)
            	conc+="\n"+vet[i];
            
            return conc;
    	}
    	catch (MalformedURLException e) 
    	{	
    		Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		} 
    	catch (IOException e) 
		{
    		Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
        
        return null;
    
    }
    */
    private String getCardapio(int pagina)
    {
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
            
            if(html.indexOf("Próximo<br>")!=-1)
            {
            	bttProximo.setEnabled(false);
            }
            else
            {
            	bttProximo.setEnabled(true);
            }
            
            if(html.indexOf("<br>Anterior")!=-1)
            {
            	html = html.substring(html.indexOf("px>")+3, html.indexOf("<br>Anterior"));
            	bttAnterior.setEnabled(false);
            }
            else
            {
            	html = html.substring(html.indexOf("px>")+3, html.indexOf("<a"));
            	bttAnterior.setEnabled(true);
            }
            
            html = html.replaceAll("<br>", "\n");
            html = html.replaceAll("<b>", "");
            html = html.replaceAll("</b>", "");
            return html.trim();
    	}
    	catch (MalformedURLException e) 
    	{	
    		Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		} 
    	catch (IOException e) 
		{
    		Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG);
		}
        
        return null;
    }
}