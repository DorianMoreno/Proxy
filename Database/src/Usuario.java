public class Usuario {
	
	private String id;
	
	private String hash;	//El hash de la contraseña + su id
	
	private String territorio;
	
	public Usuario(String id, String hash, String territorio)
	{
		this.id = id;
		this.hash = hash;
		this.territorio = territorio;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getTerritorio() {
		return territorio;
	}

	public void setTerritorio(String territorio) {
		this.territorio = territorio;
	}
}
