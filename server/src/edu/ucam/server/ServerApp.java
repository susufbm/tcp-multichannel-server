package edu.ucam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerApp {

//MAPA DE SOCKETS DE KEEPALIVE
public static Map<String, Socket> socketsKeepalive = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("Servidor iniciado...");

     //AÑADIR HILO KEEPALIVE
        new Thread() {
            public void run() {
                try (ServerSocket keepSocket = new ServerSocket(4000)) {
                    System.out.println("KeepaliveServer escuchando en puerto 4000");
                    while (true) {
                        Socket socketKeep = keepSocket.accept();
                        String clientId = socketKeep.getInetAddress().getHostAddress();
                        socketsKeepalive.put(clientId, socketKeep);
                        System.out.println("Keepalive RECIBIDO de: " + clientId);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        
        try (ServerSocket serverSocket = new ServerSocket(5000)) { //canal para los comandos
       
            while (true) {
                Socket cliente = serverSocket.accept();
  
                System.out.println("Cliente conectado: " + cliente.getInetAddress());
                GestorServer gestor = new GestorServer(cliente);
                gestor.start();
            }
   
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        }
    public static Socket getKeepaliveSocket(String clientId) {
        return socketsKeepalive.remove(clientId);
    }
}
