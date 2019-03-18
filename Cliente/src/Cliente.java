import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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
			return null;  //Terminar proceso camilo es un puto camilo es un puto camilo es un puto camilo es un puto
		}

		Socket scProxy=new Socket (ipAConectar, puertoAConectar); //Conectar con proxy
		
		out.writeUTF("1");
		scRegister.close();//El cliente cierra conexion con el Register

		System.out.println("Conexion con " + scProxy.getInetAddress() + ":" + scProxy.getPort());
		return scProxy;
	}

	public void interaccionConElProxy(boolean estadoConectado, Socket scProxy, Scanner teclado ) throws IOException
	{
		String enviar;
		DataInputStream in=new DataInputStream(scProxy.getInputStream());
		DataOutputStream out=new DataOutputStream(scProxy.getOutputStream());
		System.out.println("Sesion iniciada");
		while(estadoConectado)
		{
			//Interaccion con el usuario
			System.out.println("0. Cerrar sesión");
			System.out.println("1. Consultar proyectos y votar");
			enviar = teclado.nextLine();
			if(enviar.trim().equals("Consular proyectos y votar")|| enviar.trim().equals("1"))
			{
				out.writeUTF("ConsultarProyectos");
				out.writeUTF(nombreInicioDeSesion);		///Mandar id de usuario
				out.writeUTF(territorioInicioSesion);	///Mandar el territorio al proxy
				int n = Integer.valueOf(in.readUTF());
				if(n==0)
				{
					System.out.println("No hay consultas disponibles para votar");
				}
				else
				{
					System.out.println("Usted puede votar en la(s) siguiente(s) consulta(s):");
					List<String> consultas = new ArrayList<String>();
					for(int i=0 ; i<n ; ++i)
					{
						consultas.add(in.readUTF());
					}
					for(int i=0 ; i<n ; ++i)
					{
						System.out.println((i+1) + ". " + consultas.get(i));
					}
					String voto, consulta;
					do {
						System.out.println("Escriba el número de la consulta que quiere votar");
						consulta = teclado.nextLine().trim();
						if(Integer.valueOf(consulta) > n || Integer.valueOf(consulta) <= 0)
						{
							voto = "ERROR";
							continue;
						}
						System.out.println("Vote (A = Alto, M = Medio, B = Bajo, Salir = Cancelar operación)");
						voto = teclado.nextLine().trim();
					}
					while(!voto.equals("A") && !voto.equals("B") && !voto.equals("M") && !voto.equals("Salir"));
					out.writeUTF(consultas.get(Integer.valueOf(consulta)-1));
					out.writeUTF(voto);
					System.out.println(in.readUTF());
				}
			
			}
			if(enviar.trim().equals("Cerrar sesion") ||enviar.trim().equals("0"))
			{
				System.out.println("Cliente "+scProxy.getInetAddress()+" quiere salirse de la sesion");

				estadoConectado=false;
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
						System.out.println("0. Salir");
						System.out.println("1. Iniciar sesion");
						System.out.println("2. Registrar");
						
						enviar=teclado.nextLine();
						
						
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
									interaccionConElProxy( estadoConectado, scProxy, teclado);//Comienza la interaccion normal con el proxy

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

									interaccionConElProxy(estadoConectado, scProxy, teclado); 
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
			System.out.println("Fallo con la conexion con el manager");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Fallo: "+e);
			e.printStackTrace();
			//conectarConServidor(ipServidor);
		} catch(Exception e)
		{
			
		}
	}
}



