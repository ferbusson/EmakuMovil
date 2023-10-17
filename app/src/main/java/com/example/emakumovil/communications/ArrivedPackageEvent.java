package com.example.emakumovil.communications;

import java.util.EventObject;

import org.jdom2.Document;

public class ArrivedPackageEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7423353291901251301L;
	private Document doc;

	public ArrivedPackageEvent(Object source,Document doc) {
		super(source);
		this.doc=doc;
	}

	public org.jdom2.Document getDoc() {
		return doc;
	}

}
