package com.douglasinfoweb.bandecodroid;

import java.io.Serializable;

import org.joda.time.DateTime;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
/**
 * Classe responsavel pelo armazenamento do cardapio em um dia
 * @author Vinicius
 *
 */
public class Cardapio implements Serializable, Comparable<Cardapio> {
	private static final long serialVersionUID = 5627453443659151036L;
	private DateTime data;
	private String pratoPrincipal;
	private String pts;
	private String salada;
	private String sobremesa;
	private String suco;
	private String obs;
	public enum Refeicao {ALMOCO, JANTA};
	private Refeicao refeicao;
	
	public String getPratoPrincipal() {
		return pratoPrincipal;
	}
	public void setPratoPrincipal(String pratoPrincipal) {
		this.pratoPrincipal = pratoPrincipal;
	}
	public String getPts() {
		return pts;
	}
	public void setPts(String pts) {
		this.pts = pts;
	}
	public String getSalada() {
		return salada;
	}
	public void setSalada(String salada) {
		this.salada = salada;
	}
	public String getSobremesa() {
		return sobremesa;
	}
	public void setSobremesa(String sobremesa) {
		this.sobremesa = sobremesa;
	}
	public String getSuco() {
		return suco;
	}
	public void setSuco(String suco) {
		this.suco = suco;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
	public DateTime getData() {
		return data;
	}
	public void setData(DateTime data) {
		this.data = data;
	}
	public Refeicao getRefeicao() {
		return refeicao;
	}
	public void setRefeicao(Refeicao refeicao) {
		this.refeicao = refeicao;
	}
	
	public View getCardapioView(LayoutInflater vi) {
		//Pega o layout de cardapios
		View layout = (View)vi.inflate(R.layout.cardapio, null);
		//Pega o titulo do cardapio
		TextView titulo = (TextView)layout.findViewById(R.id.Titulo);
		ImageView icone = (ImageView)layout.findViewById(R.id.cardapio_ic);
		switch (refeicao) {
			case ALMOCO: 
						icone.setImageResource(R.drawable.ic_sol);
						titulo.setText("ALMOÇO "+Util.int2diaDaSemana(data.getDayOfWeek(),false));
						break;
			case JANTA: 
						icone.setImageResource(R.drawable.ic_lua);
						titulo.setText("JANTAR "+Util.int2diaDaSemana(data.getDayOfWeek(),false));
						break;
		}
		setLayoutText(layout,
				R.id.PratoPrincipalRow,
				R.id.PratoPrincipal,
				(pratoPrincipal != null && pratoPrincipal.length() > 2),
				pratoPrincipal);

		setLayoutText(layout,
				R.id.SucoRow,
				R.id.Suco,
				(suco != null && suco.length() > 2),
				suco);
		if (pts != null && pts.length() > 2) {
				setLayoutText(layout,
						R.id.SaladaRow,
						R.id.Salada,
						(salada != null && salada.length() > 2),
						salada+"\n"+pts);
		}  else {
			setLayoutText(layout,
					R.id.SaladaRow,
					R.id.Salada,
					(salada != null && salada.length() > 2),
					salada);
		}
		setLayoutText(layout,
				R.id.SobremesaRow,
				R.id.Sobremesa,
				(sobremesa != null && sobremesa.length() > 2),
				sobremesa);
	
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
	
	@Override
	public String toString() {
		return data+" - "+pratoPrincipal;
	}
	@Override
	public int compareTo(Cardapio arg0) {
		if (getData() != null && arg0.getData() != null) {
			if (arg0.getData().isBefore(getData())) {
				Log.v("bandeco", "1");
				return 1;
			} else if (arg0.getData().isEqual(getData())) {
				Log.v("bandeco", "0");
				return 0;
			} else {
				Log.v("bandeco", "-1");
				return -1;
			}
		}
		return 0;
	}
	
}


