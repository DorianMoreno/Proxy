import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ManejoDeProxies extends Thread {
	DataInputStream in;
	DataOutputStream out;
	final Socket sc;
	Servidor server;
	private static Semaphore semaforo;
	
	ManejoDeProxies (Servidor server, Socket aux, Semaphore sem)
	{
		sc=aux;
		this.server = server;
		semaforo = sem;
	}
	
	private void SemaforoWait()
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
	
	public void run()
	{
		SemaforoWait();
		String nombreConsulta, usuario, territorio;
		try {	
			nombreConsulta = null;
			usuario = null;
			territorio = null;
			in = new DataInputStream(sc.getInputStream());
			out= new DataOutputStream(sc.getOutputStream());
			String mensajeDelProxy = in.readUTF();
			//Acepta nueva consulta
			if(mensajeDelProxy.equals("1")) {
				mensajeDelProxy = in.readUTF();
				usuario = mensajeDelProxy;
				mensajeDelProxy = in.readUTF();
				nombreConsulta = mensajeDelProxy;
				mensajeDelProxy = in.readUTF();
				territorio = mensajeDelProxy;
				server.ingresarConsulta(nombreConsulta, usuario, territorio);
				out.writeUTF("2");
				
			}
			//Pedir lista de consultas
			else if(mensajeDelProxy.equals("2")) {
				mensajeDelProxy = in.readUTF();
				usuario = mensajeDelProxy;
				mensajeDelProxy = in.readUTF();
				territorio = mensajeDelProxy;
				server.consultasParaUsuario(usuario, territorio);
			}
			
		} catch (Exception e) {
				
				// TODO Auto-generated catch block
				System.out.println("Se cayo el proxy que esta en: "+sc.getInetAddress());
		
		}
	semaforo.release();	
	}
}
