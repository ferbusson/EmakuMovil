package com.example.emakumovil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.emakumovil.components.SearchPersonActivity;
import com.example.emakumovil.control.ACPHandler;
import com.example.emakumovil.modules.arqueo.DaySalesActivity;
import com.example.emakumovil.modules.arqueo.QueryCashActivity;
import com.example.emakumovil.modules.cartera.CarteraCxPActivity;
import com.example.emakumovil.modules.cartera.InformesCXPActivity;
import com.example.emakumovil.modules.cartera.InformesCarteraActivity;
import com.example.emakumovil.modules.compras.PurchasingTemplateActivity;
import com.example.emakumovil.modules.documents.AnnulDocument;
import com.example.emakumovil.modules.inventario.FitInventoryActivity;
import com.example.emakumovil.modules.inventario.MasterProductActivity;
import com.example.emakumovil.modules.inventario.ScanningBarcodeActivity;
import com.example.emakumovil.modules.inventario.SearchCollectorActivity;
import com.example.emakumovil.modules.inventario.UploadPhotoActivity;
import com.example.emakumovil.modules.terceros.PersonsActivity;
import com.example.emakumovil.modules.ventas.ChangeResolutionActivity;
import com.example.emakumovil.modules.ventas.DespachoVehiculos;
import com.example.emakumovil.modules.ventas.TRMActivity;
import com.example.emakumovil.modules.ventas.TiqueteActivity;

public class MenuActivity extends Activity implements OnClickListener {


    /*
     * CAJA
     */

    private TextView tv_caja;
    private Button bt_caja;
    private Button bt_ventas;
    private static final String ARQUEOS = 		"MVTR00004";
    private static final String VENTAS = 		"MVTR00008";

    /*
     * TERCEROS
     */

    private TextView tv_terceros;
    private Button bt_ntercero;
    private static final String NUEVO_TERCERO = 		"MVTR00016";

    /*
     * TRM
     */

    private TextView tv_trm;
    private Button bt_atrm;
    private static final String ACTUALIZAR_TRM = 		"MVTR00018";

    /*
     * PRODUCTOS
     */

    private TextView tv_productos;
    private Button bt_fotos;
    private Button bt_maestro_productos;
    private static final String IMAGENES = 		"MVTR00005";
    private static final String MAESTRO = 		"MVTR00009";

    /*
     * VENTAS
     */

    private TextView tv_ventas;
    private Button bt_resolucion_facturacion;

    private Button bt_venta_tiquetes;

    private Button bt_despacho_vehiculos;
    private static final String RESOLUCION = "MVTR00020";

    private static final String TIQUETES = "MVTR00021";

    private static final String DESPACHOS = "MVTR00022";

    /*
     * COMPRAS
     */

    private TextView tv_compras;
    private Button bt_compra;
    private Button bt_plantilla_con_barra;
    private Button bt_plantilla_sin_barra;
    private static final String COMPRAS = 		"MVTR00013";
    private static final String PLANTILLA_CON_BARRA = 		"MVTR00014";
    private static final String PLANTILLA_SIN_BARRA = 		"MVTR00015";

    /*
     * INVENTARIOS
     */

    private TextView tv_inventarios;
    private Button bt_ajustes;
    private Button bt_recolector_productos;
    private Button bt_consulta_recolectores;
    private static final String AJUSTES = 		"MVTR00001";
    private static final String RECOLECTORES = 	"MVTR00002";
    private static final String CRECOLECTORES = "MVTR00010";

    /*
     * CARTERA
     */

    private TextView tv_cartera;
    private Button bt_cartera;
    private Button bt_informes_cartera;
    private static final String ABONO_CARTERA = 	"MVTR00006";
    private static final String INFORMES_CARTERA = 	"MVTR00011";

    /*
     * CXP
     */

    private TextView tv_cxp;
    private Button bt_cxp;
    private Button bt_informes_cxp;
    private static final String ABONO_CXP = 		"MVTR00007";
    private static final String INFORMES_CXP = 		"MVTR00012";

    /*
     * DOCUMENTOS
     */

    private TextView tv_documentos;
    private Button bt_anular_documentos;
    private static final String ANULAR_DOCUMENTOS = "MVTR00003";


    private static final int CARTERA = 0;
    private static final int CXP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        /*
         * CAJA
         */

        System.out.println("arqueos: "+ACPHandler.isContenedTransaction(ARQUEOS));
        System.out.println("ventas: "+ACPHandler.isContenedTransaction(VENTAS));
        System.out.println("documentos: "+ACPHandler.isContenedTransaction(ANULAR_DOCUMENTOS));

        if (ACPHandler.isContenedTransaction(ARQUEOS) || ACPHandler.isContenedTransaction(VENTAS)) {
            tv_caja = (TextView)findViewById(R.id.tv_caja);
            tv_caja.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(ARQUEOS)) {
            bt_caja = (Button)findViewById(R.id.caja);
            bt_caja.setVisibility(View.VISIBLE);
            bt_caja.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(VENTAS)) {
            bt_ventas = (Button)findViewById(R.id.bt_ventas);
            bt_ventas.setVisibility(View.VISIBLE);
            bt_ventas.setOnClickListener(this);
        }

        /*
         * TERCEROS
         */

        if (ACPHandler.isContenedTransaction(NUEVO_TERCERO)) {
            tv_terceros = (TextView)findViewById(R.id.tv_mterceros);
            tv_terceros.setVisibility(View.VISIBLE);
            bt_ntercero = (Button)findViewById(R.id.bt_ntercero);
            bt_ntercero.setVisibility(View.VISIBLE);
            bt_ntercero.setOnClickListener(this);
        }

        /*
         * TRM
         */

        if (ACPHandler.isContenedTransaction(ACTUALIZAR_TRM)) {
            tv_trm = (TextView)findViewById(R.id.tv_trm);
            tv_trm.setVisibility(View.VISIBLE);
            bt_atrm = (Button)findViewById(R.id.bt_atrm);
            bt_atrm.setVisibility(View.VISIBLE);
            bt_atrm.setOnClickListener(this);
        }

        /*
         * PRODUCTOS
         */


        if (ACPHandler.isContenedTransaction(IMAGENES) || ACPHandler.isContenedTransaction(MAESTRO)) {
            tv_productos = (TextView)findViewById(R.id.tv_productos);
            tv_productos.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(IMAGENES)) {
            bt_fotos = (Button)findViewById(R.id.imagenes);
            bt_fotos.setVisibility(View.VISIBLE);
            bt_fotos.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(MAESTRO)) {
            bt_maestro_productos = (Button)findViewById(R.id.bt_maestro);
            bt_maestro_productos.setVisibility(View.VISIBLE);
            bt_maestro_productos.setOnClickListener(this);
        }

        /*
         * VENTAS
         */


        if (ACPHandler.isContenedTransaction(RESOLUCION)) {
            tv_ventas = (TextView)findViewById(R.id.tv_ventas);
            tv_ventas.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(RESOLUCION)) {
            bt_resolucion_facturacion = (Button)findViewById(R.id.bt_resolucion_facturacion);
            bt_resolucion_facturacion.setVisibility(View.VISIBLE);
            bt_resolucion_facturacion.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(TIQUETES)){
            bt_venta_tiquetes = (Button)findViewById(R.id.bt_venta_tiquetes);
            bt_venta_tiquetes.setVisibility(View.VISIBLE);
            bt_venta_tiquetes.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(DESPACHOS)){
            bt_despacho_vehiculos = (Button)findViewById(R.id.bt_despacho_vehiculos);
            bt_despacho_vehiculos.setVisibility(View.VISIBLE);
            bt_despacho_vehiculos.setOnClickListener(this);
        }

        /*
         * COMPRAS
         */


        if (ACPHandler.isContenedTransaction(COMPRAS) ||
                ACPHandler.isContenedTransaction(PLANTILLA_CON_BARRA) ||
                ACPHandler.isContenedTransaction(PLANTILLA_SIN_BARRA)) {
            tv_compras = (TextView)findViewById(R.id.tv_compras);
            tv_compras.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(PLANTILLA_CON_BARRA)) {
            bt_compra = (Button)findViewById(R.id.bt_compra);
            bt_compra.setVisibility(View.VISIBLE);
            bt_compra.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(PLANTILLA_CON_BARRA)) {
            bt_plantilla_con_barra = (Button)findViewById(R.id.bt_plantilla_con_barra);
            bt_plantilla_con_barra.setVisibility(View.VISIBLE);
            bt_plantilla_con_barra.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(PLANTILLA_SIN_BARRA)) {
            bt_plantilla_sin_barra = (Button)findViewById(R.id.bt_plantilla_sin_barra);
            bt_plantilla_sin_barra.setVisibility(View.VISIBLE);
            bt_plantilla_sin_barra.setOnClickListener(this);
        }


        /*
         * INVENTARIOS
         */


        if (ACPHandler.isContenedTransaction(AJUSTES) ||
                ACPHandler.isContenedTransaction(RECOLECTORES) ||
                ACPHandler.isContenedTransaction(CRECOLECTORES)) {
            tv_inventarios = (TextView)findViewById(R.id.tv_inventarios);
            tv_inventarios.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(AJUSTES)) {
            bt_ajustes = (Button)findViewById(R.id.bt_ajustes);
            bt_ajustes.setVisibility(View.VISIBLE);
            bt_ajustes.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(RECOLECTORES)) {
            bt_recolector_productos = (Button)findViewById(R.id.bt_recolectores);
            bt_recolector_productos.setVisibility(View.VISIBLE);
            bt_recolector_productos.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(CRECOLECTORES)) {
            bt_consulta_recolectores = (Button)findViewById(R.id.bt_consultar_recolectores);
            bt_consulta_recolectores.setVisibility(View.VISIBLE);
            bt_consulta_recolectores.setOnClickListener(this);
        }

        /*
         * CARTERA
         */

        if (ACPHandler.isContenedTransaction(ABONO_CARTERA) || ACPHandler.isContenedTransaction(INFORMES_CARTERA)) {
            tv_cartera = (TextView)findViewById(R.id.tv_cartera);
            tv_cartera.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(ABONO_CARTERA)) {
            bt_cartera = (Button) findViewById(R.id.bt_abono_cartera);
            bt_cartera.setVisibility(View.VISIBLE);
            bt_cartera.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(INFORMES_CARTERA)) {
            bt_informes_cartera = (Button)findViewById(R.id.bt_informes_cartera);
            bt_informes_cartera.setVisibility(View.VISIBLE);
            bt_informes_cartera.setOnClickListener(this);
        }

        /*
         * CXP
         */

        if (ACPHandler.isContenedTransaction(ABONO_CXP) || ACPHandler.isContenedTransaction(INFORMES_CXP)) {
            tv_cxp = (TextView)findViewById(R.id.tv_cxp);
            tv_cxp.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(ABONO_CXP)) {
            bt_cxp = (Button) findViewById(R.id.bt_abono_cxp);
            bt_cxp.setVisibility(View.VISIBLE);
            bt_cxp.setOnClickListener(this);
        }

        if (ACPHandler.isContenedTransaction(INFORMES_CXP)) {
            bt_informes_cxp = (Button)findViewById(R.id.bt_informes_cxp);
            bt_informes_cxp.setVisibility(View.VISIBLE);
            bt_informes_cxp.setOnClickListener(this);
        }

        /*
         * DOCUMENTOS
         */

        if (ACPHandler.isContenedTransaction(ANULAR_DOCUMENTOS)) {
            tv_documentos = (TextView)findViewById(R.id.tv_documentos);
            tv_documentos.setVisibility(View.VISIBLE);
        }

        if (ACPHandler.isContenedTransaction(ANULAR_DOCUMENTOS)) {
            bt_anular_documentos = (Button)findViewById(R.id.bt_anular_documentos);
            bt_anular_documentos.setVisibility(View.VISIBLE);
            bt_anular_documentos.setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View arg0) {

        /*
         * CAJA
         */

        if (arg0.getId()==R.id.caja) {
            Intent i = new Intent(this,QueryCashActivity.class);
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_ventas) {
            Intent i = new Intent(this,DaySalesActivity.class);
            this.startActivity(i);
        }

        /*
         * TERCEROS
         */

        else if (arg0.getId()==R.id.bt_ntercero) {
            Intent i = new Intent(this, PersonsActivity.class );
            System.out.println("Nuevo tercero");
            this.startActivity(i);
        }

        /*
         * TRM
         */

        else if (arg0.getId()==R.id.bt_atrm) {
            Intent i = new Intent(this, TRMActivity.class );
            this.startActivity(i);
        }

        /*
         * PRODUCTOS
         */

        else if (arg0.getId()==R.id.imagenes) {
            Intent i = new Intent(this, UploadPhotoActivity.class );
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_maestro) {
            Intent i = new Intent(this,MasterProductActivity.class);
            this.startActivity(i);
        }

        /*
         * VENTAS
         */

        else if (arg0.getId()==R.id.bt_resolucion_facturacion) {
            Intent i = new Intent(this, ChangeResolutionActivity.class );
            this.startActivity(i);
        } else if (arg0.getId() == R.id.bt_venta_tiquetes) {
            Intent i = new Intent(this, TiqueteActivity.class);
            this.startActivity(i);
        } else if (arg0.getId() == R.id.bt_despacho_vehiculos) {
        Intent i = new Intent(this, DespachoVehiculos.class);
        this.startActivity(i);
    }



        /*
         * COMPRAS
         */

        else if (arg0.getId()==R.id.bt_compra) {
            Intent i = new Intent(this,SearchPersonActivity.class);
            i.putExtra("titulo",R.string.buscar_proveedor);
            i.putExtra("query","MVSEL0010");
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_plantilla_con_barra) {
            Intent i = new Intent(this,PurchasingTemplateActivity.class);
            i.putExtra("withBarcode",true);
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_plantilla_sin_barra) {
            Intent i = new Intent(this,PurchasingTemplateActivity.class);
            i.putExtra("withBarcode",false);
            this.startActivity(i);
        }

        /*
         * INVENTARIOS
         */

        else if (arg0.getId()==R.id.bt_ajustes) {
            Intent i = new Intent(this,FitInventoryActivity.class);
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_consultar_recolectores) {
            Intent i = new Intent(this,SearchCollectorActivity.class);
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_recolectores) {
            Intent i = new Intent(this,ScanningBarcodeActivity.class);
            this.startActivity(i);
        }

        /*
         * CARTERA
         */
        else if (arg0.getId()==R.id.bt_abono_cartera) {
            Intent i = new Intent(this, CarteraCxPActivity.class );
            i.putExtra("type",CARTERA);
            i.putExtra("titulo",getString(R.string.abono_cartera));
            i.putExtra("query","MVSEL0017");
            i.putExtra("listQuery","MVSEL0019");
            i.putExtra("titulo",getString(R.string.abono_cartera));
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_informes_cartera) {
            Intent i = new Intent(this,InformesCarteraActivity.class);
            this.startActivity(i);
        }

        /*
         * CXP
         */

        else if (arg0.getId()==R.id.bt_abono_cxp) {
            Intent i = new Intent(this, CarteraCxPActivity.class );
            i.putExtra("type",CXP);
            i.putExtra("titulo",getString(R.string.abono_cxp));
            i.putExtra("query","MVSEL0018");
            i.putExtra("listQuery","MVSEL0020");
            i.putExtra("titulo",getString(R.string.abono_cxp));
            this.startActivity(i);
        }
        else if (arg0.getId()==R.id.bt_informes_cxp) {
            Intent i = new Intent(this,InformesCXPActivity.class);
            this.startActivity(i);
        }

        /*
         * ANULAR DOCUMENTOS
         */

        else if (arg0.getId()==R.id.bt_anular_documentos) {
            Intent i = new Intent(this,AnnulDocument.class);
            this.startActivity(i);
        }
    }

}
