package com.example.emakumovil.components;

public class FieldsKardex {
		
		private String descripcion;
		private int entrada;
		private int salida;
		private int saldo;
		private double ventrada;
		private double vsalida;
		private double vsaldo;

		public FieldsKardex(String descripcion,int entrada,int salida,int saldo,double ventrada,double vsalida,double vsaldo) {
			this.descripcion=descripcion;
			this.entrada=entrada;
			this.salida=salida;
			this.saldo=saldo;
			this.ventrada=ventrada;
			this.vsalida=vsalida;
			this.vsaldo=vsaldo;
		}

		public String getDescripcion() {
			return descripcion;
		}

		public int getEntrada() {
			return entrada;
		}

		public int getSalida() {
			return salida;
		}

		public int getSaldo() {
			return saldo;
		}

		public double getVentrada() {
			return ventrada;
		}

		public double getVsalida() {
			return vsalida;
		}

		public double getVsaldo() {
			return vsaldo;
		}

		
	}
