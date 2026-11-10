package edu.ucam.cliente.interfaces;

import java.io.IOException;

public interface IAutentication {
	
	public boolean autenticar(String usuario, String password) throws IOException;
	void closeSession() throws IOException;
	public int sesiones();
}
