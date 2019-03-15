import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {
	public static void main(String[] args)
	{
		ServerSocket servidor=null;
		Socket scProxy=null;
		
		
		try {
			servidor=new ServerSocket(6000);
			System.out.println("Servidor activado");
			while(true)
			{
				scProxy=servidor.accept();
				ManejoDeProxies MP=new ManejoDeProxies(scProxy);
				MP.start();
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
}
