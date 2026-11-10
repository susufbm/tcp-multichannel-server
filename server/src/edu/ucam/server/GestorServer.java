package edu.ucam.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ucam.domain.Asignatura;
import edu.ucam.domain.Matricula;
import edu.ucam.domain.Titulacion;
import edu.ucam.server.config.ServerConfig;
import edu.ucam.server.protocol.CommandParser;
import edu.ucam.server.protocol.ResponseBuilder;
import edu.ucam.server.repository.AsignaturaRepository;
import edu.ucam.server.repository.MatriculaRepository;
import edu.ucam.server.repository.TitulacionRepository;
import edu.ucam.server.service.AuthenticationService;
import edu.ucam.server.service.DataChannelService;
import edu.ucam.server.service.KeepaliveHandler;

public class GestorServer extends Thread {

    private Socket socket;
    private BufferedReader br;
    private PrintWriter pw;

    // Servicio de autenticación
    private AuthenticationService authService;
    
    // Repositorios (compartidos entre todas las conexiones para que se modifiquen en todos)
    private static TitulacionRepository titulacionRepo = new TitulacionRepository();
    private static AsignaturaRepository asignaturaRepo = new AsignaturaRepository();
    private static MatriculaRepository matriculaRepo = new MatriculaRepository();
    
    // CONTADOR GLOBAL DE CLIENTES CONECTADOS
    private static int clientesConectados = 0;
    
  //CONSTRUCTOR
    public GestorServer(Socket socket) throws IOException {
        this.socket = socket;
        //suma 1 al contador 
        clienteConectado();
        
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.pw = new PrintWriter(socket.getOutputStream(), true);
        this.authService = new AuthenticationService();
    }

    
    //===METODOS===
    
    public static synchronized int getClientesConectados() {
        return clientesConectados;
    }
    
    private static synchronized void clienteConectado() {
        clientesConectados++;
        System.out.println("Clientes conectados: " + clientesConectados);
    }
    
    private static synchronized void clienteDesconectado() {
        clientesConectados--;
        System.out.println("Clientes conectados: " + clientesConectados);
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = br.readLine()) != null) {
                
            	System.out.println("Comando recibido: " + line);
                
            	// Parseo básico para saber qué comando es
                CommandParser cmd = new CommandParser(line);
                String command = cmd.getCommand().toUpperCase();

                // Comandos de autenticación (USER, PASS, EXIT)
                if (command.equals("USER") || command.equals("PASS") || command.equals("EXIT")) {
                    boolean continuar = authService.procesarComando(line, pw);
                    if (!continuar) {
                        break; // EXIT
                    }
                  //LANZAR KEEPALIVE
                    if (authService.estaAutenticado()) {
                        String clientId = socket.getInetAddress().getHostAddress();
                        Socket keepSocket = ServerApp.getKeepaliveSocket(clientId);  // --> ESTO YA FUNCIONA
                        if (keepSocket != null) {
                            new KeepaliveHandler(keepSocket, this).start(); //LANZO EL KEEPALIVE
                            System.out.println("Keepalive activado para cliente autenticado: " + clientId);
                        } else {
                            System.out.println("No se encontró socket keepalive para: " + clientId);
                        }
                    }


                } else {
                    // Para cualquier otro comando, exigir estar autenticado
                    if (!authService.estaAutenticado()) {
                        pw.println(ResponseBuilder.failed(cmd.getNumber(), 403, "No autorizado"));
                        pw.flush();
                        continue;
                    }

                    // Comando de gestión (TIT, ASIG, MATRICULA, COUNTASIG, etc.)
                    String respuesta = procesarComando(cmd);
                    if (respuesta != null) {
                        pw.println(respuesta);
                        pw.flush();
                    }
                }
            }
        } catch (java.net.SocketException e) {
            System.out.println("Cliente desconectado abruptamente: " + e.getMessage());
        } catch (Exception e) {  
            System.err.println("Error en GestorServer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarConexion();
        }
    }
    
    private String procesarComando(CommandParser cmd) throws IOException, ClassNotFoundException {
        String command = cmd.getCommand().toUpperCase();
        String num = cmd.getNumber();
        String[] args = cmd.getArgs();

        switch (command) {
        
        	
        	//===SESIONES===
	        case "SESIONES": {
	            int numClientes = GestorServer.getClientesConectados();
	            return ResponseBuilder.ok(num, 200, String.valueOf(numClientes));
	        }
            // ===== TITULACION =====
	        case "ADDTIT": {
	            int port = ServerConfig.getFreePort();
	            if (port == -1) {
	                return ResponseBuilder.failed(num, 500, "No hay puertos libres");
	            }
	
	            // PREOK por canal de comandos
	            pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
	            pw.flush();
	
	            // Recibir Titulacion
	            Titulacion t = DataChannelService.receiveObject(port);
	            if (t == null) {
	                return ResponseBuilder.failed(num, 400, "Error en transferencia");
	            }
	
	            // Comprobar que el id de la titulacion no esté repetido
	            if (titulacionRepo.exists(t.getId())) {
	                return ResponseBuilder.failed(num, 409,
	                        "Ya existe una titulacion con id " + t.getId());
	            }
	
	            //Validar y completar asignaturas de la titulacion
	            java.util.Collection<Asignatura> colAsig = t.getAsignaturas();
	            if (colAsig != null && !colAsig.isEmpty()) {
	
	                java.util.HashSet<String> idsVistos = new java.util.HashSet<>();
	                java.util.List<Asignatura> completas = new java.util.ArrayList<>();
	
	                // Validar repetidas y existencia en el repositorio
	                for (Asignatura aStub : colAsig) {
	                    String idAsig = aStub.getId();
	
	                    // Repetidas dentro del título
	                    if (idsVistos.contains(idAsig)) {
	                        return ResponseBuilder.failed(num, 409,
	                                "La titulacion contiene asignaturas repetidas con id " + idAsig);
	                    }
	                    idsVistos.add(idAsig);
	
	                    // Comprobar que existe en el repositorio de asignaturas
	                    Asignatura completa = asignaturaRepo.getModel(idAsig);
	                    if (completa == null) {
	                        return ResponseBuilder.failed(num, 404,
	                                "No existe asignatura con id " + idAsig);
	                    }
	
	                    completas.add(completa);
	                }
	
	                //Sustituir las asignaturas por las completas
	                // Eliminar las actuales
	                java.util.List<Asignatura> copia =
	                        new java.util.ArrayList<>(t.getAsignaturas());
	                for (Asignatura a : copia) {
	                    t.removeAsignatura(a); 
	                }
	                // Añadir las completas
	                for (Asignatura aComp : completas) {
	                    t.addAsignatura(aComp);
	                }
	            }
	
	            //Guardar titulacion
	            try {
	                titulacionRepo.add(t);
	            } catch (IOException e) {
	                return ResponseBuilder.failed(num, 409, e.getMessage());
	            }
	
	            return ResponseBuilder.ok(num, 200, "Transferencia terminada");
	        }
	
            case "GETTIT": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendObject(port, titulacionRepo, id);
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "REMOVETIT": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];
                titulacionRepo.delete(id);
                return ResponseBuilder.ok(num, 200, "Titulo eliminado");
            }

            case "LISTTIT": {
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                java.util.List<Titulacion> lista = titulacionRepo.list();
                DataChannelService.sendList(port, lista);

                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "COUNTTIT": {
                int total = titulacionRepo.count();
                return ResponseBuilder.ok(num, 200, String.valueOf(total));
            }

            case "UPDATETIT": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                //Enviar PREOK con puerto para mandar al cliente el objeto actual
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                //Canal de datos 1: enviar objeto actual al cliente
                DataChannelService.sendObject(port, titulacionRepo, id);

                //Volver a pedir puerto para recibir objeto actualizado
                port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                //canal de datos 2: recibir Titulacion actualizada
                Titulacion tAct = DataChannelService.receiveObject(port);
                if (tAct == null) {
                    return ResponseBuilder.failed(num, 400, "Error en transferencia");
                }

                titulacionRepo.update(id, tAct);
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            // ===== ASIGNATURA =====

            case "ADDASIG": {
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                Asignatura a = DataChannelService.receiveObject(port);
                if (a == null) {
                    return ResponseBuilder.failed(num, 400, "Error en transferencia");
                }

                // Comprobar id duplicado en el servidor
                if (asignaturaRepo.exists(a.getId())) {
                    return ResponseBuilder.failed(num, 409,
                            "Ya existe una asignatura con id " + a.getId());
                }

                asignaturaRepo.add(a);
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "GETASIG": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendObject(port, asignaturaRepo, id);
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "REMOVEASIG": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                //Obtener la asignatura del repositorio (misma instancia que está en títulos)
                Asignatura asignatura = asignaturaRepo.getModel(id);
                if (asignatura == null) {
                    return ResponseBuilder.failed(num, 404, "No existe asignatura con id " + id);
                }

                //Eliminar del repositorio de asignaturas
                asignaturaRepo.delete(id);

                //Eliminar de TODOS los títulos que la contengan
                for (Titulacion t : titulacionRepo.list()) {
                    Asignatura aEnTit = t.getAsignatura(id);
                    if (aEnTit != null) {
                        // Usar la MISMA instancia del repositorio para que coincida la clave
                        t.removeAsignatura(asignatura);
                        titulacionRepo.update(t.getId(), t);
                    }
                }

                return ResponseBuilder.ok(num, 200, "Asignatura eliminada");
            }

            case "LISTASIG": {
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendList(port, asignaturaRepo.list());
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "COUNTASIG": {
                int total = asignaturaRepo.count();
                return ResponseBuilder.ok(num, 200, String.valueOf(total));
            }
            case "UPDATEASIG": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String idViejo = args[0];

                if (!asignaturaRepo.exists(idViejo)) {
                    return ResponseBuilder.failed(num, 404, "No existe asignatura con id: " + idViejo);
                }

                //enviar objeto actual al cliente
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendObject(port, asignaturaRepo, idViejo);

                //recibir objeto actualizado
                port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                Asignatura aActualizada = DataChannelService.receiveObject(port);
                if (aActualizada == null) {
                    return ResponseBuilder.failed(num, 400, "Error en transferencia");
                }

                try {
                    // Aquí usas la función de abajo para actualizar la asig en todos los titulos 
                    actualizarAsignaturaYPropagar(idViejo, aActualizada);
                    return ResponseBuilder.ok(num, 200, "Transferencia terminada");
                } catch (IOException e) {
                    return ResponseBuilder.failed(num, 409, e.getMessage());
                }
            }

            case "SUMCREDITS": {
                int sum = asignaturaRepo.sumCreditos();
                return ResponseBuilder.ok(num, 200, String.valueOf(sum));
            }
            case "ADDASIG2TIT": {
                if (args.length < 2) {
                    return ResponseBuilder.failed(num, 400, "Faltan parámetros (idAsignatura, idTitulacion)");
                }
                String idAsig = args[0];
                String idTit  = args[1];

                Asignatura a = asignaturaRepo.getModel(idAsig);
                if (a == null) {
                    return ResponseBuilder.failed(num, 404, "No existe asignatura con id " + idAsig);
                }

                Titulacion t = titulacionRepo.getModel(idTit);
                if (t == null) {
                    return ResponseBuilder.failed(num, 404, "No existe titulacion con id " + idTit);
                }

                if (t.getAsignatura(idAsig) != null) {
                    return ResponseBuilder.failed(num, 409, "La asignatura ya está en ese título");
                }

                t.addAsignatura(a);
                titulacionRepo.update(idTit, t);

                return ResponseBuilder.ok(num, 200, "Asignatura añadida al título.");
            }

            case "REMOVEASIGFROMTIT": {
                if (args.length < 2) {
                    return ResponseBuilder.failed(num, 400,
                            "Faltan parámetros (idAsignatura, idTitulacion)");
                }
                String idAsig = args[0];
                String idTit  = args[1];

                // 1) Comprobar que existe la titulación
                Titulacion t = titulacionRepo.getModel(idTit);
                if (t == null) {
                    return ResponseBuilder.failed(num, 404,
                            "No existe titulacion con id " + idTit);
                }

                // 2) Obtener la asignatura del MISMO repositorio donde se tomó al añadir
                Asignatura asignatura = asignaturaRepo.getModel(idAsig);
                if (asignatura == null) {
                    return ResponseBuilder.failed(num, 404,
                            "No existe asignatura con id " + idAsig);
                }

                // 3) Comprobar que esa instancia está en la titulación
                Asignatura aEnTit = t.getAsignatura(idAsig);
                if (aEnTit == null) {
                    return ResponseBuilder.failed(num, 404,
                            "La asignatura no pertenece a ese título");
                }

                // 4) Eliminar usando el objeto como clave (coincide con el que se insertó)
                t.removeAsignatura(asignatura);

                // 5) Guardar cambios
                titulacionRepo.update(idTit, t);

                return ResponseBuilder.ok(num, 200, "Asignatura eliminada del título.");
            }


            case "LISTASIGFROMTIT": {
                // <number> LISTASIGFROMTIT <idtitulacion>
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id de titulacion");
                }
                String idTit = args[0];

                Titulacion t = titulacionRepo.getModel(idTit);
                if (t == null) {
                    return ResponseBuilder.failed(num, 404, "No existe titulacion con id " + idTit);
                }

                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                // PREOK con ip y puerto
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                // Enviar las asignaturas del título por el canal de datos
                java.util.Collection<Asignatura> col = t.getAsignaturas();
                java.util.List<Asignatura> lista = new java.util.ArrayList<>(col);
                DataChannelService.sendList(port, lista);

                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }
         // ===== MATRICULA =====
            case "ADDMATRICULA": {
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                // PREOK con ip y puerto para recibir la matrícula
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                // 1) Recibir matrícula por canal de datos
                Matricula m = DataChannelService.receiveObject(port);
                if (m == null) {
                    return ResponseBuilder.failed(num, 400, "Error en transferencia");
                }

                // 2) Id de matrícula no repetido
                if (matriculaRepo.exists(m.getId())) {
                    return ResponseBuilder.failed(num, 409,
                            "Ya existe una matrícula con id " + m.getId());
                }

                // 3) Validar asignaturas de la matrícula
                java.util.Collection<Asignatura> colAsig = m.getAsignaturas();
                if (colAsig == null || colAsig.isEmpty()) {
                    return ResponseBuilder.failed(num, 400,
                            "La matrícula debe tener al menos una asignatura");
                }

                // 3.a) Número total de asignaturas existentes en el servidor
                int totalAsigServidor = asignaturaRepo.count();

                // 3.b) Conjunto para controlar repetidas y tabla para asignaturas completas
                java.util.HashSet<String> idsVistos = new java.util.HashSet<>();
                java.util.Hashtable<String, Asignatura> asignaturasCompletas =
                        new java.util.Hashtable<>();

                for (Asignatura aStub : colAsig) {
                    String idAsig = aStub.getId();

                    // Repetidas dentro de la propia matrícula
                    if (idsVistos.contains(idAsig)) {
                        return ResponseBuilder.failed(num, 409,
                                "La matrícula contiene asignaturas repetidas con id " + idAsig);
                    }
                    idsVistos.add(idAsig);

                    // Comprobar que existe en el repositorio de asignaturas
                    Asignatura completa = asignaturaRepo.getModel(idAsig);
                    if (completa == null) {
                        return ResponseBuilder.failed(num, 404,
                                "No existe asignatura con id " + idAsig);
                    }

                    // Rellenar con la versión completa
                    asignaturasCompletas.put(completa.getId(), completa);
                }

                // 3.c) No se pueden pedir más asignaturas distintas que las que existen
                if (idsVistos.size() > totalAsigServidor) {
                    return ResponseBuilder.failed(num, 409,
                            "La matrícula tiene " + idsVistos.size()
                            + " asignaturas distintas, pero solo existen "
                            + totalAsigServidor + " en el sistema");
                }

                // Sobrescribir las asignaturas de la matrícula con las completas
                m.setAsignaturas(asignaturasCompletas);

                // 4) Guardar matrícula
                try {
                    matriculaRepo.add(m);
                } catch (IOException e) {
                    return ResponseBuilder.failed(num, 409, e.getMessage());
                }

                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "GETMATRICULA": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendObject(port, matriculaRepo, id);
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "REMOVEMATRICULA": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                try {
                    matriculaRepo.delete(id);
                } catch (IOException e) {
                    // No existe la matrícula con ese id u otro problema en el repo
                    return ResponseBuilder.failed(num, 404, e.getMessage());
                }

                return ResponseBuilder.ok(num, 200, "Matricula eliminada");
            }


            case "LISTMATRICULA": {
                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }

                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendList(port, matriculaRepo.list());
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            case "COUNTMATRICULA": {
                int total = matriculaRepo.count();
                return ResponseBuilder.ok(num, 200, String.valueOf(total));
            }

            case "UPDATEMATRICULA": {
                if (args.length < 1) {
                    return ResponseBuilder.failed(num, 400, "Falta id");
                }
                String id = args[0];

                int port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                DataChannelService.sendObject(port, matriculaRepo, id);

                port = ServerConfig.getFreePort();
                if (port == -1) {
                    return ResponseBuilder.failed(num, 500, "No hay puertos libres");
                }
                pw.println(ResponseBuilder.preok(num, 200, ServerConfig.SERVER_IP, port));
                pw.flush();

                Matricula mActualizada = DataChannelService.receiveObject(port);
                if (mActualizada == null) {
                    return ResponseBuilder.failed(num, 400, "Error en transferencia");
                }

                matriculaRepo.update(id, mActualizada);
                return ResponseBuilder.ok(num, 200, "Transferencia terminada");
            }

            default:
                return ResponseBuilder.failed(num, 400, "Comando no reconocido");
        }
    }

    //FUNCION PARA QUE AL ACTUALIZAR LA ASIGNATURA SALGA EN TODOS LOS TITULOS ACTUALIZADOS
    private void actualizarAsignaturaYPropagar(String idViejo, Asignatura nueva)
            throws IOException, ClassNotFoundException {

        //Actualizar en repo de asignaturas (maneja cambio de id en el Map)
        asignaturaRepo.update(idViejo, nueva);

        //Recorrer todas las titulaciones y actualizar la asignatura dentro del Hashtable
        for (Titulacion t : titulacionRepo.list()) {

            java.util.Collection<Asignatura> asignaturas = t.getAsignaturas();
            if (asignaturas == null) continue;

            boolean cambiado = false;

            for (Asignatura a : asignaturas) {
                if (a.getId().equals(idViejo)) {
                    // Actualizar el objeto referenciado dentro de la titulacion
                    a.setId(nueva.getId());
                    a.setNombre(nueva.getNombre());
                    a.setCreditos(nueva.getCreditos());
                    cambiado = true;
                }
            }

            if (cambiado) {
                titulacionRepo.update(t.getId(), t);
            }
        }
    }



    public void cerrarConexion() {
        try {
            socket.close();
            System.out.println("Cliente desconectado");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clienteDesconectado();  
        }
    }
}
