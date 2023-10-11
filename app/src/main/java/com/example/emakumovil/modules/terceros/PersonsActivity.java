package com.example.emakumovil.modules.terceros;

import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emakumovil.R;
import com.example.emakumovil.components.AnswerEvent;
import com.example.emakumovil.components.AnswerListener;
import com.example.emakumovil.components.DialogClickEvent;
import com.example.emakumovil.components.DialogClickListener;
import com.example.emakumovil.components.SearchDataDialog;
import com.example.emakumovil.components.SearchQuery;
import com.example.emakumovil.components.SelectedDataDialog;
import com.example.emakumovil.comunications.SocketConnector;
import com.example.emakumovil.comunications.SocketWriter;

public class PersonsActivity extends Activity implements OnClickListener, DialogClickListener, AnswerListener, OnFocusChangeListener {

	private ImageButton ib_buscar_tercero;

	/*
	 * Botones de guardar
	 */

	private ImageButton ib_guardar_tercero;
	private ImageButton ib_modificar_tercero;

	/*
	 * Bloque informacion general
	 */
	private EditText et_id_char;
	private TextView tv_dv;
	private Button bt_tipo_tercero;
	private Button bt_clasificacion;
	private EditText et_ganap;
	private ImageButton ib_barcode;
	private EditText et_primern;
	private EditText et_segundon;
	private EditText et_primera;
	private EditText et_segundoa;
	private EditText et_razon_social;

	/*
	 * Bloque correo electronico
	 */
	private EditText[] et_correo_electronico = new EditText[5];
	/*
	 * Bloque Direccion
	 */
	private Button[] bt_departamento = new Button[3];
	private Button[] bt_ciudad = new Button[3];
	private EditText[] et_direccion = new EditText[3];
	private LinearLayout[] ly_direccion = new LinearLayout[3];

	/*
	 * Bloque telefonos
	 */

	private Button[] bt_tipo_tel = new Button[3];
	private EditText[] et_numero = new EditText[3];
	private LinearLayout[] ly_telefono = new LinearLayout[3];

	/*
	 * Informacion Contable
	 */
	private Button bt_regimen;
	private Button bt_actividad;
	private Button bt_lista_precios;
	private EditText et_dias_credito;
	private EditText et_cupom;
	private Button bt_estado;

	/*
	 * Informacion bancaria
	 */
	private EditText[] et_descripcion_banco = new EditText[3];
	private EditText[] et_numero_cuenta = new EditText[3];
	private Button[] bt_tipo_cuenta = new Button[3];
	private Button[] bt_banco = new Button[3];
	private Button[] bt_departamento_banco = new Button[3];
	private Button[] bt_ciudad_banco = new Button[3];
	/*
	 * Asociacion de marcas
	 */
	private Button bt_marcas[] = new Button[10];
	private TextView tv_info_aso_marcas;

	private SelectedDataDialog selected;
	private SearchDataDialog search;

	/*
	 * Ids de botones
	 */

	private String id_tipo_tercero;
	private String id_correo_electronico[] = new String[5];
	private String id_departamento[] = new String[3];
	private String id_ciudad[] = new String[3];
	private String id_tipo_tel[] = new String[3];
	private String id_regimen;
	private String id_actividad;
	private String id_lista_precios;
	private String id_estado;
	private String id_tipo_cuenta[] = new String[3];
	private String id_banco[] = new String[3];
	private String id_departamento_banco[] = new String[3];
	private String id_ciudad_banco[] = new String[3];
	private String id_clasificacion;
	private String id_marcas[] = new String[10];
	private String id_buscar_tercero;
	private String id_cuenta_bancaria[] = new String[3];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_persons);
		ib_buscar_tercero = (ImageButton) findViewById(R.id.ib_buscar_tercero);
		ib_buscar_tercero.setOnClickListener(this);

		ib_guardar_tercero = (ImageButton) findViewById(R.id.ib_guardar_tercero);
		ib_guardar_tercero.setOnClickListener(this);

		ib_modificar_tercero = (ImageButton) findViewById(R.id.ib_modificar_tercero);
		ib_modificar_tercero.setOnClickListener(this);

		/*
		 * Bloque informacion general
		 */
		et_id_char = (EditText) findViewById(R.id.et_id_char);
		et_id_char.setOnFocusChangeListener(this);
		tv_dv = (TextView) findViewById(R.id.tv_dv);

		bt_tipo_tercero = (Button) findViewById(R.id.bt_tipo_tercero);
		bt_tipo_tercero.setOnClickListener(this);

		bt_clasificacion = (Button) findViewById(R.id.bt_clasificacion);
		bt_clasificacion.setOnClickListener(this);

		et_ganap = (EditText) findViewById(R.id.et_ganap);

		ib_barcode = (ImageButton) findViewById(R.id.ib_barcode);
		ib_barcode.setOnClickListener(this);

		et_primern = (EditText) findViewById(R.id.et_primern);
		et_segundon = (EditText) findViewById(R.id.et_segundon);
		et_primera = (EditText) findViewById(R.id.et_primera);
		et_segundoa = (EditText) findViewById(R.id.et_segundoa);
		et_razon_social = (EditText) findViewById(R.id.et_razon_social);

		/*
		 * Validando entrada de solo letras
		 */
		
		InputFilter[] onlyText = new InputFilter[] {
				new InputFilter() { 
				    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) { 
				            for (int i = start; i < end; i++) { 
				                    if (Character.isDigit(source.charAt(i))) { 
				                            return ""; 
				                    } 
				            } 
				            return null; 
				     } 
				}
			};
		
		et_primern.setFilters(onlyText);
		et_segundon.setFilters(onlyText);
		et_primera.setFilters(onlyText);
		et_segundoa.setFilters(onlyText);
		
		et_correo_electronico[0] = (EditText) findViewById(R.id.et_correo_electronico);
		et_correo_electronico[1] = (EditText) findViewById(R.id.et_correo_electronico_dos);
		et_correo_electronico[2] = (EditText) findViewById(R.id.et_correo_electronico_tres);
		et_correo_electronico[3] = (EditText) findViewById(R.id.et_correo_electronico_cuatro);
		et_correo_electronico[4] = (EditText) findViewById(R.id.et_correo_electronico_cinco);

		et_correo_electronico[1].setVisibility(View.GONE);
		et_correo_electronico[2].setVisibility(View.GONE);
		et_correo_electronico[3].setVisibility(View.GONE);
		et_correo_electronico[4].setVisibility(View.GONE);

		/*
		 * Bloque Direccion
		 */
		ly_direccion[0] = (LinearLayout) findViewById(R.id.ly_direccion1);
		ly_direccion[1] = (LinearLayout) findViewById(R.id.ly_direccion2);
		ly_direccion[2] = (LinearLayout) findViewById(R.id.ly_direccion3);

		ly_direccion[1].setVisibility(View.GONE);
		ly_direccion[2].setVisibility(View.GONE);

		bt_departamento[0] = (Button) findViewById(R.id.bt_departamento);
		bt_departamento[0].setOnClickListener(this);

		bt_ciudad[0] = (Button) findViewById(R.id.bt_ciudad);
		bt_ciudad[0].setOnClickListener(this);

		et_direccion[0] = (EditText) findViewById(R.id.et_direccion);

		bt_departamento[1] = (Button) findViewById(R.id.bt_departamento_dos);
		bt_departamento[1].setOnClickListener(this);

		bt_ciudad[1] = (Button) findViewById(R.id.bt_ciudad_dos);
		bt_ciudad[1].setOnClickListener(this);

		et_direccion[1] = (EditText) findViewById(R.id.et_direccion_dos);

		bt_departamento[2] = (Button) findViewById(R.id.bt_departamento_tres);
		bt_departamento[2].setOnClickListener(this);

		bt_ciudad[2] = (Button) findViewById(R.id.bt_ciudad_tres);
		bt_ciudad[2].setOnClickListener(this);

		et_direccion[2] = (EditText) findViewById(R.id.et_direccion_tres);

		/*
		 * Bloque telefonos
		 */
		ly_telefono[0] = (LinearLayout) findViewById(R.id.ly_telefono1);
		ly_telefono[1] = (LinearLayout) findViewById(R.id.ly_telefono2);
		ly_telefono[2] = (LinearLayout) findViewById(R.id.ly_telefono3);

		ly_telefono[1].setVisibility(View.GONE);
		ly_telefono[2].setVisibility(View.GONE);

		bt_tipo_tel[0] = (Button) findViewById(R.id.bt_tipo_tel);
		bt_tipo_tel[0].setOnClickListener(this);
		et_numero[0] = (EditText) findViewById(R.id.et_numero);

		bt_tipo_tel[1] = (Button) findViewById(R.id.bt_tipo_tel_dos);
		bt_tipo_tel[1].setOnClickListener(this);
		et_numero[1] = (EditText) findViewById(R.id.et_numero_dos);

		bt_tipo_tel[2] = (Button) findViewById(R.id.bt_tipo_tel_tres);
		bt_tipo_tel[2].setOnClickListener(this);
		et_numero[2] = (EditText) findViewById(R.id.et_numero_tres);

		/*
		 * Informacion Contable
		 */

		bt_regimen = (Button) findViewById(R.id.bt_regimen);
		bt_regimen.setOnClickListener(this);
		bt_actividad = (Button) findViewById(R.id.bt_actividad);
		bt_actividad.setOnClickListener(this);
		et_dias_credito = (EditText) findViewById(R.id.et_dias_credito);
		et_cupom = (EditText) findViewById(R.id.et_cupom);
		bt_estado = (Button) findViewById(R.id.bt_estado);
		bt_estado.setOnClickListener(this);
		bt_lista_precios = (Button) findViewById(R.id.bt_lista_precios);
		bt_lista_precios.setOnClickListener(this);

		/*
		 * Informacion bancaria
		 */
		et_descripcion_banco[0] = (EditText) findViewById(R.id.et_descripcion_banco);
		et_numero_cuenta[0] = (EditText) findViewById(R.id.et_numero_cuenta);
		bt_tipo_cuenta[0] = (Button) findViewById(R.id.bt_tipo_cuenta);
		bt_tipo_cuenta[0].setOnClickListener(this);
		bt_banco[0] = (Button) findViewById(R.id.bt_banco);
		bt_banco[0].setOnClickListener(this);
		bt_departamento_banco[0] = (Button) findViewById(R.id.bt_departamento_banco);
		bt_departamento_banco[0].setOnClickListener(this);
		bt_ciudad_banco[0] = (Button) findViewById(R.id.bt_ciudad_banco);
		bt_ciudad_banco[0].setOnClickListener(this);

		et_descripcion_banco[1] = (EditText) findViewById(R.id.et_descripcion_banco_dos);
		et_numero_cuenta[1] = (EditText) findViewById(R.id.et_numero_cuenta_dos);
		bt_tipo_cuenta[1] = (Button) findViewById(R.id.bt_tipo_cuenta_dos);
		bt_tipo_cuenta[1].setOnClickListener(this);
		bt_banco[1] = (Button) findViewById(R.id.bt_banco_dos);
		bt_banco[1].setOnClickListener(this);
		bt_departamento_banco[1] = (Button) findViewById(R.id.bt_departamento_banco_dos);
		bt_departamento_banco[1].setOnClickListener(this);
		bt_ciudad_banco[1] = (Button) findViewById(R.id.bt_ciudad_banco_dos);
		bt_ciudad_banco[1].setOnClickListener(this);

		et_descripcion_banco[2] = (EditText) findViewById(R.id.et_descripcion_banco_tres);
		et_numero_cuenta[2] = (EditText) findViewById(R.id.et_numero_cuenta_tres);
		bt_tipo_cuenta[2] = (Button) findViewById(R.id.bt_tipo_cuenta_tres);
		bt_tipo_cuenta[2].setOnClickListener(this);
		bt_banco[2] = (Button) findViewById(R.id.bt_banco_tres);
		bt_banco[2].setOnClickListener(this);
		bt_departamento_banco[2] = (Button) findViewById(R.id.bt_departamento_banco_tres);
		bt_departamento_banco[2].setOnClickListener(this);
		bt_ciudad_banco[2] = (Button) findViewById(R.id.bt_ciudad_banco_tres);
		bt_ciudad_banco[2].setOnClickListener(this);

		et_descripcion_banco[1].setVisibility(View.GONE);
		et_numero_cuenta[1].setVisibility(View.GONE);
		bt_tipo_cuenta[1].setVisibility(View.GONE);
		bt_banco[1].setVisibility(View.GONE);
		bt_departamento_banco[1].setVisibility(View.GONE);
		bt_ciudad_banco[1].setVisibility(View.GONE);

		et_descripcion_banco[2].setVisibility(View.GONE);
		et_numero_cuenta[2].setVisibility(View.GONE);
		bt_tipo_cuenta[2].setVisibility(View.GONE);
		bt_banco[2].setVisibility(View.GONE);
		bt_departamento_banco[2].setVisibility(View.GONE);
		bt_ciudad_banco[2].setVisibility(View.GONE);

		/*
		 * Asociacion Marcas
		 */
		tv_info_aso_marcas = (TextView) findViewById(R.id.tv_info_aso_marcas);
		bt_marcas[0] = (Button) findViewById(R.id.bt_marcas);
		bt_marcas[0].setOnClickListener(this);
		bt_marcas[1] = (Button) findViewById(R.id.bt_marcas_dos);
		bt_marcas[1].setOnClickListener(this);
		bt_marcas[2] = (Button) findViewById(R.id.bt_marcas_tres);
		bt_marcas[2].setOnClickListener(this);
		bt_marcas[3] = (Button) findViewById(R.id.bt_marcas_cuatro);
		bt_marcas[3].setOnClickListener(this);
		bt_marcas[4] = (Button) findViewById(R.id.bt_marcas_cinco);
		bt_marcas[4].setOnClickListener(this);
		bt_marcas[5] = (Button) findViewById(R.id.bt_marcas_seis);
		bt_marcas[5].setOnClickListener(this);
		bt_marcas[6] = (Button) findViewById(R.id.bt_marcas_siete);
		bt_marcas[6].setOnClickListener(this);
		bt_marcas[7] = (Button) findViewById(R.id.bt_marcas_ocho);
		bt_marcas[7].setOnClickListener(this);
		bt_marcas[8] = (Button) findViewById(R.id.bt_marcas_nueve);
		bt_marcas[8].setOnClickListener(this);
		bt_marcas[9] = (Button) findViewById(R.id.bt_marcas_diez);
		bt_marcas[9].setOnClickListener(this);

	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		/*
		 * Informacion Personal
		 */
		savedInstanceState.putString("dv", tv_dv.getText().toString());
		savedInstanceState.putString("primern", et_primern.getText().toString());
		savedInstanceState.putString("segundon", et_segundon.getText().toString());
		savedInstanceState.putString("primera", et_primera.getText().toString());
		savedInstanceState.putString("segundoa", et_segundoa.getText().toString());
		savedInstanceState.putString("razon_social", et_razon_social.getText().toString());
		savedInstanceState.putString("id_tipo_tercero", id_tipo_tercero);
		savedInstanceState.putString("tipo_tercero", bt_tipo_tercero.getText().toString());
		savedInstanceState.putString("id_clasificacion", id_clasificacion);
		savedInstanceState.putString("clasificacion", bt_clasificacion.getText().toString());
		savedInstanceState.putString("ganap", et_ganap.getText().toString());

		/*
		 * Correo electronico
		 */
		savedInstanceState.putString("correo_electronico1", et_correo_electronico[0].getText().toString());
		savedInstanceState.putString("correo_electronico2", et_correo_electronico[1].getText().toString());
		savedInstanceState.putString("correo_electronico3", et_correo_electronico[2].getText().toString());
		savedInstanceState.putString("correo_electronico4", et_correo_electronico[3].getText().toString());
		savedInstanceState.putString("correo_electronico5", et_correo_electronico[4].getText().toString());
		/*
		 * Direccion
		 */
		savedInstanceState.putString("id_departamento1", id_departamento[0]);
		savedInstanceState.putString("id_departamento2", id_departamento[1]);
		savedInstanceState.putString("id_departamento3", id_departamento[2]);
		savedInstanceState.putString("departamento1", bt_departamento[0].getText().toString());
		savedInstanceState.putString("departamento2", bt_departamento[1].getText().toString());
		savedInstanceState.putString("departamento3", bt_departamento[2].getText().toString());
		savedInstanceState.putString("id_ciudad1", id_ciudad[0]);
		savedInstanceState.putString("id_ciudad2", id_ciudad[1]);
		savedInstanceState.putString("id_ciudad3", id_ciudad[2]);
		savedInstanceState.putString("ciudad1", bt_ciudad[0].getText().toString());
		savedInstanceState.putString("ciuda2", bt_ciudad[1].getText().toString());
		savedInstanceState.putString("ciudad3", bt_ciudad[2].getText().toString());
		savedInstanceState.putString("direccion1", et_direccion[0].getText().toString());
		savedInstanceState.putString("direccion2", et_direccion[1].getText().toString());
		savedInstanceState.putString("direccion3", et_direccion[2].getText().toString());
		/*
		 * Telefono
		 */
		savedInstanceState.putString("id_tipo_tel1", id_tipo_tel[0]);
		savedInstanceState.putString("id_tipo_tel2", id_tipo_tel[1]);
		savedInstanceState.putString("id_tipo_tel3", id_tipo_tel[2]);
		savedInstanceState.putString("tipo_tel1", bt_tipo_tel[0].getText().toString());
		savedInstanceState.putString("tipo_tel2", bt_tipo_tel[1].getText().toString());
		savedInstanceState.putString("tipo_tel3", bt_tipo_tel[2].getText().toString());
		savedInstanceState.putString("numero1", et_numero[0].getText().toString());
		savedInstanceState.putString("numero2", et_numero[1].getText().toString());
		savedInstanceState.putString("numero3", et_numero[2].getText().toString());
		/*
		 * Informacion contable
		 */
		savedInstanceState.putString("id_regimen", id_regimen);
		savedInstanceState.putString("regimen", bt_regimen.getText().toString());
		savedInstanceState.putString("id_actividad_economica", id_actividad);
		savedInstanceState.putString("actividad", bt_actividad.getText().toString());
		savedInstanceState.putString("id_lista_precios", id_lista_precios);
		savedInstanceState.putString("lista_precios", bt_lista_precios.getText().toString());
		savedInstanceState.putString("dias_credito", et_dias_credito.getText().toString());
		savedInstanceState.putString("cupo_max", et_cupom.getText().toString());
		savedInstanceState.putString("id_estado", id_estado);
		savedInstanceState.putString("estado", bt_estado.getText().toString());
		/*
		 * Informacion bancaria
		 */
		savedInstanceState.putString("id_cuenta_bancaria1", id_cuenta_bancaria[0]);
		savedInstanceState.putString("id_cuenta_bancaria2", id_cuenta_bancaria[1]);
		savedInstanceState.putString("id_cuenta_bancaria3", id_cuenta_bancaria[2]);
		savedInstanceState.putString("descripcion_cuenta1", et_descripcion_banco[0].getText().toString());
		savedInstanceState.putString("descripcion_cuenta2", et_descripcion_banco[1].getText().toString());
		savedInstanceState.putString("descripcion_cuenta3", et_descripcion_banco[2].getText().toString());
		savedInstanceState.putString("numero_cuenta1", et_numero_cuenta[0].getText().toString());
		savedInstanceState.putString("numero_cuenta2", et_numero_cuenta[1].getText().toString());
		savedInstanceState.putString("numero_cuenta3", et_numero_cuenta[2].getText().toString());
		savedInstanceState.putString("id_tipo_cuenta1", id_tipo_cuenta[0]);
		savedInstanceState.putString("id_tipo_cuenta2", id_tipo_cuenta[1]);
		savedInstanceState.putString("id_tipo_cuenta3", id_tipo_cuenta[2]);
		savedInstanceState.putString("tipo_cuenta1", bt_tipo_cuenta[0].getText().toString());
		savedInstanceState.putString("tipo_cuenta2", bt_tipo_cuenta[1].getText().toString());
		savedInstanceState.putString("tipo_cuenta3", bt_tipo_cuenta[2].getText().toString());
		savedInstanceState.putString("banco1", bt_banco[0].getText().toString());
		savedInstanceState.putString("banco2", bt_banco[1].getText().toString());
		savedInstanceState.putString("banco3", bt_banco[2].getText().toString());
		savedInstanceState.putString("id_departamento_banco1", id_departamento_banco[0]);
		savedInstanceState.putString("id_departamento_banco2", id_departamento_banco[1]);
		savedInstanceState.putString("id_departamento_banco3", id_departamento_banco[2]);
		savedInstanceState.putString("departamento_banco1", bt_departamento[0].getText().toString());
		savedInstanceState.putString("departamento_banco2", bt_departamento[1].getText().toString());
		savedInstanceState.putString("departamento_banco3", bt_departamento[2].getText().toString());
		savedInstanceState.putString("id_ciudad_banco1", id_ciudad_banco[0]);
		savedInstanceState.putString("id_ciuda_banco2", id_ciudad_banco[1]);
		savedInstanceState.putString("id_ciudad_banco3", id_ciudad_banco[2]);
		savedInstanceState.putString("ciudad_banco1", bt_ciudad[0].getText().toString());
		savedInstanceState.putString("ciudad_banco2", bt_ciudad[1].getText().toString());
		savedInstanceState.putString("ciudad_banco3", bt_ciudad[2].getText().toString());
		/*
		 * Marcas
		 */
		savedInstanceState.putString("id_marca1", id_marcas[0]);
		savedInstanceState.putString("id_marca2", id_marcas[1]);
		savedInstanceState.putString("id_marca3", id_marcas[2]);
		savedInstanceState.putString("id_marca4", id_marcas[3]);
		savedInstanceState.putString("id_marca5", id_marcas[4]);
		savedInstanceState.putString("id_marca6", id_marcas[5]);
		savedInstanceState.putString("id_marca7", id_marcas[6]);
		savedInstanceState.putString("id_marca8", id_marcas[7]);
		savedInstanceState.putString("id_marca9", id_marcas[8]);
		savedInstanceState.putString("id_marca10", id_marcas[9]);
		savedInstanceState.putString("marca1", bt_marcas[0].getText().toString());
		savedInstanceState.putString("marca2", bt_marcas[1].getText().toString());
		savedInstanceState.putString("marca3", bt_marcas[2].getText().toString());
		savedInstanceState.putString("marca4", bt_marcas[3].getText().toString());
		savedInstanceState.putString("marca5", bt_marcas[4].getText().toString());
		savedInstanceState.putString("marca6", bt_marcas[5].getText().toString());
		savedInstanceState.putString("marca7", bt_marcas[6].getText().toString());
		savedInstanceState.putString("marca8", bt_marcas[7].getText().toString());
		savedInstanceState.putString("marca9", bt_marcas[8].getText().toString());
		savedInstanceState.putString("marca10", bt_marcas[9].getText().toString());
	}

	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		savedInstanceState.putString("dv", tv_dv.getText().toString());
		tv_dv.setText(savedInstanceState.getString("dv"));

		et_primern.setText(savedInstanceState.getString("primern"));
		et_segundon.setText(savedInstanceState.getString("segundon"));
		et_primera.setText(savedInstanceState.getString("primera"));
		et_segundoa.setText(savedInstanceState.getString("segundoa"));
		et_razon_social.setText(savedInstanceState.getString("razon_social"));
		id_tipo_tercero = savedInstanceState.getString("id_tipo_tercero");
		bt_tipo_tercero.setText(savedInstanceState.getString("tipo_tercero"));
		id_clasificacion = savedInstanceState.getString("id_clasificacion");
		bt_clasificacion.setText(savedInstanceState.getString("clasificacion"));
		et_ganap.setText(savedInstanceState.getString("ganap"));

		/*
		 * Correo Electronico
		 */
		et_correo_electronico[0].setText(savedInstanceState.getString("correo_electronico1"));
		et_correo_electronico[1].setText(savedInstanceState.getString("correo_electronico2"));
		et_correo_electronico[2].setText(savedInstanceState.getString("correo_electronico3"));
		et_correo_electronico[3].setText(savedInstanceState.getString("correo_electronico4"));
		et_correo_electronico[4].setText(savedInstanceState.getString("correo_electronico5"));

		int l = 0;
		while (l < 5 && !"".equals(et_correo_electronico[l].getText().toString())) {
			System.out.println("Lo que tienen el array: " + et_correo_electronico[l].getText().toString());
			System.out.println("Lo que tienen el equal: " + getString(R.string.correo_electronico));
			System.out.println("Resultado " + !et_correo_electronico[l].getText().toString().equals(getString(R.string.correo_electronico)));
			et_correo_electronico[l].setVisibility(View.VISIBLE);
			l++;
		}

		/*
		 * Direccion
		 */
		id_departamento[0] = savedInstanceState.getString("id_departamento1");
		id_departamento[1] = savedInstanceState.getString("id_departamento2");
		id_departamento[2] = savedInstanceState.getString("id_departamento3");
		bt_departamento[0].setText(savedInstanceState.getString("departamento1"));
		bt_departamento[1].setText(savedInstanceState.getString("departamento2"));
		bt_departamento[2].setText(savedInstanceState.getString("departamento3"));
		id_ciudad[0] = savedInstanceState.getString("id_ciudad1");
		id_ciudad[0] = savedInstanceState.getString("id_ciudad2");
		id_ciudad[0] = savedInstanceState.getString("id_ciudad3");
		bt_ciudad[0].setText(savedInstanceState.getString("ciudad1"));
		bt_ciudad[1].setText(savedInstanceState.getString("ciudad2"));
		bt_ciudad[2].setText(savedInstanceState.getString("ciudad3"));
		et_direccion[0].setText(savedInstanceState.getString("direccion1"));
		et_direccion[1].setText(savedInstanceState.getString("direccion2"));
		et_direccion[2].setText(savedInstanceState.getString("direccion3"));

		/*
		 * Telefono
		 */
		id_tipo_tel[0] = savedInstanceState.getString("id_tipo_tel1");
		id_tipo_tel[1] = savedInstanceState.getString("id_tipo_tel2");
		id_tipo_tel[2] = savedInstanceState.getString("id_tipo_tel3");
		bt_tipo_tel[0].setText(savedInstanceState.getString("tipo_tel1"));
		bt_tipo_tel[1].setText(savedInstanceState.getString("tipo_tel2"));
		bt_tipo_tel[2].setText(savedInstanceState.getString("tipo_tel3"));
		et_numero[0].setText(savedInstanceState.getString("numero1"));
		et_numero[1].setText(savedInstanceState.getString("numero2"));
		et_numero[2].setText(savedInstanceState.getString("numero3"));

		/*
		 * Informacion contable
		 */
		id_regimen = savedInstanceState.getString("id_regimen");
		bt_regimen.setText(savedInstanceState.getString("regimen"));
		id_actividad = savedInstanceState.getString("id_actividad_economica");
		bt_actividad.setText(savedInstanceState.getString("actividad"));
		id_lista_precios = savedInstanceState.getString("id_lista_precios");
		bt_lista_precios.setText(savedInstanceState.getString("lista_precios"));
		et_dias_credito.setText(savedInstanceState.getString("dias_credito"));
		et_cupom.setText(savedInstanceState.getString("cupo_max"));
		id_estado = savedInstanceState.getString("id_estado");
		bt_estado.setText(savedInstanceState.getString("estado"));
		/*
		 * Informacion bancaria
		 */
		id_cuenta_bancaria[0] = savedInstanceState.getString("id_cuenta_bancaria1");
		id_cuenta_bancaria[1] = savedInstanceState.getString("id_cuenta_bancaria2");
		id_cuenta_bancaria[2] = savedInstanceState.getString("id_cuenta_bancaria3");
		et_descripcion_banco[0].setText(savedInstanceState.getString("descripcion_cuenta1"));
		et_descripcion_banco[1].setText(savedInstanceState.getString("descripcion_cuenta2"));
		et_descripcion_banco[2].setText(savedInstanceState.getString("descripcion_cuenta3"));
		et_numero_cuenta[0].setText(savedInstanceState.getString("numero_cuenta1"));
		et_numero_cuenta[1].setText(savedInstanceState.getString("numero_cuenta2"));
		et_numero_cuenta[2].setText(savedInstanceState.getString("numero_cuenta3"));
		id_tipo_cuenta[0] = savedInstanceState.getString("id_tipo_cuenta1");
		id_tipo_cuenta[1] = savedInstanceState.getString("id_tipo_cuenta2");
		id_tipo_cuenta[2] = savedInstanceState.getString("id_tipo_cuenta3");
		bt_tipo_cuenta[0].setText(savedInstanceState.getString("tipo_cuenta1"));
		bt_tipo_cuenta[1].setText(savedInstanceState.getString("tipo_cuenta2"));
		bt_tipo_cuenta[2].setText(savedInstanceState.getString("tipo_cuenta3"));
		bt_banco[0].setText(savedInstanceState.getString("banco1"));
		bt_banco[1].setText(savedInstanceState.getString("banco2"));
		bt_banco[2].setText(savedInstanceState.getString("banco3"));
		id_departamento_banco[0] = savedInstanceState.getString("id_departamento_banco1");
		id_departamento_banco[1] = savedInstanceState.getString("id_departamento_banco2");
		id_departamento_banco[2] = savedInstanceState.getString("id_departamento_banco3");
		bt_departamento_banco[0].setText(savedInstanceState.getString("departamento_banco1"));
		bt_departamento_banco[1].setText(savedInstanceState.getString("departamento_banco2"));
		bt_departamento_banco[2].setText(savedInstanceState.getString("departamento_banco3"));
		id_ciudad_banco[0] = savedInstanceState.getString("id_ciudad_banco1");
		id_ciudad_banco[1] = savedInstanceState.getString("id_ciudad_banco2");
		id_ciudad_banco[2] = savedInstanceState.getString("id_ciudad_banco3");
		bt_ciudad_banco[0].setText(savedInstanceState.getString("ciudad_banco1"));
		bt_ciudad_banco[1].setText(savedInstanceState.getString("ciudad_banco2"));
		bt_ciudad_banco[2].setText(savedInstanceState.getString("ciudad_banco3"));

		/*
		 * Marcas
		 */
		id_marcas[0] = savedInstanceState.getString("id_marca1");
		id_marcas[1] = savedInstanceState.getString("id_marca2");
		id_marcas[2] = savedInstanceState.getString("id_marca3");
		id_marcas[3] = savedInstanceState.getString("id_marca4");
		id_marcas[4] = savedInstanceState.getString("id_marca5");
		id_marcas[5] = savedInstanceState.getString("id_marca6");
		id_marcas[6] = savedInstanceState.getString("id_marca7");
		id_marcas[7] = savedInstanceState.getString("id_marca8");
		id_marcas[8] = savedInstanceState.getString("id_marca9");
		id_marcas[9] = savedInstanceState.getString("id_marca10");
		bt_marcas[0].setText(savedInstanceState.getString("marca1"));
		bt_marcas[1].setText(savedInstanceState.getString("marca2"));
		bt_marcas[2].setText(savedInstanceState.getString("marca3"));
		bt_marcas[3].setText(savedInstanceState.getString("marca4"));
		bt_marcas[4].setText(savedInstanceState.getString("marca5"));
		bt_marcas[5].setText(savedInstanceState.getString("marca6"));
		bt_marcas[6].setText(savedInstanceState.getString("marca7"));
		bt_marcas[7].setText(savedInstanceState.getString("marca8"));
		bt_marcas[8].setText(savedInstanceState.getString("marca9"));
		bt_marcas[9].setText(savedInstanceState.getString("marca10"));

		if (id_tipo_tercero != null) {
			if (id_tipo_tercero.equals("2")) {
				et_razon_social.setVisibility(View.VISIBLE);
				et_primern.setVisibility(View.GONE);
				et_segundon.setVisibility(View.GONE);
				et_primera.setVisibility(View.GONE);
				et_segundoa.setVisibility(View.GONE);
			} else {
				et_razon_social.setVisibility(View.GONE);
				et_primern.setVisibility(View.VISIBLE);
				et_segundon.setVisibility(View.VISIBLE);
				et_primera.setVisibility(View.VISIBLE);
				et_segundoa.setVisibility(View.VISIBLE);
			}
		}
		if (id_clasificacion != null && id_clasificacion.equals("4")) {
			int k = 0;
			tv_info_aso_marcas.setVisibility(View.VISIBLE);
			while (k < 10 && bt_marcas[k] != null && !bt_marcas[k].getText().equals(getString(R.string.marcas))) {
				bt_marcas[k].setVisibility(View.VISIBLE);
				k++;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.persons, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.ib_barcode) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
			startActivityForResult(intent, 0);
		} else if (v.getId() == R.id.bt_tipo_tercero) {
			selected = new SelectedDataDialog(R.id.bt_tipo_tercero, getString(R.string.stercero), "MVSEL0049", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.stercero));
		} else if (v.getId() == R.id.bt_tipo_cuenta) {
			selected = new SelectedDataDialog(R.id.bt_tipo_cuenta, getString(R.string.stipocuenta), "MVSEL0057", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.stipocuenta));
		} else if (v.getId() == R.id.bt_tipo_tel) {
			selected = new SelectedDataDialog(R.id.bt_tipo_tel, getString(R.string.stipotel), "MVSEL0052", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.stipotel));
		} else if (v.getId() == R.id.bt_regimen) {
			selected = new SelectedDataDialog(R.id.bt_regimen, getString(R.string.sregimen), "MVSEL0053", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sregimen));
		} else if (v.getId() == R.id.bt_actividad) {
			selected = new SelectedDataDialog(R.id.bt_actividad, getString(R.string.sactividad), "MVSEL0054", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sactividad));
		} else if (v.getId() == R.id.bt_lista_precios) {
			selected = new SelectedDataDialog(R.id.bt_lista_precios, getString(R.string.slistaprecios), "MVSEL0055", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.slistaprecios));
		} else if (v.getId() == R.id.bt_estado) {
			selected = new SelectedDataDialog(R.id.bt_estado, getString(R.string.sestado), "MVSEL0056", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sestado));
		} else if (v.getId() == R.id.bt_banco) {
			selected = new SelectedDataDialog(R.id.bt_banco, getString(R.string.sbanco), "MVSEL0058", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sbanco));
		} else if (v.getId() == R.id.bt_clasificacion) {
			selected = new SelectedDataDialog(R.id.bt_clasificacion, getString(R.string.sclasificacion), "MVSEL0059", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sclasificacion));
		} else if (v.getId() == R.id.bt_marcas) {
			selected = new SelectedDataDialog(R.id.bt_marcas, getString(R.string.smarcas), "MVSEL0060", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.smarcas));
		} else if (v.getId() == R.id.bt_departamento) {
			selected = new SelectedDataDialog(R.id.bt_departamento, getString(R.string.sdepartamento), "MVSEL0050", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sdepartamento));
		} else if (v.getId() == R.id.bt_ciudad) {
			if (id_departamento[0] != null) {
				selected = new SelectedDataDialog(R.id.bt_ciudad, getString(R.string.sciudad), "MVSEL0051", new String[] { id_departamento[0] });
				selected.addDialogClickListener(this);
				selected.show(getFragmentManager(), getString(R.string.sciudad));
			} else {
				Toast toast = Toast.makeText(this, getString(R.string.edepto), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();
			}
		} else if (v.getId() == R.id.bt_departamento_banco) {
			selected = new SelectedDataDialog(R.id.bt_departamento_banco, getString(R.string.sdepartamento), "MVSEL0050", null);
			selected.addDialogClickListener(this);
			selected.show(getFragmentManager(), getString(R.string.sdepartamento));
		} else if (v.getId() == R.id.bt_ciudad_banco) {
			if (id_departamento_banco[0] != null) {
				selected = new SelectedDataDialog(R.id.bt_ciudad_banco, getString(R.string.sciudad), "MVSEL0051", new String[] { id_departamento_banco[0] });
				selected.addDialogClickListener(this);
				selected.show(getFragmentManager(), getString(R.string.sciudad));
			} else {
				Toast toast = Toast.makeText(this, getString(R.string.edepto), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();
			}
		} else if (v.getId() == R.id.ib_buscar_tercero) {
			search = new SearchDataDialog(getString(R.string.buscar_tercero), "MVSEL0061", null, R.id.ib_buscar_tercero);
			search.addDialogClickListener(this);
			search.show(getFragmentManager(), getString(R.string.buscar_tercero));
		} else if (v.getId() == R.id.ib_guardar_tercero) {
			if (!et_id_char.getText().toString().equals("")) {
				System.out.println(":::"+bt_tipo_tercero.getText()+" equals "+getString(R.string.tipo_tercero));
				//if (!bt_tipo_tercero.getText().equals(getString(R.string.tipo_tercero))) {
					System.out.println("Validacion: tipo tercero" + id_tipo_tercero + " primern" + et_primern.getText().toString() + "Primer Ape " + et_primera.getText().toString());
					if (id_tipo_tercero!=null) { 
						if ((id_tipo_tercero.equals("2") && !et_razon_social.getText().toString().equals("")) || (!id_tipo_tercero.equals("2") && !et_primern.getText().toString().equals("") && !et_primera.getText().toString().equals(""))) {
							if (!bt_clasificacion.getText().equals(getString(R.string.clasificacion))) {
								if (!bt_regimen.getText().equals(getString(R.string.regimen))) {
									if (!bt_actividad.getText().equals(getString(R.string.actividad))) {
										if (!bt_lista_precios.getText().equals(getString(R.string.lista_precios))) {
											if (!bt_estado.getText().equals(getString(R.string.estado_tercero))) {
												ib_guardar_tercero.setEnabled(false);
												sendNewTransaction();
											} else {
												Toast.makeText(this, R.string.error_estado, Toast.LENGTH_LONG).show();
											}
										} else {
											Toast.makeText(this, R.string.error_lista_precios, Toast.LENGTH_LONG).show();
										}
									} else {
										Toast.makeText(this, R.string.error_actividad, Toast.LENGTH_LONG).show();
									}
								} else {
									Toast.makeText(this, R.string.error_regimen, Toast.LENGTH_LONG).show();
								}
							} else {
								Toast.makeText(this, R.string.error_clasificacion, Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(this, R.string.error_dtercero,Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(this, R.string.error_tipo_tercero, Toast.LENGTH_LONG).show();
					}
				//}
			} else {
				System.out.println("me fui por el else");
				Toast.makeText(this, R.string.error_id_char, Toast.LENGTH_LONG).show();
			}
		}
		else if (v.getId() == R.id.ib_modificar_tercero) {
			if (!et_id_char.getText().toString().equals("")) {
				if (!bt_tipo_tercero.getText().equals(getString(R.string.tipo_tercero))) {
					System.out.println("Validacion: tipo tercero" + id_tipo_tercero + " primern" + et_primern.getText().toString() + "Primer Ape " + et_primera.getText().toString());
					if ((id_tipo_tercero.equals("2") && !et_razon_social.getText().toString().equals("")) || (!id_tipo_tercero.equals("2") && !et_primern.getText().toString().equals("") && !et_primera.getText().toString().equals(""))) {
						if (!bt_clasificacion.getText().equals(getString(R.string.clasificacion))) {
							if (!bt_regimen.getText().equals(getString(R.string.regimen))) {
								if (!bt_actividad.getText().equals(getString(R.string.actividad))) {
									if (!bt_lista_precios.getText().equals(getString(R.string.lista_precios))) {
										if (!bt_estado.getText().equals(getString(R.string.estado_tercero))) {
											sendEditTransaction();
										} else {
											Toast.makeText(this, R.string.error_estado, Toast.LENGTH_LONG).show();
										}
									} else {
										Toast.makeText(this, R.string.error_lista_precios, Toast.LENGTH_LONG).show();
									}
								} else {
									Toast.makeText(this, R.string.error_actividad, Toast.LENGTH_LONG).show();
								}
							} else {
								Toast.makeText(this, R.string.error_regimen, Toast.LENGTH_LONG).show();
							}
						} else {
							Toast.makeText(this, R.string.error_clasificacion, Toast.LENGTH_LONG).show();
						}
					} else {
						Toast.makeText(this, R.string.error_tipo_tercero, Toast.LENGTH_LONG).show();
					}
				}
			} else {
				Toast.makeText(this, R.string.error_id_char, Toast.LENGTH_LONG).show();
			}
		}
	}

	private void sendNewTransaction() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00016");
		Element id = new Element("id");
		id.setText("TMV4");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);

		/*
		 * General
		 */

		Element general = new Element("package");
		Element id_char = new Element("field").setText(et_id_char.getText().toString());
		Attribute akey = new Attribute("attribute", "key");
		id_char.setAttribute(akey);
		Element dv = new Element("field").setText(tv_dv.getText().toString());
		Element n1 = new Element("field").setText(et_primern.getText().toString());
		Element n2 = new Element("field").setText(et_segundon.getText().toString());
		Element a1 = new Element("field").setText(et_primera.getText().toString());
		Element a2 = new Element("field").setText(et_segundoa.getText().toString());
		Element rs = new Element("field").setText(et_razon_social.getText().toString());
		Element cs = new Element("field").setText(id_tipo_tercero);

		general.addContent(id_char);
		general.addContent(dv);
		general.addContent(n1);
		general.addContent(n2);
		general.addContent(a1);
		general.addContent(a2);
		general.addContent(rs);
		general.addContent(cs);
		raiz.addContent(general);

		/*
		 * Perfil Tercero
		 */

		Element ptercero = new Element("package");

		Element eid_regimen = new Element("field").setText(id_regimen);
		Element eid_actividad = new Element("field").setText(id_actividad);
		Element eid_catalogo = new Element("field").setText(id_lista_precios);
		Element eid_estado = new Element("field").setText(id_estado);

		ptercero.addContent(eid_regimen);
		ptercero.addContent(eid_actividad);
		ptercero.addContent(eid_catalogo);
		ptercero.addContent(eid_estado);
		raiz.addContent(ptercero);

		/*
		 * Propiedad Tercero
		 */

		Element propiedad = new Element("package");
		Element sbpack = new Element("subpackage");

		Element id_propiedad = new Element("field").setText(id_tipo_tercero);

		sbpack.addContent(id_propiedad);
		propiedad.addContent(sbpack);
		raiz.addContent(propiedad);

		/*
		 * Perfiles
		 */

		Element perfiles = new Element("package");

		Element id_perfil = new Element("field").setText("002");

		perfiles.addContent(id_perfil);
		raiz.addContent(perfiles);

		/*
		 * Cuentas Bancarias
		 */

		Element cbancarias = new Element("package");

		for (int i = 0; i < et_descripcion_banco.length; i++) {
			if (!et_descripcion_banco[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element dbanco = new Element("field").setText(et_descripcion_banco[i].getText().toString());
				Element cbancaria = new Element("field").setText(et_numero_cuenta[i].getText().toString());
				Element tipo_ctab = new Element("field").setText(id_tipo_cuenta[i]);
				Element banco = new Element("field").setText(id_banco[i]);
				Element id_dep = new Element("field").setText(id_departamento_banco[i]);
				Element id_mun = new Element("field").setText(id_ciudad_banco[i]);
				spack.addContent(dbanco);
				spack.addContent(cbancaria);
				spack.addContent(tipo_ctab);
				spack.addContent(banco);
				spack.addContent(id_dep);
				spack.addContent(id_mun);
				cbancarias.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(cbancarias);

		/*
		 * Direcciones
		 */

		Element direcciones = new Element("package");

		for (int i = 0; i < et_direccion.length; i++) {
			if (!et_direccion[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element descd = new Element("field").setText("Ingreso por Movil");
				Element direccion = new Element("field").setText(et_direccion[i].getText().toString());
				Element id_dep = new Element("field").setText(id_departamento[i]);
				Element id_mun = new Element("field").setText(id_ciudad[i]);
				spack.addContent(descd);
				spack.addContent(direccion);
				spack.addContent(id_dep);
				spack.addContent(id_mun);
				direcciones.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(direcciones);

		/*
		 * Telefonos
		 */

		Element telefonos = new Element("package");

		for (int i = 0; i < et_numero.length; i++) {
			if (!et_numero[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element desct = new Element("field").setText("Ingreso por Movil");
				Element numero = new Element("field").setText(et_numero[i].getText().toString());
				Element id_dep = new Element("field").setText(id_departamento[0]);
				Element id_mun = new Element("field").setText(id_ciudad[0]);
				Element id_tipo = new Element("field").setText(id_tipo_tel[0]);
				spack.addContent(desct);
				spack.addContent(numero);
				spack.addContent(id_dep);
				spack.addContent(id_mun);
				spack.addContent(id_tipo);
				telefonos.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(telefonos);

		/*
		 * Correos Electronicos
		 */

		Element correos = new Element("package");

		for (int i = 0; i < et_correo_electronico.length; i++) {
			if (!et_correo_electronico[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element desce = new Element("field").setText("Ingreso por Movil");
				Element direccion_ce = new Element("field").setText(et_correo_electronico[i].getText().toString());

				spack.addContent(desce);
				spack.addContent(direccion_ce);
				correos.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(correos);

		/*
		 * Marcas
		 */

		Element marcas = new Element("package");

		for (int i = 0; i < bt_marcas.length; i++) {
			if (!bt_marcas[i].getText().toString().equals(getString(R.string.marcas))) {
				Element spack = new Element("subpackage");
				Element id_marca = new Element("field").setText(id_marcas[i]);
				spack.addContent(id_marca);
				marcas.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(marcas);

		/*
		 * Informacion de creditos
		 */

		Element info_credito = new Element("package");

		Element dcredito = new Element("field").setText(et_dias_credito.getText().toString());
		Element cupo = new Element("field").setText(et_cupom.getText().toString());

		if (!et_dias_credito.getText().toString().equals("") && !et_cupom.getText().toString().equals("")) {
			info_credito.addContent(dcredito);
			info_credito.addContent(cupo);
		}
		raiz.addContent(info_credito);

		/*
		 * Gana puntos
		 */

		Element ganap = new Element("package");
		String sganap = et_ganap.getText().toString().equals("")?"NULL":et_ganap.getText().toString();
		Element cod_ganap = new Element("field").setText(sganap);

		ganap.addContent(cod_ganap);
		raiz.addContent(ganap);

		SocketChannel socket = SocketConnector.getSock();
		Log.d("EMAKU", "EMAKU: Socket: " + socket);
		SocketWriter.writing(socket, transaction);

	}

	private void sendEditTransaction() {
		Document transaction = new Document();
		Element raiz = new Element("TRANSACTION");
		Element driver = new Element("driver");
		driver.setText("MVTR00017");
		Element id = new Element("id");
		id.setText("TMV5");
		transaction.setRootElement(raiz);
		raiz.addContent(driver);
		raiz.addContent(id);

		/*
		 * General
		 */

		Element general = new Element("package");
		Element id_char = new Element("field").setText(et_id_char.getText().toString());
		Element dv = new Element("field").setText(tv_dv.getText().toString());
		Element n1 = new Element("field").setText(et_primern.getText().toString());
		Element n2 = new Element("field").setText(et_segundon.getText().toString());
		Element a1 = new Element("field").setText(et_primera.getText().toString());
		Element a2 = new Element("field").setText(et_segundoa.getText().toString());
		Element rs = new Element("field").setText(et_razon_social.getText().toString());
		Element cs = new Element("field").setText(id_tipo_tercero);
		Element id_char2 = new Element("field").setText(et_id_char.getText().toString());
		Attribute akey = new Attribute("attribute", "key");
		id_char2.setAttribute(akey);

		general.addContent(id_char);
		general.addContent(dv);
		general.addContent(n1);
		general.addContent(n2);
		general.addContent(a1);
		general.addContent(a2);
		general.addContent(rs);
		general.addContent(cs);
		general.addContent(id_char2);
		raiz.addContent(general);

		/*
		 * Informacion de creditos
		 */

		Element info_credito = new Element("package");

		Element dcredito = new Element("field").setText(et_dias_credito.getText().toString());
		Attribute afkey1 = new Attribute("attribute", "finalKey");
		dcredito.setAttribute(afkey1);
		Element cupo = new Element("field").setText(et_cupom.getText().toString());
		Attribute afkey2 = new Attribute("attribute", "finalKey");
		cupo.setAttribute(afkey2);
		
		if (!et_dias_credito.getText().toString().equals("") && !et_cupom.getText().toString().equals("")) {
			info_credito.addContent(dcredito);
			info_credito.addContent(cupo);
		}
		raiz.addContent(info_credito);

		/*
		 * Blanco1
		 */
		
		Element blanco1 = new Element("package");
		Element fblanco = new Element("field");
		blanco1.addContent(fblanco);
		raiz.addContent(blanco1);
		
		/*
		 * Gana puntos
		 */

		Element ganap = new Element("package");
		String sganap = et_ganap.getText().toString().equals("")?"NULL":et_ganap.getText().toString();
		Element cod_ganap = new Element("field").setText(sganap);

		ganap.addContent(cod_ganap);
		raiz.addContent(ganap);

		
		/*
		 * Blanco2
		 */
		
		Element blanco2 = new Element("package");
		Element fblanco2 = new Element("field");
		blanco2.addContent(fblanco2);
		raiz.addContent(blanco2);

		/*
		 * Perfil Tercero
		 */

		Element ptercero = new Element("package");

		Element eid_regimen = new Element("field").setText(id_regimen);
		Element eid_actividad = new Element("field").setText(id_actividad);
		Element eid_catalogo = new Element("field").setText(id_lista_precios);
		Element eid_estado = new Element("field").setText(id_estado);

		ptercero.addContent(eid_regimen);
		ptercero.addContent(eid_actividad);
		ptercero.addContent(eid_catalogo);
		ptercero.addContent(eid_estado);
		raiz.addContent(ptercero);

		/*
		 * Blanco3
		 */
		
		Element blanco3 = new Element("package");
		Element fblanco3 = new Element("field");
		blanco3.addContent(fblanco3);
		raiz.addContent(blanco3);

		/*
		 * Propiedad Tercero
		 */

		Element propiedad = new Element("package");
		Element sbpack = new Element("subpackage");

		Element id_propiedad = new Element("field").setText(id_tipo_tercero);

		sbpack.addContent(id_propiedad);
		propiedad.addContent(sbpack);
		raiz.addContent(propiedad);


		/*
		 * Blanco4
		 */
		
		Element blanco4 = new Element("package");
		Element fblanco4 = new Element("field");
		blanco4.addContent(fblanco4);
		raiz.addContent(blanco4);

		/*
		 * Cuentas Bancarias
		 */

		Element cbancarias = new Element("package");

		for (int i = 0; i < et_descripcion_banco.length; i++) {
			if (!et_descripcion_banco[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element dbanco = new Element("field").setText(et_descripcion_banco[i].getText().toString());
				Element cbancaria = new Element("field").setText(et_numero_cuenta[i].getText().toString());
				Element tipo_ctab = new Element("field").setText(id_tipo_cuenta[i]);
				Element banco = new Element("field").setText(id_banco[i]);
				Element id_dep = new Element("field").setText(id_departamento_banco[i]);
				Element id_mun = new Element("field").setText(id_ciudad_banco[i]);
				spack.addContent(dbanco);
				spack.addContent(cbancaria);
				spack.addContent(tipo_ctab);
				spack.addContent(banco);
				spack.addContent(id_dep);
				spack.addContent(id_mun);
				cbancarias.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(cbancarias);

		/*
		 * Blanco5
		 */
		
		Element blanco5 = new Element("package");
		Element fblanco5 = new Element("field");
		blanco5.addContent(fblanco5);
		raiz.addContent(blanco5);

		/*
		 * Direcciones
		 */

		Element direcciones = new Element("package");

		for (int i = 0; i < et_direccion.length; i++) {
			if (!et_direccion[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element descd = new Element("field").setText("Ingreso por Movil");
				Element direccion = new Element("field").setText(et_direccion[i].getText().toString());
				Element id_dep = new Element("field").setText(id_departamento[i]);
				Element id_mun = new Element("field").setText(id_ciudad[i]);
				spack.addContent(descd);
				spack.addContent(direccion);
				spack.addContent(id_dep);
				spack.addContent(id_mun);
				direcciones.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(direcciones);

		/*
		 * Blanco6
		 */
		
		Element blanco6 = new Element("package");
		Element fblanco6 = new Element("field");
		blanco6.addContent(fblanco6);
		raiz.addContent(blanco6);

		/*
		 * Telefonos
		 */

		Element telefonos = new Element("package");

		for (int i = 0; i < et_numero.length; i++) {
			if (!et_numero[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element desct = new Element("field").setText("Ingreso por Movil");
				Element numero = new Element("field").setText(et_numero[i].getText().toString());
				Element id_dep = new Element("field").setText(id_departamento[0]);
				Element id_mun = new Element("field").setText(id_ciudad[0]);
				Element id_tipo = new Element("field").setText(id_tipo_tel[0]);
				spack.addContent(desct);
				spack.addContent(numero);
				spack.addContent(id_dep);
				spack.addContent(id_mun);
				spack.addContent(id_tipo);
				telefonos.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(telefonos);

		/*
		 * Blanco7
		 */
		
		Element blanco7 = new Element("package");
		Element fblanco7 = new Element("field");
		blanco7.addContent(fblanco7);
		raiz.addContent(blanco7);

		/*
		 * Correos Electronicos
		 */

		Element correos = new Element("package");

		for (int i = 0; i < et_correo_electronico.length; i++) {
			if (!et_correo_electronico[i].getText().toString().equals("")) {
				Element spack = new Element("subpackage");
				Element desce = new Element("field").setText("Ingreso por Movil");
				Element direccion_ce = new Element("field").setText(et_correo_electronico[i].getText().toString());

				spack.addContent(desce);
				spack.addContent(direccion_ce);
				correos.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(correos);

		/*
		 * Blanco8
		 */
		
		Element blanco8 = new Element("package");
		Element fblanco8 = new Element("field");
		blanco8.addContent(fblanco8);
		raiz.addContent(blanco8);

		/*
		 * Marcas
		 */

		Element marcas = new Element("package");

		for (int i = 0; i < bt_marcas.length; i++) {
			if (!bt_marcas[i].getText().toString().equals(getString(R.string.marcas))) {
				Element spack = new Element("subpackage");
				Element id_marca = new Element("field").setText(id_marcas[i]);
				spack.addContent(id_marca);
				marcas.addContent(spack);
			} else {
				break;
			}
		}
		raiz.addContent(marcas);



		SocketChannel socket = SocketConnector.getSock();
		Log.d("EMAKU", "EMAKU: Socket: " + socket);
		SocketWriter.writing(socket, transaction);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("EMAKU", " ResultCode: " + resultCode + " requestCode: " + requestCode);
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				// Handle successful scan
				et_ganap.setText(contents);
			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Toast toast = Toast.makeText(this, getString(R.string.ecancel), Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 25, 400);
				toast.show();
			}
		}
	}

	@Override
	public void dialogClickEvent(DialogClickEvent e) {
		// TODO Auto-generated method stub
		if (e.getIdobject() == R.id.bt_tipo_tercero) {
			bt_tipo_tercero.setText(e.getValue());
			id_tipo_tercero = e.getId();

			if (e.getId().equals("2")) {
				et_razon_social.setVisibility(View.VISIBLE);
				et_primern.setVisibility(View.GONE);
				et_segundon.setVisibility(View.GONE);
				et_primera.setVisibility(View.GONE);
				et_segundoa.setVisibility(View.GONE);
			} else {
				et_razon_social.setVisibility(View.GONE);
				et_primern.setVisibility(View.VISIBLE);
				et_segundon.setVisibility(View.VISIBLE);
				et_primera.setVisibility(View.VISIBLE);
				et_segundoa.setVisibility(View.VISIBLE);
			}
		} else if (e.getIdobject() == R.id.bt_departamento) {
			bt_departamento[0].setText(e.getValue());
			id_departamento[0] = e.getId();
			bt_ciudad[0].setText(getString(R.string.ciudad));
			id_ciudad[0] = null;
		} else if (e.getIdobject() == R.id.bt_departamento_dos) {
			bt_departamento[1].setText(e.getValue());
			id_departamento[1] = e.getId();
			bt_ciudad[1].setText(getString(R.string.ciudad_dos));
			id_ciudad[1] = null;
		} else if (e.getIdobject() == R.id.bt_departamento_tres) {
			bt_departamento[2].setText(e.getValue());
			id_departamento[2] = e.getId();
			bt_ciudad[2].setText(getString(R.string.ciudad_tres));
			id_ciudad[2] = null;
		} else if (e.getIdobject() == R.id.bt_departamento_banco) {
			bt_departamento_banco[0].setText(e.getValue());
			id_departamento_banco[0] = e.getId();
			bt_ciudad_banco[0].setText(getString(R.string.ciudad_banco));
			id_ciudad_banco[0] = null;
		} else if (e.getIdobject() == R.id.bt_ciudad) {
			bt_ciudad[0].setText(e.getValue());
			id_ciudad[0] = e.getId();
		} else if (e.getIdobject() == R.id.bt_ciudad_dos) {
			bt_ciudad[1].setText(e.getValue());
			id_ciudad[1] = e.getId();
		} else if (e.getIdobject() == R.id.bt_ciudad_tres) {
			bt_ciudad[2].setText(e.getValue());
			id_ciudad[2] = e.getId();
		} else if (e.getIdobject() == R.id.bt_ciudad_banco) {
			bt_ciudad_banco[0].setText(e.getValue());
			id_ciudad_banco[0] = e.getId();
		} else if (e.getIdobject() == R.id.bt_tipo_tel) {
			bt_tipo_tel[0].setText(e.getValue());
			id_tipo_tel[0] = e.getId();
		} else if (e.getIdobject() == R.id.bt_tipo_tel_dos) {
			bt_tipo_tel[1].setText(e.getValue());
			id_tipo_tel[1] = e.getId();
		} else if (e.getIdobject() == R.id.bt_tipo_tel_tres) {
			bt_tipo_tel[2].setText(e.getValue());
			id_tipo_tel[2] = e.getId();
		} else if (e.getIdobject() == R.id.bt_regimen) {
			bt_regimen.setText(e.getValue());
			id_regimen = e.getId();
		} else if (e.getIdobject() == R.id.bt_actividad) {
			bt_actividad.setText(e.getValue());
			id_actividad = e.getId();
		} else if (e.getIdobject() == R.id.bt_lista_precios) {
			bt_lista_precios.setText(e.getValue());
			id_lista_precios = e.getId();
		} else if (e.getIdobject() == R.id.bt_estado) {
			bt_estado.setText(e.getValue());
			id_estado = e.getId();
		} else if (e.getIdobject() == R.id.bt_tipo_cuenta) {
			bt_tipo_cuenta[0].setText(e.getValue());
			id_tipo_cuenta[0] = e.getId();
		} else if (R.id.ib_buscar_tercero == e.getIdobject()) {
			if (e.getValue() != null) {
				et_id_char.setText(e.getValue());
				id_buscar_tercero = e.getId();
				new SearchQuery(PersonsActivity.this, "MVSEL0062", new String[] { id_buscar_tercero }).start();
				new SearchQuery(PersonsActivity.this, "MVSEL0063", new String[] { id_buscar_tercero }).start();
				new SearchQuery(PersonsActivity.this, "MVSEL0064", new String[] { id_buscar_tercero }).start();
				new SearchQuery(PersonsActivity.this, "MVSEL0065", new String[] { id_buscar_tercero }).start();
				new SearchQuery(PersonsActivity.this, "MVSEL0066", new String[] { id_buscar_tercero }).start();
				new SearchQuery(PersonsActivity.this, "MVSEL0073", new String[] { id_buscar_tercero }).start();
				new SearchQuery(PersonsActivity.this, "MVSEL0074", new String[] { id_buscar_tercero }).start();
			}
		} else if (e.getIdobject() == R.id.bt_banco) {
			bt_banco[0].setText(e.getValue());
			id_banco[0] = e.getId();
		} else if (e.getIdobject() == R.id.bt_clasificacion) {
			bt_clasificacion.setText(e.getValue());
			id_clasificacion = e.getId();
			if (e.getId().equals("4")) {
				bt_marcas[0].setVisibility(View.VISIBLE);
				tv_info_aso_marcas.setVisibility(View.VISIBLE);
			} else {
				bt_marcas[0].setVisibility(View.GONE);
				tv_info_aso_marcas.setVisibility(View.GONE);
			}
		} else if (e.getIdobject() == R.id.bt_marcas) {
			bt_marcas[0].setText(e.getValue());
			id_marcas[0] = e.getId();
		}

	}

	@Override
	public void arriveAnswerEvent(AnswerEvent e) {
		// TODO Auto-generated method stub
		Document doc = e.getDocument();
		final Element elm = doc.getRootElement();
		if (e.getSqlCode().equals("MVSEL0062") || e.getSqlCode().equals("MVSEL0067")) {
			Iterator<Element> i = elm.getChildren("row").iterator();
			while (i.hasNext()) {
				System.out.println("iterator con datos");
				Element row = (Element) i.next();
				Iterator<Element> j = row.getChildren().iterator();

				final String nombre1 = ((Element) j.next()).getValue();
				final String nombre2 = ((Element) j.next()).getValue();
				final String apellido1 = ((Element) j.next()).getValue();
				final String apellido2 = ((Element) j.next()).getValue();
				final String razon_social = ((Element) j.next()).getValue();
				id_tipo_tercero = ((Element) j.next()).getValue();
				final String naturaleza_tercero = ((Element) j.next()).getValue();
				id_clasificacion = ((Element) j.next()).getValue();
				final String tipo_tercero = ((Element) j.next()).getValue();
				id_regimen = ((Element) j.next()).getValue();
				final String regimen = ((Element) j.next()).getValue();
				final String ganap = ((Element) j.next()).getValue();
				id_actividad = ((Element) j.next()).getValue();
				;
				final String actividad_economica = ((Element) j.next()).getValue();
				id_lista_precios = ((Element) j.next()).getValue();
				;
				final String lista_precios = ((Element) j.next()).getValue();
				final String dias_credito = ((Element) j.next()).getValue();
				final String cupo_maximo = ((Element) j.next()).getValue();
				id_estado = ((Element) j.next()).getValue();
				final String des_estado = ((Element) j.next()).getValue();

				this.runOnUiThread(new Runnable() {
					public void run() {
						et_primern.setText(nombre1);
						et_segundon.setText(nombre2);
						et_primera.setText(apellido1);
						et_segundoa.setText(apellido2);
						et_razon_social.setText(razon_social);
						bt_tipo_tercero.setText(tipo_tercero);
						bt_clasificacion.setText(naturaleza_tercero);
						bt_regimen.setText(regimen);
						et_ganap.setText(ganap);
						bt_actividad.setText(actividad_economica);
						bt_lista_precios.setText(lista_precios);
						et_dias_credito.setText(dias_credito);
						et_cupom.setText(cupo_maximo);
						bt_estado.setText(des_estado);
						
						if ("".equals(regimen)) {
							ib_guardar_tercero.setVisibility(View.VISIBLE);
							ib_modificar_tercero.setVisibility(View.GONE);
						}
						else {
							ib_guardar_tercero.setVisibility(View.GONE);
							ib_modificar_tercero.setVisibility(View.VISIBLE);
						}
						
						if (id_tipo_tercero.equals("2")) {
							et_razon_social.setVisibility(View.VISIBLE);
							et_primern.setVisibility(View.GONE);
							et_segundon.setVisibility(View.GONE);
							et_primera.setVisibility(View.GONE);
							et_segundoa.setVisibility(View.GONE);
						} else {
							et_razon_social.setVisibility(View.GONE);
							et_primern.setVisibility(View.VISIBLE);
							et_segundon.setVisibility(View.VISIBLE);
							et_primera.setVisibility(View.VISIBLE);
							et_segundoa.setVisibility(View.VISIBLE);
						}

						if (id_clasificacion.equals("4")) {
							bt_marcas[0].setVisibility(View.VISIBLE);
							tv_info_aso_marcas.setVisibility(View.VISIBLE);
						} else {
							bt_marcas[0].setVisibility(View.GONE);
							tv_info_aso_marcas.setVisibility(View.GONE);
						}

					}
				});
			}
		} else if (e.getSqlCode().equals("MVSEL0063") || e.getSqlCode().equals("MVSEL0068")) {
			final Iterator<Element> i = elm.getChildren("row").iterator();
			this.runOnUiThread(new Runnable() {
				public void run() {

					int k = 0;
					while (i.hasNext()) {
						System.out.println("iterator con datos");
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();

						String direccion = ((Element) j.next()).getValue();
						id_departamento[k] = ((Element) j.next()).getValue();
						String departamento = ((Element) j.next()).getValue();
						id_ciudad[k] = ((Element) j.next()).getValue();
						String ciudad = ((Element) j.next()).getValue();

						et_direccion[k].setText(direccion);
						bt_departamento[k].setText(departamento);
						bt_ciudad[k].setText(ciudad);

						ly_direccion[k].setVisibility(View.VISIBLE);
						et_direccion[k].setVisibility(View.VISIBLE);
						bt_departamento[k].setVisibility(View.VISIBLE);
						bt_ciudad[k].setVisibility(View.VISIBLE);
						k++;
					}
				}
			});

		} else if (e.getSqlCode().equals("MVSEL0064") || e.getSqlCode().equals("MVSEL0069")) {
			final Iterator<Element> i = elm.getChildren("row").iterator();

			this.runOnUiThread(new Runnable() {
				public void run() {

					int k = 0;
					while (i.hasNext()) {
						System.out.println("iterator con datos");
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();

						id_tipo_tel[k] = ((Element) j.next()).getValue();
						String tipo_tel = ((Element) j.next()).getValue();
						String numero = ((Element) j.next()).getValue();

						bt_tipo_tel[k].setText(tipo_tel);
						et_numero[k].setText(numero);

						ly_telefono[k].setVisibility(View.VISIBLE);
						et_numero[k].setVisibility(View.VISIBLE);
						k++;
					}
				}
			});

		} else if (e.getSqlCode().equals("MVSEL0065") || e.getSqlCode().equals("MVSEL0070")) {
			final Iterator<Element> i = elm.getChildren("row").iterator();
			this.runOnUiThread(new Runnable() {
				public void run() {

					int k = 0;
					while (i.hasNext()) {
						System.out.println("iterator con datos");
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();

						id_marcas[k] = ((Element) j.next()).getValue();
						String marca = ((Element) j.next()).getValue();

						bt_marcas[k].setText(marca);

						bt_marcas[k].setVisibility(View.VISIBLE);
						k++;
					}
				}
			});

		} else if (e.getSqlCode().equals("MVSEL0066") || e.getSqlCode().equals("MVSEL0071")) {
			final Iterator<Element> i = elm.getChildren("row").iterator();
			this.runOnUiThread(new Runnable() {
				public void run() {

					int k = 0;
					while (i.hasNext()) {
						System.out.println("iterator con datos");
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();

						id_cuenta_bancaria[k] = ((Element) j.next()).getValue();
						String descripcion_banco = ((Element) j.next()).getValue();

						String numero_cuenta = ((Element) j.next()).getValue();
						id_tipo_cuenta[k] = ((Element) j.next()).getValue();
						String tipo_cuenta = ((Element) j.next()).getValue();
						String banco = ((Element) j.next()).getValue();
						String departamento_banco = ((Element) j.next()).getValue();

						et_descripcion_banco[k].setText(descripcion_banco);
						et_numero_cuenta[k].setText(numero_cuenta);
						bt_tipo_cuenta[k].setText(tipo_cuenta);
						bt_banco[k].setText(banco);
						bt_departamento_banco[k].setText(departamento_banco);
						bt_ciudad_banco[k].setText(departamento_banco);

						et_descripcion_banco[k].setVisibility(View.VISIBLE);
						et_numero_cuenta[k].setVisibility(View.VISIBLE);
						bt_tipo_cuenta[k].setVisibility(View.VISIBLE);
						bt_banco[k].setVisibility(View.VISIBLE);
						bt_departamento_banco[k].setVisibility(View.VISIBLE);
						bt_ciudad_banco[k].setVisibility(View.VISIBLE);

						k++;
					}
				}
			});
		} else if (e.getSqlCode().equals("MVSEL0072") || e.getSqlCode().equals("MVSEL0073")) {
			final Iterator<Element> i = elm.getChildren("row").iterator();
			this.runOnUiThread(new Runnable() {
				public void run() {
					while (i.hasNext()) {
						System.out.println("iterator con datos");
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();
						String dv = ((Element) j.next()).getValue();
						tv_dv.setText(dv);
					}
				}
			});
		} else if (e.getSqlCode().equals("MVSEL0074") || e.getSqlCode().equals("MVSEL0075")) {
			final Iterator<Element> i = elm.getChildren("row").iterator();
			this.runOnUiThread(new Runnable() {
				public void run() {

					int k = 0;
					while (i.hasNext()) {
						Element row = (Element) i.next();
						Iterator<Element> j = row.getChildren().iterator();

						id_correo_electronico[k] = ((Element) j.next()).getValue();
						String dir_correo_electronico = ((Element) j.next()).getValue();

						et_correo_electronico[k].setText(dir_correo_electronico);

						et_correo_electronico[k].setVisibility(View.VISIBLE);
						k++;
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

	@Override
	public void onFocusChange(View arg0, boolean arg1) {
		// TODO Auto-generated method stub
		System.out.println("Di un enter");
		if (arg0.getId() == R.id.et_id_char) {
			String id_char = et_id_char.getText().toString();
			new SearchQuery(PersonsActivity.this, "MVSEL0067", new String[] { id_char }).start();
			new SearchQuery(PersonsActivity.this, "MVSEL0068", new String[] { id_char }).start();
			new SearchQuery(PersonsActivity.this, "MVSEL0069", new String[] { id_char }).start();
			new SearchQuery(PersonsActivity.this, "MVSEL0070", new String[] { id_char }).start();
			new SearchQuery(PersonsActivity.this, "MVSEL0071", new String[] { id_char }).start();
			new SearchQuery(PersonsActivity.this, "MVSEL0072", new String[] { id_char }).start();
			new SearchQuery(PersonsActivity.this, "MVSEL0075", new String[] { id_char }).start();
		}
	}

}
