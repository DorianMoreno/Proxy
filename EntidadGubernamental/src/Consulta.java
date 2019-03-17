import java.util.ArrayList;
import java.util.List;

public class Consulta {
	private String nombre;
	
	private Integer tiempo;
	
	private List<String> territorios;
	
	public Consulta(String nombre, Integer tiempo)
	{
		territorios = new ArrayList<String>();
		this.tiempo = tiempo;
		this.nombre = nombre;
	}
	
	public void addTerritorio(String territorio)
	{
		territorios.add(territorio);
	}

	public Integer getTiempo() {
		return tiempo;
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public String getTerritorios(){
		String terr = new String();
		for(String t: territorios)
		{
			terr = terr + " " + t;
		}
		return terr.trim();
	}
}
