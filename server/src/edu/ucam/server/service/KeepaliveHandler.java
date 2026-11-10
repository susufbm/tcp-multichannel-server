package edu.ucam.server.service;

import java.io.*;
import java.net.Socket;
import edu.ucam.server.GestorServer; 

public class KeepaliveHandler extends Thread {
    private final Socket keepaliveSocket;
    private final GestorServer gestor; //clase que va a llamar a esta
    private boolean activo = true;

    public KeepaliveHandler(Socket keepaliveSocket, GestorServer gestor) {
        this.keepaliveSocket = keepaliveSocket;
        this.gestor = gestor;
    }

    @Override
    public void run() {
        try (
            PrintWriter pw = new PrintWriter(keepaliveSocket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(keepaliveSocket.getInputStream()))
        ) {
            while (activo) {
                Thread.sleep(30000);  // cada 30s 
                
                pw.println("KEEPALIVE");
                pw.flush();
                
                keepaliveSocket.setSoTimeout(5000);
                String respuesta = br.readLine();
                
                if (!"OK".equals(respuesta)) {
                    System.out.println("Cliente muerto por KEEPALIVE");
                    gestor.cerrarConexion(); 
                    break;
                }else {
                    System.out.println("KEEPALIVE OK - Cliente vivo");  //lo muestro 
                }
            }
        } catch (Exception e) {
            gestor.cerrarConexion();
        } finally {
            try { keepaliveSocket.close(); } catch (IOException ignored) {}
        }
    }
}
