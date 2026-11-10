package edu.ucam.cliente;

import java.io.IOException;
import java.util.Scanner;

import edu.ucam.cliente.service.Keepalive;

public class AppUniversidad {
	
	public static void main(String args[]) throws IOException, ClassNotFoundException {
		
		try {
			 Scanner teclado = new Scanner(System.in);
			 
	        // CREO EL CLIENTE ERP
	        ClienteERP cliente = new ClienteERP();
	
	        System.out.println("Introduzca su usuario:");
	        String usuario = teclado.nextLine();
	
	        System.out.println("Introduzca su contraseña:");
	        String password = teclado.nextLine();
	
	        if (cliente.autenticar(usuario, password)) {
	        	cliente.ejecutarMenu();
	        }else {
	            System.out.println("Autenticación incorrecta");
	            cliente.cerrarSesion(); // EXIT si procede
	        }
	
	        System.out.println("FIN DE LA APLICACIÓN");
	        teclado.close();
		    
		} catch (IOException e) {
	        System.out.println("No se pudo iniciar keepalive: " + e.getMessage());
	    }
	}
}
