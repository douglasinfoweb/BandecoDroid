package com.douglasinfoweb.bandecodroid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import com.douglasinfoweb.bandecodroid.model.Configuracoes;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TabHost;

public class ConfiguracoesActivity extends Activity {
	private Configuracoes config;
	private HashSet<Restaurante> restaurantesSelecionados;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuracoes);
		
		//Pega configuracoes
		Intent intent=getIntent();
		config = (Configuracoes)intent.getExtras().get("config");
		restaurantesSelecionados = new HashSet<Restaurante>(config.getRestaurantesEscolhidos());
		//Cria o tabHost
		TabHost tabHost = (TabHost)findViewById(R.id.tabConfiguracoes);
		tabHost.setup();
		Resources res = getResources();
		TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    spec = tabHost.newTabSpec("restaurantes");
	    spec.setIndicator("", res.getDrawable(R.drawable.ic_restaurantes));
	    spec.setContent(R.id.RestaurantesGrid);
	    tabHost.addTab(spec);
	    spec = tabHost.newTabSpec("sobre");
	    spec.setIndicator("", res.getDrawable(android.R.drawable.ic_menu_help));
	    spec.setContent(R.id.about);
	    tabHost.addTab(spec);
	    //E a grid...
	    GridView grid = (GridView)findViewById(R.id.RestaurantesGrid);
		grid.setAdapter(new RestauranteAdapter(this));
		//Botao de salvar
		Button btn = (Button)findViewById(R.id.salvarButton);
		final ConfiguracoesActivity act = this;
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (getRestaurantesSelecionados().size() == 0)
					return;
				try {
					ArrayList<Restaurante> velhosRestaurantes = getConfiguracoes().getRestaurantesEscolhidos();
					ArrayList<Restaurante> novosRestaurantes = new ArrayList<Restaurante>();
					for (Restaurante r : Restaurante.possiveisRestaurantes) {
						if (getRestaurantesSelecionados().contains(r)) {
							if (velhosRestaurantes.contains(r)) {
								novosRestaurantes.add(velhosRestaurantes.get(velhosRestaurantes.indexOf(r)));
							} else {
								novosRestaurantes.add(r);
							}
						}
					}
					getConfiguracoes().setRestaurantesEscolhidos(novosRestaurantes);
					Configuracoes.save(act, act.getConfiguracoes());
					Intent resultado = new Intent();
					resultado.putExtra("config", act.getConfiguracoes());
					act.setResult(RESULT_OK, resultado);
					act.finish();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void update() {
		Button btn = (Button)findViewById(R.id.salvarButton);
		btn.setEnabled((getRestaurantesSelecionados().size()>0));
	}
	
	public Configuracoes getConfiguracoes() {
		return config;
	}

	public HashSet<Restaurante> getRestaurantesSelecionados() {
		return restaurantesSelecionados;
	}

	public void setRestaurantesSelecionados(HashSet<Restaurante> restaurantesSelecionados) {
		this.restaurantesSelecionados = restaurantesSelecionados;
	}

}
