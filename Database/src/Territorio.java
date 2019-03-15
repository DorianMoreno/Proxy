import java.util.Set;

public class Territorio {

	private static Integer id;
	
	private static String nombre;
	
	private static Set<Usuario> usuarios;
	
	public Territorio(Integer pid, String pnombre) {
		id = pid;
		nombre = pnombre;
	}
	
	public static Integer getId() {
		return id;
	}
	
	public static String getNombre() {
		return nombre;
	}
}
