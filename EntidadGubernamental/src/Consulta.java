public class Consulta {
	private String nombre;
	
	private Integer tiempo;
	
	private String territorio;
	

	public Consulta(String nombre, String territorio)
	{
		this.tiempo = 0;
		this.nombre = nombre;
		this.territorio = territorio;
	}
	
	public Consulta(String nombre, String territorio, Integer tiempo)
	{
		this.tiempo = tiempo;
		this.nombre = nombre;
		this.territorio = territorio;
	}

	public Integer getTiempo() {
		return tiempo;
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public String getTerritorio(){
		return territorio;
	}
}
