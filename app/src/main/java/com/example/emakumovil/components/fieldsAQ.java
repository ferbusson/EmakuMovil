package com.example.emakumovil.components;

public class fieldsAQ {
	private String usuario;
	private String narqueo;
	private String ndocumento;
	private double valor;
	private String recaudo;
	private String diff;
	private double diffn;
	private String fecha;
	private String total;
	private boolean repeat;
	
	public fieldsAQ(String usuario, String narqueo, double valor, String recaudo,
			String diff, double diffn, String fecha, String total,Boolean repeat,String ndocumento) {
		this.usuario = usuario;
		this.narqueo = narqueo;
		this.valor = valor;
		this.recaudo = recaudo;
		this.diff = diff;
		this.diffn = diffn;
		this.fecha = fecha;
		this.total = total;
		this.repeat=repeat;
		this.ndocumento=ndocumento;
	}

	public boolean isRepeat() {
		return repeat;
	}
	
	public String getNdocumento() {
		return ndocumento;
	}
	
	public String getFecha() {
		return fecha;
	}

	public String getTotal() {
		return total;
	}

	public String getUsuario() {
		return usuario;
	}

	public String getNarqueo() {
		return narqueo;
	}

	public double getValor() {
		return valor;
	}

	public String getRecaudo() {
		return recaudo;
	}

	public String getDiff() {
		return diff;
	}

	public double getDiffn() {
		return diffn;
	}

}
