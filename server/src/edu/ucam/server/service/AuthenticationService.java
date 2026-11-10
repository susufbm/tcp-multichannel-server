package edu.ucam.server.service;

import edu.ucam.server.interfaces.IAuthenticationService;
import edu.ucam.server.protocol.CommandParser;
import edu.ucam.server.protocol.ResponseBuilder;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationService implements IAuthenticationService{
	// Códigos de respuesta
    private static final int CODE_OK_USER = 200;
    private static final int CODE_OK_PASS = 201;
    private static final int CODE_OK_EXIT = 202;
    private static final int CODE_FAILED_USER = 400;
    private static final int CODE_FAILED_PASS = 401;
    
    //MAPA DE USUARIOS Y SUS CONTRASEÑAS
    private static final Map<String, String> usuarios = new HashMap<>();
    
    //SE EJECUTA 1 VEZ PARA RELLENAL EL MAPA
    static {
        // Usuario por defecto 
        usuarios.put("admin", "admin");
        // Usuarios adicionales para pruebas
        usuarios.put("profesor", "prof123");
        usuarios.put("estudiante", "est123");
    }
    
 // Estado de la sesión actual
    private String usuarioPendiente = null;
    private boolean autenticado = false;

    public boolean procesarComando(String comando, PrintWriter out) {
        if (comando == null || comando.trim().isEmpty()) 
            return true;
        
        try {
           //PARSEO EL COMANDO
            CommandParser parser = new CommandParser(comando);
            String number = parser.getNumber();
            String cmd = parser.getCommand().toUpperCase();
            String[] args = parser.getArgs();
            
            // Procesar según el tipo de comando
            switch (cmd) {
                case "USER":
                    return procesarUser(number, args, out);
                    
                case "PASS":
                    return procesarPass(number, args, out);
                    
                case "EXIT":
                    return procesarExit(number, out);
            }
            
        } catch (Exception e) {
            System.err.println("Error parseando comando: " + e.getMessage());
            return true;
        }
        return false;
    }
    
    
    private boolean procesarUser(String number, String[] args, PrintWriter out) {
      
        String usuario = args[0];
        
        // Verificar si el usuario existe
        if (usuarios.containsKey(usuario)) {
        	//GUARDO EL USUARIO
        	usuarioPendiente = usuario;
            autenticado = false;
            out.println(ResponseBuilder.ok(number, CODE_OK_USER, "Envíe contraseña."));
            out.flush();
            return true;
        } else {
        	usuarioPendiente = null;
            out.println(ResponseBuilder.failed(number, CODE_FAILED_USER, "Not user."));
            out.flush();
            return true;
        }
    }
    
    private boolean procesarPass(String number, String[] args, PrintWriter out) {
        
    	String password = args[0];
        
        // Verificar la contraseña
        String passwordCorrecta = usuarios.get(usuarioPendiente);
        if (passwordCorrecta != null && passwordCorrecta.equals(password)) {
            autenticado = true;
         
            out.println(ResponseBuilder.ok(number, CODE_OK_PASS, "Welcome " + usuarioPendiente));
            out.flush();
            
            return true;
            
        } else {
            String usuarioIntento = usuarioPendiente;
            usuarioPendiente = null;
            autenticado = false;
            
            out.println(ResponseBuilder.failed(number, CODE_FAILED_PASS, "Prueba de nuevo"));
            out.flush();
            
            return true;
        }
    }
    
    private boolean procesarExit(String number, PrintWriter out) {
        out.println(ResponseBuilder.ok(number, CODE_OK_EXIT, "Bye"));
        out.flush();
        System.out.println("[AUTH] Cliente desconectado: " + 
            (usuarioPendiente != null ? usuarioPendiente : "sin autenticar"));
        return false; // Cerrar conexión
    }
    
    //VERIFICA SI ESTA AUTENTICADO
    public boolean estaAutenticado() {
        return autenticado;
    }
    
    //NOMBRE DEL USUARIO ACTUAL
    public String getUsuarioActual() {
        return usuarioPendiente;
    }

    
}
