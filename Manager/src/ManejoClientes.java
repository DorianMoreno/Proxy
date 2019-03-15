import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class ManejoClientes extends Thread{

	private static Manager manager;
	
	final Socket sc;
	
	private static String mensaje;
	
	final DataInputStream in;
	
	final DataOutputStream out;
	
	private static Semaphore semaforo;
	
	private static String port;
	
	private static String ip;
	
	public ManejoClientes(Manager _manager, Socket _sc, Semaphore _semaforo, DataInputStream _in, DataOutputStream _out)
	{
		super();
		manager = _manager;
		sc = _sc;
		semaforo = _semaforo;
		in = _in;
		out = _out;
	}
	
	public void run()
	{
		try {
			semaforo.acquire();
			Scanner teclado;
			teclado=new Scanner(System.in);
			do {
				String solution = manager.preguntarProxys();
				String[] diffs = solution.split(" ");
				ip = diffs[0];
				port = diffs[1];
				
				out.writeUTF(ip);
				out.writeUTF("" + port);
				
				mensaje = in.readUTF();
			}while(mensaje == "-1");
		}catch(Exception e)
		{
			System.out.println("" + sc.getPort() + ": " + e);
		}
		semaforo.release();
		try {
			in.close();
			out.close();
			sc.close();
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
