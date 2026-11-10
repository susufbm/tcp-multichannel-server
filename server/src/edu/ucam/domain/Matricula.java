package edu.ucam.domain;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Domain entity: an enrolment linking a student to a set of subjects.
 */
public class Matricula implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private Alumno alumno;
    private Collection<Asignatura> asignaturas = new ArrayList<>();

    public Matricula() {
    }

    public Matricula(String id, Alumno alumno, java.util.Map<String, Asignatura> asignaturas) {
        this(id, alumno, asignaturas == null ? null : new ArrayList<>(asignaturas.values()));
    }

    public Matricula(String id, Alumno alumno, Collection<Asignatura> asignaturas) {
        this.id = id;
        this.alumno = alumno;
        if (asignaturas != null) {
            this.asignaturas = asignaturas;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public Collection<Asignatura> getAsignaturas() { return asignaturas; }
    public void setAsignaturas(Collection<Asignatura> asignaturas) { this.asignaturas = asignaturas; }

    /** Convenience overload: accepts a map keyed by subject id. */
    public void setAsignaturas(java.util.Map<String, Asignatura> asignaturas) {
        this.asignaturas = new ArrayList<>(asignaturas.values());
    }

    public void addAsignatura(Asignatura a) { this.asignaturas.add(a); }

    @Override
    public String toString() {
        return "Matricula{id='" + id + "', alumno='" + alumno + "', asignaturas=" + asignaturas.size() + "}";
    }
}
