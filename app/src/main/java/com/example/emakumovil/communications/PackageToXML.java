package com.example.emakumovil.communications;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

/**
 * PackageToXML.java Creado el 05-oct-2004
 * 
 * Este archivo es parte de E-Maku <A
 * href="http://comunidad.qhatu.net">(http://comunidad.qhatu.net) </A>
 * 
 * E-Maku es Software Libre; usted puede redistribuirlo y/o realizar
 * modificaciones bajo los terminos de la Licencia Publica General GNU GPL como
 * esta publicada por la Fundacion del Software Libre (FSF); tanto en la version
 * 2 de la licencia, o cualquier version posterior.
 * 
 * E-Maku es distribuido con la expectativa de ser util, pero SIN NINGUNA
 * GARANTIA; sin ninguna garantia aun por COMERCIALIZACION o por un PROPOSITO
 * PARTICULAR. Consulte la Licencia Publica General GNU GPL para mas detalles.
 * <br>
 * Esta clase covierte el flujo de entrada de un socket en paquetes XML <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class PackageToXML {

    private static ByteArrayOutputStream bufferOut = null;
    private static Document doc;
    private ByteBuffer buf;
    private SAXBuilder builder;
    private Vector <ArrivedPackageListener>arrivePackageListener = new Vector<ArrivedPackageListener>();
    
    public PackageToXML() {
        bufferOut = new ByteArrayOutputStream();
    }


    public synchronized void work(SocketChannel socket) throws IOException, NotYetConnectedException {

        buf = ByteBuffer.allocateDirect(8192);
        int numRead = 1;
        System.out.println("pase por buf: " + buf.toString());

        while (numRead > 0) {
            System.out.println("entre al while numRead>0 valor: " + numRead);
            buf.rewind();

            numRead = socket.read(buf);

            System.out.println("bufferOut antes de entrar: " + bufferOut.toString("UTF-8"));
            buf.rewind();
                for (int i = 0; i < numRead; i++) {
                System.out.println(String.format("estoy en el for numRead: " +  numRead));
                int character = buf.get(i);
                if (character != 12) {
                    if (character!=0) {
                            if(character == 7) i = i+3;
                            else {
                                System.out.println(String.format("Escribiendo bufferOut.write...: " + character));
                                bufferOut.write(character);
                            }
                    }
                }
                else {
                    ByteArrayInputStream bufferIn = null;
                    builder = new SAXBuilder();
                    System.out.println("alimentando el bufferIn desde bufferOut: " + bufferOut.toString("UTF-8"));
                    bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
                    try {
                        System.out.println("bufferIn: " + bufferIn.toString());
						doc = builder.build(bufferIn);
					} catch (JDOMException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						FileOutputStream fos = new FileOutputStream("/tmp/jdomexception.txt");
						fos.write(bufferOut.toByteArray());
						fos.close();
					}
    	            ArrivedPackageEvent event = new ArrivedPackageEvent(this,doc);
    	            notifyArrivePackage(event);
                	bufferOut.close();
                	bufferOut = null;
                	bufferIn.close();
                	bufferIn = null;
                    bufferOut = new ByteArrayOutputStream();
                }

            }

            if (numRead == -1) {
                socket.close();
                socket = null;
                return;
            }
        }
    }

    public synchronized void addArrivePackageListener(ArrivedPackageListener listener ) {
        arrivePackageListener.addElement(listener);
    }

    public synchronized void removeSuccessListener(ArrivedPackageListener listener ) {
        arrivePackageListener.removeElement(listener);
    }

    private synchronized void notifyArrivePackage(ArrivedPackageEvent event) {
        for (int i=0; i<arrivePackageListener.size();i++) {
            ArrivedPackageListener listener = (ArrivedPackageListener)arrivePackageListener.elementAt(i);
            listener.validPackage(event);
        }
    }
}