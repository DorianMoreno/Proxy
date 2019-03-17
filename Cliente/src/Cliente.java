import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Cliente extends Thread{
	String nombreInicioDeSesion;
	String hashInicioDeSesion;
	String territorioInicioSesion;
	boolean sesionIniciada;
	//En la lista de proxies buscar los que tienen menos carga.
	//Si se cae el proxy, buscar a los otros
	public static void main(String [] args) throws InterruptedException//Solo mientras se ejecute en consola
	{

		String ipServidor="25.3.250.74";
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

		System.out.println("Conexion con " + scProxy.getInetAddress() + ":" + scProxy.getPort());
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

		try { //Intentemos hacer conexion con el manager

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
						System.out.println("3. Consultar proyectos y votar");
						
						enviar=teclado.nextLine();
						
						if(enviar.trim().equals("Consular proyectos y votar")|| enviar.trim().equals("3"))
						{
							out.writeUTF("ConsultarProyectos");
							out.writeUTF(nombreInicioDeSesion);///Mandar id de usuario
							out.writeUTF(territorioInicioSesion);///Mandar el territorio al proxy
							///TODO: Imprimir los proyectos en los que el usuario puede votar
							///TODO:Abrir espacio para que el usuario vote
							///TODO: Enviar voto al proxy
							///TODO: Confirmacion
						}
						if(enviar.trim().equals("Registrar") || enviar.trim().equals("2"))
						{
							//Thread.sleep(2000);
							out.writeUTF("Registro");//Informa al proxy que va a registrarse
							System.out.println("Ingresar region");

							out.writeUTF(teclado.nextLine());// Le envia al proxy la region en donde esta ubicado
							
							System.out.println("Ingresar contraseña");
							
							out.writeUTF(String.valueOf(teclado.nextLine().hashCode()));// Le envia al proxy la region en donde esta ubicado

							System.out.println("El id asignado para usted es: "+in.readUTF());
						}

						if(enviar.trim().equals("Iniciar sesion") || enviar.trim().equals("1"))
						{
							out.writeUTF("IniciarSesion");
							System.out.println("Ingresar ID del usuario");
							this.nombreInicioDeSesion=teclado.nextLine();
							System.out.println("Ingresar contraseña");
							this.hashInicioDeSesion = String.valueOf(teclado.nextLine().hashCode());
							out.writeUTF(nombreInicioDeSesion.trim()); //Envia al proxy el nombre del usuario
							out.writeUTF(hashInicioDeSesion.trim()); //Envia al proxy el hash del usuario
							mensaje=in.readUTF();
							this.territorioInicioSesion = in.readUTF();
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
						if(enviar.trim().equals("Salir") || enviar.trim().equals("0"))
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
					System.out.println("Conexion reestablecida\n");
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



