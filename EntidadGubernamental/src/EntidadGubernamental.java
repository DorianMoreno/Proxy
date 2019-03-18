import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	
	private AutoConsulta auto;
		
	public EntidadGubernamental()
	{
		ipManager = "25.3.250.74";
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
			System.out.println(nombre);
			for(int i=1 ; i<allLines.size() ; ++i)
			{
				if(allLines.get(i).equals("")) continue;
				
				String[] parts = allLines.get(i).split(" ");
				Consulta nueva = new Consulta(parts[1], parts[2], Integer.valueOf(parts[0]));
				
				consultas.add(nueva);
			}
		}catch(Exception e) {
			System.out.println(e);
		}
		auto = new AutoConsulta(this, consultas);
		auto.start();
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
	
	public void semaforoWait()
	{
		Boolean b;
		do {
			try {
				semaforo.acquire();
			
				b = true;
			}catch(Exception e)
			{
				System.out.println(e);
				b=false;
			}
		}while(semaforo.availablePermits() != 0 && b.equals(true));
	}

	
	public void mandar(Consulta consulta)
	{
		System.out.println("Enviada consulta " + consulta.getNombre() + "(" + semaforo.availablePermits() + ")");
		semaforoWait();
		Socket scProxy = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		String mensaje;
		try {
			scProxy = binding();
		}catch(Exception e)
		{
			System.out.println("No se pudo conectar con el manager de conexiones");
			return;
		}
		if(scProxy==null)
		{
			System.out.println("No se pudo encontrar un proxy");
			return;
		}
		System.out.println("Conectado exitosamente con el proxy");
		try {
			in=new DataInputStream(scProxy.getInputStream());
			out=new DataOutputStream(scProxy.getOutputStream());				
			
			out.writeUTF("Entidad");
			do {
				out.writeUTF("SubirConsulta");
				out.writeUTF(nombre);
				out.writeUTF(consulta.getNombre());
				out.writeUTF(consulta.getTerritorio());
				mensaje = in.readUTF();
				if(mensaje.equals("EXCEPTION-FOUND"))
					System.out.println("No se pudo conectar con el servidor, reconectando...");
			}while(mensaje.equals("EXCEPTION-FOUND"));
			if(mensaje.equals("-1"))
				System.out.println("Ya existe la consulta en el servidor");
			out.writeUTF("Salir");
			scProxy.close();
			semaforo.release();
			return;
		}
		catch(Exception e)
		{
			System.out.println("Conexion con el Proxy interrumpida, reintentando conectar...");
		}
		
		if(scProxy != null)
			if(!scProxy.isClosed())
			{
				try {
					scProxy.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		
		semaforo.release();
		return;
	}
	
	private void consultarVotos()
	{		
		System.out.println("Solicitando las consultas");
		semaforoWait();
		Socket scProxy = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		String mensaje;
		try {
			scProxy = binding();
		}catch(Exception e)
		{
			System.out.println("No se pudo conectar con el manager de conexiones");
			return;
		}
		if(scProxy==null)
		{
			System.out.println("No se pudo encontrar un proxy");
			return;
		}
		System.out.println("Conectado exitosamente con el proxy");
		try {
			in=new DataInputStream(scProxy.getInputStream());
			out=new DataOutputStream(scProxy.getOutputStream());				
			
			out.writeUTF("Entidad");
			do {
				out.writeUTF("VerResultadosConsulta");
				out.writeUTF(nombre);
				mensaje = in.readUTF();
				if(mensaje.equals("EXCEPTION-FOUND"))
					System.out.println("No se pudo conectar con el servidor, reconectando...");
			}while(mensaje.equals("EXCEPTION-FOUND"));
			int n = Integer.valueOf(mensaje);
			List<String> lines = new ArrayList<String>();
			for(int i=0 ; i<n ; ++i )
			{
				lines.add(in.readUTF());
			}
			if(lines.size() == 0)
			{
				System.out.println("Esta entidad gubernamental no tiene consultas");
			}
			else {
				System.out.println("Nombre\t\tTerritorio\tbajos\tmedios\taltos");
				for(String s: lines)
				{
					System.out.println(s);
				}
			}
			out.writeUTF("Salir");
			scProxy.close();
			semaforo.release();
			return;
		}
		catch(Exception e)
		{
			System.out.println("Conexion con el Proxy interrumpida, reintentando conectar...");
		}
		
		if(scProxy != null)
			if(!scProxy.isClosed())
			{
				try {
					scProxy.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		
		semaforo.release();
		return;
	}
	
	private void menu()
	{
		Scanner teclado;
		teclado=new Scanner(System.in);
		String opcion;
		String nombreConsulta;
		String territorioConsulta;
		do {
			System.out.println("¿Qué desea hacer?");
			System.out.println("1. Subir una consulta (no listada en el archivo de texto)");
			System.out.println("2. Consultar los votos de alguna consulta");
			System.out.println("3. Salir");
			
			opcion = teclado.nextLine().trim();
			
			if(opcion.equals("1"))
			{
				System.out.println("Ingresa el nombre de la consulta a insertar");
				nombreConsulta = teclado.nextLine();
				System.out.println("Escribe el territorio sobre el que se va a hacer la consulta");
				territorioConsulta = teclado.nextLine();
				mandar(new Consulta(nombreConsulta, territorioConsulta));
			}
			else if(opcion.equals("2"))
			{
				consultarVotos();
			}
			
		}while(!opcion.equals("3"));
		
	}

	public String getNombre() {
		return nombre;
	}
}
