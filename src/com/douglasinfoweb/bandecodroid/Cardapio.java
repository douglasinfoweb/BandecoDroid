package com.douglasinfoweb.bandecodroid;

import java.io.Serializable;

import org.joda.time.DateTime;

import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
/**
 * Classe responsavel pelo armazenamento do cardapio em um dia
 * @author Vinicius
 *
 */
public class Cardapio implements Serializable {
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
		TableLayout table = (TableLayout)layout.findViewById(R.id.cardapioTable);
		TextView titulo = (TextView)layout.findViewById(R.id.Titulo);
		ImageView imageTop = (ImageView)layout.findViewById(R.id.topImage);
		ImageView imageBottom = (ImageView)layout.findViewById(R.id.bottomImage);
		
		if (refeicao.equals(Refeicao.ALMOCO)) {
			table.setBackgroundResource(R.drawable.background_almoco);
		} else {
			table.setBackgroundResource(R.drawable.background_jantar);
		}
		BitmapDrawable bm= (BitmapDrawable)table.getBackground();
		bm.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		switch (refeicao) {
			case ALMOCO: titulo.setText("ALMOÇO "+int2diaDaSemana(data.getDayOfWeek())); 
						 imageTop.setImageResource(R.drawable.cardapio_almoco_top);
						 imageBottom.setImageResource(R.drawable.cardapio_almoco_bottom);
						break;
			case JANTA: titulo.setText("JANTAR "+int2diaDaSemana(data.getDayOfWeek()));
						imageTop.setImageResource(R.drawable.cardapio_jantar_top);
						imageBottom.setImageResource(R.drawable.cardapio_jantar_bottom);
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
		
		setLayoutText(layout,
				R.id.SaladaRow,
				R.id.Salada,
				(salada != null && salada.length() > 2),
				salada);
		
		setLayoutText(layout,
				R.id.SobremesaRow,
				R.id.Sobremesa,
				(sobremesa != null && sobremesa.length() > 2),
				sobremesa);
		
		setLayoutText(layout,
				R.id.PtsRow,
				R.id.Pts,
				(pts != null && pts.length() > 2),
				pts);
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
	private String int2diaDaSemana(int dayOfWeek) {
		String result;
		switch (dayOfWeek) {
			case 1: result = "Segunda-feira"; break;
			case 2: result = "Terça-feira"; break;
			case 3: result = "Quarta-feira"; break;
			case 4: result = "Quinta-feira"; break;
			case 5: result = "Sexta-feira"; break;
			case 6: result = "Sábado"; break;
			case 7: result = "Domingo"; break;
			default: result = ""; break;
		}
		return result;
	}
	
}


