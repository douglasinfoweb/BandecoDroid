package com.douglasinfoweb.bandecodroid;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Cardapio.Refeicao;
import com.douglasinfoweb.bandecodroid.model.Configuracoes;
import com.douglasinfoweb.bandecodroid.model.Restaurante;
import com.tjeannin.apprate.AppRate;

/**
 * Activity responsavel por mostrar os cardapios
 * @author feliz
 *
 */
public class MainActivity extends Activity {
	private Configuracoes config;
	private Restaurante restauranteAtual;
   
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //APP RATER
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
        .setTitle("Ajude-nos")
        .setIcon(android.R.drawable.ic_dialog_info)
        .setMessage("Se você está curtindo o BandecoDroid, por favor ajude-nos a divulgá-lo recomendando a outros usuários.")
        .setPositiveButton("Recomendar !", null)
        .setNegativeButton("Não", null)
        .setNeutralButton("Depois", null);

        new AppRate(this)
        .setMinDaysUntilPrompt(7)
        .setMinLaunchesUntilPrompt(10)
        .setCustomDialog(builder)
        .init();
        
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        config = Configuracoes.read(this);
        if (config.getRestaurantesEscolhidos().size() == 0) {
        	abrirConfiguracoes();
        }
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Configuracoes config2 = Configuracoes.read(this);
    	if ((data == null || data.getExtras() == null || data.getExtras().get("config") == null)) {
    		if ((config2 == null || config2.getRestaurantesEscolhidos() == null || config2.getRestaurantesEscolhidos().size() == 0))
    			
    			abrirConfiguracoes();
    		else {
    	        config = Configuracoes.read(this);
    	        start();
    		}
    	} else {
    		config = (Configuracoes)data.getExtras().get("config");
        	Log.v("bandeco","config: total de restaurantes "+config.getRestaurantesEscolhidos().size());
        	start();
    	}
    }
    
    /**
     * Executado depois de ter recebido o objeto de configuração
     */
    private void start() {

		//Prepara spinner
		Spinner spinner = (Spinner)findViewById(R.id.mainSpinner);

		//Se tiver mais de um restaurante selecionado, mostra spinner
		if (config.getRestaurantesEscolhidos().size() > 1) {
			spinner.setVisibility(View.VISIBLE);
			//Cria adapter a partir de array
			ArrayAdapter<Restaurante> adapter = new ArrayAdapter<Restaurante>(this, android.R.layout.simple_spinner_item, config.getRestaurantesEscolhidos()); 
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
			spinner.setAdapter(adapter);
			
			spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				
				@Override
				public void onItemSelected(AdapterView<?> parent, View arg1,
						int pos, long arg3) {
					restauranteAtual = (Restaurante)parent.getItemAtPosition(pos);
					updateScreen();
				}
	
				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					//Do nothing
				}
			
			});
			spinner.setSelection(0);		
			restauranteAtual=config.getRestaurantesEscolhidos().get(0);
		} else {
			if (config.getRestaurantesEscolhidos().size() > 0)
				restauranteAtual=config.getRestaurantesEscolhidos().get(0);
			spinner.setVisibility(View.GONE);
		}
        //Atualiza os dados
		new BaixaCardapios().execute(false);
        
    }
    
    /**
	 * Inner Class responsável por baixar restaurantes selecionados no background
	 * @author feliz
	 */
    
	protected class BaixaCardapios extends AsyncTask<Boolean, Void, Boolean> {
		private ProgressDialog progressDialog;
		
		@Override
		protected void onPreExecute() {
	        /* Prepara e mostra tela de espera */
			progressDialog = new ProgressDialog(MainActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Baixando cardápios...");
			progressDialog.setCancelable(false);
			progressDialog.setProgress(0);
			progressDialog.show();
			
		}
		
		private Restaurante downloadRestaurante(String codigo) {
			try {
				URLConnection connection = new URL(Util.getBaseSite()+"json/"+codigo).openConnection();
				connection.setConnectTimeout(15 * 1000);
				connection.setReadTimeout(15 * 1000);
				InputStream inputStream = connection.getInputStream();
				JSONObject jRest = new JSONObject(IOUtils.toString(inputStream));
				//Populando novo restaurante
				Restaurante r = new Restaurante();
				r.nome = jRest.getString("nome");
				r.site = jRest.getString("site");
				r.codigo = jRest.getString("codigo");
				r.tinyUrl = jRest.getString("tinyUrl");
				JSONArray jCardapios = jRest.getJSONArray("cardapios");
				//Restaurando cardapios
				ArrayList<Cardapio> cardapios = new ArrayList<Cardapio>();
				for (int i=0; i < jCardapios.length(); i++) {
					JSONObject jCardapio = jCardapios.getJSONObject(i);
					Cardapio c = new Cardapio();
					c.setPratoPrincipal(jCardapio.getString("pratoPrincipal"));
					
					Refeicao refeicao;
					if (jCardapio.get("refeicao").equals("ALMOCO")) {
						refeicao = Refeicao.ALMOCO;
					} else {
						refeicao = Refeicao.JANTA;
					}
					c.setRefeicao(refeicao);
					
					DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
					DateTime dt = formatter.parseDateTime(jCardapio.getString("data"));
					c.setData(dt);
					
					if (!jCardapio.isNull("pts")) {
						c.setPts(jCardapio.getString("pts"));
					}
					
					if (!jCardapio.isNull("salada")) {
						c.setSalada(jCardapio.getString("salada"));
					}
					
					if (!jCardapio.isNull("sobremesa")) {
						c.setSobremesa(jCardapio.getString("sobremesa"));
					}
					
					if (!jCardapio.isNull("suco")) {
						c.setSuco(jCardapio.getString("suco"));
					}
					
					cardapios.add(c);
				}
				r.setCardapios(cardapios);
				return r;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		@Override
		protected Boolean doInBackground(Boolean... forcar) {
			ArrayList<Restaurante> restaurantes = config.getRestaurantesEscolhidos();
			for (Restaurante r : new ArrayList<Restaurante>(restaurantes)) {
				if (r.temQueAtualizar() || forcar[0]) {
					//Baixa objeto do restaurante
					Restaurante novoRestaurante  = downloadRestaurante(r.getCodigo());
					if (novoRestaurante == null)
						return false;
					//So por seguranca, se o servidor estiver desatualizado
					novoRestaurante.removeCardapiosAntigos();
					//Substitui restaurante na lista
					restaurantes.remove(r);
					restaurantes.add(novoRestaurante);
					//Substitui restaurante atual
					if (r.equals(restauranteAtual)) {
						restauranteAtual = novoRestaurante;
					}
					//Salva configuracao
					try {
						Configuracoes.save(MainActivity.this, config);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean okay) {
			progressDialog.dismiss();
			if (okay) {
				updateScreen();
			} else {
		        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
		        alertDialog.setTitle("Erro!");
		        alertDialog.setMessage("Erro ao baixar cardápio. Verifique conexão com internet.");
		        alertDialog.setCanceledOnTouchOutside(true);
		        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		        alertDialog.setCancelable(true);
		        alertDialog.show();
			}
		}
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem itemSite = menu.add(0, Menu.NONE, Menu.NONE, "Visitar Site");
    	itemSite.setIcon(R.drawable.ic_menu_info_details);
    	itemSite.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				//Chama browser no endereço do Restaurante
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(restauranteAtual.getSite()));
				startActivity(i);
				return true;
			}
		});

    	MenuItem itemShare = menu.add(0, Menu.NONE, Menu.NONE, "Compartilhar");
    	itemShare.setIcon(R.drawable.ic_menu_share);
    	itemShare.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				shareRestaurante();
				return true;
			}
		});
    	
    	MenuItem itemAtualiza = menu.add(0, Menu.NONE, Menu.NONE, "Atualizar");
    	itemAtualiza.setIcon(R.drawable.ic_menu_refresh);
    	itemAtualiza.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				//Força atualização
				new BaixaCardapios().execute(true);
				return true;
			}
		});
    	
    	MenuItem itemConfiguracoes = menu.add(0, Menu.NONE, Menu.NONE, "Configurações");
    	itemConfiguracoes.setIcon(R.drawable.ic_menu_preferences);
    	itemConfiguracoes.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				//Força atualização
				abrirConfiguracoes();
				return true;
			}
		}); 
    	return true;
    	
    }
    
    private void shareRestaurante() {
		//create the send intent
		Intent shareIntent = 
		 new Intent(android.content.Intent.ACTION_SEND);

		//set the type
		shareIntent.setType("text/plain");

		//add a subject
		shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
		 "Bandeco");

		//build the body of the message to be shared
		String shareMessage = "Cardapio da "+restauranteAtual.getNome()+" "+restauranteAtual.getTinyUrl();

		//add the message
		shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
		 shareMessage);

		//start the chooser for sharing
		startActivity(Intent.createChooser(shareIntent, 
		 "Compartilhar"));
    }
    
    /**
     * Abre janela de configuracoes, dando intent para ConfiguracoesActivity
     */
    public void abrirConfiguracoes() {
    	Intent intent = new Intent(this, ConfiguracoesActivity.class);
    	intent.putExtra("config", config);
    	startActivityForResult(intent, 0);
    }
    
    /**
     * Atualiza tela com estado atual
     */
    public void updateScreen() {
    	LinearLayout mainScroll = (LinearLayout)findViewById(R.id.main);
    	//Remove todas as views anteriores
    	mainScroll.removeAllViews();
    	
    	if (restauranteAtual != null) {
    		restauranteAtual.removeCardapiosAntigos();
    		//Adiciona cada cardapio
    		for (Cardapio cardapio : restauranteAtual.getCardapios()) {
    			mainScroll.addView(getCardapioView(cardapio));
    		}

    		//Se nao tiver cardapio, mostra erro
    		if (restauranteAtual.getCardapios().size() == 0) {
    			TextView text =new TextView(this);
    			text.setText("Nenhum cardápio atualizado disponível.");
    			mainScroll.addView(text);
    		}

    		//Poe botoes inferiores
    		LayoutInflater li = this.getLayoutInflater();
    		View buttonBar = li.inflate(R.layout.button_bar, null);
    		ImageButton info = (ImageButton)buttonBar.findViewById(R.id.info);
    		info.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//Chama browser no endereço do Restaurante
    				Intent i = new Intent(Intent.ACTION_VIEW);
    				i.setData(Uri.parse(restauranteAtual.getSite()));
    				startActivity(i);
				}
			});
    		ImageButton refresh = (ImageButton)buttonBar.findViewById(R.id.refresh);
    		refresh.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//Força atualização
					new BaixaCardapios().execute(true);
				}
			});
    		ImageButton preferences = (ImageButton)buttonBar.findViewById(R.id.preferences);

    		preferences.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//Força atualização
					abrirConfiguracoes();
				}
				
			});
    		ImageButton share = (ImageButton)buttonBar.findViewById(R.id.share);
    		share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					shareRestaurante();
				}
			});
    		mainScroll.addView(buttonBar);
    	} else {
    		//Isso nunca deveria acontecer
    		TextView text =new TextView(this);
    		text.setText("Erro: Nenhum restaurante atual");
    		mainScroll.addView(text);
    	}
    }	
    
    /**
     * Pega representação de um cardapio na tela
     * @param c Cardapio
     * @return representação do cardapio
     */
	public View getCardapioView(final Cardapio c) {
		//Pega o layout de cardapios
		View layout = (View)getLayoutInflater().inflate(R.layout.cardapio, null);
		//Pega o titulo do cardapio
		TextView titulo = (TextView)layout.findViewById(R.id.Titulo);
		ImageView icone = (ImageView)layout.findViewById(R.id.cardapio_ic);
		switch (c.getRefeicao()) {
			case ALMOCO: 
						icone.setImageResource(R.drawable.ic_sol);
						titulo.setText("ALMOÇO "+Util.int2diaDaSemana(c.getData().getDayOfWeek(),false));
						break;
			case JANTA: 
						icone.setImageResource(R.drawable.ic_lua);
						titulo.setText("JANTAR "+Util.int2diaDaSemana(c.getData().getDayOfWeek(),false));
						break;
		}
		setLayoutText(layout,
				R.id.PratoPrincipalRow,
				R.id.PratoPrincipal,
				(c.getPratoPrincipal() != null && c.getPratoPrincipal().length() > 2),
				c.getPratoPrincipal());

		setLayoutText(layout,
				R.id.SucoRow,
				R.id.Suco,
				(c.getSuco() != null && c.getSuco().length() > 2),
				c.getSuco());
		if (c.getPts() != null && c.getPts().length() > 2) {
				setLayoutText(layout,
						R.id.SaladaRow,
						R.id.Salada,
						(c.getSalada() != null && c.getSalada().length() > 2),
						c.getSalada()+"\n"+c.getPts());
		}  else {
			setLayoutText(layout,
					R.id.SaladaRow,
					R.id.Salada,
					(c.getSalada() != null && c.getSalada().length() > 2),
					c.getSalada());
		}
		setLayoutText(layout,
				R.id.SobremesaRow,
				R.id.Sobremesa,
				(c.getSobremesa() != null && c.getSobremesa().length() > 2),
				c.getSobremesa());
	
		return layout;
	}
	
	private void setLayoutText(View layout, int rowID, int textID, boolean show, String text) {
		TableRow rowView = (TableRow)layout.findViewById(rowID);
		TextView textView = (TextView)layout.findViewById(textID);
		

		textView.setText(text);
		if (!show) {
			rowView.setVisibility(View.GONE);
		} else {
			rowView.setVisibility(View.VISIBLE);
		}
	}
}