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
	
	private String ipManager;
	
	private Integer portManager;
	
	private Integer clock;
	
	public AutoConsulta(EntidadGubernamental pEG, Semaphore psemaforo, List<Consulta> pconsultas, String pipManager, Integer pportManager)
	{
		super();
		semaforo = psemaforo;
		EG = pEG;
		consultas = pconsultas;
		ipManager = pipManager;
		portManager = pportManager;
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
		semaforoWait();
		Socket scManager = null;
		Socket scProxy = null;
		String mensaje;
		try {
			scManager = new Socket(ipManager, portManager);
			DataInputStream inManager= new DataInputStream(scManager.getInputStream());
			DataOutputStream outManager= new DataOutputStream(scManager.getOutputStream());
			
			String ipProxy = inManager.readUTF();
			Integer portProxy = Integer.valueOf(inManager.readUTF());
			
			scProxy = new Socket(ipProxy, portProxy);
			
			if(scProxy.isBound())
			{
				outManager.writeUTF("1");
			}
			else
			{
				outManager.writeUTF("1");
			}
			scManager.close();
			
			DataInputStream inProxy= new DataInputStream(scProxy.getInputStream());
			DataOutputStream outProxy= new DataOutputStream(scProxy.getOutputStream());
			
			outProxy.writeUTF("Entidad");
			do {
				outProxy.writeUTF(consulta.getNombre());
				outProxy.writeUTF(consulta.getTerritorios());
				mensaje = inProxy.readUTF();
			}while(mensaje.equals("2"));
			
			scProxy.close();
			semaforo.release();
			return true;
		}catch(Exception e)
		{
			System.out.println(e);
		}
		
		
		
		if(scManager != null)
			if(!scManager.isClosed())
			{
				try {
					scManager.close();
				} catch (IOException e) {
					System.out.println(e);
				}
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
			mandar(con);
		}
	}
}
