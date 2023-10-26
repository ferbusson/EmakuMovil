package com.example.emakumovil.control;

import static androidx.core.content.FileProvider.getUriForFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;
import org.jdom2.output.Format;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.example.emakumovil.MainActivity;
import com.example.emakumovil.R;
import com.example.emakumovil.communications.ArrivedPackageEvent;
import com.example.emakumovil.communications.ArrivedPackageListener;
import com.example.emakumovil.communications.PingPackage;
import com.example.emakumovil.misc.utils.ZipHandler;
import com.example.emakumovil.modules.cartera.InformesCarteraActivity;
import com.example.emakumovil.transactions.Cache;
import com.example.emakumovil.transactions.TransactionServerResultSet;

import firebase.com.protolitewrapper.BuildConfig;


/**
 * ClientHeaderValidator.java Creado el 22-jul-2004
 * 
 * Este archivo es parte de E-Maku <A
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

public class ClientHeaderValidator implements  ArrivedPackageListener {

    private static Element raiz;
    private static Vector <DateListener>dateListener = new Vector<DateListener>();
    private static Vector <UpdateCodeListener>updateCodeListener = new Vector<UpdateCodeListener>();
    private static Vector <ReportListener> reportListener = new Vector<ReportListener>();
    private static Vector<SuccessListener> successListener = new Vector<SuccessListener>();
    private static Vector<ErrorListener> errorListener = new Vector<ErrorListener>();
    private static MainActivity main;

    /**
     * Este metodo se encarga de revisar toda las raices de los documentos que
     * llegan al servidor de transacciones.
     * 
     *            Documento a validar
     */

    public ClientHeaderValidator(){
    }

    public ClientHeaderValidator(MainActivity main){
    	ClientHeaderValidator.setMain(main);
    }
    
    private static void setMain(MainActivity main) {
    	ClientHeaderValidator.main=main;
    }
    
    public static boolean validGeneral(Document doc) throws ErrorMessageException {
        /*
         * Obtenemos la raiz del documento si el documento no tiene raiz
         */
        raiz = doc.getRootElement();
        String nombre = raiz.getName();
        
        XMLOutputter out = new XMLOutputter();
        out.setFormat(Format.getPrettyFormat());
/*        try {
        	if (!nombre.equals("ACPZip"))
        		out.output(raiz,System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/        
        /*
         *  Validacion paquete ANSWER 
         */
        if(nombre.equals("ANSWER")) {
        	Log.d("EMAKU","EMAKU: LLego una query");
            String id = raiz.getChildText("id");
            TransactionServerResultSet.putSpoolQuery(id,doc);
            return true;
        }
        
        /*
         * Validando paquete PONG
         */
        
        else if (nombre.equals("PONG")) {
        	PingPackage.removePing();
        	return true;
        }
        
        /*
         *  Validacion paquetes MESSAGE
         */
        
        else if(nombre.equals("MESSAGE")) {
            System.out.println("Recibi un paquete MESSAGE");
            return true;
        }
        
        /*
         *  Validación paquetes UPDATECODE
         */
        
        else if(nombre.equals("UPDATECODE")) {
			
            String key = raiz.getChildText("idDocument");
            String consecutive = raiz.getChildText("consecutive");
                       UpdateCodeEvent event = new UpdateCodeEvent(new ClientHeaderValidator(main),key,consecutive);
            notifyUpdateCode(event);
            return true;
        }
        
        /*
         *  Validación paquetes DATE
         */
        
        else if(nombre.equals("DATE")) {
            String systemDate = raiz.getChildText("systemDate");
            DateEvent event = new DateEvent(new ClientHeaderValidator(main),systemDate);
            notifyDate(event);
            return true;
        }
        else if(nombre.equals("PLAINREPORT") || nombre.equals("REPORT")) {
        	final Element element = raiz.getChild("data");
        	main.runOnUiThread(new Runnable() {
		            public void run() {
		            	try {
		            		Log.d("EMAKU","EMAKU: getFile"+main.getFilesDir());
		            		File path = Environment.getExternalStoragePublicDirectory(
		            	            Environment.DIRECTORY_DOWNLOADS);
		            		long name = System.currentTimeMillis();
		            	    File file = new File(path, name+".pdf");
							FileOutputStream fpdf = new FileOutputStream(file);
		            	    OutputStream ows = new BufferedOutputStream(fpdf);
				        	ZipHandler zip = new ZipHandler();
				        	byte [] bytesReport = zip.getDataDecode(element.getValue());
				        	Log.d("EMAKU","EMAKU: escribiendo informe");
				        	ows.write(bytesReport);
							ows.flush();
							ows.close(); 
							Log.d("EMAKU","EMAKU: Informe guardado");
							
							//Uri pathPDF = Uri.fromFile(file);
                            Uri pathPDF = FileProvider.getUriForFile(Objects.requireNonNull(main.getApplicationContext()),
                                    "com.example.emakumovil.provider",file);
                            System.out.println("pathPDF " + pathPDF);
			                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
			                pdfIntent.setDataAndType(pathPDF, "application/pdf");
			                pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

			                try
			                {
			                    main.startActivity(pdfIntent);
			                }
			                catch(ActivityNotFoundException e)
			                {
			                    Toast.makeText(main, "No existe un visor PDF", Toast.LENGTH_LONG).show(); 
			                }
		    			} catch (FileNotFoundException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			} catch (IOException e) {
		    				// TODO Auto-generated catch block
		    				e.printStackTrace();
		    			} catch (NullPointerException e) {
		    				e.printStackTrace();
		    				XMLOutputter out = new XMLOutputter();
		    		        out.setFormat(Format.getPrettyFormat());
		    		        try {
		    		        		out.output(raiz,System.out);
		    				} catch (IOException IEe) {
		    					// TODO Auto-generated catch block
		    					IEe.printStackTrace();
		    				}
		    			}
		            }
		        });
			return true;
        }        
        /*
         *  Validación paquetes SUCCESS
         */
        
        else if(nombre.equals("SUCCESS")) {
        	

            String id = raiz.getChildText("id");
            String ndocument = "";
            String message  = "";
            
            if ("Q".equals(id.substring(0,1))) {
                TransactionServerResultSet.putSpoolQuery(id,doc);
            }
            
            /*
             * si por el contrario fue una transaccion entonces ...
             */
            else if ("T".equals(id.substring(0,1))){
            	Element EsuccessMessage = raiz.getChild("successMessage");
            	Element Endocument = raiz.getChild("ndocument");
            	message = EsuccessMessage.getText();
            	ndocument = Endocument!=null?Endocument.getValue():"";
        	}
            SuccessEvent event = new SuccessEvent(new ClientHeaderValidator(main),id,ndocument,message);

            notifySuccess(event);
	        main.runOnUiThread(new Runnable() {
	            public void run() {
            		Toast.makeText(main, "Transaccion Exitosa", Toast.LENGTH_LONG).show();
	            }});
	        System.gc();
            return true;
        }
        /*
         *  Validación paquetes CACHE-ANSWER
         */
        
        else if (nombre.equals("CACHE-ANSWER")) {
            new Cache(doc);
            return true;
        }
        /*
         *  Validacion paquetes de Error
         */
        
        else if(nombre.equals("ERROR")) {
            String id = raiz.getChildText("id");
            /*
             * Si existe id quiere decir que una consulta o una transaccion
             * retorno error
             */
/*	        	XMLOutputter out = new XMLOutputter();
        	out.setFormat(Format.getPrettyFormat());
        	try {
				out.output(doc,System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
*/
            String ndocument = "";
            String message  = "";
            
            if(id!=null && "Q".equals(id.substring(0,1))) {
                /*
                 * si fue una consulta entonces ...
                 */
                TransactionServerResultSet.putSpoolQuery(id,doc);
            }
            /*
             * si por el contrario fue una transaccion entonces ...
             */
            else if (id!=null && "T".equals(id.substring(0,1))){
            	Element EerrorMessage = raiz.getChild("errorMessage");
            	Element Endocument = raiz.getChild("ndocument");
            	message = EerrorMessage!=null?EerrorMessage.getText():"";
            	ndocument = Endocument!=null?Endocument.getValue():"";
            	System.out.println("numero del documento: "+ndocument+" id transaccion: "+id);
        	}
            ErrorEvent event = new ErrorEvent(new ClientHeaderValidator(main),id,ndocument,message);
            notifyError(event);
			throw new ErrorMessageException(id);
        }
        
        return false;
    }
    
    public static void addReportListener(ReportListener listener ) {
        reportListener.add(listener);
    }

	public static void removeReportListener(ReportListener listener ) {
        reportListener.add(listener);
    }
	

    public static void addDateListener(DateListener listener ) {
        dateListener.addElement(listener);
    }

    public static void removeDateListener(DateListener listener ) {
        dateListener.removeElement(listener);
    }

    private static void notifyDate(DateEvent event) {
    	synchronized (dateListener) {
	    	for (DateListener l : dateListener) {
	    		if (l!=null) {
	    			l.cathDateEvent(event);
	    		}
	    		else {
	    			dateListener.remove(l);
	    		}
	        }
    	}
    }

    public static void addUpdateCodeListener(UpdateCodeListener listener ) {
        updateCodeListener.addElement(listener);
    }

    public static void removeUpdateCodeListener(UpdateCodeListener listener ) {
        updateCodeListener.removeElement(listener);
    }

    private static void notifyUpdateCode(UpdateCodeEvent event) {
    	synchronized(updateCodeListener){
	        for (UpdateCodeListener l : updateCodeListener) {
	        	try {
	        		l.cathUpdateCodeEvent(event);
	        	}
	        	catch(NullPointerException NPEe) {
	        		System.out.println("UPs...");
	        	}
	        }
    	}
    }

    public static void addSuccessListener(SuccessListener listener ) {
        successListener.addElement(listener);
    }

    public static void removeSuccessListener(SuccessListener listener ) {
    	System.out.println("Eliminando ***************************************************");
        successListener.removeElement(listener);
    }

    public static void removeAllSuccessListener() {
    	successListener.clear();
    }
    
    private static void notifySuccess(SuccessEvent event) {
    	System.out.println("Notificar ***************************************************");
        for (SuccessListener l : successListener) {
        	System.out.println("notificando success a "+l);
            l.cathSuccesEvent(event);
        }
    }
    
    public static void addErrorListener(ErrorListener listener ) {
        errorListener.addElement(listener);
    }

    public static void removeErrorListener(ErrorListener listener ) {
        errorListener.removeElement(listener);
    }

    private static synchronized void notifyError(ErrorEvent event) {
        for (ErrorListener l : errorListener) {
            l.cathErrorEvent(event);
        }
    }
	public void validPackage(ArrivedPackageEvent APe) {
    	Document doc = APe.getDoc();

        /*
         * Se validan las cabeceras genericas, que necesitan acceso desde el paquete
         * common
         */
        
        try {
        	
			ClientHeaderValidator.validGeneral(doc);
		} catch (ErrorMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			displayError();
		}
		
	}
	
    private static void displayError() {
    	System.out.println("ERROR: "+raiz.getChild("errorMsg").getText());
    }
}