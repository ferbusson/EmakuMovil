package com.example.emakumovil.transactions;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;

/**
 * Cache.java Creado el 02-sep-2004
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
 * Informacion de la clase
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
public class Cache {

    /**
     * 
     */
    private static Hashtable <String,CacheAnswer>Hsql = new Hashtable<String,CacheAnswer>();
    
    
    /**
     *  El constructor genera las tablas correspondientes a las caches retornadas
     * @param cache_answer contiene el documento retornado por el ST
     */
    
    public Cache(Document cache_answer) {
        Element raiz = cache_answer.getRootElement();
        List<Element> Lcache_answer = raiz.getChildren();
        Iterator<Element> i = Lcache_answer.iterator();

        String sql = null;
        CacheAnswer cacheanswer = new CacheAnswer();
        Element header = null;
        Hashtable<String,Document> values = new Hashtable<String,Document>();
        
        /*
         * Ciclo encargado de leer las primeras del paquete cache-answer
         */

        while (i.hasNext()) {

            Element datos = (Element) i.next();
            String Nelement = datos.getName();
            
            
            /*
             * Este elemento almacena el nombre de la sql que ha sido cacheada  
             */
            if (Nelement.equals("sql")) {
                sql=datos.getValue();
            } 
            /*
             * Este elemento almacena la informacion de la cabecera del retorno 
             * de la consulta
             */
            else if (Nelement.equals("header")) {
                header = datos;
            } 
            
            /*
             *  Este elemento almacena el retorno de la consulta que sera cacheado
             */
             
            else if (Nelement.equals("value")) {
                List<Element> Lvalue = datos.getChildren();
                Iterator<Element> k = Lvalue.iterator();
                getValues(values,k);
            }
            
        }

        /*
         * Almacena en la clase cacheanswer la cabecera y el contenido de la consulta
         * diferenciada por llave 
         */
        
        cacheanswer.setHeader(header);
        cacheanswer.setValue(values);
        Hsql.put(sql,cacheanswer);
    }

    /**
     *  Este metodo devuelve la hast con los valores llenos
     * @param i contiene la llave y el contenido de la consulta
     * @return retorna la hash con los valores formateados
     */
    private void getValues(Hashtable<String,Document> values,Iterator<Element> i) {
        
        
        /*
         *  Componentes de la Hash
         */
        
        //  Llave
        String key = "";
        //Objeto
        Document answer = new Document();
        Element query = new Element("ANSWER");
        query.addContent(new Element("row"));
        while (i.hasNext()) {

            Element datos = (Element) i.next();
            String Nelement = datos.getName();
            
            
            /*
             *  Este elemento almacena la llave del de la lista que contiene el
             *  resultado de la consulta
             */
            
            if (Nelement.equals("key")) {
                key=datos.getValue();
            } 
            
            /*
             *  Este elemento almacena el contenido de la consulta, se lo encalpsula
             *  en el Documento answer
             */
    
            else if (Nelement.equals("ANSWER")) {
            	query = (Element)datos.clone();
            }
        }

        answer.setRootElement(query);
        values.put(key,answer);
    }
    
    /**
     *  Este metodo retorna un boleano que indica si la consulta
     *  existe en el cache
     * @param sql almacena el id de la consulta a verificar
     * @return retorna true si la consulta esta cachada o false si no lo esta
     */
    
    public static boolean containsKey(String sql) {
        return Hsql.containsKey(sql);
    }
    
    /**
     * Este metodo retorna una consulta cacheada que no contiene llave
     * @param sql almacena el id de la consulta a retornar
     * @return returna la consulta chacheada
     */
    public static Document getAnswer(String sql) {
        return getAnswer(sql,"");
        
    }
    
    /**
     *  Este metodo retorna una consulta cacheada que contiene llave
     * @param sql almacena el id de la consulta a retornar
     * @param key almacena la llave de la consulta
     * @return retorna la consulta cacheada
     */
    public static Document getAnswer(String sql,String key) {
        CacheAnswer cacheanswer = Hsql.get(sql);
        Element header = cacheanswer.getheader();
        Document answer = (Document)((Document)(cacheanswer.getValue()).get(key)).clone();
        answer.getRootElement().addContent((Element)header.clone());
        return answer;
    }
}

/**
 *  
 * Cache.java Creado el 02-sep-2004
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
 * Esta clase encapsulara las cabeceras y los datos del retorno de las 
 * consultas que seran cacheados
 * <br>
 * @author <A href='mailto:felipe@qhatu.net'>Luis Felipe Hernandez</A>
 * @author <A href='mailto:cristian@qhatu.net'>Cristian David Cepeda</A>
 */
class CacheAnswer {
    
    private Element header = null;
    private Hashtable<String,Document> value = null;
   
    public CacheAnswer() {
        
    }
    
    public void setHeader(Element header) {
        this.header=header;
    }
    
    public Element getheader() {
        return header;
    }
    
    public Hashtable<String,Document> getValue() {
        return value;
    }
    
    public void setValue(Hashtable<String,Document> value) {
        this.value=value;
    }
    
}