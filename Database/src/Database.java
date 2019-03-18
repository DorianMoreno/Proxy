import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.Scanner;
public class Database {
	
	public int id;
	
	public List<Usuario> usuarios;

	private static Semaphore semaforo;
	
	public boolean buscarSiUsuarioExiste(String idUsuario, String hashUsuario)
	{
		for(Usuario usuario : usuarios) {
			if(usuario.getId().trim().equals(idUsuario.trim()))//Si encuentra coincidencia entre los ids
			{
				if(usuario.getHash().equals(hashUsuario))
					return true;
				else
					return false;
			}
		}
		return false;
	}
	
	public String buscarUsuario(String idUsuario)
	{
		for(Usuario usuario : usuarios) {
			if(usuario.getId().trim().equals(idUsuario.trim()))//Si encuentra coincidencia entre los ids
			{
				return usuario.getTerritorio();

			}
		}
		return "-1";
	}


	public Database() {
    this.id=0;
	semaforo=new Semaphore(1, true);
	usuarios=new ArrayList<Usuario>();
	}
	
	public static void main(String [] args)//Solo mientras se ejecute en consola
	{
		
		System.out.println("En que puerto desea inicializar la base de datos?");
		Scanner teclado=new Scanner(System.in);
		new Database().escucharProxys(Integer.parseInt(teclado.nextLine()));
		teclado.close();
	}

	void escucharProxys(int puerto)
	{
		ServerSocket servidor=null;
		Socket scProxy=null; 
		
		
		try {
			servidor=new ServerSocket(puerto);
			System.out.println("Base de datos inicializada");
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		try 
		{
			while(true)
			{
				scProxy=servidor.accept();
				System.out.println("Proxy"+ scProxy.getInetAddress()+" se quiere conectar con la base de datos");
				new manejoProxys(scProxy, semaforo,this).start();
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		try
		{
			if(servidor != null)
			{
				servidor.close();
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

		
		
		
}
