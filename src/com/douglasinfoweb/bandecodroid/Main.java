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
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity {
    private Restaurante restaurante;
	private Main main=this;
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
        /* Prepara e mostra tela de espera */
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Carregando cardapio");
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.show();
		
        //Atualiza os dados
        Log.v("bandeco","comecou a pegar dados");
        loadDataThread = new LoadDataThread(this,restaurante);
        updateScreen();
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
		        updateScreen();
		        loadDataThread.start(true);
				return true;
			}
		});
    	itemAtualiza.setIcon(R.drawable.ic_menu_refresh);
    	return true;
    }
    
    
    public void updateScreen() {
	    	if (loadDataThread.isDataReady()) {
	    		if (progressDialog != null)
	    			progressDialog.dismiss();
    			LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	    		LinearLayout mainScroll = (LinearLayout)findViewById(R.id.main);
    			mainScroll.removeAllViews();
	    		for (Cardapio cardapio : restaurante.getCardapios()) {
    				//Pega o layout de cardapios
	    			RelativeLayout layout = (RelativeLayout)vi.inflate(R.layout.cardapio, null);
    				//Pega o titulo do cardapio
    				TextView titulo = (TextView)layout.findViewById(R.id.Titulo);
    				ImageView image = (ImageView)layout.findViewById(R.id.Imagem);
    				switch (cardapio.getRefeicao()) {
    					case ALMOCO: titulo.setText("ALMO�O "+int2diaDaSemana(cardapio.getData().getDayOfWeek())); 
    								image.setImageDrawable(getResources().getDrawable(R.drawable.cardapio_almoco));
    								break;
    					case JANTA: titulo.setText("JANTAR "+int2diaDaSemana(cardapio.getData().getDayOfWeek()));
									image.setImageDrawable(getResources().getDrawable(R.drawable.cardapio_jantar));
									break;
    				}
    				//layout.setLayoutParams(new RelativeLayout.LayoutParams());
    				//Prega o prato principal
    				TextView pratoPrincipal = (TextView)layout.findViewById(R.id.PratoPrincipal);
    				pratoPrincipal.setText(cardapio.getPratoPrincipal());
    				
    				//Pega o suco
    				TextView suco = (TextView)layout.findViewById(R.id.Suco);
    				suco.setText(cardapio.getSuco());
    				
    				//Pega a salada
    				TextView salada = (TextView)layout.findViewById(R.id.Salada);
    				salada.setText(cardapio.getSalada());
    				
    				//Pega a sobremesa
    				TextView sobremesa = (TextView)layout.findViewById(R.id.Sobremesa);
    				sobremesa.setText(cardapio.getSobremesa());
    				
    				//Pega o PTS
    				TextView PTS = (TextView)layout.findViewById(R.id.PTS);
    				PTS.setText(cardapio.getPts());
    				mainScroll.addView(layout);
    			}
	    		
	    	} else {
	    		if (progressDialog != null)
	    			progressDialog.show();
	    	}
    }	
    @SuppressWarnings("unused")
    //TODO: getImage()
	private String getImage(String prato)
    {
        //http://www.google.com/search?q=almo�o&tbm=isch
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
    @Override
    protected void onStop() {
    	super.onStop();
    	if (progressDialog != null) {
	    	progressDialog.dismiss();
	    	progressDialog=null;
    	}
    }
		
	private String int2diaDaSemana(int dayOfWeek) {
		String result;
		switch (dayOfWeek) {
			case 1: result = "Segunda-feira"; break;
			case 2: result = "Ter�a-feira"; break;
			case 3: result = "Quarta-feira"; break;
			case 4: result = "Quinta-feira"; break;
			case 5: result = "Sexta-feira"; break;
			case 6: result = "S�bado"; break;
			case 7: result = "Domingo"; break;
			default: result = ""; break;
		}
		return result;
	}
}