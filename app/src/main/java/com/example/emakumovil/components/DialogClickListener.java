package com.example.emakumovil.components;

import java.util.EventListener;

/**
 * DialogClickListener.java Creado el Jueves Santo 28-Mar-2013
 * 
 * Este archivo es parte de E-Maku Movil
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
 * Clase necesaria para el manejo de eventos por click en boton aceptar de un
 * DialogFragment
 * <br>
 * @author <A href='mailto:felipe@gmail.com'>Luis Felipe Hernandez</A>
 */

public interface DialogClickListener extends EventListener {
	public void dialogClickEvent(DialogClickEvent e);
}
