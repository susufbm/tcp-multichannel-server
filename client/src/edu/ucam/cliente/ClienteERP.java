package edu.ucam.cliente;

import java.io.IOException;

import edu.ucam.cliente.interfaces.IAutentication;
import edu.ucam.cliente.interfaces.IChannelData;
import edu.ucam.cliente.interfaces.IComunicationServer;
import edu.ucam.cliente.interfaces.IRepository;
import edu.ucam.cliente.service.AutenticationService;
import edu.ucam.cliente.service.ChannelData;
import edu.ucam.cliente.service.CommunicationSocket;
import edu.ucam.cliente.service.Keepalive;
import edu.ucam.cliente.service.Menu;
import edu.ucam.cliente.service.SubjectRepository;
import edu.ucam.cliente.service.TituRepository;
import edu.ucam.domain.Asignatura;
import edu.ucam.domain.Titulacion;


public class ClienteERP {
	
	private final IComunicationServer comunication;
    private final IAutentication autentication;
    private final IRepository<Asignatura> subjectRespository;
    private final IRepository<Titulacion> tituRepository;
    private Keepalive keepalive;
    private Menu menu;
    //METER IREPOSITORY DE MATRICULA
    
    public ClienteERP() throws IOException{
    	
    	keepalive = new Keepalive();
        keepalive.start();
       
    	//CREO EL SOCKET CON EL SERVIDOR
    	this.comunication = new CommunicationSocket();
        this.comunication.connect();
        
        //CREO EL CANAL PARA ENVIAR O RECIBIR OBJETOS DEL SERVIDOR
        IChannelData channelData = new ChannelData();
        //CREO EL OBJETO PRA AUTENTICAR AL USUARIO
        this.autentication = new AutenticationService(this.comunication);
        
        this.subjectRespository = new SubjectRepository(comunication, channelData);
        this.tituRepository = new TituRepository(comunication, channelData);
        
        this.menu = new Menu(comunication, channelData);
    } 
    
    //LE PASO LOS DATOS DEL USUARIO Y COMPRUEBA QUE SEAN CORRECTOS
    public boolean autenticar(String usuario, String password) throws IOException {
        return autentication.autenticar(usuario, password);
    }
    
    //CIERRO EL SOCKET
    public void cerrarSesion() throws IOException {
        autentication.closeSession();
    }
    
    
    //VER QUE HACER AQUI
    public boolean insertarAsignatura(Asignatura asig) {
    	return false;
    }
    
    public void ejecutarMenu() throws ClassNotFoundException, IOException
    {
    	menu.menuPrincipal();
    }
    
    
}