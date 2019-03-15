import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ManejoClientes extends Thread{

	private Manager manager;
	
	private static Socket sc;
	
	private static String mensaje;
	
	private static DataInputStream in;
	
	private static DataOutputStream out;
	
	private static Semaphore semaforo;
	
	private static Integer port;
	
	private static String ip;
	
	public ManejoClientes(Manager _manager, Socket _sc)
	{
		super();
		manager = _manager;
		sc = _sc;
		try {
			in= new DataInputStream(sc.getInputStream());
			out= new DataOutputStream(sc.getOutputStream());
		}catch(Exception e)
		{
			
		}
	}
	
	public void run()
	{
		try {
			Scanner teclado;
			teclado=new Scanner(System.in);
			System.out.println(sc.getPort());
			do {
				
				manager.preguntarProxys();
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
					out.writeUTF(ip);
					out.writeUTF("" + port);
				}
				mensaje = in.readUTF();
				
				mensaje = teclado.next();
				out.writeUTF(mensaje);
				out.writeUTF(mensaje);
			}while(mensaje == "-1");
			
		}catch(Exception e)
		{
			System.out.println(e);
		}

	}
}
