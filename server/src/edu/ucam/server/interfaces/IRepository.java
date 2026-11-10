package edu.ucam.server.interfaces;

import java.io.IOException;
import java.util.List;



public interface IRepository<T> {
    public void add(T model) throws IOException, ClassNotFoundException;
    public void delete(String id) throws IOException;
    public List<T> list() throws IOException, ClassNotFoundException;
    public void update(String id, T model) throws IOException, ClassNotFoundException;
    public T getModel(String id) throws IOException, ClassNotFoundException;
    public int count() throws IOException, ClassNotFoundException;

}
