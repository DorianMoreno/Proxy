import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ManejoClientes extends Thread{

	private static Manager manager;
	
	private final Socket sc;
	
	private static String mensaje;
	
	private final DataInputStream in;
	
	private final DataOutputStream out;
	
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
		try {
			do {
				String solution = manager.preguntarProxys();
				System.out.println(solution);
				String[] diffs = solution.split(" ");
				ip = diffs[0];
				port = diffs[1];
				
				out.writeUTF(ip);
				out.writeUTF(port);
				
				mensaje = in.readUTF();
			}while(mensaje == "-1");
		}catch(Exception e)
		{
			System.out.println("Port " + sc.getPort() + ": " + e);
		}
		semaforo.release();
		try {
			sc.close();
		}catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
