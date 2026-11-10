package edu.ucam.server.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import edu.ucam.server.interfaces.IRepository;

public abstract class BaseRepository<T> implements IRepository<T> {

    protected Map<String, T> storage = new HashMap<>();//donde van a estar todos 

    @Override
    public synchronized void add(T model) throws IOException, ClassNotFoundException {
        String id = getId(model);
        
        // Verificar que no exista ya
        if (storage.containsKey(id)) {
            throw new IOException("Ya existe un elemento con id: " + id);
        }
        
        storage.put(id, model);
        System.out.println("[REPO] Añadido: " + id);
    }
    
    @Override
    public synchronized void delete(String id) throws IOException {
        if (!storage.containsKey(id)) {
            throw new IOException("No existe elemento con id: " + id);
        }
        
        storage.remove(id);
        System.out.println("[REPO] Eliminado: " + id);
    }
    
    @Override
    public synchronized void update(String id, T model) throws IOException, ClassNotFoundException {
        String newId = getId(model);
        
        // Caso 1: El ID no cambia
        if (id.equals(newId)) {
            if (!storage.containsKey(id)) {
                throw new IOException("No existe elemento con id: " + id);
            }
            storage.put(id, model);
            System.out.println("[REPO] Actualizado (mismo ID): " + id);
            return;
        }
        
        // Caso 2: El ID cambia
        if (!storage.containsKey(id)) {
            throw new IOException("No existe elemento con id original: " + id);
        }
        
        if (storage.containsKey(newId)) {
            throw new IOException("Ya existe un elemento con el nuevo id: " + newId);
        }
        
        // Quitar objeto con ID viejo y añadir con ID nuevo
        storage.remove(id);
        storage.put(newId, model);
        System.out.println("[REPO] Actualizado (cambio ID): " + id + " a " + newId);
    }
    
    @Override
    public synchronized T getModel(String id) throws IOException, ClassNotFoundException {
        return storage.get(id);
    }
    
    @Override
    public synchronized int count() throws IOException, ClassNotFoundException {
        return storage.size();
    }
    
    @Override
    public synchronized List<T> list() throws IOException, ClassNotFoundException {
        return new java.util.ArrayList<>(storage.values());
    }
    
    //COMPRUEBO QUE EXISTE OBJETO CON ESE ID
    public synchronized boolean exists(String id) {
        return storage.containsKey(id);
    }
    
    //DEVUELVE EL ID DEL OBJETO
    protected abstract String getId(T model);
    


}
