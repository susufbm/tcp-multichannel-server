package edu.ucam.cliente.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.cliente.config.*;

public class CommunicationSocket implements IComunicationServer{
	
	private Socket mySocket;
	private int communicationId = 1;
	private BufferedReader br;
	private PrintWriter pw;
	private boolean status;

	@Override
	public void connect() throws IOException {
		mySocket = new Socket(ClientConfig.ip,ClientConfig.comunicationPort);
		br = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
		pw = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream()));
		status = true;
		
	}

	@Override
	public void disconnect() throws IOException {
		if(mySocket.isConnected()) {
			mySocket.close();
			status = false;
		}
		
	}

	@Override
	public boolean isAlive() {
		return status;
	}

	@Override
	public String sendCommand(String command) throws IOException {
		//GENERO EL ID DEL COMANDO
		pw.println(communicationId + " " + command);
		pw.flush();
		//LO INCREMENTO PARA QUE EL PROXIMO COMANDO TENGA UN ID DIFERENTE
		communicationId++;
		return br.readLine();
	}
	

	@Override
	public String readLine() throws IOException {
		// TODO Auto-generated method stub
		return br.readLine();
	}
}