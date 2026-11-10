package edu.ucam.cliente.interfaces;

import java.io.IOException;

public interface IChannelData {
	
	public void sendObject(String ip, int port, Object model) throws IOException, ClassNotFoundException;
	public Object receiveObject(String ip, int port) throws IOException, ClassNotFoundException;
	

}