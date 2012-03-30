package com.douglasinfoweb.bandecodroid;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;

public class AppWidget extends AppWidgetProvider 
{
	private Restaurante restaurante;
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
		try {
			FileInputStream fis = context.openFileInput("cardapios");
			ObjectInputStream ois = new ObjectInputStream(fis);
			restaurante = (Restaurante)ois.readObject();
			ois.close();
			Log.v("bandeco","recuperou objeto com sucesso :D");
		} catch (Exception e) {
			restaurante = new BandecoUnicamp();
		}
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
