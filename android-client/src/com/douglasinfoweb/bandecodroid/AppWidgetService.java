package com.douglasinfoweb.bandecodroid;

import java.util.ArrayList;
import java.util.Collections;

import com.douglasinfoweb.bandecodroid.model.Cardapio;
import com.douglasinfoweb.bandecodroid.model.Configuracoes;
import com.douglasinfoweb.bandecodroid.model.Restaurante;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class AppWidgetService extends AppWidgetProvider 
{
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) 
	{
        for (int widgetID : appWidgetIds) {
        	updateAppWidget(context, appWidgetManager, widgetID, AppWidgetConfigure.loadRestaurantePref(context, widgetID));
       }
	}

	public static void updateAppWidget(Context context,
			AppWidgetManager appWidgetManager, int mAppWidgetId, int restauranteID) {

        Configuracoes config = Configuracoes.read(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        //Adiciona a funcionalidade de ao clicar, abrir o programa
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.backgroud_widget, pendingIntent);
        
        if (config.getRestaurantesEscolhidos().size() > restauranteID) {
        	Restaurante r = config.getRestaurantesEscolhidos().get(restauranteID);
        	ArrayList<Cardapio> cardapios = r.getCardapios(); 
        	r.removeCardapiosAntigos();
        	Collections.sort(cardapios);
        	if (r.getCardapios().size() > 0 && r.getCardapios().get(0).getData() != null) {
	        	Cardapio cardapio = cardapios.get(0);
		        views.setTextViewText(R.id.Titulo_widget, Util.int2diaDaSemana(cardapio.getData().getDayOfWeek(),true));
		        if (config.getRestaurantesEscolhidos().size() == 1) {
		        	views.setTextViewText(R.id.txt_widget, cardapio.getPratoPrincipal());
		        } else {
		        	views.setTextViewText(R.id.txt_widget, r.getNome()+"\n"+cardapio.getPratoPrincipal());
		        }
		        //Se for janta, invert
		        /*
		        if (cardapio.getRefeicao().equals(Refeicao.JANTA)) {
		        	views.setImageViewResource(R.id.topImage_widget, R.drawable.cardapio_jantar_top);
		        	views.setImageViewResource(R.id.PratoPrincipalRow_widget, R.drawable.background_jantar);
		        	views.setImageViewResource(R.id.bottomImage_widget, R.drawable.cardapio_jantar_bottom);
		        } else {
		        	views.setImageViewResource(R.id.topImage_widget, R.drawable.cardapio_almoco_top);
		        	views.setImageViewResource(R.id.PratoPrincipalRow_widget, R.drawable.background_almoco);
		        	views.setImageViewResource(R.id.bottomImage_widget, R.drawable.cardapio_almoco_bottom);
		        }*/
        	}
        } else {
	        views.setTextViewText(R.id.Titulo_widget, "Sou inútil");
	        views.setTextViewText(R.id.txt_widget, "ERRO! Me remova");
        }
        appWidgetManager.updateAppWidget(mAppWidgetId, views);		
	}
	
}
