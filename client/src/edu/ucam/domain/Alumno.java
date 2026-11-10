package edu.ucam.domain;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Domain entity (client side): a student.
 */
public class Alumno implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombre;
    private String apellidos;
    private String dni;
    private Collection<Matricula> matriculas = new ArrayList<>();

    public Alumno() {
    }

    public Alumno(String nombre, String apellidos, String dni) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public Collection<Matricula> getMatriculas() { return matriculas; }
    public void addMatricula(Matricula m) { this.matriculas.add(m); }

    @Override
    public String toString() {
        return nombre + " " + apellidos + " (" + dni + ")";
    }
}
