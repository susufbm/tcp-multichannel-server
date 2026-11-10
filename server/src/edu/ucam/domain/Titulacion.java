package edu.ucam.domain;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Domain entity: a degree programme holding a collection of subjects.
 */
public class Titulacion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String nombre;
    private Collection<Asignatura> asignaturas = new ArrayList<>();

    public Titulacion() {
    }

    public Titulacion(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Collection<Asignatura> getAsignaturas() { return asignaturas; }
    public void setAsignaturas(Collection<Asignatura> asignaturas) { this.asignaturas = asignaturas; }

    public void addAsignatura(Asignatura a) { this.asignaturas.add(a); }

    /** Finds a subject by id within this degree, or null if absent. */
    public Asignatura getAsignatura(String id) {
        for (Asignatura a : asignaturas) {
            if (a.getId() != null && a.getId().equals(id)) {
                return a;
            }
        }
        return null;
    }

    public void removeAsignatura(Asignatura a) { this.asignaturas.remove(a); }

    private Collection<Matricula> matriculas = new ArrayList<>();

    public Collection<Matricula> getMatriculas() { return matriculas; }
    public void addMatricula(Matricula m) { this.matriculas.add(m); }

    @Override
    public String toString() {
        return "Titulacion{id='" + id + "', nombre='" + nombre + "', asignaturas=" + asignaturas.size() + "}";
    }
}
