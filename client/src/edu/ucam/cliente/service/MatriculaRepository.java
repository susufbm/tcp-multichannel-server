package edu.ucam.cliente.service;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

import edu.ucam.cliente.interfaces.IChannelData;
import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.domain.Alumno;
import edu.ucam.domain.Asignatura;
import edu.ucam.domain.Matricula;

public class MatriculaRepository extends BaseRepository<Matricula>{

	Scanner teclado;
	SubjectRepository asignaturaRepo;
	// Variable temporal para guardar datos durante UPDATE
    private Integer totalAsignaturasCache = null; //Integer para poder igualar a null

	public MatriculaRepository(IComunicationServer comunication, IChannelData channelData) {
		super(comunication, channelData, "ADDMATRICULA", "REMOVEMATRICULA", "GETMATRICULA", "LISTMATRICULA", "COUNTMATRICULA", "UPDATEMATRICULA");
		teclado = new Scanner(System.in);
		asignaturaRepo = new SubjectRepository(comunication, channelData);
	}

	
	@Override
	public void add(Matricula model) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.add(model);
	}
	
	@Override
	public int count() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.count();
	}
	
	@Override
	public void delete(String id) throws IOException {
		// TODO Auto-generated method stub
		super.delete(id);
	}
	
	@Override
	public Matricula getModel(String id) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.getModel(id);
	}
	
	@Override
	public void list() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.list();
	}
	

	@Override
	public Matricula pedirDatos() throws ClassNotFoundException, IOException {
	    System.out.println("Ingrese el id de la matrícula:");
	    String id = teclado.nextLine().trim();

	    int num;
	    while (true) {
	        System.out.println("Ingrese el numero de asignaturas que quieres añadir:");
	        String linea = teclado.nextLine();
	        try {
	            num = Integer.parseInt(linea);
	            if (num <= 0) {
	                System.out.println("Debe ser un número positivo.");
	                continue;
	            }
	            break;
	        } catch (NumberFormatException e) {
	            System.out.println("Valor no válido, introduce un número.");
	        }
	    }

	    // *** NUEVO: comprobar contra el total de asignaturas existentes ***
	    int totalAsigServidor = asignaturaRepo.count();
	    if (num > totalAsigServidor) {
	        System.out.println("No puedes matricular " + num
	                + " asignaturas porque solo existen "
	                + totalAsigServidor + " en el sistema.");
	        return null; // cancelar creación de matrícula
	    }

	    Hashtable<String, Asignatura> asignaturas = new Hashtable<>();
	    int count = 0;
	    while (count < num) {
	        System.out.println("----- Asignatura " + (count + 1) + " -----");
	        System.out.println("Ingrese el id de la asignatura existente:");
	        String idAsig = teclado.nextLine().trim();

	        // aquí puedes seguir como ahora (creando stub o validando id, según diseño)
	        Asignatura a = new Asignatura();
	        a.setId(idAsig);
	        asignaturas.put(a.getId(), a);
	        count++;
	    }

	    Alumno alumno = new Alumno("alumno1", "alumno1", "alumno1");
	    return new Matricula(id, alumno, asignaturas);
	}



	@Override
	public void update(String id) throws IOException, ClassNotFoundException {
	    try {
	        totalAsignaturasCache = asignaturaRepo.count();
	        System.out.println("Total de asignaturas disponibles: " + totalAsignaturasCache);
	    } catch (Exception e) {
	        System.out.println("Error al obtener total de asignaturas: " + e.getMessage());
	        totalAsignaturasCache = 0;
	    }

	    super.update(id);

	    totalAsignaturasCache = null;
	}

	
	
	@Override
	public void listObject(Matricula matricula) throws IOException, ClassNotFoundException {
		
		if(matricula == null)
		{
			System.out.println("No hay ninguna matricula.");
			return;
		}
		
		System.out.println("ID: " + matricula.getId());
		System.out.println("Alumno: " + matricula.getAlumno().getNombre() + " " + matricula.getAlumno().getApellidos() + " " + matricula.getAlumno().getDni());
		System.out.println("Asignaturas: ");
		for(Asignatura a : matricula.getAsignaturas())
		{
			asignaturaRepo.listObject(a);
		}
	}
	
	
	@Override
	public Matricula pedirDatosActualizar(Matricula actual) throws IOException, ClassNotFoundException {

	    System.out.println("Ingrese el nuevo id de la matrícula (actual: " + actual.getId() + "):");
	    String id = teclado.nextLine().trim();
	    if (id.isEmpty()) {
	        id = actual.getId();
	    }

	    Matricula nueva = new Matricula();
	    nueva.setId(id);
	    nueva.setAlumno(actual.getAlumno());

	    // clonar asignaturas desde la Collection que devuelve getAsignaturas()
	    java.util.Hashtable<String, Asignatura> copia = new java.util.Hashtable<>();
	    for (Asignatura a : (java.util.Collection<Asignatura>) actual.getAsignaturas()) {
	        copia.put(a.getId(), a);
	    }
	    nueva.setAsignaturas(copia);

	    return nueva;
	}

	
}
