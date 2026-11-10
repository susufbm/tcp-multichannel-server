package edu.ucam.server.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.ucam.domain.Asignatura;



public class AsignaturaRepository extends BaseRepository<Asignatura> {

    @Override
    public synchronized void add(Asignatura model) throws IOException, ClassNotFoundException {
        super.add(model); 
    }

    @Override
    public synchronized void delete(String id) throws IOException {
        super.delete(id);
    }

    @Override
    public synchronized List<Asignatura> list() throws IOException, ClassNotFoundException {
        return new ArrayList<>(storage.values());
    }

    @Override
    public synchronized void update(String id, Asignatura model) throws IOException, ClassNotFoundException {
    	// TODO Auto-generated method stub
    	super.update(id, model);
    }

    @Override
    public synchronized Asignatura getModel(String id) throws IOException, ClassNotFoundException {
        return super.getModel(id);
    }

    @Override
    public synchronized int count() throws IOException, ClassNotFoundException {
        return super.count();
    }

    public synchronized int sumCreditos() {
        int suma = 0;
        for (Asignatura a : storage.values()) {
            suma += a.getCreditos();
        }
        return suma;
    }

    @Override
    protected String getId(Asignatura model) {
        return model.getId();
    }
}
