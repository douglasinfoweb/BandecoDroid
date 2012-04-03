package com.douglasinfoweb.bandecodroid;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

public class RestauranteAdapter extends BaseAdapter {
    private ConfiguracoesActivity activity;
    private Configuracoes config;

    public RestauranteAdapter(ConfiguracoesActivity c, Configuracoes conf) {
        activity = c;
        config=conf;
    }

	@Override
	public int getCount() {
		return Restaurante.possiveisRestaurantes.length;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater li = LayoutInflater.from(activity);
		View restauranteView = li.inflate(R.layout.restaurante_adapter, null);
		final Restaurante restaurante = Restaurante.possiveisRestaurantes[position]; 
		
		ImageView image = (ImageView)restauranteView.findViewById(R.id.RestauranteImage);
		image.setBackgroundResource(restaurante.getImagem());
		
		CheckBox checkBox = (CheckBox)restauranteView.findViewById(R.id.RestauranteCheckbox);
		checkBox.setChecked(config.getRestaurantesEscolhidos().contains(restaurante));
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (config.getRestaurantesEscolhidos().contains(restaurante)) {
					config.getRestaurantesEscolhidos().remove(restaurante);
				} else {
					config.getRestaurantesEscolhidos().add(restaurante);
				}
				activity.update();
			}
			
		});
		activity.update();
		return restauranteView;
	}
}
