package edu.ucam.cliente.service;

import java.io.IOException;
import java.util.Scanner;

import edu.ucam.cliente.interfaces.IChannelData;
import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.domain.Asignatura;
import edu.ucam.domain.Matricula;
import edu.ucam.domain.Titulacion;


public class Menu {

    private final Scanner teclado;
    private final SubjectRepository asignatura;
    private final TituRepository titulo;
    private final MatriculaRepository matricula;
    private String id;
    private final AutenticationService authService;

    public Menu(IComunicationServer comunication, IChannelData channelData) {
        teclado = new Scanner(System.in);
        asignatura = new SubjectRepository(comunication, channelData);
        titulo = new TituRepository(comunication, channelData);
        matricula = new MatriculaRepository(comunication, channelData);
        this.authService = new AutenticationService(comunication);
    }

    public void menuPrincipal() throws ClassNotFoundException, IOException {
        while (true) {
            System.out.println("*****MENU*****");
            System.out.println("1. Gestionar titulo.");
            System.out.println("2. Gestionar asignatura.");
            System.out.println("3. Gestionar matrícula.");
            System.out.println("4. Numero de sesiones.");
            System.out.println("0. Cerrar sesion.");

            int opcion = leerOpcion();
            if (opcion == -1) continue;

            switch (opcion) {
                case 1:
                    menuTitulo();
                    break;
                case 2:
                    menuAsignatura();
                    break;
                case 3:
                    menuMatricula();
                    break;
                case 4:
                	System.out.println("Clientes conectados: " + authService.sesiones());
                    break;
                case 0:
                	 // enviar EXIT al servidor y salir
                    authService.closeSession();   // envía comando EXIT
                    return;                      // rompe el while y vuelve a main
                default:
                    System.out.println("La opcion no es correcta. Intentelo de nuevo.");
            }
        }
    }

    public void menuTitulo() throws ClassNotFoundException, IOException {
        System.out.println("*****GESTIONAR TITULO*****");
        System.out.println("1. Aniadir titulo.");
        System.out.println("2. Actualizar titulo.");
        System.out.println("3. Obtener datos de titulo.");
        System.out.println("4. Eliminar titulo.");
        System.out.println("5. Listar titulos existentes.");
        System.out.println("6. Contar total de titulos existentes.");
        System.out.println("0. Volver.");

        int opcion = leerOpcion();
        if (opcion == -1) return;

        switch (opcion) {
            case 1:
                Titulacion t = titulo.pedirDatos();
                if (t != null) {
                    titulo.add(t);
                }
                break;
            case 2:
                System.out.println("Introduzca el id del titulo que quiere actualizar: ");
                id = teclado.nextLine();
                titulo.update(id);
                break;
            case 3:
                System.out.println("Introduzca el id del titulo que quiere obtener sus datos: ");
                id = teclado.nextLine();
                titulo.listObject(titulo.getModel(id));
                break;
            case 4:
                System.out.println("Introduzca el id del titulo que quiere eliminar: ");
                id = teclado.nextLine();
                titulo.delete(id);
                break;
            case 5:
                titulo.list();
                break;
            case 6:
                int totalT = titulo.count();
                if (totalT >= 0) {
                    System.out.println("Total de títulos: " + totalT);
                } else {
                    System.out.println("No se ha podido obtener el total (posible desconexión).");
                }
                break;
            case 0:
                return;
            default:
                System.out.println("La opcion no es correcta. Intentelo de nuevo.");
        }
    }

    public void menuAsignatura() throws ClassNotFoundException, IOException {
        System.out.println("*****GESTIONAR ASIGNATURA*****");
        System.out.println("1. Aniadir asignatura.");
        System.out.println("2. Actualizar asignatura.");
        System.out.println("3. Obtener datos de asignatura.");
        System.out.println("4. Eliminar asignatura.");
        System.out.println("5. Listar asignaturas existentes.");
        System.out.println("6. Contar asignaturas existentes.");
        System.out.println("7. Aniadir asignatura a un titulo.");
        System.out.println("8. Eliminar asignatura de un titulo.");
        System.out.println("9. Listar asignaturas de un titulo.");
        System.out.println("10. Sumar creditos de todas las asignaturas.");
        System.out.println("0. Volver.");

        int opcion = leerOpcion();
        if (opcion == -1) return;

        switch (opcion) {
            case 1:
                Asignatura a = asignatura.pedirDatos();
                if (a != null) {
                    asignatura.add(a);
                }
                break;
            case 2:
                System.out.println("Introduzca el id de la asignatura que quiere actualizar: ");
                id = teclado.nextLine();
                asignatura.update(id);
                break;
            case 3:
                System.out.println("Introduzca el id de la asignatura que quiere obtener sus datos: ");
                id = teclado.nextLine();
                asignatura.listObject(asignatura.getModel(id));
                break;
            case 4:
                System.out.println("Introduzca el id de la asignatura que quiere eliminar: ");
                id = teclado.nextLine();
                asignatura.delete(id);
                break;
            case 5:
                asignatura.list();
                break;
            case 6:
                int totalA = asignatura.count();
                if (totalA >= 0) {
                    System.out.println("Total de asignaturas: " + totalA);
                } else {
                    System.out.println("No se ha podido obtener el total (posible desconexión).");
                }
                break;
            case 7:
            	asignatura.addAsignaturaATitulo();
                break;
            case 8:
                asignatura.removeAsignaturaDeTitulo();
                break;
            case 9:
                asignatura.listAsignaturasFromTitulo();
                break;
            case 10:
            	  System.out.println("Suma total créditos: " + asignatura.sumaCreditos());
            	    break;
            case 0:
                return;
            default:
                System.out.println("La opcion no es correcta. Intentelo de nuevo.");
        }
    }

    public void menuMatricula() throws ClassNotFoundException, IOException {
        System.out.println("*****GESTIONAR MATRICULA*****");
        System.out.println("1. Aniadir matricula.");
        System.out.println("2. Actualizar matricula.");
        System.out.println("3. Obterner datos matricula.");
        System.out.println("4. Eliminar matricula.");
        System.out.println("5. Contar matriculas existentes.");
        System.out.println("6. Listar matriculas existentes.");
        System.out.println("0. Volver.");

        int opcion = leerOpcion();
        if (opcion == -1) return;

        switch (opcion) {
            case 1:
                Matricula m = matricula.pedirDatos();
                if (m != null) {
                    matricula.add(m);
                }
                break;
            case 2:
                System.out.println("Introduzca el id de la matricula que quiere actualizar: ");
                id = teclado.nextLine();
                matricula.update(id);
                break;
            case 3:
                System.out.println("Introduzca el id de la matricula que quiere obtener sus datos: ");
                id = teclado.nextLine();
                matricula.listObject(matricula.getModel(id));
                break;
            case 4:
                System.out.println("Introduzca el id de la matricula que quieres eliminar: ");
                id = teclado.nextLine();
                matricula.delete(id);
                break;
            case 5:
                int totalM = matricula.count();
                if (totalM >= 0) {
                    System.out.println("Total de matriculas: " + totalM);
                } else {
                    System.out.println("No se ha podido obtener el total (posible desconexión).");
                }
                break;
            case 6:
                matricula.list();
                break;
            case 0:
                return;
            default:
                System.out.println("La opcion no es correcta. Intentelo de nuevo.");
        }
    }

    // Método auxiliar para leer opción numérica de forma segura
    private int leerOpcion() {
        String linea = teclado.nextLine();
        try {
            return Integer.parseInt(linea);
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida, introduce un número.");
            return -1;
        }
    }
}
