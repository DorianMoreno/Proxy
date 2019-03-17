import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class EntidadGubernamental {
	
	private String nombre;
	
	private String fileName;
	
	private Path path;
	
	private List<Consulta> consultas;
	
	private Semaphore semaforo;
	
	private String ipManager;
	
	private Integer portManager;
		
	public EntidadGubernamental()
	{
		ipManager = "127.0.0.1";
		portManager = 5500;
		semaforo = new Semaphore(1, true);
		consultas = new ArrayList<Consulta>();
		fileName = "data/consultas.txt";
		//fileName = "data/consultas.txt";
		//fileName = "data/consultas.txt";
		//fileName = "data/consultas.txt";
		//fileName = "data/consultas.txt";
		path = Paths.get(fileName);
		try {
			List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
			nombre = allLines.get(0);
			for(int i=1 ; i<allLines.size() ; ++i)
			{
				if(allLines.get(i).equals("")) continue;
				
				String[] parts = allLines.get(i).split(" ");
				Consulta nueva = new Consulta(parts[1], Integer.valueOf(parts[0]));
				for(int j=2 ; j<parts.length ; ++j)
				{
					nueva.addTerritorio(parts[j]);
				}
				consultas.add(nueva);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		EntidadGubernamental EG = new EntidadGubernamental();
		EG.menu();
	}
	
	public Socket binding() throws Exception
	{
		Socket scManager= new Socket(ipManager, portManager);

		DataInputStream in = new DataInputStream(scManager.getInputStream());
		DataOutputStream out = new DataOutputStream(scManager.getOutputStream());

		//Esperar a que el manager le diga al cliente a donde conectarse

		String ipAConectar=in.readUTF();  //Aqui se obtiene la ip de donde se va a conectar el cliente
		int puertoAConectar= Integer.parseInt(in.readUTF()); //Aqui se obtiene el puerto en donde se va a conectar el cliente                             
		if(ipAConectar.equals("-1"))//Si no se encontro ningun proxy disponible
		{
			System.out.println("No se pudo encontrar ningun proxy disponible");//No se encontro ningun proxy disponible
			out.writeUTF("1");
			scManager.close();
			return null;  //Terminar proceso
		}

		Socket scProxy=new Socket (ipAConectar, puertoAConectar); //Conectar con proxy
		
		out.writeUTF("1");
		scManager.close();//El cliente cierra conexion con el Manager

		System.out.println("Conexion con " + scProxy.getInetAddress() + ":" + scProxy.getPort());
		return scProxy;
	}

	private void mandarConsulta()
	{
		
	}
	
	private void consultarVotos()
	{
		
	}
	
	private void menu()
	{
		Scanner teclado;
		teclado=new Scanner(System.in);
		String opcion;
		do {
			System.out.println("¿Qué desea hacer?");
			System.out.println("1. Subir una consulta (no listada en el archivo de texto)");
			System.out.println("2. Consultar los votos de alguna consulta");
			System.out.println("3. Salir");
			
			opcion = teclado.nextLine().trim();
			
			if(opcion.equals("1"))
			{
				
			}
			else if(opcion.equals("2"))
			{
				
			}
			
		}while(!opcion.equals("3"));
		
	}
}
