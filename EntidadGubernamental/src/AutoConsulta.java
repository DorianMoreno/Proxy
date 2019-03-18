import java.util.List;

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
