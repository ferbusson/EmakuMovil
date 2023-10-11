package com.example.emakumovil.communications;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Hashtable;
import java.util.Iterator;

public class InfoSocket extends Thread {
	
    private static Hashtable <SocketChannel,InfoSocket>Hchannelclients = new Hashtable<SocketChannel,InfoSocket>();
    private static int SocketsCount = 0;


	    private boolean loged = false;
	    private String bd;
	    private String login;
	    private Socket sock;
	    private ByteArrayOutputStream buffTmp;
	    
	    /**
	     * @param sock
	     */
	    public InfoSocket(Socket sock) {
	        this.sock = sock;
	        buffTmp = new ByteArrayOutputStream();
	        start();
	    }

	    /**
	     * 
	     */
	    public void run() {
	        try {
	            Thread.sleep(5000);
	            if (!isLoged()) {
	                removeSock(sock.getChannel());
	            }
	        }
	        catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    
	    /**
	     * Este metodo sirve para saber si una conexion ha sido autenticada
	     * @param sock
	     *            Socket al que se valida
	     * @return Retorna true si el socket a sido autenticado, de lo contrario
	     *         retorna false
	     */
	    public static boolean isLoged(SocketChannel sock) {
	        return Hchannelclients.get(sock).isLoged();
	    }
	    
	    /**
	     * Este metodo retorna 
	     * @param sock
	     * @return algo
	     */
	    public static String getLoging(SocketChannel sock) {
	        return Hchannelclients.get(sock).getLogin();
	    }
	    
	    /**
	     * Este metodo retorna unicamente el numero de socket's
	     * conectados
	     * @return algo
	     */
	    public static int getSocketsCount() {
	        return SocketsCount;
	    }

	    /**
	     * Este metodo Incrementa el contador de socket's
	     * conectados 
	     * @return El numero de socket's conectados.
	     */
	    public static int setIncrementSocketsCount() {
	        return ++SocketsCount;
	    }

	    /**
	     * Este metodo decrementa el contador de socket's
	     * conectados 
	     * @return El numero de socket's conectados.
	     */
	    public static int setDecrementSocketsCount() {
	        return --SocketsCount;
	    }
	    
	    public static Hashtable<SocketChannel,InfoSocket> getHchannelclients() {
	        return Hchannelclients;
	    }
	    
	    public static Iterator<SocketChannel> getSocketKeys() {
	    	Iterator<SocketChannel> skeys = Hchannelclients.keySet().iterator();
	    	return skeys;
	    }

	    public static String getCompanyNameKey(SocketChannel sock) {
	        return "K-"+ getBd(sock) + "-company";
	    }
	    
	    public static String getCompanyIDKey(SocketChannel sock) {
	        return "K-"+ getBd(sock) + "-companyID";    
	    }

	    /**
	     * Este metodo remueve una coneccion (socket) de la
	     * hash de conecciones.
	     * @param sock Socket que se quiere remover.
	     * @throws IOException
	     */
	    public static void removeSock(SocketChannel sock) throws IOException {
	        setDecrementSocketsCount();
	        sock.close();
	        Hchannelclients.remove(sock);
	    }

	    public static ByteArrayOutputStream getBufferTmp(SocketChannel sock) {
	        return Hchannelclients.get(sock).getBuffTmp();
	    }

	    public static void setBufferTmp(SocketChannel sock,ByteArrayOutputStream buffTmp) {
	        Hchannelclients.get(sock).setBuffTmp(buffTmp);
	    }
	    
	    /**
	     * Este metodo retorna la base de datos un socket
	     * @param sock Socket del cual se quiere obtener la informacion
	     * @return El nombre de la base de datos
	     */
	    public static String getBd(SocketChannel sock){
	    	return Hchannelclients.get(sock).getBd();
	    	
	    }

	    /**
	     * Este metodo actualiza el valor de las conexiones logeadas
	     * @param sock
	     * @param bd
	     */

	    public static void setLogin(SocketChannel sock, String bd, String login) {
	        Hchannelclients.get(sock).setLoged();
	        Hchannelclients.get(sock).setBd(bd);
	        Hchannelclients.get(sock).setLogin(login);
	    }


	    /**
	     * Este metodo se utiliza para verificar si una
	     * coneccion ha sido autenticada.
	     * @return <code><b>true</b></code> si la conexion esta autenticada,
	     * de lo contrario <code><b>false</b></code> 
	     */
	    public boolean isLoged() {
	        return loged;
	    }

	    /**
	     * Este metodo se encarga de pasar el estado
	     * de no autenticado a autenticado.
	     */
	    public void setLoged() {
	        this.loged = true;
	    }

	    /**
	     * Este metodo se encarga de retornar la
	     * base de datos a la que la coneccion(socket) hace
	     * referencia.
	     * @return Nombre de la base de datos
	     */
	    public String getBd() {
	        return bd;
	    }

	    /**
	     * Este metodo establece la base de datos
	     * para la coneccion(socket).
	     * @param bd Nombre de la base de datos.
	     */
	    public void setBd(String bd) {
	        this.bd = bd;
	    }

	    /**
	     * Este metodo se encarga de retornar
	     * el nombre del usuario referente a la coneccion(socket).
	     * @return Nombre de usuario
	     */
	    public String getLogin() {
	        return login;
	    }

	    public static void put(SocketChannel sock,InfoSocket value) {
	    	Hchannelclients.put(sock, value);
	    }
	    /**
	     * Este metodo se encarga de retornar el nombre
	     * de usuario de una coneccion(socket).
	     * @param login Nombre de usuario.
	     */
	    public void setLogin(String login) {
	        this.login = login;
	    }
	    
	    public ByteArrayOutputStream getBuffTmp() {
	        return buffTmp;
	    }
	    
	    public void setBuffTmp(ByteArrayOutputStream buffTmp) {
	        this.buffTmp = buffTmp;
	    }

	}