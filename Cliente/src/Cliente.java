import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente{

	//En la lista de proxies buscar los que tienen menos carga.
	//Si se cae el proxy, buscar a los otros
	public static void main(String [] args)//Solo mientras se ejecute en consola
	{
		String ipServidor="192.168.0.2";
		conectarConServidor(ipServidor);
	}
	
	public static void conectarConServidor(String ipServidor)
	{
		Scanner teclado;
		int puerto=5500;
		DataInputStream in;
		String enviar;
		DataOutputStream out;
		String mensaje;
		try { //Intentemos hacer conexion con el register
			
			Socket scRegister= new Socket(ipServidor, puerto);
			
			in=new DataInputStream(scRegister.getInputStream());
			out=new DataOutputStream(scRegister.getOutputStream());
			
			//Esperar a que el manager le diga al cliente a donde conectarse
			
			String ipAConectar=in.readUTF();  //Aqui se obtiene la ip de donde se va a conectar el cliente
			int puertoAConectar= Integer.parseInt(in.readUTF()); //Aqui se obtiene el puerto en donde se va a conectar el cliente                             
			if(ipAConectar.equals("-1"))//Si no se encontro ningun proxy disponible
			{
				System.out.println("No se pudo encontrar ningun proxy disponible");//No se encontro ningun proxy disponible
				return ;  //Terminar proceso
			}
			
			Socket scProxy=new Socket (ipAConectar, puertoAConectar); //Conectar con proxy
		
			out.writeUTF("1");
			scRegister.close();//El cliente cierra conexion con el Register
			
			System.out.println("Conectado exitosamente con el proxy");//Salida en consola
			boolean estadoConectado=true;
			in=new DataInputStream(scProxy.getInputStream());
			out=new DataOutputStream(scProxy.getOutputStream());
			while(estadoConectado)
			{
				
				//Inicio de sesion
				teclado = new Scanner(System.in);
				enviar=teclado.nextLine();
				out.writeUTF(enviar);
				
				System.out.println("Enviado el string: "+enviar.trim());
				
				if(enviar.trim().equals("Salir") )
				{
					System.out.println("Desconectar del servidor");
					scProxy.close();
					estadoConectado=false;
				}
				if(enviar.trim().equals("Request") ) {
					in=new DataInputStream(scProxy.getInputStream());
					mensaje=in.readUTF();
					System.out.println("Proyectos:"+mensaje);
					//Sacando la cantidad de proyectos nuevos
					
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo con la conexion con el register");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo");
			//e.printStackTrace();
			//conectarConServidor(ipServidor);
		}
	}
}



