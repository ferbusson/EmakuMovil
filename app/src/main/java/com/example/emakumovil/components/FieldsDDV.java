package com.example.emakumovil.components;

public class FieldsDDV {
	
	private String descripcion1;
	private String descripcion2;
	private String valor;
	private double dvalor;
	private double debe;
	private double haber;
	private int cantidad;
	private double pventa;
	private double total;

	public FieldsDDV(String descripcion1,String descripcion2,String valor) {
		this.descripcion1=descripcion1;
		this.descripcion2=descripcion2;
		this.valor=valor;
	}

	public FieldsDDV(String descripcion1,String descripcion2,String valor,double dvalor) {
		this.descripcion1=descripcion1;
		this.descripcion2=descripcion2;
		this.valor=valor;
		this.dvalor=dvalor;
	}

	public FieldsDDV(String descripcion1,String descripcion2,double debe,double haber) {
		this.descripcion1=descripcion1;
		this.descripcion2=descripcion2;
		this.debe=debe;
		this.haber=haber;
	}

	public FieldsDDV(String descripcion1,String descripcion2,int cantidad,double pventa,double total) {
		this.descripcion1=descripcion1;
		this.descripcion2=descripcion2;
		this.cantidad=cantidad;
		this.pventa=pventa;
		this.total=total;
	}

	public FieldsDDV(String descripcion1,String valor,double dvalor) {
		this.descripcion1=descripcion1;
		this.valor=valor;
		this.dvalor=dvalor;
	}

	public FieldsDDV(String descripcion1,String valor) {
		this.descripcion1=descripcion1;
		this.valor=valor;
	}

	public String getDescripcion1() {
		return descripcion1;
	}
	public String getDescripcion2() {
		return descripcion2;
	}
	public String getValor() {
		return valor;
	}
	
	public double getDvalor() {
		return dvalor;
	}
	
	public double getDebe() {
		return debe;
	}

	public double getHaber() {
		return haber;
	}

	public int getCantidad() {
		return cantidad;
	}

	public double getPventa() {
		return pventa;
	}

	public double getTotal() {
		return total;
	}

	public void setDebe(double debe) {
		this.debe=debe;
	}

	public void setHaber(double haber) {
		this.haber=haber;
	}

	public void setDvalor(double dvalor) {
		this.dvalor=dvalor;
	}

	public void setValor(String valor) {
		this.valor=valor;
	}

}
