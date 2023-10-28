package com.example.emakumovil.components;

public class recordsData {

	private String id;
	private String id2;
	private String id3;
	
	private String codigo;
	private String descripcion;
	private String descripcion2;
	private String descripcion3;
	private String valor;
	private double dvalor;

	public recordsData(String id,String descripcion) {
		this.id=id;
		this.descripcion=descripcion;
	}
		public recordsData(String id,String codigo,String descripcion) {
		this.id=id;
		this.codigo=codigo;
		this.descripcion=descripcion;
	}

	public recordsData(String id,String codigo,String descripcion,String descripcion2,String descripcion3,String id2,String id3) {
		this.id=id;
		this.codigo=codigo;
		this.descripcion=descripcion;
		this.descripcion2=descripcion2;
		this.descripcion3=descripcion3;
		this.id2=id2;
		this.id3=id3;
	}

	public recordsData(String id,String codigo,String descripcion,String descripcion2,String descripcion3,String valor,double dvalor) {
		this.id=id;
		this.codigo=codigo;
		this.descripcion=descripcion;
		this.descripcion2=descripcion2;
		this.descripcion3=descripcion3;
		this.valor=valor;
		this.dvalor=dvalor;
	}

	public String getId() {
		return id;
	}

	public String getId2() {
		return id2;
	}

	public String getId3() {
		return id3;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getDescripcion2() {
		return descripcion2;
	}

	public String getDescripcion3() {
		return descripcion3;
	}

	public String getValor() {
		return valor;
	}
	public double getDvalor() {
		return dvalor;
	}
	
}
