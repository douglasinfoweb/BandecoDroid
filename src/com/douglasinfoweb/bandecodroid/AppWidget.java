package com.douglasinfoweb.bandecodroid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

public class AppWidget extends AppWidgetProvider 
{
	private static HashMap<Integer,Restaurante> restaurantesWidgets  = new HashMap<Integer,Restaurante>();
	
	public static HashMap<Integer,Restaurante> getRestaurantesWidgets() {
		return restaurantesWidgets;
	}
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
        for (int widgetID : appWidgetIds) {
        	if (restaurantesWidgets.containsKey(widgetID)) {
        		updateAppWidget(context, appWidgetManager, widgetID, restaurantesWidgets.get(widgetID));
        	}
       }
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int mAppWidgetId, Restaurante r) {
        

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        if (r != null) {
	        ArrayList<Cardapio> cardapios = r.getCardapios();
	        if (cardapios.size() > 0) {
	        	Collections.sort(cardapios);
	        	Cardapio cardapio = cardapios.get(0);
	        	if (cardapio.getData() != null) {
			        views.setTextViewText(R.id.Titulo_widget, Util.int2diaDaSemana(cardapio.getData().getDayOfWeek()));
			        Configuracoes config = Configuracoes.read(context);
			        if (config.getRestaurantesEscolhidos().size() == 1) {
			        	views.setTextViewText(R.id.PratoPrincipalRow_widget, 
			        			cardapio.getPratoPrincipal());
			        } else {
			        	views.setTextViewText(R.id.PratoPrincipalRow_widget, 
			        			r.getNome()+"\n"+cardapio.getPratoPrincipal());
			        }
			   }
	        }
        }
        appWidgetManager.updateAppWidget(mAppWidgetId, views);		
	}
	
}
