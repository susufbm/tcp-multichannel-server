package edu.ucam.cliente.service;

import java.io.IOException;

import edu.ucam.cliente.config.ClientConfig;
import edu.ucam.cliente.interfaces.IAutentication;
import edu.ucam.cliente.interfaces.IComunicationServer;

public class AutenticationService implements IAutentication{
	
	private final IComunicationServer comunication;
	
	public AutenticationService(IComunicationServer comunication) {
		this.comunication = comunication;
	}

	@Override
	public boolean autenticar(String usuario, String password) throws IOException {
		String response = comunication.sendCommand("USER " + usuario);
	    System.out.println("Respuesta USER: " + response);

	    if (response == null) {
	        System.out.println("No se recibió respuesta del servidor al comando USER");
	        return false;
	    }

	    ResponseParser parser = new ResponseParser(response);
	    if (parser.isSuccess()) {
	    	 response = comunication.sendCommand("PASS " + password);
	        System.out.println("Respuesta PASS: " + response);

	        if (response == null) {
	            System.out.println("No se recibió respuesta del servidor al comando PASS");
	            return false;
	        }

	        parser = new ResponseParser(response);
	        return parser.isSuccess();
	    }
	    return false;
	}
	@Override
	//ENVIA EL COMANDO Y CIERRA EL SOCKET
	public void closeSession() throws IOException {
		comunication.sendCommand("EXIT");
	    comunication.disconnect();
	}

	@Override
	public int sesiones() {
	    String response;
	    int value = 0;
	    try {
	        response = comunication.sendCommand("SESIONES");
	        ResponseParser parser = new ResponseParser(response);
	        if(parser.isSuccess()) {
	            value = Integer.parseInt(parser.getMessage());
	        }
	    } catch (IOException e) {
	        System.out.println("Error en SESIONES: " + e.getMessage());
	    }
	    return value;
	}


}