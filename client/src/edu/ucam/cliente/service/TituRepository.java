package edu.ucam.cliente.service;


import java.io.IOException;
import java.util.Hashtable;
import java.util.Scanner;

import edu.ucam.cliente.interfaces.IChannelData;
import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.domain.Asignatura;
import edu.ucam.domain.Matricula;
import edu.ucam.domain.Titulacion;


public class TituRepository extends BaseRepository<Titulacion>{

	Scanner teclado;
	SubjectRepository asignaturaRepositorio;
	MatriculaRepository matriculaRepo;
	
	// Variable temporal para guardar datos durante UPDATE
    private Integer totalAsignaturasCache = null; //Integer para poder igualar a null
    private Integer totalMatriculasCache = null;
	
	public TituRepository(IComunicationServer comunication, IChannelData channelData) {
		super(comunication, channelData, "ADDTIT", "REMOVETIT", "GETTIT", "LISTTIT", "COUNTTIT", "UPDATETIT");
		teclado = new Scanner(System.in);
		asignaturaRepositorio = new SubjectRepository(comunication, channelData);
		matriculaRepo = new MatriculaRepository(comunication, channelData);
	}
	
	
	@Override
	public void add(Titulacion model) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.add(model);
	}
	
	@Override
	public void delete(String id) throws IOException {
		// TODO Auto-generated method stub
		super.delete(id);
	}
	
	@Override
	public Titulacion getModel(String id) throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.getModel(id);
	}
	
	@Override
	public void list() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		super.list();
	}
	
	@Override
	public Titulacion pedirDatos() {
	    System.out.println("Ingrese el id de la titulacion:");
	    String id = teclado.nextLine().trim();

	    System.out.println("Ingrese el nombre de la titulacion:");
	    String nombre = teclado.nextLine().trim();

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

	    Hashtable<String, Asignatura> asignaturas = new Hashtable<>();
	    int count = 0;
	    while (count < num) {
	        System.out.println("----- Asignatura " + (count + 1) + " -----");
	        System.out.println("Ingrese el id de la asignatura existente:");
	        String idAsig = teclado.nextLine().trim();

	        Asignatura a = new Asignatura();
	        a.setId(idAsig);
	        asignaturas.put(a.getId(), a);
	        count++;
	    }

	    Titulacion t = new Titulacion();
	    t.setId(id);
	    t.setNombre(nombre);
	    for (Asignatura a : asignaturas.values()) {
	        t.addAsignatura(a);
	    }

	    return t;
	}

    @Override
    public void update(String id) throws IOException, ClassNotFoundException {
        try {
            totalAsignaturasCache = asignaturaRepositorio.count();
            System.out.println("Total de asignaturas disponibles: " + totalAsignaturasCache);

            totalMatriculasCache = matriculaRepo.count();
            System.out.println("Total de matriculas disponibles: " + totalMatriculasCache);
        } catch (Exception e) {
            System.out.println("Error al obtener totales: " + e.getMessage());
            totalAsignaturasCache = 0;
            totalMatriculasCache  = 0;
        }

        // iniciar el UPDATE genérico
        super.update(id);

        // limpiar caches porque la proxima vez que actualicemos tendremos que volver a cargar estos datos
        totalAsignaturasCache = null;
        totalMatriculasCache  = null;
    }
	
    @Override
    public Titulacion pedirDatosActualizar(Titulacion titu) throws IOException, ClassNotFoundException {

        System.out.println("Ingrese el nuevo id de la titulacion (actual: " + titu.getId() + "):");
        String id = teclado.nextLine().trim();
        if (id.isEmpty()) {
            id = titu.getId();
        }

        System.out.println("Ingrese el nuevo nombre de la titulacion (actual: " + titu.getNombre() + "):");
        String nombre = teclado.nextLine().trim();
        if (nombre.isEmpty()) {
            nombre = titu.getNombre();
        }

        Titulacion nueva = new Titulacion();
        nueva.setId(id);
        nueva.setNombre(nombre);

        for (Asignatura a : titu.getAsignaturas()) {
            nueva.addAsignatura(a);
        }

        for (Matricula m : titu.getMatriculas()) {
            nueva.addMatricula(m);
        }

        return nueva;
    }

	
	@Override
	public int count() throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return super.count();
	}
	
	
	@Override
	public void listObject(Titulacion titu) throws IOException, ClassNotFoundException {

		if (titu == null) {
	        System.out.println("No se ha encontrado ningún titulo");
	        return;
	    }
		

		System.out.println("Id titulacion: " + titu.getId());
		System.out.println("Nombre: " + titu.getNombre());
		System.out.println("Asignaturas:");

		for (Asignatura a :  (titu).getAsignaturas()) {
		    System.out.println("  - Id: " + a.getId()
		                     + ", Nombre: " + a.getNombre()
		                     + ", Créditos: " + a.getCreditos());
		}
		
		System.out.println("Matriculas:");
		for (Matricula m :  (titu).getMatriculas()) {
		    System.out.println("  - Id: " + m.getId()
		                     + ", Alunmo: " + m.getAlumno());
		}
	
	}	
}