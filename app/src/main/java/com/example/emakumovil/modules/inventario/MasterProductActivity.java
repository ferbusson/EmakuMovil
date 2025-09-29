package com.example.emakumovil.modules.inventario;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;


import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.components.FieldsKardex;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.misc.utils.ZipHandler;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.jdom2.Document;
import org.jdom2.Element;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MasterProductActivity extends AppCompatActivity implements TextWatcher, OnClickListener,
		AnswerListener, OnTouchListener,SearchProdDialogFrame.OnProductSelectedListener {

	private TextView tv_descripcion;
	private TextView tv_ucompra;
	private TextView tv_promedio;
	private TextView tv_pventa1;
	private TextView tv_pventa2;
	private TextView tv_seccion;
	private TextView tv_grupo;
	private TextView tv_sgrupo;
	
	private EditText et_barcode;

	private ImageButton ib_limpiar;
	private ImageButton ib_buscar;
	private ImageButton ib_camera;

	private ImageView[] image = new ImageView[5];

	private ListView lv_kardex;
	private ListView lv_compras;
	private ListView lv_ventas;
	
	private TabHost tabs;

	private DataAdapterKardex adapter_kardex;
	private DataAdapterDocuments adapter_ventas;
	private DataAdapterDocuments adapter_compras;
	
	private SearchProdDialogFrame sprod;
	private ViewFlipper flipper;
	private float fromPosition;
	
	private DecimalFormat df = new DecimalFormat("##,###,##0");

	private ActivityResultLauncher<ScanOptions> barcodeLauncher;
	//
	// You MUST provide a public, no-argument constructor
	public MasterProductActivity() {
	}



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_product);
		
		et_barcode = (EditText)findViewById(R.id.et_barcode);
		et_barcode.addTextChangedListener(this);

		et_barcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// Check if the action is the "Done", "Search", or "Enter" key
				if (actionId == EditorInfo.IME_ACTION_SEARCH ||
						actionId == EditorInfo.IME_ACTION_DONE ||
						(event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {

					// Get the text from the EditText
					String barcode = et_barcode.getText().toString();

					// Only search if the field is not empty
					if (!barcode.isEmpty()) {
						// Trigger the same search routine
						searchMaster(barcode);

						// Hide the keyboard
						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(et_barcode.getWindowToken(), 0);

						return true; // We handled the event
					}
				}
				return false; // We did not handle the event
			}
		});


		tv_descripcion = (TextView)findViewById(R.id.tv_descripcion);
		tv_ucompra = (TextView)findViewById(R.id.tv_ucompra);
		tv_promedio = (TextView)findViewById(R.id.tv_cpromedio);
		tv_pventa1 = (TextView)findViewById(R.id.tv_pventa1);
		tv_pventa2 = (TextView)findViewById(R.id.tv_pventa2);
		tv_seccion = (TextView)findViewById(R.id.tv_seccion);
		tv_grupo = (TextView)findViewById(R.id.tv_grupo);
		tv_sgrupo = (TextView)findViewById(R.id.tv_sgrupo);
		
		lv_kardex = (ListView)findViewById(R.id.lv_kardex);
		lv_compras = (ListView)findViewById(R.id.lv_compras);
		lv_ventas = (ListView)findViewById(R.id.lv_ventas);
		
		ib_limpiar = (ImageButton)findViewById(R.id.ib_limpiar);
		ib_limpiar.setOnClickListener(this);
		ib_buscar = (ImageButton)findViewById(R.id.ib_buscar);
		ib_buscar.setOnClickListener(this);
		ib_camera = (ImageButton)findViewById(R.id.ib_camera);
		
		
		ib_camera.setOnClickListener(this);
		
		tabs=(TabHost)findViewById(android.R.id.tabhost);
		tabs.setup();
		Resources res = getResources();
		
		// Pestaña 1 
		
		TabHost.TabSpec spec1=tabs.newTabSpec(getString(R.string.imagen));
		spec1.setContent(R.id.ly_imagen);
		spec1.setIndicator(getString(R.string.imagen),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec1);

		// Pestaña 2 
		
		TabHost.TabSpec spec2=tabs.newTabSpec(getString(R.string.kardex));
		spec2.setContent(R.id.ly_kardex);
		spec2.setIndicator(getString(R.string.kardex),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec2);

		// Pestaña 3 
		
		TabHost.TabSpec spec3=tabs.newTabSpec(getString(R.string.ventas));
		spec3.setContent(R.id.ly_ventas);
		spec3.setIndicator(getString(R.string.ventas),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec3);

		// Pestaña 4
		
		TabHost.TabSpec spec4=tabs.newTabSpec(getString(R.string.compras));
		spec4.setContent(R.id.ly_compras);
		spec4.setIndicator(getString(R.string.compras),
		    res.getDrawable(android.R.drawable.ic_dialog_alert));
		tabs.addTab(spec4);

		for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
			// Get the view for each tab
			View tabView = tabs.getTabWidget().getChildAt(i);

			// The title is inside a TextView. Find it by its default Android ID.
			TextView tv = (TextView) tabView.findViewById(android.R.id.title);

			// Set the text color to white
			if (tv != null) {
				tv.setTextColor(getResources().getColor(android.R.color.white));
			}
		}

		adapter_kardex = new DataAdapterKardex(
				MasterProductActivity.this, R.layout.inventoryrecorddata,
				GlobalData.items_kardex);
		lv_kardex.setAdapter(adapter_kardex);
		
		adapter_ventas = new DataAdapterDocuments(
				MasterProductActivity.this, R.layout.documentrecorddata,
				GlobalData.items_documentv);
		lv_ventas.setAdapter(adapter_ventas);
		
		adapter_compras = new DataAdapterDocuments(
				MasterProductActivity.this, R.layout.documentrecorddata,
				GlobalData.items_documentc);
		lv_compras.setAdapter(adapter_compras);
		
		
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.ly_imagen);
		mainLayout.setOnTouchListener(this);

		flipper = (ViewFlipper)findViewById(R.id.flipper);
		
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		int layouts[] = new int[]{R.layout.layout_image1,
									R.layout.layout_image2,
									R.layout.layout_image3,
									R.layout.layout_image4,
									R.layout.layout_image5};
		
		for (int i=0;i<layouts.length;i++) {
			View v = inflater.inflate(layouts[i], null);
			if (flipper!=null) {
				flipper.addView(v);
			}
			int id = 0;
			if (i==0)
				id = R.id.foto0;
			if (i==1)
				id = R.id.foto1;
			if (i==2)
				id = R.id.foto2;
			if (i==3)
				id = R.id.foto3;
			if (i==4)
				id = R.id.foto4;
			System.out.println("cargando imagen "+i);
			image[i] = (ImageView)v.findViewById(id);
			image[i].setOnClickListener(this);
		}

		barcodeLauncher = registerForActivityResult(new ScanContract(), result -> {
			if (result.getContents() != null) {
				// Barcode was successfully scanned
				String barcode = result.getContents();
				Log.d("MasterProductActivity", "Barcode Scanned: " + barcode);
				// Call your existing searchMaster method to process the result
				searchMaster(barcode);
			} else {
				// Scan was cancelled by the user
				Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show();
			}
		});

		ib_camera.setOnClickListener(this);

	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId()==R.id.ib_limpiar) {
			clean();
		}
		else if (v.getId()==R.id.ib_camera) {
			getCamBarcode();
		}
		else if (v.getId()==R.id.ib_buscar) {
			System.out.println("buscando...");
			SearchProdDialogFrame dialog = SearchProdDialogFrame.newInstance(
					getString(R.string.buscar_producto), //Titulo del componente que llama la busqueda
					"MVSEL0033"); // query que el componente debe ejecutar
			// The Activity is the listener, so the dialog will call onProductSelected() automatically
			// No need for an anonymous class here

			// Use getSupportFragmentManager() for AndroidX DialogFragment
			dialog.show(getSupportFragmentManager(), "SearchProdDialog");
		}
		
	}

	private void searchMaster(final String barcode) {
		this.runOnUiThread(new Runnable() {
	        public void run() {
	    		et_barcode.setText(barcode);
				new SearchQuery(MasterProductActivity.this,"MVSEL0028",new String[]{barcode}).start(); 
				new SearchQuery(MasterProductActivity.this,"MVSEL0038",new String[]{barcode}).start(); 
				new SearchQuery(MasterProductActivity.this,"MVSEL0030",new String[]{barcode}).start(); 
				new SearchQuery(MasterProductActivity.this,"MVSEL0031",new String[]{barcode}).start(); 
				new SearchQuery(MasterProductActivity.this,"MVSEL0032",new String[]{barcode}).start(); 
			}
		});
	}
	
	private void getCamBarcode() {
		ScanOptions options = new ScanOptions();
		options.setPrompt("Scan a barcode");
		options.setBeepEnabled(true);
		options.setOrientationLocked(false);
		// Set the desired barcode formats (optional)
		// options.setDesiredBarcodeFormats(ScanOptions.PRODUCT_CODE_TYPES);

		barcodeLauncher.launch(options);

	}
	/* Esta seccion pertenecia al viejo codigo de lectura de barcode desde la camara
	 public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("EMAKU"," ResultCode: "+resultCode+" requestCode: "+requestCode);
		if (requestCode == 0) {
		    if (resultCode == RESULT_OK) {
		        String contents = intent.getStringExtra("SCAN_RESULT");
		        // Handle successful scan
		        et_barcode.setText(contents);
				// info general del producto
				new SearchQuery(this,"MVSEL0028",new String[]{contents}).start();
				// foto
				new SearchQuery(this,"MVSEL0038",new String[]{contents}).start();
				// ventas
				new SearchQuery(this,"MVSEL0030",new String[]{contents}).start();
				// compras
				new SearchQuery(this,"MVSEL0031",new String[]{contents}).start();
				// kardex
				new SearchQuery(this,"MVSEL0032",new String[]{contents}).start(); 
		    } else if (resultCode == RESULT_CANCELED) {
		        // Handle cancel
		        Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
		        toast.setGravity(Gravity.TOP, 25, 400);
		        toast.show();
		    }
		}
   } */

	 @Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		final Element elm = doc.getRootElement();
		if (e.getSqlCode().equals("MVSEL0028")) {
			Iterator<Element> i = elm.getChildren("row").iterator();
			while (i.hasNext()) {
				System.out.println("iterator con datos");
				Element row = (Element) i.next();
				Iterator<Element> j = row.getChildren().iterator();
				final String descripcion = ((Element) j.next()).getValue();
				final double ucompra = Double.parseDouble(((Element) j.next()).getValue());
				final double cpromedio = Double.parseDouble(((Element) j.next()).getValue());
				final double pventa1 = Double.parseDouble(((Element) j.next()).getValue());
				final double pventa2 = Double.parseDouble(((Element) j.next()).getValue());
				final String seccion = ((Element) j.next()).getValue();
				final String grupo = ((Element) j.next()).getValue();
				final String sgrupo = ((Element) j.next()).getValue();
				
		        this.runOnUiThread(new Runnable() {
		            public void run() {
		            	tv_descripcion.setText(getString(R.string.descripcion)+" "+descripcion);
						tv_ucompra.setText(getString(R.string.vucompra)+" "+df.format(ucompra));
						tv_promedio.setText(getString(R.string.cpromedio)+" "+df.format(cpromedio));
						tv_pventa1.setText(getString(R.string.pventa1)+" "+df.format(pventa1));
						tv_pventa2.setText(getString(R.string.pventa2)+" "+df.format(pventa2));
						tv_seccion.setText(getString(R.string.seccion)+" "+seccion);
						tv_grupo.setText(getString(R.string.grupo)+" "+grupo);
						tv_sgrupo.setText(getString(R.string.sgrupo)+" "+sgrupo);
		            }
		        });
			}
		}
		else if (e.getSqlCode().equals("MVSEL0038")) {
			final List<Element> listRows = doc.getRootElement().getChildren("row");
			this.runOnUiThread(new Runnable() {
	            public void run() {
	    			try {
						System.out.println("numero de fotos: "+listRows);
						for (int i = 0; i < listRows.size(); i++) {
							System.out.println("cargando foto: "+i);
							Element Erow = (Element) listRows.get(i);
							List<Element> Lcol = Erow.getChildren();
							String imgValue = ((Element) Lcol.get(0)).getText();
							final ZipHandler zip = new ZipHandler();
							byte[] bytesImage;
							bytesImage = zip.getDataDecode(imgValue);
							GlobalData.image[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytesImage,0,bytesImage.length),1024, 768, true);
							image[i].setImageBitmap(GlobalData.image[i]);
						}
	    			}
					catch(NullPointerException NPEe) {
						NPEe.printStackTrace();
					}
					catch(IOException IOEe) {
						IOEe.printStackTrace();
					}
				}
            });
		}
		else if (e.getSqlCode().equals("MVSEL0030")) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = elm.getChildren("row");
					GlobalData.items_documentv.clear();
					System.out.println("cargando ventas");
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						int cantidad = Integer
								.parseInt(((Element) Lcol.get(2)).getText());
						double valor = Double
								.parseDouble(((Element) Lcol.get(3)).getText());
						GlobalData.items_documentv.add(new FieldsDDV(descripcion1,descripcion2,cantidad,valor,0.0));
					}
					
					adapter_ventas.notifyDataSetChanged();
				}
			});
		
		}
		else if (e.getSqlCode().equals("MVSEL0031")) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = elm.getChildren("row");
					GlobalData.items_documentc.clear();
					System.out.println("cargando compras");
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						int cantidad = Integer
								.parseInt(((Element) Lcol.get(2)).getText());
						double valor = Double
								.parseDouble(((Element) Lcol.get(3)).getText());
						GlobalData.items_documentc.add(new FieldsDDV(descripcion1,descripcion2,cantidad,valor,0.0));
					}
					adapter_compras.notifyDataSetChanged();
					
				}
			});
		
		}
		else if (e.getSqlCode().equals("MVSEL0032")) {
			this.runOnUiThread(new Runnable() {
				public void run() {
					List<Element> listRows = elm.getChildren("row");
					GlobalData.items_kardex.clear();
					System.out.println("cargando kardex");
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion = ((Element) Lcol.get(0)).getText();
						int entrada = Integer
								.parseInt(((Element) Lcol.get(1)).getText());
						int salida = Integer
								.parseInt(((Element) Lcol.get(2)).getText());
						int saldo = Integer
								.parseInt(((Element) Lcol.get(3)).getText());
						double ventrada = Double
								.parseDouble(((Element) Lcol.get(4)).getText());
						double vsalida = Double
								.parseDouble(((Element) Lcol.get(5)).getText());
						double vsaldo = Double
								.parseDouble(((Element) Lcol.get(6)).getText());
						GlobalData.items_kardex.add(new FieldsKardex(descripcion,entrada,salida,saldo,ventrada,vsalida,vsaldo));
					}
					adapter_kardex.notifyDataSetChanged();
				}
			});
		
		}
	}

	@Override
	public void onProductSelected(String barcode) {
		// This method is called when a product is selected in the dialog.
		// The 'barcode' parameter contains the value from the first TextView of the selected item.
		Log.d("MasterProductActivity", "Product selected with barcode: " + barcode);

		// Now, call your existing searchMaster function with the received barcode.
		searchMaster(barcode);
	}

	private class DataAdapterKardex extends ArrayAdapter<FieldsKardex> {

			private LayoutInflater vi;

			public DataAdapterKardex(Context context, int textViewResourceId,
					ArrayList<FieldsKardex> items) {
				super(context, textViewResourceId, items);
				GlobalData.items_kardex = items;
				Log.d("EMAKU ","EMAKU instanciando adaptador");
				vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = convertView;
				ViewHolderKardex holder;
				
				if (v == null) {
					v = vi.inflate(R.layout.inventoryrecorddata, null);
					TextView tv_descripcion = (TextView) v
							.findViewById(R.id.tv_descripcion);
					TextView tv_entrada = (TextView) v
							.findViewById(R.id.tv_entrada);
					TextView tv_salida = (TextView) v
							.findViewById(R.id.tv_salida);
					TextView tv_saldo = (TextView) v
							.findViewById(R.id.tv_saldo);
					TextView tv_ventrada = (TextView) v
							.findViewById(R.id.tv_ventrada);
					TextView tv_vsalida = (TextView) v
							.findViewById(R.id.tv_vsalida);
					TextView tv_vsaldo = (TextView) v
							.findViewById(R.id.tv_vsaldo);
					
					holder = new ViewHolderKardex(tv_descripcion,
							tv_entrada,
							tv_salida,
							tv_saldo,
							tv_ventrada,
							tv_vsalida,
							tv_vsaldo);
					
					v.setTag(holder);
				}
				else {
					holder = (ViewHolderKardex)v.getTag();
				}

				
				FieldsKardex myData = GlobalData.items_kardex.get(position);
				if (myData != null) {
					if (holder.tv_descripcion != null) {
						holder.tv_descripcion.setText(myData.getDescripcion());
					}
					if (holder.tv_entrada != null) {
						holder.tv_entrada.setText(df.format(myData.getEntrada()));
					}
					if (holder.tv_salida != null) {
						holder.tv_salida.setText(df.format(myData.getSalida()));
					}
					if (holder.tv_saldo != null) {
						holder.tv_saldo.setText(df.format(myData.getSaldo()));
					}
					if (holder.tv_ventrada != null) {
						holder.tv_ventrada.setText(df.format(myData.getVentrada()));
					}
					if (holder.tv_vsalida != null) {
						holder.tv_vsalida.setText(df.format(myData.getVsalida()));
					}
					if (holder.tv_vsaldo != null) {
						holder.tv_vsaldo.setText(df.format(myData.getVsaldo()));
					}
				}
				return v;
			}

		}


		private class ViewHolderKardex {
	 		public TextView tv_descripcion;
	 		public TextView tv_entrada;
	 		public TextView tv_salida;
	 		public TextView tv_saldo;
	 		public TextView tv_ventrada;
	 		public TextView tv_vsalida;
	 		public TextView tv_vsaldo;
	 		
	 		public ViewHolderKardex(TextView tv_descripcion,TextView tv_entrada,TextView tv_salida,TextView tv_saldo,
	 				TextView tv_ventrada,TextView tv_vsalida,TextView tv_vsaldo) {
	 			this.tv_descripcion=tv_descripcion;
	 			this.tv_entrada=tv_entrada;
	 			this.tv_salida=tv_salida;
	 			this.tv_saldo=tv_saldo;
	 			this.tv_ventrada=tv_ventrada;
	 			this.tv_vsalida=tv_vsalida;
	 			this.tv_vsaldo=tv_vsaldo;
	 		}
	 		
	 	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	private void clean() {
		tv_descripcion.setText(getString(R.string.descripcion));
		tv_ucompra.setText(getString(R.string.vucompra));
		tv_promedio.setText(getString(R.string.cpromedio));
		tv_pventa1.setText(getString(R.string.pventa1));
		tv_pventa2.setText(getString(R.string.pventa2));
		tv_seccion.setText(getString(R.string.seccion));
		tv_grupo.setText(getString(R.string.grupo));
		tv_sgrupo.setText(getString(R.string.sgrupo));
		GlobalData.items_documentc.clear();
		GlobalData.items_documentv.clear();
		GlobalData.items_kardex.clear();
		adapter_ventas.notifyDataSetChanged();
		adapter_compras.notifyDataSetChanged();
		adapter_kardex.notifyDataSetInvalidated();
		
		et_barcode.setText("");
		et_barcode.requestFocus();
	}
	
	private class DataAdapterDocuments extends ArrayAdapter<FieldsDDV> {

		private LayoutInflater vi;
		private ArrayList<FieldsDDV> items;
		public DataAdapterDocuments(Context context, int textViewResourceId,
				ArrayList<FieldsDDV> items) {
			super(context, textViewResourceId, items);
			this.items = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			
			FieldsDDV myData = items.get(position);
			if (myData!=null) {
				v = vi.inflate(R.layout.documentrecorddata, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				TextView tv_cantidad = (TextView) v
						.findViewById(R.id.tv_cantidad);
				TextView tv_valor = (TextView) v
						.findViewById(R.id.tv_valor);
				
				tv_descripcion1.setText(myData.getDescripcion1());
				tv_descripcion2.setText(myData.getDescripcion2());
				tv_cantidad.setText(df.format(myData.getCantidad()));
				tv_valor.setText(df.format(myData.getPventa()));
			}

			return v;

		}

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		System.out.println("consultando.. "+et_barcode.getText());
		if (s.toString().contains("\n")) {
			System.out.println("tiene un enter");
			new SearchQuery(this,"MVSEL0028",new String[]{et_barcode.getText().toString().trim()}).start(); 
			new SearchQuery(this,"MVSEL0038",new String[]{et_barcode.getText().toString().trim()}).start(); 
			new SearchQuery(this,"MVSEL0030",new String[]{et_barcode.getText().toString().trim()}).start(); 
			new SearchQuery(this,"MVSEL0031",new String[]{et_barcode.getText().toString().trim()}).start(); 
			new SearchQuery(this,"MVSEL0032",new String[]{et_barcode.getText().toString().trim()}).start(); 
		}
		
	}


	@Override
	public boolean onTouch(View v, MotionEvent event) {
		 switch (event.getAction())
		    {
		    case MotionEvent.ACTION_DOWN:
		        fromPosition = event.getX();
		        break;
		    case MotionEvent.ACTION_UP: 
		        float toPosition = event.getX();
		        if (fromPosition > toPosition)
		        {
		            flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.entra_derecha));
		            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.sale_izquierda));
		            flipper.showNext();
		        }
		        else if (fromPosition < toPosition)
		        {
		            flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.entra_izquierda));
		            flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.sale_derecha));
		            flipper.showPrevious();
		        }		    default:
		        break;
		    }
		    return true;
	}
}
