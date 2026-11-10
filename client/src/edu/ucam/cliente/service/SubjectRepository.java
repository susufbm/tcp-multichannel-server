package edu.ucam.cliente.service;

import java.io.IOException;
import java.util.Scanner;

import edu.ucam.cliente.interfaces.IChannelData;
import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.domain.Asignatura;



public class SubjectRepository extends BaseRepository<Asignatura> {
	
	Scanner teclado;
	// Variable temporal para guardar datos durante UPDATE
    private Integer totalAsignaturasCache = null; //Integer para poder igualar a null
		
	public SubjectRepository(IComunicationServer comunication, IChannelData channelData) {
		super(comunication, channelData, "ADDASIG", "REMOVEASIG", "GETASIG", "LISTASIG", "COUNTASIG", "UPDATEASIG");
		teclado = new Scanner(System.in);
	}
	
	
	public int sumaCreditos() {
		String response;
		int value = 0;
		try {
			response = comunication.sendCommand("SUMCREDITS");
			ResponseParser parser = new ResponseParser(response);
			if(parser.isSuccess()) {
				value = Integer.parseInt(parser.getMessage());
			}
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
		
		return value;
	}
	
	@Override
	public Asignatura getModel(String id) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.getModel(id);
	}
	
	
	@Override
	public void add(Asignatura model) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.add(model);
	}
	
	@Override
	public Asignatura pedirDatos() {
	    System.out.println("Ingrese el id de la asignatura:");
	    String id = teclado.nextLine().trim();

	    System.out.println("Ingrese el nombre de la asignatura:");
	    String nombre = teclado.nextLine().trim();

	    int creditos;
	    while (true) {
	        System.out.println("Ingrese los creditos de la asignatura:");
	        String linea = teclado.nextLine();
	        try {
	            creditos = Integer.parseInt(linea);
	            if (creditos <= 0) {
	                System.out.println("Debe ser un número positivo.");
	                continue;
	            }
	            break;
	        } catch (NumberFormatException e) {
	            System.out.println("Valor no válido, introduce un número.");
	        }
	    }

	    return new Asignatura(id, nombre, creditos);
	}


	@Override
	public void delete(String id) throws IOException {
		// TODO Auto-generated method stub
		super.delete(id);
	}

	
	@Override
	public void list() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.list();
	}
	
	@Override
	public void update(String id) throws IOException, ClassNotFoundException {
	    try {
	        totalAsignaturasCache = count();
	        System.out.println("Total de asignaturas disponibles: " + totalAsignaturasCache);
	    } catch (Exception e) {
	        System.out.println("Error al obtener total de asignaturas: " + e.getMessage());
	        totalAsignaturasCache = 0;
	    }

	    super.update(id);

	    totalAsignaturasCache = null;
	}

	
	@Override
	public int count() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.count();
	}
	
	@Override
	public void listObject(Asignatura asig) throws IOException, ClassNotFoundException {

	    if (asig == null) {
	        System.out.println("No se ha encontrado ninguna asignatura");
	        return;
	    }

	    System.out.println("Id asignatura: " + asig.getId());
	    System.out.println("Nombre: " + asig.getNombre());
	    System.out.println("Créditos: " + asig.getCreditos());
	}
	
	@Override
    public Asignatura pedirDatosActualizar(Asignatura actual) throws IOException, ClassNotFoundException {
        System.out.println("\n--- Actualizar Asignatura ---");
        System.out.println("Deje en blanco para mantener el valor actual");
        
        // ID
        System.out.println("Ingrese el nuevo id de la asignatura (actual: " + actual.getId() + "):");
        String id = teclado.nextLine().trim();
        if (id.isEmpty()) {
            id = actual.getId();
        }
        //NO verificar si existe porque el servidor está esperando el objeto
        // Si el usuario cambia el ID a uno que ya existe, el servidor lo comprueba
        
        // NOMBRE
        System.out.println("Ingrese el nuevo nombre de la asignatura (actual: " + actual.getNombre() + "):");
        String nombre = teclado.nextLine().trim();
        if (nombre.isEmpty()) {
            nombre = actual.getNombre();
        }
        
        // CRÉDITOS
        int creditos;
        while (true) {
            System.out.println("Ingrese los créditos de la asignatura (actual: " + actual.getCreditos() + "):");
            String linea = teclado.nextLine().trim();
            if (linea.isEmpty()) {
                creditos = actual.getCreditos();
                break;
            }
            try {
                creditos = Integer.parseInt(linea);
                if (creditos <= 0) {
                    System.out.println("Debe ser un número positivo.");
                    continue;
                }
                break;
            } catch (NumberFormatException e) {
                System.out.println("Valor no válido, introduce un número.");
            }
        }
        
        return new Asignatura(id, nombre, creditos);
    }
	
	

	public void addAsignaturaATitulo() throws IOException, ClassNotFoundException {

	    System.out.println("Ingrese el id de la asignatura que quiere añadir al título:");
	    String idAsig = teclado.nextLine().trim();

	    System.out.println("Ingrese el id del título:");
	    String idTit = teclado.nextLine().trim();

	    // Enviar comando directo al servidor
	    String response = comunication.sendCommand("ADDASIG2TIT " + idAsig + " " + idTit);
	    ResponseParser parser = new ResponseParser(response);

	    if (!parser.isSuccess()) {
	        System.out.println("FAILED " + parser.numerosDeRespuesta()
	                           + " " + parser.getMessage());
	        return;
	    }

	    System.out.println("OK " + parser.numerosDeRespuesta()
	                       + " " + parser.getMessage());
	}


	public void removeAsignaturaDeTitulo() throws IOException, ClassNotFoundException {

	    System.out.println("Ingrese el id de la asignatura que quiere eliminar del título:");
	    String idAsig = teclado.nextLine().trim();

	    System.out.println("Ingrese el id del título:");
	    String idTit = teclado.nextLine().trim();

	    // Enviar comando: REMOVEASIGFROMTIT <idasignatura> <idtitulacion>
	    String response = comunication.sendCommand("REMOVEASIGFROMTIT " + idAsig + " " + idTit);
	    ResponseParser parser = new ResponseParser(response);

	    if (!parser.isSuccess()) {
	        System.out.println("FAILED " + parser.numerosDeRespuesta()
	                           + " " + parser.getMessage());
	        return;
	    }

	    System.out.println("OK " + parser.numerosDeRespuesta()
	                       + " " + parser.getMessage());
	}


	@SuppressWarnings("unchecked")
	public void listAsignaturasFromTitulo() throws IOException, ClassNotFoundException {

	    System.out.println("Ingrese el id de la titulacion:");
	    String idTit = teclado.nextLine().trim();

	    String response = comunication.sendCommand("LISTASIGFROMTIT " + idTit);
	    ResponseParser parser = new ResponseParser(response);

	    if (!parser.isPREOK()) {
	        System.out.println("FAILED " + parser.numerosDeRespuesta()
	                           + " Mensaje error: " + parser.getMessage());
	        return;
	    }

	    System.out.println("PREOK " + parser.numerosDeRespuesta() + " "
	                       + parser.getIp() + " " + parser.getPort());

	    // Recibir array genérico y convertir
	    Object recibido = channelData.receiveObject(parser.getIp(), parser.getPort());
	    Object[] arrayObj = (Object[]) recibido;   // esto sí es válido

	    java.util.List<Asignatura> lista = new java.util.ArrayList<>();
	    if (arrayObj != null) {
	        for (Object o : arrayObj) {
	            lista.add((Asignatura) o);
	        }
	    }

	    // Leer respuesta final OK/FAILED
	    ResponseParser ultima = new ResponseParser(comunication.readLine());
	    if (!ultima.isSuccess()) {
	        System.out.println("FAILED " + ultima.numerosDeRespuesta()
	                           + " Mensaje error: " + ultima.getMessage());
	        return;
	    }

	    // Mostrar asignaturas
	    if (lista.isEmpty()) {
	        System.out.println("La titulacion no tiene asignaturas.");
	    } else {
	        System.out.println("Asignaturas de la titulacion " + idTit + ":");
	        for (Asignatura a : lista) {
	            System.out.println("  - Id: " + a.getId()
	                             + ", Nombre: " + a.getNombre()
	                             + ", Créditos: " + a.getCreditos());
	        }
	    }

	    System.out.println("OK " + ultima.numerosDeRespuesta() + " Transferencia terminada.");
	}


}