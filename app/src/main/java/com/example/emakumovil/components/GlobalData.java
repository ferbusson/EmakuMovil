package com.example.emakumovil.components;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.widget.ListView;

import com.example.emakumovil.modules.cartera.ListCarteraActivity.FieldsCartera;
import com.example.emakumovil.modules.compras.PurchasingActivity.TempoXLS;
import com.example.emakumovil.modules.compras.PurchasingInformationDialog.Retenciones;


public class GlobalData {

	
	public static Bitmap[] image = new Bitmap[5];
	
	private static ListView LVinfodocs;
	private static ListView LVinforec;
	private static ListView LVlistdocs;
	private static ListView LVinforecu;
	private static ListView LVlistdocsu;
	private static ListView LVlistdia;
	private static ListView LVlistrecdia;
	
	private static ArrayList<fieldsPhotoTextValue> myDLinfodocs = new ArrayList<fieldsPhotoTextValue>();
	private static ArrayList<fieldsPhotoTextValue> myDLinforec = new ArrayList<fieldsPhotoTextValue>();
	private static ArrayList<fieldsPhotoTextValue> myDLlistdocs = new ArrayList<fieldsPhotoTextValue>();
	private static ArrayList<fieldsPhotoTextValue> myDLinfodia = new ArrayList<fieldsPhotoTextValue>();
	private static ArrayList<fieldsPhotoTextValue> myDLinforecdia = new ArrayList<fieldsPhotoTextValue>();
	private static ArrayList<fieldsPhotoTextValue> myDLinfodocsu = new ArrayList<fieldsPhotoTextValue>();
	private static ArrayList<fieldsPhotoTextValue> myDLinforecu = new ArrayList<fieldsPhotoTextValue>();
	
	public static ArrayList<FieldsCartera> items = new ArrayList<FieldsCartera>();
	public static ArrayList<FieldsDDV> itemsbc = new ArrayList<FieldsDDV>();
	public static ArrayList<FieldsDDV> items_contab = new ArrayList<FieldsDDV>();
	public static ArrayList<FieldsDDV> items_prod = new ArrayList<FieldsDDV>();
	public static ArrayList<FieldsDDV> items_documentv = new ArrayList<FieldsDDV>();
	public static ArrayList<FieldsDDV> items_documentc = new ArrayList<FieldsDDV>();
	public static ArrayList<FieldsKardex> items_kardex = new ArrayList<FieldsKardex>();
	public static ArrayList<FieldsDDV> items_sprod = new ArrayList<FieldsDDV>();
	public static ArrayList<fieldsAQ> items_aq = new ArrayList<fieldsAQ>();
	public static ArrayList<TempoXLS> xls = new ArrayList<TempoXLS>();
	public static ArrayList<Retenciones> data_retenciones = new ArrayList<Retenciones>();
	
	public static DialogClickListener dialogClickListener;


	public static ArrayList<fieldsPhotoTextValue> getMyDLinfodocs() {
		return GlobalData.myDLinfodocs;
	}

	public static ArrayList<fieldsPhotoTextValue> getMyDLinforec() {
		return GlobalData.myDLinforec;
	}

	public static ArrayList<fieldsPhotoTextValue> getMyDLinfodocsu() {
		return GlobalData.myDLinfodocsu;
	}

	public static ArrayList<fieldsPhotoTextValue> getMyDLinforecu() {
		return GlobalData.myDLinforecu;
	}

	public static ArrayList<fieldsPhotoTextValue> getMyDLlistdocs() {
		return GlobalData.myDLlistdocs;
	}

	public static ArrayList<fieldsPhotoTextValue> getMyDLinfodia() {
		return GlobalData.myDLinfodia;
	}

	public static ArrayList<fieldsPhotoTextValue> getMyDLinforecdia() {
		return GlobalData.myDLinforecdia;
	}


	public static void setLVlistrecdia(ListView LVlistrecdia) {
		GlobalData.LVlistrecdia=LVlistrecdia;
	}

	public static void setLVlistdia(ListView LVlistdia) {
		GlobalData.LVlistdia=LVlistdia;
	}
	
	public static void setLVinfodocs(ListView LVinfodocs) {
		GlobalData.LVinfodocs=LVinfodocs;
	}

	public static void setLVlistdocs(ListView LVlistdocs) {
		GlobalData.LVlistdocs=LVlistdocs;
	}

	public static void setLVinforec(ListView LVinforec) {
		GlobalData.LVinforec=LVinforec;
	}

	public static void setLVlistdocsu(ListView LVinfodocsu) {
		GlobalData.LVlistdocsu=LVinfodocsu;
	}

	public static void setLVinforecu(ListView LVinforecu) {
		GlobalData.LVinforecu=LVinforecu;
	}

	public static ListView getLVlistdia() {
		return GlobalData.LVlistdia;
	}

	public static ListView getLVlistrecdia() {
		return GlobalData.LVlistrecdia;
	}

	public static ListView getLVinforec() {
		return GlobalData.LVinforec;
	}

	public static ListView getLVinfodocs() {
		return GlobalData.LVinfodocs;
	}

	public static ListView getLVinforecu() {
		return GlobalData.LVinforecu;
	}

	public static ListView getLVlistdocsu() {
		return GlobalData.LVlistdocsu;
	}

	public static ListView getLVlistdocs() {
		return GlobalData.LVlistdocs;
	}

}
