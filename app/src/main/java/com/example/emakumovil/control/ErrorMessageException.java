package com.example.emakumovil.control;


public class ErrorMessageException extends Exception {
/**
	 * 
	 */
	private static final long serialVersionUID = 5363412429515039450L;
private String mensaje;
    
    public ErrorMessageException(String component) {
        this.mensaje = "ERROR" + component; /// modificado para android.. pendiente usode multilenguaje
    }

    public String getMessage() {
        return  mensaje;
    }
}
