import java.util.ArrayList;
import java.util.List;

/**
 * 
 */

/**
 * @author ancam
 *
 */
public class Consulta {
	private int cantidadVotosTotales;
	private String nombreConsulta;
	private String direccionOrigen;
	private String territorio;
	private List<String> usuarioVotaron;
	private List<String> votos;
	
	Consulta(String nombreConsulta, String direccionOrigen, String territorio){
		this.cantidadVotosTotales = 0;
		this.usuarioVotaron = new ArrayList<String>();
		this.votos = new ArrayList<String>();
		this.nombreConsulta = nombreConsulta;
		this.direccionOrigen = direccionOrigen;
		this.territorio = territorio;
	}
	
	//Retorna resultado de los votos
	public int[] resultadosVotos(){
		int[] vector = new int[3];
		for(int i=0 ; i<3 ; ++i)
		{
			vector[i] = 0;
		}
		for(int i=0; i<this.votos.size(); i++) {
			//Alto, Medio, Bajo
			if(votos.get(i).equals("A")) { 
				vector[0] = vector[0]+1;
			}
			else if(votos.get(i).equals("M")) {
				vector[1] = vector[1]+1;
			}
			else if(votos.get(i).equals("B")) {
				vector[2] = vector[2]+1;
			}
		}
		return vector;
	}
	
	//Agrega +1 a una consulta
	public boolean voto(String usuario, String voto) {
		for(String us: usuarioVotaron)
		{
			if(us.equals(usuario))
				return false;
		}
		this.usuarioVotaron.add(usuario);
		if(voto.equals("A") || voto.equals("M") || voto.equals("B")) { 
			this.votos.add(voto);
		}
		return true;
	}
	
	//Se comprueba si el usuario ya voto
	public boolean comprobar(String usuario) {
		for(int i=0; i<this.usuarioVotaron.size(); i++) {
			if(this.usuarioVotaron.get(i).equals(usuario)) {
				return false;
			}
		}
		return true;
	}
	
	public String print()
	{
		int[] vector = resultadosVotos();
		return nombreConsulta + "\t\t" +  territorio + "\t\t" + vector[2] + "\t" + vector[1] + "\t" + vector[0];
	}
	
	//Gets and Sets
	public int getCantidadVotosTotales() {
		return cantidadVotosTotales;
	}

	public void setCantidadVotosTotales(int cantidadVotosTotales) {
		this.cantidadVotosTotales = cantidadVotosTotales;
	}

	public String getNombreConsulta() {
		return nombreConsulta;
	}

	public void setNombreConsulta(String nombreConsulta) {
		this.nombreConsulta = nombreConsulta;
	}

	public String getDireccionOrigen() {
		return direccionOrigen;
	}

	public void setDireccionOrigen(String direccionOrigen) {
		this.direccionOrigen = direccionOrigen;
	}

	public String getTerritorio() {
		return territorio;
	}

	public void setTerritorio(String territorio) {
		this.territorio = territorio;
	}

	public List<String> getUsuarioVotaron() {
		return usuarioVotaron;
	}

	public void setUsuarioVotaron(List<String> usuarioVotaron) {
		this.usuarioVotaron = usuarioVotaron;
	}

	public List<String> getVotos() {
		return votos;
	}

	public void setVotos(List<String> votos) {
		this.votos = votos;
	}
	
	
}
