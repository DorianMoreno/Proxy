import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Database {
	
	private Set<Usuario> usuarios;
	
	private Set<Territorio> territorios;
	
	private String fileName;
	
	private Path path;

	private List<String> allLines;
	
	public Database() {
		fileName = "data/database.txt";
		path = Paths.get(fileName);
		try
		{
			allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
			for(String str: allLines)
			{
				
				String[] parts = str.split(" ");
				if(parts.length != 3) continue;
				
				
			}
		}
		catch(Exception e)
		{
			System.out.println("No se encontró el archivo de guardado");
		}
	}
}
