package edu.ucam.server.service;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import edu.ucam.server.config.ServerConfig;
import edu.ucam.server.protocol.ResponseBuilder;
import edu.ucam.server.interfaces.IRepository;

public class DataChannelService {
//hacemos las funciones estaticas para no instanciar la clase cada vez que quiera usarla
//como no hay atributos de la clase no hay problema en hacerlo asi
	
	//RECIBIR OBJETO DEL CLIENTE
	public static <T> T receiveObject(int port) {
	    ServerSocket dataServer = null;
	    Socket clientData = null;
	    ObjectInputStream ois = null;

	    try {
	        dataServer = new ServerSocket(port);
	        clientData = dataServer.accept();
	        ois = new ObjectInputStream(clientData.getInputStream());

	        @SuppressWarnings("unchecked") //IGNORA AL COMPILADOR CUANDO DA UN WARNING DE QUE NO PUEDE COMPROBAR
	        //SI EL OBJETO ES TIPO T
	        T obj = (T) ois.readObject();
	        //DEVUELVE EL OBEJETO 
	        return obj;

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	    	//CIERRO EL SOCKET
	        try { if (ois != null) ois.close(); } catch (Exception ignored) {}
	        try { if (clientData != null) clientData.close(); } catch (Exception ignored) {}
	        try { if (dataServer != null) dataServer.close(); } catch (Exception ignored) {}
	       
	        //LIBERO EL PUERTO
	        ServerConfig.releasePort(port);
	    }
	}


	public static <T> void sendObject(int port, IRepository<T> repo, String id) {
	    
		ServerSocket dataServer = null;
	    Socket clientData = null;
	    ObjectOutputStream oos = null;

	    try {
	        //Abrir el socket de datos en el puerto indicado
	        dataServer = new ServerSocket(port);

	        //Esperar a que el cliente se conecte al canal de datos
	        clientData = dataServer.accept();

	        //Crear el "canal" para enviar objetos
	        oos = new ObjectOutputStream(clientData.getOutputStream());

	        //Obtener el objeto del repositorio
	        T obj = repo.getModel(id);

	        //Enviar el objeto al cliente
	        oos.writeObject(obj);
	        oos.flush();

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        // 6) Cerrar recursos y liberar el puerto
	        try { if (oos != null) oos.close(); } catch (Exception ignored) {}
	        try { if (clientData != null) clientData.close(); } catch (Exception ignored) {}
	        try { if (dataServer != null) dataServer.close(); } catch (Exception ignored) {}
	        ServerConfig.releasePort(port);
	    }
	}

	
	public static <T> void sendList(int port, java.util.List<T> lista) {
	    ServerSocket dataServer = null;
	    Socket clientData = null;
	    ObjectOutputStream oos = null;

	    try {
	        dataServer = new ServerSocket(port);
	        clientData = dataServer.accept();
	        oos = new ObjectOutputStream(clientData.getOutputStream());

	        // Convertimos la lista a array de Object y la enviamos
	        Object[] array = lista.toArray();
	        oos.writeObject(array);
	        oos.flush();

	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try { if (oos != null) oos.close(); } catch (Exception ignored) {}
	        try { if (clientData != null) clientData.close(); } catch (Exception ignored) {}
	        try { if (dataServer != null) dataServer.close(); } catch (Exception ignored) {}
	        ServerConfig.releasePort(port);
	    }
	}
}