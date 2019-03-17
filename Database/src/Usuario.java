public class Usuario {
	
	private String id;
	
	private String contra;	//En realidad es el hash de la contraseña + su id
	
	private String territorio;
	
	public Usuario(String id, String contra, String territorio)
	{
		this.id = id;
		this.contra = contra;
		this.territorio = territorio;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContra() {
		return contra;
	}

	public void setContra(String contra) {
		this.contra = contra;
	}

	public String getTerritorio() {
		return territorio;
	}

	public void setTerritorio(String territorio) {
		this.territorio = territorio;
	}
}
