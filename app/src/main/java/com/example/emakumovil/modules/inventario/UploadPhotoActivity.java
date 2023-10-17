package com.example.emakumovil.modules.inventario;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.List;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug.MemoryInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.GlobalData;
import com.example.emakumovil.components.SelectedDataDialog;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.communications.SocketWriter;
import com.example.emakumovil.misc.utils.ZipHandler;
import com.example.emakumovil.transactions.TransactionServerException;
import com.example.emakumovil.transactions.TransactionServerResultSet;

public class UploadPhotoActivity extends Activity implements OnClickListener, AnswerListener,TextWatcher, OnTouchListener,DialogClickListener {

	private EditText barcode;
	private TextView descripcion;
	private TextView marca;
	private ImageButton Bbarcode;
	private ImageButton Blimpiar;
	private ImageButton Bsave;
	private ImageView[] image = new ImageView[5];
	private String[] nameImage = new String[5];
	private WakeLock wl;
	private ViewFlipper flipper;
	private float fromPosition;
	private int selectedImage;
	private String idItem;

	private Button bt_seccion;
	private Button bt_grupo;
	private Button bt_sgrupo;

	private String id_seccion;
	private String id_grupo;
	private String id_sgrupo;
	
	private final int SCREEN_DIM_WAKE_LOCK = 6;

	private SelectedDataDialog selected;

	private Vector<AnswerListener> answerListener = new Vector<AnswerListener>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.addAnswerListener(this);
		setContentView(R.layout.activity_upload_photo);


		barcode = (EditText)findViewById(R.id.et_barcode);
		barcode.addTextChangedListener(this);

		descripcion = (TextView)findViewById(R.id.tv_descripcion);
		marca = (TextView)findViewById(R.id.tv_marca);

		Bbarcode = (ImageButton)findViewById(R.id.ib_barcode);
		Bbarcode.setOnClickListener(this);
		Blimpiar = (ImageButton)findViewById(R.id.ib_limpiar);
		Blimpiar.setOnClickListener(this);
		Bsave = (ImageButton)findViewById(R.id.ib_guardar);
		Bsave.setOnClickListener(this);

		bt_seccion = (Button)findViewById(R.id.bt_seccion);
		bt_seccion.setOnClickListener(this);

		bt_grupo = (Button)findViewById(R.id.bt_grupo);
		bt_grupo.setOnClickListener(this);

		bt_sgrupo = (Button)findViewById(R.id.bt_sgrupo);
		bt_sgrupo.setOnClickListener(this);

		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layout_flipper);
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
			flipper.addView(v);
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

		if (savedInstanceState!=null) {
			fromPosition = savedInstanceState.getFloat("fromPosition");
			selectedImage = savedInstanceState.getInt("selectedImage");
			idItem = savedInstanceState.getString("idItem");
			marca.setText(savedInstanceState.getString("marca"));
			descripcion.setText(savedInstanceState.getString("descripcion"));
			barcode.setText(savedInstanceState.getString("barcode"));
			nameImage=savedInstanceState.getStringArray("nameimage");
			id_seccion = savedInstanceState.getString("id_seccion");
			id_grupo = savedInstanceState.getString("id_grupo");
			id_sgrupo = savedInstanceState.getString("id_sgrupo");
			bt_seccion.setText(savedInstanceState.getString("seccion"));
			bt_grupo.setText(savedInstanceState.getString("grupo"));
			bt_sgrupo.setText(savedInstanceState.getString("sgrupo"));
			loadCacheImages();
		}
		else {
			cleanImages();
		}


		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "BusSnoozeAlarm");
		wl.acquire();
	}

	@Override
	public void onResume() {
		super.onResume();
		try{
			wl.release();
		} 
		catch(RuntimeException e){
			
		}
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putFloat("fromPosition",fromPosition);
		savedInstanceState.putInt("selectedImage",selectedImage);
		savedInstanceState.putString("idItem",idItem);
		savedInstanceState.putString("barcode",barcode.getText().toString());
		savedInstanceState.putString("marca",marca.getText().toString());
		savedInstanceState.putString("descripcion",descripcion.getText().toString());
		savedInstanceState.putStringArray("nameimage",nameImage);
		savedInstanceState.putString("id_seccion",id_seccion);
		savedInstanceState.putString("id_grupo",id_grupo);
		savedInstanceState.putString("id_sgrupo",id_sgrupo);
		savedInstanceState.putString("seccion",bt_seccion.getText().toString());
		savedInstanceState.putString("grupo",bt_grupo.getText().toString());
		savedInstanceState.putString("sgrupo",bt_sgrupo.getText().toString());
	}

	private void cleanImages() {
		BitmapFactory.Options o=new BitmapFactory.Options();
		o.inSampleSize = 2;
		o.inDither=false;                     //Disable Dithering mode
		o.inPurgeable=true;                   //Tell to gc that whether it needs free memory, the Bitmap can be cleared

		for (int i=0;i<image.length;i++) {
			GlobalData.image[i] = BitmapFactory.decodeResource(getResources(),R.drawable.sin_img, o);
			image[i].setImageBitmap(GlobalData.image[i]);
		}
	}

	private void loadCacheImages() {
		for (int i=0;i<image.length;i++) {
			if (GlobalData.image[i]!=null)
			image[i].setImageBitmap(GlobalData.image[i]);
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
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

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		int view = arg0.getId();
		Log.d("EMAKU","EMAKU: view: "+view);
		if (view==R.id.ib_barcode) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
	        intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
	        startActivityForResult(intent, 0);
		}
		else if (view==R.id.foto0 || view==R.id.foto1 || view==R.id.foto2 || view==R.id.foto3 || view==R.id.foto4) {
			if (idItem!=null && !idItem.equals("")) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (view==R.id.foto0) {
					selectedImage=0;
				}
				else if (view==R.id.foto1) {
					selectedImage=1;
					nameImage[1] = "MT"+String.valueOf(System.currentTimeMillis())+ ".jpg";
				}
				else if (view==R.id.foto2) {
					selectedImage=2;
					nameImage[2] = "MT"+String.valueOf(System.currentTimeMillis())+ ".jpg";
				}
				else if (view==R.id.foto3) {
					selectedImage=3;
					nameImage[3] = "MT"+String.valueOf(System.currentTimeMillis())+ ".jpg";
				}
				else if (view==R.id.foto4) {
					selectedImage=4;
					nameImage[4] = "MT"+String.valueOf(System.currentTimeMillis())+ ".jpg";
				}

				nameImage[selectedImage] = "MT"+String.valueOf(System.currentTimeMillis())+ ".jpg";

		        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(Environment.getExternalStoragePublicDirectory(
	    	            Environment.DIRECTORY_PICTURES+File.separator+nameImage[selectedImage])));
				//Uri creattion to strore images
		        startActivityForResult(intent, 1);
			}
			else {
                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_iditem), Toast.LENGTH_LONG).show();
			}
		}
		else if (view==R.id.ib_limpiar) {
			clean();
		}
		else if (view==R.id.ib_guardar) {
			if (id_seccion!=null && !id_seccion.equals("")) {
				if (id_grupo!=null && !id_grupo.equals("")) {
					if (id_sgrupo!=null && !id_sgrupo.equals("")) {
						sendTransaction();
					}
					else {
		                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_sgrupo), Toast.LENGTH_LONG).show();
					}
				}
				else {
	                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_grupo), Toast.LENGTH_LONG).show();
				}
			}
			else {
                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_seccion), Toast.LENGTH_LONG).show();
			}
		}
		else if (view==R.id.bt_seccion) {
			if (idItem!=null && !idItem.equals("")) {
				selectedDataDialog(((Button)arg0).getId(),R.string.consulta_seccion,"MVSEL0040",null);
			}
			else {
                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_barcode_seccion), Toast.LENGTH_LONG).show();
			}
		}
		else if (view==R.id.bt_grupo) {
			if (id_seccion!=null && !id_seccion.equals("")) {
				selectedDataDialog(((Button)arg0).getId(),R.string.consulta_grupo,"MVSEL0041",new String[]{id_seccion});
			}
			else {
                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_seccion), Toast.LENGTH_LONG).show();
			}
		}
		else if (view==R.id.bt_sgrupo) {
			if (id_seccion!=null && !id_seccion.equals("")) {
				selectedDataDialog(((Button)arg0).getId(),R.string.consulta_sgrupo,"MVSEL0042",new String[]{id_seccion,id_grupo});
			}
			else {
                Toast.makeText(UploadPhotoActivity.this,getString(R.string.error_grupo), Toast.LENGTH_LONG).show();
			}
		}
	}


	private void clean() {
		barcode.setText("");
		descripcion.setText(getString(R.string.descripcion));
		marca.setText(getString(R.string.marca));
		bt_seccion.setText(getString(R.string.seccion));
		bt_grupo.setText(getString(R.string.grupo));
		bt_sgrupo.setText(getString(R.string.sgrupo));
		id_seccion="";
		id_grupo="";
		id_sgrupo="";
		cleanImages();
	}
	private void selectedDataDialog(long button,int title,String sql,String[] args) {
		selected = new SelectedDataDialog(button,getString(title),sql,args);
		selected.addDialogClickListener(this);
		selected.show(getFragmentManager(),getString(R.string.buscar_referencia));
	}

	 @Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("EMAKU"," ResultCode: "+resultCode+" requestCode: "+requestCode);
		if (requestCode == 0) {
		    if (resultCode == RESULT_OK) {
		        String contents = intent.getStringExtra("SCAN_RESULT");
		        // Handle successful scan
		        barcode.setText(contents);
		        searchQuery("MVSEL0037",contents);
		        searchQuery("MVSEL0036",contents);
		        searchQuery("MVSEL0038",contents);
		    } else if (resultCode == RESULT_CANCELED) {
		        // Handle cancel
		        Toast toast = Toast.makeText(this, "Scan was Cancelled!", Toast.LENGTH_LONG);
		        toast.setGravity(Gravity.TOP, 25, 400);
		        toast.show();

		    }
		}
		if (resultCode == RESULT_OK && requestCode==1 ) {
			try {
				loadImage();
			}
			catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		else if(resultCode == RESULT_CANCELED) {
		      Log.d("Result: ", "Launch Cancelled.");
		}
    }

	private void loadImage() throws FileNotFoundException {
		try {
			System.out.println("selected Image: "+selectedImage);
			File fimage = new File(Uri.fromFile(Environment.getExternalStoragePublicDirectory(
		            Environment.DIRECTORY_PICTURES+File.separator+nameImage[selectedImage])).getPath());
			FileInputStream fis = new FileInputStream(fimage);
			nameImage[selectedImage] = fimage.getName();
			 GlobalData.image[selectedImage] = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(fis),1024, 768, true);
			image[selectedImage].setImageBitmap(GlobalData.image[selectedImage]);
			try {
				fis.close();
				fimage = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (OutOfMemoryError OOMe) {
			System.out.println("*************************************************************");
			System.out.println("reintentando por memoria copada");
			System.out.println("liberando memoria");
			android.app.ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
			System.out.println("memoria actual: "+mi.availMem);
			System.gc();
			System.out.println("memoria actual: "+mi.availMem);
			System.out.println("cargando nuevamente imagenes");
			loadImage();
			System.out.println("**************************************************************");
		}
	}

	private void searchQuery(String sqlCode,String value) {

		class SearchingSQL extends Thread {

			private String sqlCode;
			private String arg;

			public SearchingSQL(String sqlCode,String arg) {
				this.sqlCode=sqlCode;
				this.arg=arg;
			}

			@Override
			public void run() {
				Document doc = null;
				try {
					String[] args = new String[]{arg};
					doc = TransactionServerResultSet.getResultSetST(sqlCode, args);//new String[]{XMLTField.getText()});
					Log.d("EMAKU","EMAKU Query desde Searching..");
				} catch (TransactionServerException e) {
					e.printStackTrace();
				}
				AnswerEvent event = new AnswerEvent(this, sqlCode, doc);
				notificando(event);
			}
		}
		new SearchingSQL(sqlCode,value).start();
	}

	private void notificando(AnswerEvent event) {
		for(AnswerListener l:answerListener) {
			System.out.println("Notificando a =>"+l);
			l.arriveAnswerEvent(event);
		}
	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		System.out.println("llego la query "+e.getSqlCode());
		Document doc = e.getDocument();
		if (e.getSqlCode().equals("MVSEL0037")) {
			final List<Element> listRows = doc.getRootElement().getChildren("row");
			this.runOnUiThread(new Runnable() {
	            @Override
				public void run() {
	    			try {
						for (int i = 0; i < listRows.size(); i++) {
							Element Erow = listRows.get(i);
							List<Element> Lcol = Erow.getChildren();
							descripcion.setText(Lcol.get(0).getText());
							String seccion = Lcol.get(1).getText();
							String grupo = Lcol.get(2).getText();
							String sgrupo = Lcol.get(3).getText();
							if (seccion!=null && !seccion.equals("")) {
								bt_seccion.setText(seccion);
								id_seccion = Lcol.get(4).getText();
							}
							if (grupo!=null && !grupo.equals("")) {
								bt_grupo.setText(grupo);
								id_grupo = Lcol.get(5).getText();
							}
							if (sgrupo!=null && !sgrupo.equals("")) {
								bt_sgrupo.setText(sgrupo);
								id_sgrupo = Lcol.get(6).getText();
							}
							idItem = Lcol.get(7).getText();
						}
	    			}
					catch(NullPointerException NPEe) {
						NPEe.printStackTrace();
					}
				}
            });
		}
		else if (e.getSqlCode().equals("MVSEL0036")) {
			try {
				final String value =doc.getRootElement().getChild("row").getChildText("col").trim().toString();
		        this.runOnUiThread(new Runnable() {
		            @Override
					public void run() {
		    			Log.d("EMAKU","EMAKU ref:"+value);
		    			marca.setText(getString(R.string.marca)+" "+value);
		            	Log.d("EMAKU","EMAKU desde editText "+marca.getText());
		            }});
			}
			catch(NullPointerException NPEe) {
			}

		}
		else if (e.getSqlCode().equals("MVSEL0038")) {
				final List<Element> listRows = doc.getRootElement().getChildren("row");
				this.runOnUiThread(new Runnable() {
		            @Override
					public void run() {
		    			try {
							System.out.println("numero de fotos: "+listRows);
							for (int i = 0; i < listRows.size(); i++) {
								System.out.println("cargando foto: "+i);
								Element Erow = listRows.get(i);
								List<Element> Lcol = Erow.getChildren();
								String imgValue = Lcol.get(0).getText();
								final ZipHandler zip = new ZipHandler();
								byte[] bytesImage;
								bytesImage = zip.getDataDecode(imgValue);
								GlobalData.image[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeByteArray(bytesImage,0,bytesImage.length),1024, 768, true);
								nameImage[i] = zip.getCurrentFile();
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
	}

	@Override
	public boolean containSqlCode(String sqlCode) {
		// TODO Auto-generated method stub
		return false;
	}


	public void addAnswerListener(AnswerListener listener ) {
		 answerListener.addElement(listener);
	}

	public void removeAnswerListener(AnswerListener listener ) {
		 answerListener.removeElement(listener);
	}

	private void sendTransaction() {
		Element root = new Element("TRANSACTION");
		Document doc = new Document(root);
		Element driver = new Element("driver");
		driver.setText("MVTR00005");
		Element id = new Element("id");
		id.setText("P1");
		Element p1 = new Element("package");
		Element f1 = new Element("field");
		Element s = new Element("field");
		Element g = new Element("field");
		Element sg = new Element("field");

		f1.setAttribute("name","idItem");
		f1.setAttribute("attribute","key");
		f1.setText(idItem);
		s.setText(id_seccion);
		g.setText(id_grupo);
		sg.setText(id_sgrupo);
		p1.addContent(f1);
		p1.addContent(s);
		p1.addContent(g);
		p1.addContent(sg);

		Element p2 = new Element("package");
		for (int i=0;i<5;i++) {
			if (GlobalData.image[i]!= null && !GlobalData.image[i].equals(BitmapFactory.decodeResource(this.getResources(), R.drawable.sin_img))
					&& nameImage[i]!=null){
				Element sp = new Element("subpackage");
				Element f2 = new Element("field");
				f2.setText(nameImage[i]);
				Element f3 = new Element("field");
				f3.setAttribute("saveImage","true");
				f3.setText(getImageB64(i));
				sp.addContent(f2);
				sp.addContent(f3);
				p2.addContent(sp);
			}
		}

		root.addContent(driver);
		root.addContent(id);
		root.addContent(p1);
		root.addContent(p2);

		SocketChannel sock = SocketConnector.getSock();
		System.out.println("Escribiendo paquete");
		SocketWriter.writing(sock, doc);
	}

	private String getImageB64(int i) {
    	try {
    		ByteArrayOutputStream stream=new ByteArrayOutputStream();
    	    GlobalData.image[i].compress(Bitmap.CompressFormat.JPEG, 90, stream);
			ZipHandler zip = new ZipHandler(stream,nameImage[i]);
    		Log.d("EMAKU","EMAKU zip "+zip.getDataEncode().toString());
			return new String(zip.getDataEncode());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			e.printStackTrace();
			return null;
		}

	}


	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		if (s.toString().contains("\n")) {
			barcode.setText(barcode.getText().toString().trim());
			String contents = barcode.getText().toString().trim();
			System.out.println("tiene un enter");
			cleanImages();
	        searchQuery("MVSEL0037",contents);
	        searchQuery("MVSEL0036",contents);
	        searchQuery("MVSEL0038",contents);
		}

	}

	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		if (R.id.bt_seccion==e.getIdobject()) {
			bt_seccion.setText(e.getValue());
			id_seccion=e.getId();
		}
		else if (R.id.bt_grupo==e.getIdobject()) {
			bt_grupo.setText(e.getValue());
			id_grupo=e.getId();
		}
		else if (R.id.bt_sgrupo==e.getIdobject()) {
			bt_sgrupo.setText(e.getValue());
			id_sgrupo=e.getId();
		}
	}
}
