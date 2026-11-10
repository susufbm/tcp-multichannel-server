package edu.ucam.cliente.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import edu.ucam.cliente.config.ClientConfig;

public class Keepalive extends Thread {
    
    private Socket keepaliveSocket;
    private BufferedReader br;
    private PrintWriter pw;
    
    public Keepalive() throws IOException {
        // Canal keepalive SEPARADO (puerto 4000)
        keepaliveSocket = new Socket(ClientConfig.ip, 4000);
        br = new BufferedReader(new InputStreamReader(keepaliveSocket.getInputStream()));
        pw = new PrintWriter(new OutputStreamWriter(keepaliveSocket.getOutputStream()));
        System.out.println("Keepalive conectado a " + ClientConfig.ip + ":4000");
    }
    
    @Override
    public void run() {
        try {
            String linea;
            while ((linea = br.readLine()) != null) {
                if ("KEEPALIVE".equals(linea)) {
                    pw.println("OK");
                    pw.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Keepalive desconectado: " + e.getMessage());
        } finally {
            try { keepaliveSocket.close(); } catch (IOException ignored) {}
        }
    }
}
