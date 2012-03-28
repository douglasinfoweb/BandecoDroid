package com.douglasinfoweb.bandecodroid;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    private int cardapioAtual=0;
    private Restaurante restaurante;
	private TextView texto;
	private Main main=this;
    private Button bttAnterior;
    private  Button bttProximo;
	private LoadDataThread loadDataThread;
	private ProgressDialog progressDialog;
   
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        /* Tentar recuperar dados */
        try {
			FileInputStream fis = openFileInput("cardapios");
			ObjectInputStream ois = new ObjectInputStream(fis);
			restaurante = (Restaurante)ois.readObject();
			ois.close();
			Log.v("bandeco","recuperou objeto com sucesso :D");
		} catch (Exception e) {
			restaurante = new BandecoUnicamp();
		}
        /* Mostra tela de espera */
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Carregando cardapio");
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.show();
		
        /* Pega referencia aos components da UI */
        texto = (TextView) findViewById(R.id.texto);
        bttAnterior = (Button) this.findViewById(R.id.btt_anterior);
        bttProximo = (Button) findViewById(R.id.btt_proximo);
        
        /* Configura botoes */
        bttAnterior.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(cardapioAtual>0)
					cardapioAtual--;
				updateScreen();
			}
		});
        
        bttProximo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				if (cardapioAtual<restaurante.getCardapios().size()-1)
					cardapioAtual++;
				updateScreen();
			}
		});
        
        //Atualiza os dados
        Log.v("bandeco","comecou a pegar dados");
        loadDataThread = new LoadDataThread(this,restaurante);
        loadDataThread.start(false);
        //E agora a tela
		updateScreen();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem itemAtualiza = menu.add(0, Menu.NONE, Menu.NONE, "Atualizar");
    	itemAtualiza.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
		        loadDataThread = new LoadDataThread(main,restaurante);
		        loadDataThread.start(true);
				return true;
			}
		});
    	itemAtualiza.setIcon(R.drawable.atualizar);
    	return true;
    }
    	
  
    
    public void updateScreen() {
	    	if (loadDataThread.isDataReady()) {
	    		progressDialog.dismiss();
	    		bttAnterior.setEnabled((cardapioAtual > 0));
		    	bttProximo.setEnabled((cardapioAtual < restaurante.getCardapios().size()-1));
		    	if (restaurante.getCardapios().size() >= 1) {
		    		texto.setText(restaurante.getCardapios().get(cardapioAtual).toString());
		    	} else {
		    		texto.setText("Nenhum cardapio.");
		    	}
	    	} else {
	    		progressDialog.show();
	    	}
    }	
    @SuppressWarnings("unused")
    //TODO: getImage()
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
    
    @SuppressWarnings("unused")
    //Nao usa?
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