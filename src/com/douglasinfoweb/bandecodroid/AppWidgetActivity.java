package com.douglasinfoweb.bandecodroid;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AppWidgetActivity extends Activity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private Spinner spinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		
		Configuracoes config = Configuracoes.read(this);
		
		if (config.getRestaurantesEscolhidos().size() > 1) {
			setContentView(R.layout.widget_config);
			findViewById(R.id.widget_config_salvar).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Restaurante r = (Restaurante)spinner.getSelectedItem();
					mostrarWidget(r);
					AppWidget.getRestaurantesWidgets().put(mAppWidgetId, r);
					finish();
				}
			});
			spinner = (Spinner)findViewById(R.id.widget_config_spinner);
			ArrayAdapter<Restaurante> adapter = new ArrayAdapter<Restaurante>(this, android.R.layout.simple_spinner_item, config.getRestaurantesEscolhidos()); 
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
	        // Find the widget id from the intent. 
	        Intent intent = getIntent();
	        Bundle extras = intent.getExtras();
	        if (extras != null) {
	            mAppWidgetId = extras.getInt(
	                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
	        }

	        // If they gave us an intent without the widget id, just bail.
	        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
	            finish();
	        }
	        
		} else {
			if (config.getRestaurantesEscolhidos().size() == 1)
				mostrarWidget(config.getRestaurantesEscolhidos().get(0));
			else
				finish();
		}
	}
	private void mostrarWidget(Restaurante r) {
        // Push widget update to surface with newly set prefix
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        AppWidget.updateAppWidget(this, appWidgetManager,
                mAppWidgetId, r);
        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
	}
}
