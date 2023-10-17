package com.example.emakumovil.communications;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import org.jdom2.Document;
import org.jdom2.Element;


public class PingPackage extends Thread {

    private Document ping = new Document().setRootElement(new Element("PING"));
	private static long tinicial;
	private static ArrayList<String> pings = new ArrayList<String>();
    public PingPackage() {
    }
    
	public void run() {
		while(true) {
			try {
				SocketChannel sock = SocketConnector.getSock();
				Thread.sleep(10000);
				System.out.println("Escribiendo ping");
				System.out.println("ping pendientes: "+pings.size());
				if (pings.size()>10) {
					try {
						sock.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						System.out.println("socket cerrado por inactiviadad");
						pings.clear();
						e.printStackTrace();
					}
				}
				SocketWriter.writing(sock, ping);
				tinicial = System.currentTimeMillis();
				pings.add(String.valueOf(tinicial));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
	
	public static long getTinicial() {
		return tinicial;
	}
	
	public static void removePing() {
		if (pings.size()>0) {
			pings.remove(0);
		}
	}
}
