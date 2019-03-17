import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente extends Thread{
	String nombreInicioDeSesion;
	boolean sesionIniciada;
	//En la lista de proxies buscar los que tienen menos carga.
	//Si se cae el proxy, buscar a los otros
	public static void main(String [] args) throws InterruptedException//Solo mientras se ejecute en consola
	{

		String ipServidor="127.0.0.1";
		new Cliente().conectarConServidor(ipServidor);
	}
	public  Socket binding(String ipServidor, int puerto) throws UnknownHostException, IOException 
	{
		Socket scRegister= new Socket(ipServidor, puerto);

		DataInputStream in=new DataInputStream(scRegister.getInputStream());
		DataOutputStream out=new DataOutputStream(scRegister.getOutputStream());

		//Esperar a que el manager le diga al cliente a donde conectarse

		String ipAConectar=in.readUTF();  //Aqui se obtiene la ip de donde se va a conectar el cliente
		int puertoAConectar= Integer.parseInt(in.readUTF()); //Aqui se obtiene el puerto en donde se va a conectar el cliente                             
		if(ipAConectar.equals("-1"))//Si no se encontro ningun proxy disponible
		{
			System.out.println("No se pudo encontrar ningun proxy disponible");//No se encontro ningun proxy disponible
			out.writeUTF("1");
			scRegister.close();
			return null;  //Terminar proceso
		}

		Socket scProxy=new Socket (ipAConectar, puertoAConectar); //Conectar con proxy

		out.writeUTF("1");
		scRegister.close();//El cliente cierra conexion con el Register

		return scProxy;
	}

	public void interaccionConElProxy(boolean estadoConectado, Socket scProxy ) throws IOException
	{
		String enviar;
		String mensaje;
		DataInputStream in=new DataInputStream(scProxy.getInputStream());
		DataOutputStream out=new DataOutputStream(scProxy.getOutputStream());
		System.out.println("Sesion iniciada");
		Scanner teclado = new Scanner(System.in);
		while(estadoConectado)
		{
			//Interaccion con el usuario
			System.out.println("Ingresar comando");
			enviar=teclado.nextLine();
			if(!enviar.equals("Salir")) {
				out.writeUTF(enviar);
			}else
			{
				out.writeUTF("Esto solo para saber si sigue conectado");
			}
			System.out.println("Enviado el string: "+enviar.trim());
			if(enviar.trim().equals("Salir") )
			{
				System.out.println("Cliente "+scProxy.getInetAddress()+" quiere salirse de la sesion");

				estadoConectado=false;
			}
			if(enviar.trim().equals("Request") ) {
				out.writeUTF("Request");
				in=new DataInputStream(scProxy.getInputStream());
				mensaje=in.readUTF();
				System.out.println("Proyectos:"+mensaje);
				//Sacando la cantidad de proyectos nuevos

			}
		}
	}

	public void conectarConServidor(String ipServidor) 
	{

		this.nombreInicioDeSesion="vacio";
		Scanner teclado;
		int puerto=5500;
		DataInputStream in;
		String enviar;
		DataOutputStream out;
		String mensaje;
		this.sesionIniciada=false;

		try { //Intentemos hacer conexion con el register

			Socket scProxy= binding(ipServidor, puerto);
			if(scProxy==null)
				return;
			System.out.println("Conectado exitosamente con el proxy");//Salida en consola
			boolean estadoConectado=false;
			//Inicio de sesion
			teclado = new Scanner(System.in);
			while(true) {
				try {
					while(true)
					{
						in=new DataInputStream(scProxy.getInputStream());
						out=new DataOutputStream(scProxy.getOutputStream());
						System.out.println("0.Salir");
						System.out.println("1.Iniciar sesion");
						System.out.println("2.Registrar");

						enviar=teclado.nextLine();
						if(enviar.trim().equals("Registrar"))
						{
							//Thread.sleep(2000);
							out.writeUTF("lol");//Informa al proxy que va a registrarse
							System.out.println("Ingresar region");

							out.writeUTF(teclado.nextLine());// Le envia al proxy la region en donde esta ubicado

							System.out.println("El id asignado para usted es: "+in.readUTF());
						}

						if(enviar.trim().equals("Iniciar sesion"))
						{
							out.writeUTF("IniciarSesion");
							System.out.println("Ingresar ID del usuario");
							this.nombreInicioDeSesion=teclado.nextLine();
							out.writeUTF(nombreInicioDeSesion.trim());//Envia al proxy el nombre de usuario
							mensaje=in.readUTF();
							if(mensaje.trim().equals("true"))
							{
								try {
									this.sesionIniciada=true;
									estadoConectado=true;
									interaccionConElProxy( estadoConectado, scProxy );//Comienza la interaccion normal con el proxy

								}
								catch(IOException e)
								{
									System.out.println("Proxy desconectado de forma abrupta reconectando e iniciando sesion otra vez...");
									scProxy.close();
									scProxy=this.binding(ipServidor, puerto);
									if(scProxy==null)
									{
										return;
									}

									interaccionConElProxy(estadoConectado, scProxy); 
								}
							}
							else
							{
								if(mensaje.trim().equals("false"))
								{
									System.out.println("Nombre de usuario no registrado, vuelva a ingresar otro comando");
								}
							}
						}
						if(enviar.trim().equals("Salir"))
						{
							out.writeUTF("Salir");
							scProxy.close();
							return;
						}

					}
				}
				catch(IOException e)
				{
					//Reeconectar con el proxy
					System.out.println("Proxy desconectado de forma abrupta");
					scProxy.close();
					scProxy= binding(ipServidor, puerto);
					if(scProxy==null)
					{
						return;
					}
				}
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo con la conexion con el register");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Fallo: "+e);
			e.printStackTrace();
			//conectarConServidor(ipServidor);
		} catch(Exception e)
		{

		}
	}
}



