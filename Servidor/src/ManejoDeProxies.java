import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class ManejoDeProxies extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket sc;
	ManejoDeProxies (Socket aux)
	{
		sc=aux;
	}
	
	public void run()
	{
		
		try {	
			
			
			System.out.println("Conexion solicitada de la ip: "+sc.getInetAddress());
			//Cliente conectado	
			in= new DataInputStream(sc.getInputStream());
			out= new DataOutputStream(sc.getOutputStream());
			String mensajeDelProxy;//=in.readUTF(); //espera un mensaje
			//System.out.println(mensajeDelCliente);
			//out.writeUTF("Mensaje del servidor: conexion exitosa");
			//Inicio de sesion del usuario
			//boolean usuarioConectado=true;
			while(true) {	
				
				mensajeDelProxy=in.readUTF();
				System.out.println(sc.getInetAddress()+" :"+mensajeDelProxy);
				if(false)
				{
					sc.close();
					System.out.println("desconectado el cliente"+sc.getInetAddress());
				}
			}
		
		
		} catch (Exception e) {
				
				// TODO Auto-generated catch block
				System.out.println("Se cayo el proxy que esta en: "+sc.getInetAddress());
		
	}
	
}
}
