package com.example.emakumovil.control;

import java.util.EventObject;

/**
 * ErrorEvent.java Creado el 18-jun-2007
 * 
 * Este archivo es parte de E-Maku
 * <A href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net)</A>
 *
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General
 * GNU GPL como esta publicada por la Fundacion del Software Libre (FSF);
 * tanto en la version 2 de la licencia, o cualquier version posterior.
 *
 * E-Maku es distribuido con la expectativa de ser util, pero
 * SIN NINGUNA GARANTIA; sin ninguna garantia aun por COMERCIALIZACION
 * o por un PROPOSITO PARTICULAR. Consulte la Licencia Publica General
 * GNU GPL para mas detalles.
 * <br>
 * Se necesita que cualquier clase pueda saber cuando llega un paquete 
 * Success, para ello se crea esta clase la cual se encargara de generar
 * de forma de eventos la llegada de estos paquetes
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 */

public class ErrorEvent extends EventObject {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3881628462121920923L;
	private String idPackage;
    private String ndocument;
    private String message;
    
    public ErrorEvent(Object source, String idPackage,String ndocument,String message) {
        super(source);
        this.idPackage = idPackage;
        this.ndocument=ndocument;
        this.message=message;
    }

    
    /**
     * Asigna el idPackage al objeto
     */
    
    public synchronized String getIdPackage() {
        return idPackage;
    }
    
    /**
     * Retorna el idPackage del objeto
     * @param idPackage
     */
    
    public synchronized void setIdPackage(String idPackage) {
        this.idPackage = idPackage;
    }
    
    
    public synchronized String getNdocument() {
		return ndocument;
	}

	public String getMessage() {
		return message;
	}

}
