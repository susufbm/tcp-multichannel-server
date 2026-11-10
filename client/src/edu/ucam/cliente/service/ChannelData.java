package edu.ucam.cliente.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import edu.ucam.cliente.interfaces.IChannelData;

public class ChannelData implements IChannelData {

		@Override
		public void sendObject(String ip, int port, Object model) throws IOException {
			Socket socketDatos = new Socket(ip, port);
	        ObjectOutputStream oos = new ObjectOutputStream(socketDatos.getOutputStream());
	        oos.writeObject(model);
	        oos.flush();
	        System.out.println("Objeto enviado al servidor");
		}
		
		@Override
		public Object receiveObject(String ip, int port) throws IOException, ClassNotFoundException {
		   // System.out.println("[CLIENT] Conectando a " + ip + ":" + port);
		    try (Socket socketDatos = new Socket(ip, port);
		         ObjectInputStream ois = new ObjectInputStream(socketDatos.getInputStream())) {
		        return ois.readObject();
		    }
		}


}