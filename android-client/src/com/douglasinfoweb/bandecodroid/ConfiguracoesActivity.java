package com.douglasinfoweb.bandecodroid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TabHost;

import com.douglasinfoweb.bandecodroid.model.Configuracoes;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

/**
 * Activity responsável pela tela de configurações
 * @author feliz
 *
 */
public class ConfiguracoesActivity extends Activity {
	private Configuracoes config;
	private ArrayList<Restaurante> restaurantesDisponiveis=null;
	private ArrayList<Restaurante> restaurantesSelecionados;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.configuracoes);
		
		//Pega configuracoes
		Intent intent=getIntent();
		config = (Configuracoes)intent.getExtras().get("config");
		if (config != null)
			restaurantesSelecionados = config.getRestaurantesEscolhidos();
		
		//Configura TAB
		TabHost tabHost = (TabHost)findViewById(R.id.tabConfiguracoes);
		tabHost.setup();
		Resources res = getResources();
		
		//Configura aba Restaurantes
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    spec = tabHost.newTabSpec("restaurantes");
	    spec.setIndicator("", res.getDrawable(R.drawable.ic_restaurantes));
	    spec.setContent(R.id.RestaurantesGrid);
	    tabHost.addTab(spec);
	    
	    //Configura aba Sobre
	    spec = tabHost.newTabSpec("sobre");
	    spec.setIndicator("", res.getDrawable(android.R.drawable.ic_menu_help));
	    spec.setContent(R.id.about);
	    tabHost.addTab(spec);
	    
		//Botao de salvar
		Button btn = (Button)findViewById(R.id.salvarButton);
		//Especifica se botao eh enable ou nao
		btn.setEnabled(restaurantesSelecionados.size()>0);
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (restaurantesSelecionados.size() == 0)
					return;
				try {
					ArrayList<Restaurante> velhosRestaurantes = config.getRestaurantesEscolhidos();
					ArrayList<Restaurante> novosRestaurantes = new ArrayList<Restaurante>();
					
					//Eh importante pegar objetos dos velhos restaurantes,
					//asssim evita recarregar os cardapios ja carregados
					for (Restaurante r : restaurantesSelecionados) {
						if (velhosRestaurantes.contains(r)) {
							novosRestaurantes.add(velhosRestaurantes.get(velhosRestaurantes.indexOf(r)));
						} else {
							novosRestaurantes.add(r);
						}
					}
					
					//Salva configuracoes
					config.setRestaurantesEscolhidos(novosRestaurantes);
					Configuracoes.save(ConfiguracoesActivity.this, config);
					
					//Retorna intent com configuracoes para MainActivity
					Intent resultado = new Intent();
					resultado.putExtra("config", config);
					ConfiguracoesActivity.this.setResult(RESULT_OK, resultado);
					ConfiguracoesActivity.this.finish();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		//Começa a baixar restaurantes
		new BaixaRestaurantes().execute();
	}
	
	/**
	 * Inner Class responsável por baixar restaurantes no background
	 * @author feliz
	 *
	 */
	protected class BaixaRestaurantes extends AsyncTask<Void, Void, ArrayList<Restaurante>> {

		private ProgressDialog progressDialog;
		
		protected void onPreExecute() {
	        /* Prepara e mostra tela de espera */
			progressDialog = new ProgressDialog(ConfiguracoesActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Atualizando lista");
			progressDialog.setCancelable(false);
			progressDialog.setProgress(0);
			progressDialog.show();
		};
		
		@SuppressWarnings("unchecked")
		@Override
		protected ArrayList<Restaurante> doInBackground(Void... params) {
			try {
				//Baixa lista de restaurantes
				InputStream inputStream = new URL(Util.getBaseSite()+"obj/restaurantes").openStream();
				ObjectInputStream objStream = new ObjectInputStream(inputStream);
				ArrayList<Restaurante> rests  = (ArrayList<Restaurante>)objStream.readObject();
				//Baixa imagem de cada restaurante
				for (Restaurante r : rests) {
					File f = getBaseContext().getFileStreamPath(r.getImageFilename());
					if (!f.exists()) {
						downloadFile(r.getImageURL(), r.getImageFilename());
					}
				}
				return rests;
			} catch (MalformedURLException e) {
				//URL nao conectada
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				//Provavel erro de conexao
				e.printStackTrace();
				return null;
			} catch (ClassNotFoundException e) {
				// Formato invalido de objeto
				e.printStackTrace();
				return null;
			}		
		}
		
		/**
		 * Baixa URL para arquivo
		 * @param sURL URL a ser baixada
		 * @param file arquivo a ser salvo
		 * @throws IOException
		 */
		private void downloadFile(String sURL, String file) throws IOException {
            URL url = new URL(sURL);
            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = ConfiguracoesActivity.this.openFileOutput(file, Context.MODE_PRIVATE);
            byte[] buffer = new byte[1024]; // Adjust if you want
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1)
            {
                output.write(buffer, 0, bytesRead);
            }
            output.flush();
            output.close();
            input.close();
		}
		
		@Override
		protected void onPostExecute(ArrayList<Restaurante> result) {
			progressDialog.dismiss();
			if (result == null) {
				//Mostra mensagem de erro
				restaurantesDisponiveis=null;
				
		        final AlertDialog alertDialog = new AlertDialog.Builder(ConfiguracoesActivity.this).create();
		        alertDialog.setTitle("Erro!");
		        alertDialog.setMessage("Erro ao baixar lista de restaurantes. Verifique se há conexão com a internet.");
		        alertDialog.setCanceledOnTouchOutside(true);
		        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		        alertDialog.setCancelable(true);
		        alertDialog.show();
		        
		        alertDialog.setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface arg0) {
						alertDialog.dismiss();
						new BaixaRestaurantes().execute();
					}
				});
			} else {
				//Mostra cardapios
				restaurantesDisponiveis=result;

				//Configura grid...
				GridView grid = (GridView)findViewById(R.id.RestaurantesGrid);
				grid.setAdapter(new RestauranteAdapter());
			}
		}
	}
	
	/**
	 * Inner Class responsavel por controlar lista de icones de restaurantes
	 * @author feliz
	 *
	 */
	private class RestauranteAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return restaurantesDisponiveis.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
			//Infla restaurante e pega objeto correspondente
			LayoutInflater li = LayoutInflater.from(ConfiguracoesActivity.this);
			View restauranteView = li.inflate(R.layout.restaurante_adapter, null);
			final Restaurante restaurante = restaurantesDisponiveis.get(position); 
			
			//Especifica icone do restaurante
			ImageView image = (ImageView)restauranteView.findViewById(R.id.RestauranteImage);
			File imgFile = getBaseContext().getFileStreamPath(restaurante.getImageFilename());
		    if(imgFile.exists())
		    {
		        image.setImageURI(Uri.fromFile(imgFile));
		    }
			//image.setImageResource(restaurante.getImagem());
			
			//Especifica comportamento e visual do check box
			CheckBox checkBox = (CheckBox)restauranteView.findViewById(R.id.RestauranteCheckbox);
			checkBox.setChecked(restaurantesSelecionados.contains(restaurante));
			
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					if (restaurantesSelecionados.contains(restaurante)) {
						restaurantesSelecionados.remove(restaurante);
					} else {
						restaurantesSelecionados.add(restaurante);
					}
					//Especifica se botao eh enable ou nao
					Button btn = (Button)findViewById(R.id.salvarButton);
					btn.setEnabled(restaurantesSelecionados.size()>0);
				}
				
			});
			return restauranteView;
		}
	}
	

}
