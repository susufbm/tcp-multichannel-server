package edu.ucam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.ucam.server.service.KeepaliveHandler;
// NO IMPORTAS GestorServer aquí

public class KeepaliveServer extends Thread {
    private ServerSocket serverKeep;
    private Map<String, Socket> socketsPendientes = new ConcurrentHashMap<>(); // IP → socket
    
    public KeepaliveServer() throws IOException {
        serverKeep = new ServerSocket(4000);
        System.out.println("KeepaliveServer escuchando en puerto 4000");
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                Socket socket = serverKeep.accept();
                String clientId = socket.getInetAddress().getHostAddress();
                socketsPendientes.put(clientId, socket); // ESPERAMOS GestorServer
                System.out.println("Keepalive pendiente de: " + clientId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    // MÉTODO PÚBLICO para GestorServer
    public void conectarKeepalive(String clientId, GestorServer gestor) {
        Socket socket = socketsPendientes.remove(clientId);
        if (socket != null) {
            KeepaliveHandler handler = new KeepaliveHandler(socket, gestor); 
            handler.start();
            System.out.println("Keepalive activado para: " + clientId);
        }
    }
}
