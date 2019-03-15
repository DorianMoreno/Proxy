import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ManejoDeCliente extends Thread{
	DataInputStream in;
	DataOutputStream out;
	Proxy proxy;
	private Socket sc;
	private Socket scServidor;
	ManejoDeCliente(Socket socketDelCliente, Socket socketDelServidor, Proxy pro )
	{
		super();
		sc=socketDelCliente;
		scServidor=socketDelServidor;
		proxy=pro;
	}
	public void run()
	{
		
		try {	
		System.out.println("Conexion solicitada de la ip: "+sc.getInetAddress());
		//Cliente conectado	
		in= new DataInputStream(sc.getInputStream());
		out= new DataOutputStream(sc.getOutputStream());
		String mensajeDelCliente;
		//out.writeUTF("Mensaje del servidor: conexion exitosa");
		//Inicio de sesion del usuario
		boolean usuarioConectado=true;
		while(usuarioConectado) {
			mensajeDelCliente=in.readUTF();
			System.out.println(sc.getInetAddress()+" :"+mensajeDelCliente);
			////////////
			
			if(mensajeDelCliente.equals("1"))
			{
				////
				System.out.println("Cantidad usuarios: "+(proxy.getCantUsuariosConectados()));
				out.writeUTF(String.valueOf(proxy.getCantUsuariosConectados()));
				/////
			}
			
			///////////
			if(mensajeDelCliente.trim().equals("Salir"))
			{
				sc.close();
				proxy.setCantUsuariosConectados(proxy.getCantUsuariosConectados()-1);
				usuarioConectado=false;
			}
			
			
		}		
		System.out.println("desconectado el cliente"+sc.getInetAddress());
	} catch (Exception e) {
			
			// TODO Auto-generated catch block
			System.out.println("Se cayo la conexion en: "+sc.getInetAddress());
			proxy.setCantUsuariosConectados(proxy.getCantUsuariosConectados()-1);
			
	}

	
	
}
}