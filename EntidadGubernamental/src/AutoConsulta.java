import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Semaphore;

public class AutoConsulta extends Thread {

	private EntidadGubernamental EG;

	private List<Consulta> consultas;

	public AutoConsulta(EntidadGubernamental pEG, List<Consulta> pconsultas)
	{
		super();
		EG = pEG;
		consultas = pconsultas;
	}
	
	public void run()
	{
		consultas.sort(new SortConsultas());
		for(Consulta con: consultas)
		{
			EG.mandar(con);
		}
	}
}
