package edu.ucam.cliente.interfaces;

import java.io.IOException;

public interface IComunicationServer {
	
	public void connect() throws IOException;
	public void disconnect() throws IOException;
	public boolean isAlive();
	public String sendCommand(String command) throws IOException;
	public String readLine() throws IOException;
	
}
