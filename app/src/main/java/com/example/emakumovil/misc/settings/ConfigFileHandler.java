package com.example.emakumovil.misc.settings;

import android.app.Activity;

import com.example.emakumovil.misc.parameters.EmakuParametersStructure;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * ConfigFile.java Creado el 25-jun-2004
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
 * Esta clase es la encargada de almacenar los datos de configuracion necesarios
 * para el common <br>
 *
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez </A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda </A>
 */
public class ConfigFileHandler  extends Activity {

    private static SAXBuilder builder;
    private static Document doc;
    private static Element root;
    private static String language;
    private static String logMode;
    private static String jarDirectory;
    private static String jarFile;
    private static String lookAndFeel;
    private static String cash;
    private static ArrayList<Element> companies;
    private static Hashtable<String,HostConnection> HTHostConnections = new Hashtable<String, HostConnection>();;
    private static String currentCompany = "";


    /**
     * Este metodo se encarga de cargar el archivo de configuracion
     *
     * @throws ConfigFileNotLoadException
     */
    public static void loadSettings() throws ConfigFileNotLoadException {
        try {
            companies = new ArrayList<Element>();
            builder = new SAXBuilder();
            String path = "http://198.211.126.206/.qhatu/clientEmakuMovil.conf"; /// Pendiente por configurar por dominio
            System.out.println("path: "+path);
            URL url = new URL(path);
            System.out.println("cargando url "+url);
            doc = builder.build(url);
            System.out.println("cargando archivo en doc");
            root = doc.getRootElement();
            List<Element> configList = root.getChildren();
            Iterator<Element> i = configList.iterator();

            /**
             * Ciclo encargado de leer las primeras etiquetas del archivo XML,
             * en este caso Configuración
             */

            int counter = 0;
            //parameters = new Vector<String>();
            while (i.hasNext()) {
                Element data = (Element) i.next();
                String name = data.getName();
                System.out.println("name: "+name);
                if (name.equals("language")) {
                    language = data.getValue();
                    //parameters.add("language");
                    counter++;
                }
                else if (name.equals("log")) {
                    logMode = data.getValue();
                    //parameters.add("log");
                    counter++;
                }
                else if (name.equals("lookAndFeel")) {
                    lookAndFeel = data.getValue();
                    //parameters.add("lookAndFeel");
                    counter++;
                }
                else if (name.equals("cash")) {
                    cash = data.getValue();
                    //parameters.add("cash");
                    counter++;
                }
                else if (name.equals("company")) {
                    loadHostConnections(data);
                    companies.add((Element)data.clone());
                    //parameters.add("company");
                    counter++;
                }
                EmakuParametersStructure.addParameter(name,data.getValue());
            }

            if(counter < 5) {
                System.out.println("Archivo de configuracion corrupto"); // pendiente por pasar salidas de depuracion android
                System.exit(0);
            }

        }
        catch (FileNotFoundException FNFEe) {
            FNFEe.printStackTrace();
            throw new ConfigFileNotLoadException();
        }
        catch (JDOMException JDOMEe) {
            JDOMEe.printStackTrace();
            throw new ConfigFileNotLoadException();
        }
        catch (IOException IOEe) {
            IOEe.printStackTrace();
            System.out.println("------------------------------------");
            System.out.println(IOEe.getLocalizedMessage());
            System.out.println("------------------------------------");
            throw new ConfigFileNotLoadException();
        }
    }

    private static void loadHostConnections(Element e) {

        Iterator<Element> it = e.getChildren().iterator();
        HostConnection hostConnection = new HostConnection();
        String key = null;
        while (it.hasNext()) {
            Element config = (Element) it.next();
            String name = config.getName();
            String value = config.getValue();
            if (name.equals("name")) {
                System.out.println("name "+value.trim());
                key = value.trim();
            }
            else if (name.equals("host")) {
                System.out.println("host "+value);
                hostConnection.setHost(value);
            } else if (name.equals("serverport")) {
                System.out.println("serverport "+Integer.parseInt(value));
                hostConnection.setPort(Integer.parseInt(value));
            }
        }
        if (key!=null) {
            System.out.println("Entering HTHostConnections ... ");
            HTHostConnections.put(key,hostConnection);
        }
    }

    public static void loadJarFile(String nameCompany) {
        Iterator<Element> i = root.getChildren("company").iterator();
        boolean isCompany = false;
        System.out.println("Inside loadJarFile ... ");
        while (i.hasNext() && !isCompany) {
            Element data = (Element) i.next();
            Iterator<Element> j = data.getChildren().iterator();
            while (j.hasNext()) {
                Element config = (Element) j.next();
                String name = config.getName();
                String value = config.getValue();
                if (name.equals("name") && value.trim().equals(nameCompany.trim())) {
                    System.out.println("name isCompany ... ");
                    isCompany = true;
                }
                else if (name.equals("jarFile")) {
                    System.out.println("jarFile ... ");
                    jarFile = value;
                }
                else if (name.equals("directory")) {
                    System.out.println("directory ... ");
                    jarDirectory = value;
                }
            }
        }

    }
    /**
     * Este metodo retorna el host servidor
     *
     * @return la ip o el nombre del servidor de transacciones
     */
    public static String getHost() {
        System.out.println("Trayendo host de company: "+ConfigFileHandler.currentCompany);
        HostConnection hc = HTHostConnections.get(ConfigFileHandler.currentCompany);
        if (hc!=null) {
            return hc.getHost();
        }
        System.out.println("Host not set in the client.conf");
        return null;
    }

    public static String getLanguage() {
        return language;
    }

    public static String getLogMode() {
        return logMode;
    }

    public static ArrayList<Element> getCompanies() {
        return companies;
    }

    public static String getCash() {
        return cash;
    }

    public static String getLookAndFeel() {
        return lookAndFeel;
    }
    /**
     *
     * @return serverport
     */
    public static int getServerPort() {
        HostConnection hc = HTHostConnections.get(ConfigFileHandler.currentCompany);
        if (hc!=null) {
            return hc.getPort();
        }
        System.out.println("Port not set in the client.conf");
        return -1;
    }
    /**
     *
     * @param newhost
     */
    public static void setHost(String newhost) {
        root.getChild("host").setText(newhost);
    }
    /**
     *
     * @param newport
     */
    public static void setPort(int newport) {
        root.getChild("serverport").setText(Integer.toString(newport));
    }
    public static String getJarDirectory() {
        return jarDirectory;
    }
    public static String getJarFile() {
        return jarFile;
    }

    public static void setCurrentCompany(String company) {
        ConfigFileHandler.currentCompany = company;
    }

    public static String getCurrentCompany() {
        return currentCompany;
    }



}

class HostConnection {

    private String host;
    private int port;

    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public int getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
}
