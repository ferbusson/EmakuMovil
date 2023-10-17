package com.example.emakumovil.modules.inventario;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.FieldsDDV;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;
import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.control.SuccessEvent;
import com.example.emakumovil.control.SuccessListener;

public class ScanningBarcodeActivity extends Activity implements OnKeyListener, AnswerListener, DialogClickListener, OnClickListener, SuccessListener {

	private EditText et_barcode;
	private ListView lv_listbarras;
	private TextView tv_total;
	private TextView tv_titulo;
	private ImageButton im_guardar;
	private ImageButton im_limpiar;
	DataAdapter adapter;
	private double total;
	private int selectedItemClick;
	private WakeLock wl;
	DecimalFormat df = new DecimalFormat("##,###,###");
	private static ModifyingQuantityDialog modifyCant;
	private final int SCREEN_DIM_WAKE_LOCK = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scanning_barcode);
		ClientHeaderValidator.addSuccessListener(this);
		et_barcode = (EditText)findViewById(R.id.et_barcode);
		im_guardar = (ImageButton)findViewById(R.id.ib_guardar);
		im_guardar.setOnClickListener(this);
		im_limpiar = (ImageButton)findViewById(R.id.ib_limpiar);
		im_limpiar.setOnClickListener(this);
		
		//et_barcode.addTextChangedListener(this);
		et_barcode.setOnKeyListener(this);
		tv_total = (TextView) findViewById(R.id.tv_total);
		lv_listbarras = (ListView)findViewById(R.id.lv_listbarras);
		tv_titulo = (TextView)findViewById(R.id.tv_document);
		adapter = new DataAdapter(
				ScanningBarcodeActivity.this, R.layout.infofototextovalor,
				GlobalData.itemsbc);
		lv_listbarras.setAdapter(adapter);
		if (savedInstanceState!=null) {
			adapter.notifyDataSetChanged();
			totalizar();
		}
		modifyCant = new ModifyingQuantityDialog();
		modifyCant.addDialogClickListener(this);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wl = pm.newWakeLock(SCREEN_DIM_WAKE_LOCK, "Alarm");
		wl.acquire();
	}

	@Override
	public void onResume() {
		super.onResume();
		wl.release();
	}
	
	public void onStop() {
		super.onStop();
		System.out.println("apagando");
		
		 if (wl != null) {
	            System.out.println("Releasing wakelock");
	            try {
	                wl.release();
	            } catch (Throwable th) {
	            	System.out.println("Exception "+th.getMessage());
	            }
	        } else {
	        	System.out.println("Wakelock reference is null");
	        }
	}
	
	private void totalizar() {
		total=0;
		System.out.println("numero de items: "+GlobalData.itemsbc.size());
		for (int i=0;i<GlobalData.itemsbc.size();i++) {
			FieldsDDV v = GlobalData.itemsbc.get(i);
			total+= (int)v.getDvalor();
			System.out.println("totalizando: "+total+" total sin parcear "+v.getDvalor());
		}
		tv_total.setText(df.format(total));

	}
	


	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego query ");
		Document doc = e.getDocument();
		final Element rootNode = doc.getRootElement();
		this.runOnUiThread(new Runnable() {
			public void run() {
				et_barcode.setText("");
				et_barcode.setEnabled(true);
				List<Element> listRows = rootNode.getChildren("row");
				if (listRows.size()>0) {
					for (int i = 0; i < listRows.size(); i++) {
						Element Erow = (Element) listRows.get(i);
						List<Element> Lcol = Erow.getChildren();
						String descripcion1 = ((Element) Lcol.get(0)).getText();
						String descripcion2 = ((Element) Lcol.get(1)).getText();
						double dvalor = Double.parseDouble(((Element) Lcol.get(2)).getText());
						String valor = df.format(dvalor);
						GlobalData.itemsbc.add(new FieldsDDV(descripcion1,descripcion2,valor,dvalor));
					}
					total++;
					adapter.notifyDataSetChanged();
					lv_listbarras.post(new Runnable(){
						  public void run() {
						    lv_listbarras.setSelection(lv_listbarras.getCount() - 1);
						  }});
					registerForContextMenu(lv_listbarras);
					tv_total.setText(df.format(total));
				}
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
	  super.onCreateContextMenu(menu, v, menuInfo);
	  getMenuInflater().inflate(R.menu.contextmenubarcode,menu);
	  menu.setHeaderTitle(R.string.registro_de_barra);
	  AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
	  selectedItemClick = info.position;
	  
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	 /*
	    switch (item.getItemId()) {
	        case R.id.it_modificar_cantidad:
	        	modificarCantidad(selectedItemClick);
	            return true;
	        case R.id.it_eliminar_registro:
	        	eliminarRegisro(selectedItemClick);
	            return true;
	        default:
	        	return false;
	    }*/
		return false;
	}

	private void modificarCantidad(int position) {
    	modifyCant.show(getFragmentManager(),getString(R.string.modificar_cantidad));
	}
	
	private void eliminarRegisro(int position) {
		GlobalData.itemsbc.remove(position);
		adapter.notifyDataSetChanged();
		totalizar();
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
	   // System.out.println("key pressed "+String.valueOf(event.getKeyCode()));
	    return super.dispatchKeyEvent(event);
	}
	
	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}

	private class DataAdapter extends ArrayAdapter<FieldsDDV> {

		private LayoutInflater vi;

		public DataAdapter(Context context, int textViewResourceId,
				ArrayList<FieldsDDV> items) {
			super(context, textViewResourceId, items);
			GlobalData.itemsbc = items;
			Log.d("EMAKU ","EMAKU instanciando adaptador");
			vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			ViewHolder holder;
			
			if (v == null) {
				v = vi.inflate(R.layout.listicondatadatavalue, null);
				TextView tv_descripcion1 = (TextView) v
						.findViewById(R.id.tv_descripcion1);
				TextView tv_descripcion2 = (TextView) v
						.findViewById(R.id.tv_descripcion2);
				TextView tv_valor = (TextView) v
						.findViewById(R.id.tv_valor);
				holder = new ViewHolder(tv_descripcion1,tv_descripcion2,tv_valor);
				v.setTag(holder);
			}
			else {
				holder = (ViewHolder)v.getTag();
			}

			
			FieldsDDV myData = GlobalData.itemsbc.get(position);
			if (myData != null) {
				if (holder.tv_descripcion1 != null) {
					holder.tv_descripcion1.setText(myData.getDescripcion1());
				}
				if (holder.tv_descripcion2 != null) {
					holder.tv_descripcion2.setText(myData.getDescripcion2());
				}
				if (holder.tv_valor != null) {
					holder.tv_valor.setText(myData.getValor());
				}
			}
			return v;
		}

	}
	
	private class ViewHolder {
 		public TextView tv_descripcion1;
 		public TextView tv_descripcion2;
 		public TextView tv_valor;
 		
 		public ViewHolder(TextView tv_descripcion1,TextView tv_descripcion2,TextView tv_valor) {
 			this.tv_descripcion1=tv_descripcion1;
 			this.tv_descripcion2=tv_descripcion2;
 			this.tv_valor=tv_valor;
 		}
 		
 	}

	
	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		FieldsDDV f = GlobalData.itemsbc.get(selectedItemClick);
		f.setDvalor(e.getCantidad());
		f.setValor(df.format(e.getCantidad()));
		adapter.notifyDataSetChanged();
		totalizar();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId()==R.id.ib_limpiar) {
			im_guardar.setEnabled(true);
			tv_titulo.setText(R.string.barras_escaneadas);
			GlobalData.itemsbc.clear();
			adapter.notifyDataSetChanged();
			totalizar();
		}
		else if (v.getId()==R.id.ib_guardar) {
			sendTransaction();
		}
	}


	private void sendTransaction() {
		im_guardar.setEnabled(false);
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00002");
		Element id = new Element("id");
		id.setText("TMV4");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);

		// Obs..
		Element obs = new Element("package");
		raiz.addContent(obs);
		
		// Tabla
		
		Element tabla = getElementBarras();
		raiz.addContent(tabla);

		XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
        try {
			out.output(raiz,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

         SocketChannel socket = SocketConnector.getSock();
        Log.d("EMAKU","EMAKU: Socket: "+socket);
        SocketWriter.writing(socket, transaction);

	}
	
	private Element getElementBarras() {
		Element pack = new Element("package");
		for (int i=0;i<GlobalData.itemsbc.size();i++) {
			FieldsDDV tabla = GlobalData.itemsbc.get(i);
			double cantidad = tabla.getDvalor();
			if (cantidad>0) {
				Element spack = new Element("subpackage");
				Element barra = new Element("field");
				Element cant = new Element("field");
				
				barra.setText(tabla.getDescripcion1());
				cant.setText(String.valueOf((int)tabla.getDvalor()));
				
				spack.addContent(barra);
				spack.addContent(cant);
				pack.addContent(spack);
			}
			else {
				break;
			}
		}
		return pack;
	}

	@Override
	public void cathSuccesEvent(SuccessEvent e) {
		// TODO Auto-generated method stub
		final String numero = e.getNdocument();
		this.runOnUiThread(new Runnable() {
			public void run() {
				System.out.println("recolector guardado: "+getString(R.string.recolector_guardado)+" "+numero);
				tv_titulo.setText(getString(R.string.recolector_guardado)+" "+numero);
			}
		});
	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		 if (v instanceof EditText && keyCode == KeyEvent.KEYCODE_ENTER) {
				et_barcode.setEnabled(false);
				System.out.println("consultando.. "+et_barcode.getText());
				new SearchQuery(ScanningBarcodeActivity.this,"MVSEL0024", new String[] {
						et_barcode.getText().toString().trim()}).start();

			 return true;
         }
		return false;
	}
}
