public class Consulta {
	private String nombre;
	
	private Integer tiempo;
	
	private String territorio;
	
	public Consulta(String nombre, Integer tiempo, String territorio)
	{
		territorio = new String();
		this.tiempo = tiempo;
		this.nombre = nombre;
		this.territorio = territorio;
	}
	
	public void setTerritorio(String territorio)
	{
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
