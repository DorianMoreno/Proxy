import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Servidor{
	private List<Consulta> listaConsultas;
	private static Semaphore semaforo;
	
	Servidor(){
		semaforo = new Semaphore(1, true);
		listaConsultas = new ArrayList<Consulta>();
	}
	
	public static void main(String[] args)
	{
		ServerSocket servidor=null;
		Socket scProxy=null;
		Servidor server = new Servidor();
		try {
			servidor=new ServerSocket(6000);
			System.out.println("Servidor activado");
			
			while(true)
			{
				scProxy = servidor.accept();
				ManejoDeProxies MP=new ManejoDeProxies(server, scProxy, semaforo);
				MP.start();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//Obtiene listas posibles para votar con el usuario
	public List<Consulta> consultasParaUsuario(String usuario,String territorio) {
		List<Consulta> listaConsultas = consultasPorTerritorio(territorio);
		List<Consulta> listaConsultasFinal = consultasSinVotar(listaConsultas,usuario);
		
		return listaConsultasFinal;
	}
	
	//Crea nueva consulta
	public void ingresarConsulta(String nombreConsulta, String usuario, String territorio) {
		Consulta consulta = new Consulta(nombreConsulta, usuario, territorio);
		this.listaConsultas.add(consulta);
	}
	
	//Retorna consultas posibles por territorio
	public List<Consulta> consultasPorTerritorio(String territorio){
		List<Consulta> listaConsultasTerritorio = new ArrayList<Consulta>();
		for(int i=0; i<this.listaConsultas.size(); i++) {
			if(this.listaConsultas.get(i).getTerritorio().equals(territorio)) {
				listaConsultasTerritorio.add(this.listaConsultas.get(i));
			}
		}
		return listaConsultasTerritorio;
	}
	
	//Retorna consultas posibles por entidad
	public List<Consulta> consultasPorEntidad(String entidad){
		List<Consulta> listaConsultasEntidad = new ArrayList<Consulta>();
		for(int i=0; i<this.listaConsultas.size(); i++) {
			if(this.listaConsultas.get(i).getDireccionOrigen().equals(entidad)) {
				listaConsultasEntidad.add(this.listaConsultas.get(i));
			}
		}
		return listaConsultasEntidad;
	}
	
	//Retornar lista de consultas sin votar
	public List<Consulta> consultasSinVotar(List<Consulta> consultas, String usuario){
		List<Consulta> listaDefinitiva = new ArrayList<Consulta>();
		for(int i = 0; i<consultas.size(); i++) {
			if(consultas.get(i).comprobar(usuario)) {
				listaDefinitiva.add(consultas.get(i));
			}
		}
		return listaDefinitiva;
	}
	
	public boolean existeConsulta(String usuario, String consulta)
	{
		for(Consulta con: listaConsultas)
		{
			if(con.getDireccionOrigen().equals(usuario) && con.getNombreConsulta().equals(consulta))
				return true;
		}
		return false;
	}
	
	public List<Consulta> getListaConsultas() {
		return listaConsultas;
	}

	public void setListaConsultas(List<Consulta> listaConsultas) {
		this.listaConsultas = listaConsultas;
	}
	
}
