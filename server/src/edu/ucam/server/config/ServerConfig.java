package edu.ucam.server.config;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerConfig {

    public static final int COMMAND_PORT = 5000;
    public static final int DATA_PORT_START = 5001;
    public static final int DATA_PORT_END = 5100;
    public static final String SERVER_IP = "127.0.0.1";

    private static Map<Integer, Boolean> portMap = new ConcurrentHashMap<>(); //pueden entrar varios hilos a la vez

    //SOLO SE EJECUTA 1 VEZ AL CARGAR EN MEMORIA
    static {
        for (int i = DATA_PORT_START; i <= DATA_PORT_END; i++) {
           //TRUE SI ESTA DISPONIBLE, FALSE ESTA EN USO
        	portMap.put(i, true); 
        }  
    }

    public static synchronized Integer getFreePort() {
        for (int p = DATA_PORT_START; p <= DATA_PORT_END; p++) {
            Boolean libre = portMap.get(p);
            if (libre != null && libre) {
                portMap.put(p, false);
                return p;
            }
        }
        //SI NO ENCUENTRO NINGUNO 
        return -1;
    }


    public static synchronized void releasePort(int port) {
    	if (port >= DATA_PORT_START && port <= DATA_PORT_END) {
            //LO VUELVO A PONER LIBRE 
    		portMap.put(port, true);
        }
    }
}
