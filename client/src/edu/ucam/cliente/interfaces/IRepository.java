package edu.ucam.cliente.interfaces;

import java.io.IOException;


public abstract interface IRepository <T>{
	
	public void add(T model) throws IOException, ClassNotFoundException;
	public void delete(String id) throws IOException;
	public void list() throws IOException, ClassNotFoundException;
	public void update(String id) throws IOException, ClassNotFoundException;
	public T getModel(String id) throws IOException, ClassNotFoundException;
	public T pedirDatos() throws IOException, ClassNotFoundException;
	public T pedirDatosActualizar(T model) throws IOException, ClassNotFoundException;
	public int count()throws IOException, ClassNotFoundException;
	public void listObject(T model)throws IOException, ClassNotFoundException;
}
