package edu.ucam.cliente.service;

import java.io.IOException;
import java.util.List;

import edu.ucam.cliente.config.ClientConfig;
import edu.ucam.cliente.interfaces.IChannelData;
import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.cliente.interfaces.IRepository;

abstract class  BaseRepository<T> implements IRepository<T>{
	
	protected final IComunicationServer comunication;
	protected final IChannelData channelData;
	protected final String insertCommand;
	protected final String deleteCommand;
	protected final String getCommand;
	protected final String listCommand;
	protected final String countCommand;
	protected final String updateCommand;
	
	
	public BaseRepository(IComunicationServer comunication, IChannelData channelData, 
			String insertCommand, String deleteCommand, String getCommand, String listCommand, String countCommand, String updateCommand) {
				this.comunication = comunication;
				this.channelData = channelData;
				this.insertCommand = insertCommand;
				this.deleteCommand = deleteCommand;
				this.getCommand = getCommand;
				this.listCommand = listCommand;
				this.countCommand = countCommand;
				this.updateCommand = updateCommand;
	}
	
	
	//FUNCION PARA OBTENER UN OBJETO (GET TITU... GET MATRICULA...)
	@SuppressWarnings("unchecked")
	public T getModel(String id) throws IOException, ClassNotFoundException {
		//METER EN EL ID EN EL COMANDO NUM + " " + GETCOMMAND + ID
		String response = comunication.sendCommand(getCommand +  " " + id); //get comand es GETTITULO por ej.
		//INTERPRETO SI LA RESPUESTA ES CORRECTA
		ResponseParser parser = new ResponseParser(response);
		//CREO EL OBJETO
		T responseModel = null;
		if(!parser.isPREOK())
			return null;
		
		System.out.println("PREOK " + parser.numerosDeRespuesta() + " " + parser.getIp() + " " + parser.getPort());	
		//COJO EL OBJETO DEL CANAL DE DATOS ENVIADO POR EL SERVIDOR
		responseModel = (T)channelData.receiveObject(parser.getIp(), parser.getPort());
		
		//HAGO OTRO PARSER PARA LEER LA SEGUNDA RESPUESTA DEL SERVIDOR
		ResponseParser ultimaRespuesta = new ResponseParser(comunication.readLine());
		
		if(!ultimaRespuesta.isSuccess())
		{
			System.out.println("FAILED " + parser.numerosDeRespuesta() + " Mensaje error");
			return null;
		}
		System.out.println("OK " + parser.numerosDeRespuesta() + " Transferencia terminada.");	
		
		return responseModel;
	}
	
	//TERMINAR DE IMPLEMENTAR ESTAS FUNCIONES
	public void add(T model) throws IOException, ClassNotFoundException 
	{
		//COMPRUEBO QUE HAYA OBJETO NO VACIO
		if (model == null) {
		    System.out.println("Modelo nulo, no se envía comando al servidor.");
		    return;
		}
		//ENVIO EL COMANDO
		String response = comunication.sendCommand(insertCommand); 
		//INTERPRETO SI LA RESPUESTA ES CORRECTA
		ResponseParser parser = new ResponseParser(response);
		
		if(!parser.isPREOK())
			return;
		
		System.out.println("PREOK " + parser.numerosDeRespuesta() + " " + parser.getIp() + " " + parser.getPort());	
		
		//MANDO EL OBJETO AL SERVIDOR
		channelData.sendObject(parser.getIp(), parser.getPort(), model);
		
		//HAGO OTRO PARSER PARA LEER LA SEGUNDA RESPUESTA DEL SERVIDOR
		ResponseParser ultimaRespuesta = new ResponseParser(comunication.readLine());
		
		if(!ultimaRespuesta.isSuccess())
		{
			System.out.println("FAILED " + parser.numerosDeRespuesta() + " Mensaje error");
			return;
		}
		System.out.println("OK " + parser.numerosDeRespuesta() + " Transferencia terminada.");	
	}

	
	public void update(String id) throws IOException, ClassNotFoundException {
	    //Enviar comando UPDATE
	    String response = comunication.sendCommand(updateCommand + " " + id);

	    //PRIMER PREOK: puerto para enviar el objeto actual al cliente
	    ResponseParser preok1 = new ResponseParser(response);
	    if (!preok1.isPREOK()) {
	        System.out.println("FAILED " + preok1.numerosDeRespuesta() + " Mensaje error");
	        return;
	    }
	    System.out.println("PREOK " + preok1.numerosDeRespuesta() + " " +
	                       preok1.getIp() + " " + preok1.getPort());

	    //Recibir objeto actual desde el servidor
	    @SuppressWarnings("unchecked") //PARA QUE EL COMPILADOR NO MUESTRE ERROR EN EL CAST (T)
	    T objeto = (T) channelData.receiveObject(preok1.getIp(), preok1.getPort());
	    if (objeto == null) {
	        System.out.println("No se ha encontrado ningún objeto con id: " + id);
	        ResponseParser respFinal = new ResponseParser(comunication.readLine());
	        System.out.println(respFinal.getMessage());
	        return;
	    }
	    //imprimo el ibjeto por pantalla
	    System.out.println("Objeto actual: ");
	    listObject(objeto);

	    //Pedir al usuario los nuevos datos
	    T objetoActualizado = pedirDatosActualizar(objeto);
	    if (objetoActualizado == null) {
	        System.out.println("No se han introducido datos válidos para actualizar.");
	        return;
	    }

	    //SEGUNDO PREOK: puerto para recibir el objeto actualizado en el servidor
	    String respPreok2 = comunication.readLine();  
	    ResponseParser preok2 = new ResponseParser(respPreok2);
	    if (!preok2.isPREOK()) {
	        System.out.println("FAILED " + preok2.numerosDeRespuesta() + " Mensaje error");
	        return;
	    }
	    System.out.println("PREOK " + preok2.numerosDeRespuesta() + " " +
	                       preok2.getIp() + " " + preok2.getPort());

	    //Enviar objeto actualizado
	    channelData.sendObject(preok2.getIp(), preok2.getPort(), objetoActualizado);

	    //Leer respuesta final OK/FAILED
	    ResponseParser ultimaRespuesta = new ResponseParser(comunication.readLine());
	    if (!ultimaRespuesta.isSuccess()) {
	        System.out.println("FAILED " + ultimaRespuesta.numerosDeRespuesta() + " Mensaje error");
	        return;
	    }
	    System.out.println("OK " + ultimaRespuesta.numerosDeRespuesta() + " Transferencia terminada.");
	}

	public void delete(String id) throws IOException {
	    // Enviar comando: p.ej. REMOVETIT 2
	    String response = comunication.sendCommand(deleteCommand + " " + id);

	    ResponseParser parser = new ResponseParser(response);

	    if (!parser.isSuccess()) {
	        // FAILED <number> <code> <mensaje>
	        System.out.println("FAILED " + parser.numerosDeRespuesta()
	                           + " " + parser.getMessage());
	        return;
	    }

	    // OK <number> <code> <mensaje>
	    System.out.println("OK " + parser.numerosDeRespuesta()
	                       + " " + parser.getMessage());
	}

	
	public void list() throws IOException, ClassNotFoundException {
		//MANDO EL COMANDO
		String response = comunication.sendCommand(listCommand); 
		
		//INTERPRETO SI LA RESPUESTA ES CORRECTA
		ResponseParser parser = new ResponseParser(response);

		if(!parser.isPREOK())
			return;
		
		System.out.println("PREOK " + parser.numerosDeRespuesta() + " " + parser.getIp() + " " + parser.getPort());
		
		// RECIBO EL ARRAY DEL SERVIDOR
	    T[] array = (T[]) channelData.receiveObject(parser.getIp(), parser.getPort());

	    // CONVIERTO EL ARRAY A LISTA
	    List<T> lista = java.util.Arrays.asList(array);
		
	    //HAGO OTRO PARSER PARA LEER LA SEGUNDA RESPUESTA DEL SERVIDOR
		ResponseParser ultimaRespuesta = new ResponseParser(comunication.readLine());
		
		//IMPRIMIMOS LA LISTA
		if(lista != null)
			for (T elemento : lista) {
			    listObject(elemento); 
			    System.out.println(" ");
			}
		if(!ultimaRespuesta.isSuccess())
		{
			System.out.println("FAILED " + parser.numerosDeRespuesta() + " Mensaje error.");
			return ;
		}
		
		System.out.println("OK " + parser.numerosDeRespuesta() + " Transferencia terminada.");	
		//return lista;
	}
	
	@Override
	public T pedirDatos() throws IOException, ClassNotFoundException {
		return null;
		
	}
	
	@Override
	public T pedirDatosActualizar(T model) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int count() throws IOException, ClassNotFoundException {
	    try {
	        String response = comunication.sendCommand(countCommand);
	        System.out.println("Respuesta COUNT (" + countCommand + "): " + response);

	        ResponseParser parser = new ResponseParser(response);

	        if (!parser.isSuccess()) {
	            System.out.println("FAILED " + parser.numerosDeRespuesta() + " Mensaje error.");
	            return -1;
	        }

	        int total = Integer.parseInt(parser.getMessage());
	        System.out.println("OK " + parser.numerosDeRespuesta() + " " + total);
	        return total;

	    } catch (java.net.SocketException e) {
	        System.out.println("Conexión cerrada con el servidor: " + e.getMessage());
	        return -1;
	    }
	}


	
	@Override
	public void listObject(T model) throws IOException, ClassNotFoundException {

	    if (model == null) {
	        System.out.println("No se ha encontrado ningún objeto ");
	        return;
	    }
	}
	
	
	
	
}