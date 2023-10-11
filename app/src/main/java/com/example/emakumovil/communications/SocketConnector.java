package com.example.emakumovil.communications;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Iterator;

/**
 * SocketConnector.java Creado el 29-jul-2004
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
 * Esta clase es la encargada de conectar el cliente con el servidor
 * de transacciones.
 * <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class SocketConnector extends Thread {

	//private static Hashtable Hsockets = new Hashtable();
    private static SocketChannel socket;
    private static PackageToXML packageXML;
    private static String database ;
    private static String user;
    private static String password;
    
    public static String getDatabase() {
		return database;
	}

	public static void setDatabase(String database) {
		SocketConnector.database = database;
	}


	public static String getPassword() {
		return password;
	}

	public static void setPassword(String password) {
		SocketConnector.password = password;
	}

	public static String getUser() {
		return user;
	}

	public static void setUser(String user) {
		SocketConnector.user = user;
	}


	/**
     * Este constructor inicializa el hilo de conexion al
     * servidor de transacciones.
     * @param host
     * @param port
     */
    
    public SocketConnector(String host,int port,PackageToXML packageXML) 
    throws UnresolvedAddressException, IOException{
    	this.setName("SocketConnector");
    	SocketConnector.packageXML=packageXML;
        socket = SocketChannel.open();
        socket.configureBlocking(false);
        InetSocketAddress addr = new InetSocketAddress(host,port);

        socket.connect(addr);
  //  	Hsockets.put(key,socket);
        
//    	new PackageToXML();
        /*
         * Se intenta establecer una conexion con el servidor,
         * si en 5 segundos esta no es posible, se lanza la 
         * excepcion ConnectException
         */

        int k=0;
        while(!socket.finishConnect()){
        		try {
        			//System.out.print(".");
        			Thread.sleep(100);
        			k++;
        			if (k==50) {
        				throw new ConnectException();
    				}
        		}
        		catch(InterruptedException IEe) {
        			IEe.printStackTrace();
        		}
        }
    	
    }
    
    public void run() {
        try {
            
            Selector selector = Selector.open();
            socket.register(selector, SelectionKey.OP_READ);

            while (true) {

                int n = selector.select();
                if (n > 0) {
                    Iterator<SelectionKey> iterador = selector.selectedKeys().iterator();
                    while (iterador.hasNext()) {

                        SelectionKey clave = (SelectionKey) iterador.next();
                        iterador.remove();
                        
                        if (clave.isReadable()) {
                        	packageXML.work(socket);
                        } else if(clave.isValid()){
                            socket.close();
                            socket=null;
                            return;
                        }
                    }
                }
            }
        }
        catch (NotYetConnectedException e) {
            //e.printStackTrace();
        	System.out.println("Reconectando por perdida...");
        	SocketWriter.reconect();
        }
        catch (IOException e) {
            //e.printStackTrace();
        	System.out.println("Reconectando por error io...");
        	SocketWriter.reconect();
        }
    }
    
    public static SocketChannel getSock() {
        return socket;
    }
    
    public static boolean isConcected(){
//    	SocketChannel socket = (SocketChannel)Hsockets.get(key);
        return socket.isConnected();
    }

	public static PackageToXML getPackageXML() {
		return packageXML;
	}
    
}
