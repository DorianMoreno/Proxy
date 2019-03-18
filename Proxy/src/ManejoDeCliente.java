import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ManejoDeCliente extends Thread{
	private DataInputStream in;
	private DataOutputStream out;
	private Semaphore semaforo;
	private Proxy proxy;
	private final Socket sc;
	private String ipServidor;
	private Integer portServidor;
	public ManejoDeCliente(Socket socketDelCliente, Proxy pro,Semaphore sema, String ipServidor, Integer portServidor)
	{
		super();
		this.semaforo=sema;
		sc=socketDelCliente;
		proxy=pro;
		this.ipServidor = ipServidor;
		this.portServidor = portServidor;
	}
	public void tratoParaLaEntidadGubernamental(Socket scEntidad)
	{
		Socket scServer = null;
		String mensajeDeLaEntidad;
		System.out.println("El cliente "+scEntidad.getInetAddress()+":"+scEntidad.getPort()+" es una entidad gubernamental");
		try
		{
			DataInputStream inEntidad= new DataInputStream(scEntidad.getInputStream());
			DataOutputStream outEntidad= new DataOutputStream(scEntidad.getOutputStream());
			while(true)
			{
				mensajeDeLaEntidad=inEntidad.readUTF();
				if(mensajeDeLaEntidad.trim().equals("SubirConsulta"))
				{
					try {
						scServer = new Socket(ipServidor, portServidor);
						DataInputStream inServer = new DataInputStream(scServer.getInputStream());
						DataOutputStream outServer = new DataOutputStream(scServer.getOutputStream());
						
						outServer.writeUTF("1");
						outServer.writeUTF(inEntidad.readUTF());
						outServer.writeUTF(inEntidad.readUTF());
						outServer.writeUTF(inEntidad.readUTF());
						outEntidad.writeUTF(inServer.readUTF());
						scServer.close();
						
					}catch(Exception e)
					{
						System.out.println("No se pudo establecer conexión con el servidor");
						outEntidad.writeUTF("EXCEPTION-FOUND");
					}
				}
				else if(mensajeDeLaEntidad.trim().contentEquals("VerResultadosConsulta"))
				{
					try {
						scServer = new Socket(ipServidor, portServidor);
						DataInputStream inServer = new DataInputStream(scServer.getInputStream());
						DataOutputStream outServer = new DataOutputStream(scServer.getOutputStream());
						
						outServer.writeUTF("3");
						outServer.writeUTF(inEntidad.readUTF());
						int n = Integer.valueOf(inServer.readUTF());
						outEntidad.writeUTF(String.valueOf(n));
						for(int i=0 ; i<n ; ++i)
						{
							outEntidad.writeUTF(inServer.readUTF());
						}
						scServer.close();
					}catch(Exception e)
					{
						System.out.println("No se pudo establecer conexión con el servidor");
						outEntidad.writeUTF("EXCEPTION-FOUND");
					}
				}
				if(mensajeDeLaEntidad.trim().contentEquals("Salir"))
					break;
			}
			scEntidad.close();
		}
		catch(Exception e)
		{
			System.out.println("Algo paso con la de la entidad gubernamental");
		}
		proxy.reducirUsuarios();
	}

	private void SemaforoWait()
	{
		Boolean b;
		do {
			try {
				semaforo.acquire();
				b = true;
			}catch(Exception e)
			{
				System.out.println(e);
				b=false;
			}
		}while(semaforo.availablePermits() != 0 && b.equals(true));
	}

	public void run()
	{
		Socket scDataBase; 
		String nombreUsuario;
		String hashUsuario;
		String regionUsuario;
		DataInputStream inDB;
		DataOutputStream outDB;

		try {	
			System.out.println("Conexion solicitada de la ip: "+sc.getInetAddress()+" "+sc.getPort());
			//Cliente conectado	
			in= new DataInputStream(sc.getInputStream());
			out= new DataOutputStream(sc.getOutputStream());
			String mensajeDelCliente = " ";
			String hash;
			//out.writeUTF("Mensaje del servidor: conexion exitosa");
			//Inicio de sesion del usuario
			boolean usuarioConectado=true;
			while(usuarioConectado) {
				mensajeDelCliente=in.readUTF();
				if(mensajeDelCliente.equals("Entidad"))
				{
					tratoParaLaEntidadGubernamental(sc);
					return;
				}
				if(mensajeDelCliente.equals("Registro"))//En el inicio de usuario de uncliente
				{
					regionUsuario=in.readUTF();//Recibe registro del usuario
					hash = in.readUTF();
					this.SemaforoWait();//Comienza seccion critica

					scDataBase= new Socket("127.0.0.1",7000);//Conectarse al database
					inDB= new DataInputStream(scDataBase.getInputStream());
					outDB= new DataOutputStream(scDataBase.getOutputStream());
					outDB.writeUTF("Registrar");//Pedirle id al database
					outDB.writeUTF(regionUsuario);// Se envia la region del usuario
					outDB.writeUTF(hash);
					out.writeUTF(inDB.readUTF());	//enviarle el id al usuario


					//

					scDataBase.close();
					semaforo.release();//Termina seccion critica
				}
				if(mensajeDelCliente.equals("1"))
				{
					System.out.println("La conexion de"+sc.getInetAddress()+" "+sc.getPort()+ " pertenece al manager, enviar carga del proxy");
					////
					System.out.println("Cantidad usuarios: "+(proxy.getCantUsuariosConectados()));
					out.writeUTF(String.valueOf(proxy.getCantUsuariosConectados()));
					/////
				}
				if(mensajeDelCliente.trim().equals("Salir"))
				{
					sc.close();
					proxy.reducirUsuarios();
					usuarioConectado=false;
				}
				if(mensajeDelCliente.trim().equals("ConsultarProyectos"))
				{
					String idUsuario=in.readUTF();//Leer id del usuario
					String territorioUsuario=in.readUTF();//Leer territorio del usuario
					
					Socket scServidor = new Socket(ipServidor, portServidor);
					DataOutputStream outServer = new DataOutputStream(scServidor.getOutputStream());
					DataInputStream inServer = new DataInputStream(scServidor.getInputStream());
					outServer.writeUTF("2");
					outServer.writeUTF(idUsuario);
					outServer.writeUTF(territorioUsuario);
					int n = Integer.valueOf(inServer.readUTF());
					out.writeUTF(String.valueOf(n));
					for(int i=0 ; i<n ; i++)
					{
						out.writeUTF(inServer.readUTF());
					}
					if(n != 0)
					{
						outServer.writeUTF(in.readUTF());
						outServer.writeUTF(in.readUTF());
						out.writeUTF(inServer.readUTF());
					}
					scServidor.close();
				}
				if(mensajeDelCliente.trim().equals("IniciarSesion"))
				{
					System.out.println("Cliente "+sc.getInetAddress()+" "+sc.getPort() +" esta intentando iniciar sesion");
					nombreUsuario=in.readUTF();//Recibe el nombre de usuario
					hashUsuario = in.readUTF();//Recibe el hash de la contraseña del usuario
					this.SemaforoWait();//Comienza seccion critica

					scDataBase= new Socket("127.0.0.1",7000);//Conectarse al database
					inDB= new DataInputStream(scDataBase.getInputStream());
					outDB= new DataOutputStream(scDataBase.getOutputStream());
					outDB.writeUTF("BuscarID");//Decirle al database que va a buscar un id ya existente
					outDB.writeUTF(nombreUsuario);//Pasarle el id del usuario al DB
					outDB.writeUTF(hashUsuario);//Pasarle el hash de la contraseña del usuario al DB
					out.writeUTF(inDB.readUTF());//Espera confirmacion de la DB //Informar al usuario si su id existe o no
					out.writeUTF(inDB.readUTF());//Si el usuario existe manda su territorio, sino envía un "false"


					scDataBase.close();


					semaforo.release();//Termina seccion critica

				}
			}
			System.out.println("desconectado el cliente"+sc.getInetAddress()+" "+sc.getPort());
		} catch (Exception e) {

			System.out.println("Se cayo la conexion en: "+sc.getInetAddress()+" "+sc.getPort());
			proxy.reducirUsuarios();
			e.printStackTrace();
		}



	}
}