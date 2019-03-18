import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AutoConsulta extends Thread {

	private EntidadGubernamental EG;

	private Semaphore semaforo;

	private List<Consulta> consultas;

	private Integer clock;

	public AutoConsulta(EntidadGubernamental pEG, Semaphore psemaforo, List<Consulta> pconsultas)
	{
		super();
		semaforo = psemaforo;
		EG = pEG;
		consultas = pconsultas;
	}

	private void semaforoWait()
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

	private boolean mandar(Consulta consulta)
	{
		System.out.println("Enviada consulta " + consulta.getNombre() + "(" + semaforo.availablePermits() + ")");
		semaforoWait();
		Socket scProxy = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		String mensaje;
		try {
			scProxy = EG.binding();
		}catch(Exception e)
		{
			System.out.println("No se pudo conectar con el manager de conexiones");
			return false;
		}
		if(scProxy==null)
		{
			System.out.println("No se pudo encontrar un proxy");
			return false;
		}
		System.out.println("Conectado exitosamente con el proxy");
		try {
			in=new DataInputStream(scProxy.getInputStream());
			out=new DataOutputStream(scProxy.getOutputStream());				
			
			out.writeUTF("Entidad");
			do {
				out.writeUTF("SubirConsulta");
				out.writeUTF(consulta.getNombre());
				out.writeUTF(consulta.getTerritorio());
				mensaje = in.readUTF();
			}while(!mensaje.equals("1"));
			out.writeUTF("Salir");
			scProxy.close();
			semaforo.release();
			return true;
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
		return false;
	}
	
	public void run()
	{
		consultas.sort(new SortConsultas());
		for(Consulta con: consultas)
		{
			while(!mandar(con));
		}
		System.out.println("SIsas");
	}
}
