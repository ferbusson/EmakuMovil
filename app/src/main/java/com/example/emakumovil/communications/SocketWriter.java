package com.example.emakumovil.communications;

import com.example.emakumovil.control.ClientHeaderValidator;
import com.example.emakumovil.misc.settings.ConfigFileHandler;
import com.example.emakumovil.misc.settings.ConfigFileNotLoadException;


import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;


/**
 * SocketWriter.java Creado el 23-jul-2004
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
 * Informacion de la clase <br>
 * 
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David
 *         Cepeda </A>
 */
public class SocketWriter {

    
	public static boolean writing(SocketChannel sock,Document doc) {
		return writing(false,sock,doc);
	}
	
    public static boolean writing(boolean serverData,SocketChannel sock,Document doc) {
    	if (sock!= null) {
	        synchronized(sock) {
		        try {
		            ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
		            
		            XMLOutputter xmlOutputter = new XMLOutputter();
		            //xmlOutputter.setFormat(Format.getPrettyFormat());
		            xmlOutputter.output(doc, bufferOut);
		            
		    	    //xmlOutputter.output(doc,System.out);
	
		            bufferOut.write(new String("\f").getBytes());
		            
		            ByteArrayInputStream bufferIn = new ByteArrayInputStream(bufferOut.toByteArray());
		            bufferOut.close();
		            bufferOut = null;
		            return sendBuffer(serverData,sock,bufferIn);
		        }
		        catch (FileNotFoundException e) {
		            e.printStackTrace();
		            return false;
		        }
		        catch (ClosedChannelException e) {
		        	if (serverData) {
		        		try {
							InfoSocket.removeSock(sock);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		        	}
		        	e.printStackTrace();
		        	return false;
		        }
		        catch (IOException e) {
		            e.printStackTrace();
		            return false;
		        }
	        }
    	}
    	else {
    		System.out.println("reconectando por socket null...");
    		reconect();
    		writing(serverData,SocketConnector.getSock(),doc);
    		return false;
    	}
    }
    
    public static void writing(boolean serverData,SocketChannel sock, String data)  {
        ByteArrayInputStream bufferIn = new ByteArrayInputStream(data.getBytes());
        sendBuffer(serverData,sock,bufferIn);
    }

    public static void writing(boolean serverData,SocketChannel sock, ByteArrayOutputStream data)  {
        ByteArrayInputStream bufferIn = new ByteArrayInputStream(data.toByteArray());
        sendBuffer(serverData,sock,bufferIn);
    }
    private static boolean sendBuffer(boolean serverData,SocketChannel sock,ByteArrayInputStream buffer) {
        try {
	        ByteBuffer buf = ByteBuffer.allocate(8192);
	        byte[] bytes = new byte[8192];
	
	        int count = 0;
	
	        while (count >= 0) {
	
	            buf.clear();
	            count = buffer.read(bytes);
	                
	            for (int i=0;i<count;i++) {
	                buf.put(bytes[i]);
	            }
	  
	            buf.flip();
	            //int cont =0;
	            while (buf.remaining()>0) { // && cont<10) {
		                if (serverData) {
		            		System.out.println("user: "+InfoSocket.getLoging(sock)+" data "+buf.remaining());
	            		}
		                sock.write(buf);
	            		if (serverData) {
	            			if (buf.remaining()>0) {
	            				try {
	            					System.out.print("*");
	            					Thread.sleep(150);
	            				}
	            				catch(InterruptedException e) {
	            					e.printStackTrace();
	            				}
	            			}
	            		}
/*	            			if (!lastBuf.equals(buf)) {
	            				cont ++;
	            				System.out.println("Se reintentara por "+cont+" vez la impresion de: "+lastBuf.remaining()+" datos");
	            				try {
	            					Thread.sleep(100);
	            				}
	            				catch(InterruptedException e) {
	            					e.printStackTrace();
	            				}
	            			} 
*/	            		
	            }
	            if (buf.remaining()>0) {
	            	System.out.println("Se cancelo escritura por problemas con el socket y se cerrara");
	            	InfoSocket.removeSock(sock);
	            	return false;
	            }
	                
	        }
	        return true;
        }
        catch (NotYetConnectedException NYCEe) {
        	System.out.println("El canal no tiene enlace de conexion");
        	NYCEe.printStackTrace();
        	return false;
        }
        catch(ClosedByInterruptException CBIEe) {
        	System.out.println("El canal fue interrumpido y se cerro mientras escribia");
        	CBIEe.printStackTrace();
        	return false;
        }
        catch (AsynchronousCloseException ACEe) {
        	System.out.println("El canal fue cerrado mientras escribia");
        	ACEe.printStackTrace();
        	return false;
        }
        catch (ClosedChannelException e) {
        	System.out.println("se a perdido la conexion y el canal estaba cerrado");
        	e.printStackTrace();
        	System.out.println("eliminando canal");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		reconect();
			buffer.reset();
			System.out.println("reenviando paquete");
            return sendBuffer(false,SocketConnector.getSock(),buffer);
        } catch (SocketException e) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				sock.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		reconect();
			buffer.reset();
			System.out.println("reenviando paquete por brokenPike");
            return sendBuffer(false,SocketConnector.getSock(),buffer);
        } 
        catch (IOException e) {
			// TODO Auto-generated catch block
        	System.out.println("Error de entrada y salida en escritura de socket");
			e.printStackTrace();
			return false;
           /*
            * Adicionada linea para forzar reconeccion por IO
            *
			System.out.println("Forzando reconeccion por excepcion de entrada y salida");
    		reconect();
			buffer.reset();
			System.out.println("reenviando paquete");
            return sendBuffer(false,SocketConnector.getSock(),buffer);
			*/
		}
    }
    
    public static synchronized boolean reconect() {
    	SocketChannel sock = SocketConnector.getSock();
    	if (sock==null || !sock.isConnected()) { // Se adiciono validacion para reconeccion por socket nulo.
	    	System.out.println("reconectando..");
			PackageToXML packageXML = new PackageToXML();
			ClientHeaderValidator headers = new ClientHeaderValidator();
			packageXML.addArrivePackageListener(headers);
			System.out.println("instanciandos client y package ");
			SocketConnector socketConnector=null;
			SocketChannel socket=null;
			try {
				System.out.println("Reconectando..");
				while (true) {
					try {
						
						String host = ConfigFileHandler.getHost();
						if (host!=null) {
							socketConnector = new SocketConnector(ConfigFileHandler.getHost(),
									  ConfigFileHandler.getServerPort(),packageXML);
						}
						else {
							try {
								System.out.println("El host fue nulo, cargando nueva configuracion");
								ConfigFileHandler.loadSettings();
								System.out.println("Host: "+ConfigFileHandler.getHost());
								System.out.println("port: "+ConfigFileHandler.getServerPort());
								
								socketConnector = new SocketConnector(ConfigFileHandler.getHost(),
										  ConfigFileHandler.getServerPort(),packageXML);
								
							} catch (ConfigFileNotLoadException e) {
								// TODO Auto-generated catch block
								System.out.println("no fue posible traer archivo de configuracion");
								e.printStackTrace();
							}
							
						}
						break;
					}
					catch(ConnectException CEe) {
						try {
							Thread.sleep(500);
							System.out.print("*");
						}
						catch (InterruptedException IEe) {}
					}
				}
				System.out.println("Lanzando nuevo hilo");
				if (socketConnector!=null) {
					socketConnector.start();
		            socket = SocketConnector.getSock();
				
					System.out.println("enviando paquete para reconexion en: "+socket.isConnected());
					System.out.println("database: "+SocketConnector.getDatabase());
					System.out.println("user: "+SocketConnector.getUser());
					System.out.println("passwd: "+SocketConnector.getPassword());
					System.out.println("ip: "+SocketConnector.getIp());
					System.out.println("mac: "+SocketConnector.getMac());

					SocketWriter.writing(socket,
					                    CNXSender.getPackage("MCNX",
					                            SocketConnector.getDatabase(),
					                            SocketConnector.getUser(),
					                            SocketConnector.getPassword(),
												SocketConnector.getIp(),
												SocketConnector.getMac()
										));
					System.out.println("reescribiendo...");
				}
				else {
					System.out.println("No fue posible bajar el archivo de configuracion..");
				}
	
			} catch (UnresolvedAddressException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("imposible reconectar por e1");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("imposible reconectar por io");
				packageXML.removeSuccessListener(headers);
				packageXML = null;
				headers = null;
				socketConnector=null;
				try {
					if (socket!=null)
						socket.close();
						socket = null;
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				System.gc();
			}
			return true;
		}
		else {
			System.out.println("Reconeccion anterior exitosa...");
			return false;
		}
    }
}