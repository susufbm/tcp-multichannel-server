package edu.ucam.server.repository;

import java.io.IOException;
import java.util.List;

import edu.ucam.domain.Titulacion;



public class TitulacionRepository extends BaseRepository<Titulacion> {

    @Override
    protected String getId(Titulacion model) {
        return model.getId();
    }
    
    @Override
    public void add(Titulacion model) throws IOException, ClassNotFoundException {
    	// TODO Auto-generated method stub
    	super.add(model);
    }
    
    @Override
    public int count() throws IOException, ClassNotFoundException {
    	// TODO Auto-generated method stub
    	return super.count();
    }
    
    @Override
    public void delete(String id) throws IOException {
    	// TODO Auto-generated method stub
    	super.delete(id);
    }
    
    @Override
    public boolean exists(String id) {
    	// TODO Auto-generated method stub
    	return super.exists(id);
    }
    
    @Override
    public Titulacion getModel(String id) throws IOException, ClassNotFoundException {
    	// TODO Auto-generated method stub
    	return super.getModel(id);
    }
    
    @Override
    public List<Titulacion> list() throws IOException, ClassNotFoundException {
    	// TODO Auto-generated method stub
    	return super.list();
    }
    
    @Override
    public synchronized void update(String id, Titulacion model) throws IOException, ClassNotFoundException {
    	// TODO Auto-generated method stub
    	super.update(id, model);
    }
    
    
}
