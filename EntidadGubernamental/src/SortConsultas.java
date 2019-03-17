import java.util.Comparator;

public class SortConsultas implements Comparator<Consulta> {

	@Override
	public int compare(Consulta a, Consulta b) {
		if(a.getTiempo() == b.getTiempo())
			return 0;
		if(a.getTiempo() > b.getTiempo())
			return 1;
		return -1;
	}
	
}
