import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Manager{

	//En la lista de proxies buscar los que tienen menos carga.
	//Si se cae el proxy, buscar a los otros
	private static List<String> allLines;
	
	private static String fileName;
	
	private static Path path;
	
	private static List<String> ip;
	
	private static List<Integer> port;
	
	private static List<Integer> cantidad;
		
	public Manager()
	{
		ip = new ArrayList<String>();
		port = new ArrayList<Integer>();
		cantidad = new ArrayList<Integer>();
		fileName = "data/proxy_ip.txt";
		path = Paths.get(fileName);
		try
		{
			allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
			for(String str: allLines)
			{
				String[] parts = str.split(" ");
				ip.add(parts[0]);
				port.add(Integer.parseInt(parts[1]));
				cantidad.add(0);
			}
			System.out.println(cantidad.size());
		}
		catch(Exception e)
		{
			System.out.println("No se encontró el archivo de proxys");
		}
	}
	
	public static void main(String [] args)//Solo mientras se ejecute en consola
	{
		Manager man = new Manager();
		man.esperarCliente();
	}
	
	public static void preguntarProxys()
	{
		for(int i=0 ; i<ip.size() ; ++i)
		{
			DataInputStream in;
			String enviar;
			DataOutputStream out;
			System.out.println("Intento de conexión con " + ip.get(i) + " en puerto " + port.get(i));
			try {
				Socket sc= new Socket(ip.get(i), port.get(i));
				//Inicio de sesion
				out=new DataOutputStream(sc.getOutputStream());
				in=new DataInputStream(sc.getInputStream());
				out.writeUTF("1");
				String mensaje=in.readUTF();
				cantidad.set(i, Integer.parseInt(mensaje));
				sc.close();
			} catch (Exception e) {
				System.out.println(e);
				cantidad.set(i, -1);
			}
		}
		System.out.println("Las cantidades de usuarios en los proxys son:");
		for(Integer cant: cantidad)
		{
			System.out.println(cant);
		}
	}
	
	public static void esperarCliente()
	{
		ServerSocket servidor = null;
		Socket scCliente = null;
		DataInputStream in;
		DataOutputStream out;
		String mensaje;
		try
		{
			servidor=new ServerSocket(5500);
			System.out.println("Proxy activado");
			while(true)
			{
				scCliente=servidor.accept();
				in= new DataInputStream(scCliente.getInputStream());
				out= new DataOutputStream(scCliente.getOutputStream());
				do {
					preguntarProxys();
					int index = -1;
					int minimo = 1000000000;
					for(int i=0 ; i<cantidad.size() ; ++i)
					{
						if(minimo > cantidad.get(i) && cantidad.get(i)!=-1)
						{
							minimo = cantidad.get(i);
							index = i;
						}
					}
					if(index == -1)
					{
						out.writeUTF("-1");
						out.writeUTF("-1");
					}
					else
					{
						out.writeUTF(ip.get(index));
						out.writeUTF("" + port.get(index));
					}
					mensaje = in.readUTF();
				}while(mensaje == "-1");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}



