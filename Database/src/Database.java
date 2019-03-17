import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.Scanner;
public class Database {
	public int id;
	public List<Usuario> usuarios;
	
	private Set<Territorio> territorios;
	
	private String fileName;
	
	private Path path;
	private static Semaphore semaforo;
	private List<String> allLines;
	
	public boolean buscarSiUsuarioExiste(String idUsuario)
	{
		for (Usuario usuario : usuarios) {
			if(usuario.getId().trim().equals(idUsuario.trim()))//Si encuentra coincidencia entre los ids
			{
				return true;
			}
			
		}
		return false;
	}
	
		public Database() {
	    this.id=0;
		semaforo=new Semaphore(1, true);
		usuarios=new ArrayList<Usuario>();
		/*fileName = "data/database.txt";
		path = Paths.get(fileName);
		try
		{
			allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
			for(String str: allLines)
			{
				
				String[] parts = str.split(" ");
				if(parts.length != 3) continue;
			}
		}
		catch(Exception e)
		{
			System.out.println("No se encontró el archivo de guardado");
		}
		*/
	}
	
		public static void main(String [] args)//Solo mientras se ejecute en consola
		{
			
			System.out.println("En que puerto desea inicializar la base de datos?");
			Scanner teclado=new Scanner(System.in);
			new Database().escucharProxys(Integer.parseInt(teclado.nextLine()));
		}
	
		void escucharProxys(int puerto)
		{
			ServerSocket servidor=null;
			Socket scProxy=null; 
			
			
			try {
				servidor=new ServerSocket(puerto);
				System.out.println("Base de datos inicializada");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}
			try 
			{
				while(true)
				{
					scProxy=servidor.accept();
					System.out.println("Proxy"+ scProxy.getInetAddress()+" se quiere conectar con la base de datos");
					new manejoProxys(scProxy, this.semaforo,this).start();
					
				}
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		
		
		
}
