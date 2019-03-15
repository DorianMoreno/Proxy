public class Usuario {
	
	private String id;
	
	private String contra;	//En realidad es el hash de la contraseña + su id
	
	private Territorio territorio;
	
	public Usuario(String id, String contra, Territorio territorio)
	{
		this.id = id;
		this.contra = contra;
		this.territorio = territorio;
	}
}
