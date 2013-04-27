package com.douglasinfoweb.bandecodroid;

import com.douglasinfoweb.bandecodroid.model.Configuracoes;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class AppWidgetConfigure extends Activity {
    private int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private final static String PREFS_NAME = "com.douglasinfoweb.bandecodroid.AppWidgetService";
    private Spinner spinner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);
		setContentView(R.layout.widget_config);
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
        
		final Configuracoes config = Configuracoes.read(this);
		
		if (config.getRestaurantesEscolhidos().size() > 1) {
			findViewById(R.id.widget_config_salvar).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					Restaurante r = (Restaurante)spinner.getSelectedItem();
					int rID = config.getRestaurantesEscolhidos().indexOf(r);
					saveRestaurantePref(getApplicationContext(), mAppWidgetId, rID);
					mostrarWidget(rID);
					finish();
				}
			});
			spinner = (Spinner)findViewById(R.id.widget_config_spinner);
			ArrayAdapter<Restaurante> adapter = new ArrayAdapter<Restaurante>(this, android.R.layout.simple_spinner_item, config.getRestaurantesEscolhidos()); 
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinner.setAdapter(adapter);
	        
	        
		} else {
			if (config.getRestaurantesEscolhidos().size() == 1) {
				saveRestaurantePref(getApplicationContext(), mAppWidgetId, 0);
				mostrarWidget(0);
			}
			else
				finish();
		}
	}
	private void mostrarWidget(int rID) {
        // Push widget update to surface with newly set prefix
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        AppWidgetService.updateAppWidget(this, appWidgetManager,
                mAppWidgetId, rID);
        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
	}
    static void saveRestaurantePref(Context context, int appWidgetId, int restauranteID) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(String.valueOf(appWidgetId), restauranteID);
        prefs.commit();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static int loadRestaurantePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int prefix = prefs.getInt(String.valueOf(appWidgetId), 0);
        return prefix;
    }
}
