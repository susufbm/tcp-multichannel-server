package edu.ucam.server.interfaces;

import java.io.PrintWriter;

public interface IAuthenticationService {
    boolean procesarComando(String comando, PrintWriter out);
    boolean estaAutenticado();
    String getUsuarioActual();
}
