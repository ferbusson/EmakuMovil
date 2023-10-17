package com.example.emakumovil.control;

import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.emakumovil.MainActivity;
import com.example.emakumovil.MenuActivity;

import com.example.emakumovil.communications.ArrivedPackageEvent;
import com.example.emakumovil.communications.ArrivedPackageListener;
import com.example.emakumovil.communications.PingPackage;
import com.example.emakumovil.communications.SocketConnector;
import com.example.emakumovil.misc.parameters.EmakuParametersStructure;
import com.example.emakumovil.misc.settings.ConfigFileHandler;

/**
 * ClientHeaderValidator.java Creado el 22-jul-2004
 * 
 * Este archivo es parte de JMClient <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * JMClient es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase se encarga de Validar las cabeceras (raices) de los paquetes XML
 * que llegan al Servidor de transacciones. <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */

public class HeadersValidator implements ArrivedPackageListener {

    private static Element root;
    private MainActivity main;

    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     * @param doc
     *            Documento a validar
     */

    public HeadersValidator(MainActivity main){
    	this.main=main;
    	new ClientHeaderValidator(main);
    }
    
    public void validPackage(ArrivedPackageEvent APe) {
    	
    	Document doc = APe.getDoc();

        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        root = doc.getRootElement();
        String nombre = root.getName();
        
        /*
         * Se validan las cabeceras genericas, que necesitan acceso desde el paquete
         * common
         */
        
        try {
        	
			if (!ClientHeaderValidator.validGeneral(doc)) {
			    /*
			     *  Validacion paquete ACP
			     */
			    if(nombre.equals("ACPBegin")) {
			    	/*
			    	 * Si este paquete llega aqui se debe cambiar de Layout
			    	 */

			        /*
			         * Cargando configuración dependiendo de la empresa ...
			         */
			        String dataBase = EmakuParametersStructure.getParameter("dataBase");
			        ConfigFileHandler.loadJarFile(dataBase);

			        Log.d("EMAKU","EMAKU info: Usuario Logeado");
			    }
			    
			    else if(nombre.equals("ACPZip")) {
			    	ACPHandler.ACPData(doc);
			        main.runOnUiThread(new Runnable() {
			            public void run() {
			            	PingPackage ping = new PingPackage();
			            	ping.start();
			            	 Intent i = new Intent(main, MenuActivity.class );
			                 main.startActivity(i);
			            	
			            }
			        });
			    }
			    else if(nombre.equals("ACPFAILURE")) {
			        final String message = root.getChildText("message");
			        main.runOnUiThread(new Runnable() {
			            public void run() {
							Toast.makeText(main,
									"Error: "+message,Toast.LENGTH_LONG).show();
					        //main.enableButton();
			            }
			        });
			        Log.d("EMAKU","EMAKU: "+message);
			        try {
						SocketConnector.getSock().close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			    }
			    
			    
			    /*
			     *  Si no corresponde a ninguno de los paquetes anteriores es porque 
			     *  el formato del protocolo esta errado.
			     */
			    
			    else {
			        System.out.println("Error en el formato del protocolo");
			        	XMLOutputter out = new XMLOutputter();
			        	out.setFormat(Format.getPrettyFormat());
			        	try {
							out.output(doc,System.out);
						} catch (IOException e) {
							e.printStackTrace();
						}

			        }
			}
		} catch (ErrorMessageException e) {
			// TODO Auto-generated catch block
			displayError();
		}
    }
    
    private void displayError() {
    	final String msg = root.getChild("errorMsg").getText();
        main.runOnUiThread(new Runnable() {
            public void run() {
        		Toast.makeText(main,
        				"Error: "+msg,Toast.LENGTH_LONG).show();
            }
        });
    	Log.d("EMAKU","EMAKU: error: "+root.getChild("errorMsg").getText());
    }
}