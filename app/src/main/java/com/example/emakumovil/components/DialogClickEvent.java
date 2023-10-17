package com.example.emakumovil.components;

import java.util.EventObject;

import org.jdom2.Element;

/**
 * DialogClickEvent.java Creado el Jueves Santo 28-Mar-2013
 * 
 * Este archivo es parte de E-Maku Movil
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Clase necesaria para el manejo de eventos por click en boton aceptar de un
 * DialogFragment
 * <br>
 * @author <A href='mailto:felipe@gmail.com'>Luis Felipe Hernandez</A>
 */

public class DialogClickEvent extends EventObject {

	/**
	 * 
	 */
	private double efectivoneto;
	private double cambio;
	private double tarjetas;
	private double bancos;
	private int cantidad;
	
	private String id_tarjeta;
	private String id_banco;
	private String id_banco_tarjeta;
	
	private String id;
	private String value;
	private long b;
	
	private Element retenciones;
	private double valor;
	private double tretenciones;
	
	private static final long serialVersionUID = 1L;


	public DialogClickEvent(Object source,int cantidad) {
		super(source);
		this.cantidad=cantidad;
	}
	
	public DialogClickEvent(Object source,long b,Element retenciones,double valor,double tretenciones) {
		super(source);
		this.b=b;
		this.retenciones=retenciones;
		this.tretenciones=tretenciones;
		this.valor=valor;
	}

	public DialogClickEvent(Object source,String value,long b) {
		super(source);
		this.value=value;
		this.b=b;
	}

	public DialogClickEvent(Object source,String id,String value,long b) {
		super(source);
		this.id=id;
		this.value=value;
		this.b=b;
	}

	public DialogClickEvent(Object source,double efectivoneto,double cambio,double bancos,String id_banco) {
		super(source);
		// TODO Auto-generated constructor stub
		this.efectivoneto=efectivoneto;
		this.cambio=cambio;
		this.bancos=bancos;
		this.id_banco=id_banco;
	}

	public DialogClickEvent(Object source,double efectivoneto,double cambio,double tarjetas,double bancos,String id_tarjeta,String id_banco_tarjeta,String id_banco) {
		super(source);
		// TODO Auto-generated constructor stub
		this.efectivoneto=efectivoneto;
		this.cambio=cambio;
		this.tarjetas=tarjetas;
		this.bancos=bancos;
		this.id_tarjeta=id_tarjeta;
		this.id_banco=id_banco;
		this.id_banco_tarjeta=id_banco_tarjeta;
	}

	public double getEfectivoneto() {
		return efectivoneto;
	}

	public double getCambio() {
		return cambio;
	}

	public double getTarjetas() {
		return tarjetas;
	}

	public double getBancos() {
		return bancos;
	}

	public String getId_tarjeta() {
		return id_tarjeta;
	}

	public String getId_banco() {
		return id_banco;
	}

	public String getId_banco_tarjeta() {
		return id_banco_tarjeta;
	}
	
	public int getCantidad() {
		return cantidad;
	}

	public String getValue() {
		return value;
	}
	
	public long getIdobject() {
		return b;
	}

	public String getId() {
		return id;
	}

	public Element getRetenciones() {
		return retenciones;
	}

	public double getValor() {
		return valor;
	}
	
	public double getTretenciones() {
		return tretenciones;
	}
	
}
