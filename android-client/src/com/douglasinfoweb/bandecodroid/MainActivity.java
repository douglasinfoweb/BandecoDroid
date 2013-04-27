package com.douglasinfoweb.bandecodroid;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

public class MainActivity extends Activity {
	private Configuracoes config;
    private MainActivity main=this;
	private LoadDataThread loadDataThread;
	private ProgressDialog progressDialog;
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
    private void start() {
        /* Prepara e mostra tela de espera */
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Baixando cardapios");
		progressDialog.setCancelable(false);
		progressDialog.setProgress(0);
		progressDialog.show();

		//Prepara spinner
		Spinner spinner = (Spinner)findViewById(R.id.mainSpinner);

		if (config.getRestaurantesEscolhidos().size() > 1) {
			spinner.setVisibility(View.VISIBLE);
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
        Log.v("bandeco","comecou a pegar dados");
        loadDataThread = new LoadDataThread(this);
        updateScreen();
        loadDataThread.start(false);
        //E agora a tela
		updateScreen();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem itemAtualiza = menu.add(0, Menu.NONE, Menu.NONE, "Atualizar");
    	itemAtualiza.setIcon(R.drawable.ic_menu_refresh);
    	itemAtualiza.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
		        loadDataThread = new LoadDataThread(main);
		        updateScreen();
		        loadDataThread.start(true);
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
    
    public void abrirConfiguracoes() {
    	Intent intent = new Intent(main, ConfiguracoesActivity.class);
    	intent.putExtra("config", config);
    	startActivityForResult(intent, 0);
    }
    public void updateScreen() {
	    	if (loadDataThread.isDataReady()) {
	    		if (progressDialog != null)
	    			progressDialog.dismiss();
	    		LinearLayout mainScroll = (LinearLayout)findViewById(R.id.main);
    			mainScroll.removeAllViews();
    			if (config.getRestaurantesEscolhidos().size() > 0) {
	    			if (restauranteAtual != null) {
			    		for (Cardapio cardapio : restauranteAtual.getCardapios()) {
		    				mainScroll.addView(getCardapioView(cardapio));
		    			}
			    		if (restauranteAtual.getCardapios().size() == 0) {
		    				TextView text =new TextView(this);
		    				text.setText("Não foi possível recuperar nenhum cardápio");
		    				mainScroll.addView(text);
			    		}
			    		Button btn = new Button(this);
			    		btn.setText("Ir para site do restaurante");
			    		btn.setOnClickListener(new OnClickListener() {			
							@Override
							public void onClick(View arg0) {
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(restauranteAtual.getSite()));
								startActivity(i);
							}
						});
			    		mainScroll.addView(btn);
	    			} else {
	    				TextView text =new TextView(this);
	    				text.setText("Erro: Nenhum restaurante atual");
	    				mainScroll.addView(text);
	    			}
    			}
	    	} else {
	    		if (progressDialog != null)
	    			progressDialog.show();
	    	}
    }	
    @Override
    protected void onStop() {
    	super.onStop();
    	if (progressDialog != null) {
	    	progressDialog.dismiss();
	    	progressDialog=null;
    	}
    }
    public void save() throws IOException {
    	Configuracoes.save(this, config);
    }
	public Configuracoes getConfig() {
		return config;
	}
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