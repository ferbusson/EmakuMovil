package com.example.emakumovil.components;

public class fieldsPhotoTextValue {

	private int photo;
	private String text;
	private String value;
	private String codigo_tipo;
	
	public fieldsPhotoTextValue(int photo,String codigo_tipo,String text,String value) {
		this.photo=photo;
		this.codigo_tipo=codigo_tipo;
		this.text=text;
		this.value=value;
	}

	public String getCodigo_tipo() {
		return codigo_tipo;
	}

	public int getPhoto() {
		return photo;
	}

	public String getText() {
		return text;
	}

	public String getValue() {
		return value;
	}


}
