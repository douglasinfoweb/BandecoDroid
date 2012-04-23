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

    public RestauranteAdapter(ConfiguracoesActivity c) {
        activity = c;
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
		image.setImageResource(restaurante.getImagem());
		
		CheckBox checkBox = (CheckBox)restauranteView.findViewById(R.id.RestauranteCheckbox);
		checkBox.setChecked(activity.getRestaurantesSelecionados().contains(restaurante));
		
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (activity.getRestaurantesSelecionados().contains(restaurante)) {
					activity.getRestaurantesSelecionados().remove(restaurante);
				} else {
					activity.getRestaurantesSelecionados().add(restaurante);
				}
				activity.update();
			}
			
		});
		activity.update();
		return restauranteView;
	}
}
