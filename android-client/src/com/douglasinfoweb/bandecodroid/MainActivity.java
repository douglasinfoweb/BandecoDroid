package com.douglasinfoweb.bandecodroid;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Configuracoes;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

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
        setContentView(R.layout.main);
        config = Configuracoes.read(this);
        if (config.getRestaurantesEscolhidos().size() == 0) {
        	abrirConfiguracoes();
        }
        start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (data == null || data.getExtras() == null || data.getExtras().get("config") == null) {
        	abrirConfiguracoes();
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
		
		@Override
		protected Boolean doInBackground(Boolean... forcar) {
			ArrayList<Restaurante> restaurantes = config.getRestaurantesEscolhidos();
			for (Restaurante r : new ArrayList<Restaurante>(restaurantes)) {
				if (r.temQueAtualizar() || forcar[0]) {
					//Baixa lista de restaurantes
					InputStream inputStream;
					try {
						//Baixa objeto do restaurante
						inputStream = new URL(Util.getBaseSite()+"obj/"+r.getCodigo()).openStream();
						ObjectInputStream objStream = new ObjectInputStream(inputStream);
						Restaurante novoRestaurante  = (Restaurante)objStream.readObject();
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
						Configuracoes.save(MainActivity.this, config);
					} catch (MalformedURLException e) {
						e.printStackTrace();
						return false;
					} catch (IOException e) {
						e.printStackTrace();
						return false;
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
						return false;
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
    	MenuItem itemAtualiza = menu.add(0, Menu.NONE, Menu.NONE, "Atualizar");
    	itemAtualiza.setIcon(R.drawable.ic_menu_refresh);
    	itemAtualiza.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				new BaixaCardapios().execute(true);
				return true;
			}
		});
    	
    	MenuItem itemConfiguracoes = menu.add(0, Menu.NONE, Menu.NONE, "Configurações");
    	itemConfiguracoes.setIcon(R.drawable.ic_menu_preferences);
    	itemConfiguracoes.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				abrirConfiguracoes();
				return true;
			}
		});
    	return true;
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

    		//Poe botao do restaurante
    		Button btn = new Button(this);
    		btn.setText("Visitar site");
    		btn.setOnClickListener(new OnClickListener() {			
    			@Override
    			public void onClick(View arg0) {
    				//Chama browser no endereço do Restaurante
    				Intent i = new Intent(Intent.ACTION_VIEW);
    				i.setData(Uri.parse(restauranteAtual.getSite()));
    				startActivity(i);
    			}
    		});
    		mainScroll.addView(btn);
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
	public View getCardapioView(Cardapio c) {
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