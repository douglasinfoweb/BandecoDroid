package com.douglasinfoweb.bandecodroid;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class AppWidget extends AppWidgetProvider 
{
	private Configuracoes config;
	private LoadDataThread loadDataThread;
	private ProgressDialog progressDialog;
	private Restaurante restauranteAtual;
	private Context mContext;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		mContext = context;
		config = new Configuracoes();
        /* Tentar recuperar dados */
        try {
			FileInputStream fis = context.openFileInput("config");
			ObjectInputStream ois = new ObjectInputStream(fis);
			config = (Configuracoes)ois.readObject();
			ois.close();
			Log.v("bandeco","recuperou objeto com sucesso :D");
		} catch (Exception e) {
			e.printStackTrace(); 
		}
        if (config.getRestaurantesEscolhidos().size() == 0) {
        	abrirConfiguracoes();
        }
        start();
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	public void abrirConfiguracoes() {
    	Intent intent = new Intent(mContext, ConfiguracoesActivity.class);
    	intent.putExtra("config", config);
    	startActivityForResult(intent, 0);
    }
	
	private void start() {
        /* Prepara e mostra tela de espera */
		progressDialog = new ProgressDialog(this.mContext);
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
					case ALMOCO: titulo.setText("ALMOÇO "+int2diaDaSemana(cardapio.getData().getDayOfWeek())); 
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
}
