import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;
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
		System.out.println("Conexion establecida con " + sc.getPort());
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
				if(server.existeConsulta(usuario, nombreConsulta))
					out.writeUTF("-1");
				else
				{
					server.ingresarConsulta(nombreConsulta, usuario, territorio);
					out.writeUTF("1");
				}
			}
			//Pedir lista de consultas
			else if(mensajeDelProxy.equals("2")) {
				mensajeDelProxy = in.readUTF();
				usuario = mensajeDelProxy;
				mensajeDelProxy = in.readUTF();
				territorio = mensajeDelProxy;
				server.consultasParaUsuario(usuario, territorio);
			}
			else if(mensajeDelProxy.equals("3")) {
				mensajeDelProxy = in.readUTF();
				usuario = mensajeDelProxy;
				List<Consulta> votadas = server.consultasPorEntidad(usuario);
				out.writeUTF(String.valueOf(votadas.size()));
				for(Consulta con: votadas)
				{
					out.writeUTF(con.print());
				}
			}
			
		} catch (Exception e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Se cayo el proxy que esta en: "+sc.getInetAddress());
		
		}
		semaforo.release();	
	}
}
